package com.receiptofi.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;
import com.receiptofi.utils.CreateTempFile;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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
@Service
public final class LandingService {
    private static final Logger LOG = LoggerFactory.getLogger(LandingService.class);

    @Autowired private ReceiptManager receiptManager;
    @Autowired private DocumentManager documentManager;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private FileDBService fileDBService;
    @Autowired private FileUploadDocumentSenderJMS senderJMS;
    @Autowired private ItemService itemService;
    @Autowired private NotificationService notificationService;
    @Autowired private FileSystemService fileSystemService;
    @Autowired private ImageSplitService imageSplitService;
    @Autowired private ReceiptParserService receiptParserService;

    @Value ("${SCALE_UPLOAD_IMAGE_ON:false}")
    private boolean scaledUploadImage;

    static Ordering<ReceiptGrouped> descendingOrder = new Ordering<ReceiptGrouped>() {
        public int compare(ReceiptGrouped left, ReceiptGrouped right) {
            return Longs.compare(left.dateInMillisForSorting(), right.dateInMillisForSorting());
        }
    };

    public long pendingReceipt(String profileId) {
        return documentManager.numberOfPendingReceipts(profileId);
    }

    public long rejectedReceipt(String profileId) {
        return documentManager.numberOfRejectedReceipts(profileId);
    }

    /**
     * Do not use this open end query
     *
     * @param profileId
     * @return
     */
    public List<ReceiptEntity> getAllReceipts(String profileId) {
        return receiptManager.getAllReceipts(profileId);
    }

    public List<ReceiptEntity> getAllReceiptsForTheYear(String profileId, DateTime startOfTheYear) {
        return receiptManager.getAllReceiptsForTheYear(profileId, startOfTheYear);
    }

    public List<ReceiptEntity> getAllReceiptsForThisMonth(String profileId, DateTime monthYear) {
        return receiptManager.getAllReceiptsForThisMonth(profileId, monthYear);
    }

    @Mobile
    @SuppressWarnings ("unused")
    public List<ReceiptEntity> getAllUpdatedReceiptSince(String profileId, Date since) {
        return receiptManager.getAllUpdatedReceiptSince(profileId, since);
    }

    public Iterator<ReceiptGrouped> getReceiptGroupedByDate(String profileId) {
        return receiptManager.getAllObjectsGroupedByDate(profileId);
    }

    public Map<String, BigDecimal> getAllItemExpenseForTheYear(String profileId) {
        return itemService.getAllItemExpenseForTheYear(profileId);
    }

    public List<ReceiptGrouped> getAllObjectsGroupedByMonth(String userProfileId) {
        Iterator<ReceiptGrouped> groupedIterator = receiptManager.getAllObjectsGroupedByMonth(userProfileId);

        List<ReceiptGrouped> receiptGroupedList = Lists.newArrayList(groupedIterator);
        return descendingOrder.sortedCopy(receiptGroupedList);
    }

    /**
     * Add appropriate empty months if month count is less than three
     *
     * @param receiptGroupedList
     * @return
     */
    public List<ReceiptGrouped> addMonthsIfLessThanThree(List<ReceiptGrouped> receiptGroupedList) {
        List<ReceiptGrouped> sortedList = Lists.newArrayList(receiptGroupedList);

        /** In case there is just receipts for one month then add empty data to show the chart pretty for at least two additional months */
        if (sortedList.size() < 3) {
            if (sortedList.size() == 1) {
                ReceiptGrouped receiptGrouped = sortedList.get(0);
                DateTime dateTime = receiptGrouped.getDateTime();

                dateTime = dateTime.minusMonths(1);
                ReceiptGrouped r1 = ReceiptGrouped.newInstance(BigDecimal.ZERO, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
                sortedList.add(r1);

                dateTime = dateTime.minusMonths(1);
                ReceiptGrouped r2 = ReceiptGrouped.newInstance(BigDecimal.ZERO, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
                sortedList.add(r2);

            } else if (sortedList.size() == 2) {
                ReceiptGrouped receiptGrouped = sortedList.get(0);
                DateTime dateTime = receiptGrouped.getDateTime();

                dateTime = dateTime.minusMonths(1);
                ReceiptGrouped r1 = ReceiptGrouped.newInstance(BigDecimal.ZERO, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
                sortedList.add(r1);
            }

            sortedList = descendingOrder.sortedCopy(sortedList);
        }
        return sortedList;
    }

    public List<ReceiptGroupedByBizLocation> getAllObjectsGroupedByBizLocation(String userProfileId) {
        Iterator<ReceiptGroupedByBizLocation> grpIterator = receiptManager.getAllReceiptGroupedByBizLocation(userProfileId);
        return Lists.newArrayList(grpIterator);
    }

    /**
     * For donut pie chart
     *
     * @param receipts
     * @return
     */
    public Map<String, Map<String, BigDecimal>> allBusinessByExpenseType(List<ReceiptEntity> receipts) {
        Map<String, Map<String, BigDecimal>> maps = new HashMap<>();

        for (ReceiptEntity receipt : receipts) {
            BizNameEntity bizNameEntity = receipt.getBizName();
            bizNameEntity = bizNameManager.findOne(bizNameEntity.getId());

            List<ItemEntity> itemEntities = itemService.getAllItemsOfReceipt(receipt.getId());
            if (itemEntities.size() > 0) {
                Map<String, BigDecimal> itemMaps = new HashMap<>();

                for (ItemEntity itemEntity : itemEntities) {
                    BigDecimal sum = BigDecimal.ZERO;
                    sum = itemService.calculateTotalCost(sum, itemEntity);
                    if (itemEntity.getExpenseTag() == null) {
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

                String bizName = StringEscapeUtils.escapeEcmaScript(bizNameEntity.getBusinessName());
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
     * Computes all the expenses user has
     *
     * @param userProfileId
     */
    public Map<String, BigDecimal> computeTotalExpense(String userProfileId) {
        return computeToDateExpense(getAllReceipts(userProfileId));
    }

    /**
     * Computes YTD expenses
     *
     * @param userProfileId
     */
    public Map<String, BigDecimal> computeYearToDateExpense(String userProfileId) {
        return computeToDateExpense(getAllReceiptsForTheYear(userProfileId, DateUtil.startOfYear()));
    }

    private Map<String, BigDecimal> computeToDateExpense(List<ReceiptEntity> receipts) {
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (ReceiptEntity receipt : receipts) {
            tax = Maths.add(tax, receipt.getTax());
            total = Maths.add(total, receipt.getTotal());
        }

        Map<String, BigDecimal> map = new HashMap<>();
        map.put("tax", tax);
        map.put("totalWithoutTax", Maths.subtract(total, tax));
        map.put("total", total);

        return map;
    }

    /**
     * Saves the Receipt Image, Creates ReceiptOCR, ItemOCR and Sends JMS
     *
     * @param uploadReceiptImage
     * @throws Exception
     */
    public void uploadDocument(UploadDocumentImage uploadReceiptImage) {
        String documentBlobId = null;
        DocumentEntity documentEntity = null;
        FileSystemEntity fileSystem = null;
        List<ItemEntityOCR> items;
        try {
            //No more using OCR
            String receiptOCRTranslation = StringUtils.EMPTY;
            //String receiptOCRTranslation = ABBYYCloudService.instance().performRecognition(uploadReceiptImage.getFileData().getBytes());
            //TODO remove Temp Code
            //String receiptOCRTranslation = FileUtils.readFileToString(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/Target.txt"));
            LOG.info("Upload document rid={} fileType={}", uploadReceiptImage.getRid(), uploadReceiptImage.getFileType());

            BufferedImage bufferedImage;
            if (scaledUploadImage) {
                File scaled = scaleImage(uploadReceiptImage);
                uploadReceiptImage.setFile(scaled);
                bufferedImage = imageSplitService.bufferedImage(scaled);
            } else {
                bufferedImage = imageSplitService.bufferedImage(uploadReceiptImage.getFileData().getInputStream());
            }
            documentBlobId = fileDBService.saveFile(uploadReceiptImage);
            uploadReceiptImage.setBlobId(documentBlobId);

            documentEntity = DocumentEntity.newInstance();
            documentEntity.setDocumentStatus(DocumentStatusEnum.OCR_PROCESSED);

            fileSystem = new FileSystemEntity(documentBlobId, bufferedImage, 0, 0);
            fileSystemService.save(fileSystem);
            documentEntity.addReceiptBlobId(fileSystem);

            documentEntity.setUserProfileId(uploadReceiptImage.getRid());
            //Cannot pre-select it for now
            //receiptOCR.setReceiptOf(ReceiptOfEnum.EXPENSE);

            setEmptyBiz(documentEntity);

            items = new LinkedList<>();
            receiptParserService.read(receiptOCRTranslation, documentEntity, items);

            //Save Document, Items and the Send JMS
            documentManager.save(documentEntity);
            itemOCRManager.saveObjects(items);

            LOG.info("Upload complete document={} rid={}", documentEntity.getId(), documentEntity.getUserProfileId());
            UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(documentEntity.getUserProfileId());
            senderJMS.send(documentEntity, userProfile);
        } catch (Exception exce) {
            LOG.error("Exception occurred during saving receipt={}", exce.getLocalizedMessage(), exce);
            LOG.warn("Undo all the saves");

            int sizeFSInitial = fileDBService.getFSDBSize();
            if (documentBlobId != null) {
                fileDBService.deleteHard(documentBlobId);
            }
            int sizeFSFinal = fileDBService.getFSDBSize();
            LOG.info("Storage File: Initial size: " + sizeFSInitial + ", Final size: " + sizeFSFinal);

            if (fileSystem != null) {
                fileSystemService.deleteHard(fileSystem);
            }

            long sizeReceiptInitial = documentManager.collectionSize();
            long sizeItemInitial = itemOCRManager.collectionSize();
            if (documentEntity != null) {
                itemOCRManager.deleteWhereReceipt(documentEntity);
                documentManager.deleteHard(documentEntity);
            }
            long sizeReceiptFinal = documentManager.collectionSize();
            long sizeItemFinal = itemOCRManager.collectionSize();

            if (sizeReceiptInitial == sizeReceiptFinal) {
                LOG.warn("Initial receipt size and Final receipt size are same: '" + sizeReceiptInitial + "' : '" + sizeReceiptFinal + "'");
            } else {
                LOG.warn("Initial receipt size: " + sizeReceiptInitial + ", Final receipt size: " + sizeReceiptFinal + ". Removed Document: " + documentEntity.getId());
            }

            if (sizeItemInitial == sizeItemFinal) {
                LOG.warn("Initial item size and Final item size are same: '" + sizeItemInitial + "' : '" + sizeItemFinal + "'");
            } else {
                LOG.warn("Initial item size: " + sizeItemInitial + ", Final item size: " + sizeItemFinal);
            }

            LOG.warn("Complete with rollback: throwing exception");
            throw new RuntimeException(exce);
        }
    }

    /**
     * Scales uploaded document image
     *
     * @param uploadReceiptImage
     * @return
     * @throws IOException
     */
    private File scaleImage(UploadDocumentImage uploadReceiptImage) throws IOException {
        MultipartFile commonsMultipartFile = uploadReceiptImage.getFileData();
        File original = CreateTempFile.file(
                "image_" +
                        FilenameUtils.getBaseName(commonsMultipartFile.getOriginalFilename()),
                FilenameUtils.getExtension(commonsMultipartFile.getOriginalFilename())
        );

        commonsMultipartFile.transferTo(original);
        return imageSplitService.decreaseResolution(original);
    }

    /**
     * Saves the Receipt Image, Creates ReceiptOCR, ItemOCR and Sends JMS
     *
     * @param userProfileId
     * @param uploadReceiptImage
     * @throws Exception
     */
    public void appendMileage(String documentId, String userProfileId, UploadDocumentImage uploadReceiptImage) {
        throw new UnsupportedOperationException("");
    }

    /**
     * Can be deleted as DBRef for Biz is not annotated @NotNull. To be considered if DBRef has to be annotated with @NotNull
     *
     * @param documentEntity
     */
    public void setEmptyBiz(DocumentEntity documentEntity) {
        documentEntity.setBizName(bizNameManager.noName());
        documentEntity.setBizStore(bizStoreManager.noStore());
    }

    public List<NotificationEntity> notifications(String userProfileId) {
        return notificationService.notifications(userProfileId);
    }

    public long notificationCount(String userProfileId) {
        return notificationService.notificationCount(userProfileId);
    }
}
