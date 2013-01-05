/**
 * 
 */
package com.tholix.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@Document(collection = "USER_PROFILE")
public class UserProfileEntity extends BaseEntity {

	private static final long serialVersionUID = -1560672689033084436L;
	
	@Indexed(unique = true)
	private String emailId;

	@Size(min = 1, max = 128)
	private String firstName;

	@Size(min = 1, max = 128)
	private String lastName;

	@DateTimeFormat(iso = ISO.DATE)
	private Date registration;

	/* For time zone */
	@NotNull
	private int hoursOffset;
	
	@DBRef
	private UserEntity user;

	private UserProfileEntity(String emailId, String firstName, String lastName, Date registration, UserEntity user) {
		super();
		this.emailId = emailId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.registration = registration;
		this.user = user;
	}

	/**
	 * This method is used when the Entity is created for the first time. 
	 * 
	 * @param firstName
	 * @param lastName
	 * @param registration
	 * @param user
	 * @return
	 */
	public static UserProfileEntity newInstance(String emailId, String firstName, String lastName, Date registration, UserEntity user) {
		return new UserProfileEntity(emailId, firstName, lastName, registration, user);
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}	

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public Date getRegistration() {
		return registration;
	}

	public void setRegistration(Date registration) {
		this.registration = registration;
	}

	public int getHoursOffset() {
		return hoursOffset;
	}

	public void setHoursOffset(int hoursOffset) {
		this.hoursOffset = hoursOffset;
	}

	/**
	 * Method appends first and last name
	 * 
	 * @return Name
	 */
	public String getName() {
		return this.firstName + " " + this.lastName;
	}
}
