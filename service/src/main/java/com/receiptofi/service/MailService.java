package com.receiptofi.service;

import static com.receiptofi.domain.types.UserLevelEnum.ACCOUNTANT;
import static com.receiptofi.domain.types.UserLevelEnum.USER;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.MailEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.MailStatusEnum;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.MailManager;
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
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    /** Not expecting more than 100 invites in a minute. */
    private static final int SIZE_100 = 100;

    private AccountService accountService;
    private InviteService inviteService;
    private FreeMarkerConfigurationFactory freemarkerConfiguration;
    private EmailValidateService emailValidateService;
    private UserAuthenticationManager userAuthenticationManager;
    private UserAccountManager userAccountManager;
    private UserProfilePreferenceService userProfilePreferenceService;
    private FriendService friendService;
    private NotificationService notificationService;
    private MailManager mailManager;

    private String devSentTo;
    private String inviteeEmail;
    private String emailAddressName;
    private String domain;
    private String https;
    private String mailInviteSubject;
    private String mailRecoverSubject;
    private String mailValidateSubject;
    private String mailRegistrationActiveSubject;
    private String accountNotFoundSubject;

    private final Cache<String, String> invitees;

    @Autowired
    public MailService(
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

            @Value ("${mail.account.not.found.subject}")
            String accountNotFoundSubject,

            AccountService accountService,
            InviteService inviteService,

            @SuppressWarnings ("SpringJavaAutowiringInspection")
            FreeMarkerConfigurationFactory freemarkerConfiguration,

            EmailValidateService emailValidateService,
            FriendService friendService,
            UserAuthenticationManager userAuthenticationManager,
            UserAccountManager userAccountManager,
            UserProfilePreferenceService userProfilePreferenceService,
            NotificationService notificationService,
            MailManager mailManager,

            @Value ("${MailService.inviteCachePeriod}")
            int inviteCachePeriod
    ) {

        this.devSentTo = devSentTo;
        this.inviteeEmail = inviteeEmail;
        this.emailAddressName = emailAddressName;
        this.domain = domain;
        this.https = https;
        this.mailInviteSubject = mailInviteSubject;
        this.mailRecoverSubject = mailRecoverSubject;
        this.mailValidateSubject = mailValidateSubject;
        this.mailRegistrationActiveSubject = mailRegistrationActiveSubject;
        this.accountNotFoundSubject = accountNotFoundSubject;

        this.accountService = accountService;
        this.inviteService = inviteService;
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.emailValidateService = emailValidateService;
        this.friendService = friendService;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userAccountManager = userAccountManager;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.notificationService = notificationService;
        this.mailManager = mailManager;

        invitees = CacheBuilder.newBuilder()
                .maximumSize(SIZE_100)
                .expireAfterWrite(inviteCachePeriod, TimeUnit.MINUTES)
                .build();
    }

    public boolean registrationCompleteEmail(String userId, String name) {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", name);
        rootMap.put("contact_email", userId);
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            LOG.info("Account registration sent to={}", StringUtils.isEmpty(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(name)
                    .setSubject(mailRegistrationActiveSubject)
                    .setMessage(freemarkerToString("mail/registration-active.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Registration failure email for={}", userId, exception);
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
            LOG.info("Account validation sent to={}", StringUtils.isEmpty(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(name)
                    .setSubject(mailValidateSubject)
                    .setMessage(freemarkerToString("mail/self-signup.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Validation failure email for={}", userId, exception);
            return false;
        }
        return true;
    }

    /**
     * Send recover email to user of provided email id.
     * http://bharatonjava.wordpress.com/2012/08/27/sending-email-using-java-mail-api/
     *
     * @param userId
     */
    public MailTypeEnum mailRecoverLink(String userId) {
        UserAccountEntity userAccount = accountService.findByUserId(userId);
        if (null == userAccount) {
            LOG.warn("Could not recover user={}", userId);

            Map<String, String> rootMap = new HashMap<>();
            rootMap.put("contact_email", userId);
            rootMap.put("domain", domain);
            rootMap.put("https", https);

            try {
                MailEntity mail = new MailEntity()
                        .setToMail(userId)
                        .setSubject(accountNotFoundSubject)
                        .setMessage(freemarkerToString("mail/account-recover-unregistered-user.ftl", rootMap))
                        .setMailStatus(MailStatusEnum.N);
                mailManager.save(mail);

                return MailTypeEnum.SUCCESS;
            } catch (IOException | TemplateException exception) {
                LOG.error("Account not found email={}", exception.getLocalizedMessage(), exception);
            }

            return MailTypeEnum.ACCOUNT_NOT_FOUND;
        }

        if (null != userAccount.getProviderId()) {
            /** Cannot change password for social account. Well this condition is checked in Mobile Server too. */
            LOG.warn("Social account user={} tried recovering password", userId);
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
                MailEntity mail = new MailEntity()
                        .setToMail(userId)
                        .setToName(userAccount.getName())
                        .setSubject(mailRecoverSubject)
                        .setMessage(freemarkerToString("mail/account-recover.ftl", rootMap))
                        .setMailStatus(MailStatusEnum.N);
                mailManager.save(mail);

                return MailTypeEnum.SUCCESS;
            } catch (IOException | TemplateException exception) {
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
     * Send mail of receipt processed when notification delivery has failed or no TOKEN is registered for the device
     * to receive notification.
     *
     * @param rid
     * @param message
     * @return
     */
    public MailTypeEnum sendReceiptProcessed(String rid, String message) {
        UserAccountEntity userAccount = accountService.findByReceiptUserId(rid);

        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", userAccount.getName());
        rootMap.put("notification", message);

        try {
            MailEntity mail = new MailEntity()
                    .setToMail(userAccount.getUserId())
                    .setToName(userAccount.getName())
                    .setSubject("ReceiptApp: " + message)
                    .setMessage(freemarkerToString("mail/receipt-processed.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);

            return MailTypeEnum.SUCCESS;
        } catch (IOException | TemplateException exception) {
            LOG.error("Receipt processed email={}", exception.getLocalizedMessage(), exception);
            return MailTypeEnum.FAILURE;
        }
    }

    private String freemarkerToString(String ftl, Map<String, String> rootMap) throws IOException, TemplateException {
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        Template template = cfg.getTemplate(ftl);
        return processTemplateIntoString(template, rootMap);
    }

    /**
     * Send Invite to user.
     *
     * @param invitedUserEmail Invitee's email address
     * @param rid              RID of person inviting
     * @param uid              UID of person inviting
     * @return
     */
    public String sendInvite(String invitedUserEmail, String rid, String uid) {
        LOG.info("invitedUserEmail={} by rid={} uid={}", invitedUserEmail, rid, uid);
        Boolean responseStatus = Boolean.FALSE;
        String responseMessage = null;
        boolean isValid = EmailValidator.getInstance().isValid(invitedUserEmail);

        if (isValid && !invitedUserEmail.equals(uid)) {
            UserProfileEntity invitedUserProfile = accountService.doesUserExists(invitedUserEmail);
            UserAccountEntity invitedUserAccount = null;
            if (invitedUserProfile != null) {
                invitedUserAccount = accountService.findByReceiptUserId(invitedUserProfile.getReceiptUserId());
            }

            if (StringUtils.isNotBlank(invitees.getIfPresent(invitedUserEmail))) {
                LOG.info("Duplicate Request invite={}, list max={} actual={}",
                        invitedUserEmail, SIZE_100, invitees.size());

                /**
                 * In case users is impatient and hits invite twice for same user. This will ensure same records
                 * are not created twice.
                 */
                return generateInviteResponse(false, "Almost ready, sending invitee to '" + invitedUserEmail + "'");
            }
            invitees.put(invitedUserEmail, invitedUserEmail);

            /**
             * Condition when the user does not exists then invite. Also allow re-invite if the user is not active and
             * is not deleted. The second condition could result in a bug when administrator has made the user inactive.
             * Best solution is to add automated re-invite using quartz/cron job. Make sure there is a count kept to
             * limit the number of invite.
             */
            if (null == invitedUserProfile
                    || null == invitedUserAccount
                    || (!invitedUserAccount.isActive() && !invitedUserAccount.isDeleted())) {

                responseStatus = invokeCorrectInvitation(invitedUserEmail, rid, invitedUserProfile, USER);
                responseMessage = addNotification(invitedUserEmail, rid, responseStatus);

            } else {
                FriendEntity friend = friendService.getConnection(rid, invitedUserProfile.getReceiptUserId());
                if (null != friend && !friend.isConnected()) {
                    /** Auto connect if invited friend is connecting to invitee. */
                    if (friend.getFriendUserId().equalsIgnoreCase(rid) && StringUtils.isBlank(friend.getUnfriendUser())) {
                        friend.acceptConnection();
                        friend.connect();
                        friendService.save(friend);

                        notificationService.addNotification(
                                "New connection with " + invitedUserProfile.getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                rid);

                        notificationService.addNotification(
                                "New connection with " + accountService.doesUserExists(uid).getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                invitedUserProfile.getReceiptUserId());

                        responseStatus = Boolean.TRUE;
                        responseMessage = "Connected with " + StringUtils.abbreviate(invitedUserEmail, 26);
                    } else if (friend.getUnfriendUser().equalsIgnoreCase(rid) && StringUtils.isNotBlank(friend.getUnfriendUser())) {
                        friend.connect();
                        friend.setUnfriendUser(null);
                        friendService.save(friend);

                        notificationService.addNotification(
                                "Re-connection with " + invitedUserProfile.getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                rid);

                        notificationService.addNotification(
                                "Re-connection with " + accountService.doesUserExists(uid).getName(),
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                invitedUserProfile.getReceiptUserId());

                        responseStatus = Boolean.TRUE;
                        responseMessage = "Connected with " + StringUtils.abbreviate(invitedUserEmail, 26);
                    } else {
                        notificationService.addNotification(
                                "Invitation sent to '" + invitedUserEmail + "'",
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                rid);

                        UserProfileEntity userProfileOfInvitee = accountService.doesUserExists(uid);
                        notificationService.addNotification(
                                "Re-connection requested by "
                                        + (userProfileOfInvitee == null ? uid : userProfileOfInvitee.getName())
                                        + ". Submit email '"
                                        + uid
                                        + "' in invite to connect.",
                                NotificationTypeEnum.MESSAGE,
                                NotificationGroupEnum.S,
                                invitedUserProfile.getReceiptUserId());

                        responseStatus = Boolean.TRUE;
                        responseMessage = "Invitation sent to " + StringUtils.abbreviate(invitedUserEmail, 26);
                    }
                } else if (friend == null) {
                    friend = new FriendEntity(rid, invitedUserProfile.getReceiptUserId());
                    friendService.save(friend);

                    notificationService.addNotification(
                            "Sent friend request to " + invitedUserProfile.getName(),
                            NotificationTypeEnum.MESSAGE,
                            NotificationGroupEnum.S,
                            rid);

                    notificationService.addNotification(
                            "New friend request from " + accountService.doesUserExists(uid).getName(),
                            NotificationTypeEnum.MESSAGE,
                            NotificationGroupEnum.S,
                            invitedUserProfile.getReceiptUserId());

                    responseStatus = Boolean.TRUE;
                    responseMessage = "Friend request sent to " + StringUtils.abbreviate(invitedUserEmail, 26);
                }

                LOG.info("{}, already registered. Setting invite. Thanks! active={} deleted={}",
                        invitedUserEmail,
                        invitedUserProfile.isActive(),
                        invitedUserProfile.isDeleted());
            }

            invitees.invalidate(invitedUserEmail);
        } else {
            if (!isValid) {
                responseMessage = "Invalid Email: " + StringUtils.abbreviate(invitedUserEmail, 26);
            } else {
                responseMessage = "You are registered.";
            }
        }

        LOG.info("Invite mail={} status={} message={}", invitedUserEmail, responseStatus, responseMessage);
        return generateInviteResponse(responseStatus, responseMessage);
    }

    /**
     * Send Accountant Invite to user.
     *
     * @param invitedUserEmail Invitee's email address
     * @param rid              RID of person inviting
     * @param uid              UID of person inviting
     * @return
     */
    public String sendAccountantInvite(String invitedUserEmail, String rid, String uid) {
        LOG.info("invitedUserEmail={} by rid={} uid={}", invitedUserEmail, rid, uid);
        Boolean responseStatus = Boolean.FALSE;
        String responseMessage;
        boolean isValid = EmailValidator.getInstance().isValid(invitedUserEmail);

        if (isValid && !invitedUserEmail.equals(uid)) {
            UserProfileEntity invitedUserProfile = accountService.doesUserExists(invitedUserEmail);
            if (StringUtils.isNotBlank(invitees.getIfPresent(invitedUserEmail))) {
                LOG.info("Duplicate Request invite={}, list max={} actual={}",
                        invitedUserEmail, SIZE_100, invitees.size());

                /**
                 * In case users is impatient and hits invite twice for same user. This will ensure same records
                 * are not created twice.
                 */
                return generateInviteResponse(false, "Almost ready, sending invitee to '" + invitedUserEmail + "'");
            }
            invitees.put(invitedUserEmail, invitedUserEmail);

            /**
             * Condition when the user does not exists then invite. Also allow re-invite if the user is not active and
             * is not deleted. The second condition could result in a bug when administrator has made the user inactive.
             * Best solution is to add automated re-invite using quartz/cron job. Make sure there is a count kept to
             * limit the number of invite.
             */
            responseStatus = invokeCorrectInvitation(invitedUserEmail, rid, invitedUserProfile, ACCOUNTANT);
            responseMessage = addNotification(invitedUserEmail, rid, responseStatus);

            invitees.invalidate(invitedUserEmail);
        } else {
            if (!isValid) {
                responseMessage = "Invalid Email: " + StringUtils.abbreviate(invitedUserEmail, 26);
            } else {
                responseMessage = "You are registered.";
            }
        }

        LOG.info("Invite mail={} status={} message={}", invitedUserEmail, responseStatus, responseMessage);
        return generateInviteResponse(responseStatus, responseMessage);
    }

    private String addNotification(String invitedUserEmail, String rid, Boolean responseStatus) {
        String responseMessage;
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
        return responseMessage;
    }

    private String generateInviteResponse(boolean responseStatus, String responseMessage) {
        JsonObject response = new JsonObject();
        response.addProperty("status", responseStatus);
        response.addProperty("message", responseMessage);
        return new Gson().toJson(response);
    }

    private boolean invokeCorrectInvitation(String invitedUserEmail, String rid, UserProfileEntity userProfile, UserLevelEnum userLevel) {
        boolean status;
        if (null == userProfile) {
            status = sendInvitation(invitedUserEmail, rid, userLevel);
        } else {
            status = reSendInvitation(invitedUserEmail, rid, userLevel);
        }
        return status;
    }

    /**
     * Send invite.
     *
     * @param invitedUserEmail Invited users email address
     * @param invitedByRid     Existing users email address
     * @return
     */
    private boolean sendInvitation(String invitedUserEmail, String invitedByRid, UserLevelEnum userLevel) {
        UserAccountEntity invitedBy = accountService.findByReceiptUserId(invitedByRid);
        if (invitedBy != null) {
            InviteEntity invite = null;
            try {
                invite = inviteService.initiateInvite(invitedUserEmail, invitedBy, userLevel);

                if (userLevel == USER) {
                    FriendEntity friend = new FriendEntity(invitedByRid, invite.getInvited().getReceiptUserId());
                    friendService.save(friend);
                }

                formulateInvitationMail(invitedUserEmail, invitedBy, invite);
                return true;
            } catch (RuntimeException e) {
                LOG.error("Error persisting InviteEntity, reason={}", e.getLocalizedMessage(), e);

                if (invite != null) {
                    friendService.deleteHard(invitedByRid, invite.getInvited().getReceiptUserId());
                    deleteInvite(invite);
                    LOG.warn("Due to failure in sending the invitation email. Deleting Invite={}, for={}",
                            invite.getId(), invite.getEmail());
                }
            }
        }
        return false;
    }

    /**
     * Re-send invite or send new invitation to existing (pending) invite to user.
     *
     * @param email        Invited users email address
     * @param invitedByRid Existing users rid
     * @return
     */
    private boolean reSendInvitation(String email, String invitedByRid, UserLevelEnum userLevel) {
        UserAccountEntity invitedBy = accountService.findByReceiptUserId(invitedByRid);
        if (null != invitedBy) {
            FriendEntity friend = null;
            try {
                InviteEntity invite = inviteService.reInviteActiveInvite(email, invitedBy);
                boolean isNewInvite = false;
                if (null == invite) {
                    /**
                     * Means invite may exists through another user.
                     * Better to create a new invite for the requesting user.
                     */
                    invite = reCreateAnotherInvite(email, invitedBy, userLevel);
                    if (null == invite) {
                        return false;
                    }

                    isNewInvite = true;
                    if (userLevel == USER) {
                        friend = new FriendEntity(invitedByRid, invite.getInvited().getReceiptUserId());
                        friendService.save(friend);
                    }
                } else {
                    invite.isActive();
                    inviteService.save(invite);
                }

                /* Users can be in friends list. */
                if (null == friend && userLevel == USER) {
                    friend = friendService.getConnection(invitedByRid, invite.getInvited().getReceiptUserId());
                    if (!friend.isConnected() && friend.getReceiptUserId().equalsIgnoreCase(invitedByRid)) {
                        /** When invitee has cancelled interest in their friend and would like to re-invite. */
                        if (friendService.inviteAgain(friend.getId(), RandomString.newInstance().nextString())) {
                            LOG.info("Re-invite connection from {} to {}", invitedByRid, invite.getInvited().getReceiptUserId());
                        }
                    }
                }

                formulateInvitationMail(email, invitedBy, invite);
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
    private InviteEntity reCreateAnotherInvite(String email, UserAccountEntity invitedBy, UserLevelEnum userLevel) {
        UserProfileEntity userProfile = accountService.doesUserExists(email);
        try {
            String auth = HashText.computeBCrypt(RandomString.newInstance().nextString());
            InviteEntity invite = InviteEntity.newInstance(email, auth, userProfile, invitedBy, userLevel);
            inviteService.save(invite);
            return invite;
        } catch (Exception exception) {
            LOG.error("Error occurred during creation of invited user={} reason={}",
                    email, exception.getLocalizedMessage(), exception);
            return null;
        }
    }

    private void formulateInvitationMail(String email, UserAccountEntity invitedBy, InviteEntity invite) {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("from", invitedBy.getName());
        rootMap.put("fromEmail", invitedBy.getUserId());
        rootMap.put("to", email);
        rootMap.put("link", invite.getAuthenticationKey());
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            String message;
            switch (invite.getUserLevel()) {
                case USER:
                    message = freemarkerToString("mail/invite.ftl", rootMap);
                    break;
                case ACCOUNTANT:
                    message = freemarkerToString("mail/inviteAccountant.ftl", rootMap);
                    break;
                default:
                    throw new RuntimeException("");
            }
            LOG.info("Invitation send to={}", StringUtils.isEmpty(devSentTo) ? email : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(StringUtils.isEmpty(devSentTo) ? email : devSentTo)
                    .setFromMail(inviteeEmail)
                    .setFromName(emailAddressName)
                    .setSubject(mailInviteSubject + " - " + invitedBy.getName())
                    .setMessage(message)
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (TemplateException | IOException exception) {
            LOG.error("Invitation failure email inviteId={}, for={}", invite.getId(), invite.getEmail(), exception);
            throw new RuntimeException(exception);
        }
    }

    /**
     * When invitation fails remove all the reference to the Invitation and the new user.
     *
     * @param invite
     */
    private void deleteInvite(InviteEntity invite) {
        LOG.info("Deleting: Profile, Auth, Preferences, Invite as the invitation message failed to sent");
        UserProfileEntity userProfile = accountService.doesUserExists(invite.getEmail());
        UserAccountEntity userAccount = accountService.findByReceiptUserId(userProfile.getReceiptUserId());
        UserAuthenticationEntity userAuthenticationEntity = userAccount.getUserAuthentication();
        UserPreferenceEntity userPreferenceEntity = accountService.getPreference(userProfile);

        userProfilePreferenceService.deleteHard(userPreferenceEntity);
        userAuthenticationManager.deleteHard(userAuthenticationEntity);
        userAccountManager.deleteHard(userAccount);
        userProfilePreferenceService.deleteHard(userProfile);
        inviteService.deleteHard(invite);
    }
}
