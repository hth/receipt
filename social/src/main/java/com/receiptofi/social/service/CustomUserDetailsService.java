package com.receiptofi.social.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.LoginService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.social.UserAccountDuplicateException;
import com.receiptofi.social.annotation.Social;
import com.receiptofi.social.config.SocialConfig;
import com.receiptofi.social.connect.ConnectionService;
import com.receiptofi.utils.RandomString;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
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

    @Autowired private LoginService loginService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private SocialConfig socialConfig;
    @Autowired private AccountService accountService;
    @Autowired private ConnectionService connectionService;
    @Autowired private GenerateUserIdManager generateUserIdManager;
    @Autowired private GoogleAccessTokenService googleAccessTokenService;

    @Value ("${mail.validation.timeout.period}")
    private int mailValidationTimeoutPeriod;

    @Value ("${CustomUserDetailsService.account.not.validated.message}")
    private String accountNotValidatedMessage;

    /**
     * @param email - lower case string
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOG.info("login attempted user={}", email);

        //Always check user login with lower letter email case
        UserProfileEntity userProfile = userProfilePreferenceService.findByEmail(email);
        if (null == userProfile) {
            LOG.warn("not found user={}", email);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = loginService.findByReceiptUserId(userProfile.getReceiptUserId());
            LOG.warn("user={} accountValidated={}", userAccount.getReceiptUserId(), userAccount.isAccountValidated());

            boolean condition = isUserActiveAndRegistrationTurnedOn(userAccount, userProfile);
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
    private boolean isUserActiveAndRegistrationTurnedOn(UserAccountEntity userAccount, UserProfileEntity userProfile) {
        if (userAccount.isRegisteredWhenRegistrationIsOff()) {
            throw new RuntimeException("Registration is turned off. We will notify you on your registered email " +
                    (StringUtils.isNotBlank(userProfile.getEmail()) ? "<b>" + userProfile.getEmail() + "</b>" : "") +
                    " when we start accepting new users.");
        } else if (userAccount.isActive()) {
            if (userAccount.isAccountValidated() ||
                    userAccount.isAccountNotValidatedBeyondSelectedDays(mailValidationTimeoutPeriod)) {
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
        } else {
            LOG.error("Reached condition for invalid account rid={}", userAccount.getReceiptUserId());
            return false;
        }
    }

    @Social
    public UserDetails loadUserByUserId(String uid) throws UsernameNotFoundException {
        LOG.info("login through Provider user={}", uid);

        UserProfileEntity userProfile = userProfilePreferenceService.findByUserId(uid);
        if (null == userProfile) {
            LOG.warn("not found user={}", uid);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = loginService.findByReceiptUserId(userProfile.getReceiptUserId());
            LOG.warn("user={} accountValidated={}", userAccount.getReceiptUserId(), userAccount.isAccountValidated());

            boolean condition = isUserActiveAndRegistrationTurnedOn(userAccount, userProfile);
            return new ReceiptUser(
                    StringUtils.isBlank(userAccount.getUserId()) ? userProfile.getUserId() : userAccount.getUserId(),
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
            LOG.error("duplicate account error pid={} reason={}", provider, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    private UserAccountEntity connectFacebook(ProviderEnum provider, String accessToken) {
        UserAccountEntity userAccount;
        UsersConnectionRepository userConnectionRepository;
        ConnectionRepository connectionRepository;
        List<Connection<?>> connections;
        Facebook facebook = new FacebookTemplate(accessToken);
        String facebookProfileId = facebook.userOperations().getUserProfile().getId();
        LOG.debug("facebook profile mail={}", facebook.userOperations().getUserProfile().getEmail());

        userAccount = accountService.findByProviderUserId(facebookProfileId);
        if (null == userAccount) {
            userAccount = saveNewFacebookUserAccountEntity(
                    accessToken,
                    provider,
                    facebook.userOperations().getUserProfile());
        } else {
            LOG.info("access token different between old and new",
                    StringUtils.difference(userAccount.getAccessToken(), accessToken));
            userAccount.setAccessToken(accessToken);
            accountService.saveUserAccount(userAccount);
        }

        userConnectionRepository = socialConfig.usersConnectionRepository();
        connectionRepository = userConnectionRepository.createConnectionRepository(facebookProfileId);
        connections = connectionRepository.findConnections(provider.name());
        Assert.isTrue(isConnectionPopulated(connections, provider.name()), "connection repository size is zero");
        socialConfig.mongoConnectionService().update(facebookProfileId, connections.get(0));
        return userAccount;
    }

    /**
     * @param provider
     * @param authorizationCode
     * @param accessToken
     * @param refreshToken      Always store user refresh tokens. If your application needs a new refresh token it must
     *                          sent a request with the approval_prompt query parameter set to force. This will cause
     *                          the user to see a dialog to grant permission to your application again.
     * @return
     * @link https://developers.google.com/glass/develop/mirror/authorization
     */
    private UserAccountEntity connectGoogle(
            ProviderEnum provider,
            String authorizationCode,
            String accessToken,
            String refreshToken
    ) {
        UserAccountEntity userAccount;
        UsersConnectionRepository userConnectionRepository;
        ConnectionRepository connectionRepository;
        List<Connection<?>> connections;
        Google google = new GoogleTemplate(accessToken);
        String googleProfileId = google.plusOperations().getGoogleProfile().getId();
        LOG.debug("google profile mail={}", google.plusOperations().getGoogleProfile().getAccountEmail());

        userAccount = accountService.findByProviderUserId(googleProfileId);
        if (null == userAccount) {
            userAccount = saveNewGoogleUserAccountEntity(
                    accessToken,
                    refreshToken,
                    authorizationCode,
                    provider,
                    google.plusOperations().getGoogleProfile());
        } else {
            LOG.info("access token different between old and new",
                    StringUtils.difference(userAccount.getAccessToken(), accessToken));
            userAccount.setAccessToken(accessToken);
            userAccount.setAuthorizationCode(authorizationCode);
            userAccount.setRefreshToken(refreshToken);
            accountService.saveUserAccount(userAccount);
        }

        userConnectionRepository = socialConfig.usersConnectionRepository();
        connectionRepository = userConnectionRepository.createConnectionRepository(googleProfileId);
        connections = connectionRepository.findConnections(provider.name());
        Assert.isTrue(isConnectionPopulated(connections, provider.name()), "connection repository size is zero");
        socialConfig.mongoConnectionService().update(googleProfileId, connections.get(0));
        return userAccount;
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
            FacebookProfile facebookProfile
    ) {
        UserAuthenticationEntity userAuthentication = accountService.getUserAuthenticationEntity(
                RandomString.newInstance().nextString()
        );

        UserAccountEntity userAccount = UserAccountEntity.newInstance(
                generateUserIdManager.getNextAutoGeneratedUserId(),
                facebookProfile.getId(),
                facebookProfile.getFirstName(),
                facebookProfile.getLastName(),
                userAuthentication
        );
        userAccount.setProviderId(provider);
        userAccount.setProviderUserId(facebookProfile.getId());
        userAccount.setAccessToken(accessToken);
        userAccount.setProfileUrl(facebookProfile.getLink());
        accountService.saveUserAccount(userAccount);

        //save profile
        connectionService.copyAndSaveFacebookToUserProfile(facebookProfile, userAccount);

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
        UserAuthenticationEntity userAuthentication = accountService.getUserAuthenticationEntity(
                RandomString.newInstance().nextString()
        );

        UserAccountEntity userAccount = UserAccountEntity.newInstance(
                generateUserIdManager.getNextAutoGeneratedUserId(),
                person.getId(),
                person.getGivenName(),
                person.getFamilyName(),
                userAuthentication
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
        accountService.saveUserAccount(userAccount);

        //save profile
        connectionService.copyAndSaveGoogleToUserProfile(person, userAccount);

        return userAccount;
    }

    /**
     * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
     * Basically, this interprets the access value whether it's for a regular user or admin.
     *
     * @param roles
     * @return collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Set<RoleEnum> roles) {
        List<GrantedAuthority> authList = new ArrayList<>(4);

        for (RoleEnum roleEnum : roles) {
            authList.add(new SimpleGrantedAuthority(roleEnum.name()));
        }

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
