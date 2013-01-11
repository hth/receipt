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

import com.mongodb.WriteResult;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 9:17:04 PM
 * 
 */
public class ReceiptManagerImpl implements ReceiptManager {
	private final Log log = LogFactory.getLog(getClass());

	private static final long serialVersionUID = -8812261440000722447L;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ReceiptEntity> getAllObjects() {
		return mongoTemplate.findAll(ReceiptEntity.class, TABLE);
	}

	@Override
	public List<ReceiptEntity> getAllObjectsForUser(String userProfileId) {
		return mongoTemplate.find(new Query(Criteria.where("userProfileId").is(userProfileId)), ReceiptEntity.class, TABLE);
	}

	@Override
	public void saveObject(ReceiptEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			// Cannot use insert because insert does not perform update like save.
			// Save will always try to update or create new record.
			// mongoTemplate.insert(object, TABLE);

			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ReceiptEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public ReceiptEntity getObject(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), ReceiptEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String id) {
		mongoTemplate.remove(new Query(Criteria.where("id").is(id)), ReceiptEntity.class);
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
