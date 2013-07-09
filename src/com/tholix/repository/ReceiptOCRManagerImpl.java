/**
 *
 */
package com.tholix.repository;

import java.util.List;

import static com.tholix.repository.util.AppendAdditionalFields.*;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * @author hitender
 * @since Jan 6, 2013 1:29:44 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public class ReceiptOCRManagerImpl implements ReceiptOCRManager {
	private static final long serialVersionUID = 8740416340416509290L;
	private static final Logger log = Logger.getLogger(ReceiptManagerImpl.class);

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<ReceiptEntityOCR> getAllObjects() {
		return mongoTemplate.findAll(ReceiptEntityOCR.class, TABLE);
	}

	//TODO invoke transaction here
	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(ReceiptEntityOCR object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			// Cannot use insert because insert does not perform update like save.
			// Save will always try to update or create new record.
			// mongoTemplate.insert(object, TABLE);

			object.setUpdated();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ReceiptEntityOCR: " + e.getLocalizedMessage());
			log.error("Duplicate record entry for ReceiptEntityOCR: " + object);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public ReceiptEntityOCR findOne(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), ReceiptEntityOCR.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(ReceiptEntityOCR object) {
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
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public long numberOfPendingReceipts(String userProfileId) {
        Criteria criteria1 = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Query query = Query.query(criteria1).addCriteria(isActive()).addCriteria(isNotDeleted());
		return mongoTemplate.count(query, TABLE);
	}

	@Override
	public List<ReceiptEntityOCR> getAllPending(String userProfileId) {
        Criteria criteria1 = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Query query = Query.query(criteria1).addCriteria(isActive()).addCriteria(isNotDeleted());

        Sort sort = new Sort(Direction.ASC, "CREATE");
		return mongoTemplate.find(query.with(sort), ReceiptEntityOCR.class, TABLE);
	}

    @Override
    public List<ReceiptEntityOCR> getAllRejected(String userProfileId) {
        Criteria criteria1 = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Criteria criteria2 = Criteria.where("RECEIPT_STATUS_ENUM").is(ReceiptStatusEnum.TURK_RECEIPT_REJECT);
        Query query = Query.query(criteria1).addCriteria(criteria2).addCriteria(isNotActive()).addCriteria(isDeleted());

        Sort sort = new Sort(Direction.ASC, "CREATE");
        return mongoTemplate.find(query.with(sort), ReceiptEntityOCR.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
