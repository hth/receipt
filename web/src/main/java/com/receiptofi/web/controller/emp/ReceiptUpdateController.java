/**
 *
 */
package com.receiptofi.web.controller.emp;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.web.form.ReceiptDocumentForm;
import com.receiptofi.web.validator.MileageDocumentValidator;
import com.receiptofi.web.validator.ReceiptDocumentValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Class manages first processing of a receipt. That includes loading of a receipts by technician.
 * Updating of a receipt by technician. Same is true for recheck of receipt by technician.
 * This same class is used for showing the pending receipt to user.
 *
 * @author hitender
 * @since Jan 7, 2013 2:13:22 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
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
    @RequestMapping (
            value = "/update/{documentId}",
            method = RequestMethod.GET)
    public ModelAndView getNewDocument(
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

    private void updateReceipt(
            String documentId,
            ReceiptDocumentForm receiptDocumentForm,
            Model model,
            HttpServletRequest httpServletRequest
    ) {
         /** Gymnastic to show BindingResult errors if any or any special receipt document containing error message. */
        if (model.asMap().containsKey("result")) {
             /** result contains validation errors. */
            model.addAttribute("org.springframework.validation.BindingResult.receiptDocumentForm", model.asMap().get("result"));
            receiptDocumentForm = (ReceiptDocumentForm) model.asMap().get("receiptDocumentForm");
            loadBasedOnAppropriateUserLevel(documentId, receiptDocumentForm, httpServletRequest);
        } else if (model.asMap().containsKey("receiptDocumentForm")) {
            /** errorMessage here contains any other logical error found. */
            receiptDocumentForm = (ReceiptDocumentForm) model.asMap().get("receiptDocumentForm");
            loadBasedOnAppropriateUserLevel(documentId, receiptDocumentForm, httpServletRequest);
        } else {
            loadBasedOnAppropriateUserLevel(documentId, receiptDocumentForm, httpServletRequest);
        }
    }

    /**
     * For Technician: Loads recheck receipts.
     *
     * @param documentId
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping (
            value = "/recheck/{documentId}",
            method = RequestMethod.GET)
    public ModelAndView getDocumentForRecheck(
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
     * Process receipt after submitted by technician.
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping (
            value = "/submit",
            method = RequestMethod.POST,
            params = "receipt-submit")
    public ModelAndView submitReceipt(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Turk processing a receipt={} biz={}",
                receiptDocumentForm.getReceiptDocument().getId(),
                receiptDocumentForm.getReceiptDocument().getBizName().getBusinessName());

        receiptDocumentValidator.validate(receiptDocumentForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }

        try {
            if (documentUpdateService.hasReceiptWithSimilarChecksum(receiptDocumentForm.getReceiptEntity().getChecksum())) {
                LOG.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");
                receiptDocumentForm.setErrorMessage(duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptDocumentForm.getReceiptEntity();
            List<ItemEntity> items = receiptDocumentForm.getItemEntity(receipt);
            receiptDocumentForm.updateItemWithTaxAmount(items, receipt);
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();

            documentUpdateService.processDocumentForReceipt(receiptUser.getRid(), receipt, items, document);
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception exce) {
            LOG.error("Error in Submit Process saving receipt, reason={}", exce.getLocalizedMessage(), exce);
            receiptDocumentForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    /**
     * Process receipt after submitted by technician.
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping (
            value = "/submitMileage",
            method = RequestMethod.POST,
            params = "mileage-submit")
    public ModelAndView submitMileage(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
            LOG.error("validation error");
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }

        try {
            MileageEntity mileage = receiptDocumentForm.getMileageEntity();
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();
            documentUpdateService.processDocumentForMileage(receiptUser.getRid(), mileage, document);
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception exce) {
            LOG.error("Error in Submit Process saving receipt, reason={}", exce.getLocalizedMessage(), exce);
            receiptDocumentForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    /**
     * Reject receipt since it can't be processed or its not a receipt.
     *
     * @param receiptDocumentForm
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (
            value = "/submit",
            method = RequestMethod.POST,
            params = "receipt-reject")
    public ModelAndView submitRejectedReceipt(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return submitRejectionOfDocument(receiptUser.getRid(), receiptDocumentForm, redirectAttrs);
    }

    /**
     * Reject receipt since it can't be processed or its not a receipt.
     *
     * @param receiptDocumentForm
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (
            value = "/submitMileage",
            method = RequestMethod.POST,
            params = "mileage-reject")
    public ModelAndView submitRejectedMileage(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return submitRejectionOfDocument(receiptUser.getRid(), receiptDocumentForm, redirectAttrs);
    }

    /**
     * Rejects any document.
     *
     * @param receiptDocumentForm
     * @param redirectAttrs
     * @return
     */
    private ModelAndView submitRejectionOfDocument(
            String technicianId,
            ReceiptDocumentForm receiptDocumentForm,
            RedirectAttributes redirectAttrs
    ) {
        LOG.info("Beginning of Rejecting document={}", receiptDocumentForm.getReceiptDocument().getId());
        try {
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();
            documentUpdateService.processDocumentForReject(technicianId, document.getId(), document.getDocumentOfType());
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception e) {
            LOG.error("Error happened during rejecting document={} reason={}", receiptDocumentForm.getReceiptDocument().getId(), e.getLocalizedMessage(), e);

            String message = "Receipt could not be processed for Reject. Contact administrator with Document # ";
            receiptDocumentForm.setErrorMessage(message + receiptDocumentForm.getReceiptDocument().getId());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_UPDATE + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    /**
     * Process receipt for after recheck by technician.
     *
     * @param receiptDocumentForm
     * @param result
     * @return
     */
    @RequestMapping (
            value = "/recheck",
            method = RequestMethod.POST)
    public ModelAndView submitReceiptRecheck(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Turk processing a receipt " + receiptDocumentForm.getReceiptDocument().getId() + " ; Title : " + receiptDocumentForm.getReceiptDocument().getBizName().getBusinessName());
        receiptDocumentValidator.validate(receiptDocumentForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            redirectAttrs.addFlashAttribute("result", result);
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }

        try {
            //TODO: Note should not happen as the condition to check for duplicate has already been satisfied when receipt was first processed.
            // Unless Technician has changed the date or some data. Date change should be exclude during re-check. Something to think about.
            if (documentUpdateService.checkIfDuplicate(receiptDocumentForm.getReceiptEntity().getChecksum(), receiptDocumentForm.getReceiptEntity().getId())) {
                LOG.info("Found pre-existing receipt with similar information for the selected date. Could be rejected and marked as duplicate.");

                receiptDocumentForm.setErrorMessage(duplicateReceiptMessage);
                redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
                return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
            }

            //TODO add validate receipt entity as this can some times be invalid and add logic to recover a broken receipts by admin
            ReceiptEntity receipt = receiptDocumentForm.getReceiptEntity();
            List<ItemEntity> items = receiptDocumentForm.getItemEntity(receipt);
            receiptDocumentForm.updateItemWithTaxAmount(items, receipt);
            DocumentEntity document = receiptDocumentForm.getReceiptDocument();

            documentUpdateService.processDocumentReceiptReCheck(receiptUser.getRid(), receipt, items, document);
            return new ModelAndView(REDIRECT_EMP_LANDING_HTM);
        } catch (Exception exce) {
            LOG.error("Error in Recheck save reason={}", exce.getLocalizedMessage(), exce);

            receiptDocumentForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("receiptDocumentForm", receiptDocumentForm);
            return new ModelAndView("redirect:/emp" + NEXT_PAGE_RECHECK + "/" + receiptDocumentForm.getReceiptDocument().getId() + ".htm");
        }
    }

    private void loadBasedOnAppropriateUserLevel(String documentId, ReceiptDocumentForm receiptDocumentForm, HttpServletRequest request) {
        Assert.notNull(receiptDocumentForm, "ReceiptDocumentForm should not be null");
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DocumentEntity document = documentUpdateService.loadActiveDocumentById(documentId);
        if (null == document || document.isDeleted()) {
            if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_TECHNICIAN") || request.isUserInRole("ROLE_SUPERVISOR")) {
                LOG.warn("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
                receiptDocumentForm.setErrorMessage("Receipt could not be found. Looks like user deleted the receipt before technician could process it.");
            } else {
                LOG.warn("No such receipt exists. Request made by: " + receiptUser.getRid());
                receiptDocumentForm.setErrorMessage("No such receipt exists");
            }
        } else if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_TECHNICIAN") || request.isUserInRole("ROLE_SUPERVISOR") || document.getReceiptUserId().equalsIgnoreCase(receiptUser.getRid())) {
            //Important: The condition below makes sure when validation fails it does not over write the item list
            if (null == receiptDocumentForm.getReceiptDocument() && null == receiptDocumentForm.getItems()) {
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
