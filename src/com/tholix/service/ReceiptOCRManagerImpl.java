/**
 * 
 */
package com.tholix.service;

import com.mongodb.WriteResult;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.service.routes.ReceiptSenderJMS;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author hitender
 * @when Jan 6, 2013 1:29:44 PM
 * 
 */
public class ReceiptOCRManagerImpl implements ReceiptOCRManager {
	private static final long serialVersionUID = 8740416340416509290L;
	private static final Logger log = Logger.getLogger(ReceiptManagerImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private UserProfileManager userProfileManager;
	
	@Autowired
	private ReceiptSenderJMS senderJMS;

	@Override
	public List<ReceiptEntityOCR> getAllObjects() {
		return mongoTemplate.findAll(ReceiptEntityOCR.class, TABLE);
	}

	//TODO invoke transaction here
	@Override
	public void save(ReceiptEntityOCR object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			boolean sendToJMS = false;
			if(object.getId() == null) {
				//Once the ReceiptOCR has been saved and has a populate ID then send it across to WORKER via JMS
				sendToJMS = true;
			}
			// Cannot use insert because insert does not perform update like save.
			// Save will always try to update or create new record.
			// mongoTemplate.insert(object, TABLE);
	
			object.setUpdated();
			mongoTemplate.save(object, TABLE);
			
			if(sendToJMS) {
				log.info("ReceiptEntityOCR @Id after save: " + object.getId());
				UserProfileEntity userProfile = userProfileManager.findOne(object.getUserProfileId());
				senderJMS.send(object, userProfile);
			}
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ReceiptEntityOCR: " + e.getLocalizedMessage());
			log.error("Duplicate record entry for ReceiptEntityOCR: " + object);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public ReceiptEntityOCR findOne(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), ReceiptEntityOCR.class, TABLE);
		
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void delete(ReceiptEntityOCR object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void dropCollection() {
		throw new UnsupportedOperationException("Method not implemented");
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
