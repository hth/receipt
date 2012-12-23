/**
 * 
 */
package com.tholix.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender 
 * @when Dec 23, 2012 1:48:36 AM
 *
 */
@Document(collection="USER_PREFERENCE")
public class UserPreference extends BaseEntity {
	
	private static final long serialVersionUID = 1096957451728520230L;
	
	@DBRef
	@Indexed(unique = true)
	private ReceiptUser receiptUser;
	
	
	
}
