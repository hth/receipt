/**
 *
 */
package com.receiptofi.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.validation.constraints.NotNull;

/**
 * @author hitender
 * @since Dec 15, 2012 8:11:45 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_AUTHENTICATION")
@CompoundIndexes ({
        @CompoundIndex (name = "user_authentication_idx", def = "{'PA': 1, 'AU': 1}", unique = true)
})
public class UserAuthenticationEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationEntity.class);

    @NotNull
    @Field ("PA")
    private String password;

    @NotNull
    @Field ("AU")
    private String authenticationKey;

    /**
     * Required for Bean Instantiation.
     */
    @SuppressWarnings ("unused")
    private UserAuthenticationEntity() {
        super();
    }

    /**
     *
     * @param password
     * @param authenticationKey
     */
    private UserAuthenticationEntity(String password, String authenticationKey) {
        super();
        this.password = password;
        this.authenticationKey = authenticationKey;
    }

    /**
     * This method is used when the Entity is created for the first time.
     *
     * @param password
     * @param authenticationKey - (password + time stamp) to HashCode this needs to go to OAuth
     * @return
     */
    public static UserAuthenticationEntity newInstance(String password, String authenticationKey) {
        return new UserAuthenticationEntity(password, authenticationKey);
    }

    public static UserAuthenticationEntity blankInstance() {
        return new UserAuthenticationEntity();
    }

    public String getPassword() {
        return password;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    @Transient
    public String getAuthenticationKeyEncoded() {
        try {
            return URLEncoder.encode(authenticationKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("failed to encode authenticationKey reason={}", e.getLocalizedMessage(), e);
        }
        return authenticationKey;
    }

    /**
     * Note: Do not show password and authenticationKey
     * @return
     */
    @Override
    public String toString() {
        return "UserAuthenticationEntity{}";
    }
}
