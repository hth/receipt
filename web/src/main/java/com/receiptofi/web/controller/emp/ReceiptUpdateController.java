/**
 *
 */
package com.receiptofi.web.controller.emp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.form.ReceiptDocumentForm;
import com.receiptofi.web.util.PerformanceProfiling;
import com.receiptofi.web.validator.MileageDocumentValidator;
import com.receiptofi.web.validator.ReceiptDocumentValidator;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Class manages first processing of a receipt. That includes loading of a receipts by technician.
 * Updating of a receipt by technician. Same is true for recheck of receipt by technician.
 * This same class is used for showing the pending receipt to user
 *
 * @author hitender
 * @since Jan 7, 2013 2:13:22 AM
 */
@Controller
@RequestMapping (value = "/emp")
public final class ReceiptUpdateController {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptUpdateController.class);

    private static final String NEXT_PAGE_UPDATE = "/update";
    private static final String NEXT_PAGE_RECHECK = "/recheck";
    public static final String REDIRECT_EMP_LANDING_HTM = "redirect:/emp/landing.htm";

    @Autowired private ReceiptDocumentValidator receiptDocumentValidator;
    @Autowired private DocumentUpdateService documentUpdateService;
    @Autowired private MileageDocumentValidator mileageDocumentValidator;

    @Value ("${duplicate.receipt}")
    private String duplicateReceiptMessage;

    /**
     * For Technician: Loads new receipts.
     * For User :Method helps user to load either pending new receipt or pending recheck receipt.
     * Added logic to make sure only the user of the receipt or technician can see the receipt.
     *
     * @param documentId
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping (value = "/update/{documentId}", method = RequestMethod.GET)
    public ModelAndView update(
            @PathVariable
            String documentId,

            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            Model model,
            HttpServletRequest httpServletRequest
    ) {
        updateReceipt(documentId, receiptDocumentForm, model, httpServletRequest);
        return new ModelAndView(NEXT_PAGE_UPDATE);
    }

    private void updateReceipt(String documentId, ReceiptDocumentForm receiptDocumentForm, Model model, HttpServletRequest httpServletRequest) {
        DateTime time = DateUtil.now();

        //Gymnastic to show BindingResult errors if any or any special receipt document containing error message
        if (model.asMap().containsKey("result")) {
            //result contains validation errors
            model.addAttribute("org.springframework.validation.BindingResult.receiptDocumentForm", model.asMap().get("result"));
            receiptDocumentForm = (ReceiptDocumentForm) model.asMap().get("receiptDocumentForm");
            loadBasedOnAppropriateUserLevel(documentId, receiptDocumentForm, httpServletRequest);
        } else if (model.asMap().containsKey("receiptDocumentForm")) {
            //errorMessage here contains any other logical error found
            receiptDocumentForm = (ReceiptDocumentForm) model.asMap().get("receiptDocumentForm");
            loadBasedOnAppropriateUserLevel(documentId, receiptDocumentForm, httpServletRequest);
        } else {
            loadBasedOnAppropriateUserLevel(documentId, receiptDocumentForm, httpServletRequest);
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    /**
     * For Technician: Loads recheck receipt
     *
     * @param documentId
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping (value = "/recheck/{documentId}", method = RequestMethod.GET)
    public ModelAndView recheck(
            @PathVariable
            String documentId,

            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            Model model,
            HttpServletRequest httpServletRequest
    ) {
        updateReceipt(documentId, receiptDocumentForm, model, httpServletRequest);
        return new ModelAndView(NEXT_PAGE_RECHECK);
    }

    /**
     * Process receipt after submitted by technician
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping (value = "/submit", method = RequestMethod.POST, params = "receipt-submit")
    public ModelAndView submit(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        DateTime time = DateUtil.now();
        LOG.info("Turk processing a receipt " + receiptDocumentForm.getReceiptDocument().getId() + " ; Title : " + receiptDocumentForm.getReceiptDocument().getBizName().getBusinessName());
        receiptDocumentValidator.validate(receiptDocumentForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }

        try {
            if (documentUpdateService.hasReceiptWithSimilarChecksum(receiptDocumentForm.getReceiptEntity().getChecksum())) {
                LOG.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");

                receiptDocumentForm.setErrorMessage(duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptDocumentForm.getReceiptEntity();
            List<ItemEntity> items = receiptDocumentForm.getItemEntity(receipt);
            receiptDocumentForm.updateItemWithTaxAmount(items, receipt);
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();

            documentUpdateService.turkProcessReceipt(receipt, items, document);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception exce) {
            LOG.error("Error in Submit Process, reason={}", exce.getLocalizedMessage(), exce);

            receiptDocumentForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

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
    @RequestMapping (value = "/submitMileage", method = RequestMethod.POST, params = "mileage-submit")
    public ModelAndView submitMileage(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        DateTime time = DateUtil.now();
        switch (receiptDocumentForm.getReceiptDocument().getDocumentOfType()) {
            case MILEAGE:
                LOG.info("Mileage : ");
                break;
            default:
                LOG.error("Reached unreachable condition, DocumentOfType={}", receiptDocumentForm.getReceiptDocument().getDocumentOfType());
                throw new RuntimeException("Reached unreachable condition " + receiptDocumentForm.getReceiptDocument().getDocumentOfType());
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
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();
            documentUpdateService.turkMileage(mileage, document);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception exce) {
            LOG.error("Error in Submit Process, reason={}", exce.getLocalizedMessage(), exce);

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
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (value = "/submit", method = RequestMethod.POST, params = "receipt-reject")
    public ModelAndView rejectReceipt(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            RedirectAttributes redirectAttrs
    ) {
        return rejectDocument(receiptDocumentForm, redirectAttrs);
    }

    /**
     * Reject receipt since it can't be processed or its not a receipt
     *
     * @param receiptDocumentForm
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (value = "/submitMileage", method = RequestMethod.POST, params = "mileage-reject")
    public ModelAndView rejectMileage(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            RedirectAttributes redirectAttrs
    ) {
        return rejectDocument(receiptDocumentForm, redirectAttrs);
    }

    /**
     * Rejects any document
     *
     * @param receiptDocumentForm
     * @param redirectAttrs
     * @return
     */
    private ModelAndView rejectDocument(ReceiptDocumentForm receiptDocumentForm, RedirectAttributes redirectAttrs) {
        DateTime time = DateUtil.now();
        LOG.info("Beginning of Rejecting document={}", receiptDocumentForm.getReceiptDocument().getId());
        try {
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();
            documentUpdateService.turkDocumentReject(document.getId(), document.getDocumentOfType());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception e) {
            LOG.error("Error happened during rejecting document={} reason={}", receiptDocumentForm.getReceiptDocument().getId(), e.getLocalizedMessage(), e);

            String message = "Receipt could not be processed for Reject. Contact administrator with Document # ";
            receiptDocumentForm.setErrorMessage(message + receiptDocumentForm.getReceiptDocument().getId());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt reject");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    /**
     * Process receipt for after recheck by technician
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping (value = "/recheck", method = RequestMethod.POST)
    public ModelAndView recheck(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        DateTime time = DateUtil.now();
        LOG.info("Turk processing a receipt " + receiptDocumentForm.getReceiptDocument().getId() + " ; Title : " + receiptDocumentForm.getReceiptDocument().getBizName().getBusinessName());
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
            if (documentUpdateService.checkIfDuplicate(receiptDocumentForm.getReceiptEntity().getChecksum(), receiptDocumentForm.getReceiptEntity().getId())) {
                LOG.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");

                receiptDocumentForm.setErrorMessage(duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptDocumentForm.getReceiptEntity();
            List<ItemEntity> items = receiptDocumentForm.getItemEntity(receipt);
            receiptDocumentForm.updateItemWithTaxAmount(items, receipt);
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();

            documentUpdateService.turkProcessReceiptReCheck(receipt, items, document);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception exce) {
            LOG.error("Error in Recheck process: " + exce.getLocalizedMessage());

            receiptDocumentForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt recheck save");
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    private void loadBasedOnAppropriateUserLevel(String documentId, ReceiptDocumentForm receiptDocumentForm, HttpServletRequest request) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DocumentEntity document = documentUpdateService.loadActiveDocumentById(documentId);
        if (document == null || document.isDeleted()) {
            if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_TECHNICIAN") || request.isUserInRole("ROLE_SUPERVISOR")) {
                LOG.warn("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
                receiptDocumentForm.setErrorMessage("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
            } else {
                LOG.warn("No such receipt exists. Request made by: " + receiptUser.getRid());
                receiptDocumentForm.setErrorMessage("No such receipt exists");
            }
        } else if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_TECHNICIAN") || request.isUserInRole("ROLE_SUPERVISOR") || document.getReceiptUserId().equalsIgnoreCase(receiptUser.getRid())) {
            //Important: The condition below makes sure when validation fails it does not over write the item list
            if (receiptDocumentForm.getReceiptDocument() == null && receiptDocumentForm.getItems() == null) {
                receiptDocumentForm.setReceiptDocument(document);

                List<ItemEntityOCR> items = documentUpdateService.loadItemsOfReceipt(document);
                receiptDocumentForm.setItems(items);
            }
            //helps load the image on failure
            receiptDocumentForm.getReceiptDocument().setFileSystemEntities(document.getFileSystemEntities());
        } else {
            LOG.warn("Un-authorized access by user={} accessing receipt={}", receiptUser.getRid(), documentId);
        }
    }
}
