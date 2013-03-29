/**
 * 
 */
package com.tholix.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ItemManager;
import com.tholix.service.UserProfileManager;
import com.tholix.utils.Formatter;
import com.tholix.web.form.UserLoginForm;
import com.tholix.web.form.UserSearchForm;

/**
 * @author hitender 
 * @when Mar 26, 2013 1:14:24 AM
 * {@link http://viralpatel.net/blogs/spring-3-mvc-autocomplete-json-tutorial/}
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminLandingFormController extends BaseController {
	private final Log log = LogFactory.getLog(getClass());
	private static final String nextPage = "/admin/landing";
	
	@Autowired UserProfileManager userProfileManager;
	
	@RequestMapping(value = "/landing", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, HttpSession session) {	
		isSessionSet(userSession, session); 
		ModelAndView modelAndView = new ModelAndView(nextPage, "userSearchForm", UserSearchForm.newInstance());		
		return modelAndView;
	}
	
	@RequestMapping(value = "/find_user", method = RequestMethod.GET)
	public @ResponseBody List<String> findUser(@RequestParam("term") String name) {	
		//TODO This is not working because the AJAX is messed up in Spring 3.2.2
		return findMatchingUsers(name);
	}
	
	@RequestMapping(value = "/landing", method = RequestMethod.POST)
	public ModelAndView loadUser(@ModelAttribute("userLoginForm") UserSearchForm userSearchForm, BindingResult result) {
		List<UserSearchForm> userSearchForms = findAllUsers(userSearchForm.getName());		
		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("users", userSearchForms);
		modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());		
		return modelAndView;
	}
	
	/**
	 * This method is called from AJAX to get the matching list of users in the system
	 * 
	 * @param name
	 * @return
	 */
	private List<String> findMatchingUsers(String name) {
		List<String> users = new ArrayList<String>();
		for(UserSearchForm userSearchForm : findAllUsers(name)) {
			users.add(userSearchForm.getName());
		}
		log.info(users);
		return users;
	}

	/**
	 * This method returns well populated users with 'id' and other relevant data for showing user profile.
	 * 
	 * @param name
	 * @return
	 */
	private List<UserSearchForm> findAllUsers(String name) {
		log.info("Search string for user name: " + name);	
		List<UserSearchForm> userList = new ArrayList<UserSearchForm>();
		for(UserProfileEntity user : userProfileManager.searchUser(name)) {
			UserSearchForm userForm = UserSearchForm.newInstance(user);
			userList.add(userForm);
		}
		log.info("found users.. total size " + userList.size());
		return userList;
	}
}
