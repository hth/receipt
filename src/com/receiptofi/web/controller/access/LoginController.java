/**
 *
 */
package com.receiptofi.web.controller.access;

import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.LoginService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.cache.CachedUserAgentStringParser;
import com.receiptofi.web.form.UserLoginForm;
import com.receiptofi.web.validator.UserLoginValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import net.sf.uadetector.ReadableUserAgent;

/**
 * @author hitender
 * @since Dec 16, 2012 6:12:17 PM
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    public static final String LOGIN_PAGE = "login";

    //private UserAgentStringParser parser;
    private final CachedUserAgentStringParser parser;

    @Autowired private UserLoginValidator userLoginValidator;
    @Autowired private LoginService loginService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

    public LoginController() {
        //Get an UserAgentStringParser and analyze the requesting client
        //parser = UADetectorServiceFactory.getResourceModuleParser();
        parser = CachedUserAgentStringParser.getInstance();
    }

	// TODO add later to my answer http://stackoverflow.com/questions/3457134/how-to-display-a-formatted-datetime-in-spring-mvc-3-0

	/**
	 * @link http://stackoverflow.com/questions/1069958/neither-bindingresult-nor-plain-target-object-for-bean-name-available-as-request
	 *
	 * @info: OR you could just replace it in Form Request method getReceiptUser model.addAttribute("receiptUser", UserAuthenticationEntity.findReceiptUser(""));
	 *
	 * @return UserAuthenticationEntity
	 */
	@ModelAttribute("userLoginForm")
	public UserLoginForm getUserLoginForm() {
		return UserLoginForm.newInstance();
	}

    @PostConstruct
    public void init() {
        log.info("Init of login controller");
    }

    @PreDestroy
    public void cleanUp() {
        log.info("Cleanup of login controller");
    }

    /**
     * Loads initial form
     *
     * @return
     */
	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Locale locale, HttpServletRequest request) {
        DateTime time = DateUtil.now();
		log.info("Locale Type={}", locale);

        ReadableUserAgent agent = parser.parse(request.getHeader("User-Agent"));
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0) {
            Cookie cookie = cookies[0];
            String cookieId = cookie.getValue();
            String ip = getClientIpAddress(request);

            log.debug(cookieId + ", " + ip + ", " + agent);
            loginService.saveUpdateBrowserInfo(cookieId, ip, agent.toString());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return LOGIN_PAGE;
	}

    /**
     * Performs login validation for the user
     *
     * @param userLoginForm
     * @param result
     * @param redirectAttrs
     * @return
     */
	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("userLoginForm") UserLoginForm userLoginForm, BindingResult result, final RedirectAttributes redirectAttrs) {
        DateTime time = DateUtil.now();
		userLoginValidator.validate(userLoginForm, result);
		if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
			return LOGIN_PAGE;
		} else {
            //Always check user login with lower letter email case
			UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(StringUtils.lowerCase(userLoginForm.getEmailId()));
			if (userProfile != null) {

				UserAuthenticationEntity user = loginService.loadUserAccount(userProfile.getReceiptUserId()).getUserAuthentication();
                boolean passwordIsValid = false;
                try {
                    passwordIsValid = HashText.checkPassword(userLoginForm.getPassword(), user.getPassword()) || HashText.checkPassword(userLoginForm.getPassword(), user.getGrandPassword());
                } catch (Exception notValidHash) {
                    log.warn("Invalid hash for user={}", userLoginForm.getEmailId(), notValidHash);
                }
                if(passwordIsValid) {
					log.info("Login email={} and found={}", userLoginForm.getEmailId(), userProfile.getEmail());

					UserSession userSession = UserSession.newInstance(userProfile.getEmail(), userProfile.getId(), userProfile.getLevel());
					redirectAttrs.addFlashAttribute("userSession", userSession);

                    String path = landingHomePage(userProfile.getLevel());
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
					return path;
				} else {
					log.warn("Password not matching for user={}", userLoginForm.getEmailId());
				}
                result.rejectValue("emailId", "field.emailId.notMatching");
			} else {
				log.warn("No Email Id found in record={}", userLoginForm.getEmailId());
				result.rejectValue("emailId", "field.emailId.notFound");
			}

            userLoginForm.setPassword("");
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "failure");
            return LOGIN_PAGE;
		}
	}

    /**
     * Get the user landing page when they log in or try to access un-authorized page
     *
     * @param level
     * @return
     */
    public static String landingHomePage(UserLevelEnum level) {
        String path = "redirect:/landing.htm";
        switch(level) {
            case ADMIN:
                path = "redirect:/admin/landing.htm";
                break;
            case USER_PAID:
                //do nothing for now
                break;
            case USER:
                //do nothing for now
                break;
            case EMPLOYER:
                //do nothing for now
                break;
            case EMPLOYER_PAID:
                //do nothing for now
                break;
            case TECHNICIAN:
                path = "redirect:/emp/landing.htm";
                break;
            case SUPERVISOR:
                //do nothing for now
                break;
        }
        return path;
    }

    /**
     * Returns clients IP address
     *
     * @param request
     * @return
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if(ip == null) {
            log.warn("IP Address found is NULL");
        }
        return ip;
    }
}