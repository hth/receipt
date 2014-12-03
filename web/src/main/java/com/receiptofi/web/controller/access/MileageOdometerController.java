package com.receiptofi.web.controller.access;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.MileageService;
import com.receiptofi.web.form.MileageForm;

import org.apache.commons.lang3.StringUtils;

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
 * User: hitender
 * Date: 1/13/14 8:25 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/modv")
public final class MileageOdometerController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    @Autowired private MileageService mileageService;

    @Value ("${MileageOdometerController.nextPage:/mileage}")
    private String nextPage;

    @RequestMapping (value = "/{mileageId}", method = RequestMethod.GET)
    public ModelAndView loadForm(
            @PathVariable ("mileageId")
            String mileageId,

            @ModelAttribute ("mileageForm")
            MileageForm mileageForm,

            Model model
    ) {
        LOG.info("Loading mileage id={}", mileageId);
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.mileageForm", model.asMap().get("result"));

            MileageEntity mileageEntity = mileageService.getMileage(mileageId, receiptUser.getRid());

            mileageForm = (MileageForm) model.asMap().get("mileageForm");
            mileageForm.setMileage(mileageEntity);
        } else {
            MileageEntity mileageEntity = mileageService.getMileage(mileageId, receiptUser.getRid());
            if (null == mileageEntity) {
                //TODO check all get methods that can result in display sensitive data of other users to someone else fishing
                //Possible condition of bookmark or trying to gain access to some unknown receipt
                LOG.warn("rid={}, tried submitting an invalid mileage id={}", receiptUser.getRid(), mileageId);
            } else {
                mileageForm.setMileage(mileageEntity);
            }
        }

        return new ModelAndView(nextPage);
    }

    @RequestMapping (method = RequestMethod.POST, params = "delete")
    public ModelAndView delete(
            @ModelAttribute ("mileageForm")
            MileageForm mileageForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        LOG.info("Delete mileage id={}", mileageForm.getMileage().getId());

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (!mileageService.deleteHardMileage(mileageForm.getMileage().getId(), receiptUser.getRid())) {
                LOG.error("error in deleting mileage");
                redirectAttrs.addFlashAttribute("result", result);

                mileageForm.setErrorMessage("Delete request failed to execute");
                redirectAttrs.addFlashAttribute("mileageForm", mileageForm);
                return new ModelAndView("redirect:/access/modv/" + mileageForm.getMileage().getId() + ".htm");
            }
        } catch (Exception exce) {
            LOG.error("Error occurred during mileage delete: Receipt rid={}, error reason={}",
                    mileageForm.getMileage().getId(), exce.getLocalizedMessage(), exce);
            result.rejectValue("errorMessage", StringUtils.EMPTY, "Delete request failed to execute");
            redirectAttrs.addFlashAttribute("result", result);

            //set the error message to display to user
            mileageForm.setErrorMessage("Delete request failed to execute");
            redirectAttrs.addFlashAttribute("mileageForm", mileageForm);
            return new ModelAndView("redirect:/access/modv/" + mileageForm.getMileage().getId() + ".htm");
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/access/landing.htm");
        modelAndView.addObject("showTab", "1");
        return modelAndView;
    }

    @RequestMapping (method = RequestMethod.POST, params = "split")
    public ModelAndView split(
            @ModelAttribute ("mileageForm")
            MileageForm mileageForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        LOG.debug("Split mileage id={}", mileageForm.getMileage().getId());
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            mileageService.split(mileageForm.getMileage().getId(), receiptUser.getRid());
        } catch (Exception exce) {
            LOG.error("Error occurred during splitting mileage: Mileage={}, reason={}", mileageForm.getMileage().getId(), exce.getLocalizedMessage(), exce);
            result.rejectValue("errorMessage", StringUtils.EMPTY, exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);

            //set the error message to display to user
            mileageForm.setErrorMessage(exce.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("mileageForm", mileageForm);
            return new ModelAndView("redirect:/access/modv/" + mileageForm.getMileage().getId() + ".htm");
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/access/landing.htm");
        modelAndView.addObject("showTab", "1");
        return modelAndView;
    }
}
