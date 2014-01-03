/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.update;

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

/**
 * @author hitender
 * @since Jan 6, 2013 1:35:47 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public final class ItemOCRManagerImpl implements ItemOCRManager {
	private static final long serialVersionUID = -6094519223354771552L;
	private static final Logger log = LoggerFactory.getLogger(ItemOCRManagerImpl.class);

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
            if(object.getId() != null) {
                object.setUpdated();
            }
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
	public List<ItemEntityOCR> getWhereReceipt(DocumentEntity receipt) {
		Query query = Query.query(Criteria.where("RECEIPT.$id").is(new ObjectId(receipt.getId())));
		Sort sort = new Sort(Direction.ASC, "SEQUENCE");
		return mongoTemplate.find(query.with(sort), ItemEntityOCR.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(ItemEntityOCR object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteWhereReceipt(DocumentEntity receipt) {
		Query query = Query.query(Criteria.where("RECEIPT.$id").is(new ObjectId(receipt.getId())));
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
		Update update = Update.update("NAME", object.getName());
		return mongoTemplate.updateFirst(query, update(update), TABLE);
	}

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
