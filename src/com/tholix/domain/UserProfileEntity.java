/**
 *
 */
package com.tholix.domain;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @since Dec 23, 2012 1:48:09 AM
 *
 */
@Document(collection = "USER_PROFILE")
public class UserProfileEntity extends BaseEntity {

	private static final long serialVersionUID = -1560672689033084436L;

	@Indexed(unique = true)
	@Email
	private String emailId;

	@Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First Name must be alphabetic with no spaces")
    private String firstName;

	@Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last Name must be alphabetic with no spaces")
    private String lastName;

	@DateTimeFormat(iso = ISO.DATE)
	private Date registration;

	/* For time zone */
	@NotNull
	private int hoursOffset;

	@DBRef
	private UserAuthenticationEntity userAuthentication;

	@NotNull
	private UserLevelEnum level = UserLevelEnum.USER;

	/** To make bean happy */
	private UserProfileEntity() {}

	private UserProfileEntity(String emailId, String firstName, String lastName, Date registration, UserAuthenticationEntity userAuthentication) {
		super();
		this.emailId = emailId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.registration = registration;
		this.userAuthentication = userAuthentication;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 *
	 * @param firstName
	 * @param lastName
	 * @param registration
	 * @param userAuthentication
	 * @return
	 */
	public static UserProfileEntity newInstance(String emailId, String firstName, String lastName, Date registration, UserAuthenticationEntity userAuthentication) {
		return new UserProfileEntity(emailId, firstName, lastName, registration, userAuthentication);
	}

	public UserAuthenticationEntity getUserAuthentication() {
		return userAuthentication;
	}

	public void setUserAuthentication(UserAuthenticationEntity userAuthentication) {
		this.userAuthentication = userAuthentication;
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

	public UserLevelEnum getLevel() {
		return level;
	}

	public void setLevel(UserLevelEnum level) {
		this.level = level;
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
