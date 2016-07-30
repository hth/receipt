/**
 *
 */
package com.receiptofi.web.form;

import com.receiptofi.domain.UserProfileEntity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author hitender
 * @since Mar 26, 2013 3:52:26 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class UserSearchForm {

    private String userName = "";
    private List<UserProfileEntity> userProfiles;

    /**
     * Not sure why this logic but it forces user toe enter more than two characters to find a specific user
     *
     * @return
     */
    public String getUserName() {
        if (userName.length() <= 2 || ", ".equalsIgnoreCase(userName)) {
            return StringUtils.EMPTY;
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<UserProfileEntity> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UserProfileEntity> userProfiles) {
        this.userProfiles = userProfiles;
    }
}
