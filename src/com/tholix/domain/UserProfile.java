/**
 * 
 */
package com.tholix.domain;

import java.util.Date;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * @author hitender 
 * @when Dec 23, 2012 1:48:09 AM
 *
 */
@Document(collection="USER_PROFILE")
//@CompoundIndexes({
//    @CompoundIndex(name = "age_idx", def = "{'lastName': 1, 'age': -1}")
//})
public class UserProfile extends BaseEntity {
	
	private static final long serialVersionUID = -1560672689033084436L;

	@DBRef
	@Indexed(unique = true)
	private ReceiptUser receiptUser;
	
	private String firstName;
	private String lastName;
	
	@DateTimeFormat(iso=ISO.DATE)
	private Date signup;	
	
	/* For time zone */
	private int hoursOffset;

	@PersistenceConstructor
	private UserProfile(String firstName, String lastName, Date signup, ReceiptUser receiptUser) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.signup = signup;
		this.receiptUser = receiptUser;
	}
	
	public static UserProfile newInstance(String firstName, String lastName, Date signup, ReceiptUser receiptUser) {
		return new UserProfile(firstName, lastName, signup, receiptUser);
	}

	public ReceiptUser getReceiptUser() {
		return receiptUser;
	}

	public void setReceiptUser(ReceiptUser receiptUser) {
		this.receiptUser = receiptUser;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getSignup() {
		return signup;
	}

	public void setSignup(Date signup) {
		this.signup = signup;
	}

	public int getHoursOffset() {
		return hoursOffset;
	}

	public void setHoursOffset(int hoursOffset) {
		this.hoursOffset = hoursOffset;
	}
}
