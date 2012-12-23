/**
 * 
 */
package com.tholix.service;

import com.tholix.domain.UserProfile;;

/**
 * @author hitender 
 * @when Dec 23, 2012 3:45:26 AM
 *
 */
public interface UserProfileManager extends RepositoryManager<UserProfile> {
	public static String TABLE = "USER_PROFILE";
}
