package com.receiptofi.web.controller.open;

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
import com.receiptofi.utils.RandomString;
import com.receiptofi.web.form.InviteAuthenticateForm;
import com.receiptofi.web.util.HttpRequestResponseParser;
import com.receiptofi.web.util.PerformanceProfiling;
import com.receiptofi.web.validator.InviteAuthenticateValidator;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 1:48 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Controller
@RequestMapping (value = "/open/invite")
public final class InviteController {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotController.class);

    /** Used in JSP page /invite/authenticateConfirm */
    private static final String SUCCESS = "success";

    @Value ("${authenticatePage:/invite/authenticate}")
    private String authenticatePage;

    @Value ("${authenticateResult:redirect:/open/invite/result.htm}")
    private String authenticateResult;

    @Value ("${authenticateConfirmPage:/invite/authenticateConfirm}")
    private String authenticateConfirmPage;

    @Autowired private AccountService accountService;
    @Autowired private LoginService loginService;
    @Autowired private InviteService inviteService;
    @Autowired private InviteAuthenticateValidator inviteAuthenticateValidator;
    @Autowired private UserProfileManager userProfileManager;

    @RequestMapping (method = RequestMethod.GET, value = "authenticate")
    public String loadForm(
            @RequestParam ("authenticationKey")
            String key,

            @ModelAttribute ("inviteAuthenticateForm")
            InviteAuthenticateForm inviteAuthenticateForm
    ) {
        InviteEntity invite = inviteService.findInviteAuthenticationForKey(key);
        if (invite != null) {
            inviteAuthenticateForm.setEmailId(invite.getEmail());
            inviteAuthenticateForm.setFirstName(invite.getInvited().getFirstName());
            inviteAuthenticateForm.setLastName(invite.getInvited().getLastName());
            inviteAuthenticateForm.getForgotAuthenticateForm().setAuthenticationKey(key);
            inviteAuthenticateForm.getForgotAuthenticateForm().setReceiptUserId(invite.getInvited().getReceiptUserId());
        }
        return authenticatePage;
    }

    /**
     * Completes user invitation sent through email
     *
     * @param form
     * @param redirectAttrs
     * @param result
     * @return
     */
    @RequestMapping (method = RequestMethod.POST, value = "authenticate", params = {"confirm_invitation"})
    public String completeInvitation(
            @ModelAttribute ("inviteAuthenticateForm")
            InviteAuthenticateForm form,

            RedirectAttributes redirectAttrs,
            BindingResult result
    ) {
        DateTime time = DateUtil.now();
        inviteAuthenticateValidator.validate(form, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
            return authenticatePage;
        } else {
            InviteEntity invite =
                    inviteService.findInviteAuthenticationForKey(form.getForgotAuthenticateForm().getAuthenticationKey());
            if (invite == null) {
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
                redirectAttrs.addFlashAttribute(SUCCESS, "false");
            } else {
                UserProfileEntity userProfile = invite.getInvited();
                userProfile.setFirstName(form.getFirstName());
                userProfile.setLastName(form.getLastName());
                userProfile.active();

                UserAuthenticationEntity userAuthenticationEntity = UserAuthenticationEntity.newInstance(
                        HashText.computeBCrypt(form.getForgotAuthenticateForm().getPassword()),
                        HashText.computeBCrypt(RandomString.newInstance().nextString())
                );

                UserAccountEntity userAccount = loginService.findByReceiptUserId(userProfile.getReceiptUserId());

                userAuthenticationEntity.setId(userAccount.getUserAuthentication().getId());
                userAuthenticationEntity.setVersion(userAccount.getUserAuthentication().getVersion());
                userAuthenticationEntity.setCreated(userAccount.getUserAuthentication().getCreated());
                userAuthenticationEntity.setUpdated();
                try {
                    userProfileManager.save(userProfile);
                    accountService.updateAuthentication(userAuthenticationEntity);

                    userAccount.setFirstName(userProfile.getFirstName());
                    userAccount.setLastName(userProfile.getLastName());
                    userAccount.active();
                    userAccount.setAccountValidated(true);
                    userAccount.setUserAuthentication(userAuthenticationEntity);
                    accountService.saveUserAccount(userAccount);

                    inviteService.invalidateAllEntries(invite);
                    redirectAttrs.addFlashAttribute(SUCCESS, "true");
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " success");
                } catch (Exception e) {
                    LOG.error("Error during updating of the old authentication keys={}", e.getLocalizedMessage(), e);
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
                    redirectAttrs.addFlashAttribute(SUCCESS, "false");
                }
            }
            return authenticateResult;
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/result")
    public String success(
            @ModelAttribute ("success")
            String success,

            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        //TODO(hth) strengthen the check here as this can be hacked to get a dummy confirmation page
        if (StringUtils.isNotBlank(success)) {
            return authenticateConfirmPage;
        }
        LOG.warn(
                "404 request access={} success={} header={}",
                authenticateConfirmPage,
                success,
                HttpRequestResponseParser.printHeader(httpServletRequest)
        );
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }
}
