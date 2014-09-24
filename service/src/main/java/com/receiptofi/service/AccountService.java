package com.receiptofi.service;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;

/**
 * User: hitender
 * Date: 4/24/13
 * Time: 9:53 PM
 */
@Service
public final class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final UserAccountManager userAccountManager;
    private final UserAuthenticationManager userAuthenticationManager;
    private final UserProfileManager userProfileManager;
    private final UserPreferenceManager userPreferenceManager;
    private final ForgotRecoverManager forgotRecoverManager;
    private final GenerateUserIdManager generateUserIdManager;

    @Value("${domain}")
    private String domain;

    @Autowired
    public AccountService(
            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserProfileManager userProfileManager,
            UserPreferenceManager userPreferenceManager,
            ForgotRecoverManager forgotRecoverManager,
            GenerateUserIdManager generateUserIdManager
    ) {
        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userProfileManager = userProfileManager;
        this.userPreferenceManager = userPreferenceManager;
        this.forgotRecoverManager = forgotRecoverManager;
        this.generateUserIdManager = generateUserIdManager;
    }

    public UserProfileEntity doesUserExists(String mail) {
        return userProfileManager.findOneByMail(mail);
    }

    public UserAccountEntity findByReceiptUserId(String receiptUserId) {
        return userAccountManager.findByReceiptUserId(receiptUserId);
    }

    public UserAccountEntity findByUserId(String mail) {
        return userAccountManager.findByUserId(mail);
    }

    public UserAccountEntity findByProviderUserId(String providerUserId) {
        return  userAccountManager.findByProviderUserId(providerUserId);
    }

    /**
     * Create a new account
     * @param email
     * @param firstName
     * @param lastName
     * @param password
     * @return
     */
    public UserAccountEntity executeCreationOfNewAccount(String email, String firstName, String lastName, String password) {
        UserAccountEntity userAccount;
        UserAuthenticationEntity userAuthentication;
        UserProfileEntity userProfile;

        try {
            userAuthentication = getUserAuthenticationEntity(password);
        } catch (Exception e) {
            LOG.error("During saving UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("error saving user authentication ", e);
        }

        try {
            userAccount = UserAccountEntity.newInstance(
                    generateUserIdManager.getNextAutoGeneratedUserId(),
                    email,
                    firstName,
                    lastName,
                    userAuthentication
            );
            userAccount.inActive(); //activated on email validation
            userAccountManager.save(userAccount);

            userProfile = UserProfileEntity.newInstance(
                    email,
                    firstName,
                    lastName,
                    userAccount.getReceiptUserId()
            );
            userProfileManager.save(userProfile);
        } catch (Exception e) {
            LOG.error("During saving UserProfileEntity={}", e.getLocalizedMessage(), e);

            //Roll back
            userAuthenticationManager.deleteHard(userAuthentication);
            throw new RuntimeException("error saving user profile ", e);
        }

        try {
            UserPreferenceEntity userPreferenceEntity = UserPreferenceEntity.newInstance(userProfile);
            userPreferenceManager.save(userPreferenceEntity);
        } catch (Exception e) {
            LOG.error("During saving UserPreferenceEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("error saving user preference ", e);
        }

        return userAccount;
    }

    /**
     * Used in for sending authentication link to recover account in case of the lost password
     *
     * @param receiptUserId
     * @return
     */
    public ForgotRecoverEntity initiateAccountRecovery(String receiptUserId) {
        String authenticationKey = HashText.computeBCrypt(RandomString.newInstance().nextString());
        ForgotRecoverEntity forgotRecoverEntity = ForgotRecoverEntity.newInstance(receiptUserId, authenticationKey);
        forgotRecoverManager.save(forgotRecoverEntity);
        return forgotRecoverEntity;
    }

    public void invalidateAllEntries(String receiptUserId) {
        forgotRecoverManager.invalidateAllEntries(receiptUserId);
    }

    public ForgotRecoverEntity findAccountAuthenticationForKey(String key) {
        return forgotRecoverManager.findByAuthenticationKey(key);
    }

    /**
     * Called during forgotten password or during an invite
     *
     * @param userAuthenticationEntity
     * @throws Exception
     */
    public void updateAuthentication(UserAuthenticationEntity userAuthenticationEntity) throws Exception {
        userAuthenticationManager.save(userAuthenticationEntity);
    }

    public UserPreferenceEntity getPreference(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    public void saveUserAccount(UserAccountEntity userAccountEntity) {
        userAccountManager.save(userAccountEntity);
    }

    public UserAccountEntity changeAccountRolesToMatchUserLevel(String receiptUserId, UserLevelEnum userLevel) {
        UserAccountEntity userAccountEntity = findByReceiptUserId(receiptUserId);
        switch(userLevel) {
            case TECHNICIAN:
                userAccountEntity.setRoles(
                        new LinkedHashSet<RoleEnum>() {{
                            add(RoleEnum.ROLE_USER);
                            add(RoleEnum.ROLE_TECHNICIAN);
                        }}
                );
                break;
            case SUPERVISOR:
                userAccountEntity.setRoles(
                        new LinkedHashSet<RoleEnum>() {{
                            add(RoleEnum.ROLE_USER);
                            add(RoleEnum.ROLE_TECHNICIAN);
                            add(RoleEnum.ROLE_SUPERVISOR);
                        }}
                );
                break;
            case ADMIN:
                userAccountEntity.setRoles(
                        new LinkedHashSet<RoleEnum>() {{
                            add(RoleEnum.ROLE_USER);
                            add(RoleEnum.ROLE_TECHNICIAN);
                            add(RoleEnum.ROLE_SUPERVISOR);
                            add(RoleEnum.ROLE_ADMIN);
                        }}
                );
                break;
            default:
                userAccountEntity.setRoles(
                        new LinkedHashSet<RoleEnum>() {{
                            add(RoleEnum.ROLE_USER);
                        }}
                );
        }
        return userAccountEntity;
    }

    public UserAuthenticationEntity getUserAuthenticationEntity(String password) {
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                HashText.computeBCrypt(password),
                HashText.computeBCrypt(RandomString.newInstance().nextString())
        );
        userAuthenticationManager.save(userAuthentication);
        return userAuthentication;
    }
}
