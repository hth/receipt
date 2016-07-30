package com.receiptofi.service;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.site.ReceiptUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

/**
 * Maintains if registration is allowed.
 * User: hitender
 * Date: 2/17/15 8:18 AM
 * Date: 6/22/14 7:30 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
@Scope (BeanDefinition.SCOPE_SINGLETON)
public class RegistrationService {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);

    private boolean registrationTurnedOn;
    private String loginController;

    @Autowired
    public RegistrationService(
            @Value ("${registration.turned.on}")
            boolean registrationTurnedOn,

            @Value ("${indexController:/open/login.htm}")
            String loginController
    ) {
        this.registrationTurnedOn = registrationTurnedOn;
        this.loginController = loginController;
    }

    public boolean validateIfRegistrationIsAllowed(ModelMap map, Authentication authentication) {
        if (!((UserDetails) authentication.getPrincipal()).isEnabled()) {
            ReceiptUser receiptUser = (ReceiptUser) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(
                    new AnonymousAuthenticationToken(
                            String.valueOf(System.currentTimeMillis()),
                            "anonymousUser",
                            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
                    )
            );
            map.addAttribute("deniedSignup", true);
            map.addAttribute("user", receiptUser.getUsername());
            map.addAttribute("pid", receiptUser.getPid());
            return true;
        }
        return false;
    }

    /**
     * To check is app is currently accepting registration.
     *
     * @param userAccount
     */
    void isRegistrationAllowed(UserAccountEntity userAccount) {
        if (!registrationTurnedOn) {
            userAccount.setRegisteredWhenRegistrationIsOff(!registrationTurnedOn);
        }
    }

    /**
     * Last line of defense when registration is turned off and user logs in through one of the social provider.
     *
     * @param user
     * @return
     */
    public boolean checkRegistrationIsTurnedOn(UserDetails user) {
        LOG.info("profile active={} user={} redirect to {}", user.isEnabled(), user.getUsername(), loginController);
        return !(user.isEnabled() || registrationTurnedOn);
    }

    public String getLoginController() {
        return loginController;
    }

    public boolean isRegistrationTurnedOn() {
        return registrationTurnedOn;
    }
}
