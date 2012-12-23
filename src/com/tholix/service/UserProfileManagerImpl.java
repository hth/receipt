/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.WriteResult;
import com.tholix.domain.UserProfile;

/**
 * @author hitender 
 * @when Dec 23, 2012 3:45:47 AM
 *
 */
public class UserProfileManagerImpl implements UserProfileManager {

	private static final long serialVersionUID = 7078530488197339683L;
	
	@Autowired
    MongoTemplate mongoTemplate;

	@Override
	public List<UserProfile> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(UserProfile object) {
		mongoTemplate.insert(object, TABLE);		
	}

	@Override
	public UserProfile getObject(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createCollection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropCollection() {
		// TODO Auto-generated method stub
		
	}
	
	

}
