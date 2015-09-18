package com.receiptofi.social.connect;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.RoleEnum;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.FriendService;
import com.receiptofi.social.UserAccountDuplicateException;
import com.receiptofi.social.annotation.Social;
import com.receiptofi.social.config.ProviderConfig;
import com.receiptofi.social.service.CustomUserDetailsService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.RandomString;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
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
import org.springframework.social.google.api.plus.PeoplePage;
import org.springframework.social.google.api.plus.Person;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Autowired private GenerateUserIdManager generateUserIdManager;
    @Autowired private AccountService accountService;
    @Autowired private ProviderConfig providerConfig;
    @Autowired private UserAccountManager userAccountManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private FriendService friendService;

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
     * Called when the provider userId i.e puid does not exists in DB. Invoked only by website social login for the
     * first time. Not invoked by mobile sign up.
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
                /** Social account, hence account is considered validated by default. */
                userAccount.setAccountValidatedBeginDate();
                userAccount.setAccountValidated(true);
                accountService.createNewAccount(userAccount);

                userProfile = copyToUserProfile(user, userAccount);
                accountService.save(userProfile);
                accountService.createPreferences(userProfile);

                /** Set the blank names in UserAccount from UserProfile. */
                userAccount.setFirstName(userProfile.getFirstName());
                userAccount.setLastName(userProfile.getLastName());
                customUserDetailsService.updateUserIdWithEmailWhenPresent(userAccount, userProfile);
            } else if (userProfile.getProviderId() != ProviderEnum.FACEBOOK) {
                LOG.info("Account already exists rid={} email={} pid={}",
                        userProfile.getReceiptUserId(), userProfile.getEmail(), userProfile.getProviderId());
                throw new UserAccountDuplicateException("Found existing user with similar login");
            }
        } else {
            Google google = getGoogle(userAccountFromConnection);
            Person person = getGooglePerson(google);
            UserProfileEntity userProfile = userProfileManager.findByEmail(person.getAccountEmail());

            if (userProfile == null) {
                UserAccountEntity userAccount = createUserAccount(userId, userConn);
                /** Social account, hence account is considered validated by default. */
                userAccount.setAccountValidatedBeginDate();
                userAccount.setAccountValidated(true);
                accountService.createNewAccount(userAccount);

                userProfile = copyToUserProfile(person, userAccount);
                accountService.save(userProfile);
                accountService.createPreferences(userProfile);

                /** Set the blank names in UserAccount from UserProfile. */
                userAccount.setFirstName(userProfile.getFirstName());
                userAccount.setLastName(userProfile.getLastName());
                customUserDetailsService.updateUserIdWithEmailWhenPresent(userAccount, userProfile);
            } else if (userProfile.getProviderId() != ProviderEnum.GOOGLE) {
                LOG.info("Account already exists rid={} email={} pid={}",
                        userProfile.getReceiptUserId(), userProfile.getEmail(), userProfile.getProviderId());
                throw new UserAccountDuplicateException("Found existing user with similar login");
            }
        }
    }

    private Facebook getFacebook(UserAccountEntity userAccountFromConnection) {
        return new FacebookTemplate(userAccountFromConnection.getAccessToken(), "");
    }

    private Facebook getFacebook(String accessToken) {
        return new FacebookTemplate(accessToken, "");
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

                /**
                 * Rare and one time execution for if condition below.
                 *
                 * This happens when this user was added as a friend. User as a friend from Facebook would be added
                 * only when this user has been using the same app and is not signed up. That would not happen.
                 *
                 * The likelihood of this happening when user has already signed is next to zero. Only happens when
                 * testing app using facebook test users. To avoid this condition delete app access from test user app
                 * settings.
                 */
                if (StringUtils.isBlank(userAccount.getAccessToken()) &&
                        userAccount.getProviderId() != null &&
                        !userAccount.isActive()) {

                    /**
                     * Payment should not be decided on account active or in-active,
                     * instead should be based on BillingAccount
                     */
                    userAccount.active();

                    LOG.info("Pending user just signed up for first time. rid={}", userAccount.getReceiptUserId());
                }

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
        /** Get user matching RID, even when user is in-active. */
        UserProfileEntity userProfile = userProfileManager.forProfilePreferenceFindByReceiptUserId(userAccount.getReceiptUserId());
        Assert.notNull(userProfile, "UserProfile is null for rid=" + userAccount.getReceiptUserId());
        if (DateUtil.getDuration(userProfile.getUpdated(), new Date()) > lastFetched) {
            if (ProviderEnum.valueOf(userConn.getKey().getProviderId().toUpperCase()) == ProviderEnum.FACEBOOK) {
                Facebook facebook = getFacebook(userAccountFromConnection);
                User user = getFacebookUser(facebook);
                userProfile = copyToUserProfile(user, userAccount);
                accountService.save(userProfile);

                /** Update with latest names in UserAccount from UserProfile. */
                updateUserAccountName(userAccount, userProfile);
                customUserDetailsService.updateUserIdWithEmailWhenPresent(userAccount, userProfile);
            } else {
                Google google = getGoogle(userAccountFromConnection);
                Person person = getGooglePerson(google);
                userProfile = copyToUserProfile(person, userAccount);
                accountService.save(userProfile);

                /** Update with latest names in UserAccount from UserProfile. */
                updateUserAccountName(userAccount, userProfile);
                customUserDetailsService.updateUserIdWithEmailWhenPresent(userAccount, userProfile);
            }
        } else {
            LOG.info("Skipped social userProfile update as it was last fetched within seconds={}", lastFetched);
        }

        if (providerConfig.isPopulateSocialFriendOn()) {
            if (ProviderEnum.valueOf(userConn.getKey().getProviderId().toUpperCase()) == ProviderEnum.FACEBOOK) {
                Facebook facebook = getFacebook(userAccountFromConnection);
                populateFacebookFriends(userAccount, facebook);
            } else {
                Google google = getGoogle(userAccountFromConnection);
                populateGoogleFriends(userAccount, google);
            }
        }

    }

    /**
     * Updates with latest names in UserAccount from UserProfile.
     *
     * @param userAccount
     * @param userProfile
     */
    private void updateUserAccountName(UserAccountEntity userAccount, UserProfileEntity userProfile) {
        if (StringUtils.equalsIgnoreCase(userAccount.getFirstName(), userProfile.getFirstName())
                && StringUtils.equalsIgnoreCase(userAccount.getLastName(), userProfile.getLastName())) {

            LOG.debug("Found matching first and last name");
        } else {
            userAccount.setFirstName(userProfile.getFirstName());
            userAccount.setLastName(userProfile.getLastName());
            userAccountManager.save(userAccount);
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
                                            where("UID").is(facebookUserProfile.getId()),
                                            where("UID").is(facebookUserProfile.getEmail())
                                    )
                    ),
                    UserAccountEntity.class
            );

            /**
             * Rare execution for if condition below.
             *
             * This happens when this user was added as a friend. User as a friend from Facebook would be added
             * only when this user has been using the same app and is not signed up. That would not happen.
             *
             * The likelihood of this happening when user has already signed is next to zero. Only happens when
             * testing app using facebook test users. To avoid this condition delete app access from test user app
             * settings.
             */
            if (null == userAccountEntity) {
                userAccountEntity = UserAccountEntity.newInstance(
                        generateUserIdManager.getNextAutoGeneratedUserId(),
                        facebookUserProfile.getId(),
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        UserAuthenticationEntity.blankInstance()
                );
                /** Inactive because friend and has not signed up. */
                userAccountEntity.inActive();
                userAccountEntity.setProviderId(ProviderEnum.FACEBOOK);
                userAccountEntity.setProviderUserId(facebookUserProfile.getId());

                /** Social account, hence account is considered validated by default. */
                userAccountEntity.setAccountValidatedBeginDate();
                userAccountEntity.setAccountValidated(true);
                accountService.createNewAccount(userAccountEntity);

                UserProfileEntity userProfile = copyToUserProfile(facebookUserProfile, userAccountEntity);
                accountService.save(userProfile);
                accountService.createPreferences(userProfile);

                /** Set the blank names in UserAccount from UserProfile. */
                userAccountEntity.setFirstName(userProfile.getFirstName());
                userAccountEntity.setLastName(userProfile.getLastName());
                userAccountEntity.setDisplayName(userProfile.getName());
                userAccountEntity.setProfileUrl(userProfile.getLink());
                customUserDetailsService.updateUserIdWithEmailWhenPresent(userAccount, userProfile);
                accountService.save(userAccountEntity);

                LOG.info("Added friend rid={} provider={}", userAccountEntity.getReceiptUserId(), ProviderEnum.FACEBOOK);
            }

            if (!friendService.hasConnection(userAccount.getReceiptUserId(), userAccountEntity.getReceiptUserId())) {
                FriendEntity friend = new FriendEntity(userAccount.getReceiptUserId(), userAccountEntity.getReceiptUserId());
                friendService.save(friend);
            }
        }
    }

    private void populateGoogleFriends(UserAccountEntity userAccount, Google google) {
        PeoplePage people = google.plusOperations().getPeopleInCircles("me", null);
        List<Person> persons = people.getItems();
        for (Person person : persons) {
            LOG.warn("Missing Google get friends");
        }

        // XXX TODO page Circle to get all the users in the circle
        LOG.warn("Missing Google get friends");
    }

    public UserProfileEntity copyToUserProfile(User facebookUserProfile, UserAccountEntity userAccount) {
        LOG.info("copying facebookUserProfile to userProfile for userAccount={}", userAccount.getReceiptUserId());
        UserProfileEntity userProfile = userProfileManager.findByProviderUserId(facebookUserProfile.getId());
        if (null == userProfile) {
            userProfile = new UserProfileEntity();
        } else {
            userProfile.setUpdated();
        }

        deepCopy(facebookUserProfile, userProfile);

        String id = userProfile.getId();
        if (StringUtils.isEmpty(userProfile.getBirthday())) {
            int minAge = facebookUserProfile.getAgeRange().getMin();
            LocalDate birth = LocalDate.now().minusYears(minAge).with(TemporalAdjusters.firstDayOfYear());
            userProfile.setBirthday(DB_FORMATTER.format(birth));
        }
        userProfile.setProviderUserId(facebookUserProfile.getId());
        userProfile.setProviderId(ProviderEnum.FACEBOOK);
        userProfile.setReceiptUserId(userAccount.getReceiptUserId());
        userProfile.setId(id);
        if (userAccount.isActive()) {
            userProfile.active();
        } else {
            userProfile.inActive();
        }
        return userProfile;
    }

    public UserProfileEntity copyToUserProfile(Person googleUserProfile, UserAccountEntity userAccount) {
        LOG.debug("copying googleUserProfile to userProfile for userAccount={}", userAccount.getReceiptUserId());
        UserProfileEntity userProfile = userProfileManager.findByProviderUserId(googleUserProfile.getId());
        if (null == userProfile) {
            userProfile = new UserProfileEntity();
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
        return userProfile;
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
