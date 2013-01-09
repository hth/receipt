/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.WriteResult;
import com.tholix.domain.ItemFeatureEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 9:21:35 PM
 * 
 */
public class ItemFeatureManagerImpl implements ItemFeatureManager {
	private static final long serialVersionUID = -2211419786590573846L;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ItemFeatureEntity> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(ItemFeatureEntity object) throws Exception {
		mongoTemplate.save(object, TABLE);
	}

	@Override
	public ItemFeatureEntity getObject(String id) {
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
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}

	}

}
