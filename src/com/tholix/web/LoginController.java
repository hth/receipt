/**
 *
 */
package com.tholix.web;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.LoginService;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.utils.SHAHashing;
import com.tholix.web.form.UserLoginForm;
import com.tholix.web.validator.UserLoginValidator;

/**
 * @author hitender
 * @when Dec 16, 2012 6:12:17 PM
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController {
    private static final Logger log = Logger.getLogger(LoginController.class);
    public static final String LOGIN_PAGE = "login";

    @Autowired private UserLoginValidator userLoginValidator;
    @Autowired private LoginService loginService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

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

    /**
     * Loads initial form
     *
     * @return
     */
	@RequestMapping(method = RequestMethod.GET)
	public String loadForm() {
        DateTime time = DateUtil.now();
		log.info("LoginController login");
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
			UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userLoginForm.getEmailId());
			if (userProfile != null) {
				userLoginForm.setPassword(SHAHashing.hashCodeSHA512(userLoginForm.getPassword()));
				UserAuthenticationEntity user = loginService.loadAuthenticationEntity(userProfile);
				if (user.getPassword().equals(userLoginForm.getPassword())) {
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
    protected static String landingHomePage(UserLevelEnum level) {
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
            case WORKER:
                path = "redirect:/emp/landing.htm";
                break;
            case SUPERVISOR:
                //do nothing for now
                break;
        }
        return path;
    }
}
