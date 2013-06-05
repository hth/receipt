package com.tholix.web;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ForgotRecoverEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.service.AccountService;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.utils.RandomString;
import com.tholix.utils.SHAHashing;
import com.tholix.web.form.ForgotAuthenticateForm;
import com.tholix.web.form.ForgotRecoverForm;
import com.tholix.web.form.UserRegistrationForm;
import com.tholix.web.validator.ForgotAuthenticateValidator;
import com.tholix.web.validator.ForgotRecoverValidator;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 9:44 AM
 */
@Controller
@RequestMapping(value = "/forgot")
public class ForgotController {
    private static final Logger log = Logger.getLogger(ForgotController.class);

    private static final String FORGOT_RECOVER_ACCOUNT      = "/forgot/recover";
    private static final String FORGOT_RECOVER_CONFIRM      = "/forgot/recoverConfirm";
    private static final String FORGOT_RECOVER_AUTH         = "/forgot/authenticate";
    private static final String FORGOT_RECOVER_AUTH_CONFIRM = "/forgot/authenticateConfirm";

    private static final String SUBJECT = "How to reset your Receipt-O-Fi ID password.";

    @Autowired private AccountService accountService;
    @Autowired private MailSender mailSender;
    @Autowired private SimpleMailMessage simpleMailMessage;
    @Autowired private ForgotRecoverValidator forgotRecoverValidator;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private ForgotAuthenticateValidator forgotAuthenticateValidator;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;

    /**
     * Method just for changing the URL, hence have to use re-direct.
     * This could be an expensive call because of redirect.
     *
     * @param userRegistrationForm
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "recover")
    public ModelAndView loadForm(@ModelAttribute("userRegistrationForm") UserRegistrationForm userRegistrationForm, HttpServletResponse httpServletResponse) throws IOException {
        log.info("Recover password process initiated for user: " + userRegistrationForm.getEmailId());
        if(StringUtils.isEmpty(userRegistrationForm.getEmailId())) {
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot access recover directly");
            return null;
        }

        ForgotRecoverForm forgotRecoverForm = ForgotRecoverForm.newInstance();
        forgotRecoverForm.setEmailId(userRegistrationForm.getEmailId());
        forgotRecoverForm.setCaptcha(userRegistrationForm.getEmailId());

        return new ModelAndView(FORGOT_RECOVER_ACCOUNT, "forgotRecoverForm", forgotRecoverForm);
    }

    /**
     * Recover the account and sends email to users account
     *
     * @param forgotRecoverForm
     * @param result
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "recover", params = {"recover_account"})
    public ModelAndView post(@ModelAttribute("accountRecoverForm") ForgotRecoverForm forgotRecoverForm, HttpServletResponse httpServletResponse, BindingResult result) throws IOException {
        DateTime time = DateUtil.now();
        if(StringUtils.isEmpty(forgotRecoverForm.getEmailId())) {
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden to access this page directly");
            return null;
        }

        forgotRecoverValidator.validate(forgotRecoverForm, result);
        if(result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "validation error");

            ModelAndView modelAndView = new ModelAndView(FORGOT_RECOVER_ACCOUNT);
            modelAndView.addObject("forgotRecoverForm", forgotRecoverForm);

            return modelAndView;
        }

        UserProfileEntity userProfileEntity =  accountService.findIfUserExists(forgotRecoverForm.getEmailId());
        if(userProfileEntity != null) {
            try {
                Configuration cfg = freemarkerConfiguration.createConfiguration();
                Template template = cfg.getTemplate("text-account-recover.ftl");
                final String text = processPasswordRest(template, userProfileEntity);

                try {
                    //TODO change this to real user id instead
                    simpleMailMessage.setTo("admin@tholix.com");
                    simpleMailMessage.setSubject(SUBJECT);
                    simpleMailMessage.setText(text);

                    mailSender.send(simpleMailMessage);
                } catch(MailException exception) {
                    log.error("Eat exception during sending and formulating email: " + exception.getLocalizedMessage());
                }
            } catch (IOException | TemplateException exception) {
                log.error("Eat exception during sending and formulating email: " + exception.getLocalizedMessage());
            }
        }

        return new ModelAndView(FORGOT_RECOVER_CONFIRM);
    }

    @RequestMapping(method = RequestMethod.GET, value = "authenticate")
    public ModelAndView loadForm(@RequestParam("authenticationKey") String key) {
        ForgotRecoverEntity forgotRecoverEntity = accountService.findAccountAuthenticationForKey(key);
        ModelAndView modelAndView = new ModelAndView(FORGOT_RECOVER_AUTH);

        if(forgotRecoverEntity != null) {
            ForgotAuthenticateForm forgotAuthenticateForm = ForgotAuthenticateForm.newInstance();
            forgotAuthenticateForm.setAuthenticationKey(key);
            forgotAuthenticateForm.setUserProfileId(forgotRecoverEntity.getUserProfileId());
            modelAndView.addObject("forgotAuthenticateForm", forgotAuthenticateForm);
        }

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "authenticate", params = {"update_password"})
    public ModelAndView post(@ModelAttribute("forgotAuthenticateForm") ForgotAuthenticateForm forgotAuthenticateForm, BindingResult result) {
        DateTime time = DateUtil.now();
        forgotAuthenticateValidator.validate(forgotAuthenticateForm, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
            return new ModelAndView(FORGOT_RECOVER_AUTH, "forgotAuthenticateForm", forgotAuthenticateForm);
        } else {
            ForgotRecoverEntity forgotRecoverEntity = accountService.findAccountAuthenticationForKey(forgotAuthenticateForm.getAuthenticationKey());
            ModelAndView modelAndView = new ModelAndView(FORGOT_RECOVER_AUTH_CONFIRM);
            if(forgotRecoverEntity != null) {
                UserProfileEntity userProfileEntity = userProfilePreferenceService.findById(forgotRecoverEntity.getUserProfileId());

                UserAuthenticationEntity userAuthenticationEntity = UserAuthenticationEntity.newInstance(
                        SHAHashing.hashCodeSHA512(forgotAuthenticateForm.getPassword()),
                        SHAHashing.hashCodeSHA1(RandomString.newInstance().nextString()));


                userAuthenticationEntity.setId(userProfileEntity.getUserAuthentication().getId());
                userAuthenticationEntity.setVersion(userProfileEntity.getUserAuthentication().getVersion());
                userAuthenticationEntity.setCreated(userProfileEntity.getUserAuthentication().getCreated());
                userAuthenticationEntity.setUpdated();
                try {
                    accountService.updateAuthentication(userAuthenticationEntity);
                    accountService.invalidateAllPreviousEntries(forgotRecoverEntity);
                    modelAndView.addObject("success", true);
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " success");
                } catch (Exception e) {
                    log.error("Error during updating of the old authentication keys: " + e.getLocalizedMessage());
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
                    modelAndView.addObject("success", false);
                }
            } else {
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
                modelAndView.addObject("success", false);
            }
            return modelAndView;
        }
    }

    private String processPasswordRest(Template template, UserProfileEntity userProfileEntity) throws IOException, TemplateException {
        ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(userProfileEntity);

        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", userProfileEntity.getName());
        rootMap.put("link", forgotRecoverEntity.getAuthenticationKey());
        return processTemplateIntoString(template, rootMap);
    }
}
