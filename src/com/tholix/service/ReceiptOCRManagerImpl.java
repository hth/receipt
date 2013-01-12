/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.WriteResult;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * @author hitender
 * @when Jan 6, 2013 1:29:44 PM
 * 
 */
public class ReceiptOCRManagerImpl implements ReceiptOCRManager {
	private static final long serialVersionUID = 8740416340416509290L;
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ReceiptEntityOCR> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(ReceiptEntityOCR object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
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
	public ReceiptEntityOCR getObject(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), ReceiptEntityOCR.class, TABLE);
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
	public long numberOfPendingReceipts(String userProfileId) {
		return mongoTemplate.count(new Query(Criteria.where("userProfileId").is(userProfileId)
				.andOperator(Criteria.where("receiptStatus").is(ReceiptStatusEnum.OCR_PROCESSED.name()))), TABLE);
	}
	
	@Override
	public List<ReceiptEntityOCR> getAllObjects(String userProfileId) {
		Sort sort = new Sort(Direction.DESC, "receiptDate").and(new Sort(Direction.DESC, "created"));
		return mongoTemplate.find(new Query(Criteria.where("userProfileId").is(userProfileId)
				.andOperator(Criteria.where("receiptStatus").is(ReceiptStatusEnum.OCR_PROCESSED.name())))
				.with(sort), ReceiptEntityOCR.class, TABLE);
	}
}
