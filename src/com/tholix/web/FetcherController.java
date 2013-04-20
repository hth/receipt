package com.tholix.web;

import java.util.ArrayList;
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

import com.tholix.domain.UserProfileEntity;
import com.tholix.service.ReceiptManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.UserSearchForm;

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

    @Autowired private ReceiptManager receiptManager;

    @RequestMapping(value = "/find_company", method = RequestMethod.GET)
    public @ResponseBody
    List<String> findBusinessTitle(@RequestParam("term") String name) {
        return findTitles(name);
    }

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param title
     * @return
     */
    private List<String> findTitles(String title) {
        DateTime time = DateUtil.now();
        log.info("Search string for business name: " + title);
        List<String> titles = receiptManager.findTitles(title);
        log.info("found business.. total size " + titles.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return titles;
    }
}
