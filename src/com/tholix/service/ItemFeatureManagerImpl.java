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
		return mongoTemplate.findAll(ItemFeatureEntity.class, TABLE);
	}

	@Override
	public void saveObject(ItemFeatureEntity object) throws Exception {
		mongoTemplate.save(object, TABLE);
	}

	@Override
	public ItemFeatureEntity getObject(String id) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void deleteObject(ItemFeatureEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}

	}

}
