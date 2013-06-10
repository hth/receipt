package com.tholix.service;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.ForgotRecoverEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.ForgotRecoverManager;
import com.tholix.repository.UserAuthenticationManager;
import com.tholix.repository.UserPreferenceManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.utils.RandomString;
import com.tholix.utils.SHAHashing;
import com.tholix.web.form.UserRegistrationForm;

/**
 * User: hitender
 * Date: 4/24/13
 * Time: 9:53 PM
 */
@Service
public class AccountService {
    private static final Logger log = Logger.getLogger(AccountService.class);

    @Autowired private UserAuthenticationManager userAuthenticationManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserPreferenceManager userPreferenceManager;
    @Autowired private ForgotRecoverManager forgotRecoverManager;

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
            userAuthenticationManager.save(userAuthentication);
        } catch (Exception e) {
            log.error("During saving UserAuthenticationEntity: " + e.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user authentication");
            throw new Exception("error saving user authentication");
        }

        try {
            userProfile = userRegistrationForm.newUserProfileEntity(userAuthentication);
            userProfileManager.save(userProfile);
        } catch (Exception e) {
            log.error("During saving UserProfileEntity: " + e.getLocalizedMessage());

            //Roll back
            userAuthenticationManager.delete(userAuthentication);

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

    public void updateAuthentication(UserAuthenticationEntity userAuthenticationEntity) throws Exception {
        userAuthenticationManager.save(userAuthenticationEntity);
    }
}
