/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 9:16:44 PM
 * 
 */
public class ItemManagerImpl implements ItemManager {
	private static final Logger log = Logger.getLogger(ItemManagerImpl.class);

	private static final long serialVersionUID = 5734660649481504610L;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ItemEntity> getAllObjects() {
		return mongoTemplate.findAll(ItemEntity.class, TABLE);
	}

	@Override
	public void save(ItemEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void saveObjects(List<ItemEntity> objects) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			//TODO reflection error saving the list
			//mongoTemplate.insert(objects, TABLE);
			for(ItemEntity object : objects) {
				save(object);
			}
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public ItemEntity findOne(String id) {
		Sort sort = new Sort(Direction.ASC, "sequence");
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)).with(sort), ItemEntity.class, TABLE);
	}

	@Override
	public List<ItemEntity> getWhereReceipt(ReceiptEntity receipt) {
		Sort sort = new Sort(Direction.ASC, "sequence");
		return mongoTemplate.find(new Query(Criteria.where("receipt").is(receipt)).with(sort), ItemEntity.class, TABLE);
	}
	
	@Override
	public List<ItemEntity> getAllObjectWithName(String name) {
		return mongoTemplate.find(new Query(Criteria.where("name").is(name)), ItemEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void delete(ItemEntity object) {
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

	@Override
	public WriteResult updateObject(ItemEntity object) {
		Query query = new Query(Criteria.where("id").is(object.getId()));
		Update update = Update.update("name", object.getName());
		return mongoTemplate.updateFirst(query, update, TABLE);
	}
	
	@Override
	public void deleteWhereReceipt(ReceiptEntity receipt) {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		mongoTemplate.remove(new Query(Criteria.where("receipt").is(receipt)), ItemEntity.class);
	}
}
