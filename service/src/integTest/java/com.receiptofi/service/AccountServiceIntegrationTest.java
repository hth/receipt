package com.receiptofi.service;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import com.receiptofi.IntegrationTests;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.ForgotRecoverManagerImpl;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.GenerateUserIdManagerImpl;
import com.receiptofi.repository.RegisteredDeviceManager;
import com.receiptofi.repository.RegisteredDeviceManagerImpl;
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

/**
 * User: hitender
 * Date: 2/25/14 1:02 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Category (IntegrationTests.class)
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
    private RegisteredDeviceManager registeredDeviceManager;
    private EmailValidateService emailValidateService;
    private RegistrationService registrationService;
    private UserProfilePreferenceService userProfilePreferenceService;

    private AccountService accountService;

    @Before
    public void setup() {

        userAccountManager = new UserAccountManagerImpl(getMongoTemplate());
        userAuthenticationManager = new UserAuthenticationManagerImpl(getMongoTemplate());
        registeredDeviceManager = new RegisteredDeviceManagerImpl(getMongoTemplate());
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
                generateUserIdManager,
                emailValidateService,
                registrationService,
                userProfilePreferenceService
        );

        userProfileCollection = getCollection(userProfileCollectionName);
        populateUserProfileCollection();

        forgotRecoverCollection = getCollection(forgotRecoverCollectionName);
        populateForgotRecoverCollection();
    }

    @Test
    public void testFindIfUser_Exists_Integration() throws Exception {
        assertEquals("user_community_1@receiptofi.com", accountService.doesUserExists("user_community_1@receiptofi.com").getEmail());
    }

    @Test
    public void testFindIfUser_Does_Not_Exists() throws Exception {
        assertNull(accountService.doesUserExists("user_community_3@receiptofi.com"));
    }

    @Ignore
    public void testCreateNewAccount() throws Exception {

    }

    @Test
    public void testInitiateAccountRecovery_Integration() throws Exception {
        UserProfileEntity userProfileEntity = userProfileManager.findOneByMail("user_community_1@receiptofi.com");
        ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(userProfileEntity.getReceiptUserId());
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
        for (String jsonOfUserProfile : FORGOT_RECOVER_DATA) {
            DBObject dbObject = (DBObject) JSON.parse(jsonOfUserProfile);
            forgotRecoverCollection.save(dbObject);
        }
        assertThat(forgotRecoverCollection.getCount(), equalTo((long) FORGOT_RECOVER_DATA.length));
    }

    private void populateUserProfileCollection() {
        for (String jsonOfUserProfile : USER_PROFILE_DATA) {
            DBObject dbObject = (DBObject) JSON.parse(jsonOfUserProfile);
            userProfileCollection.save(dbObject);
        }
        assertThat(userProfileCollection.getCount(), equalTo((long) USER_PROFILE_DATA.length));
    }

    private static final String[] FORGOT_RECOVER_DATA = new String[]{
            "{\n" +
                    "    \"_id\" : {$oid: \"537305b23004599a154294a9\"},\n" +
                    "    \"RID\" : \"5370907530041668259b38d1\",\n" +
                    "    \"AUTH\" : \"$2a$15$2UowKxnrUCRqkK3CbDtzou6cZMcIKAzVB37Op379l5D9YE7pWxsam\",\n" +
                    "    \"V\" : 0,\n" +
                    "    \"U\" : {$date: \"2014-05-14T05:57:06.870Z\"},\n" +
                    "    \"C\" : {$date: \"2014-05-14T05:57:06.870Z\"},\n" +
                    "    \"A\" : true,\n" +
                    "    \"D\" : false\n" +
                    "}"
    };

    private static final String[] USER_PROFILE_DATA = new String[]{
            "{\n" +
                    "    \"_id\" : {$oid: \"537068ee3004ff079b5af89c\"},\n" +
                    "    \"RID\" : \"10000000001\",\n" +
                    "    \"UID\" : \"4564545645646\",\n" +
                    "    \"PID\" : \"FACEBOOK\",\n" +
                    "    \"UN\" : \"baan.dal\",\n" +
                    "    \"N\" : \"Ann Pluse\",\n" +
                    "    \"FN\" : \"Ann\",\n" +
                    "    \"LN\" : \"Pluse\",\n" +
                    "    \"GE\" : \"male\",\n" +
                    "    \"LO\" : \"en_US\",\n" +
                    "    \"URL\" : \"https://www.facebook.com/baan.dal\",\n" +
                    "    \"TZ\" : -7,\n" +
                    "    \"UT\" : {$date: \"2014-03-13T05:01:04.000Z\"},\n" +
                    "    \"BI\" : \"About me is here\",\n" +
                    "	 \"EM\" : \"user_community_1@receiptofi.com\",\n" +
                    "    \"ULE\" : \"USER\",\n" +
                    "    \"V\" : 1,\n" +
                    "    \"U\" : {$date: \"2014-05-12T06:23:42.281Z\"},\n" +
                    "    \"C\" : {$date: \"2014-05-12T06:23:42.281Z\"},\n" +
                    "    \"A\" : true,\n" +
                    "    \"D\" : false\n" +
                    "}",
            "{\n" +
                    "    \"_id\" : {$oid: \"537068ef3004ff079b5af89e\"},\n" +
                    "    \"RID\" : \"10000000002\",\n" +
                    "    \"UID\" : \"98374509349050\",\n" +
                    "    \"PID\" : \"FACEBOOK\",\n" +
                    "    \"N\" : \"박진영\",\n" +
                    "    \"FN\" : \"진영\",\n" +
                    "    \"LN\" : \"박\",\n" +
                    "    \"GE\" : \"female\",\n" +
                    "    \"LO\" : \"ko_KR\",\n" +
                    "    \"URL\" : \"https://www.facebook.com/profile.php?id=98374509349050\",\n" +
                    "    \"TP_ID\" : \"pbU11ZeqqMY1lImnlTptEBSm_9s\",\n" +
                    "    \"TZ\" : 0,\n" +
                    "    \"UT\" : {$date: \"2014-04-16T09:29:46.000Z\"},\n" +
                    "    \"ULE\" : \"USER\",\n" +
                    "    \"V\" : 1,\n" +
                    "    \"U\" : {$date: \"2014-05-12T06:23:43.163Z\"},\n" +
                    "    \"C\" : {$date: \"2014-05-12T06:23:43.163Z\"},\n" +
                    "    \"A\" : true,\n" +
                    "    \"D\" : false\n" +
                    "}",
            "{\n" +
                    "    \"_id\" : {$oid: \"5370907530041668259b38d1\"},\n" +
                    "    \"RID\" : \"10000000004\",\n" +
                    "    \"FN\" : \"User\",\n" +
                    "    \"LN\" : \"Two\",\n" +
                    "    \"EM\" : \"user_community_2@receiptofi.com\",\n" +
                    "    \"ULE\" : \"USER\",\n" +
                    "    \"V\" : 0,\n" +
                    "    \"U\" : {$date: \"2014-05-12T09:12:21.900Z\"},\n" +
                    "    \"C\" : {$date: \"2014-05-12T09:12:21.900Z\"},\n" +
                    "    \"A\" : true,\n" +
                    "    \"D\" : false\n" +
                    "}"
    };
}
