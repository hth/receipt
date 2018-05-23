package com.receiptofi.service;

import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.types.ExpenseTagIconEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.InviteManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.apache.commons.lang3.StringUtils;

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
import java.util.HashSet;
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
    private GenerateUserIdService generateUserIdService;
    private EmailValidateService emailValidateService;
    private RegistrationService registrationService;
    private ExpensesService expensesService;
    private BillingService billingService;
    private NotificationService notificationService;

    /* Hack to fix circular dependency of InviteService. */
    private InviteManager inviteManager;

    private String[] expenseTags;
    private String[] expenseTagColors;
    private String[] expenseTagIcons;
    private int promotionalPeriod;

    @Autowired
    public AccountService(
            @Value ("${ExpenseTags.Default:HOME,BUSINESS}")
            String[] expenseTags,

            @Value ("${ExpenseTagColors.Default:#1a9af9,#b492e8}")
            String[] expenseTagColors,

            @Value ("${ExpenseTagIcons.Default:V101,V102}")
            String[] expenseTagIcons,

            @Value ("${promotionalPeriod}")
            int promotionalPeriod,

            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserProfileManager userProfileManager,
            UserPreferenceManager userPreferenceManager,
            ForgotRecoverManager forgotRecoverManager,
            GenerateUserIdService generateUserIdService,
            EmailValidateService emailValidateService,
            RegistrationService registrationService,
            ExpensesService expensesService,
            BillingService billingService,
            NotificationService notificationService,
            InviteManager inviteManager) {
        this.expenseTags = expenseTags;
        this.expenseTagColors = expenseTagColors;
        this.expenseTagIcons = expenseTagIcons;
        this.promotionalPeriod = promotionalPeriod;

        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userProfileManager = userProfileManager;
        this.userPreferenceManager = userPreferenceManager;
        this.forgotRecoverManager = forgotRecoverManager;
        this.generateUserIdService = generateUserIdService;
        this.emailValidateService = emailValidateService;
        this.registrationService = registrationService;
        this.expensesService = expensesService;
        this.billingService = billingService;
        this.notificationService = notificationService;
        this.inviteManager = inviteManager;
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
            if (StringUtils.isBlank(password)) {
               userAuthentication = getUserAuthenticationEntity();
            } else {
                userAuthentication = getUserAuthenticationEntity(password);
            }
        } catch (Exception e) {
            LOG.error("During saving UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("error saving user authentication ", e);
        }

        String rid = generateUserIdService.getNextAutoGeneratedUserId();
        try {

            userAccount = UserAccountEntity.newInstance(
                    rid,
                    mail,
                    firstName,
                    lastName,
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
        UserAuthenticationEntity userAuthentication = getUserAuthenticationEntity();
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
                    expenseTagColors[i],
                    ExpenseTagIconEnum.valueOf(expenseTagIcons[i]));

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

    UserPreferenceEntity getPreference(UserProfileEntity userProfile) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfile);
    }

    public void saveUserAccount(UserAccountEntity userAccount) {
        userAccountManager.save(userAccount);
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
            case TECH_RECEIPT:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                userAccount.setRoles(roles);
                break;
            case TECH_CAMPAIGN:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_CAMPAIGN);
                userAccount.setRoles(roles);
                break;
            case SUPERVISOR:
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_CAMPAIGN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                userAccount.setRoles(roles);
                break;
            case ADMIN:
                /** As of now admin does not have Business and Enterprise role. */
                roles.add(RoleEnum.ROLE_USER);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_CAMPAIGN);
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
                roles.add(RoleEnum.ROLE_USER);
                userAccount.setRoles(roles);
                break;
            case ENTERPRISE:
                roles.add(RoleEnum.ROLE_ENTERPRISE);
                userAccount.setRoles(roles);
                break;
            case BUSINESS:
                roles.add(RoleEnum.ROLE_BUSINESS);
                userAccount.setRoles(roles);
                break;
            case ACCOUNTANT:
                roles.add(RoleEnum.ROLE_ACCOUNTANT);
                userAccount.setRoles(roles);
                break;
            default:
                LOG.error("Reached unreachable condition, UserLevel={}", userLevel.name());
                throw new RuntimeException("Reached unreachable condition " + userLevel.name());
        }
        return userAccount;
    }

    private UserAuthenticationEntity getUserAuthenticationEntity(String password) {
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                HashText.computeBCrypt(password),
                HashText.computeBCrypt(RandomString.newInstance().nextString())
        );
        userAuthenticationManager.save(userAuthentication);
        return userAuthentication;
    }

    /**
     * Use for Social signup or for invite. This should speed up the sign up process as it eliminates dual creation
     * of BCrypt string.
     *
     * @return
     */
    public UserAuthenticationEntity getUserAuthenticationEntity() {
        String code = HashText.computeBCrypt(RandomString.newInstance().nextString());
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                code,
                code
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

    public void updateCountryShortName(String countryShortName, String rid) {
        userProfileManager.updateCountryShortName(countryShortName.toUpperCase(), rid);
    }

    public long inactiveNonValidatedAccount(Date pastActivationDate) {
        return userAccountManager.inactiveNonValidatedAccount(pastActivationDate);
    }

    public List<UserAccountEntity> findRegisteredAccountWhenRegistrationIsOff(int registrationInviteDailyLimit) {
        return userAccountManager.findRegisteredAccountWhenRegistrationIsOff(registrationInviteDailyLimit);
    }

    public void removeRegistrationIsOffFrom(String id) {
        userAccountManager.removeRegistrationIsOffFrom(id);
    }

    public void validateAccount(EmailValidateEntity emailValidate, UserAccountEntity userAccount) {
        markAccountValidated(userAccount);

        emailValidate.inActive();
        emailValidateService.saveEmailValidateEntity(emailValidate);
    }

    public void validateAccount(InviteEntity invite, UserAccountEntity userAccount) {
        markAccountValidated(userAccount);

        invite.inActive();
        inviteManager.save(invite);
    }

    private void markAccountValidated(UserAccountEntity userAccount) {
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
                "Welcome " + userAccount.getName() + ". Next step, take a picture of your receipt from app to process it.",
                NotificationTypeEnum.PUSH_NOTIFICATION,
                NotificationGroupEnum.N,
                userAccount.getReceiptUserId());
    }

    public UserProfileEntity findProfileByReceiptUserId(String receiptUserId) {
        return userProfileManager.findByReceiptUserId(receiptUserId);
    }

    public void removeAuthenticationOrphan() {
        List<UserAuthenticationEntity> userAuthentications = userAuthenticationManager.getAll();

        for (UserAuthenticationEntity userAuthentication : userAuthentications) {
            UserAccountEntity userAccount = userAccountManager.findByUserAuthentication(userAuthentication.getId());
            if (userAccount == null) {
                LOG.warn("Orphan user authentication={}", userAuthentication.getId());
                userAuthenticationManager.deleteHard(userAuthentication);
            }
        }
    }

    public void removeUserPreferencesOrphan() {
        List<UserPreferenceEntity> userPreferences = userPreferenceManager.getAll();
        for (UserPreferenceEntity userPreference : userPreferences) {
            if (userPreference.getUserProfile() == null) {
                LOG.warn("Orphan user preference={} rid={}", userPreference.getId(), userPreference.getReceiptUserId());
                userPreferenceManager.deleteHard(userPreference);
            }
        }
    }

    public void createMissingUserPreferences() {
        List<UserProfileEntity> userProfiles = userProfileManager.getAll();
        for (UserProfileEntity userProfile : userProfiles) {
            UserPreferenceEntity userPreference = userPreferenceManager.getByRid(userProfile.getReceiptUserId());
            if (userPreference == null) {
                userPreference = UserPreferenceEntity.newInstance(userProfile);
                userPreferenceManager.save(userPreference);
                LOG.warn("Created user preference for rid={}", userProfile.getReceiptUserId());
            }
        }
    }

    public void createMissingExpenseTags() {
        List<UserProfileEntity> userProfiles = userProfileManager.getAll();
        for (UserProfileEntity userProfile : userProfiles) {
            List<ExpenseTagEntity> allExpenseTypes = expensesService.getAllExpenseTypes(userProfile.getReceiptUserId());
            if (allExpenseTypes.isEmpty()) {
                addDefaultExpenseTag(userProfile.getReceiptUserId());
                LOG.warn("Created default expenseTag for rid={}", userProfile.getReceiptUserId());
            }
        }
    }

    public void removeDuplicatesBillingHistory() {
        List<UserProfileEntity> userProfiles = userProfileManager.getAll();
        for (UserProfileEntity userProfile : userProfiles) {
            List<BillingHistoryEntity> billingHistories = billingService.getHistory(userProfile.getReceiptUserId());
            Set<String> unique = new HashSet<>();

            for (BillingHistoryEntity billingHistory : billingHistories) {
                if (unique.contains(billingHistory.getBilledForMonth())) {
                    billingService.deleteHardBillingHistory(billingHistory);
                    LOG.warn("delete duplicate history rid={} billedForMonth={}", billingHistory.getRid(), billingHistory.getBilledForMonth());
                } else {
                    unique.add(billingHistory.getBilledForMonth());
                }
            }
        }
    }
}
