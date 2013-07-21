package com.tholix.web.controller;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

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
@RequestMapping(value = "/eval")
@SessionAttributes({"userSession"})
public class EvalFeedbackController {
    private static final Logger log = Logger.getLogger(EvalFeedbackController.class);

    /* Refers to feedback.jsp and next one to feedbackConfirm.jsp */
    private static final String NEXT_PAGE_IS_CALLED_FEEDBACK            = "/eval/feedback";
    private static final String NEXT_PAGE_IS_CALLED_FEEDBACK_CONFIRM    = "/eval/feedbackConfirm";

    /* For confirming which page to show */
    private static final String SUCCESS_EVAL = "success_eval_feedback";

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

    @RequestMapping(method = RequestMethod.POST, value = "/feedback")
    public ModelAndView postForm(@ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
                                 HttpServletRequest httpServletRequest, BindingResult result) {

        DateTime time = DateUtil.now();
        feedbackValidator.validate(feedbackForm, result);
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_FEEDBACK);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result check");
            return modelAndView;
        }

        feedbackService.addFeedback(feedbackForm.getComment(), feedbackForm.getRating(), feedbackForm.getFileData(), userSession);
        log.info("Feedback saved successfully");

        httpServletRequest.getSession().setAttribute(SUCCESS_EVAL, true);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView("redirect:" + NEXT_PAGE_IS_CALLED_FEEDBACK_CONFIRM + ".htm");
    }

    /**
     * Add this gymnastic to make sure the page does not process when refreshed again or bookmarked.
     *
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/feedbackConfirm")
    public String recoverConfirm(HttpServletRequest httpServletRequest) throws IOException {
        Enumeration<String> attributes = httpServletRequest.getSession().getAttributeNames();
        while(attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();
            if(attributeName.equals(SUCCESS_EVAL)) {
                boolean condition = (boolean) httpServletRequest.getSession().getAttribute(SUCCESS_EVAL);
                if(condition) {
                    httpServletRequest.getSession().setAttribute(SUCCESS_EVAL, false);
                    return NEXT_PAGE_IS_CALLED_FEEDBACK_CONFIRM;
                }
            }
        }
        return "redirect:" + NEXT_PAGE_IS_CALLED_FEEDBACK + ".htm";
    }
}
