/**
 *
 */
package com.tholix.web.form;

import com.google.common.base.Objects;

import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @since Mar 26, 2013 3:52:26 PM
 *
 */
public final class UserSearchForm {

	private String id;
	private String userName = "";
	private String firstName = "";
	private String lastName = "";
	private UserLevelEnum level;
    private String emailId;

	/** To make bean happy */
	private UserSearchForm() {}

    public static UserSearchForm newInstance() {
        return new UserSearchForm();
    }

	public static UserSearchForm newInstance(UserProfileEntity userProfile) {
		UserSearchForm userSearchForm = new UserSearchForm();

        userSearchForm.setId(userProfile.getId());
        userSearchForm.setFirstName(userProfile.getFirstName());
        userSearchForm.setLastName(userProfile.getLastName());
        userSearchForm.setUserName(userProfile.getLastName() + ", " + userProfile.getFirstName());
        userSearchForm.setLevel(userProfile.getLevel());
        userSearchForm.setEmailId(userProfile.getEmailId());
        return userSearchForm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		if(userName.length() > 2 && !userName.equalsIgnoreCase(", ")) {
			return userName;
		}
		return "";
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

	public UserLevelEnum getLevel() {
		return level;
	}

	public void setLevel(UserLevelEnum level) {
		this.level = level;
	}

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("level", level)
                .add("emailId", emailId)
                .toString();
    }
}
