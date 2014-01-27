/**
 *
 */
package com.receiptofi.web.controller;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.ReceiptUpdateService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.ReceiptDocumentForm;
import com.receiptofi.web.validator.MileageDocumentValidator;
import com.receiptofi.web.validator.ReceiptDocumentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
    private static final Logger log = LoggerFactory.getLogger(ReceiptUpdateController.class);

	private static final String NEXT_PAGE_UPDATE        = "/update";
    private static final String NEXT_PAGE_RECHECK       = "/recheck";
    public static final String REDIRECT_EMP_LANDING_HTM = "redirect:/emp/landing.htm";

    @Autowired private ReceiptDocumentValidator receiptDocumentValidator;
    @Autowired private MileageDocumentValidator mileageDocumentValidator;
    @Autowired private ReceiptUpdateService receiptUpdateService;

    @Value("${duplicate.receipt}")
    private String duplicateReceiptMessage;

    /**
     * For Technician: Loads new receipts.
     * For User :Method helps user to load either pending new receipt or pending recheck receipt.
     *
     * Added logic to make sure only the user of the receipt or technician can see the receipt.
     *
     * @param userSession
     * @param receiptOCRId
     * @param receiptDocumentForm
     * @return
     */
	@RequestMapping(value = "/update/{receiptOCRId}", method = RequestMethod.GET)
	public ModelAndView update(@PathVariable String receiptOCRId, @ModelAttribute("userSession") UserSession userSession,
                               @ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm,
                               final Model model) {

        DateTime time = DateUtil.now();

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.receiptDocumentForm", model.asMap().get("result"));
            receiptDocumentForm = (ReceiptDocumentForm) model.asMap().get("receiptDocumentForm");
            loadBasedOnAppropriateUserLevel(receiptOCRId, userSession, receiptDocumentForm);
        } else {
            loadBasedOnAppropriateUserLevel(receiptOCRId, userSession, receiptDocumentForm);
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(NEXT_PAGE_UPDATE);
	}

    /**
     * For Technician: Loads recheck receipt
     *
     * @param userSession
     * @param receiptOCRId
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping(value = "/recheck/{receiptOCRId}", method = RequestMethod.GET)
    public ModelAndView recheck(@PathVariable String receiptOCRId, @ModelAttribute("userSession") UserSession userSession,
                                @ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm,
                                final Model model) {

        DateTime time = DateUtil.now();

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.receiptDocumentForm", model.asMap().get("result"));
            receiptDocumentForm = (ReceiptDocumentForm) model.asMap().get("receiptDocumentForm");
            loadBasedOnAppropriateUserLevel(receiptOCRId, userSession, receiptDocumentForm);
        } else {
            loadBasedOnAppropriateUserLevel(receiptOCRId, userSession, receiptDocumentForm);
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(NEXT_PAGE_RECHECK);
    }

    /**
     * Process receipt after submitted by technician
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
	@RequestMapping(value = "/submit", method = RequestMethod.POST, params= "receipt-submit")
	public ModelAndView submit(@ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm,
                         BindingResult result,
                         final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptDocumentForm.getReceiptDocument().getId() + " ; Title : " + receiptDocumentForm.getReceiptDocument().getBizName().getName());
		receiptDocumentValidator.validate(receiptDocumentForm, result);
		if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
		}

        try {
            if(receiptUpdateService.checkIfDuplicate(receiptDocumentForm.getReceiptEntity().getCheckSum())) {
                log.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");
                result.rejectValue("errorMessage", "", duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("result", result);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptDocumentForm.getReceiptEntity();
            List<ItemEntity> items = receiptDocumentForm.getItemEntity(receipt);
            receiptDocumentForm.updateItemWithTaxAmount(items, receipt);
            DocumentEntity documentForm = receiptDocumentForm.getReceiptDocument();

            receiptUpdateService.turkReceipt(receipt, items, documentForm);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error("Error in Submit Process: " + exce.getLocalizedMessage());
            result.rejectValue("errorMessage", "", exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
	}

    /**
     * Process receipt after submitted by technician
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping(value = "/submitMileage", method = RequestMethod.POST, params= "mileage-submit")
    public ModelAndView submitMileage(@ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm,
                                      BindingResult result,
                                      final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        switch(receiptDocumentForm.getReceiptDocument().getDocumentOfType()) {
            case MILEAGE:
                log.info("Mileage : ");
                break;
        }

        mileageDocumentValidator.validate(receiptDocumentForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }

        try {
            MileageEntity mileage = receiptDocumentForm.getMileageEntity();
            DocumentEntity receiptOCR = receiptDocumentForm.getReceiptDocument();
            receiptUpdateService.turkMileage(mileage, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error("Error in Submit Process: " + exce.getLocalizedMessage());

            result.rejectValue("errorMessage", "", exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);

            receiptDocumentForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    /**
     * Reject receipt since it can't be processed or its not a receipt
     *
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, params="receipt-reject")
    public ModelAndView reject(@ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm) {
        DateTime time = DateUtil.now();
        log.info("Beginning of Rejecting Document: " + receiptDocumentForm.getReceiptDocument().getId());
        try {
            DocumentEntity receiptOCR = receiptDocumentForm.getReceiptDocument();
            receiptUpdateService.turkReject(receiptOCR);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt reject");

            receiptDocumentForm.setErrorMessage("Receipt could not be processed for Reject. " +
                    "Contact administrator with Document # " + receiptDocumentForm.getReceiptDocument().getId());
            return new ModelAndView(NEXT_PAGE_UPDATE);
        }
    }

    /**
     * Process receipt for after recheck by technician
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping(value = "/recheck", method = RequestMethod.POST)
    public ModelAndView recheck(@ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm,
                                BindingResult result,
                                final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptDocumentForm.getReceiptDocument().getId() + " ; Title : " + receiptDocumentForm.getReceiptDocument().getBizName().getName());
        receiptDocumentValidator.validate(receiptDocumentForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }

        try {
            //TODO: Note should not happen as the condition to check for duplicate has already been satisfied when receipt was first processed.
            // Unless Technician has changed the date or some data. Date change should be exclude during re-check. Something to think about.
            if(receiptUpdateService.checkIfDuplicate(receiptDocumentForm.getReceiptEntity().getCheckSum())) {
                log.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");
                result.rejectValue("errorMessage", "", duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("result", result);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptDocumentForm.getReceiptEntity();
            List<ItemEntity> items = receiptDocumentForm.getItemEntity(receipt);
            receiptDocumentForm.updateItemWithTaxAmount(items, receipt);
            DocumentEntity receiptOCR = receiptDocumentForm.getReceiptDocument();

            receiptUpdateService.turkReceiptReCheck(receipt, items, receiptOCR);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch(Exception exce) {
            log.error("Error in Recheck process: " + exce.getLocalizedMessage());
            result.rejectValue("errorMessage", "", exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt recheck save");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    /**
     * Delete operation can only be performed by user and not technician
     *
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm) {
        //Check cannot delete a pending receipt which has been processed once, i.e. has receipt id
        //The check here is not required but its better to check before calling service method
        if(StringUtils.isEmpty(receiptDocumentForm.getReceiptDocument().getReceiptId())) {
            receiptUpdateService.deletePendingReceiptOCR(receiptDocumentForm.getReceiptDocument());
        }
        return "redirect:/pending.htm";
    }

    private void loadBasedOnAppropriateUserLevel(String receiptOCRId, UserSession userSession, ReceiptDocumentForm receiptDocumentForm) {
        DocumentEntity receipt = receiptUpdateService.loadReceiptOCRById(receiptOCRId);
        if(receipt == null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                log.info("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
            } else {
                log.warn("No such receipt exists. Request made by: " + userSession.getUserProfileId());
            }
        } else if(userSession.getUserProfileId().equalsIgnoreCase(receipt.getUserProfileId()) || (userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue())) {
            //Important: The condition below makes sure when validation fails it does not over write the item list
            if(receiptDocumentForm.getReceiptDocument() == null && receiptDocumentForm.getItems() == null) {
                receiptDocumentForm.setReceiptDocument(receipt);

                List<ItemEntityOCR> items = receiptUpdateService.loadItemsOfReceipt(receipt);
                receiptDocumentForm.setItems(items);
            }
            //helps load the image on failure
            receiptDocumentForm.getReceiptDocument().setFileSystemEntities(receipt.getFileSystemEntities());
        } else {
            log.warn("Un-authorized access by user: " + userSession.getUserProfileId() + ", accessing receipt: " + receiptOCRId);
        }
    }
}
