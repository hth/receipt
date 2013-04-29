/**
 *
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.AdminLandingService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.BizForm;
import com.tholix.web.form.UserSearchForm;

/**
 * @author hitender
 * @when Mar 26, 2013 1:14:24 AM
 * {@link http://viralpatel.net/blogs/spring-3-mvc-autocomplete-json-tutorial/}
 */
@Controller
@RequestMapping(value = "/admin")
@SessionAttributes({"userSession"})
public class AdminLandingController {
	private static final Logger log = Logger.getLogger(AdminLandingController.class);
	private static final String nextPage = "/admin/landing";

    @Autowired private AdminLandingService adminLandingService;

	@RequestMapping(value = "/landing", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(nextPage);
            modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
            modelAndView.addObject("bizForm", BizForm.newInstance());

            return modelAndView;
        }

        //Re-direct user to his home page because user tried accessing Un-Authorized page
        log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
        return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
	}

	@RequestMapping(value = "/find_user", method = RequestMethod.GET)
	public @ResponseBody
    List<String> findUser(@RequestParam("term") String name) {
		return adminLandingService.findMatchingUsers(name);
	}

	@RequestMapping(value = "/landing", method = RequestMethod.POST)
	public ModelAndView loadUser(@ModelAttribute("userLoginForm") UserSearchForm userSearchForm) {
        DateTime time = DateUtil.now();
		List<UserSearchForm> userSearchForms = adminLandingService.findAllUsers(userSearchForm.getUserName());

        ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("users", userSearchForms);
		modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
        modelAndView.addObject("bizForm", BizForm.newInstance());

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

    @RequestMapping(value = "/addBusiness", method = RequestMethod.POST)
    public ModelAndView addBiz(@ModelAttribute("bizForm") BizForm bizForm, BindingResult result) {
        DateTime time = DateUtil.now();
        //TODO add validation logic

        ReceiptEntity receiptEntity = ReceiptEntity.newInstance();
        receiptEntity.setBizName(bizForm.getBizName());
        receiptEntity.setBizStore(bizForm.getBizStore());

        adminLandingService.saveNewBusinessAndOrStore(receiptEntity);

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
        modelAndView.addObject("bizForm", BizForm.newInstance());

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
