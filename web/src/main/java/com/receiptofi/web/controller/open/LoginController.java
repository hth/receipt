package com.receiptofi.web.controller.open;

import com.receiptofi.security.OnLoginAuthenticationSuccessHandler;
import com.receiptofi.service.LoginService;
import com.receiptofi.service.RegistrationService;
import com.receiptofi.web.cache.CachedUserAgentStringParser;
import com.receiptofi.web.form.UserLoginForm;
import com.receiptofi.web.util.HttpRequestResponseParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.pieroxy.ua.detection.UserAgentDetectionResult;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author hitender
 * @since Dec 16, 2012 6:12:17 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/login")
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Value ("${loginPage:login}")
    private String loginPage;

    //private UserAgentStringParser parser;
    private final CachedUserAgentStringParser parser;

    @Autowired private LoginService loginService;
    @Autowired private RegistrationService registrationService;
    @Autowired private OnLoginAuthenticationSuccessHandler onLoginAuthenticationSuccessHandler;

    public LoginController() {
        //Get an UserAgentStringParser and analyze the requesting client
        //parser = UADetectorServiceFactory.getResourceModuleParser();
        parser = CachedUserAgentStringParser.getInstance();
    }

    // TODO(hth) add later to my answer http://stackoverflow.com/questions/3457134/how-to-display-a-formatted-datetime-in-spring-mvc-3-0

    /**
     * isEnabled() false exists when properties registration.turned.on is false and user is trying to gain access
     * or signup through one of the provider. This is last line of defense for user signing in through social provider.
     * <p>
     * During application start up a call is made to show index page. Hence this method and only this controller
     * contains support for request type HEAD.
     * <p>
     * We have added support for HEAD request in filter to prevent failing on HEAD request. As of now there is no valid
     * reason why filter contains this HEAD request as everything is secure after login and there are no bots or
     * crawlers when a valid user has logged in.
     * <p>
     *
     * @param locale
     * @param map
     * @param request
     * @return
     * @see <a href="http://axelfontaine.com/blog/http-head.html">http://axelfontaine.com/blog/http-head.html</a>
     */
    @RequestMapping (method = {RequestMethod.GET, RequestMethod.HEAD})
    public String loadForm(
            @RequestHeader ("User-Agent")
            String userAgent,

            @ModelAttribute ("userLoginForm")
            UserLoginForm userLoginForm,

            Locale locale,
            ModelMap map,
            HttpServletRequest request
    ) {
        LOG.info("Locale Type={}", locale);

        UserAgentDetectionResult res = parser.parse(userAgent);
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            Cookie cookie = cookies[0];
            String cookieId = cookie.getValue();
            String ip = HttpRequestResponseParser.getClientIpAddress(request);

            String browser = res.getBrowser().description;
            String browserVersion = res.getBrowser().version;

            String device = res.getDevice().deviceType.getLabel();
            String deviceBrand = res.getDevice().brand.getLabel();

            String operatingSystem = res.getOperatingSystem().family.getLabel();
            String operatingSystemVersion = res.getOperatingSystem().version;

            LOG.info("cookie={}, ip={}, user-agent={}", cookieId, ip, userAgent);
            loginService.saveUpdateBrowserInfo(cookieId, ip, userAgent, browser, browserVersion, device, deviceBrand, operatingSystem, operatingSystemVersion);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOG.info("Auth {}", authentication.getPrincipal().toString());
        if (authentication instanceof AnonymousAuthenticationToken) {
            return loginPage;
        }

        if (registrationService.validateIfRegistrationIsAllowed(map, authentication)) {
            return loginPage;
        }

        return "redirect:" + onLoginAuthenticationSuccessHandler.determineTargetUrl(authentication);
    }
}
