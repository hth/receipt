package com.receiptofi.service;

import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    private NotificationService notificationService;

    private String[] expenseTags;
    private String[] expenseTagColors;
    private int promotionalPeriod;

    @Autowired
    public AccountService(
            @Value ("${ExpenseTags.Default:HOME,BUSINESS}")
            String[] expenseTags,

            @Value ("${ExpenseTagColors.Default:#1a9af9,#b492e8}")
            String[] expenseTagColors,

            @Value ("${promotionalPeriod}")
            int promotionalPeriod,

            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserProfileManager userProfileManager,
            UserPreferenceManager userPreferenceManager,
            ForgotRecoverManager forgotRecoverManager,
            GenerateUserIdManager generateUserIdManager,
            EmailValidateService emailValidateService,
            RegistrationService registrationService,
            ExpensesService expensesService,
            BillingService billingService,
            NotificationService notificationService
    ) {
        this.expenseTags = expenseTags;
        this.expenseTagColors = expenseTagColors;
        this.promotionalPeriod = promotionalPeriod;

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
        this.notificationService = notificationService;
    }

    public UserProfileEntity doesUserExists(String mail) {
        return userProfileManager.findOneByMail(mail);
    }

    public UserAccountEntity findByReceiptUserId(String rid) {
        return userAccountManager.findByReceiptUserId(rid);
    }

    @Mobile
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
        addWelcomeNotification(userAccount);
        return userAccount;
    }

    /**
     * Create new account using social login.
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
        save(userAccount);
        addDefaultExpenseTag(userAccount.getReceiptUserId());
        addWelcomeNotification(userAccount);
    }

    public void save(UserAccountEntity userAccount) {
        try {
            userAccountManager.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAccountEntity={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Saving UserAccount rid={} reason={}", userAccount.getReceiptUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user account");
        }
    }

    /**
     * Save userProfile.
     */
    public void save(UserProfileEntity userProfile) {
        try {
            userProfileManager.save(userProfile);
            LOG.debug("Created UserProfileEntity={} id={}", userProfile.getReceiptUserId(), userProfile.getId());
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserProfileEntity={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Saving UserProfile rid={} reason={}", userProfile.getReceiptUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user profile");
        }
    }

    /**
     * Create and Save user preferences. Shared with social registration.
     */
    public void createPreferences(UserProfileEntity userProfile) {
        try {
            UserPreferenceEntity userPreferenceEntity = UserPreferenceEntity.newInstance(userProfile);
            userPreferenceManager.save(userPreferenceEntity);
            LOG.debug("Created UserPreferenceEntity={}", userPreferenceEntity.getReceiptUserId());
        } catch (Exception e) {
            LOG.error("Saving UserPreferenceEntity rid={} reason={}", userProfile.getReceiptUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user preference");
        }
    }

    /**
     * Shared with social registration
     *
     * @param rid
     */
    private void addDefaultExpenseTag(String rid) {
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
    private void billAccount(UserAccountEntity userAccount) {
        billingService.save(userAccount.getBillingAccount());

        /**
         * Mark month as PROMOTIONAL.
         */
        BillingHistoryEntity billingHistory;
        for (int monthCount = 0; monthCount < promotionalPeriod; monthCount++) {
            billingHistory = new BillingHistoryEntity(
                    userAccount.getReceiptUserId(),
                    Date.from(LocalDateTime.now().plusMonths(monthCount).toInstant(ZoneOffset.UTC)));
            billingHistory.setBilledStatus(BilledStatusEnum.P);
            billingHistory.setBillingPlan(BillingPlanEnum.P);
            billingService.save(billingHistory);
        }
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
    ForgotRecoverEntity initiateAccountRecovery(String receiptUserId) {
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
     * @param userAuthentication
     */
    public void updateAuthentication(UserAuthenticationEntity userAuthentication) {
        userAuthenticationManager.save(userAuthentication);
    }

    UserPreferenceEntity getPreference(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    public void saveUserAccount(UserAccountEntity userAccountEntity) {
        userAccountManager.save(userAccountEntity);
    }

    private void updateAccountToValidated(String id, AccountInactiveReasonEnum accountInactiveReason) {
        userAccountManager.updateAccountToValidated(id, accountInactiveReason);
    }

    /**
     * Change user role to match user level.
     *
     * @param rid
     * @param userLevel
     * @return
     */
    public UserAccountEntity changeAccountRolesToMatchUserLevel(String rid, UserLevelEnum userLevel) {
        UserAccountEntity userAccount = findByReceiptUserId(rid);
        Set<RoleEnum> roles = new LinkedHashSet<>();
        switch (userLevel) {
            case TECHNICIAN:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                userAccount.setRoles(roles);
                break;
            case SUPERVISOR:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                userAccount.setRoles(roles);
                break;
            case ADMIN:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_BUSINESS);
                roles.add(RoleEnum.ROLE_ENTERPRISE);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                roles.add(RoleEnum.ROLE_ADMIN);
                roles.add(RoleEnum.ROLE_ANALYSIS_READ);
                userAccount.setRoles(roles);
                break;
            case ANALYSIS_READ:
                roles.add(RoleEnum.ROLE_ANALYSIS_READ);
                userAccount.setRoles(roles);
                break;
            case USER:
            case USER_COMMUNITY:
            case USER_PAID:
                roles.add(RoleEnum.ROLE_USER);
                userAccount.setRoles(roles);
                break;
            case ENTERPRISE:
            case ENTERPRISE_COMMUNITY:
            case ENTERPRISE_PAID:
                roles.add(RoleEnum.ROLE_ENTERPRISE);
                userAccount.setRoles(roles);
                break;
            case BUSINESS_SMALL:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_BUSINESS);
            case BUSINESS_LARGE:
                roles.add(RoleEnum.ROLE_BUSINESS);
                userAccount.setRoles(roles);
                break;
            default:
                LOG.error("Reached unreachable condition, UserLevel={}", userLevel.name());
                throw new RuntimeException("Reached unreachable condition " + userLevel.name());
        }
        return userAccount;
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

    //TODO validate the code for getting correct age of user
    private int getAge(ReceiptUser receiptUser) {
        UserProfileEntity userProfile = doesUserExists(receiptUser.getUsername());
        String bd = userProfile.getBirthday();
        if (StringUtils.isNotBlank(bd)) {
            Date date = DateUtil.getDateFromString(bd);

            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalDate today = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Period duration = Period.between(zdt.toLocalDate(), today);
            return duration.getYears();
        }
        return 100;
    }

    public List<UserAccountEntity> findAllTechnician() {
        return userAccountManager.findAllTechnician();
    }

    /**
     * Should be called when from catch condition of DataIntegrityViolationException.
     *
     * @param userAccount
     * @param e
     */
    public void deleteAllWhenAccountCreationFailedDueToDuplicate(UserAccountEntity userAccount, DataIntegrityViolationException e) {
        Assert.notNull(e, "DataIntegrityViolationException is not set or not invoked properly");

        List<ExpenseTagEntity> expenseTagEntities = expensesService.getAllExpenseTypes(userAccount.getReceiptUserId());
        for (ExpenseTagEntity expenseTag : expenseTagEntities) {
            expensesService.deleteHard(expenseTag, e);
        }

        userAuthenticationManager.deleteHard(userAccount.getUserAuthentication());
        userAccountManager.deleteHard(userAccount);
        billingService.deleteHardBillingWhenAccountCreationFails(userAccount.getReceiptUserId());

        UserPreferenceEntity userPreference = userPreferenceManager.getByRid(userAccount.getReceiptUserId());
        if (null != userPreference) {
            userPreferenceManager.deleteHard(userPreference);
        }

        UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(userAccount.getReceiptUserId());
        if (null != userProfile) {
            userProfileManager.deleteHard(userProfile);
        }
    }

    private void addWelcomeNotification(UserAccountEntity userAccount) {
        notificationService.addNotification(
                "Welcome " + userAccount.getName() + ". Next step, take a picture of the receipt from app to process it.",
                NotificationTypeEnum.PUSH_NOTIFICATION,
                NotificationGroupEnum.N,
                userAccount.getReceiptUserId());
    }

    UserProfileEntity findProfileByReceiptUserId(String receiptUserId) {
        return userProfileManager.findByReceiptUserId(receiptUserId);
    }
}
