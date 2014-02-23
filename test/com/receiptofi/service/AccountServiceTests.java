package com.receiptofi.service;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.ForgotRecoverManagerImpl;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserAuthenticationManagerImpl;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserPreferenceManagerImpl;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.repository.UserProfileManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * User: hitender
 * Date: 2/19/14 11:21 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTests extends AbstractMongoDBTest {

    private String userProfileCollectionName = "USER_PROFILE";
    private DBCollection userProfileCollection;

    private UserAuthenticationManager userAuthenticationManager;
    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;
    private ForgotRecoverManager forgotRecoverManager;
    private AccountService accountService;

    @Before
    public void setup() {
        userAuthenticationManager = new UserAuthenticationManagerImpl(createDatastore(UserAuthenticationEntity.class));
        userProfileManager = new UserProfileManagerImpl(createDatastore(UserProfileEntity.class));
        userPreferenceManager = new UserPreferenceManagerImpl(createDatastore(UserPreferenceEntity.class));
        forgotRecoverManager = new ForgotRecoverManagerImpl(createDatastore(ForgotRecoverEntity.class));
        accountService = new AccountService(userAuthenticationManager, userProfileManager, userPreferenceManager, forgotRecoverManager);

        assertTrue(getMongoTemplate().collectionExists(userProfileCollectionName));
        userProfileCollection = getMongoTemplate().getCollection(userProfileCollectionName);
        populateUserProfileCollection();
    }

    @Test
    public void testFindIfUser_Exists() throws Exception {
        assertEquals("user_community_1@receiptofi.com", accountService.findIfUserExists("user_community_1@receiptofi.com").getEmailId());
    }

    @Test
    public void testFindIfUser_Does_Not_Exists() throws Exception {
        assertNull(accountService.findIfUserExists("user_community_3@receiptofi.com"));
    }

    @Test
    public void testCreateNewAccount() throws Exception {

    }

    @Test
    public void testInitiateAccountRecovery() throws Exception {

    }

    @Test
    public void testInvalidateAllEntries() throws Exception {

    }

    @Test
    public void testFindAccountAuthenticationForKey() throws Exception {

    }

    @Test
    public void testUpdateAuthentication() throws Exception {

    }

    @Test
    public void testGetPreference() throws Exception {

    }

    @After
    public void afterTest() {
        userProfileCollection.drop();
        assertThat(userProfileCollection.getCount(), equalTo(0L));
    }

    private void populateUserProfileCollection() {
        for(String jsonOfUserProfile : USER_PROFILE_DATA) {
            DBObject dbObject = (DBObject) JSON.parse(jsonOfUserProfile);
            userProfileCollection.save(dbObject);
        }
        assertThat(userProfileCollection.getCount(), equalTo((long) USER_PROFILE_DATA.length));
    }

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
