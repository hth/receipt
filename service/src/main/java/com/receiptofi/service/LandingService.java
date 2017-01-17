package com.receiptofi.service;

import com.google.common.collect.Lists;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.domain.value.ReceiptListView;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.MessageDocumentManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.wrapper.ThisYearExpenseByTag;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.FileUtil;
import com.receiptofi.utils.Maths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 2:04 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class LandingService {
    private static final Logger LOG = LoggerFactory.getLogger(LandingService.class);

    private ReceiptManager receiptManager;
    private DocumentManager documentManager;
    private ItemOCRManager itemOCRManager;
    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private UserProfileManager userProfileManager;
    private FileDBService fileDBService;
    private ItemService itemService;
    private NotificationService notificationService;
    private FileSystemService fileSystemService;
    private ImageSplitService imageSplitService;
    private ReceiptParserService receiptParserService;
    private MessageDocumentManager messageDocumentManager;

    @Autowired
    public LandingService(
            ReceiptManager receiptManager,
            DocumentManager documentManager,
            ItemOCRManager itemOCRManager,
            BizNameManager bizNameManager,
            BizStoreManager bizStoreManager,
            UserProfileManager userProfileManager,
            FileDBService fileDBService,
            ItemService itemService,
            NotificationService notificationService,
            FileSystemService fileSystemService,
            ImageSplitService imageSplitService,
            ReceiptParserService receiptParserService,
            MessageDocumentManager messageDocumentManager) {
        this.receiptManager = receiptManager;
        this.documentManager = documentManager;
        this.itemOCRManager = itemOCRManager;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.userProfileManager = userProfileManager;
        this.fileDBService = fileDBService;
        this.itemService = itemService;
        this.notificationService = notificationService;
        this.fileSystemService = fileSystemService;
        this.imageSplitService = imageSplitService;
        this.receiptParserService = receiptParserService;
        this.messageDocumentManager = messageDocumentManager;
    }

    public long pendingReceipt(String rid) {
        return documentManager.numberOfPendingReceipts(rid);
    }

    public long rejectedReceipt(String rid) {
        return documentManager.numberOfRejectedReceipts(rid);
    }

    /**
     * Do not use this open end query.
     *
     * @param rid
     * @return
     */
    public List<ReceiptEntity> getAllReceipts(String rid) {
        return receiptManager.getAllReceipts(rid);
    }

    public List<ReceiptEntity> getAllReceiptsForTheYear(String rid, DateTime startOfTheYear) {
        return receiptManager.getAllReceiptsForTheYear(rid, startOfTheYear);
    }

    public List<ReceiptEntity> getAllReceiptsForThisMonth(String rid, DateTime monthYear) {
        return receiptManager.getAllReceiptsForThisMonth(rid, monthYear);
    }

    public Iterator<ReceiptGrouped> getReceiptGroupedByDate(String rid) {
        return receiptManager.getAllObjectsGroupedByDate(rid);
    }

    public List<ThisYearExpenseByTag> getAllItemExpenseForTheYear(String rid) {
        return itemService.getAllItemExpenseForTheYear(rid);
    }

    public List<ReceiptGrouped> getReceiptGroupedByMonth(String rid) {
        return receiptManager.getReceiptGroupedByMonth(rid);
    }

    public List<ReceiptListView> getReceiptsForMonths(String rid, List<ReceiptGrouped> groupedByMonth) {
        List<ReceiptListView> receiptListViews = new LinkedList<>();
        for (ReceiptGrouped receiptGrouped : groupedByMonth) {

            ReceiptListView receiptListView = new ReceiptListView()
                    .setMonth(receiptGrouped.getMonth())
                    .setYear(receiptGrouped.getYear())
                    .setDate(receiptGrouped.getDateTime().toDate())
                    .setSplitTotal(receiptGrouped.getSplitTotal())
                    .setCountryShortName(receiptGrouped.getCountryShortName());

            receiptListView.setReceiptListViewGroupedList(
                    receiptManager.getReceiptForGroupedByMonth(
                            rid,
                            receiptGrouped.getMonth(),
                            receiptGrouped.getYear()
                    )
            );

            receiptListViews.add(receiptListView);
        }
        return receiptListViews;
    }

    /**
     * Add appropriate empty months if month count is less than three.
     *
     * @param receiptGroupedList
     * @return
     */
    public List<ReceiptGrouped> addMonthsIfLessThanThree(List<ReceiptGrouped> receiptGroupedList, int size) {
        LinkedList<ReceiptGrouped> copiedList = Lists.newLinkedList(receiptGroupedList);

        /**
         * In case there is just receipts for one month then add empty data to show the chart pretty for at least two
         * additional months. Empty data appends to last month in the DESC sorted list.
         */
        if (size == 1) {
            ReceiptGrouped receiptGrouped = copiedList.get(0);
            DateTime dateTime = receiptGrouped.getDateTime();

            dateTime = dateTime.minusMonths(1);
            ReceiptGrouped r1 = ReceiptGrouped.newInstance(
                    BigDecimal.ZERO,
                    dateTime.getYear(),
                    dateTime.getMonthOfYear(),
                    dateTime.getDayOfMonth(),
                    receiptGrouped.getCountryShortName());
            copiedList.add(r1);

            dateTime = dateTime.minusMonths(1);
            ReceiptGrouped r2 = ReceiptGrouped.newInstance(
                    BigDecimal.ZERO,
                    dateTime.getYear(),
                    dateTime.getMonthOfYear(),
                    dateTime.getDayOfMonth(),
                    receiptGrouped.getCountryShortName());
            copiedList.add(r2);
        } else if (size == 2) {
            ReceiptGrouped receiptGrouped = copiedList.get(1);
            DateTime dateTime = receiptGrouped.getDateTime();

            dateTime = dateTime.minusMonths(1);
            ReceiptGrouped r1 = ReceiptGrouped.newInstance(
                    BigDecimal.ZERO,
                    dateTime.getYear(),
                    dateTime.getMonthOfYear(),
                    dateTime.getDayOfMonth(),
                    receiptGrouped.getCountryShortName());
            copiedList.add(r1);
        }

        return copiedList;
    }

    public List<ReceiptGroupedByBizLocation> getAllObjectsGroupedByBizLocation(String userProfileId) {
        return Lists.newArrayList(receiptManager.getAllReceiptGroupedByBizLocation(userProfileId));
    }

    /**
     * For donut pie chart.
     *
     * @param receipts
     * @return
     */
    public Map<String, Map<String, BigDecimal>> allBusinessByExpenseType(List<ReceiptEntity> receipts) {
        Map<String, Map<String, BigDecimal>> maps = new HashMap<>();

        for (ReceiptEntity receipt : receipts) {
            List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());
            if (!items.isEmpty()) {
                Map<String, BigDecimal> itemMaps = new HashMap<>();

                for (ItemEntity itemEntity : items) {
                    BigDecimal sum = BigDecimal.ZERO;
                    sum = itemService.calculateTotalCost(sum, itemEntity);
                    if (null == itemEntity.getExpenseTag()) {
                        if (itemMaps.containsKey("Un-Assigned")) {
                            BigDecimal out = itemMaps.get("Un-Assigned");
                            itemMaps.put("Un-Assigned", Maths.add(out, sum));
                        } else {
                            itemMaps.put("Un-Assigned", sum);
                        }
                    } else {
                        if (itemMaps.containsKey(itemEntity.getExpenseTag().getTagName())) {
                            BigDecimal out = itemMaps.get(itemEntity.getExpenseTag().getTagName());
                            itemMaps.put(itemEntity.getExpenseTag().getTagName(), Maths.add(out, sum));
                        } else {
                            itemMaps.put(itemEntity.getExpenseTag().getTagName(), sum);
                        }
                    }
                }

                Assert.hasText(receipt.getBizName().getBusinessName(), "Business name is empty.");
                String bizName = StringEscapeUtils.escapeEcmaScript(receipt.getBizName().getBusinessName());
                if (maps.containsKey(bizName)) {
                    Map<String, BigDecimal> mapData = maps.get(bizName);
                    for (String key : itemMaps.keySet()) {
                        if (mapData.containsKey(key)) {
                            BigDecimal value = mapData.get(key);
                            BigDecimal existingSum = itemMaps.get(key);
                            mapData.put(key, Maths.add(existingSum, value));
                        } else {
                            mapData.put(key, itemMaps.get(key));
                        }
                    }
                    maps.put(bizName, mapData);
                } else {
                    maps.put(bizName, itemMaps);
                }
            }
        }

        return maps;
    }

    /**
     * Computes all the expenses user has.
     *
     * Not being used.
     * @param rid
     */
    @Deprecated
    public Map<String, BigDecimal> computeTotalExpense(String rid) {
        return computeToDateExpense(getAllReceipts(rid));
    }

    /**
     * Computes YTD expenses.
     *
     * Not being used.
     * @param rid
     */
    @Deprecated
    public Map<String, BigDecimal> computeYearToDateExpense(String rid) {
        return computeToDateExpense(getAllReceiptsForTheYear(rid, DateUtil.startOfYear()));
    }

    @Deprecated
    private Map<String, BigDecimal> computeToDateExpense(List<ReceiptEntity> receipts) {
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (ReceiptEntity receipt : receipts) {
            tax = Maths.add(tax, receipt.getSplitTax());
            total = Maths.add(total, receipt.getSplitTotal());
        }

        Map<String, BigDecimal> map = new HashMap<>();
        map.put("tax", Maths.adjustScale(tax));
        map.put("totalWithoutTax", Maths.adjustScale(Maths.subtract(total, tax)));
        map.put("total", Maths.adjustScale(total));

        return map;
    }

    /**
     * Saves the Receipt Image, Creates ReceiptOCR, ItemOCR and Sends JMS.
     *
     * @param documentImage
     * @throws Exception
     */
    public DocumentEntity uploadDocument(UploadDocumentImage documentImage) {
        String blobId = null;
        DocumentEntity document = null;
        FileSystemEntity fileSystem = null;
        List<ItemEntityOCR> items;
        try {
            //No more using OCR
            String receiptOCRTranslation = "";
            //String receiptOCRTranslation = ABBYYCloudService.instance().performRecognition(uploadReceiptImage.getFileData().getBytes());
            //TODO remove Temp Code
            //String receiptOCRTranslation = FileUtils.readFileToString(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/Target.txt"));
            LOG.info("Upload document rid={} fileType={}", documentImage.getRid(), documentImage.getFileType());

            BufferedImage bufferedImage = imageSplitService.bufferedImage(documentImage.getFileData().getInputStream());
            blobId = fileDBService.saveFile(documentImage);
            documentImage.setBlobId(blobId);

            document = DocumentEntity.newInstance();
            document.setDocumentStatus(DocumentStatusEnum.PENDING);

            fileSystem = new FileSystemEntity(
                    blobId,
                    documentImage.getRid(),
                    bufferedImage,
                    0,
                    0,
                    documentImage.getFileData(),
                    FileTypeEnum.R);
            fileSystemService.save(fileSystem);

            document.addReceiptBlobId(fileSystem);
            document.setReceiptUserId(documentImage.getRid());
            //Cannot pre-select it for now
            //receiptOCR.setReceiptOf(ReceiptOfEnum.EXPENSE);

            setEmptyBiz(document);

            items = new LinkedList<>();
            receiptParserService.read(receiptOCRTranslation, document, items);

            /** Save Document, Items and the Send JMS. */
            documentManager.save(document);
            itemOCRManager.saveObjects(items);

            /** Added document uploaded successfully. */
            notificationService.addNotification(
                    fileSystem.getOriginalFilename() + " upload successful. Having technical issues. Receipt will be processed within 24hrs.",
                    NotificationTypeEnum.DOCUMENT_UPLOADED,
                    NotificationGroupEnum.F,
                    document);

            LOG.info("Upload complete document={} rid={}", document.getId(), document.getReceiptUserId());
            UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(document.getReceiptUserId());

            /* Add to Message document. */
            MessageDocumentEntity messageDocument = MessageDocumentEntity.newInstance(document.getId(), userProfile.getLevel(), document.getDocumentStatus());
            messageDocumentManager.save(messageDocument);

            return document;
        } catch (Exception exce) {
            LOG.error("Exception occurred during saving receipt={}", exce.getLocalizedMessage(), exce);
            LOG.warn("Undo all the saves");

            int sizeFSInitial = fileDBService.getFSDBSize();
            if (null != blobId) {
                fileDBService.deleteHard(blobId);
            }
            int sizeFSFinal = fileDBService.getFSDBSize();
            LOG.info("Storage File: Initial size: " + sizeFSInitial + ", Final size: " + sizeFSFinal);

            if (null != fileSystem) {
                fileSystemService.deleteHard(fileSystem);
            }

            long sizeReceiptInitial = documentManager.collectionSize();
            long sizeItemInitial = itemOCRManager.collectionSize();
            if (null != document) {
                itemOCRManager.deleteWhere(document.getId());
                documentManager.deleteHard(document);
            }
            long sizeReceiptFinal = documentManager.collectionSize();
            long sizeItemFinal = itemOCRManager.collectionSize();

            if (sizeReceiptInitial == sizeReceiptFinal) {
                LOG.warn("Initial receipt size and Final receipt size are same: '{}' : '{}'", sizeReceiptInitial, sizeReceiptFinal);
            } else {
                LOG.warn("Initial receipt size: {}, Final receipt size: {}. Removed Document: {}", sizeReceiptInitial, sizeReceiptFinal, document.getId());
            }

            if (sizeItemInitial == sizeItemFinal) {
                LOG.warn("Initial item size and Final item size are same: '{}' : '{}'", sizeItemInitial, sizeItemFinal);
            } else {
                LOG.warn("Initial item size: {}, Final item size: {}", sizeItemInitial, sizeItemFinal);
            }

            LOG.warn("Complete with rollback: throwing exception");
            throw new RuntimeException(exce);
        }
    }

    /**
     * Scales uploaded document image.
     *
     * @param uploadReceiptImage
     * @return
     * @throws IOException
     */
    private File scaleImage(UploadDocumentImage uploadReceiptImage) throws IOException {
        MultipartFile commonsMultipartFile = uploadReceiptImage.getFileData();
        File original = FileUtil.createTempFile(
                "image_" +
                        FilenameUtils.getBaseName(commonsMultipartFile.getOriginalFilename()),
                FilenameUtils.getExtension(commonsMultipartFile.getOriginalFilename())
        );

        commonsMultipartFile.transferTo(original);
        return imageSplitService.decreaseResolution(original);
    }

    /**
     * Saves the Receipt Image, Creates ReceiptOCR, ItemOCR and Sends JMS.
     *
     * @param documentId
     * @param userProfileId
     * @param uploadReceiptImage
     */
    public void appendMileage(String documentId, String userProfileId, UploadDocumentImage uploadReceiptImage) {
        throw new UnsupportedOperationException("");
    }

    /**
     * Can be deleted as DBRef for Biz is not annotated @NotNull.
     * To be considered if DBRef has to be annotated with @NotNull.
     *
     * @param documentEntity
     */
    public void setEmptyBiz(DocumentEntity documentEntity) {
        documentEntity.setBizName(bizNameManager.noName());
        documentEntity.setBizStore(bizStoreManager.noStore());
    }
}
