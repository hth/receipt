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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public Map<String, Long> countReceiptForBizStore(Set<BizStoreEntity> bizStoreEntities) {
        Map<String, Long> bizReceiptCount = new HashMap<>();
        for (BizStoreEntity bizStoreEntity : bizStoreEntities) {
            long count = receiptService.countAllReceiptForAStore(bizStoreEntity);
            bizReceiptCount.put(bizStoreEntity.getId(), count);
        }
        return bizReceiptCount;
    }

    public long countReceiptForBizName(BizNameEntity bizNameEntity) {
        return receiptService.countAllReceiptForABizName(bizNameEntity);
    }

    /**
     * This method is being used by Admin to create new Business and Stores. Also this method is being used by
     * receipt update to do the same.
     *
     * @param receiptEntity
     */
    public void saveNewBusinessAndOrStore(ReceiptEntity receiptEntity) throws Exception {
        BizNameEntity bizNameEntity = receiptEntity.getBizName();
        BizStoreEntity bizStoreEntity = receiptEntity.getBizStore();

        BizNameEntity bizName = bizNameManager.findOneByName(bizNameEntity.getBusinessName());
        if (null == bizName) {
            try {
                bizNameManager.save(bizNameEntity);

                bizStoreEntity.setBizName(bizNameEntity);
                externalService.decodeAddress(bizStoreEntity);
                bizStoreManager.save(bizStoreEntity);

                receiptEntity.setBizName(bizNameEntity);
                receiptEntity.setBizStore(bizStoreEntity);
            } catch (DuplicateKeyException e) {
                BizStoreEntity biz = bizStoreManager.findOne(bizStoreEntity);
                LOG.error("Address and Phone already registered with another Business Name={}, reason={}",
                        biz.getBizName().getBusinessName(), e.getLocalizedMessage(), e);

                if (StringUtils.isNotEmpty(bizNameEntity.getId())) {
                    bizNameManager.deleteHard(bizNameEntity);
                }

                throw new Exception("Address and Phone already registered with another Business Name: " +
                        biz.getBizName().getBusinessName());
            }
        } else {
            BizStoreEntity bizStore = bizStoreManager.findOne(bizStoreEntity);
            if (null == bizStore) {
                try {
                    bizStoreEntity.setBizName(bizName);
                    externalService.decodeAddress(bizStoreEntity);
                    bizStoreManager.save(bizStoreEntity);

                    receiptEntity.setBizName(bizName);
                    receiptEntity.setBizStore(bizStoreEntity);
                } catch (DuplicateKeyException e) {
                    BizStoreEntity biz = bizStoreManager.findOne(bizStoreEntity);
                    LOG.error("Address and Phone already registered with another Business Name={}, reason={}",
                            biz.getBizName().getBusinessName(), e.getLocalizedMessage(), e);
                    throw new Exception("Address and Phone already registered with another Business Name: " +
                            biz.getBizName().getBusinessName());
                }
            } else if (!bizStore.isValidatedUsingExternalAPI()) {
                externalService.decodeAddress(bizStore);
                bizStoreManager.save(bizStore);

                receiptEntity.setBizName(bizName);
                receiptEntity.setBizStore(bizStore);
            } else {
                receiptEntity.setBizName(bizName);
                receiptEntity.setBizStore(bizStore);
            }
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
}
