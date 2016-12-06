package com.receiptofi.web.controller.open;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.EmailValidateService;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Validate email of a user from the email sent.
 * User: hitender
 * Date: 5/17/14 9:54 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/validate")
public class ValidateEmailController {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateEmailController.class);

    private EmailValidateService emailValidateService;
    private AccountService accountService;

    @Value ("${emailValidate:redirect:/open/validate/result.htm}")
    private String validateResult;

    @Value ("${emailValidatePage:validate/success}")
    private String validateSuccessPage;

    @Value ("${emailValidatePage:validate/failure}")
    private String validateFailurePage;

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @Autowired
    public ValidateEmailController(
            EmailValidateService emailValidateService,
            AccountService accountService
    ) {
        this.emailValidateService = emailValidateService;
        this.accountService = accountService;
    }

    @RequestMapping (method = RequestMethod.GET)
    public String validateEmail(
            @RequestParam ("authenticationKey")
            ScrubbedInput key,

            RedirectAttributes redirectAttrs,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        EmailValidateEntity emailValidate = emailValidateService.findByAuthenticationKey(key.getText());
        if (null == emailValidate) {
            LOG.info("Email address authentication failed because its deleted/invalid auth={}", key);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if(!emailValidate.isActive()) {
            LOG.info("Email address authentication previously validated for auth={}", key);
            httpServletResponse.sendError(HttpServletResponse.SC_GONE);
            return null;
        }

        UserAccountEntity userAccount = accountService.findByReceiptUserId(emailValidate.getReceiptUserId());
        if (userAccount.isAccountValidated()) {
            redirectAttrs.addFlashAttribute("success", "false");
            LOG.info("email address authentication failed for user={}", userAccount.getReceiptUserId());
        } else {
            accountService.validateAccount(emailValidate, userAccount);
            redirectAttrs.addFlashAttribute("success", "true");
            redirectAttrs.addFlashAttribute(
                    "userRegisteredWhenRegistrationIsOff",
                    userAccount.isRegisteredWhenRegistrationIsOff());

            LOG.info("email address authentication success for user={}", userAccount.getReceiptUserId());
        }
        return validateResult;
    }

    @RequestMapping (method = RequestMethod.GET, value = "/result")
    public String success(
            @ModelAttribute ("success")
            ScrubbedInput success,

            @ModelAttribute ("userRegisteredWhenRegistrationIsOff")
            boolean userRegisteredWhenRegistrationIsOff,

            ModelMap modelMap,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        String nextPage = null;
        if (StringUtils.isNotBlank(success.getText())) {
            nextPage =  Boolean.valueOf(success.getText()) ? validateSuccessPage : validateFailurePage;
            if (userRegisteredWhenRegistrationIsOff && !registrationTurnedOn) {
                modelMap.addAttribute(
                        "registrationMessage",
                        "Currently we are not accepting new users. We will notify you on your registered email when " +
                                "we start accepting new users.");
            } else {
                modelMap.addAttribute(
                        "registrationMessage",
                        "Please log in with your email address and password entered during registration.");
            }
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return nextPage;
    }
}
