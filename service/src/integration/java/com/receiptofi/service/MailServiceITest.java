package com.receiptofi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.receiptofi.ITest;
import com.receiptofi.LoadResource;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.utils.DateUtil;

import org.joda.time.DateTime;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: hitender
 * Date: 4/8/16 11:05 AM
 */
public class MailServiceITest extends ITest {

    private MailService mailService;

    @Before
    public void classSetup() throws IOException {
        Properties mailProperties = new Properties();
        mailProperties.put("mail.transport.protocol", properties.get("smtpProtocol"));
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.smtp.port", properties.get("sslMailPort"));
        mailProperties.put("mail.smtp.socketFactory.port", properties.get("sslMailPort"));
        mailProperties.put("mail.debug", true);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setPort(Integer.parseInt(properties.getProperty("sslMailPort")));
        mailSender.setDefaultEncoding(properties.getProperty("mailEncoding"));
        mailSender.setHost(properties.getProperty("goDaddyMailHost"));
        mailSender.setUsername(properties.getProperty("goDaddyUsername"));
        mailSender.setPassword(properties.getProperty("goDaddy"));

        FreeMarkerConfigurationFactory freemarkerConfiguration = new FreeMarkerConfigurationFactory();
        TemplateLoader templateLoader = new FileTemplateLoader(LoadResource.getFreemarkerLocation());
        freemarkerConfiguration.setPreTemplateLoaders(templateLoader);

        String appendToSubject = "::" + properties.getProperty("braintree.environment") + ":: ";

        mailService = new MailService(
                properties.getProperty("dev.sent.to"),
                properties.getProperty("invitee.email"),
                properties.getProperty("email.address.name"),
                properties.getProperty("domain"),
                properties.getProperty("https"),
                appendToSubject + properties.getProperty("mail.invite.subject"),
                appendToSubject + properties.getProperty("mail.recover.subject"),
                appendToSubject + properties.getProperty("mail.validate.subject"),
                appendToSubject + properties.getProperty("mail.registration.active.subject"),
                appendToSubject + properties.getProperty("mail.account.not.found"),
                accountService,
                inviteService,
                freemarkerConfiguration,
                emailValidateService,
                friendService,
                userAuthenticationManager,
                userAccountManager,
                userProfilePreferenceService,
                notificationService,
                mailManager,
                1
        );
    }

    @Test
    public void registrationCompleteEmail() throws Exception {
        assertTrue("Sent invitation", mailService.registrationCompleteEmail("test@receiptofi.com", "Test"));
    }

    @Test
    public void accountValidationMail() throws Exception {
        assertTrue("Account Validation", mailService.accountValidationMail("test@receiptofi.com", "Test", "$someCodeForAuth"));
    }

    @Test
    public void mailRecoverLink() throws Exception {
        /** Account does not exists. Send invite. */
        assertEquals("Account not found but success in sending email",
                MailTypeEnum.SUCCESS,
                mailService.mailRecoverLink("delete-mail-recover@receiptofi.com"));

        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "delete-mail-recover@receiptofi.com",
                "First",
                "Name",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        assertEquals("Account not validated",
                MailTypeEnum.ACCOUNT_NOT_VALIDATED,
                mailService.mailRecoverLink("delete-mail-recover@receiptofi.com"));

        /** Validated account. */
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());
        assertEquals("Account recovery link sent",
                MailTypeEnum.SUCCESS,
                mailService.mailRecoverLink("delete-mail-recover@receiptofi.com"));
    }

    @Test
    public void sendInvite() throws Exception {
        UserAccountEntity primaryUserAccount = accountService.findByUserId("delete@receiptofi.com");
        if (primaryUserAccount == null) {
            /** Create New User. */
            primaryUserAccount = accountService.createNewAccount(
                    "delete@receiptofi.com",
                    "First",
                    "Name",
                    "testtest",
                    DateUtil.parseAgeForBirthday("25"));
        }

        String json = mailService.sendInvite("delete@receiptofi.com", primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        JSONObject jsonObject = new JSONObject(json);
        assertFalse("Failed to send invite to yourself", jsonObject.getBoolean("status"));
        assertEquals("User registering them self", "You are registered.", jsonObject.getString("message"));

        json = mailService.sendInvite("delete@receiptofi", primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        jsonObject = new JSONObject(json);
        assertFalse("Failed to send invite to yourself", jsonObject.getBoolean("status"));
        assertEquals("User registering with invalid email", "Invalid Email: delete@receiptofi", jsonObject.getString("message"));

        json = mailService.sendInvite("friend@receiptofi.com", primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        jsonObject = new JSONObject(json);
        assertTrue("Success in sending invite", jsonObject.getBoolean("status"));
        assertEquals("Invitation sent", "Invitation Sent to: friend@receiptofi.com", jsonObject.getString("message"));
        assertEquals(
                "Invitation sent to 'friend@receiptofi.com'",
                notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).get(0).getMessage());
        assertNotNull("Friend does not exists", accountService.findByUserId("friend@receiptofi.com"));
    }

    @Test
    public void inviteExistingUserToBeFriend() {
        UserAccountEntity primaryUserAccount = accountService.findByUserId("delete@receiptofi.com");
        if (primaryUserAccount == null) {
            /** Create New User. */
            primaryUserAccount = accountService.createNewAccount(
                    "delete@receiptofi.com",
                    "First",
                    "Name",
                    "testtest",
                    DateUtil.parseAgeForBirthday("25"));
        }

        UserAccountEntity existing = accountService.findByUserId("invite-existing@receiptofi.com");
        if (existing == null) {
            /** Create New User. */
            existing = accountService.createNewAccount(
                    "invite-existing@receiptofi.com",
                    "Invite",
                    "Name",
                    "testtest",
                    DateUtil.parseAgeForBirthday("25"));
        }

        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(existing.getReceiptUserId(), existing.getUserId());
        accountService.validateAccount(emailValidate, existing);
        existing = accountService.findByUserId(existing.getUserId());
        assertTrue("Account validated", existing.isAccountValidated());

        /** primaryUserAccount send's invite to existing user. */
        inviteFriend(primaryUserAccount, existing);

        /** Now do a reverse invite. Which defaults to auto connection between two. */
        inviteInviteeToBeFriend(primaryUserAccount, existing);

        /** Get total notification to primary. */
        int notificationPrimary = notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).size();

        /** Primary unfriend's existing. */
        friendService.unfriend(primaryUserAccount.getReceiptUserId(), existing.getUserId());
        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), existing.getReceiptUserId());
        assertFalse("Friends not connected", friend.isConnected());

        /** Existing friend tries reconnecting by sending the invite to primary. */
        unfriendAndReInviteSentByUnfriend(primaryUserAccount, existing);

        /** Increased notification count by one for primary user. */
        assertEquals(
                notificationPrimary + 1,
                notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).size());

        /**
         * Re-connect with existing user by primaryUserAccount. Only the one who
         * has initiated disconnect can perform an auto re-connect.
         */
        reInvite(primaryUserAccount, existing);
    }

    private void inviteFriend(UserAccountEntity primaryUserAccount, UserAccountEntity existing) {
        String json = mailService.sendInvite(existing.getUserId(), primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        JSONObject jsonObject = new JSONObject(json);
        assertTrue("Success in sending invite", jsonObject.getBoolean("status"));
        assertEquals("Friend request sent", "Friend request sent to invite-existing@receipt...", jsonObject.getString("message"));

        assertEquals(
                "Sent friend request to Invite Name",
                notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).get(0).getMessage());

        assertEquals(
                "New friend request from First Name",
                notificationService.getAllNotifications(existing.getReceiptUserId()).get(0).getMessage());

        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), existing.getReceiptUserId());
        assertFalse("Friends not yet connected", friend.isConnected());
        assertFalse("Friend not yet accepted connection", friend.isAcceptConnection());
    }

    private void inviteInviteeToBeFriend(UserAccountEntity primaryUserAccount, UserAccountEntity existing) {
        String json = mailService.sendInvite(primaryUserAccount.getUserId(), existing.getReceiptUserId(), existing.getUserId());
        JSONObject jsonObject = new JSONObject(json);
        assertTrue("Success in sending invite", jsonObject.getBoolean("status"));
        assertEquals("Friends are now connected", "Connected with delete@receiptofi.com", jsonObject.getString("message"));

        assertEquals(
                "New connection with Invite Name",
                notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).get(0).getMessage());

        assertEquals(
                "New connection with First Name",
                notificationService.getAllNotifications(existing.getReceiptUserId()).get(0).getMessage());

        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), existing.getReceiptUserId());
        assertTrue("Friends connected", friend.isConnected());
        assertTrue("Friend accepted connection", friend.isAcceptConnection());
    }

    private void unfriendAndReInviteSentByUnfriend(UserAccountEntity primaryUserAccount, UserAccountEntity existing) {
        /** Unfriend existing sent's invite to primary. */
        String json = mailService.sendInvite(primaryUserAccount.getUserId(), existing.getReceiptUserId(), existing.getUserId());
        JSONObject jsonObject = new JSONObject(json);
        assertTrue("Success in sending invite", jsonObject.getBoolean("status"));
        assertEquals("Friends are now connected", "Invitation sent to delete@receiptofi.com", jsonObject.getString("message"));

        assertEquals(
                "Re-connection requested by Invite Name. Submit email 'invite-existing@receiptofi.com' in invite to connect.",
                notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).get(0).getMessage());

        assertEquals(
                "Invitation sent to 'delete@receiptofi.com'",
                notificationService.getAllNotifications(existing.getReceiptUserId()).get(0).getMessage());

        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), existing.getReceiptUserId());
        assertFalse("Friends not connected", friend.isConnected());
        assertTrue("Friend accepted connection from before", friend.isAcceptConnection());
    }

    private void reInvite(UserAccountEntity primaryUserAccount, UserAccountEntity existing) {
        /** Primary sent's invite to existing. This should results in reconnect. */
        String json = mailService.sendInvite(existing.getUserId(), primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        JSONObject jsonObject = new JSONObject(json);
        assertTrue("Success in sending invite", jsonObject.getBoolean("status"));
        assertEquals("Friends are now connected", "Connected with invite-existing@receipt...", jsonObject.getString("message"));

        assertEquals(
                "Re-connection with Invite Name",
                notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId()).get(0).getMessage());

        assertEquals(
                "Re-connection with First Name",
                notificationService.getAllNotifications(existing.getReceiptUserId()).get(0).getMessage());

        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), existing.getReceiptUserId());
        assertTrue("Friends connected", friend.isConnected());
        assertTrue("Friend accepted connection", friend.isAcceptConnection());
    }

    @Test
    public void inviteInactiveUserToBeFriend() {
        UserAccountEntity primaryUserAccount = accountService.findByUserId("delete@receiptofi.com");
        if (primaryUserAccount == null) {
            /** Create New User. */
            primaryUserAccount = accountService.createNewAccount(
                    "delete@receiptofi.com",
                    "First",
                    "Name",
                    "testtest",
                    DateUtil.parseAgeForBirthday("25"));
        }
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);

        UserAccountEntity existing = accountService.findByUserId("invite-inactive@receiptofi.com");
        if (existing == null) {
            /** Create New User. */
            existing = accountService.createNewAccount(
                    "invite-inactive@receiptofi.com",
                    "First",
                    "Name",
                    "testtest",
                    DateUtil.parseAgeForBirthday("25"));
        }
        assertFalse("Account validated", existing.isAccountValidated());
        int inactiveAccount = accountService.inactiveNonValidatedAccount(DateTime.now().plusDays(2).toDate());
        assertEquals("Marked one account inactive", 1, inactiveAccount);
        existing = accountService.findByUserId("invite-inactive@receiptofi.com");
        assertFalse("Account Active", existing.isActive());
        assertFalse("Account Deleted", existing.isDeleted());

        String json = mailService.sendInvite("invite-inactive@receiptofi.com", primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        JSONObject jsonObject = new JSONObject(json);
        assertTrue("Success in sending invite", jsonObject.getBoolean("status"));
        assertEquals("Friend request sent", "Invitation Sent to: invite-inactive@receipt...", jsonObject.getString("message"));
    }

    /**
     * Asynchronous mobile invite can lead to multiple quick invite from same user.
     * //TODO add similar for expense tag
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testMultipleInviteFromSameEmailInQuickSuccession() throws InterruptedException, ExecutionException {
        UserAccountEntity primaryUserAccount = accountService.findByUserId("delete@receiptofi.com");
        if (primaryUserAccount == null) {
            /** Create New User. */
            primaryUserAccount = accountService.createNewAccount(
                    "delete@receiptofi.com",
                    "First",
                    "Name",
                    "testtest",
                    DateUtil.parseAgeForBirthday("25"));
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Set<Future<String>> set = new LinkedHashSet<>();
        for (int i = 0; i < 2; i++) {
            InviteCallable worker = new InviteCallable(
                    primaryUserAccount.getReceiptUserId(),
                    primaryUserAccount.getUserId(),
                    mailService);
            Future<String> s = executor.submit(worker);
            set.add(s);
            Thread.sleep(1000);
        }

        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }

        int count = 0;
        for (Future<String> s : set) {
            JSONObject jsonObject;
            if (0 == count) {
                jsonObject = new JSONObject(s.get());
                assertTrue("Send invite successfully", jsonObject.getBoolean("status"));
                assertEquals("When user has sticky finger", "Invitation Sent to: anotheruser@receiptofi.com", jsonObject.getString("message"));
            } else {
                jsonObject = new JSONObject(s.get());
                assertFalse("Fail to send invite when one is in process", jsonObject.getBoolean("status"));
                assertEquals("When user has sticky finger", "Almost ready, sending invitee to 'anotheruser@receiptofi.com'", jsonObject.getString("message"));
            }

            count++;
        }

    }

    private class InviteCallable implements Callable<String> {
        private final String rid;
        private final String uid;
        private final MailService mailService;

        InviteCallable(String rid, String uid, MailService mailService) {
            this.rid = rid;
            this.uid = uid;
            this.mailService = mailService;
        }

        public String call() {
            return mailService.sendInvite("anotheruser@receiptofi.com", rid, uid);
        }
    }
}
