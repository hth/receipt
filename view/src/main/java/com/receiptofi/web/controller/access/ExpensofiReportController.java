package com.receiptofi.web.controller.access;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.service.ItemAnalyticService;
import com.receiptofi.service.NotificationService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.service.ftp.FtpService;
import com.receiptofi.utils.FileUtil;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.helper.AnchorFileInExcel;
import com.receiptofi.web.helper.json.ExcelFileName;
import com.receiptofi.web.view.ExpensofiExcelView;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 11/30/13 2:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/access/expensofi")
public class ExpensofiReportController {
    private static final Logger LOG = LoggerFactory.getLogger(ExpensofiReportController.class);

    @Value ("${expensofiReportLocation}")
    private String expensofiReportLocation;

    private String bucketName;
    private String awsS3Endpoint;

    private final ReceiptService receiptService;
    private final NotificationService notificationService;
    private final ItemAnalyticService itemAnalyticService;
    private final ExpensofiExcelView expensofiExcelView;
    private final FtpService ftpService;

    @Autowired
    public ExpensofiReportController(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            @Value ("${aws.s3.endpoint}")
            String awsS3Endpoint,

            ReceiptService receiptService,
            NotificationService notificationService,
            ItemAnalyticService itemAnalyticService,
            ExpensofiExcelView expensofiExcelView,
            FtpService ftpService
    ) {
        this.bucketName = bucketName;
        this.awsS3Endpoint = awsS3Endpoint;

        this.receiptService = receiptService;
        this.notificationService = notificationService;
        this.itemAnalyticService = itemAnalyticService;
        this.expensofiExcelView = expensofiExcelView;
        this.ftpService = ftpService;
    }

    @RequestMapping (
            value = "/items",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json")
    public String updateExpenseTagOfItems(
            @RequestBody
            String itemIds,

            Model model
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        JsonArray jsonItems = getJsonElements(itemIds);
        List<ItemEntity> items = getItemEntities(receiptUser.getRid(), jsonItems);

        if (!items.isEmpty()) {
            model.addAttribute("items", items);
            Assert.notNull(model.asMap().get("items"));

            ReceiptEntity receiptEntity = items.get(0).getReceipt();
            Collection<AnchorFileInExcel> anchorFileInExcels = new LinkedList<>();
            for (FileSystemEntity fileSystem : receiptEntity.getFileSystemEntities()) {
                String uri = awsS3Endpoint +
                        bucketName +
                        "/" +
                        bucketName +
                        "/" +
                        fileSystem.getKey();
                InputStream is = null;
                try {
                    is = new URL(uri).openStream();
                    AnchorFileInExcel anchorFileInExcel = new AnchorFileInExcel(
                            IOUtils.toByteArray(is),
                            fileSystem.getContentType());
                    anchorFileInExcels.add(anchorFileInExcel);
                } catch (FileNotFoundException e) {
                    LOG.error("File not found at URL={}", uri, e);
                } catch (IOException e) {
                    LOG.error("Failed to load receipt image reason={}", e.getLocalizedMessage(), e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
            model.addAttribute("to_be_anchored_files", anchorFileInExcels);

            try {
                String filename = FileUtil.createRandomFilename();
                model.addAttribute("file-name", filename);
                expensofiExcelView.generateExcel(model.asMap(), new HSSFWorkbook());
                updateReceiptWithExcelFilename(receiptEntity, filename);
                notificationService.addNotification(
                        receiptEntity.getBizName().getBusinessName() +
                                " expense report created",
                        NotificationTypeEnum.EXPENSE_REPORT,
                        NotificationGroupEnum.F,
                        receiptEntity);
                return new ExcelFileName(filename).asJson();
            } catch (IOException e) {
                LOG.error("Failure in creating and saving excel report to file system: " + e.getLocalizedMessage(), e);
            }
        }
        return new ExcelFileName("").asJson();
    }

    private void updateReceiptWithExcelFilename(ReceiptEntity receipt, String filename) {
        if (StringUtils.isNotEmpty(receipt.getExpenseReportInFS())) {
            /* Delete existing file on server before updating. */
            ftpService.delete(receipt.getExpenseReportInFS());
        }
        receiptService.updateReceiptWithExpReportFilename(receipt.getId(), filename);
    }

    private List<ItemEntity> getItemEntities(String receiptUserId, JsonArray jsonItems) {
        List<ItemEntity> items = new ArrayList<>();
        for (Object jsonItem : jsonItems) {
            ItemEntity ie = itemAnalyticService.findItemById(
                    jsonItem.toString().substring(1, jsonItem.toString().length() - 1),
                    receiptUserId);
            items.add(ie);
        }
        return items;
    }

    private JsonArray getJsonElements(String itemIds) throws UnsupportedEncodingException {
        String result = URLDecoder.decode(itemIds, ScrubbedInput.UTF_8);
        result = result.substring(0, result.length() - 1);

        JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
        return (JsonArray) jsonObject.get("items");
    }
}
