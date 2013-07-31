/**
 *
 */
package com.tholix.web.controller.admin;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.AdminLandingService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.LoginController;
import com.tholix.web.form.UserSearchForm;

/**
 * Redirect to prevent re-submit.
 *
 * @author hitender
 * @since Mar 26, 2013 1:14:24 AM
 */
@Controller
@RequestMapping(value = "/admin")
@SessionAttributes({"userSession"})
public class AdminLandingController {
	private static final Logger log = Logger.getLogger(AdminLandingController.class);
	private static final String nextPage = "/admin/landing";

    @Autowired private AdminLandingService adminLandingService;

	@RequestMapping(value = "/landing", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("userSearchForm") UserSearchForm userSearchForm) {

        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(nextPage);
            modelAndView.addObject("userSearchForm", userSearchForm);
            return modelAndView;
        }

        //Re-direct user to his home page because user tried accessing Un-Authorized page
        log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
        return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
	}

    /**
     * Note: UserSession parameter is to make sure no outside get requests are made.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param name Search for user name
     * @param userSession
     * @param httpServletResponse
     * @return
     */
	@RequestMapping(value = "/find_user", method = RequestMethod.GET)
	public @ResponseBody
    List<String> findUser(@RequestParam("term") String name, @ModelAttribute("userSession") UserSession userSession,
                          HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel() == UserLevelEnum.ADMIN) {
		        return adminLandingService.findMatchingUsers(name);
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
	}

    /**
     *
     * @param userSearchForm
     * @param userSession - Required when user try to refresh page after log out
     * @return
     */
	@RequestMapping(value = "/landing", method = RequestMethod.POST)
	public String loadUser(@ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("userLoginForm") UserSearchForm userSearchForm,
                                 final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        List<UserSearchForm> userSearchForms = adminLandingService.findAllUsers(userSearchForm.getUserName());

        redirectAttrs.addFlashAttribute("users", userSearchForms);
        redirectAttrs.addFlashAttribute("userSearchForm", userSearchForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());

        //Re-direct to prevent resubmit
        return "redirect:" + nextPage + ".htm";
	}
}
