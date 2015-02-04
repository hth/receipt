/**
 *
 */
package com.receiptofi.web.controller.open;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.EmailValidateService;
import com.receiptofi.service.MailService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.ColorUtil;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.web.form.UserRegistrationForm;
import com.receiptofi.web.helper.AvailabilityStatus;
import com.receiptofi.web.validator.UserRegistrationValidator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hitender
 * @since Dec 24, 2012 3:13:26 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/registration")
public class AccountRegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountRegistrationController.class);

    private UserRegistrationValidator userRegistrationValidator;
    private AccountService accountService;
    private MailService mailService;
    private EmailValidateService emailValidateService;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Value ("${registrationPage:registration}")
    private String registrationPage;

    @Value ("${registrationSuccess:redirect:/open/registration/success.htm}")
    private String registrationSuccess;

    @Value ("${registrationSuccessPage:registrationsuccess}")
    private String registrationSuccessPage;

    @Value ("${recover:redirect:/open/forgot/recover.htm}")
    private String recover;

    @Value ("${ExpenseTags.Default:HOME,BUSINESS}")
    private String[] expenseTags;

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Value ("${AccountRegistrationController.passwordLength}")
    private int passwordLength;

    @Autowired
    public AccountRegistrationController(
            UserRegistrationValidator userRegistrationValidator,
            AccountService accountService,
            MailService mailService,
            EmailValidateService emailValidateService,
            UserProfilePreferenceService userProfilePreferenceService
    ) {
        this.userRegistrationValidator = userRegistrationValidator;
        this.accountService = accountService;
        this.mailService = mailService;
        this.emailValidateService = emailValidateService;
        this.userProfilePreferenceService = userProfilePreferenceService;
    }

    @ModelAttribute ("userRegistrationForm")
    public UserRegistrationForm getUserRegistrationForm() {
        return UserRegistrationForm.newInstance();
    }

    @RequestMapping (method = RequestMethod.GET)
    public String loadForm() {
        LOG.debug("New Account Registration invoked");
        return registrationPage;
    }

    @RequestMapping (method = RequestMethod.POST, params = {"signup"})
    public String signup(
            @ModelAttribute ("userRegistrationForm")
            UserRegistrationForm userRegistrationForm,

            RedirectAttributes redirectAttrs,
            BindingResult result
    ) {
        userRegistrationValidator.validate(userRegistrationForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            return registrationPage;
        }

        UserProfileEntity userProfile = accountService.doesUserExists(userRegistrationForm.getMail());
        if (userProfile != null) {
            LOG.warn("account exists");
            userRegistrationValidator.accountExists(userRegistrationForm, result);
            return registrationPage;
        }

        UserAccountEntity userAccount;
        try {
            userAccount = accountService.createNewAccount(
                    userRegistrationForm.getMail(),
                    userRegistrationForm.getFirstName(),
                    userRegistrationForm.getLastName(),
                    userRegistrationForm.getPassword(),
                    userRegistrationForm.getBirthday());
        } catch (RuntimeException exce) {
            LOG.error("failure in registering user", exce.getLocalizedMessage(), exce);
            return registrationPage;
        }

        LOG.info("Registered new user Id={}", userAccount.getReceiptUserId());
        redirectAttrs.addFlashAttribute("email", userAccount.getUserId());

        EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                userAccount.getReceiptUserId(),
                userAccount.getUserId());

        mailService.accountValidationMail(
                userAccount.getUserId(),
                userAccount.getName(),
                accountValidate.getAuthenticationKey());

        /** Add default expense tags. */
        for (String tag : expenseTags) {
            ExpenseTagEntity expenseTag = ExpenseTagEntity.newInstance(
                    tag,
                    userAccount.getReceiptUserId(),
                    ColorUtil.getRandom());

            userProfilePreferenceService.saveExpenseTag(expenseTag);
        }

        LOG.info("success");
        return registrationSuccess;
    }

    /**
     * Starts the account recovery process.
     *
     * @param email
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping (method = RequestMethod.GET, value = "/success")
    public String success(
            @ModelAttribute ("email")
            String email,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        if (StringUtils.isNotBlank(email)) {
            return registrationSuccessPage;
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    /**
     * Starts the account recovery process.
     *
     * @param userRegistrationForm
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (method = RequestMethod.POST, params = {"recover"})
    public String recover(
            @ModelAttribute ("userRegistrationForm")
            UserRegistrationForm userRegistrationForm,

            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("userRegistrationForm", userRegistrationForm);
        return recover;
    }

    /**
     * Ajax call to check if the account is available to register.
     *
     * @param body
     * @return
     * @throws IOException
     */
    @RequestMapping (
            value = "/availability",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String getAvailability(@RequestBody String body) throws IOException {
        String email = StringUtils.lowerCase(ParseJsonStringToMap.jsonStringToMap(body).get("email").getText());
        AvailabilityStatus availabilityStatus;

        UserProfileEntity userProfileEntity = accountService.doesUserExists(email);
        if (null != userProfileEntity && userProfileEntity.getEmail().equals(email)) {
            LOG.info("Email={} provided during registration exists", email);
            availabilityStatus = AvailabilityStatus.notAvailable(email);
            return String.format("{ \"valid\" : \"%s\", \"message\" : \"<b>%s</b> is already registered. %s\" }",
                    availabilityStatus.isAvailable(),
                    email,
                    StringUtils.join(availabilityStatus.getSuggestions()));
        }
        LOG.info("Email available={} for registration", email);
        availabilityStatus = AvailabilityStatus.available();
        return String.format("{ \"valid\" : \"%s\" }", availabilityStatus.isAvailable());
    }
}
