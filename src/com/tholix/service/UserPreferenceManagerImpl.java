/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.WriteResult;
import com.tholix.domain.UserPreferenceEntity;

/**
 * @author hitender
 * @when Dec 24, 2012 3:19:22 PM
 * 
 */
public class UserPreferenceManagerImpl implements UserPreferenceManager {
	private static final long serialVersionUID = -4805176857358849811L;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<UserPreferenceEntity> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(UserPreferenceEntity object) {
		mongoTemplate.save(object, TABLE);
	}

	@Override
	public UserPreferenceEntity getObject(String id) {
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
