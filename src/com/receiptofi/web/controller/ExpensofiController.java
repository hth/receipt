package com.receiptofi.web.controller;

import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.joda.time.DateTime;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * User: hitender
 * Date: 11/30/13 2:45 AM
 */
@Controller
@SessionAttributes({"userSession"})
public class ExpensofiController {
    private static final Logger log = LoggerFactory.getLogger(ExpensofiController.class);

    @Autowired private ReceiptService receiptService;
    @Autowired private FileDBService fileDBService;

    /**
     * Handles requests to list all accounts for currently logged in user.
     */
    //http://localhost:8080/receipt/expensofi/52992ea430042887af1d0d3f.htm?output=excel to get excel output
    @RequestMapping(value = "/expensofi/{receiptId}", method = RequestMethod.GET)
    public String showExpenseExcel(@PathVariable String receiptId, @ModelAttribute("userSession") UserSession userSession, Model model) {
        DateTime time = DateUtil.now();

        ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId, userSession.getUserProfileId());
        if(receiptEntity != null) {
            List<ItemEntity> items = receiptService.findItems(receiptEntity);
            model.addAttribute("items", items);
            assert(model.asMap().get("items") != null);

            GridFSDBFile gridFSDBFile = fileDBService.getFile(receiptEntity.getReceiptBlobId());
            InputStream is = null;
            try {
                is = gridFSDBFile.getInputStream();
                byte[] bytes = IOUtils.toByteArray(is);
                model.addAttribute("image", bytes);
            } catch (IOException exce) {
                log.error("Failed to load receipt image: " + exce.getLocalizedMessage());
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return "receipt/expensofi";
    }
}
