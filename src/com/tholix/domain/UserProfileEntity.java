/**
 * 
 */
package com.tholix.domain;

import java.util.Date;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author hitender 
 * @when Dec 23, 2012 1:48:09 AM
 *
 */
@Document(collection="USER_PROFILE")
public class UserProfileEntity extends BaseEntity {
	
	private static final long serialVersionUID = -1560672689033084436L;

	@DBRef
	@Indexed(unique = true)
	private ReceiptUserEntity receiptUser;
	
	@Size(min=1, max=128) 
	private String firstName;
	
	@Size(min=1, max=128) 
	private String lastName;
	
	@DateTimeFormat(iso=ISO.DATE)
	private Date signup;	
	
	/* For time zone */
	@NotNull
	private int hoursOffset;

	@PersistenceConstructor
	private UserProfileEntity(String firstName, String lastName, Date signup, ReceiptUserEntity receiptUser) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.signup = signup;
		this.receiptUser = receiptUser;
	}
	
	public static UserProfileEntity newInstance(String firstName, String lastName, Date signup, ReceiptUserEntity receiptUser) {
		return new UserProfileEntity(firstName, lastName, signup, receiptUser);
	}

	public ReceiptUserEntity getReceiptUser() {
		return receiptUser;
	}

	public void setReceiptUser(ReceiptUserEntity receiptUser) {
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
