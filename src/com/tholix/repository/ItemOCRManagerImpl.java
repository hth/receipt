/**
 *
 */
package com.tholix.repository;

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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntityOCR;

/**
 * @author hitender
 * @when Jan 6, 2013 1:35:47 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public class ItemOCRManagerImpl implements ItemOCRManager {
	private static final long serialVersionUID = -6094519223354771552L;
	private static final Logger log = Logger.getLogger(ItemOCRManagerImpl.class);

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<ItemEntityOCR> getAllObjects() {
		return mongoTemplate.findAll(ItemEntityOCR.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(ItemEntityOCR object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntityOCR: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveObjects(List<ItemEntityOCR> objects) throws Exception {
		//TODO reflection error saving the list
		//mongoTemplate.insert(objects, TABLE);
		for(ItemEntityOCR object : objects) {
			save(object);
		}
	}

	@Override
	public ItemEntityOCR findOne(String id) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public List<ItemEntityOCR> getWhereReceipt(ReceiptEntityOCR receipt) {
		Query query = Query.query(Criteria.where("receipt").is(receipt));
		Sort sort = new Sort(Direction.ASC, "sequence");
		return mongoTemplate.find(query.with(sort), ItemEntityOCR.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(ItemEntityOCR object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteWhereReceipt(ReceiptEntityOCR receipt) {
		Query query = Query.query(Criteria.where("receipt").is(receipt));
		mongoTemplate.remove(query, ItemEntityOCR.class);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void dropCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(ItemEntityOCR object) {
		Query query = Query.query(Criteria.where("id").is(object.getId()));
		Update update = Update.update("name", object.getName());
		return mongoTemplate.updateFirst(query, update, TABLE);
	}

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
