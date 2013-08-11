package com.tholix.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.repository.BizNameManager;
import com.tholix.repository.BizStoreManager;
import com.tholix.web.form.BizForm;

/**
 * User: hitender
 * Date: 8/8/13
 * Time: 8:48 AM
 */
@Service
public final class BizService {
    private static final Logger log = Logger.getLogger(BizService.class);

    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private ExternalService externalService;
    @Autowired private ReceiptService receiptService;

    public BizNameEntity findName(String bizId) {
        return bizNameManager.findOne(bizId);
    }

    public void saveName(BizNameEntity bizNameEntity) throws Exception {
        bizNameManager.save(bizNameEntity);
    }

    public BizStoreEntity findStore(String storeId) {
        return bizStoreManager.findOne(storeId);
    }

    public void saveStore(BizStoreEntity bizStoreEntity) throws Exception {
        bizStoreManager.save(bizStoreEntity);
    }

    public Set<BizStoreEntity> bizSearch(String bizName, String bizAddress, String bizPhone) {
        Set<BizStoreEntity> bizStoreEntities = new HashSet<>();

        if(StringUtils.isNotEmpty(bizName)) {
            List<BizNameEntity> bizNameEntities = bizNameManager.findAllBiz(bizName);
            for(BizNameEntity bizNameEntity : bizNameEntities) {
                List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(bizAddress, bizPhone, bizNameEntity);
                bizStoreEntities.addAll(bizStores);
            }
        } else {
            List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(bizAddress, bizPhone, null);
            bizStoreEntities.addAll(bizStores);
        }
        return bizStoreEntities;
    }

    public void countReceiptForBizStore(Set<BizStoreEntity> bizStoreEntities, BizForm bizForm) {
        for(BizStoreEntity bizStoreEntity : bizStoreEntities) {
            long count = receiptService.countAllReceipt(bizStoreEntity);
            bizForm.addReceiptCount(bizStoreEntity.getId(), count);
        }
    }

    /**
     * This method is being used by Admin to create new Business and Stores. Also this method is being used by receipt update to do the same.
     *
     * @param receiptEntity
     */
    public void saveNewBusinessAndOrStore(ReceiptEntity receiptEntity) throws Exception {
        BizNameEntity bizNameEntity = receiptEntity.getBizName();
        BizStoreEntity bizStoreEntity = receiptEntity.getBizStore();

        BizNameEntity bizName = bizNameManager.findOneByName(bizNameEntity.getName());
        if(bizName == null) {
            try {
                bizNameManager.save(bizNameEntity);

                bizStoreEntity.setBizName(bizNameEntity);
                externalService.decodeAddress(bizStoreEntity);
                bizStoreManager.save(bizStoreEntity);

                receiptEntity.setBizName(bizNameEntity);
                receiptEntity.setBizStore(bizStoreEntity);
            } catch (DuplicateKeyException | IOException e) {
                log.error(e.getLocalizedMessage());

                if(StringUtils.isNotEmpty(bizNameEntity.getId())) {
                    bizNameManager.deleteHard(bizNameEntity);
                }
                BizStoreEntity biz = bizStoreManager.findOne(bizStoreEntity);
                throw new Exception("Address and Phone already registered with another Business Name: " + biz.getBizName().getName());
            }
        } else {
            BizStoreEntity bizStore = bizStoreManager.findOne(bizStoreEntity);
            if(bizStore == null) {
                try {
                    bizStoreEntity.setBizName(bizName);
                    externalService.decodeAddress(bizStoreEntity);
                    bizStoreManager.save(bizStoreEntity);

                    receiptEntity.setBizName(bizName);
                    receiptEntity.setBizStore(bizStoreEntity);
                } catch (DuplicateKeyException | IOException e) {
                    log.error(e.getLocalizedMessage());
                    BizStoreEntity biz = bizStoreManager.findOne(bizStoreEntity);
                    throw new Exception("Address and Phone already registered with another Business Name: " + biz.getBizName().getName());
                }
            } else {
                receiptEntity.setBizName(bizName);
                receiptEntity.setBizStore(bizStore);
            }
        }
    }

    /**
     * This method is being used by Admin to create new Business and Stores. Also this method is being used by receipt update to do the same.
     *
     * @param receiptEntity
     */
    public void saveNewBusinessAndOrStore(ReceiptEntityOCR receiptEntity) throws Exception {
        BizNameEntity bizNameEntity = receiptEntity.getBizName();
        BizStoreEntity bizStoreEntity = receiptEntity.getBizStore();

        BizNameEntity bizName = bizNameManager.findOneByName(bizNameEntity.getName());
        if(bizName == null) {
            try {
                bizNameManager.save(bizNameEntity);

                bizStoreEntity.setBizName(bizNameEntity);
                externalService.decodeAddress(bizStoreEntity);
                bizStoreManager.save(bizStoreEntity);

                receiptEntity.setBizName(bizNameEntity);
                receiptEntity.setBizStore(bizStoreEntity);
            } catch (DuplicateKeyException e) {
                log.error(e.getLocalizedMessage());

                if(StringUtils.isNotEmpty(bizNameEntity.getId())) {
                    bizNameManager.deleteHard(bizNameEntity);
                }
                BizStoreEntity biz = bizStoreManager.findOne(bizStoreEntity);
                throw new Exception("Address and Phone already registered with another Business Name: " + biz.getBizName().getName());
            }
        } else {
            BizStoreEntity bizStore = bizStoreManager.findOne(bizStoreEntity);
            if(bizStore == null) {
                try {
                    bizStoreEntity.setBizName(bizName);
                    externalService.decodeAddress(bizStoreEntity);
                    bizStoreManager.save(bizStoreEntity);

                    receiptEntity.setBizName(bizName);
                    receiptEntity.setBizStore(bizStoreEntity);
                } catch (DuplicateKeyException e) {
                    log.error(e.getLocalizedMessage());
                    BizStoreEntity biz = bizStoreManager.findOne(bizStoreEntity);
                    throw new Exception("Address and Phone already registered with another Business Name: " + biz.getBizName().getName());
                }
            } else {
                receiptEntity.setBizName(bizName);
                receiptEntity.setBizStore(bizStore);
            }
        }
    }

    /**
     * Find last ten business stores for business name
     *
     * @param receiptEntity
     * @return
     */
    public Set<BizStoreEntity> getAllStoresForBusinessName(ReceiptEntity receiptEntity) {
        Set<BizStoreEntity> bizStoreEntities = new HashSet<>();
        bizStoreEntities.addAll(bizStoreManager.findAllWithStartingAddressStartingPhone(null, null, receiptEntity.getBizName()));
        return bizStoreEntities;
    }
}
