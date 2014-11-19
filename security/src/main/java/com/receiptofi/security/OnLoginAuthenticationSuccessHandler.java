package com.receiptofi.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.receiptofi.domain.types.RoleEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

/**
 * User: hitender
 * Date: 5/28/14 12:42 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
public class OnLoginAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OnLoginAuthenticationSuccessHandler.class);

    private static final String ACCESS_LANDING_HTM = "/access/landing.htm";
    private static final String EMP_LANDING_HTM = "/emp/landing.htm";
    private static final String ADMIN_LANDING_HTM = "/admin/landing.htm";
    private static final String DISPLAY_LANDING_HTM = "/display/landing.htm";

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
         * http://localhost:8080/receipt/j_spring_security_check
         */
        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
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
    protected String determineTargetUrl(Authentication authentication) {
        String targetURL;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        GrantedAuthority grantedAuthority = authorities.iterator().next();
        switch (RoleEnum.valueOf(grantedAuthority.getAuthority())) {
            case ROLE_USER:
                targetURL = ACCESS_LANDING_HTM;
                break;
            case ROLE_SUPERVISOR:
                targetURL = EMP_LANDING_HTM;
                break;
            case ROLE_TECHNICIAN:
                targetURL = EMP_LANDING_HTM;
                break;
            case ROLE_ADMIN:
                targetURL = ADMIN_LANDING_HTM;
                break;
            case ROLE_ANALYSIS_READ:
                targetURL = DISPLAY_LANDING_HTM;
                break;
            default:
                LOG.error("Role set is not defined");
                throw new IllegalStateException("Role set is not defined");
        }

        return targetURL;
    }
}
