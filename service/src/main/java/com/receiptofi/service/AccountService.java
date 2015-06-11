package com.receiptofi.service;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.ProviderEnum;
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

import org.apache.commons.lang3.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 4/24/13
 * Time: 9:53 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private UserAccountManager userAccountManager;
    private UserAuthenticationManager userAuthenticationManager;
    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;
    private ForgotRecoverManager forgotRecoverManager;
    private GenerateUserIdManager generateUserIdManager;
    private EmailValidateService emailValidateService;
    private RegistrationService registrationService;
    private ExpensesService expensesService;
    private BillingService billingService;

    @Value ("${domain}")
    private String domain;

    @Value ("${ExpenseTags.Default:HOME,BUSINESS}")
    private String[] expenseTags;

    @Value("${ExpenseTagColors.Default:#1a9af9,#b492e8}")
    private String[] expenseTagColors;

    @Autowired
    public AccountService(
            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserProfileManager userProfileManager,
            UserPreferenceManager userPreferenceManager,
            ForgotRecoverManager forgotRecoverManager,
            GenerateUserIdManager generateUserIdManager,
            EmailValidateService emailValidateService,
            RegistrationService registrationService,
            ExpensesService expensesService,
            BillingService billingService
    ) {
        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userProfileManager = userProfileManager;
        this.userPreferenceManager = userPreferenceManager;
        this.forgotRecoverManager = forgotRecoverManager;
        this.generateUserIdManager = generateUserIdManager;
        this.emailValidateService = emailValidateService;
        this.registrationService = registrationService;
        this.expensesService = expensesService;
        this.billingService = billingService;
    }

    public UserProfileEntity doesUserExists(String mail) {
        return userProfileManager.findOneByMail(mail);
    }

    public UserAccountEntity findByReceiptUserId(String rid) {
        return userAccountManager.findByReceiptUserId(rid);
    }

    public UserAccountEntity findByUserId(String mail) {
        return userAccountManager.findByUserId(mail);
    }

    public UserAccountEntity findByProviderUserId(String providerUserId) {
        return userAccountManager.findByProviderUserId(providerUserId);
    }

    /**
     * Creates new user account. There are some rollback but this process should not fail.
     *
     * @param mail
     * @param firstName
     * @param lastName
     * @param password
     * @param birthday
     * @return
     */
    public UserAccountEntity createNewAccount(
            String mail,
            String firstName,
            String lastName,
            String password,
            String birthday
    ) {
        UserAccountEntity userAccount = null;
        UserAuthenticationEntity userAuthentication;
        UserProfileEntity userProfile;

        try {
            userAuthentication = getUserAuthenticationEntity(password);
        } catch (Exception e) {
            LOG.error("During saving UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("error saving user authentication ", e);
        }

        String rid = generateUserIdManager.getNextAutoGeneratedUserId();
        try {

            userAccount = UserAccountEntity.newInstance(
                    rid,
                    mail,
                    WordUtils.capitalize(firstName),
                    WordUtils.capitalize(lastName),
                    userAuthentication
            );
            userAccount.setAccountValidated(false);
            userAccount.setAccountValidatedBeginDate();
            /**
             * when registration is allowed and account is not validated, then user account login is active until
             * mail.validation.timeout.period. System will limit login and access beyond the days allowed.
             */
            registrationService.isRegistrationAllowed(userAccount);
            billAccount(userAccount);
            userAccount.active();
            userAccountManager.save(userAccount);

            userProfile = UserProfileEntity.newInstance(
                    mail,
                    firstName,
                    lastName,
                    rid,
                    birthday
            );
            userProfileManager.save(userProfile);
        } catch (Exception e) {
            LOG.error("During saving UserProfileEntity={}", e.getLocalizedMessage(), e);

            //Roll back
            if (userAccount != null) {
                deleteBilling(rid);
                userAccountManager.deleteHard(userAccount);
            }
            userAuthenticationManager.deleteHard(userAuthentication);
            throw new RuntimeException("Error saving user profile");
        }

        createPreferences(userProfile);
        addDefaultExpenseTag(rid);
        return userAccount;
    }

    /**
     * Create new account using social login.
     *
     * @param userAccount
     */
    public void createNewAccount(UserAccountEntity userAccount) {
        Assert.notNull(userAccount.getProviderId());
        LOG.info("New account created using social user={} provider={}",
                userAccount.getReceiptUserId(), userAccount.getProviderId());
        /**
         * UserAuthenticationEntity is not required but needed. Social user will not be able to reset the authentication
         * since its a social account.
         */
        UserAuthenticationEntity userAuthentication = getUserAuthenticationEntity(RandomString.newInstance().nextString());
        userAccount.setUserAuthentication(userAuthentication);
        registrationService.isRegistrationAllowed(userAccount);
        billAccount(userAccount);
        userAccountManager.save(userAccount);
        addDefaultExpenseTag(userAccount.getReceiptUserId());
    }

    /**
     * Shared with social registration
     *
     * @param userProfile
     */
    public void createPreferences(UserProfileEntity userProfile) {
        try {
            UserPreferenceEntity userPreferenceEntity = UserPreferenceEntity.newInstance(userProfile);
            userPreferenceManager.save(userPreferenceEntity);
        } catch (Exception e) {
            LOG.error("During saving UserPreferenceEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user preference");
        }
    }

    /**
     * Shared with social registration
     *
     * @param rid
     */
    public void addDefaultExpenseTag(String rid) {
        int i = 0;
        /** Add default expense tags. */
        for (String tag : expenseTags) {
            ExpenseTagEntity expenseTag = ExpenseTagEntity.newInstance(
                    tag,
                    rid,
                    expenseTagColors[i]);

            expensesService.saveExpenseTag(expenseTag);
            i++;
        }
    }

    /**
     * Shared with social registration
     *
     * @param userAccount
     */
    public void billAccount(UserAccountEntity userAccount) {
        BillingAccountEntity billingAccount = userAccount.getBillingAccount();

        /** Consider the account as billed from get go as BillingHistory is created with Promotional. */
        billingAccount.markAccountBilled();
        billingService.save(billingAccount);

        /**
         * Mark PROMOTIONAL as billed for the first and second month.
         * First month marked PROMOTIONAL during signup.
         */
        BillingHistoryEntity billingHistory = new BillingHistoryEntity(
                userAccount.getReceiptUserId(),
                new Date());
        billingHistory.setBilledStatus(BilledStatusEnum.P);
        billingHistory.setAccountBillingType(AccountBillingTypeEnum.P);
        billingService.save(billingHistory);

        /** Second month marked as PROMOTIONAL too. */
//        billingHistory = new BillingHistoryEntity(
//                userAccount.getReceiptUserId(),
//                Date.from(LocalDateTime.now().plusMonths(1).toInstant(ZoneOffset.UTC)));
//        billingHistory.setBilledStatus(BilledStatusEnum.P);
//        billingHistory.setAccountBillingType(AccountBillingTypeEnum.P);
//        billingService.save(billingHistory);
    }

    /**
     * Called when user account creation fails.
     *
     * @param rid
     */
    private void deleteBilling(String rid) {
        billingService.deleteHardBillingWhenAccountCreationFails(rid);
    }

    public List<UserAccountEntity> findAllForBilling(int skipDocuments, int limit) {
        return userAccountManager.findAllForBilling(skipDocuments, limit);
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

    public ForgotRecoverEntity findByAuthenticationKey(String key) {
        return forgotRecoverManager.findByAuthenticationKey(key);
    }

    /**
     * Called during forgotten password or during an invite.
     *
     * @param userAuthenticationEntity
     * @throws Exception
     */
    public void updateAuthentication(UserAuthenticationEntity userAuthenticationEntity) {
        userAuthenticationManager.save(userAuthenticationEntity);
    }

    public UserPreferenceEntity getPreference(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    public void saveUserAccount(UserAccountEntity userAccountEntity) {
        userAccountManager.save(userAccountEntity);
    }

    public void updateAccountToValidated(String id, AccountInactiveReasonEnum accountInactiveReason) {
        userAccountManager.updateAccountToValidated(id, accountInactiveReason);
    }

    public UserAccountEntity changeAccountRolesToMatchUserLevel(String receiptUserId, UserLevelEnum userLevel) {
        UserAccountEntity userAccountEntity = findByReceiptUserId(receiptUserId);
        Set<RoleEnum> roles = new LinkedHashSet<>();
        switch (userLevel) {
            case TECHNICIAN:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                userAccountEntity.setRoles(roles);
                break;
            case SUPERVISOR:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                userAccountEntity.setRoles(roles);
                break;
            case ADMIN:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                roles.add(RoleEnum.ROLE_ADMIN);
                roles.add(RoleEnum.ROLE_ANALYSIS_READ);
                userAccountEntity.setRoles(roles);
                break;
            case ANALYSIS_READ:
                roles.add(RoleEnum.ROLE_ANALYSIS_READ);
                userAccountEntity.setRoles(roles);
                break;
            case USER:
            case USER_COMMUNITY:
            case USER_PAID:
            case EMPLOYER:
            case EMPLOYER_COMMUNITY:
            case EMPLOYER_PAID:
                roles.add(RoleEnum.ROLE_USER);
                userAccountEntity.setRoles(roles);
                break;
            default:
                LOG.error("Reached unreachable condition, UserLevel={}", userLevel.name());
                throw new RuntimeException("Reached unreachable condition " + userLevel.name());
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

    public UserAccountEntity findByAuthorizationCode(ProviderEnum provider, String authorizationCode) {
        return userAccountManager.findByAuthorizationCode(provider, authorizationCode);
    }

    /**
     * Updates existing userId with new userId.
     * </p>
     * Do not add send email in this method. Any call invokes this method needs to call accountValidationMail after it.
     *
     * @param existingUserId
     * @param newUserId
     * @return
     * @see com.receiptofi.service.MailService#accountValidationMail(String, String, String) ()
     */
    @Mobile
    @SuppressWarnings ("unused")
    public UserAccountEntity updateUID(String existingUserId, String newUserId) {
        UserAccountEntity userAccount = findByUserId(existingUserId);
        if (!userAccount.isAccountValidated()) {
            emailValidateService.invalidateAllEntries(userAccount.getReceiptUserId());
        }
        userAccount.setUserId(newUserId);
        userAccount.setAccountValidated(false);
        userAccount.active();

        UserProfileEntity userProfile = doesUserExists(existingUserId);
        userProfile.setEmail(newUserId);

        /** Always update userAccount before userProfile */
        userAccountManager.save(userAccount);
        userProfileManager.save(userProfile);

        return userAccount;
    }

    /**
     * For Web Application use this method to change user email.
     * </p>
     * Do not add send email in this method. Any call invokes this method needs to call accountValidationMail after it.
     *
     * @param existingUserId
     * @param newUserId
     * @param rid
     * @return
     * @see com.receiptofi.service.MailService#accountValidationMail(String, String, String) ()
     */
    public UserAccountEntity updateUID(String existingUserId, String newUserId, String rid) {
        if (findByUserId(newUserId) == null) {

            UserAccountEntity userAccount = findByReceiptUserId(rid);
            if (!userAccount.isAccountValidated()) {
                emailValidateService.invalidateAllEntries(userAccount.getReceiptUserId());
            }
            userAccount.setUserId(newUserId);
            userAccount.setAccountValidated(false);
            userAccount.active();

            UserProfileEntity userProfile = userProfileManager.forProfilePreferenceFindByReceiptUserId(rid);
            userProfile.setEmail(newUserId);

            /** Always update userAccount before userProfile */
            userAccountManager.save(userAccount);
            userProfileManager.save(userProfile);

            return userAccount;
        } else {
            return null;
        }
    }

    public void updateName(String firstName, String lastName, String rid) {
        UserAccountEntity userAccount = findByReceiptUserId(rid);
        UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(rid);

        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);

        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);

        userProfileManager.save(userProfile);
        userAccountManager.save(userAccount);
    }

    public int inactiveNonValidatedAccount(Date pastActivationDate) {
        return userAccountManager.inactiveNonValidatedAccount(pastActivationDate);
    }

    public List<UserAccountEntity> findRegisteredAccountWhenRegistrationIsOff(int registrationInviteDailyLimit) {
        return userAccountManager.findRegisteredAccountWhenRegistrationIsOff(registrationInviteDailyLimit);
    }

    public void removeRegistrationIsOffFrom(String id) {
        userAccountManager.removeRegistrationIsOffFrom(id);
    }

    public void validateAccount(EmailValidateEntity emailValidate, UserAccountEntity userAccount) {
        if (null != userAccount.getAccountInactiveReason()) {
            switch (userAccount.getAccountInactiveReason()) {
                case ANV:
                    updateAccountToValidated(userAccount.getId(), AccountInactiveReasonEnum.ANV);
                    break;
                default:
                    LOG.error("Reached unreachable condition, rid={}", userAccount.getReceiptUserId());
                    throw new RuntimeException("Reached unreachable condition " + userAccount.getReceiptUserId());
            }
        } else {
            userAccount.setAccountValidated(true);
            saveUserAccount(userAccount);
        }

        emailValidate.inActive();
        emailValidate.setUpdated();
        emailValidateService.saveEmailValidateEntity(emailValidate);
    }
}
