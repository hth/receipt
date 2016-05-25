package com.receiptofi.domain;

import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * Stores user account info and social account mapping.
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_ACCOUNT")
@CompoundIndexes ({
        @CompoundIndex (name = "user_account_role_idx", def = "{'UID': 1, 'PID': 1, 'RE': 1}", unique = true),
        @CompoundIndex (name = "user_account_provider_user_idx", def = "{'UID': 1, 'PID': 1, 'PUID': 1}", unique = true),
        @CompoundIndex (name = "user_account_rid_idx", def = "{'RID': 1}", unique = true),
        @CompoundIndex (name = "user_account_uid_idx", def = "{'UID': 1}", unique = true),
        @CompoundIndex (name = "user_account_ac_idx", def = "{'AC': 1}")
})
public class UserAccountEntity extends BaseEntity {

    public static final String BLANK_SPACE = " ";

    /** Unique Id throughout the system. This will never change. */
    @NotNull
    @Field ("RID")
    private String receiptUserId;

    /**
     * This is set by third party and cannot be relied on.
     * It could be either matching provider's Id or email.
     */
    @NotNull
    @Field ("UID")
    private String userId;

    @Field ("PID")
    private ProviderEnum providerId;

    /** Provider User Id matches id's from social provider. */
    @Field ("PUID")
    private String providerUserId;

    @Field ("DN")
    private String displayName;

    @Field ("PURL")
    private String profileUrl;

    @Field ("IURL")
    private String imageUrl;

    @Field ("AT")
    private String accessToken;

    @Field ("AC")
    private String authorizationCode;

    @Field ("SE")
    private String secret;

    @Field ("RT")
    private String refreshToken;

    @Field ("ET")
    private Long expireTime;

    @Field ("FN")
    private String firstName;

    @Field ("LN")
    private String lastName;

    @Field ("RE")
    private Set<RoleEnum> roles;

    @DBRef
    @Field ("USER_AUTHENTICATION")
    private UserAuthenticationEntity userAuthentication;

    @Field ("AV")
    private boolean accountValidated;

    @Field ("AVD")
    private Date accountValidatedBeginDate;

    /** When RegistrationIsOff, the value is true. */
    @Field ("RIO")
    private boolean registeredWhenRegistrationIsOff;

    @Field ("AIR")
    private AccountInactiveReasonEnum accountInactiveReason;

    @DBRef
    @Field ("BILLING_ACCOUNT")
    private BillingAccountEntity billingAccount;

    private UserAccountEntity() {
        super();
        roles = new LinkedHashSet<>();
        roles.add(RoleEnum.ROLE_USER);
    }

    private UserAccountEntity(
            String receiptUserId,
            String userId,
            String firstName,
            String lastName,
            UserAuthenticationEntity userAuthentication
    ) {
        this();
        this.receiptUserId = receiptUserId;
        this.userId = userId;
        this.firstName = WordUtils.capitalize(firstName);
        this.lastName = WordUtils.capitalize(lastName);
        this.userAuthentication = userAuthentication;
        billingAccount = new BillingAccountEntity(receiptUserId);
    }

    public static UserAccountEntity newInstance(
            String receiptUserId,
            String userId,
            String firstName,
            String lastName,
            UserAuthenticationEntity userAuthentication
    ) {
        return new UserAccountEntity(receiptUserId, userId, firstName, lastName, userAuthentication);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProviderEnum getProviderId() {
        return providerId;
    }

    public void setProviderId(ProviderEnum providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = WordUtils.capitalize(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = WordUtils.capitalize(lastName);
    }

    public Set<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEnum> roles) {
        this.roles = roles;
    }

    public void addRole(RoleEnum role) {
        this.roles.add(role);
    }

    public UserAuthenticationEntity getUserAuthentication() {
        return userAuthentication;
    }

    public void setUserAuthentication(UserAuthenticationEntity userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public void setAccountValidated(boolean accountValidated) {
        if (!accountValidated && this.accountValidated) {
            /**
             * Update accountValidatedBeginDate with new date when account has been validated previously or else
             * keep the date same as this can lead to continuous increase in account validation timeout period.
             */
            accountValidatedBeginDate = DateUtil.midnight(DateTime.now().plusDays(1).toDate());
        }

        this.accountValidated = accountValidated;
    }

    public Date getAccountValidatedBeginDate() {
        return accountValidatedBeginDate;
    }

    public void setAccountValidatedBeginDate() {
        this.accountValidatedBeginDate = DateUtil.midnight(DateTime.now().plusDays(1).toDate());
    }

    /**
     * Condition to check if account is not validated beyond validation expired period.
     *
     * @param mailValidationFailPeriod
     * @return
     */
    public boolean isValidationExpired(int mailValidationFailPeriod) {
        return accountValidated || !(new Duration(accountValidatedBeginDate.getTime(),
                new Date().getTime()).getStandardDays() > mailValidationFailPeriod);
    }

    public String getName() {
        if (StringUtils.isNotBlank(firstName)) {
            if (StringUtils.isNotBlank(lastName)) {
                return StringUtils.trim(firstName + BLANK_SPACE + lastName);
            } else {
                return firstName;
            }
        }
        if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        }
        return userId;
    }

    public boolean isRegisteredWhenRegistrationIsOff() {
        return registeredWhenRegistrationIsOff;
    }

    public void setRegisteredWhenRegistrationIsOff(boolean registeredWhenRegistrationIsOff) {
        this.registeredWhenRegistrationIsOff = registeredWhenRegistrationIsOff;
    }

    public AccountInactiveReasonEnum getAccountInactiveReason() {
        return accountInactiveReason;
    }

    public void setAccountInactiveReason(AccountInactiveReasonEnum accountInactiveReason) {
        this.accountInactiveReason = accountInactiveReason;
    }

    public BillingAccountEntity getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccountEntity billingAccount) {
        this.billingAccount = billingAccount;
    }

    @Override
    public String toString() {
        return "UserAccountEntity{" +
                "receiptUserId='" + receiptUserId + '\'' +
                ", userId='" + userId + '\'' +
                ", providerId=" + providerId +
                ", providerUserId='" + providerUserId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", secret='" + secret + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expireTime=" + expireTime +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                ", userAuthentication=" + userAuthentication +
                ", accountValidated=" + accountValidated +
                '}';
    }
}
