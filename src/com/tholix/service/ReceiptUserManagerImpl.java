/**
 * 
 */
package com.tholix.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.tholix.domain.ReceiptUser;

/**
 * @author hitender Dec 16, 2012 1:20:53 PM
 */
@Repository
public class ReceiptUserManagerImpl implements ReceiptUserManager {
	protected final Log logger = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 5745317401200234475L;	
	
	@Autowired
    MongoTemplate mongoTemplate;

	@Override
	public ReceiptUser findReceiptUser(String emailId) {
		logger.info("Find user : " + emailId);
		return mongoTemplate.findOne(new Query(Criteria.where("emailId").is(emailId)), ReceiptUser.class, TABLE);
	}

	@Override
	public void saveReceiptUser(ReceiptUser receiptUser) {
		logger.info("save " + receiptUser);
		mongoTemplate.save(receiptUser, TABLE);
		logger.info("saved successfully");
	}
}
