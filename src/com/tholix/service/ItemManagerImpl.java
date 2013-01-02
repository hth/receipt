/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserPreferenceEntity;

/**
 * @author hitender 
 * @when Dec 26, 2012 9:16:44 PM
 *
 */
public class ItemManagerImpl implements ItemManager {
	private final Log log = LogFactory.getLog(getClass());
	
	private static final long serialVersionUID = 5734660649481504610L;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ItemEntity> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(ItemEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		} 			
	}

	@Override
	public ItemEntity getObject(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemEntity> getObjectWithRecipt(ReceiptEntity receipt) {
		return mongoTemplate.find(new Query(Criteria.where("receipt").is(receipt)), ItemEntity.class, TABLE);
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

	@Override
	public WriteResult updateObject(ItemEntity object) {
		Query query = new Query(Criteria.where("_id").is(object.getId()));
		Update update = Update.update("name", object.getName());	
		return mongoTemplate.updateFirst(query, update, TABLE);
	}
}
