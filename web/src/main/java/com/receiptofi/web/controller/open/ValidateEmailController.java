package com.receiptofi.web.controller.open;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.EmailValidateService;
import com.receiptofi.service.RegistrationService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
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
    private RegistrationService registrationService;

    @Value ("${emailValidate:redirect:/open/validate/result.htm}")
    private String validateResult;

    @Value ("${emailValidatePage:validate/success}")
    private String validateSuccessPage;

    @Value ("${emailValidatePage:validate/failure}")
    private String validateFailurePage;

    @Autowired
    public ValidateEmailController(
            EmailValidateService emailValidateService,
            AccountService accountService,
            RegistrationService registrationService
    ) {
        this.emailValidateService = emailValidateService;
        this.accountService = accountService;
        this.registrationService = registrationService;
    }

    @RequestMapping (method = RequestMethod.GET)
    public String validateEmail(
            @RequestParam ("authenticationKey")
            String key,

            RedirectAttributes redirectAttrs,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        EmailValidateEntity emailValidate = emailValidateService.findByAuthenticationKey(key);
        if (null == emailValidate) {
            LOG.info("authentication failed for invalid auth={}", key);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else {
            UserAccountEntity userAccount = accountService.findByReceiptUserId(emailValidate.getReceiptUserId());
            if (userAccount.isAccountValidated()) {
                redirectAttrs.addFlashAttribute("success", "false");
                LOG.info("authentication failed for user={}", userAccount.getReceiptUserId());
            } else {
                userAccount.setAccountValidated(true);
                registrationService.isRegistrationAllowed(userAccount);
                accountService.saveUserAccount(userAccount);

                emailValidate.inActive();
                emailValidate.setUpdated();
                emailValidateService.saveEmailValidateEntity(emailValidate);
                redirectAttrs.addFlashAttribute("success", "true");
                LOG.info("authentication success for user={}", userAccount.getReceiptUserId());
            }
            return validateResult;
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/result")
    public String success(@ModelAttribute ("success") String success, HttpServletResponse httpServletResponse) throws IOException {
        if (StringUtils.isNotBlank(success)) {
            return Boolean.valueOf(success) ? validateSuccessPage : validateFailurePage;
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }
}
