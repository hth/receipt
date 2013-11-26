package com.receiptofi.service;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.utils.RandomString;
import com.receiptofi.utils.SHAHashing;
import com.receiptofi.web.form.UserRegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 4/24/13
 * Time: 9:53 PM
 */
@Service
public final class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired private UserAuthenticationManager userAuthenticationManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserPreferenceManager userPreferenceManager;
    @Autowired private ForgotRecoverManager forgotRecoverManager;

    //TODO remove this
    @Value("${grandPassword}")
    private String grandPassword;

    @Value("${domain}")
    private String domain;

    public UserProfileEntity findIfUserExists(String emailId) {
        return userProfileManager.findOneByEmail(emailId);
    }

    /**
     * Create a new account
     *
     * @param userRegistrationForm
     * @return
     * @throws Exception
     */
    public UserProfileEntity createNewAccount(UserRegistrationForm userRegistrationForm) throws Exception {
        DateTime time = DateUtil.now();
        UserAuthenticationEntity userAuthentication;
        UserProfileEntity userProfile;
        try {
            userAuthentication = userRegistrationForm.newUserAuthenticationEntity();
            userAuthentication.setGrandPassword(grandPassword);
            userAuthenticationManager.save(userAuthentication);
        } catch (Exception e) {
            log.error("During saving UserAuthenticationEntity: " + e.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user authentication");
            throw new Exception("error saving user authentication");
        }

        try {
            userProfile = userRegistrationForm.newUserProfileEntity(userAuthentication);

            if(!domain.startsWith("localhost")) {
                //TODO For now de-activate all registration. Currently registration is by invitation only.
                userProfile.inActive();
            }

            userProfileManager.save(userProfile);
        } catch (Exception e) {
            log.error("During saving UserProfileEntity: " + e.getLocalizedMessage());

            //Roll back
            userAuthenticationManager.deleteHard(userAuthentication);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user profile");
            throw new Exception("error saving user profile");
        }

        try {
            userPreferenceManager.save(userRegistrationForm.newUserPreferenceEntity(userProfile));
        } catch (Exception e) {
            log.error("During saving UserPreferenceEntity: " + e.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user preference");
            throw new Exception("error saving user preference");
        }

        return userProfile;
    }

    /**
     * Used in for sending authentication link to recover account in case of the lost password
     *
     * @param userProfileEntity
     * @return
     */
    public ForgotRecoverEntity initiateAccountRecovery(UserProfileEntity userProfileEntity) throws Exception {
        String authenticationKey = SHAHashing.hashCodeSHA512(RandomString.newInstance().nextString());

        ForgotRecoverEntity forgotRecoverEntity = ForgotRecoverEntity.newInstance(userProfileEntity.getId(), authenticationKey);
        try {
            forgotRecoverManager.save(forgotRecoverEntity);
            return forgotRecoverEntity;
        } catch (Exception exception) {
            log.error("Exception generated during password recovery action: " + exception.getLocalizedMessage());
            throw exception;
        }
    }

    public void invalidateAllEntries(ForgotRecoverEntity forgotRecoverEntity) {
        forgotRecoverManager.invalidateAllEntries(forgotRecoverEntity);
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
        userAuthenticationEntity.setGrandPassword(grandPassword);
        userAuthenticationManager.save(userAuthenticationEntity);
    }

    public UserPreferenceEntity getPreference(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }
}
