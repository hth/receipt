package com.receiptofi.service;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
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
    private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;
    private EmailValidateService emailValidateService;
    private LoginService loginService;
    private UserAuthenticationManager userAuthenticationManager;
    private UserAccountManager userAccountManager;
    private UserProfilePreferenceService userProfilePreferenceService;
    private FriendService friendService;

    @Value ("${do.not.reply.email}")
    private String doNotReplyEmail;

    @Value ("${dev.sent.to}")
    private String devSentTo;

    @Value ("${invitee.email}")
    private String inviteeEmail;

    @Value ("${email.address.name}")
    private String emailAddressName;

    @Value ("${domain}")
    private String domain;

    @Value ("${https}")
    private String https;

    @Value ("${mail.invite.subject}")
    private String mailInviteSubject;

    @Value ("${mail.recover.subject}")
    private String mailRecoverSubject;

    @Value ("${mail.validate.subject}")
    private String mailValidateSubject;

    @Value ("${mail.registration.active.subject}")
    private String mailRegistrationActiveSubject;

    @Value ("${mail.account.not.found}")
    private String accountNotFound;

    @Autowired
    public MailService(AccountService accountService,
                       InviteService inviteService,
                       JavaMailSenderImpl mailSender,

                       @SuppressWarnings ("SpringJavaAutowiringInspection")
                       FreeMarkerConfigurationFactoryBean freemarkerConfiguration,

                       EmailValidateService emailValidateService,
                       FriendService friendService,
                       LoginService loginService,
                       UserAuthenticationManager userAuthenticationManager,
                       UserAccountManager userAccountManager,
                       UserProfilePreferenceService userProfilePreferenceService
    ) {
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
                    helper.setTo(new InternetAddress(devSentTo, emailAddressName));
                    LOG.info("Mail account not found send to={}", devSentTo);
                } else {
                    helper.setTo(new InternetAddress(mail, userAccount.getName()));
                    LOG.info("Mail account not found send to={}", mail);
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
    public boolean sendInvitation(String invitedUserEmail, String invitedByRid) {
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
    public boolean reSendInvitation(String emailId, String invitedByRid) {
        UserAccountEntity invitedBy = accountService.findByReceiptUserId(invitedByRid);
        if (null != invitedBy) {
            FriendEntity friend = null;
            try {
                InviteEntity inviteEntity = inviteService.reInviteActiveInvite(emailId, invitedBy);
                boolean isNewInvite = false;
                if (null == inviteEntity) {
                    //Means invite exist by another user. Better to create a new invite for the requesting user
                    inviteEntity = reCreateAnotherInvite(emailId, invitedBy);
                    isNewInvite = true;

                    friend = new FriendEntity(invitedByRid, inviteEntity.getInvited().getReceiptUserId());
                    friendService.save(friend);
                }

                formulateInvitationMail(emailId, invitedBy, inviteEntity);
                if (!isNewInvite) {
                    inviteService.save(inviteEntity);
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
            LOG.error("Invitation failure email inviteId={}, for={}, exception={}",
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
        URL headerUrl = Thread.currentThread().getContextClassLoader().getResource("../jsp/images/headerReceiptApp.jpg");
        Assert.notNull(headerUrl);
        FileSystemResource headerRes = new FileSystemResource(headerUrl.getPath());
        helper.addInline("headerReceiptApp.logo", headerRes);

        if (subject.startsWith(mailInviteSubject)) {
            //Attach image always at the end
            URL googleUrl = Thread.currentThread().getContextClassLoader().getResource("../jsp/images/smallGoogle.jpg");
            Assert.notNull(googleUrl);
            FileSystemResource googleRes = new FileSystemResource(googleUrl.getPath());
            helper.addInline("googlePlus.logo", googleRes);

            //Attach image always at the end
            URL facebookUrl = Thread.currentThread().getContextClassLoader().getResource("../jsp/images/smallFacebook.jpg");
            Assert.notNull(facebookUrl);
            FileSystemResource facebookRes = new FileSystemResource(facebookUrl.getPath());
            helper.addInline("facebook.logo", facebookRes);

            //Attach image always at the end
            URL androidUrl = Thread.currentThread().getContextClassLoader().getResource("../jsp/images/googlePlay129x45.jpg");
            Assert.notNull(androidUrl);
            FileSystemResource androidRes = new FileSystemResource(androidUrl.getPath());
            helper.addInline("android.logo", androidRes);

            //Attach image always at the end
            URL iosUrl = Thread.currentThread().getContextClassLoader().getResource("../jsp/images/app_store_coming_soon129x45.jpg");
            Assert.notNull(iosUrl);
            FileSystemResource iosRes = new FileSystemResource(iosUrl.getPath());
            helper.addInline("ios.logo", iosRes);
        }

        try {
            mailSender.send(message);
        } catch (MailSendException mailSendException) {
            LOG.error("Mail send exception={}", mailSendException.getLocalizedMessage());
            throw new MessagingException(mailSendException.getLocalizedMessage(), mailSendException);
        }
    }

    private String freemarkerToString(String ftl, Map<String, String> rootMap) throws IOException, TemplateException {
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        Template template = cfg.getTemplate(ftl);
        return processTemplateIntoString(template, rootMap);
    }

    /**
     * When invitation fails remove all the reference to the Invitation and the new user.
     *
     * @param inviteEntity
     */
    private void deleteInvite(InviteEntity inviteEntity) {
        LOG.info("Deleting: Profile, Auth, Preferences, Invite as the invitation message failed to sent");
        UserProfileEntity userProfile = accountService.doesUserExists(inviteEntity.getEmail());
        UserAccountEntity userAccount = loginService.findByReceiptUserId(userProfile.getReceiptUserId());
        UserAuthenticationEntity userAuthenticationEntity = userAccount.getUserAuthentication();
        UserPreferenceEntity userPreferenceEntity = accountService.getPreference(userProfile);

        userProfilePreferenceService.deleteHard(userPreferenceEntity);
        userAuthenticationManager.deleteHard(userAuthenticationEntity);
        userAccountManager.deleteHard(userAccount);
        userProfilePreferenceService.deleteHard(userProfile);
        inviteService.deleteHard(inviteEntity);
    }
}
