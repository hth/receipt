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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.ReceiptUpdateService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ReceiptOCRForm;
import com.tholix.web.validator.ReceiptOCRFormValidator;

/**
 * Class manages first processing of a receipt. That includes loading of a receipts by technician.
 * Updating of a receipt by technician. Same is true for recheck of receipt by technician.
 *
 * @author hitender
 * @since Jan 7, 2013 2:13:22 AM
 */
@Controller
@RequestMapping(value = "/emp")
@SessionAttributes({"userSession"})
public class ReceiptUpdateController {
    private static final Logger log = Logger.getLogger(ReceiptUpdateController.class);

	private static final String nextPage = "update";
    private static final String nextPageRecheck = "recheck";
    public static final String REDIRECT_EMP_LANDING_HTM = "redirect:/emp/landing.htm";

    @Autowired private ReceiptOCRFormValidator receiptOCRFormValidator;
    @Autowired private ReceiptUpdateService receiptUpdateService;

    /**
     * Loads the receipts for technician.
     *
     * Added logic to make sure only the user of the receipt or technician can see the receipt.
     *
     * @param userSession
     * @param receiptOCRId
     * @param receiptOCRForm
     * @return
     */
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ModelAndView update(@ModelAttribute("userSession") UserSession userSession, @RequestParam("id") String receiptOCRId, @ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm) {
        DateTime time = DateUtil.now();
        ReceiptEntityOCR receipt = receiptUpdateService.loadReceiptOCRById(receiptOCRId);
        if(receipt == null) {
            if(userSession.getLevel().value >= UserLevelEnum.WORKER.getValue()) {
                log.info("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
            } else {
                log.warn("No such receipt exists. Request made by: " + userSession.getUserProfileId());
            }
        } else if(userSession.getUserProfileId().equalsIgnoreCase(receipt.getUserProfileId()) || (userSession.getLevel().value >= UserLevelEnum.WORKER.getValue())) {
            receiptOCRForm.setReceiptOCR(receipt);

            List<ItemEntityOCR> items = receiptUpdateService.loadItemsOfReceipt(receipt);
            receiptOCRForm.setItems(items);
        } else {
            log.warn("Un-authorized access by user: " + userSession.getUserProfileId() + ", accessing receipt: " + receiptOCRId);
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(nextPage);
	}

    /**
     * Process receipt after submitted by technician
     *
     * @param receiptOCRForm
     * @param result
     * @return
     */
	@RequestMapping(value = "/update", method = RequestMethod.POST, params="update")
	public String update(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm, BindingResult result) {
        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptOCRForm.getReceiptOCR().getId() + " ; Title : " + receiptOCRForm.getReceiptOCR().getBizName().getName());
		receiptOCRFormValidator.validate(receiptOCRForm, result);
		if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return nextPage;
		}

        try {
            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptOCRForm.getReceiptEntity();
            List<ItemEntity> items = receiptOCRForm.getItemEntity(receipt);
            ReceiptEntityOCR receiptOCR = receiptOCRForm.getReceiptOCR();
            receiptUpdateService.turkReceipt(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return REDIRECT_EMP_LANDING_HTM;
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("receipt", "", exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return nextPage;
        }
	}

    /**
     * Reject receipt since it can't be processed or its not a receipt
     *
     * @param receiptOCRForm
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, params="reject")
    public ModelAndView reject(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm) {
        DateTime time = DateUtil.now();
        log.info("Rejecting Receipt OCR: " + receiptOCRForm.getReceiptOCR().getId());
        try {
            ReceiptEntityOCR receiptOCR = receiptOCRForm.getReceiptOCR();
            receiptUpdateService.turkReject(receiptOCR);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt reject");

            receiptOCRForm.setErrorMessage("Receipt could not be processed for Reject. Contact administrator with Receipt OCR # " + receiptOCRForm.getReceiptOCR().getId());
            return new ModelAndView(nextPage);
        }
    }

    /**
     * Loads receipt for recheck for technician
     *
     * @param userSession
     * @param receiptOCRId
     * @param receiptOCRForm
     * @return
     */
    @RequestMapping(value = "/recheck", method = RequestMethod.GET)
    public ModelAndView recheck(@ModelAttribute("userSession") UserSession userSession, @RequestParam("id") String receiptOCRId, @ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm) {
        DateTime time = DateUtil.now();
        this.update(userSession, receiptOCRId, receiptOCRForm);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(nextPageRecheck);
    }

    /**
     * Process receipt for after recheck by technician
     *
     * @param receiptOCRForm
     * @param result
     * @return
     */
    @RequestMapping(value = "/recheck", method = RequestMethod.POST)
    public String recheck(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm, BindingResult result) {
        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptOCRForm.getReceiptOCR().getId() + " ; Title : " + receiptOCRForm.getReceiptOCR().getBizName().getName());
        receiptOCRFormValidator.validate(receiptOCRForm, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return nextPageRecheck;
        }

        try {
            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptOCRForm.getReceiptEntity();
            List<ItemEntity> items = receiptOCRForm.getItemEntity(receipt);
            ReceiptEntityOCR receiptOCR = receiptOCRForm.getReceiptOCR();
            receiptUpdateService.turkReceiptReCheck(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return REDIRECT_EMP_LANDING_HTM;
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("receipt", "", exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt recheck save");
            return nextPageRecheck;
        }
    }

    /**
     * Delete operation can only be performed by user and not technician
     *
     * @param receiptOCRForm
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm) {
        receiptUpdateService.deletePendingReceiptOCR(receiptOCRForm.getReceiptOCR());
        return "redirect:/pending.htm";
    }
}
