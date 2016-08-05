package com.receiptofi.security;

import com.receiptofi.domain.types.RoleEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 5/28/14 12:42 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class OnLoginAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OnLoginAuthenticationSuccessHandler.class);

    /** For users. */
    @Value ("${accessLanding:/access/landing.htm}")
    private String accessLanding;

    /** For receipt techs. */
    @Value ("${empLanding:/emp/receipt/landing.htm}")
    private String empReceiptLanding;

    /** For campaign techs. */
    @Value ("${empLanding:/emp/campaign/landing.htm}")
    private String empCampaignLanding;

    /** For supers. */
    @Value ("${empLanding:/emp/landing.htm}")
    private String empLanding;

    @Value ("${accountantLanding:/accountant/landing.htm}")
    private String accountantLanding;

    @Value ("${adminLanding:/admin/landing.htm}")
    private String adminLanding;

    @Value ("${displayLanding:/display/landing.htm}")
    private String displayLanding;

    @Value ("${businessLanding:/business/landing.htm}")
    private String businessLanding;

    @Value ("${enterpriseLanding:/enterprise/landing.htm}")
    private String enterpriseLanding;

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws ServletException, IOException {
        if (request.getHeader("cookie") != null) {
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        }

        /**
         * Refer: http://www.baeldung.com/2011/10/31/securing-a-restful-web-service-with-spring-security-3-1-part-3/
         * To execute:
         * curl -i -X POST
         * -d emailId=some@mail.com
         * -d password=realPassword
         * http://localhost:8080/receipt/login
         */
        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (null == savedRequest) {
            clearAuthenticationAttributes(request);
            return;
        }
        final String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || targetUrlParameter != null &&
                StringUtils.hasText(request.getParameter(targetUrlParameter))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    protected void handle(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
        String targetUrl = determineTargetUrl(auth);

        if (res.isCommitted()) {
            LOG.debug("Response has already been committed. Unable to redirect endpoint={}", targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(req, res, targetUrl);
    }

    /**
     * Builds the landing URL according to the user role when they log in.
     * Refer: http://www.baeldung.com/spring_redirect_after_login
     */
    public String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        switch (getHighestRoleEnum(authorities)) {
            case ROLE_USER:
                return accessLanding;
            case ROLE_SUPERVISOR:
                return empLanding;
            case ROLE_TECHNICIAN:
                return empReceiptLanding;
            case ROLE_CAMPAIGN:
                return empCampaignLanding;
            case ROLE_ACCOUNTANT:
                return accountantLanding;
            case ROLE_ADMIN:
                return adminLanding;
            case ROLE_ANALYSIS_READ:
                return displayLanding;
            case ROLE_BUSINESS:
                return businessLanding;
            case ROLE_ENTERPRISE:
                return enterpriseLanding;
            default:
                LOG.error("Role set is not defined");
                throw new IllegalStateException("Role set is not defined");
        }
    }

    /**
     * Finds the highest available role for landing page.
     *
     * @param authorities
     * @return
     */
    private RoleEnum getHighestRoleEnum(Collection<? extends GrantedAuthority> authorities) {
        RoleEnum roleEnum = null;
        for (GrantedAuthority grantedAuthority : authorities) {
            if (null == roleEnum || roleEnum.ordinal() < RoleEnum.valueOf(grantedAuthority.getAuthority()).ordinal()) {
                roleEnum = RoleEnum.valueOf(grantedAuthority.getAuthority());
            }
        }

        return roleEnum;
    }
}
