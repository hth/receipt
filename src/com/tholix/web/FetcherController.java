package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.joda.time.DateTime;

import com.tholix.domain.BizNameEntity;
import com.tholix.service.BizNameManager;
import com.tholix.service.BizStoreManager;
import com.tholix.service.ItemManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * User: hitender
 * Date: 4/19/13
 * Time: 11:44 PM
 */
@Controller
@RequestMapping(value = "/fetcher")
@SessionAttributes({"userSession"})
public class FetcherController {
    private static final Logger log = Logger.getLogger(FetcherController.class);

    @Autowired private ItemManager itemManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;

    @RequestMapping(value = "/find_company", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchBiz(@RequestParam("term") String bizName) {
        return findBizName(bizName);
    }

    @RequestMapping(value = "/find_address", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchBiz(@RequestParam("term") String bizAddress, @RequestParam("extraParam") String bizName) {
        return findBizAddress(bizAddress, bizName);
    }

    @RequestMapping(value = "/find_item", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchItem(@RequestParam("term") String itemName, @RequestParam("extraParam") String bizName) {
        return findItems(itemName, bizName);
    }

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param bizName
     * @return
     */
    private List<String> findBizName(String bizName) {
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
    private List<String> findBizAddress(String bizAddress, String bizName) {
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
    private List<String> findItems(String itemName, String bizName) {
        DateTime time = DateUtil.now();
        log.info("Search for item name: " + itemName + ", within Biz Name: " + bizName);
        List<String> items = itemManager.findItems(itemName, bizName);
        log.info("found item.. total size " + items.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return items;
    }
}
