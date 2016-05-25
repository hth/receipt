package com.receiptofi.service;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizStoreManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 8/8/13
 * Time: 8:48 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizService {
    private static final Logger LOG = LoggerFactory.getLogger(BizService.class);

    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private ExternalService externalService;

    @Autowired
    public BizService(
            BizNameManager bizNameManager,
            BizStoreManager bizStoreManager,
            ExternalService externalService) {
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.externalService = externalService;
    }

    public BizNameEntity getByBizNameId(String bizId) {
        return bizNameManager.getById(bizId);
    }

    public void saveName(BizNameEntity bizNameEntity) {
        bizNameManager.save(bizNameEntity);
    }

    public BizStoreEntity getByStoreId(String storeId) {
        return bizStoreManager.getById(storeId);
    }

    public void saveStore(BizStoreEntity bizStoreEntity) {
        bizStoreManager.save(bizStoreEntity);
    }

    public Set<BizStoreEntity> bizSearch(String businessName, String bizAddress, String bizPhone) {
        Set<BizStoreEntity> bizStoreEntities = new HashSet<>();

        if (StringUtils.isNotEmpty(businessName)) {
            List<BizNameEntity> bizNameEntities = bizNameManager.findAllBizWithMatchingName(businessName);
            for (BizNameEntity bizNameEntity : bizNameEntities) {
                List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(
                        bizAddress,
                        bizPhone,
                        bizNameEntity);
                bizStoreEntities.addAll(bizStores);
            }
        } else {
            List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(
                    bizAddress,
                    bizPhone,
                    null);
            bizStoreEntities.addAll(bizStores);
        }
        return bizStoreEntities;
    }

    /**
     * This method is being used by Admin to create new Business and Stores and is being used by receipt update to do
     * the same.
     *
     * @param receipt
     */
    public void saveNewBusinessAndOrStore(ReceiptEntity receipt) {
        BizNameEntity bizNameEntity = receipt.getBizName();
        BizStoreEntity bizStoreEntity = receipt.getBizStore();

        BizNameEntity bizName = bizNameManager.findOneByName(bizNameEntity.getBusinessName());
        if (null == bizName) {
            try {
                bizNameManager.save(bizNameEntity);

                bizStoreEntity.setBizName(bizNameEntity);
                if (!bizStoreEntity.isValidatedUsingExternalAPI()) {
                    externalService.decodeAddress(bizStoreEntity);
                }
                bizStoreManager.save(bizStoreEntity);

                receipt.setBizName(bizNameEntity);
                receipt.setBizStore(bizStoreEntity);
            } catch (DuplicateKeyException e) {
                BizStoreEntity biz = findMatchingStore(bizStoreEntity.getAddress(), bizStoreEntity.getPhone());
                LOG.error("Address and Phone already registered with another Business Name={}, reason={}",
                        biz.getBizName().getBusinessName(), e.getLocalizedMessage(), e);

                if (StringUtils.isNotEmpty(bizNameEntity.getId())) {
                    bizNameManager.deleteHard(bizNameEntity);
                }

                throw new RuntimeException("Address and Phone already registered with another Business Name: " +
                        biz.getBizName().getBusinessName());
            }
        } else {
            BizStoreEntity bizStore = findMatchingStore(bizStoreEntity.getAddress(), bizStoreEntity.getPhone());
            if (null == bizStore
                    /** OR condition is when address or phones is corrected or updated during re-check. */
                    || !bizStore.getAddress().equals(bizStoreEntity.getAddress())
                    || !bizStore.getPhone().equals(bizStoreEntity.getPhone())) {
                updateReceiptWithNewBizStore(bizStoreEntity, bizName, receipt);

            } else if (!bizStore.isValidatedUsingExternalAPI()) {
                bizStoreManager.save(bizStore);

                receipt.setBizName(bizName);
                receipt.setBizStore(bizStore);
            } else {
                receipt.setBizName(bizName);
                receipt.setBizStore(bizStore);
            }
        }
    }

    private void updateReceiptWithNewBizStore(BizStoreEntity bizStoreEntity, BizNameEntity bizName, ReceiptEntity receiptEntity) {
        try {
            bizStoreEntity.setBizName(bizName);
            if (!bizStoreEntity.isValidatedUsingExternalAPI()) {
                externalService.decodeAddress(bizStoreEntity);
            }
            bizStoreManager.save(bizStoreEntity);

            receiptEntity.setBizName(bizName);
            receiptEntity.setBizStore(bizStoreEntity);
        } catch (DuplicateKeyException e) {
            BizStoreEntity biz = findMatchingStore(bizStoreEntity.getAddress(), bizStoreEntity.getPhone());
            LOG.error("Address and Phone already registered with another Business Name={}, reason={}",
                    biz.getBizName().getBusinessName(), e.getLocalizedMessage(), e);

            throw new RuntimeException("Address and Phone already registered with another Business Name: " +
                    biz.getBizName().getBusinessName());
        }
    }

    /**
     * Copies BizName and BizStore from ReceiptEntity to DocumentEntity.
     *
     * @param document
     * @param receipt
     */
    public void copyBizNameAndBizStoreFromReceipt(DocumentEntity document, ReceiptEntity receipt) {
        document.setBizName(receipt.getBizName());
        document.setBizStore(receipt.getBizStore());
    }

    /**
     * Find last ten business stores for business name having the same Id as is BizNameEntity. Some business with same
     * name will not show up here. It will match the BizNameEntity Id for recently added business store with address
     * or with just added new address for existing business store.
     *
     * @param receiptEntity
     * @return
     */
    public Set<BizStoreEntity> getAllStoresForSameBusinessNameId(ReceiptEntity receiptEntity) {
        Set<BizStoreEntity> bizStoreEntities = new HashSet<>();
        bizStoreEntities.addAll(
                bizStoreManager.findAllWithStartingAddressStartingPhone(
                        null,
                        null,
                        receiptEntity.getBizName()
                )
        );
        return bizStoreEntities;
    }

    public void deleteBizStore(BizStoreEntity bizStore) {
        bizStoreManager.deleteHard(bizStore);
    }

    public void deleteBizName(BizNameEntity bizName) {
        bizNameManager.deleteHard(bizName);
    }

    public BizStoreEntity findMatchingStore(String address, String phone) {
        return bizStoreManager.findMatchingStore(address, phone);
    }

    public BizStoreEntity findOneBizStore(String bizNameId) {
        return bizStoreManager.findOne(bizNameId);
    }
}
