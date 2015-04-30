package com.receiptofi.social.connect;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.RegistrationService;
import com.receiptofi.social.UserAccountDuplicateException;
import com.receiptofi.social.annotation.Social;
import com.receiptofi.social.config.ProviderConfig;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.RandomString;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Organization;
import org.springframework.social.google.api.plus.Person;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Social
public class ConnectionServiceImpl implements ConnectionService {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionServiceImpl.class);

    private MongoTemplate mongoTemplate;
    private ConnectionConverter connectionConverter;

    @Autowired private GenerateUserIdManager generateUserIdManager;
    @Autowired private AccountService accountService;
    @Autowired private RegistrationService registrationService;
    @Autowired private ProviderConfig providerConfig;
    @Autowired private UserAccountManager userAccountManager;
    @Autowired private UserProfileManager userProfileManager;

    @Value ("${social.profile.lastFetched:60}")
    private long lastFetched;

    @Autowired
    public ConnectionServiceImpl(
            MongoTemplate mongoTemplate,
            ConnectionConverter connectionConverter
    ) {
        this.mongoTemplate = mongoTemplate;
        this.connectionConverter = connectionConverter;
    }

    /**
     * This is called when the provider userId i.e puid does not exists in DB.
     *
     * @param userId
     * @param userConn
     */
    public void create(String userId, Connection<?> userConn) {
        UserAccountEntity userAccountFromConnection = connectionConverter.convert(userId, userConn);
        if (ProviderEnum.valueOf(userConn.getKey().getProviderId().toUpperCase()) == ProviderEnum.FACEBOOK) {
            Facebook facebook = getFacebook(userAccountFromConnection);
            User user = getFacebookUser(facebook);
            UserProfileEntity userProfile = userProfileManager.findByEmail(user.getEmail());

            if (userProfile == null) {
                UserAccountEntity userAccount = createUserAccount(userId, userConn);
                accountService.createNewAccount(userAccount);

                copyToUserProfile(user, userAccount);
            } else {
                LOG.info("Account already exists rid={} email={} pid={}",
                        userProfile.getReceiptUserId(), userProfile.getEmail(), userProfile.getProviderId());
            }
        } else {
            Google google = getGoogle(userAccountFromConnection);
            Person person = getGooglePerson(google);
            UserProfileEntity userProfile = userProfileManager.findByEmail(person.getAccountEmail());

            if (userProfile == null) {
                UserAccountEntity userAccount = createUserAccount(userId, userConn);
                accountService.createNewAccount(userAccount);

                copyToUserProfile(person, userAccount);
            } else {
                LOG.info("Account already exists rid={} email={} pid={}",
                        userProfile.getReceiptUserId(), userProfile.getEmail(), userProfile.getProviderId());
            }
        }
    }

    private Facebook getFacebook(UserAccountEntity userAccountFromConnection) {
        return new FacebookTemplate(userAccountFromConnection.getAccessToken(), "notfoundexception");
    }

    private User getFacebookUser(Facebook facebook) {
        return facebook.userOperations().getUserProfile();
    }

    private Google getGoogle(UserAccountEntity userAccountFromConnection) {
        return new GoogleTemplate(userAccountFromConnection.getAccessToken());
    }

    private Person getGooglePerson(Google google) {
        return google.plusOperations().getGoogleProfile();
    }

    /**
     * Create UserAccount when the user does not exists.
     *
     * @param userId
     * @param userConn
     * @return
     */
    private UserAccountEntity createUserAccount(String userId, Connection<?> userConn) {
        return connectionConverter.convert(
                userId,
                generateUserIdManager.getNextAutoGeneratedUserId(),
                userConn
        );
    }

    public void update(String userId, Connection<?> userConn) {
        UserAccountEntity userAccountFromConnection = connectionConverter.convert(userId, userConn);
        LOG.info("populated userAccountFromConnection={}", userAccountFromConnection);

        UserAccountEntity userAccount = getUserAccountEntity(
                userId,
                userAccountFromConnection.getProviderId(),
                userAccountFromConnection.getProviderUserId()
        );
        LOG.info("fetched userAccount={}", userAccount);
        if (userAccount != null) {
            if (DateUtil.getDuration(userAccount.getUpdated(), new Date()) > lastFetched) {
                LOG.info("fetched before save userAccount={}", userAccount);

                userAccount.setDisplayName(userAccountFromConnection.getDisplayName());
                userAccount.setProfileUrl(userAccountFromConnection.getProfileUrl());
                userAccount.setExpireTime(userAccountFromConnection.getExpireTime());
                userAccount.setAccessToken(userAccountFromConnection.getAccessToken());
                userAccount.setImageUrl(userAccountFromConnection.getImageUrl());

                userAccount.setUpdated();
                userAccountManager.save(userAccount);
            } else {
                LOG.info("Skipped social userAccount update as it was last fetched within seconds={}", lastFetched);
            }
        } else {
            UserAuthenticationEntity userAuthentication = accountService.getUserAuthenticationEntity(
                    RandomString.newInstance().nextString()
            );
            userAccountFromConnection.setUserAuthentication(userAuthentication);
            userAccountManager.save(userAccountFromConnection);
        }

        Assert.notNull(userAccount, "UserAccount is null before social profile update.");
        UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(userAccount.getReceiptUserId());
        if (DateUtil.getDuration(userProfile.getUpdated(), new Date()) > lastFetched) {
            if (ProviderEnum.valueOf(userConn.getKey().getProviderId().toUpperCase()) == ProviderEnum.FACEBOOK) {
                Facebook facebook = getFacebook(userAccountFromConnection);
                User user = getFacebookUser(facebook);
                copyToUserProfile(user, userAccount);

                if (providerConfig.isPopulateSocialFriendOn()) {
                    populateFacebookFriends(userAccount, facebook);
                }
            } else {
                Google google = getGoogle(userAccountFromConnection);
                Person person = getGooglePerson(google);
                copyToUserProfile(person, userAccount);

                if (providerConfig.isPopulateSocialFriendOn()) {
                    // XXX TODO page Circle to get all the users in the circle
                    LOG.warn("Missing Google get friends");
                }
            }
        } else {
            LOG.info("Skipped social userProfile update as it was last fetched within seconds={}", lastFetched);
        }
    }

    /**
     * //TODO this method should be pushed down to cron job; as this consumes time
     * Populates friends from Facebook
     *
     * @param userAccount
     * @param facebook
     */
    private void populateFacebookFriends(UserAccountEntity userAccount, Facebook facebook) {
        List<User> profiles = facebook.friendOperations().getFriendProfiles();
        for (User facebookUserProfile : profiles) {
            UserAccountEntity userAccountEntity = mongoTemplate.findOne(
                    query(new Criteria()
                                    .orOperator(
                                            where("PUID").is(facebookUserProfile.getId()),
                                            where("UID").is(facebookUserProfile.getId())
                                    )
                    ),
                    UserAccountEntity.class
            );
            if (null == userAccountEntity) {
                userAccountEntity = UserAccountEntity.newInstance(
                        generateUserIdManager.getNextAutoGeneratedUserId(),
                        facebookUserProfile.getId(),
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        UserAuthenticationEntity.blankInstance()
                );
                userAccountEntity.inActive();
                //userAccountEntity.setDeleted(false);

                UserAuthenticationEntity userAuthentication = accountService.getUserAuthenticationEntity(
                        RandomString.newInstance().nextString()
                );
                userAccountEntity.setUserAuthentication(userAuthentication);
                registrationService.isRegistrationAllowed(userAccount);
                LOG.info("new account created user={} provider={}",
                        userAccountEntity.getReceiptUserId(), ProviderEnum.FACEBOOK);
            } else {
                userAccountEntity.setUpdated();
            }
            userAccountEntity.setUserId(facebookUserProfile.getId());
            userAccountEntity.setProviderId(ProviderEnum.FACEBOOK);
            userAccountEntity.setProviderUserId(facebookUserProfile.getId());

            userAccountManager.save(userAccountEntity);
            copyToUserProfile(facebookUserProfile, userAccountEntity);
        }
    }

    public void copyToUserProfile(User facebookUserProfile, UserAccountEntity userAccount) {
        LOG.info("copying facebookUserProfile to userProfile for userAccount={}", userAccount.getReceiptUserId());
        UserProfileEntity userProfile = userProfileManager.findByProviderUserId(facebookUserProfile.getId());
        boolean createProfilePreference = false;
        if (null == userProfile) {
            userProfile = new UserProfileEntity();
            createProfilePreference = true;
        } else {
            userProfile.setUpdated();
        }

        deepCopy(facebookUserProfile, userProfile);

        String id = userProfile.getId();
        userProfile.setProviderUserId(facebookUserProfile.getId());
        userProfile.setProviderId(ProviderEnum.FACEBOOK);
        userProfile.setReceiptUserId(userAccount.getReceiptUserId());
        userProfile.setId(id);
        if (userAccount.isActive()) {
            userProfile.active();
        } else {
            userProfile.inActive();
        }
        userProfileManager.save(userProfile);
        if (createProfilePreference) {
            accountService.createPreferences(userProfile);
        }

        //TODO(hth) think about moving this up in previous method call
        updateUserIdWithEmailWhenPresent(userAccount, userProfile);
    }

    public void copyToUserProfile(Person googleUserProfile, UserAccountEntity userAccount) {
        LOG.debug("copying googleUserProfile to userProfile for userAccount={}", userAccount.getReceiptUserId());
        UserProfileEntity userProfile = userProfileManager.findByProviderUserId(googleUserProfile.getId());
        boolean createProfilePreference = false;
        if (null == userProfile) {
            userProfile = new UserProfileEntity();
            createProfilePreference = true;
        } else {
            userProfile.setUpdated();
        }

        deepCopy(googleUserProfile, userProfile);

        String id = userProfile.getId();
        userProfile.setProviderUserId(googleUserProfile.getId());
        userProfile.setProviderId(ProviderEnum.GOOGLE);
        userProfile.setReceiptUserId(userAccount.getReceiptUserId());
        userProfile.setId(id);
        if (userAccount.isActive()) {
            userProfile.active();
        } else {
            userProfile.inActive();
        }
        userProfileManager.save(userProfile);
        if (createProfilePreference) {
            accountService.createPreferences(userProfile);
        }

        //TODO(hth) think about moving this up in previous method call
        updateUserIdWithEmailWhenPresent(userAccount, userProfile);
    }

    private void deepCopy(User facebookUserProfile, UserProfileEntity userProfile) {
        try {
            BeanUtils.copyProperties(userProfile, facebookUserProfile);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    private void deepCopy(Person googleUserProfile, UserProfileEntity userProfile) {
        userProfile.setFirstName(googleUserProfile.getGivenName());
        userProfile.setLastName(googleUserProfile.getFamilyName());
        userProfile.setName(googleUserProfile.getDisplayName());
        userProfile.setLink(googleUserProfile.getUrl());
        //skipped thumbnailURL; this can be found in user account entity
        userProfile.setBirthday(googleUserProfile.getBirthday() == null ? null : googleUserProfile.getBirthday().toString());
        userProfile.setGender(googleUserProfile.getGender() == null ? null : googleUserProfile.getGender());
        //skipped occupation
        userProfile.setAbout(googleUserProfile.getAboutMe() == null ? null : googleUserProfile.getAboutMe());
        userProfile.setRelationshipStatus(googleUserProfile.getRelationshipStatus() == null ? null : googleUserProfile.getRelationshipStatus());
        //skipped urls
        if (googleUserProfile.getOrganizations() != null) {
            for (Organization organization : googleUserProfile.getOrganizations()) {
                Reference reference = new Reference(organization.getName());
                //TODO fix me or check me out when Google Auth bug is fixed
//                WorkEntry workEntry = new WorkEntry(
//                        reference,
//                        organization.getStartDate(),
//                        organization.getEndDate()
//                );
//                userProfile.addWork(workEntry);
            }
        }

        if (googleUserProfile.getPlacesLived() != null) {
            Set<String> places = googleUserProfile.getPlacesLived().keySet();
            places.stream().filter(value -> googleUserProfile.getPlacesLived().get(value)).forEach(value -> {
                Reference reference = new Reference(value);
                userProfile.setLocation(reference);
            });
        }
        userProfile.setEmail(googleUserProfile.getAccountEmail());
    }

    /**
     * Replaces userId number with email if exists. Social providers provides Id when email is not shared.
     *
     * @param userAccount
     * @param userProfile
     */
    private void updateUserIdWithEmailWhenPresent(UserAccountEntity userAccount, UserProfileEntity userProfile) {
        try {
            if (StringUtils.isNotBlank(userProfile.getEmail())) {
                if (StringUtils.equalsIgnoreCase(userAccount.getUserId(), userProfile.getEmail())) {
                    LOG.debug("found matching userId and mail address, skipping update");
                } else {
                    LOG.debug("about to update userId={} with email={}", userAccount.getUserId(), userProfile.getEmail());
                    userAccount.setUserId(userProfile.getEmail());
                    userAccountManager.save(userAccount);
                }
            } else {
                LOG.debug("found empty email, skipping update");
            }
        } catch (DuplicateKeyException e) {
            LOG.error(
                    "account already exists userId={} with email={} reason={}",
                    userAccount.getUserId(),
                    userProfile.getEmail(),
                    e.getLocalizedMessage(),
                    e
            );
            throw new UserAccountDuplicateException("Found existing user with similar login", e);
        }
    }

    public void remove(String userId, ConnectionKey connectionKey) {
        Query q = query(where("UID").is(userId)
                .and("PID").is(connectionKey.getProviderId())
                .and("PUID").is(connectionKey.getProviderUserId()));
        mongoTemplate.remove(q, UserAccountEntity.class);
    }

    public void remove(String userId, ProviderEnum providerId) {
        Query q = query(where("UID").is(userId).and("PID").is(providerId));
        mongoTemplate.remove(q, UserAccountEntity.class);
    }

    public Connection<?> getPrimaryConnection(String userId, ProviderEnum providerId) {
        Query q = query(where("UID").is(userId).and("PID").is(providerId).and("RE").is(RoleEnum.ROLE_USER));
        UserAccountEntity mc = mongoTemplate.findOne(q, UserAccountEntity.class);
        return connectionConverter.convert(mc);
    }

    public Connection<?> getConnection(String userId, ProviderEnum providerId, String providerUserId) {
        UserAccountEntity mc = getUserAccountEntity(userId, providerId, providerUserId);
        return connectionConverter.convert(mc);
    }

    /**
     * Find if UserAccount exits for PID and PUID. PUID has preference over UID as PUID comes from provider
     *
     * @param userId
     * @param providerId
     * @param providerUserId
     * @return
     */
    private UserAccountEntity getUserAccountEntity(String userId, ProviderEnum providerId, String providerUserId) {
        Query q = query(where("UID").is(userId).and("PID").is(providerId));
        if (StringUtils.isNotBlank(providerUserId)) {
            q = query(where("PUID").is(providerUserId).and("PID").is(providerId));
        }
        return mongoTemplate.findOne(q, UserAccountEntity.class);
    }

    public List<Connection<?>> getConnections(String userId) {
        // select where userId = ? order by providerId, role
        Query q = query(where("PUID").is(userId));
        Sort sort = new Sort(Sort.Direction.ASC, "PID").and(new Sort(Sort.Direction.ASC, "RE"));
        return runQuery(q.with(sort));
    }

    public List<Connection<?>> getConnections(String userId, ProviderEnum providerId) {
        LOG.info("PUID={} PID={}", userId, providerId);
        Query q = query(where("PUID").is(userId).and("PID").is(providerId));
        Sort sort = new Sort(Sort.Direction.ASC, "RE");
        return runQuery(q.with(sort));
    }

    public List<Connection<?>> getConnections(String userId, MultiValueMap<String, String> providerUsers) {
        if (null == providerUsers || providerUsers.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
        }

        List<Criteria> lc = new ArrayList<>();
        for (Entry<String, List<String>> entry : providerUsers.entrySet()) {
            String providerId = entry.getKey();
            lc.add(where("PID").is(providerId).and("PUID").in(entry.getValue()));
        }

        Query query = query(where("UID").is(userId).orOperator(lc.toArray(new Criteria[lc.size()])));
        Sort sort = new Sort(Sort.Direction.ASC, "PID").and(new Sort(Sort.Direction.ASC, "RE"));
        return runQuery(query.with(sort));
    }

    public Set<String> getUserIds(ProviderEnum providerId, Set<String> providerUserIds) {
        Query q = query(where("PID").is(providerId).and("PUID").in(new ArrayList<>(providerUserIds)));
        q.fields().include("UID");

        List<UserAccountEntity> results = mongoTemplate.find(q, UserAccountEntity.class);
        return results.stream().map(UserAccountEntity::getUserId).collect(Collectors.toSet());
    }

    public List<String> getUserIds(ProviderEnum providerId, String providerUserId) {
        Query q = query(where("PID").is(providerId).and("PUID").is(providerUserId));
        q.fields().include("UID");

        List<UserAccountEntity> results = mongoTemplate.find(q, UserAccountEntity.class);
        return results.stream().map(UserAccountEntity::getUserId).collect(Collectors.toList());
    }

    private List<Connection<?>> runQuery(Query query) {
        List<UserAccountEntity> results = mongoTemplate.find(query, UserAccountEntity.class);
        return results.stream().map(connectionConverter::convert).collect(Collectors.toList());
    }
}
