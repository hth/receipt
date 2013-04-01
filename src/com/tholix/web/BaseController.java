/**
 * 
 */
package com.tholix.web;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tholix.domain.UserSession;

/**
 * @author hitender 
 * @when Mar 28, 2013 2:00:46 PM
 *
 */
public abstract class BaseController {
	private static final Logger log = Logger.getLogger(BaseController.class);
	
	/**
	 * @param userSession
	 * @param session
	 * @return
	 */
	public UserSession isSessionSet(UserSession userSession, HttpSession session) {
		if(userSession.isEmpty()) {
			//get the UserSession from session because a reload on this page fails without having valid userSession modelAttribute 
			userSession = (UserSession) session.getAttribute("userSession");
		}
		session.setAttribute("userSession", userSession);		
		return userSession;
	}
}
