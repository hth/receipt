package com.tholix.service;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.BizNameEntity;
import com.tholix.repository.BizNameManager;
import com.tholix.repository.BizStoreManager;
import com.tholix.repository.ItemManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 7:57 PM
 */
@Service
public class FetcherService {
    private static final Logger log = Logger.getLogger(FetcherService.class);

    @Autowired private ItemManager itemManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param bizName
     * @return
     */
    public List<String> findBizName(String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for Biz Name: " + bizName);
        List<String> titles = bizNameManager.findAllBizStr(bizName);
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
    public List<String> findBizAddress(String bizAddress, String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for Biz address: " + bizAddress + ", within Biz Name" + bizName);
        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        List<String> list = bizStoreManager.findAllAddress(bizAddress, bizNameEntity);
        log.info("found item.. total size " + list.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return list;
    }

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param itemName
     * @param bizName
     * @return
     */
    public List<String> findItems(String itemName, String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for item name: " + itemName + ", within Biz Name: " + bizName);
        List<String> items = itemManager.findItems(itemName, bizName);
        log.info("found item.. total size " + items.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return items;
    }
}
