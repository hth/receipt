package com.receiptofi.web.controller.admin;

import com.receiptofi.service.EvalFeedbackService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: hitender
 * Date: 7/8/16 8:15 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/admin")
public class ReadFeedbackController {
    private static final Logger LOG = LoggerFactory.getLogger(ReadFeedbackController.class);

    @Value ("${nextPage:/admin/feedback}")
    private String nextPage;

    private EvalFeedbackService evalFeedbackService;

    @Autowired
    public ReadFeedbackController(EvalFeedbackService evalFeedbackService) {
        this.evalFeedbackService = evalFeedbackService;
    }

    @RequestMapping (
            value = "/feedback",
            method = RequestMethod.GET
    )
    public ModelAndView loadEval() {
        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("eval", evalFeedbackService.latestFeedback());
        return modelAndView;
    }
}
