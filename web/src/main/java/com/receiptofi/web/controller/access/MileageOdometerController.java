package com.receiptofi.web.controller.access;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.MileageService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.form.MileageForm;
import com.receiptofi.web.util.PerformanceProfiling;

import org.apache.commons.lang3.StringUtils;

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
 * User: hitender
 * Date: 1/13/14 8:25 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
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
            @PathVariable
            String mileageId,

            @ModelAttribute ("mileageForm")
            MileageForm mileageForm,

            Model model
    ) {
        DateTime time = DateUtil.now();
        LOG.info("Loading MileageEntity with id: " + mileageId);

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.mileageForm", model.asMap().get("result"));

            MileageEntity mileageEntity = mileageService.getMileage(mileageId, receiptUser.getRid());

            mileageForm = (MileageForm) model.asMap().get("mileageForm");
            mileageForm.setMileage(mileageEntity);
        } else {
            MileageEntity mileageEntity = mileageService.getMileage(mileageId, receiptUser.getRid());
            if (mileageEntity == null) {
                //TODO check all get methods that can result in display sensitive data of other users to someone else fishing
                //Possible condition of bookmark or trying to gain access to some unknown receipt
                LOG.warn("rid={}, tried submitting an invalid mileage id={}", receiptUser.getRid(), mileageId);
            } else {
                mileageForm.setMileage(mileageEntity);
            }
        }

        ModelAndView modelAndView = new ModelAndView(nextPage);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    @RequestMapping (method = RequestMethod.POST, params = "delete")
    public ModelAndView delete(
            @ModelAttribute ("mileageForm")
            MileageForm mileageForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        DateTime time = DateUtil.now();
        LOG.info("Delete mileage " + mileageForm.getMileage().getId());

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (!mileageService.deleteHardMileage(mileageForm.getMileage().getId(), receiptUser.getRid())) {
                redirectAttrs.addFlashAttribute("result", result);

                mileageForm.setErrorMessage("Delete request failed to execute");
                redirectAttrs.addFlashAttribute("mileageForm", mileageForm);

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in deleting mileage");
                return new ModelAndView("redirect:/access/modv/" + mileageForm.getMileage().getId() + ".htm");
            }
        } catch (Exception exce) {
            LOG.error("Error occurred during receipt delete: Receipt Id: " + mileageForm.getMileage().getId() + ", error message: " + exce.getLocalizedMessage());
            result.rejectValue("errorMessage", StringUtils.EMPTY, "Delete request failed to execute");
            redirectAttrs.addFlashAttribute("result", result);

            //set the error message to display to user
            mileageForm.setErrorMessage("Delete request failed to execute");
            redirectAttrs.addFlashAttribute("mileageForm", mileageForm);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in deleting mileage");
            return new ModelAndView("redirect:/access/modv/" + mileageForm.getMileage().getId() + ".htm");
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/access/landing.htm");
        modelAndView.addObject("showTab", "1");
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "deleted mileage successfully");
        return modelAndView;
    }

    @RequestMapping (method = RequestMethod.POST, params = "split")
    public ModelAndView split(
            @ModelAttribute ("mileageForm")
            MileageForm mileageForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        DateTime time = DateUtil.now();
        LOG.debug("Split mileage " + mileageForm.getMileage().getId());
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

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
            return new ModelAndView("redirect:/access/modv/" + mileageForm.getMileage().getId() + ".htm");
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
        ModelAndView modelAndView = new ModelAndView("redirect:/access/landing.htm");
        modelAndView.addObject("showTab", "1");
        return modelAndView;
    }
}
