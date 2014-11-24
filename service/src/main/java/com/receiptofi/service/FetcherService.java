package com.receiptofi.service;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.utils.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 7:57 PM
 */
@Service
public final class FetcherService {
    private static final Logger LOG = LoggerFactory.getLogger(FetcherService.class);

    @Autowired private ItemManager itemManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private FileSystemService fileSystemService;

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizName(String bizName) {
        LOG.info("Search for Biz Name={}", bizName);
        Set<String> titles = bizNameManager.findAllDistinctBizStr(bizName);
        LOG.info("found business count={}", titles.size());
        return titles;
    }

    /**
     * @param bizAddress
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizAddress(String bizAddress, String bizName) {
        LOG.info("Search for Biz address={} within name={}", bizAddress, bizName);
        Set<String> address = new HashSet<>();

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if (null != bizNameEntity) {
            List<BizStoreEntity> list = bizStoreManager.getAllWithJustSpecificField(
                    bizAddress, bizNameEntity, BizStoreEntity.ADDRESS_FIELD_NAME);

            for (BizStoreEntity bizStoreEntity : list) {
                address.add(bizStoreEntity.getAddress());
            }

            LOG.info("found addresses count={} unique count={}", list.size(), address.size());
        }
        return address;
    }

    /**
     * @param bizAddress
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizPhone(String bizPhone, String bizAddress, String bizName) {
        LOG.info("Search for Biz address={} within name={}", bizAddress, bizName);
        Set<String> phone = new HashSet<>();

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if (null != bizNameEntity) {
            List<BizStoreEntity> list = bizStoreManager.getAllWithJustSpecificField(
                    bizPhone, bizAddress, bizNameEntity, BizStoreEntity.PHONE_FIELD_NAME);

            for (BizStoreEntity bizStoreEntity : list) {
                phone.add(Formatter.phone(bizStoreEntity.getPhone()));
            }

            LOG.info("found phones count={} unique count={}", list.size(), phone.size());
        }
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
        LOG.info("Search for item name={} Biz Name={}", itemName, bizName);
        List<ItemEntity> itemList = itemManager.findItems(itemName, bizName);

        Set<String> itemSet = new HashSet<>();
        for (ItemEntity re : itemList) {
            itemSet.add(re.getName());
        }

        LOG.info("found item count={} unique count={}", itemList.size(), itemSet.size());
        return itemSet;
    }

    public void changeFSImageOrientation(String fileSystemId, int imageOrientation, String blobId) throws Exception {
        FileSystemEntity fileSystemEntity = fileSystemService.findById(fileSystemId);
        if (blobId.equalsIgnoreCase(fileSystemEntity.getBlobId())) {
            fileSystemEntity.setImageOrientation(fileSystemEntity.getImageOrientation() + imageOrientation);
            fileSystemEntity.switchHeightAndWidth();
            fileSystemService.save(fileSystemEntity);
        }
    }
}
