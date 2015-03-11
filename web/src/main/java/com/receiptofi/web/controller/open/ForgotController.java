package com.receiptofi.web.controller.open;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.LoginService;
import com.receiptofi.service.MailService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.ForgotAuthenticateForm;
import com.receiptofi.web.form.ForgotRecoverForm;
import com.receiptofi.web.form.UserRegistrationForm;
import com.receiptofi.web.util.HttpRequestResponseParser;
import com.receiptofi.web.validator.ForgotAuthenticateValidator;
import com.receiptofi.web.validator.ForgotRecoverValidator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 9:44 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/forgot")
public class ForgotController {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotController.class);

    /** Used in RedirectAttributes */
    private static final String SUCCESS_EMAIL = "success_email";

    /** Used in JSP page /forgot/authenticateConfirm */
    private static final String SUCCESS = "success";

    /** Called when user hits on forgot password. */
    @Value ("${password:/forgot/password2}")
    private String passwordPage;

    /** Called when user tries to register with email already existing. Then user is directed to recover instead. */
    @Value ("${recoverPage:/forgot/recover2}")
    private String recoverPage;

    @Value ("${recoverConfirmPage:/forgot/recoverConfirm}")
    private String recoverConfirmPage;

    @Value ("${recoverConfirm:redirect:/open/forgot/recoverConfirm.htm}")
    private String recoverConfirm;

    @Value ("${authenticatePage:/forgot/authenticate2}")
    private String authenticatePage;

    @Value ("${authenticationConfirmPage:/forgot/authenticateConfirm}")
    private String authenticateConfirm;

    private AccountService accountService;
    private ForgotRecoverValidator forgotRecoverValidator;
    private UserProfilePreferenceService userProfilePreferenceService;
    private ForgotAuthenticateValidator forgotAuthenticateValidator;
    private MailService mailService;
    private LoginService loginService;

    @Autowired
    public ForgotController(
            AccountService accountService,
            ForgotRecoverValidator forgotRecoverValidator,
            UserProfilePreferenceService userProfilePreferenceService,
            ForgotAuthenticateValidator forgotAuthenticateValidator,
            MailService mailService,
            LoginService loginService
    ) {
        this.accountService = accountService;
        this.forgotRecoverValidator = forgotRecoverValidator;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.forgotAuthenticateValidator = forgotAuthenticateValidator;
        this.mailService = mailService;
        this.loginService = loginService;
    }

    @RequestMapping (
            method = RequestMethod.GET,
            value = "password"
    )
    public String onPasswordLinkClicked(
            @ModelAttribute ("forgotRecoverForm")
            ForgotRecoverForm forgotRecoverForm
    ) {
        LOG.info("Password recovery page invoked");
        return passwordPage;
    }

    @RequestMapping (
            method = RequestMethod.POST,
            value = "password",
            params = {"forgot_password"}
    )
    public String emailUserForPasswordRecovery(
            @ModelAttribute ("forgotRecoverForm")
            ForgotRecoverForm forgotRecoverForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) throws IOException {
        forgotRecoverValidator.validate(forgotRecoverForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            return passwordPage;
        }

        MailTypeEnum status = mailService.mailRecoverLink(forgotRecoverForm.getMail().getText().toLowerCase());
        if (MailTypeEnum.FAILURE == status) {
            LOG.error("Failed to send recovery email for user={}", forgotRecoverForm.getMail());
        }

        redirectAttrs.addFlashAttribute(
                SUCCESS_EMAIL,
                status == MailTypeEnum.ACCOUNT_NOT_VALIDATED ? status : MailTypeEnum.SUCCESS);
        return recoverConfirm;
    }

    /**
     * Method just for changing the URL, hence have to use re-direct.
     * This could be an expensive call because of redirect.
     * Its redirected from RequestMethod.POST form.
     *
     * @param userRegistrationForm
     * @return
     * @see AccountRegistrationController#recover(UserRegistrationForm, RedirectAttributes)
     */
    @RequestMapping (method = RequestMethod.GET, value = "recover")
    public ModelAndView whenAccountAlreadyExists(
            @ModelAttribute ("userRegistrationForm")
            UserRegistrationForm userRegistrationForm,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("Recover password process initiated for user={}", userRegistrationForm.getMail());
        if (StringUtils.isEmpty(userRegistrationForm.getMail())) {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access recover directly");
            return null;
        }

        ForgotRecoverForm forgotRecoverForm = ForgotRecoverForm.newInstance();
        forgotRecoverForm.setMail(new ScrubbedInput(userRegistrationForm.getMail()));
        forgotRecoverForm.setCaptcha(userRegistrationForm.getMail());

        return new ModelAndView(recoverPage, "forgotRecoverForm", forgotRecoverForm);
    }

    /**
     * Add this gymnastic to make sure the page does not process when refreshed again or bookmarked.
     *
     * @return
     * @throws IOException
     */
    @RequestMapping (method = RequestMethod.GET, value = "recoverConfirm")
    public String showConfirmationPageForProcessingPasswordRecovery(
            @ModelAttribute (SUCCESS_EMAIL)
            String success,

            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws IOException {

        //TODO(hth) strengthen the check here as this can be hacked to get a dummy confirmation page
        if (StringUtils.isNotBlank(success)) {
            return recoverConfirmPage;
        }
        LOG.warn(
                "404 request access={} success={} header={}",
                recoverConfirmPage,
                success,
                HttpRequestResponseParser.printHeader(httpServletRequest)
        );
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }

    @RequestMapping (method = RequestMethod.GET, value = "authenticate")
    public String whenClickedOnEmailLink(
            @RequestParam ("authenticationKey")
            String key,

            ForgotAuthenticateForm forgotAuthenticateForm
    ) {
        ForgotRecoverEntity forgotRecoverEntity = accountService.findByAuthenticationKey(key);
        if (forgotRecoverEntity != null) {
            forgotAuthenticateForm.setAuthenticationKey(key);
            forgotAuthenticateForm.setReceiptUserId(forgotRecoverEntity.getReceiptUserId());
        }
        return authenticatePage;
    }

    @RequestMapping (method = RequestMethod.POST, value = "authenticate", params = {"update_password"})
    public ModelAndView updatePassword(
            @ModelAttribute ("forgotAuthenticateForm")
            ForgotAuthenticateForm forgotAuthenticateForm,

            BindingResult result
    ) {
        forgotAuthenticateValidator.validate(forgotAuthenticateForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            return new ModelAndView(authenticatePage);
        } else {
            ForgotRecoverEntity forgotRecover = accountService.findByAuthenticationKey(
                    forgotAuthenticateForm.getAuthenticationKey());

            ModelAndView modelAndView = new ModelAndView(authenticateConfirm);
            if (null == forgotRecover) {
                modelAndView.addObject(SUCCESS, false);
            } else {
                UserProfileEntity userProfileEntity = userProfilePreferenceService.findByReceiptUserId(
                        forgotRecover.getReceiptUserId());
                Assert.notNull(userProfileEntity);

                UserAuthenticationEntity userAuthenticationEntity = UserAuthenticationEntity.newInstance(
                        HashText.computeBCrypt(forgotAuthenticateForm.getPassword()),
                        HashText.computeBCrypt(RandomString.newInstance().nextString())
                );

                UserAuthenticationEntity userAuthenticationLoaded = loginService.findByReceiptUserId(
                        userProfileEntity.getReceiptUserId()
                ).getUserAuthentication();

                userAuthenticationEntity.setId(userAuthenticationLoaded.getId());
                userAuthenticationEntity.setVersion(userAuthenticationLoaded.getVersion());
                userAuthenticationEntity.setCreated(userAuthenticationLoaded.getCreated());
                userAuthenticationEntity.setUpdated();
                try {
                    accountService.updateAuthentication(userAuthenticationEntity);
                    accountService.invalidateAllEntries(forgotRecover.getReceiptUserId());
                    modelAndView.addObject(SUCCESS, true);
                } catch (Exception e) {
                    LOG.error("Error during updating of the old authentication key message={}",
                            e.getLocalizedMessage(), e);
                    modelAndView.addObject(SUCCESS, false);
                }
            }
            return modelAndView;
        }
    }
}
