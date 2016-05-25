package com.receiptofi.service;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 7:57 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FetcherService {
    private static final Logger LOG = LoggerFactory.getLogger(FetcherService.class);

    @Autowired private ItemManager itemManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private FileSystemService fileSystemService;

    /**
     * This method is called from AJAX to get the matching list of users in the system.
     *
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizName(String bizName) {
        LOG.debug("Search for Biz Name={}", bizName);
        Set<String> titles = bizNameManager.findAllDistinctBizStr(bizName);
        LOG.debug("found business count={}", titles.size());
        return titles;
    }

    /**
     * @param bizAddress
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizAddress(String bizAddress, String bizName) {
        LOG.debug("Search for Biz address={} within name={}", bizAddress, bizName);
        Set<String> address = new HashSet<>();

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if (null != bizNameEntity) {
            List<BizStoreEntity> list = bizStoreManager.getAllWithJustSpecificField(
                    bizAddress,
                    bizNameEntity,
                    BizStoreEntity.ADDRESS_FIELD_NAME);

            address.addAll(list.stream().map(BizStoreEntity::getAddress).collect(Collectors.toList()));
            LOG.debug("found addresses count={} unique count={}", list.size(), address.size());
        }
        return address;
    }

    /**
     *
     * @param bizPhone
     * @param bizAddress
     * @param bizName
     * @return
     */
    public Set<String> findDistinctBizPhone(String bizPhone, String bizAddress, String bizName) {
        LOG.debug("Search for Biz address={} within name={}", bizAddress, bizName);
        Set<String> phone = new HashSet<>();

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if (null != bizNameEntity) {
            List<BizStoreEntity> list = bizStoreManager.getAllWithJustSpecificField(
                    bizPhone,
                    bizAddress,
                    bizNameEntity,
                    BizStoreEntity.PHONE_FIELD_NAME);

            phone.addAll(list.stream().map(bizStore -> CommonUtil.phoneFormatter(bizStore.getPhone(), bizStore.getCountryShortName())).collect(Collectors.toList()));
            LOG.debug("found phones count={} unique count={}", list.size(), phone.size());
        }
        return phone;
    }

    /**
     * This method is called from AJAX to get the matching list of items in the system.
     * Populates with just the 'name' of the item.
     *
     * @param itemName
     * @param bizName
     * @return
     */
    public Set<String> findDistinctItems(String itemName, String bizName) {
        LOG.debug("Search for item name={} Biz Name={}", itemName, bizName);
        List<ItemEntity> itemList = itemManager.findItems(itemName, bizName);
        Set<String> itemSet = itemList.stream().map(ItemEntity::getName).collect(Collectors.toSet());
        LOG.debug("found item count={} unique count={}", itemList.size(), itemSet.size());
        return itemSet;
    }

    public void changeFSImageOrientation(String fileSystemId, int imageOrientation, String blobId) throws Exception {
        FileSystemEntity fileSystem = fileSystemService.getById(fileSystemId);
        if (blobId.equalsIgnoreCase(fileSystem.getBlobId())) {
            fileSystem.setImageOrientation(fileSystem.getImageOrientation() + imageOrientation);
            fileSystem.switchHeightAndWidth();
            fileSystemService.save(fileSystem);
        }
    }
}
