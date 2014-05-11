/**
 *
 */
package com.receiptofi.web.controller.open;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.service.AccountService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.UserRegistrationForm;
import com.receiptofi.web.helper.AvailabilityStatus;
import com.receiptofi.web.validator.UserRegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

/**
 * @author hitender
 * @since Dec 24, 2012 3:13:26 PM
 */
@Controller
@RequestMapping(value = "/open/new")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private static final String NEW_ACCOUNT = "/new";
    private static final String FORGOT_RECOVER_ACCOUNT = "/forgot/recover";

    @Autowired private UserRegistrationValidator userRegistrationValidator;
    @Autowired private AccountService accountService;

    @ModelAttribute("userRegistrationForm")
    public UserRegistrationForm getUserRegistrationForm() {
        return UserRegistrationForm.newInstance();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String loadForm() {
        log.debug("Loading New Account");
        return NEW_ACCOUNT;
    }

    @RequestMapping(method = RequestMethod.POST, params = {"signup"})
    public String post(
            @ModelAttribute("userRegistrationForm")
            UserRegistrationForm userRegistrationForm,

            BindingResult result
    ) {
        DateTime time = DateUtil.now();
        userRegistrationValidator.validate(userRegistrationForm, result);
        if(result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "validation error");
            return NEW_ACCOUNT;
        }

        UserProfileEntity userProfile = accountService.findIfUserExists(userRegistrationForm.getEmailId());
        if(userProfile != null) {
            userRegistrationValidator.accountExists(userRegistrationForm, result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "account exists");
            return NEW_ACCOUNT;
        }

        try {
            //TODO For now de-activate all registration. Currently registration is by invitation only.
            userProfile = accountService.createNewAccount(userRegistrationForm);
            log.info("Registered new Email Id: " + userProfile.getEmail());
        } catch (Exception exce) {
            log.error(exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "failure in registering user");
            return NEW_ACCOUNT;
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");

        if(userProfile.isActive()) {
            /** This code to invoke the controller */
            return "redirect:/access/login.htm";
        } else {
            //TODO For now de-activate all registration. Currently registration is by invitation only.
            return NEW_ACCOUNT;
        }
    }

    /**
     * Starts the account recovery process
     *
     * @param userRegistrationForm
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"recover"})
    public String recover(
            @ModelAttribute("userRegistrationForm")
            UserRegistrationForm userRegistrationForm,

            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("userRegistrationForm", userRegistrationForm);
        return "redirect:" + FORGOT_RECOVER_ACCOUNT + ".htm";
    }

    /**
     * Ajax call to check if the account is available to register.
     *
     * This code can be accessed from outside without any checking. Mostly will provide information about user is
     * registered in the system or not.
     *
     * TODO: Change it. This is currently a threat.
     *
     * @param emailId
     * @return
     */
    @RequestMapping(value = "/availability", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAvailability(@RequestParam String emailId) {
        DateTime time = DateUtil.now();
        log.info("Auto find if the emailId is present: " + emailId);
        AvailabilityStatus availabilityStatus;

        UserProfileEntity userProfileEntity = accountService.findIfUserExists(emailId);
        if(userProfileEntity != null && userProfileEntity.getEmail().equals(emailId)) {
            log.info("Not Available: " + emailId);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            availabilityStatus = AvailabilityStatus.notAvailable(emailId);
            return new StringBuilder()
                    .append("{ \"valid\" : \"")
                    .append(availabilityStatus.isAvailable())
                    .append("\", \"message\" : \"")
                    .append("<b>")
                    .append(emailId)
                    .append("</b>")
                    .append(" is already registered. ")
                    .append(StringUtils.join(availabilityStatus.getSuggestions()))
                    .append("\" }")
                    .toString();
        }
        log.info("Available: " + emailId);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
        availabilityStatus = AvailabilityStatus.available();
        return "{ \"valid\" : \"" + availabilityStatus.isAvailable() + "\" }";
    }
}
