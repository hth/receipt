/**
 * 
 */
package com.tholix.web;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tholix.domain.UserSession;

/**
 * @author hitender 
 * @when Mar 28, 2013 2:00:46 PM
 *
 */
public abstract class BaseController {

	private final Log log = LogFactory.getLog(getClass());
	
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
