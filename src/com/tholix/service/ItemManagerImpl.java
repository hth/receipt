/**
 *
 */
package com.tholix.service;

import java.util.ArrayList;
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

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 9:16:44 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public class ItemManagerImpl implements ItemManager {
	private static final Logger log = Logger.getLogger(ItemManagerImpl.class);

	private static final long serialVersionUID = 5734660649481504610L;

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<ItemEntity> getAllObjects() {
		return mongoTemplate.findAll(ItemEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(ItemEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(ItemEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(ItemEntity object) {
		Query query = new Query(Criteria.where("id").is(object.getId()));
		Update update = Update.update("name", object.getName());
		return mongoTemplate.updateFirst(query, update, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteWhereReceipt(ReceiptEntity receipt) {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		mongoTemplate.remove(new Query(Criteria.where("receipt").is(receipt)), ItemEntity.class);
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<String> findItems(String name) {
        Criteria criteria = Criteria.where("name").regex(name, "i");
        Query query = new Query(criteria);

        //This makes just one of the field populated
        query.fields().include("name");
        List<ItemEntity> list = mongoTemplate.find(query, ItemEntity.class, TABLE);

        List<String> names = new ArrayList<>();
        for(ItemEntity re : list) {
            names.add(re.getName());
        }

        return names;
    }
}
