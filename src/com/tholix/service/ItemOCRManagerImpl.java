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

import com.mongodb.WriteResult;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntityOCR;

/**
 * @author hitender
 * @when Jan 6, 2013 1:35:47 PM
 * 
 */
public class ItemOCRManagerImpl implements ItemOCRManager {
	private static final long serialVersionUID = -6094519223354771552L;
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ItemEntityOCR> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(ItemEntityOCR object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntityOCR: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void saveObjects(List<ItemEntityOCR> objects) throws Exception {
		mongoTemplate.insert(objects, TABLE);
	}

	@Override
	public ItemEntityOCR getObject(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemEntityOCR> getObjectWithRecipt(ReceiptEntityOCR receipt) {
		return mongoTemplate.find(new Query(Criteria.where("receipt").is(receipt)), ItemEntityOCR.class, TABLE);
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

	@Override
	public WriteResult updateObject(ItemEntityOCR object) {
		Query query = new Query(Criteria.where("_id").is(object.getId()));
		Update update = Update.update("name", object.getName());
		return mongoTemplate.updateFirst(query, update, TABLE);
	}
}
