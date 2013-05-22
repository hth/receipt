package com.tholix.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.repository.BizNameManager;
import com.tholix.repository.BizStoreManager;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ItemOCRManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.service.routes.ReceiptSenderJMS;
import com.tholix.utils.ABBYYCloudService;
import com.tholix.utils.Formatter;
import com.tholix.utils.ReceiptParser;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 2:04 AM
 */
@Service
public class LandingService {
    private static final Logger log = Logger.getLogger(LandingService.class);

    @Autowired private ReceiptManager receiptManager;
    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private FileDBService fileDBService;
    @Autowired private ReceiptSenderJMS senderJMS;
    @Autowired private ItemManager itemManager;

    public long pendingReceipt(String profileId) {
        return receiptOCRManager.numberOfPendingReceipts(profileId);
    }

    public List<ReceiptEntity> allReceipts(String profileId) {
        return receiptManager.getAllObjectsForUser(profileId);
    }

    public Map<Date, BigDecimal> getReceiptGroupedByDate(String profileId) {
        return receiptManager.getAllObjectsGroupedByDate(profileId);
    }

    public Map<String, BigDecimal> getAllItemExpense(String profileId) {
        return itemManager.getAllItemExpense(profileId);
    }

    /**
     * @param receipts
     * @param modelAndView
     */
    public void computeTotalExpense(List<ReceiptEntity> receipts, ModelAndView modelAndView) {
        double tax = 0.00;
        double total = 0.00;
        for(ReceiptEntity receipt : receipts) {
            tax += receipt.getTax();
            total += receipt.getTotal();
        }

        modelAndView.addObject("tax", Formatter.df.format(tax));
        modelAndView.addObject("totalWithoutTax", Formatter.df.format(total - tax));
        modelAndView.addObject("total", Formatter.df.format(total));
    }

    /**
     * Saves the Receipt Image, Creates ReceiptOCR, ItemOCR and Sends JMS
     *
     * @param profileId
     * @param uploadReceiptImage
     * @throws Exception
     */
    public void uploadReceipt(String profileId, UploadReceiptImage uploadReceiptImage) throws Exception {
        String receiptBlobId = null;
        ReceiptEntityOCR receiptOCR = null;
        List<ItemEntityOCR> items;
        try {
            String receiptOCRTranslation = ABBYYCloudService.instance().performRecognition(uploadReceiptImage.getFileData().getBytes());
            //TODO remove Temp Code
            //String receiptOCRTranslation = FileUtils.readFileToString(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/Target.txt"));
            log.info("Translation: " + receiptOCRTranslation);

            receiptBlobId = fileDBService.saveFile(uploadReceiptImage);
            log.info("File Id: " + receiptBlobId);

            receiptOCR = ReceiptEntityOCR.newInstance(uploadReceiptImage.getDescription(), ReceiptStatusEnum.OCR_PROCESSED, receiptBlobId, profileId, receiptOCRTranslation);
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
                fileDBService.deleteFile(receiptBlobId);
            }
            int sizeFSFinal = fileDBService.getFSDBSize();
            log.info("Storage File: Initial size: " + sizeFSInitial + ", Final size: " + sizeFSFinal);

            long sizeReceiptInitial = receiptOCRManager.collectionSize();
            long sizeItemInitial = itemOCRManager.collectionSize();
            if(receiptOCR != null) {
                itemOCRManager.deleteWhereReceipt(receiptOCR);
                receiptOCRManager.delete(receiptOCR);
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
}
