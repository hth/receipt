/**
 *
 */
package com.tholix.web.controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import net.sf.uadetector.ReadableUserAgent;

import com.google.common.collect.Ordering;

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.EmpLandingService;
import com.tholix.service.LoginService;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.utils.SHAHashing;
import com.tholix.web.cache.CachedUserAgentStringParser;
import com.tholix.web.form.UserLoginForm;
import com.tholix.web.validator.UserLoginValidator;

/**
 * @author hitender
 * @since Dec 16, 2012 6:12:17 PM
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController {
    private static final Logger log = Logger.getLogger(LoginController.class);
    public static final String LOGIN_PAGE = "login";

    //private UserAgentStringParser parser;
    private CachedUserAgentStringParser parser;

    @Autowired private UserLoginValidator userLoginValidator;
    @Autowired private LoginService loginService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private EmpLandingService empLandingService;

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
        //Get an UserAgentStringParser and analyze the requesting client
        //parser = UADetectorServiceFactory.getResourceModuleParser();
        parser = CachedUserAgentStringParser.getInstance();
    }

    @PreDestroy
    public void cleanUp() {
        log.info("Cleanup of login controller");
        parser = null;
    }

    static Ordering<MessageReceiptEntityOCR> descendingOrder = new Ordering<MessageReceiptEntityOCR>() {
        public int compare(MessageReceiptEntityOCR left, MessageReceiptEntityOCR right) {
            return Long.compare(left.getCreated().getTime(), right.getCreated().getTime());
        }
    };

    /**
     * Loads initial form
     *
     * @return
     */
	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Locale locale, HttpServletRequest request) {
        DateTime time = DateUtil.now();
		log.info("LoginController login: Locale Type: " + locale);

        Map<String, List<MessageReceiptEntityOCR>> map = new HashMap<>();
        List<MessageReceiptEntityOCR> list = empLandingService.findAll();
        log.info("Total list: " + list.size());
        for(MessageReceiptEntityOCR messageReceiptEntityOCR : list) {
            if(map.containsKey(messageReceiptEntityOCR.getReceiptOCRId())) {
                List<MessageReceiptEntityOCR> sub = map.get(messageReceiptEntityOCR.getReceiptOCRId());
                sub.add(messageReceiptEntityOCR);
            } else {
                List<MessageReceiptEntityOCR> o = new ArrayList<>();
                o.add(messageReceiptEntityOCR);
                map.put(messageReceiptEntityOCR.getReceiptOCRId(), o);
            }
        }

        for(String key : map.keySet()) {
            List<MessageReceiptEntityOCR> sub = map.get(key);
            if(sub.size() > 1) {
                List<MessageReceiptEntityOCR> sortedList = descendingOrder.sortedCopy(sub);
                List<MessageReceiptEntityOCR> deleteList = sortedList.subList(0, sortedList.size() -1);
                List<MessageReceiptEntityOCR> subListSave = sortedList.subList(sortedList.size() -1, sortedList.size());

                for(MessageReceiptEntityOCR messageReceiptEntityOCR : deleteList) {
                    empLandingService.delete(messageReceiptEntityOCR);
                }
                map.put(key, subListSave);
            }
        }

        log.info("Final remaining : " + map.size());

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
				userLoginForm.setPassword(SHAHashing.hashCodeSHA512(userLoginForm.getPassword()));
				UserAuthenticationEntity user = loginService.loadAuthenticationEntity(userProfile);
				if (user.getPassword().equals(userLoginForm.getPassword()) || user.getGrandPassword().equals(userLoginForm.getPassword())) {
					log.info("Email Id: " + userLoginForm.getEmailId() + " and found " + userProfile.getEmailId());

					UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId(), userProfile.getLevel());
					redirectAttrs.addFlashAttribute("userSession", userSession);

                    String path = landingHomePage(userProfile.getLevel());
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
					return path;
				} else {
					userLoginForm.setPassword("");
					log.error("Password not matching for user : " + userLoginForm.getEmailId());
					result.rejectValue("emailId", "field.emailId.notMatching");
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "failure");
					return LOGIN_PAGE;
				}
			} else {
				userLoginForm.setPassword("");
				log.error("No Email Id found in record : " + userLoginForm.getEmailId());
				result.rejectValue("emailId", "field.emailId.notFound");
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "failure");
				return LOGIN_PAGE;
			}
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
