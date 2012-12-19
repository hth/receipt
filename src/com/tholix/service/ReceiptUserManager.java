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
	public static String TABLE = "users";

	public void saveReceiptUser(ReceiptUser receiptUser);

	public ReceiptUser findReceiptUser(String emailId);

}
