package com.tholix.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.tholix.service.MailService;
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

    private static final String FORGOT_PASSWORD             = "/forgot/password";
    private static final String FORGOT_RECOVER_ACCOUNT      = "/forgot/recover";
    private static final String FORGOT_RECOVER_CONFIRM      = "/forgot/recoverConfirm";
    private static final String FORGOT_RECOVER_AUTH         = "/forgot/authenticate";
    private static final String FORGOT_RECOVER_AUTH_CONFIRM = "/forgot/authenticateConfirm";

    /** Used in session */
    private static final String SUCCESS_EMAIL   = "success_email";

    /** Used in JSP page /forgot/authenticateConfirm */
    private static final String SUCCESS         = "success";

    @Autowired private AccountService accountService;
    @Autowired private ForgotRecoverValidator forgotRecoverValidator;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private ForgotAuthenticateValidator forgotAuthenticateValidator;
    @Autowired private MailService mailService;

    @RequestMapping(method = RequestMethod.GET, value = "password")
    public ModelAndView password(@ModelAttribute("forgotRecoverForm") ForgotRecoverForm forgotRecoverForm) {
        log.info("Load password recovery page");
        return new ModelAndView(FORGOT_PASSWORD, "forgotRecoverForm", forgotRecoverForm);
    }

    @RequestMapping(method = RequestMethod.POST, value = "password", params = {"forgot_password"})
    public ModelAndView postPassword(@ModelAttribute("forgotRecoverForm") ForgotRecoverForm forgotRecoverForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BindingResult result) throws IOException {
        DateTime time = DateUtil.now();
        forgotRecoverValidator.validate(forgotRecoverForm, result);
        if(result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "validation error");

            ModelAndView modelAndView = new ModelAndView(FORGOT_PASSWORD);
            modelAndView.addObject("forgotRecoverForm", forgotRecoverForm);

            return modelAndView;
        }

        mailService.mailRecoverLink(forgotRecoverForm.getEmailId());

        // Check the mantra section
        // http://www.theserverside.com/news/1365146/Redirect-After-Post
        // Fix for form re-submission is by re-directing to a GET request from POST request and little bit of gymnastic
        httpServletRequest.getSession().setAttribute(SUCCESS_EMAIL, true);
        return new ModelAndView("redirect:" + FORGOT_RECOVER_CONFIRM + ".htm");
    }

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
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access recover directly");
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
    @RequestMapping(method = RequestMethod.POST, value = "recover", params = {"forgot_recover"})
    public ModelAndView post(@ModelAttribute("forgotRecoverForm") ForgotRecoverForm forgotRecoverForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BindingResult result) throws IOException {
        DateTime time = DateUtil.now();
        if(StringUtils.isEmpty(forgotRecoverForm.getEmailId())) {
            httpServletResponse.sendError(SC_FORBIDDEN, "Forbidden to access this page directly");
            return null;
        }

        forgotRecoverValidator.validate(forgotRecoverForm, result);
        if(result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "validation error");

            ModelAndView modelAndView = new ModelAndView(FORGOT_RECOVER_ACCOUNT);
            modelAndView.addObject("forgotRecoverForm", forgotRecoverForm);

            return modelAndView;
        }

        mailService.mailRecoverLink(forgotRecoverForm.getEmailId());

        // Check the mantra section
        // http://www.theserverside.com/news/1365146/Redirect-After-Post
        // Fix for form re-submission is by re-directing to a GET request from POST request and little bit of gymnastic
        httpServletRequest.getSession().setAttribute(SUCCESS_EMAIL, true);
        return new ModelAndView("redirect:" + FORGOT_RECOVER_CONFIRM + ".htm");
    }

    /**
     * Add this gymnastic to make sure the page does not process when refreshed again or bookmarked.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET, value = "recoverConfirm")
    public String recoverConfirm(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        Enumeration<String> attributes = httpServletRequest.getSession().getAttributeNames();
        while(attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();
            if(attributeName.equals(SUCCESS_EMAIL)) {
                boolean condition = (boolean) httpServletRequest.getSession().getAttribute(SUCCESS_EMAIL);
                if(condition) {
                    //important to invalidate at the end
                    httpServletRequest.getSession().invalidate();
                    return FORGOT_RECOVER_CONFIRM;
                }
            }
        }
        httpServletResponse.sendError(SC_FORBIDDEN, "Forbidden to access this page directly");
        return null;
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
                    modelAndView.addObject(SUCCESS, true);
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " success");
                } catch (Exception e) {
                    log.error("Error during updating of the old authentication keys: " + e.getLocalizedMessage());
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
                    modelAndView.addObject(SUCCESS, false);
                }
            } else {
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
                modelAndView.addObject(SUCCESS, false);
            }
            return modelAndView;
        }
    }
}
