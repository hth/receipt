package com.receiptofi.service;

import com.receiptofi.IntegrationTests;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.ForgotRecoverManagerImpl;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.GenerateUserIdManagerImpl;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAccountManagerImpl;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserAuthenticationManagerImpl;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserPreferenceManagerImpl;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.repository.UserProfileManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * User: hitender
 * Date: 2/25/14 1:02 AM
 */
@Category(IntegrationTests.class)
public class AccountServiceIntegrationTest extends RealMongoForTests {
    private String userProfileCollectionName = "USER_PROFILE";
    private DBCollection userProfileCollection;

    private String forgotRecoverCollectionName = "FORGOT_RECOVER";
    private DBCollection forgotRecoverCollection;

    private UserAccountManager userAccountManager;
    private UserAuthenticationManager userAuthenticationManager;
    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;
    private ForgotRecoverManager forgotRecoverManager;
    private GenerateUserIdManager generateUserIdManager;

    private AccountService accountService;

    @Before
    public void setup() {

        userAccountManager = new UserAccountManagerImpl(getMongoTemplate());
        userAuthenticationManager = new UserAuthenticationManagerImpl(getMongoTemplate());
        userProfileManager = new UserProfileManagerImpl(getMongoTemplate());
        userPreferenceManager = new UserPreferenceManagerImpl(getMongoTemplate());
        forgotRecoverManager = new ForgotRecoverManagerImpl(getMongoTemplate());
        generateUserIdManager = new GenerateUserIdManagerImpl(getMongoTemplate());
        accountService = new AccountService(
                userAccountManager,
                userAuthenticationManager,
                userProfileManager,
                userPreferenceManager,
                forgotRecoverManager,
                generateUserIdManager
        );

        userProfileCollection = getCollection(userProfileCollectionName);
        populateUserProfileCollection();

        forgotRecoverCollection = getCollection(forgotRecoverCollectionName);
        populateForgotRecoverCollection();
    }

    @Test
    public void testFindIfUser_Exists_Integration() throws Exception {
        assertEquals("user_community_1@receiptofi.com", accountService.findIfUserExists("user_community_1@receiptofi.com").getEmail());
    }

    @Test
    public void testFindIfUser_Does_Not_Exists() throws Exception {
        assertNull(accountService.findIfUserExists("user_community_3@receiptofi.com"));
    }

    @Ignore
    public void testCreateNewAccount() throws Exception {

    }

    @Test
    public void testInitiateAccountRecovery_Integration() throws Exception {
        UserProfileEntity userProfileEntity = userProfileManager.findOneByEmail("user_community_1@receiptofi.com");
        ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(userProfileEntity);
        assertNotNull(forgotRecoverEntity.getId());
    }

    @Ignore
    public void testInvalidateAllEntries() throws Exception {

    }

    @Ignore
    public void testFindAccountAuthenticationForKey() throws Exception {

    }

    @Ignore
    public void testUpdateAuthentication() throws Exception {

    }

    @Ignore
    public void testGetPreference() throws Exception {

    }

    @After
    public void afterTest() {
        userProfileCollection.drop();
        assertThat(userProfileCollection.getCount(), equalTo(0L));

        forgotRecoverCollection.drop();
        assertThat(forgotRecoverCollection.getCount(), equalTo(0L));
    }

    private void populateForgotRecoverCollection() {
        for(String jsonOfUserProfile : FORGOT_RECOVER_DATA) {
            DBObject dbObject = (DBObject) JSON.parse(jsonOfUserProfile);
            forgotRecoverCollection.save(dbObject);
        }
        assertThat(forgotRecoverCollection.getCount(), equalTo((long) FORGOT_RECOVER_DATA.length));
    }

    private void populateUserProfileCollection() {
        for(String jsonOfUserProfile : USER_PROFILE_DATA) {
            DBObject dbObject = (DBObject) JSON.parse(jsonOfUserProfile);
            userProfileCollection.save(dbObject);
        }
        assertThat(userProfileCollection.getCount(), equalTo((long) USER_PROFILE_DATA.length));
    }

    private static final String[] FORGOT_RECOVER_DATA = new String[] {
            "{\n" +
                    "    \"_id\" : {$oid: \"521fbb57036499d95a7048e6\"},\n" +
                    "    \"USER_PROFILE_ID\" : \"521b24d90364b935ab7bbc10\",\n" +
                    "    \"AUTH\" : \"1c73721ff298769272362c4b083622a70de74b43a8a6f92cd6091c6a1a79234e5b56d3f775f3157ed436d947d33662cab789519d52344562b6a38fe68d1f3924\",\n" +
                    "    \"VERSION\" : 2,\n" +
                    "    \"UPDATE\" : {$date: \"2013-09-07T17:17:28.392Z\"},\n" +
                    "    \"CREATE\" : {$date: \"2013-08-29T21:21:27.131Z\"},\n" +
                    "    \"ACTIVE\" : false,\n" +
                    "    \"DELETE\" : false\n" +
                    "}"
    };

    private static final String[] USER_PROFILE_DATA = new String[] {
            "{\n" +
                    "    \"ACTIVE\" : true,\n" +
                    "    \"CREATE\" : {$date: \"2013-08-26T09:49:23.441Z\"},\n" +
                    "    \"DELETE\" : false,\n" +
                    "    \"EMAIL\" : \"super@receiptofi.com\",\n" +
                    "    \"FIRST_NAME\" : \"Super\",\n" +
                    "    \"HOURS_OFF_SET\" : 0,\n" +
                    "    \"LAST_NAME\" : \"User\",\n" +
                    "    \"REGISTRATION\" : {$date: \"2013-08-26T09:49:23.441Z\"},\n" +
                    "    \"UPDATE\" : {$date: \"2013-08-26T09:49:23.441Z\"},\n" +
                    "    \"USER_AUTHENTICATION\" : {\n" +
                    "        \"$ref\" : \"USER_AUTHENTICATION\",\n" +
                    "        \"$id\" : \"521b24a30364b935ab7bbc0c\"\n" +
                    "    },\n" +
                    "    \"USER_LEVEL_ENUM\" : \"ADMIN\",\n" +
                    "    \"VERSION\" : 0,\n" +
                    "    \"_id\" : {$oid: \"521b24a30364b935ab7bbc0d\"}\n" +
                    "}"
            ,
            "{\n" +
                    "    \"_id\" : {$oid: \"521b24d90364b935ab7bbc10\"},\n" +
                    "    \"EMAIL\" : \"user_community_1@receiptofi.com\",\n" +
                    "    \"FIRST_NAME\" : \"User\",\n" +
                    "    \"LAST_NAME\" : \"One\",\n" +
                    "    \"REGISTRATION\" : {$date: \"2013-08-26T09:50:17.043Z\"},\n" +
                    "    \"HOURS_OFF_SET\" : 0,\n" +
                    "    \"USER_LEVEL_ENUM\" : \"USER_COMMUNITY\",\n" +
                    "    \"VERSION\" : 5,\n" +
                    "    \"UPDATE\" : {$date: \"2013-09-13T09:25:55.641Z\"},\n" +
                    "    \"CREATE\" : {$date: \"2013-08-26T09:50:17.043Z\"},\n" +
                    "    \"ACTIVE\" : true,\n" +
                    "    \"DELETE\" : false,\n" +
                    "    \"USER_AUTHENTICATION\" : {\n" +
                    "        \"$ref\" : \"USER_AUTHENTICATION\",\n" +
                    "        \"$id\" : \"521b24d90364b935ab7bbc0f\"\n" +
                    "    }\n" +
                    "}"
            ,
            "{\n" +
                    "    \"_id\" : {$oid: \"521b265b0364b935ab7bbc15\"},\n" +
                    "    \"EMAIL\" : \"tech@receiptofi.com\",\n" +
                    "    \"FIRST_NAME\" : \"Tech\",\n" +
                    "    \"LAST_NAME\" : \"User\",\n" +
                    "    \"REGISTRATION\" : {$date: \"2013-08-26T09:56:43.292Z\"},\n" +
                    "    \"HOURS_OFF_SET\" : 0,\n" +
                    "    \"USER_LEVEL_ENUM\" : \"TECHNICIAN\",\n" +
                    "    \"VERSION\" : 3,\n" +
                    "    \"UPDATE\" : {$date: \"2013-08-26T09:59:35.326Z\"},\n" +
                    "    \"CREATE\" : {$date: \"2013-08-26T09:56:43.292Z\"},\n" +
                    "    \"ACTIVE\" : true,\n" +
                    "    \"DELETE\" : false,\n" +
                    "    \"USER_AUTHENTICATION\" : {\n" +
                    "        \"$ref\" : \"USER_AUTHENTICATION\",\n" +
                    "        \"$id\" : \"521b265b0364b935ab7bbc14\"\n" +
                    "    }\n" +
                    "}"
            ,
            "{\n" +
                    "    \"_id\" : {$oid: \"521bba950364b935ab7bbc1e\"},\n" +
                    "    \"EMAIL\" : \"user_community_2@receiptofi.com\",\n" +
                    "    \"FIRST_NAME\" : \"User\",\n" +
                    "    \"LAST_NAME\" : \"Two\",\n" +
                    "    \"REGISTRATION\" : {$date: \"2013-08-26T20:29:09.777Z\"},\n" +
                    "    \"HOURS_OFF_SET\" : 0,\n" +
                    "    \"USER_LEVEL_ENUM\" : \"USER_COMMUNITY\",\n" +
                    "    \"VERSION\" : 3,\n" +
                    "    \"UPDATE\" : {$date: \"2013-09-07T17:18:33.648Z\"},\n" +
                    "    \"CREATE\" : {$date: \"2013-08-26T20:29:09.777Z\"},\n" +
                    "    \"ACTIVE\" : true,\n" +
                    "    \"DELETE\" : false,\n" +
                    "    \"USER_AUTHENTICATION\" : {\n" +
                    "        \"$ref\" : \"USER_AUTHENTICATION\",\n" +
                    "        \"$id\" : \"521bba950364b935ab7bbc1d\"\n" +
                    "    }\n" +
                    "}"
    };
}
