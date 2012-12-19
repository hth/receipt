/**
 * 
 */
package com.tholix.service;

import java.io.Serializable;

import com.tholix.domain.ReceiptUser;

/**
 * @author hitender Dec 16, 2012 1:20:31 PM
 */
public interface ReceiptUserManager extends Serializable {

	public ReceiptUser getReceiptUser(String emailId, String password);

	public ReceiptUser findReceiptUser(String emailId);

}
