/**
 * 
 */
package com.tholix.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.tholix.domain.ReceiptUser;
import com.tholix.utils.MongoDB;

/**
 * @author hitender Dec 16, 2012 1:20:53 PM
 */
public class ReceiptUserManagerImpl implements ReceiptUserManager {
	protected final Log logger = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 5745317401200234475L;

	public static ReceiptUserManagerImpl newInstance() {
		return new ReceiptUserManagerImpl();
	}

	@Override
	public ReceiptUser findReceiptUser(String emailId) {
		return MongoDB.mo().findOne(new Query(Criteria.where("emailId").is(emailId)), ReceiptUser.class, TABLE);
	}

	@Override
	public void saveReceiptUser(ReceiptUser receiptUser) {
		MongoDB.mo().save(receiptUser, TABLE);
	}
}
