package com.receiptofi.web.controller.access;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.EvalFeedbackService;
import com.receiptofi.web.form.EvalFeedbackForm;
import com.receiptofi.web.validator.EvalFeedbackValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hitender
 * Date: 7/19/13
 * Time: 8:19 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/eval")
public class EvalFeedbackController {
    private static final Logger LOG = LoggerFactory.getLogger(EvalFeedbackController.class);

    /* Refers to feedback.jsp and next one to feedbackConfirm.jsp. */
    @Value ("${EvalFeedbackController.nextPage:/eval/feedback}")
    private String nextPage;

    @Value ("${EvalFeedbackController.nextPageConfirm:/eval/feedbackConfirm}")
    private String nextPageConfirm;

    /* For confirming which page to show. */
    private static final String SUCCESS_EVAL = "success_eval_feedback";

    @Autowired EvalFeedbackService evalFeedbackService;
    @Autowired EvalFeedbackValidator evalFeedbackValidator;

    @RequestMapping (method = RequestMethod.GET, value = "/feedback")
    public ModelAndView loadForm(
            @ModelAttribute ("evalFeedbackForm")
            EvalFeedbackForm evalFeedbackForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOG.info("Feedback loadForm: " + receiptUser.getRid());
        return new ModelAndView(nextPage);
    }

    @RequestMapping (method = RequestMethod.POST, value = "/feedback")
    public String submitFeedback(
            @ModelAttribute ("evalFeedbackForm")
            EvalFeedbackForm evalFeedbackForm,

            HttpServletRequest httpServletRequest,
            BindingResult result
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        evalFeedbackValidator.validate(evalFeedbackForm, result);
        if (result.hasErrors()) {
            LOG.warn("validation fail");
            return nextPage;
        }

        evalFeedbackService.addFeedback(
                evalFeedbackForm.getComment().getText(),
                evalFeedbackForm.getRating(),
                evalFeedbackForm.getFileData(),
                receiptUser.getRid());
        LOG.info("Feedback saved successfully");

        httpServletRequest.getSession().setAttribute(SUCCESS_EVAL, true);
        return "redirect:/access" + nextPageConfirm + ".htm";
    }

    /**
     * Add this gymnastic to make sure the page does not process when refreshed again or bookmarked.
     *
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping (method = RequestMethod.GET, value = "/feedbackConfirm")
    public String recoverConfirm(HttpServletRequest httpServletRequest) throws IOException {
        Enumeration<String> attributes = httpServletRequest.getSession().getAttributeNames();
        while (attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();
            if (attributeName.equals(SUCCESS_EVAL)) {
                boolean condition = (boolean) httpServletRequest.getSession().getAttribute(SUCCESS_EVAL);
                if (condition) {
                    httpServletRequest.getSession().setAttribute(SUCCESS_EVAL, false);
                    return nextPageConfirm;
                }
            }
        }
        return "redirect:/access" + nextPage + ".htm";
    }
}
