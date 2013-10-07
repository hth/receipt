/**
 *
 */
package com.tholix.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import com.tholix.web.validator.ReceiptOCRValidator;

/**
 * Class manages first processing of a receipt. That includes loading of a receipts by technician.
 * Updating of a receipt by technician. Same is true for recheck of receipt by technician.
 *
 * This same class is used for showing the pending receipt to user
 *
 * @author hitender
 * @since Jan 7, 2013 2:13:22 AM
 */
@Controller
@RequestMapping(value = "/emp")
@SessionAttributes({"userSession"})
public class ReceiptUpdateController {
    private static final Logger log = Logger.getLogger(ReceiptUpdateController.class);

	private static final String NEXT_PAGE_UPDATE        = "/update";
    private static final String NEXT_PAGE_RECHECK       = "/recheck";
    public static final String REDIRECT_EMP_LANDING_HTM = "redirect:/emp/landing.htm";

    @Autowired private ReceiptOCRValidator receiptOCRValidator;
    @Autowired private ReceiptUpdateService receiptUpdateService;

    //TODO fix this to get the data from properties file
    @Value("${duplicate.receipt}")
    private static String duplicateReceiptMessage = "Found pre-existing receipt with similar information for the " +
            "selected date. Suggestion: Confirm the receipt data or else mark as duplicate by rejecting this receipt.";

    /**
     * For Technician: Loads new receipts.
     * For User :Method helps user to load either pending new receipt or pending recheck receipt.
     *
     * Added logic to make sure only the user of the receipt or technician can see the receipt.
     *
     * @param userSession
     * @param receiptOCRId
     * @param receiptOCRForm
     * @return
     */
	@RequestMapping(value = "/update/{receiptOCRId}", method = RequestMethod.GET)
	public ModelAndView update(@PathVariable String receiptOCRId, @ModelAttribute("userSession") UserSession userSession,
                               @ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm,
                               final Model model) {

        DateTime time = DateUtil.now();
        loadBasedOnAppropriateUserLevel(receiptOCRId, userSession, receiptOCRForm);

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.receiptOCRForm", model.asMap().get("result"));
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(NEXT_PAGE_UPDATE);
	}

    /**
     * For Technician: Loads recheck receipt
     *
     * @param userSession
     * @param receiptOCRId
     * @param receiptOCRForm
     * @return
     */
    @RequestMapping(value = "/recheck/{receiptOCRId}", method = RequestMethod.GET)
    public ModelAndView recheck(@PathVariable String receiptOCRId, @ModelAttribute("userSession") UserSession userSession,
                                @ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm,
                                final Model model) {

        DateTime time = DateUtil.now();
        loadBasedOnAppropriateUserLevel(receiptOCRId, userSession, receiptOCRForm);

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.receiptOCRForm", model.asMap().get("result"));
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(NEXT_PAGE_RECHECK);
    }

    /**
     * Process receipt after submitted by technician
     *
     * @param receiptOCRForm
     * @param result
     * @return
     */
	@RequestMapping(value = "/submit", method = RequestMethod.POST, params= "submit")
	public ModelAndView submit(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm,
                         BindingResult result,
                         final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptOCRForm.getReceiptOCR().getId() + " ; Title : " + receiptOCRForm.getReceiptOCR().getBizName().getName());
		receiptOCRValidator.validate(receiptOCRForm, result);
		if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptOCRForm.getReceiptOCR().getId() + ".htm");
		}

        try {
            if(receiptUpdateService.checkIfDuplicate(receiptOCRForm.getReceiptEntity().getCheckSum())) {
                log.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");
                result.rejectValue("errorMessage", "", duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("result", result);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptOCRForm.getReceiptOCR().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptOCRForm.getReceiptEntity();
            List<ItemEntity> items = receiptOCRForm.getItemEntity(receipt);
            receiptOCRForm.updateItemWithTaxAmount(items, receipt);
            ReceiptEntityOCR receiptOCR = receiptOCRForm.getReceiptOCR();

            receiptUpdateService.turkReceipt(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("errorMessage", "", exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptOCRForm.getReceiptOCR().getId() + ".htm");
        }
	}

    /**
     * Reject receipt since it can't be processed or its not a receipt
     *
     * @param receiptOCRForm
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, params="reject")
    public ModelAndView reject(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm) {
        DateTime time = DateUtil.now();
        log.info("Beginning of Rejecting Receipt OCR: " + receiptOCRForm.getReceiptOCR().getId());
        try {
            ReceiptEntityOCR receiptOCR = receiptOCRForm.getReceiptOCR();
            receiptUpdateService.turkReject(receiptOCR);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt reject");

            receiptOCRForm.setErrorMessage("Receipt could not be processed for Reject. Contact administrator with Receipt OCR # " + receiptOCRForm.getReceiptOCR().getId());
            return new ModelAndView(NEXT_PAGE_UPDATE);
        }
    }

    /**
     * Process receipt for after recheck by technician
     *
     * @param receiptOCRForm
     * @param result
     * @return
     */
    @RequestMapping(value = "/recheck", method = RequestMethod.POST)
    public ModelAndView recheck(@ModelAttribute("receiptOCRForm") ReceiptOCRForm receiptOCRForm,
                                BindingResult result,
                                final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptOCRForm.getReceiptOCR().getId() + " ; Title : " + receiptOCRForm.getReceiptOCR().getBizName().getName());
        receiptOCRValidator.validate(receiptOCRForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptOCRForm.getReceiptOCR().getId() + ".htm");
        }

        try {
            //TODO: Note should not happen as the condition to check for duplicate has already been satisfied when receipt was first processed.
            // Unless Technician has changed the date or some data. Date change should be exclude during re-check. Something to think about.
            if(receiptUpdateService.checkIfDuplicate(receiptOCRForm.getReceiptEntity().getCheckSum())) {
                log.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");
                result.rejectValue("errorMessage", "", duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("result", result);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptOCRForm.getReceiptOCR().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptOCRForm.getReceiptEntity();
            List<ItemEntity> items = receiptOCRForm.getItemEntity(receipt);
            receiptOCRForm.updateItemWithTaxAmount(items, receipt);
            ReceiptEntityOCR receiptOCR = receiptOCRForm.getReceiptOCR();

            receiptUpdateService.turkReceiptReCheck(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("errorMessage", "", exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt recheck save");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptOCRForm.getReceiptOCR().getId() + ".htm");
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
        //Check cannot delete a pending receipt which has been processed once, i.e. has receipt id
        //The check here is not required but its better to check before calling service method
        if(StringUtils.isEmpty(receiptOCRForm.getReceiptOCR().getReceiptId())) {
            receiptUpdateService.deletePendingReceiptOCR(receiptOCRForm.getReceiptOCR());
        }
        return "redirect:/pending.htm";
    }

    private void loadBasedOnAppropriateUserLevel(String receiptOCRId, UserSession userSession, ReceiptOCRForm receiptOCRForm) {
        ReceiptEntityOCR receipt = receiptUpdateService.loadReceiptOCRById(receiptOCRId);
        if(receipt == null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                log.info("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
            } else {
                log.warn("No such receipt exists. Request made by: " + userSession.getUserProfileId());
            }
        } else if(userSession.getUserProfileId().equalsIgnoreCase(receipt.getUserProfileId()) || (userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue())) {
            receiptOCRForm.setReceiptOCR(receipt);

            List<ItemEntityOCR> items = receiptUpdateService.loadItemsOfReceipt(receipt);
            receiptOCRForm.setItems(items);
        } else {
            log.warn("Un-authorized access by user: " + userSession.getUserProfileId() + ", accessing receipt: " + receiptOCRId);
        }
    }
}
