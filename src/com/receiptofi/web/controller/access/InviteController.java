package com.receiptofi.web.controller.access;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.InviteService;
import com.receiptofi.service.LoginService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.utils.RandomString;
import com.receiptofi.web.form.InviteAuthenticateForm;
import com.receiptofi.web.validator.InviteAuthenticateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 1:48 PM
 */
@Controller
@RequestMapping(value = "/invite")
public class InviteController {
    private static final Logger log = LoggerFactory.getLogger(ForgotController.class);

    private static final String INVITE_AUTH         = "/invite/authenticate";
    private static final String INVITE_AUTH_CONFIRM = "/invite/authenticateConfirm";

    /** Used in JSP page /forgot/authenticateConfirm */
    private static final String SUCCESS         = "success";

    @Autowired private AccountService accountService;
    @Autowired private LoginService loginService;
    @Autowired private InviteService inviteService;
    @Autowired private InviteAuthenticateValidator inviteAuthenticateValidator;
    @Autowired private UserProfileManager userProfileManager;

    @RequestMapping(method = RequestMethod.GET, value = "authenticate")
    public ModelAndView loadForm(@RequestParam("authenticationKey") String key) {
        InviteEntity inviteEntity = inviteService.findInviteAuthenticationForKey(key);
        ModelAndView modelAndView = new ModelAndView(INVITE_AUTH);

        if(inviteEntity != null) {
            InviteAuthenticateForm inviteAuthenticateForm = InviteAuthenticateForm.newInstance();
            inviteAuthenticateForm.setEmailId(inviteEntity.getEmailId());
            inviteAuthenticateForm.setFirstName(inviteEntity.getInvited().getFirstName());
            inviteAuthenticateForm.setLastName(inviteEntity.getInvited().getLastName());
            inviteAuthenticateForm.getForgotAuthenticateForm().setAuthenticationKey(key);
            inviteAuthenticateForm.getForgotAuthenticateForm().setUserProfileId(inviteEntity.getInvited().getId());
            modelAndView.addObject("inviteAuthenticateForm", inviteAuthenticateForm);
        }

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "authenticate", params = {"confirm_invitation"})
    public ModelAndView post(@ModelAttribute("inviteAuthenticateForm") InviteAuthenticateForm inviteAuthenticateForm, BindingResult result) {
        DateTime time = DateUtil.now();
        inviteAuthenticateValidator.validate(inviteAuthenticateForm, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
            return new ModelAndView(INVITE_AUTH, "inviteAuthenticateForm", inviteAuthenticateForm);
        } else {
            InviteEntity inviteEntity = inviteService.findInviteAuthenticationForKey(inviteAuthenticateForm.getForgotAuthenticateForm().getAuthenticationKey());
            ModelAndView modelAndView = new ModelAndView(INVITE_AUTH_CONFIRM);
            if(inviteEntity != null) {
                UserProfileEntity userProfileEntity = inviteEntity.getInvited();
                userProfileEntity.setFirstName(inviteAuthenticateForm.getFirstName());
                userProfileEntity.setLastName(inviteAuthenticateForm.getLastName());
                userProfileEntity.active();

                UserAuthenticationEntity userAuthenticationEntity = UserAuthenticationEntity.newInstance(
                        HashText.computeBCrypt(inviteAuthenticateForm.getForgotAuthenticateForm().getPassword()),
                        HashText.computeBCrypt(RandomString.newInstance().nextString()));


                UserAccountEntity userAccountEntity = loginService.loadUserAccount(userProfileEntity.getReceiptUserId());

                userAuthenticationEntity.setId(userAccountEntity.getUserAuthentication().getId());
                userAuthenticationEntity.setVersion(userAccountEntity.getUserAuthentication().getVersion());
                userAuthenticationEntity.setCreated(userAccountEntity.getUserAuthentication().getCreated());
                userAuthenticationEntity.setUpdated();
                try {
                    userProfileManager.save(userProfileEntity);
                    accountService.updateAuthentication(userAuthenticationEntity);
                    inviteService.invalidateAllEntries(inviteEntity);
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
