package com.tholix.web.controller;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.UserSession;
import com.tholix.service.FeedbackService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.FeedbackForm;
import com.tholix.web.validator.FeedbackValidator;

/**
 * User: hitender
 * Date: 7/19/13
 * Time: 8:19 AM
 */
@Controller
@RequestMapping(value = "/feedback")
@SessionAttributes({"userSession"})
public class FeedbackController {
    private static final Logger log = Logger.getLogger(FeedbackController.class);

    /**
     * Refers to landing.jsp
     */
    private static final String NEXT_PAGE_IS_CALLED_FEEDBACK = "/feedback/feedback";

    @Autowired FeedbackService feedbackService;
    @Autowired FeedbackValidator feedbackValidator;

    @RequestMapping(method = RequestMethod.GET, value = "/feedback")
    public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("feedbackForm") FeedbackForm feedbackForm) {
        DateTime time = DateUtil.now();
        log.info("Feedback loadForm: " + userSession.getEmailId());
        ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_FEEDBACK);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView postForm(@ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
                                 BindingResult result) {

        DateTime time = DateUtil.now();
        feedbackValidator.validate(feedbackForm, result);
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_FEEDBACK);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result check");
            return modelAndView;
        }

        feedbackService.addFeedback(feedbackForm.getComment(), feedbackForm.getRating(), feedbackForm.getFileData(), userSession);
        return new ModelAndView("/feedback/feedbackConfirm");
    }
}
