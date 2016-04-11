package com.receiptofi.service;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.util.Assert;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 10:20 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private AccountService accountService;
    private InviteService inviteService;
    private JavaMailSenderImpl mailSender;
    private FreeMarkerConfigurationFactory freemarkerConfiguration;
    private EmailValidateService emailValidateService;
    private LoginService loginService;
    private UserAuthenticationManager userAuthenticationManager;
    private UserAccountManager userAccountManager;
    private UserProfilePreferenceService userProfilePreferenceService;
    private FriendService friendService;
    private NotificationService notificationService;

    private String doNotReplyEmail;
    private String devSentTo;
    private String inviteeEmail;
    private String emailAddressName;
    private String domain;
    private String https;
    private String mailInviteSubject;
    private String mailRecoverSubject;
    private String mailValidateSubject;
    private String mailRegistrationActiveSubject;
    private String accountNotFound;
    private String googleSmall;
    private String facebookSmall;
    private String appStore;
    private String googlePlay;

    @Autowired
    public MailService(
            @Value ("${do.not.reply.email}")
            String doNotReplyEmail,

            @Value ("${dev.sent.to}")
            String devSentTo,

            @Value ("${invitee.email}")
            String inviteeEmail,

            @Value ("${email.address.name}")
            String emailAddressName,

            @Value ("${domain}")
            String domain,

            @Value ("${https}")
            String https,

            @Value ("${mail.invite.subject}")
            String mailInviteSubject,

            @Value ("${mail.recover.subject}")
            String mailRecoverSubject,

            @Value ("${mail.validate.subject}")
            String mailValidateSubject,

            @Value ("${mail.registration.active.subject}")
            String mailRegistrationActiveSubject,

            @Value ("${mail.account.not.found}")
            String accountNotFound,

            @Value ("${mail.googleSmall:..//jsp//images//smallGoogle.jpg}")
            String googleSmall,

            @Value ("${mail.googlePlay:..//jsp//images//googlePlay151x47.jpg}")
            String googlePlay,

            @Value ("${mail.facebookSmall:..//jsp//images//smallFacebook.jpg}")
            String facebookSmall,

            @Value ("${mail.appStore:..//jsp//images//app-store151x48.jpg}")
            String appStore,

            AccountService accountService,
            InviteService inviteService,
            JavaMailSenderImpl mailSender,

            @SuppressWarnings ("SpringJavaAutowiringInspection")
            FreeMarkerConfigurationFactory freemarkerConfiguration,

            EmailValidateService emailValidateService,
            FriendService friendService,
            LoginService loginService,
            UserAuthenticationManager userAuthenticationManager,
            UserAccountManager userAccountManager,
            UserProfilePreferenceService userProfilePreferenceService,
            NotificationService notificationService
    ) {

        this.doNotReplyEmail = doNotReplyEmail;
        this.devSentTo = devSentTo;
        this.inviteeEmail = inviteeEmail;
        this.emailAddressName = emailAddressName;
        this.domain = domain;
        this.https = https;
        this.mailInviteSubject = mailInviteSubject;
        this.mailRecoverSubject = mailRecoverSubject;
        this.mailValidateSubject = mailValidateSubject;
        this.mailRegistrationActiveSubject = mailRegistrationActiveSubject;
        this.accountNotFound = accountNotFound;
        this.googleSmall = googleSmall;
        this.googlePlay = googlePlay;
        this.facebookSmall = facebookSmall;
        this.appStore = appStore;

        this.accountService = accountService;
        this.inviteService = inviteService;
        this.mailSender = mailSender;
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.emailValidateService = emailValidateService;
        this.friendService = friendService;
        this.loginService = loginService;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userAccountManager = userAccountManager;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.notificationService = notificationService;
    }

    public boolean registrationCompleteEmail(String userId, String name) {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", name);
        rootMap.put("contact_email", userId);
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            MimeMessage message = mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));

            String sentTo = StringUtils.isEmpty(devSentTo) ? userId : devSentTo;
            if (sentTo.equalsIgnoreCase(devSentTo)) {
                helper.setTo(new InternetAddress(devSentTo, emailAddressName));
            } else {
                helper.setTo(new InternetAddress(userId, name));
            }
            LOG.info("Account validation sent to={}", StringUtils.isEmpty(devSentTo) ? userId : devSentTo);
            sendMail(
                    name + ": " + mailRegistrationActiveSubject,
                    freemarkerToString("mail/registration-active.ftl", rootMap),
                    message,
                    helper
            );
        } catch (IOException | TemplateException | MessagingException exception) {
            LOG.error("Validation failure email for={}", userId, exception);
            return false;
        }
        return true;
    }

    /**
     * Sends out email to validate account.
     *
     * @param userId
     * @param name
     * @param auth   - Authentication key to authenticate user when clicking link in mail
     * @return
     */
    public boolean accountValidationMail(String userId, String name, String auth) {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", name);
        rootMap.put("contact_email", userId);
        rootMap.put("link", auth);
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            MimeMessage message = mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));

            String sentTo = StringUtils.isEmpty(devSentTo) ? userId : devSentTo;
            if (sentTo.equalsIgnoreCase(devSentTo)) {
                helper.setTo(new InternetAddress(devSentTo, emailAddressName));
            } else {
                helper.setTo(new InternetAddress(userId, name));
            }
            LOG.info("Account validation sent to={}", StringUtils.isEmpty(devSentTo) ? userId : devSentTo);
            sendMail(
                    name + ": " + mailValidateSubject,
                    freemarkerToString("mail/self-signup.ftl", rootMap),
                    message,
                    helper
            );
        } catch (IOException | TemplateException | MessagingException exception) {
            LOG.error("Validation failure email for={}", userId, exception);
            return false;
        }
        return true;
    }

    /**
     * Send recover email to user of provided email id.
     * http://bharatonjava.wordpress.com/2012/08/27/sending-email-using-java-mail-api/
     *
     * @param mail
     */
    public MailTypeEnum mailRecoverLink(String mail) {
        UserAccountEntity userAccount = accountService.findByUserId(mail);
        if (null == userAccount) {
            LOG.warn("Could not recover user={}", mail);

            Map<String, String> rootMap = new HashMap<>();
            rootMap.put("contact_email", mail);
            rootMap.put("domain", domain);
            rootMap.put("https", https);

            try {
                MimeMessage message = mailSender.createMimeMessage();

                // use the true flag to indicate you need a multipart message
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));

                String sentTo = StringUtils.isEmpty(devSentTo) ? mail : devSentTo;
                if (sentTo.equalsIgnoreCase(devSentTo)) {
                    LOG.info("Mail account not found send to={}", devSentTo);
                    helper.setTo(new InternetAddress(devSentTo, emailAddressName));
                } else {
                    LOG.info("Mail account not found send to={}", mail);
                    helper.setTo(new InternetAddress(mail, ""));
                }

                sendMail(
                        accountNotFound,
                        freemarkerToString("mail/account-recover-unregistered-user.ftl", rootMap),
                        message,
                        helper
                );
                return MailTypeEnum.SUCCESS;
            } catch (IOException | TemplateException | MessagingException exception) {
                LOG.error("Account not found email={}", exception.getLocalizedMessage(), exception);
            }

            return MailTypeEnum.ACCOUNT_NOT_FOUND;
        }

        if (null != userAccount.getProviderId()) {
            /** Cannot change password for social account. Well this condition is checked in Mobile Server too. */
            LOG.warn("Social account user={} tried recovering password", mail);
            return MailTypeEnum.SOCIAL_ACCOUNT;
        }

        if (userAccount.isAccountValidated()) {
            ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(
                    userAccount.getReceiptUserId());

            Map<String, String> rootMap = new HashMap<>();
            rootMap.put("to", userAccount.getName());
            rootMap.put("link", forgotRecoverEntity.getAuthenticationKey());
            rootMap.put("domain", domain);
            rootMap.put("https", https);

            try {
                MimeMessage message = mailSender.createMimeMessage();

                // use the true flag to indicate you need a multipart message
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));

                String sentTo = StringUtils.isEmpty(devSentTo) ? mail : devSentTo;
                if (sentTo.equalsIgnoreCase(devSentTo)) {
                    helper.setTo(new InternetAddress(devSentTo, emailAddressName));
                    LOG.info("Mail recovery send to={}", devSentTo);
                } else {
                    helper.setTo(new InternetAddress(mail, userAccount.getName()));
                    LOG.info("Mail recovery send to={}", mail);
                }

                sendMail(
                        userAccount.getName() + ": " + mailRecoverSubject,
                        freemarkerToString("mail/account-recover.ftl", rootMap),
                        message,
                        helper
                );
                return MailTypeEnum.SUCCESS;
            } catch (IOException | TemplateException | MessagingException exception) {
                LOG.error("Recovery email={}", exception.getLocalizedMessage(), exception);
                return MailTypeEnum.FAILURE;
            }
        } else {
            /** Since account is not validated, send account validation email. */
            EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                    userAccount.getReceiptUserId(),
                    userAccount.getUserId());

            boolean status = accountValidationMail(
                    userAccount.getUserId(),
                    userAccount.getName(),
                    accountValidate.getAuthenticationKey());

            if (status) {
                return MailTypeEnum.ACCOUNT_NOT_VALIDATED;
            }
            return MailTypeEnum.FAILURE;
        }
    }

    /**
     * Send invite.
     *
     * @param invitedUserEmail Invited users email address
     * @param invitedByRid     Existing users email address
     * @return
     */
    private boolean sendInvitation(String invitedUserEmail, String invitedByRid) {
        UserAccountEntity invitedBy = accountService.findByReceiptUserId(invitedByRid);
        if (invitedBy != null) {
            InviteEntity inviteEntity = null;
            try {
                inviteEntity = inviteService.initiateInvite(invitedUserEmail, invitedBy);

                FriendEntity friend = new FriendEntity(invitedByRid, inviteEntity.getInvited().getReceiptUserId());
                friendService.save(friend);

                formulateInvitationMail(invitedUserEmail, invitedBy, inviteEntity);
                return true;
            } catch (RuntimeException e) {
                LOG.error("Error persisting InviteEntity, reason={}", e.getLocalizedMessage(), e);

                if (inviteEntity != null) {
                    friendService.deleteHard(invitedByRid, inviteEntity.getInvited().getReceiptUserId());
                    deleteInvite(inviteEntity);
                    LOG.warn("Due to failure in sending the invitation email. Deleting Invite={}, for={}",
                            inviteEntity.getId(), inviteEntity.getEmail());
                }
            }
        }
        return false;
    }

    /**
     * Re-send invite or send new invitation to existing (pending) invite to user.
     *
     * @param emailId      Invited users email address
     * @param invitedByRid Existing users email address
     * @return
     */
    private boolean reSendInvitation(String emailId, String invitedByRid) {
        UserAccountEntity invitedBy = accountService.findByReceiptUserId(invitedByRid);
        if (null != invitedBy) {
            FriendEntity friend = null;
            try {
                InviteEntity invite = inviteService.reInviteActiveInvite(emailId, invitedBy);
                boolean isNewInvite = false;
                if (null == invite) {
                    /**
                     * Means invite may exists through another user.
                     * Better to create a new invite for the requesting user.
                     */
                    invite = reCreateAnotherInvite(emailId, invitedBy);
                    isNewInvite = true;

                    friend = new FriendEntity(invitedByRid, invite.getInvited().getReceiptUserId());
                    friendService.save(friend);
                } else {
                    invite.isActive();
                    inviteService.save(invite);
                }

                if (null == friend) {
                    friend = friendService.getConnection(invitedByRid, invite.getInvited().getReceiptUserId());
                    if (!friend.isConnected() && friend.getReceiptUserId().equalsIgnoreCase(invitedByRid)) {
                        /** When invitee has cancelled interest in their friend and would like to re-invite. */
                        if (friendService.inviteAgain(friend.getId(), RandomString.newInstance().nextString())) {
                            LOG.info("Re-invite connection from {} to {}", invitedByRid, invite.getInvited().getReceiptUserId());
                        }
                    }
                }

                formulateInvitationMail(emailId, invitedBy, invite);
                if (!isNewInvite) {
                    inviteService.save(invite);
                }
            } catch (Exception e) {
                LOG.error("Error persisting InviteEntity, reason={}", e.getLocalizedMessage(), e);
                if (friend != null) {
                    friendService.deleteHard(friend.getReceiptUserId(), friend.getFriendUserId());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Invitation is created by the new user.
     *
     * @param email
     * @param invitedBy
     * @return
     */
    private InviteEntity reCreateAnotherInvite(String email, UserAccountEntity invitedBy) {
        InviteEntity inviteEntity = inviteService.find(email);
        try {
            String auth = HashText.computeBCrypt(RandomString.newInstance().nextString());
            inviteEntity = InviteEntity.newInstance(email, auth, inviteEntity.getInvited(), invitedBy);
            inviteService.save(inviteEntity);
            return inviteEntity;
        } catch (Exception exception) {
            LOG.error("Error occurred during creation of invited user={} reason={}",
                    email, exception.getLocalizedMessage(), exception);
            return null;
        }
    }

    private void formulateInvitationMail(String email, UserAccountEntity invitedBy, InviteEntity inviteEntity) {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("from", invitedBy.getName());
        rootMap.put("fromEmail", invitedBy.getUserId());
        rootMap.put("to", email);
        rootMap.put("link", inviteEntity.getAuthenticationKey());
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            MimeMessage message = mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(inviteeEmail, emailAddressName));
            helper.setTo(StringUtils.isEmpty(devSentTo) ? email : devSentTo);
            LOG.info("Invitation send to={}", StringUtils.isEmpty(devSentTo) ? email : devSentTo);
            sendMail(
                    mailInviteSubject + " - " + invitedBy.getName(),
                    freemarkerToString("mail/invite.ftl", rootMap),
                    message,
                    helper
            );
        } catch (TemplateException | IOException | MessagingException exception) {
            LOG.error("Invitation failure email inviteId={}, for={}",
                    inviteEntity.getId(), inviteEntity.getEmail(), exception);
            throw new RuntimeException(exception);
        }
    }

    private void sendMail(
            String subject,
            String text,
            MimeMessage message,
            MimeMessageHelper helper
    ) throws MessagingException {
        // use the true flag to indicate the text included is HTML
        helper.setText(text, true);
        helper.setSubject(subject);

        //Attach image always at the end
        if (subject.startsWith(mailInviteSubject)) {
            //Attach image always at the end
            helper.addInline("googlePlus.logo", getFileSystemResource(googleSmall));
            helper.addInline("facebook.logo", getFileSystemResource(facebookSmall));
            helper.addInline("ios.logo", getFileSystemResource(appStore));
            helper.addInline("android.logo", getFileSystemResource(googlePlay));
        }

        try {
            int count = 0;
            boolean connected = false;
            while (!connected && count < 10) {
                count ++;
                try {
                    mailSender.testConnection();
                    connected = true;
                } catch (MessagingException m) {
                    LOG.error("Failed to connect with mail server count={} reason={}", count, m.getLocalizedMessage(), m);
                }
            }

            count = 0;
            boolean noAuthenticationException = false;
            while (!noAuthenticationException && count < 10) {
                count ++;
                try {
                    mailSender.send(message);
                    noAuthenticationException = true;
                } catch (MailAuthenticationException e) {
                    LOG.error("Failed to send mail server count={} reason={}", count, e.getLocalizedMessage(), e);
                }
            }
        } catch (MailSendException mailSendException) {
            LOG.error("Mail send exception={}", mailSendException.getLocalizedMessage());
            throw new MessagingException(mailSendException.getLocalizedMessage(), mailSendException);
        }
    }

    private FileSystemResource getFileSystemResource(String location) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(location);
        Assert.notNull(url, "File not found at location " + location);
        return new FileSystemResource(url.getPath());
    }

    private String freemarkerToString(String ftl, Map<String, String> rootMap) throws IOException, TemplateException {
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        Template template = cfg.getTemplate(ftl);
        return processTemplateIntoString(template, rootMap);
    }

    /**
     * When invitation fails remove all the reference to the Invitation and the new user.
     *
     * @param invite
     */
    private void deleteInvite(InviteEntity invite) {
        LOG.info("Deleting: Profile, Auth, Preferences, Invite as the invitation message failed to sent");
        UserProfileEntity userProfile = accountService.doesUserExists(invite.getEmail());
        UserAccountEntity userAccount = loginService.findByReceiptUserId(userProfile.getReceiptUserId());
        UserAuthenticationEntity userAuthenticationEntity = userAccount.getUserAuthentication();
        UserPreferenceEntity userPreferenceEntity = accountService.getPreference(userProfile);

        userProfilePreferenceService.deleteHard(userPreferenceEntity);
        userAuthenticationManager.deleteHard(userAuthenticationEntity);
        userAccountManager.deleteHard(userAccount);
        userProfilePreferenceService.deleteHard(userProfile);
        inviteService.deleteHard(invite);
    }

    public String sendInvite(String invitedUserEmail, String rid, String uid) {
        Boolean responseStatus = Boolean.FALSE;
        String responseMessage;
        boolean isValid = EmailValidator.getInstance().isValid(invitedUserEmail);
        if (isValid && !invitedUserEmail.equals(uid)) {
            UserProfileEntity userProfile = accountService.doesUserExists(invitedUserEmail);
            /**
             * Condition when the user does not exists then invite. Also allow re-invite if the user is not active and
             * is not deleted. The second condition could result in a bug when administrator has made the user inactive.
             * Best solution is to add automated re-invite using quartz/cron job. Make sure there is a count kept to
             * limit the number of invite.
             */
            if (null == userProfile || !userProfile.isActive() && !userProfile.isDeleted()) {
                responseStatus = invokeCorrectInvitation(invitedUserEmail, rid, userProfile);
                if (responseStatus) {
                    notificationService.addNotification(
                            "Invitation sent to '" + invitedUserEmail + "'",
                            NotificationTypeEnum.MESSAGE,
                            NotificationGroupEnum.S,
                            rid);

                    responseMessage = "Invitation Sent to: " + StringUtils.abbreviate(invitedUserEmail, 26);
                } else {
                    notificationService.addNotification(
                            "Unsuccessful in sending invitation to '" + invitedUserEmail + "'",
                            NotificationTypeEnum.MESSAGE,
                            NotificationGroupEnum.S,
                            rid);

                    responseMessage = "Unsuccessful in sending invitation: " + StringUtils.abbreviate(invitedUserEmail, 26);
                }
            } else if (userProfile.isActive() && !userProfile.isDeleted()) {
                FriendEntity friend = friendService.getConnection(rid, userProfile.getReceiptUserId());
                if (null != friend && !friend.isConnected()) {
                    /** Auto connect if invited friend is connecting to invitee. */
                    if (friend.getFriendUserId().equalsIgnoreCase(rid)) {
                        friend.acceptConnection();
                        friend.connect();
                        friendService.save(friend);

                        notificationService.addNotification(
                                "New connection with " + userProfile.getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                rid);

                        notificationService.addNotification(
                                "New connection with " + accountService.doesUserExists(uid).getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                userProfile.getReceiptUserId());
                    } else if (StringUtils.isNotBlank(friend.getUnfriendUser())) {
                        friend.connect();
                        friend.setUnfriendUser(null);
                        friendService.save(friend);

                        notificationService.addNotification(
                                "Re-connection with " + userProfile.getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                rid);

                        notificationService.addNotification(
                                "Re-connection with " + accountService.doesUserExists(uid).getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                userProfile.getReceiptUserId());
                    } else {
                        invokeCorrectInvitation(invitedUserEmail, rid, userProfile);

                        notificationService.addNotification(
                                "Invitation sent to '" + invitedUserEmail + "'",
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                rid);
                    }
                } else if (friend == null) {
                    friend = new FriendEntity(rid, userProfile.getReceiptUserId());
                    friendService.save(friend);

                    notificationService.addNotification(
                            "Sent friend request to " + userProfile.getName(),
                            NotificationTypeEnum.MESSAGE,
                            NotificationGroupEnum.S,
                            rid);

                    notificationService.addNotification(
                            "New friend request from " + accountService.doesUserExists(uid).getName(),
                            NotificationTypeEnum.MESSAGE,
                            NotificationGroupEnum.S,
                            userProfile.getReceiptUserId());
                }

                LOG.info("{}, already registered. Thanks! active={} deleted={}",
                        invitedUserEmail,
                        userProfile.isActive(),
                        userProfile.isDeleted());

                responseStatus = Boolean.TRUE;
                responseMessage = "Friend request sent to " + StringUtils.abbreviate(invitedUserEmail, 26);
            } else {
                LOG.info("{}, already registered but no longer with us. Appreciate! active={} deleted={}",
                        invitedUserEmail,
                        userProfile.isActive(),
                        userProfile.isDeleted());

                // TODO can put a condition to check or if user is still in invitation mode or has completed registration
                // TODO Based on either condition we can let user recover password or re-send invitation

                //Have to send a positive message
                responseStatus = Boolean.TRUE;
                responseMessage = StringUtils.abbreviate(invitedUserEmail, 26) + ", already invited. Appreciate!";
            }
        } else {
            if (!isValid) {
                responseMessage = "Invalid Email: " + StringUtils.abbreviate(invitedUserEmail, 26);
            } else {
                responseMessage = "You are registered.";
            }
        }

        LOG.info("Invite mail={} status={} message={}", invitedUserEmail, responseStatus, responseMessage);
        JsonObject response = new JsonObject();
        response.addProperty("status", responseStatus);
        response.addProperty("message", responseMessage);
        return new Gson().toJson(response);
    }

    private boolean invokeCorrectInvitation(String invitedUserEmail, String rid, UserProfileEntity userProfile) {
        boolean status;
        if (null == userProfile) {
            status = sendInvitation(invitedUserEmail, rid);
        } else {
            status = reSendInvitation(invitedUserEmail, rid);
        }
        return status;
    }
}
