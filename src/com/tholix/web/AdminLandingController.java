/**
 *
 */
package com.tholix.web;

import java.util.ArrayList;
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

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.BizNameManager;
import com.tholix.service.BizStoreManager;
import com.tholix.service.UserProfileManager;
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

	@Autowired UserProfileManager userProfileManager;
    @Autowired BizNameManager bizNameManager;
    @Autowired BizStoreManager bizStoreManager;

	@RequestMapping(value = "/landing", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(nextPage);
            modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
            modelAndView.addObject("bizForm", BizForm.newInstance());

            return modelAndView;
        } else {
            //Re-direct user to his home page because user tried accessing Un-Authorized page
            log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
            return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
        }
	}

	@RequestMapping(value = "/find_user", method = RequestMethod.GET)
	public @ResponseBody List<String> findUser(@RequestParam("term") String name) {
		return findMatchingUsers(name);
	}

	@RequestMapping(value = "/landing", method = RequestMethod.POST)
	public ModelAndView loadUser(@ModelAttribute("userLoginForm") UserSearchForm userSearchForm) {
        DateTime time = DateUtil.now();
		List<UserSearchForm> userSearchForms = findAllUsers(userSearchForm.getUserName());

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
        BizNameEntity bizName = bizNameManager.findOne("name", bizForm.getBizName().getName());
        if(bizName == null) {
            try {
                bizNameManager.save(bizForm.getBizName());
                bizForm.getBizStore().setBizName(bizForm.getBizName());
                bizStoreManager.save(bizForm.getBizStore());
            } catch (Exception e) {
                //TODO add condition
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            bizForm.getBizStore().setBizName(bizName);
            try {
                bizStoreManager.save(bizForm.getBizStore());
            } catch (Exception e) {
                //TODO add condition
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
        modelAndView.addObject("bizForm", BizForm.newInstance());

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

	/**
	 * This method is called from AJAX to get the matching list of users in the system
	 *
	 * @param name
	 * @return
	 */
	private List<String> findMatchingUsers(String name) {
        DateTime time = DateUtil.now();
		List<String> users = new ArrayList<>();
		for(UserSearchForm userSearchForm : findAllUsers(name)) {
			users.add(userSearchForm.getUserName());
		}
		log.info(users);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return users;
	}

	/**
	 * This method returns well populated users with 'id' and other relevant data for showing user profile.
	 *
	 * @param name
	 * @return
	 */
	private List<UserSearchForm> findAllUsers(String name) {
        DateTime time = DateUtil.now();
		log.info("Search string for user name: " + name);
		List<UserSearchForm> userList = new ArrayList<UserSearchForm>();
		for(UserProfileEntity user : userProfileManager.searchAllByName(name)) {
			UserSearchForm userForm = UserSearchForm.newInstance(user);
			userList.add(userForm);
		}
		log.info("found users.. total size " + userList.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return userList;
	}
}
