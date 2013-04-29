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

import com.tholix.service.FetcherService;

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

    @Autowired FetcherService fetcherService;

    @RequestMapping(value = "/find_company", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchBiz(@RequestParam("term") String bizName) {
        return fetcherService.findBizName(bizName);
    }

    @RequestMapping(value = "/find_address", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchBiz(@RequestParam("term") String bizAddress, @RequestParam("extraParam") String bizName) {
        return fetcherService.findBizAddress(bizAddress, bizName);
    }

    @RequestMapping(value = "/find_item", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchItem(@RequestParam("term") String itemName, @RequestParam("extraParam") String bizName) {
        return fetcherService.findItems(itemName, bizName);
    }
}
