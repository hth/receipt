package com.receiptofi.service;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.ReceiptEntityOCR;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.ReceiptOfEnum;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.ReceiptOCRManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;
import com.receiptofi.utils.CreateTempFile;
import com.receiptofi.utils.ImageSplit;
import com.receiptofi.utils.Maths;
import com.receiptofi.utils.ReceiptParser;
import com.receiptofi.web.form.UploadReceiptImage;
import com.receiptofi.web.helper.ReceiptForMonth;
import com.receiptofi.web.helper.ReceiptLandingView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 2:04 AM
 */
@Service
public final class LandingService {
    private static final Logger log = LoggerFactory.getLogger(LandingService.class);

    @Autowired private ReceiptManager receiptManager;
    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private FileDBService fileDBService;
    @Autowired private FileUploadDocumentSenderJMS senderJMS;
    @Autowired private ItemManager itemManager;
    @Autowired private ItemService itemService;
    @Autowired private NotificationService notificationService;
    @Autowired private ReceiptService receiptService;

    static Ordering<ReceiptGrouped> descendingOrder = new Ordering<ReceiptGrouped>() {
        public int compare(ReceiptGrouped left, ReceiptGrouped right) {
            return Longs.compare(left.dateInMillisForSorting(), right.dateInMillisForSorting());
        }
    };

    public long pendingReceipt(String profileId) {
        return receiptOCRManager.numberOfPendingReceipts(profileId);
    }

    @SuppressWarnings("unused")
    public List<ReceiptEntity> getAllReceipts(String profileId) {
        return receiptManager.getAllReceipts(profileId);
    }

    public List<ReceiptEntity> getAllReceiptsForThisMonth(String profileId, DateTime monthYear) {
        return receiptManager.getAllReceiptsForThisMonth(profileId, monthYear);
    }

    public Iterator<ReceiptGrouped> getReceiptGroupedByDate(String profileId) {
        return receiptManager.getAllObjectsGroupedByDate(profileId);
    }

    public Map<String, BigDecimal> getAllItemExpense(String profileId) {
        return itemService.getAllItemExpense(profileId);
    }

    public List<ReceiptGrouped> getAllObjectsGroupedByMonth(String userProfileId) {
        Iterator<ReceiptGrouped> groupedIterator = receiptManager.getAllObjectsGroupedByMonth(userProfileId);

        List<ReceiptGrouped> receiptGroupedList = Lists.newArrayList(groupedIterator);
        List<ReceiptGrouped> sortedList = descendingOrder.sortedCopy(receiptGroupedList);
        return sortedList;
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
        if(sortedList.size() < 3) {
            if(sortedList.size() == 1) {
                ReceiptGrouped receiptGrouped = sortedList.get(0);
                DateTime dateTime = receiptGrouped.getDateTime();

                dateTime = dateTime.minusMonths(1);
                ReceiptGrouped r1 = ReceiptGrouped.newInstance(BigDecimal.ZERO, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
                sortedList.add(r1);

                dateTime = dateTime.minusMonths(1);
                ReceiptGrouped r2 = ReceiptGrouped.newInstance(BigDecimal.ZERO, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
                sortedList.add(r2);

            } else if(sortedList.size() == 2) {
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

        for(ReceiptEntity receipt : receipts) {
            BizNameEntity bizNameEntity = receipt.getBizName();
            bizNameEntity = bizNameManager.findOne(bizNameEntity.getId());

            List<ItemEntity> itemEntities = itemManager.getWhereReceipt(receipt);
            if(itemEntities.size() > 0) {
                Map<String, BigDecimal> itemMaps = new HashMap<>();

                for(ItemEntity itemEntity : itemEntities) {
                    BigDecimal sum = BigDecimal.ZERO;
                    sum = itemService.calculateTotalCost(sum, itemEntity);
                    if(itemEntity.getExpenseType() != null) {
                        if(itemMaps.containsKey(itemEntity.getExpenseType().getExpName())) {
                            BigDecimal out = itemMaps.get(itemEntity.getExpenseType().getExpName());
                            itemMaps.put(itemEntity.getExpenseType().getExpName(), Maths.add(out, sum));
                        } else {
                            itemMaps.put(itemEntity.getExpenseType().getExpName(), sum);
                        }
                    } else {
                        if(itemMaps.containsKey("Un-Assigned")) {
                            BigDecimal out = itemMaps.get("Un-Assigned");
                            itemMaps.put("Un-Assigned", Maths.add(out, sum));
                        } else {
                            itemMaps.put("Un-Assigned", sum);
                        }
                    }
                }

                String bizName = StringEscapeUtils.escapeEcmaScript(bizNameEntity.getName());
                if(maps.containsKey(bizName)) {
                    Map<String, BigDecimal> mapData = maps.get(bizName);
                    for(String key : itemMaps.keySet()) {
                        if(mapData.containsKey(key)) {
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
     * @param userProfileId
     * @param modelAndView
     */
    public void computeTotalExpense(String userProfileId, ModelAndView modelAndView) {
        List<ReceiptEntity> receipts = getAllReceipts(userProfileId);
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for(ReceiptEntity receipt : receipts) {
            tax = Maths.add(tax, receipt.getTax());
            total = Maths.add(total, receipt.getTotal());
        }

        modelAndView.addObject("tax", tax);
        modelAndView.addObject("totalWithoutTax", Maths.subtract(total, tax));
        modelAndView.addObject("total", total);
    }

    /**
     * Saves the Receipt Image, Creates ReceiptOCR, ItemOCR and Sends JMS
     *
     * @param userProfileId
     * @param uploadReceiptImage
     * @throws Exception
     */
    public void uploadReceipt(String userProfileId, UploadReceiptImage uploadReceiptImage) throws Exception {
        String receiptBlobId = null;
        String receiptScaledBlobId = null;
        ReceiptEntityOCR receiptOCR = null;
        List<ItemEntityOCR> items;
        try {
            //No more using OCR
            String receiptOCRTranslation = "";
            //String receiptOCRTranslation = ABBYYCloudService.instance().performRecognition(uploadReceiptImage.getFileData().getBytes());
            //TODO remove Temp Code
            //String receiptOCRTranslation = FileUtils.readFileToString(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/Target.txt"));
            log.info("Translation: " + receiptOCRTranslation);

            receiptBlobId = fileDBService.saveFile(uploadReceiptImage);
            log.info("File Id: " + receiptBlobId);

            MultipartFile commonsMultipartFile = uploadReceiptImage.getFileData();
            File original = CreateTempFile.file(new StringBuilder()
                    .append("image_")
                    .append(FilenameUtils.getBaseName(commonsMultipartFile.getOriginalFilename()))
                    .toString(),
                    FilenameUtils.getExtension(commonsMultipartFile.getOriginalFilename()));
            commonsMultipartFile.transferTo(original);
            File scaled = ImageSplit.decreaseResolution(original);
            uploadReceiptImage.setFile(scaled);
            receiptScaledBlobId = fileDBService.saveFile(uploadReceiptImage);

            receiptOCR = ReceiptEntityOCR.newInstance();
            receiptOCR.setDocumentStatus(DocumentStatusEnum.OCR_PROCESSED);
            receiptOCR.setReceiptBlobId(receiptBlobId);
            receiptOCR.setReceiptScaledBlobId(receiptScaledBlobId);
            receiptOCR.setUserProfileId(userProfileId);
            receiptOCR.setReceiptOCRTranslation(receiptOCRTranslation);
            //Cannot pre-select it for now
            //receiptOCR.setReceiptOf(ReceiptOfEnum.EXPENSE);

            setEmptyBiz(receiptOCR);

            items = new LinkedList<>();
            ReceiptParser.read(receiptOCRTranslation, receiptOCR, items);

            //Save Receipt OCR, Items and the Send JMS
            receiptOCRManager.save(receiptOCR);
            itemOCRManager.saveObjects(items);

            log.info("ReceiptEntityOCR @Id after save: " + receiptOCR.getId());
            UserProfileEntity userProfile = userProfileManager.findOne(receiptOCR.getUserProfileId());
            senderJMS.send(receiptOCR, userProfile);
        } catch (Exception exce) {
            log.error("Exception occurred during saving receipt: " + exce.getLocalizedMessage());
            log.warn("Undo all the saves");

            int sizeFSInitial = fileDBService.getFSDBSize();
            if(receiptBlobId != null) {
                fileDBService.deleteHard(receiptBlobId);
            }
            if(receiptScaledBlobId != null) {
                fileDBService.deleteHard(receiptScaledBlobId);
            }
            int sizeFSFinal = fileDBService.getFSDBSize();
            log.info("Storage File: Initial size: " + sizeFSInitial + ", Final size: " + sizeFSFinal);

            long sizeReceiptInitial = receiptOCRManager.collectionSize();
            long sizeItemInitial = itemOCRManager.collectionSize();
            if(receiptOCR != null) {
                itemOCRManager.deleteWhereReceipt(receiptOCR);
                receiptOCRManager.deleteHard(receiptOCR);
            }
            long sizeReceiptFinal = receiptOCRManager.collectionSize();
            long sizeItemFinal = itemOCRManager.collectionSize();

            if(sizeReceiptInitial != sizeReceiptFinal) {
                log.warn("Initial receipt size: " + sizeReceiptInitial + ", Final receipt size: " + sizeReceiptFinal + ". Removed ReceiptOCR: " + receiptOCR.getId());
            } else {
                log.warn("Initial receipt size and Final receipt size are same: '" + sizeReceiptInitial + "' : '" + sizeReceiptFinal + "'");
            }

            if(sizeItemInitial != sizeItemFinal) {
                log.warn("Initial item size: " + sizeItemInitial + ", Final item size: " + sizeItemFinal);
            } else {
                log.warn("Initial item size and Final item size are same: '" + sizeItemInitial + "' : '" + sizeItemFinal + "'");
            }

            log.info("Complete with rollback: throwing exception");
            throw new Exception(exce.getLocalizedMessage());
        }
    }

    /**
     * Can be deleted as DBRef for Biz is not annotated @NotNull. To be considered if DBRef has to be annotated with @NotNull
     *
     * @param receiptEntityOCR
     */
    public void setEmptyBiz(ReceiptEntityOCR receiptEntityOCR) {
        receiptEntityOCR.setBizName(bizNameManager.noName());
        receiptEntityOCR.setBizStore(bizStoreManager.noStore());
    }

    public List<NotificationEntity> notifications(String userProfileId) {
        return notificationService.notifications(userProfileId);
    }

    /**
     *
     * @param allReceiptsForThisMonth
     * @param monthYear
     * @return
     */
    public ReceiptForMonth getReceiptForMonth(List<ReceiptEntity> allReceiptsForThisMonth, DateTime monthYear) {
        String pattern = "MMM, yyyy";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);

        ReceiptForMonth receiptForMonth = ReceiptForMonth.newInstance();
        receiptForMonth.setMonthYear(dtf.print(monthYear));
        for(ReceiptEntity receiptEntity : allReceiptsForThisMonth) {
            receiptForMonth.addReceipt(ReceiptLandingView.newInstance(receiptEntity));
        }
        return receiptForMonth;
    }
}
