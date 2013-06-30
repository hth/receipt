package com.tholix.web;

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

import com.tholix.domain.InviteEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.UserProfileManager;
import com.tholix.service.AccountService;
import com.tholix.service.InviteService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.utils.RandomString;
import com.tholix.utils.SHAHashing;
import com.tholix.web.form.InviteAuthenticateForm;
import com.tholix.web.validator.InviteAuthenticateValidator;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 1:48 PM
 */
@Controller
@RequestMapping(value = "/invite")
public class InviteController {
    private static final Logger log = Logger.getLogger(ForgotController.class);

    private static final String INVITE_AUTH         = "/invite/authenticate";
    private static final String INVITE_AUTH_CONFIRM = "/invite/authenticateConfirm";

    /** Used in JSP page /forgot/authenticateConfirm */
    private static final String SUCCESS         = "success";

    @Autowired private AccountService accountService;
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
                        SHAHashing.hashCodeSHA512(inviteAuthenticateForm.getForgotAuthenticateForm().getPassword()),
                        SHAHashing.hashCodeSHA1(RandomString.newInstance().nextString()));


                userAuthenticationEntity.setId(userProfileEntity.getUserAuthentication().getId());
                userAuthenticationEntity.setVersion(userProfileEntity.getUserAuthentication().getVersion());
                userAuthenticationEntity.setCreated(userProfileEntity.getUserAuthentication().getCreated());
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
