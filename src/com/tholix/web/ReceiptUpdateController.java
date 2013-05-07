/**
 *
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.service.ReceiptUpdateService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ReceiptForm;
import com.tholix.web.validator.ReceiptFormValidator;

/**
 * @author hitender
 * @when Jan 7, 2013 2:13:22 AM
 *
 */
@Controller
@RequestMapping(value = "/emp")
public class ReceiptUpdateController {
    private static final Logger log = Logger.getLogger(ReceiptUpdateController.class);

	private static final String nextPage = "update";
    private static final String nextPageRecheck = "recheck";
    public static final String REDIRECT_EMP_LANDING_HTM = "redirect:/emp/landing.htm";

    @Autowired private ReceiptFormValidator receiptFormValidator;
    @Autowired private ReceiptUpdateService receiptUpdateService;

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ModelAndView update(@RequestParam("id") String receiptOCRId, @ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        ReceiptEntityOCR receipt = receiptUpdateService.loadReceiptOCRById(receiptOCRId);
		receiptForm.setReceipt(receipt);

		List<ItemEntityOCR> items = receiptUpdateService.loadItemsOfReceipt(receipt);
		receiptForm.setItems(items);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return new ModelAndView(nextPage);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("receiptForm") ReceiptForm receiptForm, BindingResult result) {
        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptForm.getReceipt().getId() + " ; Title : " + receiptForm.getReceipt().getBizName().getName());
		receiptFormValidator.validate(receiptForm, result);
		if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return nextPage;
		}

        try {
            ReceiptEntity receipt = receiptForm.getReceiptEntity();
            List<ItemEntity> items = receiptForm.getItemEntity(receipt);
            ReceiptEntityOCR receiptOCR = receiptForm.getReceipt();
            receiptUpdateService.turkReceipt(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return REDIRECT_EMP_LANDING_HTM;
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("receipt", exce.getLocalizedMessage(), exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return nextPage;
        }
	}

    @RequestMapping(value = "/recheck", method = RequestMethod.GET)
    public ModelAndView recheck(@RequestParam("id") String receiptOCRId, @ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        update(receiptOCRId, receiptForm);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(nextPageRecheck);
    }

    @RequestMapping(value = "/recheck", method = RequestMethod.POST)
    public String recheck(@ModelAttribute("receiptForm") ReceiptForm receiptForm, BindingResult result) {
        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptForm.getReceipt().getId() + " ; Title : " + receiptForm.getReceipt().getBizName().getName());
        receiptFormValidator.validate(receiptForm, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return nextPageRecheck;
        }

        try {
            ReceiptEntity receipt = receiptForm.getReceiptEntity();
            List<ItemEntity> items = receiptForm.getItemEntity(receipt);
            ReceiptEntityOCR receiptOCR = receiptForm.getReceipt();
            receiptUpdateService.turkReceiptReCheck(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return REDIRECT_EMP_LANDING_HTM;
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("receipt", exce.getLocalizedMessage(), exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return nextPageRecheck;
        }
    }
}
