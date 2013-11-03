package com.tholix.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.repository.BizNameManager;
import com.tholix.repository.BizStoreManager;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;
import com.tholix.utils.PerformanceProfiling;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 7:57 PM
 */
@Service
public final class FetcherService {
    private static final Logger log = Logger.getLogger(FetcherService.class);

    @Autowired private ItemManager itemManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private ReceiptManager receiptManager;
    @Autowired private ReceiptOCRManager receiptOCRManager;

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizName(String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for Biz Name: " + bizName);
        Set<String> titles = bizNameManager.findAllDistinctBizStr(bizName);
        log.info("found business.. total size " + titles.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return titles;
    }

    /**
     *
     * @param bizAddress
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizAddress(String bizAddress, String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for Biz address: " + bizAddress + ", within Biz Name: " + bizName);
        Set<String> address = new HashSet<>();

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if(bizNameEntity != null) {
            List<BizStoreEntity> list = bizStoreManager.getAllWithJustSpecificField(bizAddress, bizNameEntity, BizStoreManager.ADDRESS);
            for(BizStoreEntity bizStoreEntity : list) {
                address.add(bizStoreEntity.getAddress());
            }

            log.info("found address(es).. total size " + list.size() + ", but unique items size: " + address.size());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return address;
    }

    /**
     *
     * @param bizAddress
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizPhone(String bizPhone, String bizAddress, String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for Biz address: " + bizAddress + ", within Biz Name: " + bizName);
        Set<String> phone = new HashSet<>();

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if(bizNameEntity != null) {
            List<BizStoreEntity> list = bizStoreManager.getAllWithJustSpecificField(bizPhone, bizAddress, bizNameEntity, BizStoreManager.PHONE);

            for(BizStoreEntity bizStoreEntity : list) {
                phone.add(Formatter.phone(bizStoreEntity.getPhone()));
            }

            log.info("found item.. total size " + list.size() + ", but unique items size: " + phone.size());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return phone;
    }

    /**
     * This method is called from AJAX to get the matching list of items in the system.
     * Populates with just the 'name' of the item
     *
     * @param itemName
     * @param bizName
     * @return
     */
    public Set<String> findDistinctItems(String itemName, String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for item name: " + itemName + ", within Biz Name: " + bizName);
        List<ItemEntity> itemList = itemManager.findItems(itemName, bizName);

        Set<String> items = new HashSet<>();
        for(ItemEntity re : itemList) {
            items.add(re.getName());
        }

        log.info("found item.. total size " + itemList.size() + ", but unique items size: " + items.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return items;
    }

    /**
     *
     * @param receiptId
     * @param imageOrientation
     * @param userProfileId
     */
    public void changeImageOrientation(String receiptId, int imageOrientation, String userProfileId) throws Exception {
        DateTime time = DateUtil.now();
        ReceiptEntity receiptEntity = receiptManager.findReceipt(receiptId, userProfileId);
        receiptEntity.setImageOrientation(receiptEntity.getImageOrientation() + imageOrientation);
        receiptManager.save(receiptEntity);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    /**
     *
     * @param receiptOCRId
     * @param imageOrientation
     * @param userProfileId
     */
    public void changeReceiptOCRImageOrientation(String receiptOCRId, int imageOrientation, String userProfileId) throws Exception {
        DateTime time = DateUtil.now();
        ReceiptEntityOCR receiptEntityOCR = receiptOCRManager.findOne(receiptOCRId);
        if(receiptEntityOCR.getUserProfileId().equalsIgnoreCase(userProfileId)) {
            receiptEntityOCR.setImageOrientation(receiptEntityOCR.getImageOrientation() + imageOrientation);
            receiptOCRManager.save(receiptEntityOCR);
        }
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
    }
}
