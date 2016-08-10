package com.receiptofi.web.controller.open;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.service.InviteService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.InviteAuthenticateForm;
import com.receiptofi.web.util.HttpRequestResponseParser;
import com.receiptofi.web.validator.InviteAuthenticateValidator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/invite")
public class InviteController {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotController.class);

    /** Used in JSP page /invite/authenticateConfirm */
    private static final String SUCCESS = "success";

    @Value ("${authenticatePage:/invite/authenticate}")
    private String authenticatePage;

    @Value ("${authenticateResult:redirect:/open/invite/result.htm}")
    private String authenticateResult;

    @Value ("${authenticateConfirmPage:/invite/authenticateConfirm}")
    private String authenticateConfirmPage;

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @Value ("${businessRegistrationFlow:redirect:/open/business/registration.htm}")
    private String businessRegistrationFlow;

    private InviteService inviteService;
    private InviteAuthenticateValidator inviteAuthenticateValidator;

    @Autowired
    public InviteController(
            InviteService inviteService,
            InviteAuthenticateValidator inviteAuthenticateValidator
    ) {
        this.inviteService = inviteService;
        this.inviteAuthenticateValidator = inviteAuthenticateValidator;
    }

    @RequestMapping (method = RequestMethod.GET, value = "authenticate")
    public String loadForm(
            @RequestParam ("authenticationKey")
            ScrubbedInput key,

            @ModelAttribute ("inviteAuthenticateForm")
            InviteAuthenticateForm inviteAuthenticateForm,

            ModelMap model,
            RedirectAttributes redirectAttrs,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        InviteEntity invite = inviteService.findByAuthenticationKey(key.getText());
        if (null == invite) {
            LOG.info("Invite failed because its deleted/invalid auth={}", key);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if(!invite.isActive()) {
            LOG.info("Invite has been previously completed for auth={}", key);
            httpServletResponse.sendError(HttpServletResponse.SC_GONE);
            return null;
        }

        switch (invite.getUserLevel()) {
            case USER:
                model.addAttribute("registrationTurnedOn", registrationTurnedOn);
                inviteAuthenticateForm
                        .setMail(new ScrubbedInput(invite.getEmail()))
                        .setFirstName(new ScrubbedInput(invite.getInvited().getFirstName()))
                        .setLastName(new ScrubbedInput(invite.getInvited().getLastName()))
                        .setUserLevel(invite.getUserLevel())
                        .getForgotAuthenticateForm().setAuthenticationKey(key.getText())
                        .setReceiptUserId(invite.getInvited().getReceiptUserId());
                return authenticatePage;
            case BUSINESS:
                //TODO for business
                redirectAttrs.addFlashAttribute("authenticationKey", invite.getAuthenticationKey());
                return businessRegistrationFlow;
            case ACCOUNTANT:
                redirectAttrs.addFlashAttribute("authenticationKey", invite.getAuthenticationKey());
                return businessRegistrationFlow;
            case ENTERPRISE:
                //TODO for enterprise
                redirectAttrs.addFlashAttribute("authenticationKey", invite.getAuthenticationKey());
                return businessRegistrationFlow;
            default:
                LOG.error("Reached unsupported rid={} uid={} condition={}",
                        invite.getInvited().getReceiptUserId(), invite.getEmail(), invite.getUserLevel());
                throw new UnsupportedOperationException("Reached unsupported condition");
        }
    }

    /**
     * Completes user invitation sent through email.
     *
     * @param form
     * @param model
     * @param redirectAttrs
     * @param result
     * @return
     */
    @RequestMapping (method = RequestMethod.POST, value = "authenticate", params = {"confirm_invitation"})
    public String completeInvitation(
            @ModelAttribute ("inviteAuthenticateForm")
            InviteAuthenticateForm form,

            ModelMap model,
            RedirectAttributes redirectAttrs,
            BindingResult result
    ) {
        inviteAuthenticateValidator.validate(form, result);
        if (result.hasErrors()) {
            LOG.warn("validation fail");
            model.addAttribute("registrationTurnedOn", registrationTurnedOn);
            return authenticatePage;
        } else {
            String key = form.getForgotAuthenticateForm().getAuthenticationKey();
            InviteEntity invite = inviteService.findByAuthenticationKey(key);
            if (null == invite) {
                redirectAttrs.addFlashAttribute(SUCCESS, "false");
            } else {
                boolean signupComplete = inviteService.completeProfileForInvitationSignup(
                        form.getFirstName().getText(),
                        form.getLastName().getText(),
                        form.getBirthday().getText(),
                        "",
                        "",
                        "",
                        form.getForgotAuthenticateForm().getPassword(),
                        invite
                );
                redirectAttrs.addFlashAttribute(SUCCESS, Boolean.valueOf(signupComplete).toString());
            }
            return authenticateResult;
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/result")
    public String success(
            @ModelAttribute ("success")
            ScrubbedInput success,

            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        //TODO(hth) strengthen the check here as this can be hacked to get a dummy confirmation page
        if (StringUtils.isNotBlank(success.getText())) {
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
