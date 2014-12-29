package com.receiptofi.web.controller.access;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.ReceiptService;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * User: hitender
 * Date: 5/12/13
 * Time: 1:23 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/day")
public class ThisDayController {
    private static final Logger LOG = LoggerFactory.getLogger(ThisDayController.class);
    private static final String nextPage = "/day";

    @Autowired private ReceiptService receiptService;

    @RequestMapping (method = RequestMethod.GET)
    public ModelAndView getThisDay(@RequestParam ("date") String date) {
        Long longDate = Long.parseLong(date);
        DateTime dateTime = new DateTime(longDate);
        List<ReceiptEntity> receipts = receiptService.findReceipt(
                dateTime,
                ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid()
        );

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("receipts", receipts);
        return modelAndView;
    }
}
