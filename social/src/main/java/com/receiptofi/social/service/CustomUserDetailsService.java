package com.receiptofi.social.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.InviteService;
import com.receiptofi.service.MailService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.social.UserAccountDuplicateException;
import com.receiptofi.social.annotation.Social;
import com.receiptofi.social.config.SocialConfig;
import com.receiptofi.social.connect.ConnectionService;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 3/29/14 12:33 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private SocialConfig socialConfig;
    @Autowired private AccountService accountService;
    @Autowired private ConnectionService connectionService;
    @Autowired private GenerateUserIdManager generateUserIdManager;
    @Autowired private GoogleAccessTokenService googleAccessTokenService;
    @Autowired private InviteService inviteService;
    @Autowired private MailService mailService;

    @Value ("${mail.validation.timeout.period}")
    private int mailValidationTimeoutPeriod;

    @Value ("${CustomUserDetailsService.account.not.validated.message}")
    private String accountNotValidatedMessage;

    @Value ("${CustomUserDetailsService.account.signup.incomplete.message}")
    private String accountSignupIncompleteMessage;

    /**
     * @param email - lower case string
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOG.info("login attempted user={}", email);

        /** Always check user login with lower letter email case. */
        UserProfileEntity userProfile = userProfilePreferenceService.findByEmail(email);
        if (null == userProfile) {
            LOG.warn("Not found user={}", email);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = accountService.findByReceiptUserId(userProfile.getReceiptUserId());
            LOG.info("user={} accountValidated={}", userAccount.getReceiptUserId(), userAccount.isAccountValidated());

            boolean condition = isUserActiveAndRegistrationTurnedOn(userAccount);
            if (!condition && null == userAccount.getProviderId()) {
                /** Throw exception when its NOT a social signup. */
                throw new RuntimeException("Registration is turned off. We will notify you on your registered email " +
                        (StringUtils.isNotBlank(userProfile.getEmail()) ? "<b>" + userProfile.getEmail() + "</b>" : "") +
                        " when we start accepting new users.");
            }

            return new ReceiptUser(
                    userProfile.getEmail(),
                    userAccount.getUserAuthentication().getPassword(),
                    getAuthorities(userAccount.getRoles()),
                    userProfile.getReceiptUserId(),
                    userProfile.getProviderId(),
                    userProfile.getLevel(),
                    condition,
                    userAccount.isAccountValidated()
            );
        }
    }

    /**
     * If registration is turned on then check if the account is validated and not beyond set number of days
     * And, if registration is turned off then check is userAccount is active.
     *
     * @param userAccount
     * @return
     */
    public boolean isUserActiveAndRegistrationTurnedOn(UserAccountEntity userAccount) {
        if (userAccount.isRegisteredWhenRegistrationIsOff()) {
            /**
             * Do not throw exception here as Social API will get exception that would not be handled properly.
             * For regular sign up user to be notified during login is managed inside the method.
             * @see com.receiptofi.social.service.CustomUserDetailsService#loadUserByUsername(String)
             */
            return false;
        } else if (userAccount.isActive()) {
            if (userAccount.isAccountValidated() || userAccount.isValidationExpired(mailValidationTimeoutPeriod)) {
                return true;
            } else {
                throw new RuntimeException(accountNotValidatedMessage);
            }
        } else if (null != userAccount.getAccountInactiveReason()) {
            switch (userAccount.getAccountInactiveReason()) {
                case ANV:
                    throw new RuntimeException(accountNotValidatedMessage);
                default:
                    LOG.error("Reached condition for invalid account rid={}", userAccount.getReceiptUserId());
                    return false;
            }
        } else if (!userAccount.isActive() && null == userAccount.getAccountInactiveReason()) {
            LOG.info("Invited user did not complete signup rid={}", userAccount.getReceiptUserId());

            InviteEntity invite = inviteService.find(userAccount.getUserId());
            if (invite.isActive()) {
                switch (invite.getUserLevel()) {
                    case BUSINESS:
                    case ACCOUNTANT:
                        mailService.sendBusinessInvite(
                                invite.getEmail(),
                                invite.getInvitedBy().getReceiptUserId(),
                                invite.getInvitedBy().getUserId(),
                                invite.getUserLevel());
                        break;
                    case USER:
                        mailService.sendInvite(invite.getEmail(), invite.getInvitedBy().getReceiptUserId(), invite.getInvitedBy().getUserId());
                        break;
                    case ENTERPRISE:
                        //TODO
                        break;
                    default:
                        LOG.error("Reached unsupported rid={} uid={} condition={}",
                                invite.getInvited().getReceiptUserId(), invite.getEmail(), invite.getUserLevel());
                        throw new UnsupportedOperationException("Reached unsupported condition");
                }
                throw new RuntimeException(accountSignupIncompleteMessage);
            }
            return false;
        } else {
            LOG.error("Reached condition for invalid account rid={}", userAccount.getReceiptUserId());
            return false;
        }
    }

    /**
     * Loads user by provider user id from Google or Facebook.
     *
     * @param puid - Provider User Id
     * @return
     * @throws UsernameNotFoundException
     */
    @Social
    public UserDetails loadUserByProviderUserId(String puid) throws UsernameNotFoundException {
        LOG.info("login through Provider user={}", puid);

        UserProfileEntity userProfile = userProfilePreferenceService.findByProviderUserId(puid);
        if (null == userProfile) {
            LOG.warn("Not found provider user={}", puid);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = accountService.findByReceiptUserId(userProfile.getReceiptUserId());
            LOG.info("user={} accountValidated={}", userAccount.getReceiptUserId(), userAccount.isAccountValidated());

            boolean condition = isUserActiveAndRegistrationTurnedOn(userAccount);
            return new ReceiptUser(
                    StringUtils.isBlank(userAccount.getUserId()) ? userProfile.getProviderUserId() : userAccount.getUserId(),
                    userAccount.getUserAuthentication().getPassword(),
                    getAuthorities(userAccount.getRoles()),
                    userProfile.getReceiptUserId(),
                    userProfile.getProviderId(),
                    userProfile.getLevel(),
                    condition,
                    userAccount.isAccountValidated()
            );
        }
    }

    /**
     * Mobile app sends provider Id and accessToken to sign up or login.
     *
     * @param provider
     * @param accessToken Facebook sends accessToken and Google sends authorization code
     * @return
     */
    @Mobile
    @Social
    public String signInOrSignup(ProviderEnum provider, String accessToken) {
        UserAccountEntity userAccount;
        try {
            switch (provider) {
                case FACEBOOK:
                    userAccount = connectFacebook(provider, accessToken);
                    break;
                case GOOGLE:
                    userAccount = accountService.findByAuthorizationCode(provider, accessToken);
                    if (userAccount == null) {
                        Map<String, ScrubbedInput> map = googleAccessTokenService.getTokenForAuthorizationCode(accessToken);
                        if (!map.isEmpty()) {
                            userAccount = connectGoogle(
                                    provider,
                                    accessToken,
                                    map.get("access_token").getText(),
                                    map.get("refresh_token").getText());
                        }
                    } else {
                        //TODO(hth) do we need to go through this when token are same. Check if token is same when profile info has not changed.
                        LOG.info("found same google authorization code, so skipping");
//                        userAccount = connectGoogle(
//                                provider,
//                                accessToken,
//                                userAccount.getAccessToken(),
//                                userAccount.getRefreshToken());
                    }
                    break;
                default:
                    LOG.error("Reached unreachable condition", provider);
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
            if (null != userAccount) {
                JsonObject result = new JsonObject();
                result.addProperty("X-R-MAIL", userAccount.getUserId());
                result.addProperty("X-R-AUTH", userAccount.getUserAuthentication().getAuthenticationKeyEncoded());
                return new Gson().toJson(result);
            }
            return "{}";
        } catch (UserAccountDuplicateException e) {
            LOG.error("Duplicate account error pid={} reason={}", provider, e.getLocalizedMessage(), e);
            throw e;
        } catch (DuplicateKeyException e) {
            LOG.error("Duplicate account error pid={} reason={}", provider, e.getLocalizedMessage(), e);
            throw new UserAccountDuplicateException("Account with similar email address exists under another social provider");
        }
    }

    /**
     * Mobile connection only. Not for web social login.
     *
     * @param provider
     * @param accessToken
     * @return
     */
    @Mobile
    private UserAccountEntity connectFacebook(ProviderEnum provider, String accessToken) {
        UserAccountEntity userAccount = null;
        UsersConnectionRepository userConnectionRepository;
        ConnectionRepository connectionRepository;
        List<Connection<?>> connections;

        Facebook facebook = new FacebookTemplate(accessToken);
        User user = facebook.userOperations().getUserProfile();
        String facebookProfileId = user.getId();
        LOG.info("facebook profile puid={} mail={}",
                facebookProfileId,
                facebook.userOperations().getUserProfile().getEmail());

        UserProfileEntity userProfile = connectionService.getUserProfileEntity(
                facebook.userOperations().getUserProfile().getEmail(),
                facebook.userOperations().getUserProfile().getId());
        if (null == userProfile) {
            try {
                userAccount = saveNewFacebookUserAccountEntity(
                        accessToken,
                        provider,
                        facebook.userOperations().getUserProfile());

                /** Copy to user profile. */
                userProfile = connectionService.copyToUserProfile(facebook.userOperations().getUserProfile(), userAccount);
                accountService.save(userProfile);
                accountService.createNewAccount(userAccount);
                accountService.createPreferences(userProfile);
                updateUserIdWithEmailWhenPresent(userAccount, userProfile);
            } catch (DataIntegrityViolationException e) {
                if (userAccount != null) {
                    LOG.error("Account already exists rid={}", userAccount.getReceiptUserId());
                    accountService.deleteAllWhenAccountCreationFailedDueToDuplicate(userAccount, e);
                }
                throw new UserAccountDuplicateException("Found existing user with similar login");
            }
        } else {
            userAccount = accountService.findByProviderUserId(facebookProfileId);
            if (null == userAccount) {
                LOG.warn("Account already exists rid={} email={} pid={}",
                        userProfile.getReceiptUserId(), userProfile.getEmail(), userProfile.getProviderId());
                throw new UserAccountDuplicateException("Found existing user with similar login");
            } else {
                LOG.info("access token different between old and new",
                        StringUtils.difference(userAccount.getAccessToken(), accessToken));
                userAccount.setAccessToken(accessToken);
                accountService.saveUserAccount(userAccount);
            }
        }

        userConnectionRepository = socialConfig.usersConnectionRepository();
        connectionRepository = userConnectionRepository.createConnectionRepository(facebookProfileId);
        connections = connectionRepository.findConnections(provider.name());
        Assert.isTrue(isConnectionPopulated(connections, provider.name()), "connection repository size is zero");
        socialConfig.mongoConnectionService().update(facebookProfileId, connections.get(0));
        return userAccount;
    }

    /**
     * Mobile connection only. Not for web social login.
     *
     * @param provider
     * @param authorizationCode
     * @param accessToken
     * @param refreshToken      Always store user refresh tokens. If your application needs a new refresh token it must
     *                          sent a request with the approval_prompt query parameter set to force. This will cause
     *                          the user to see a dialog to grant permission to your application again.
     * @return
     * @link https://developers.google.com/glass/develop/mirror/authorization
     */
    @Mobile
    private UserAccountEntity connectGoogle(
            ProviderEnum provider,
            String authorizationCode,
            String accessToken,
            String refreshToken
    ) {
        UserAccountEntity userAccount = null;
        UsersConnectionRepository userConnectionRepository;
        ConnectionRepository connectionRepository;
        List<Connection<?>> connections;

        Google google = new GoogleTemplate(accessToken);
        String googleProfileId = google.plusOperations().getGoogleProfile().getId();
        LOG.info("google profile puid={} mail={}",
                googleProfileId,
                google.plusOperations().getGoogleProfile().getAccountEmail());

        UserProfileEntity userProfile = connectionService.getUserProfileEntity(
                google.plusOperations().getGoogleProfile().getAccountEmail(),
                google.plusOperations().getGoogleProfile().getId());

        if (null == userProfile) {
            try {
                userAccount = saveNewGoogleUserAccountEntity(
                        accessToken,
                        refreshToken,
                        authorizationCode,
                        provider,
                        google.plusOperations().getGoogleProfile());

                /** Copy to user profile. */
                userProfile = connectionService.copyToUserProfile(google.plusOperations().getGoogleProfile(), userAccount);
                accountService.save(userProfile);
                accountService.createNewAccount(userAccount);
                accountService.createPreferences(userProfile);
                updateUserIdWithEmailWhenPresent(userAccount, userProfile);
            } catch (DataIntegrityViolationException e) {
                if (userAccount != null) {
                    LOG.error("Account already exists rid={}", userAccount.getReceiptUserId());
                    accountService.deleteAllWhenAccountCreationFailedDueToDuplicate(userAccount, e);
                }
                throw new UserAccountDuplicateException("Found existing user with similar login");
            }
        } else {
            userAccount = accountService.findByProviderUserId(googleProfileId);
            if (null == userAccount) {
                LOG.warn("Account already exists rid={} email={} pid={}",
                        userProfile.getReceiptUserId(), userProfile.getEmail(), userProfile.getProviderId());
                throw new UserAccountDuplicateException("Found existing user with similar login");
            } else {
                LOG.info("access token different between old and new",
                        StringUtils.difference(userAccount.getAccessToken(), accessToken));
                userAccount.setAccessToken(accessToken);
                userAccount.setAuthorizationCode(authorizationCode);
                userAccount.setRefreshToken(refreshToken);
                accountService.saveUserAccount(userAccount);
            }
        }

        userConnectionRepository = socialConfig.usersConnectionRepository();
        connectionRepository = userConnectionRepository.createConnectionRepository(googleProfileId);
        connections = connectionRepository.findConnections(provider.name());
        Assert.isTrue(isConnectionPopulated(connections, provider.name()), "connection repository size is zero");
        socialConfig.mongoConnectionService().update(googleProfileId, connections.get(0));
        return userAccount;
    }

    /**
     * Any failure in the process of creating UserProfile, delete all reminiscence of that user including user account.
     */
    private void createOrSaveUserProfile(
            UserAccountEntity userAccount,
            UserProfileEntity userProfile,
            boolean createProfilePreference
    ) {
        try {
            accountService.save(userProfile);
            if (createProfilePreference) {
                accountService.createPreferences(userProfile);
            }
        } catch (Exception e) {
            LOG.error("Something went wrong in creating user profile email={} rid={} userAccount={} userProfile={} reason={}",
                    userProfile.getEmail(), userAccount.getReceiptUserId(), userAccount.getId(), userProfile.getId(), e.getLocalizedMessage(), e);

            /**
             * Keep this code commented out since we are not sure if this will happen how often. Note: If there is any
             * error in saving user profile, this will delete all the user information, billing and account. It would be
             * nice to know if the account is newly created or old. Better to check user account create time stamp is
             * less than a minute to decide in deleting. Could opt of softdelete instead.
             */

            /*List<ExpenseTagEntity> expenseTagEntities = expensesService.getAllExpenseTypes(userAccount.getReceiptUserId());
            expenseTagEntities.forEach(expenseTagManager::deleteHard);

            userAuthenticationManager.deleteHard(userAccount.getUserAuthentication());
            userAccountManager.deleteHard(userAccount);
            billingService.deleteHardBillingWhenAccountCreationFails(userProfile.getReceiptUserId());
            if (StringUtils.isNotBlank(userProfile.getId())) {
                userProfileManager.deleteHard(userProfile);
            }*/

            throw new RuntimeException("Something went wrong and we failed to create or save userProfile. Please bear with us until an engineer looks into this issue.");
        }
    }

    /**
     * Replaces userId number with email if exists. Social providers provides Id when email is not shared or user
     * email is not verified.
     *
     * @param userAccount
     * @param userProfile
     */
    public void updateUserIdWithEmailWhenPresent(UserAccountEntity userAccount, UserProfileEntity userProfile) {
        try {
            if (StringUtils.isNotBlank(userProfile.getEmail())) {
                if (StringUtils.equalsIgnoreCase(userAccount.getUserId(), userProfile.getEmail())) {
                    LOG.debug("Found matching userId and mail address, skipping update");
                } else {
                    LOG.debug("About to update userId={} with email={}", userAccount.getUserId(), userProfile.getEmail());
                    userAccount.setUserId(userProfile.getEmail());
                    accountService.save(userAccount);
                }
            } else {
                LOG.debug("found empty email, skipping update");
            }
        } catch (DataIntegrityViolationException e) {
            LOG.error(
                    "Account already exists userId={} with email={} reason={}",
                    userAccount.getUserId(),
                    userProfile.getEmail(),
                    e.getLocalizedMessage(),
                    e
            );
            throw new UserAccountDuplicateException("Found existing user with similar login", e);
        }
    }

    /**
     * Save UserAccountEntity when user signs up from mobile using Facebook provider.
     *
     * @param accessToken
     * @param provider
     * @param facebookProfile
     * @return
     */
    private UserAccountEntity saveNewFacebookUserAccountEntity(
            String accessToken,
            ProviderEnum provider,
            User facebookProfile
    ) {
        UserAccountEntity userAccount = UserAccountEntity.newInstance(
                generateUserIdManager.getNextAutoGeneratedUserId(),
                facebookProfile.getId(),
                facebookProfile.getFirstName(),
                facebookProfile.getLastName(),
                UserAuthenticationEntity.blankInstance()
        );
        userAccount.setProviderId(provider);
        userAccount.setProviderUserId(facebookProfile.getId());
        userAccount.setAccessToken(accessToken);
        userAccount.setProfileUrl(facebookProfile.getLink());

        /** Social account, hence account is considered validated by default. */
        userAccount.setAccountValidatedBeginDate();
        userAccount.setAccountValidated(true);
        return userAccount;
    }

    /**
     * Save UserAccountEntity when user signs up from mobile using Google provider.
     *
     * @param accessToken
     * @param refreshToken
     * @param authorizationCode
     * @param provider
     * @param person
     * @return
     */
    private UserAccountEntity saveNewGoogleUserAccountEntity(
            String accessToken,
            String refreshToken,
            String authorizationCode,
            ProviderEnum provider,
            Person person
    ) {
        UserAccountEntity userAccount = UserAccountEntity.newInstance(
                generateUserIdManager.getNextAutoGeneratedUserId(),
                person.getId(),
                person.getGivenName(),
                person.getFamilyName(),
                UserAuthenticationEntity.blankInstance()
        );
        //TODO(hth) save offline access key created by google
        userAccount.setProviderId(provider);
        userAccount.setProviderUserId(person.getId());
        userAccount.setDisplayName(person.getDisplayName());
        userAccount.setProfileUrl(person.getUrl());
        userAccount.setImageUrl(person.getImageUrl());
        userAccount.setAccessToken(accessToken);
        userAccount.setRefreshToken(refreshToken);
        userAccount.setAuthorizationCode(authorizationCode);

        /** Social account, hence account is considered validated by default. */
        userAccount.setAccountValidatedBeginDate();
        userAccount.setAccountValidated(true);
        return userAccount;
    }

    /**
     * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
     * Basically, this interprets the access value whether it's for a regular user or admin.
     *
     * @param roles
     * @return collection of granted authorities
     */
    public Collection<? extends GrantedAuthority> getAuthorities(Set<RoleEnum> roles) {
        List<GrantedAuthority> authList = new ArrayList<>(RoleEnum.values().length);
        authList.addAll(roles.stream().map(roleEnum -> new SimpleGrantedAuthority(roleEnum.name())).collect(Collectors.toList()));
        return authList;
    }

    private boolean isConnectionPopulated(List<Connection<?>> connections, String pid) {
        if (connections.isEmpty()) {
            LOG.warn("connection repository size is zero for pid={}", pid);
            return false;
        }
        return true;
    }
}
