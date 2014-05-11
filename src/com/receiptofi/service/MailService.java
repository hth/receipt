package com.receiptofi.service;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.InviteManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 10:20 AM
 */
@Service
public final class MailService {
    private static Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired private AccountService accountService;
    @Autowired private InviteService inviteService;
    @Autowired private JavaMailSenderImpl mailSender;
    @Autowired private LoginService loginService;

    @Autowired private InviteManager inviteManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserAuthenticationManager userAuthenticationManager;
    @Autowired private UserPreferenceManager userPreferenceManager;
    @Autowired private UserAccountManager userAccountManager;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;

    @Value("${do.not.reply.email}")
    private String doNotReplyEmail;

    @Value("${dev.sent.to}")
    private String devSentTo;

    @Value("${invitee.email}")
    private String inviteeEmail;

    @Value("${email.address.name}")
    private String emailAddressName;

    @Value("${domain}")
    private String domain;

    @Value("${https}")
    private String https;

    @Value("${mail.invite.subject}")
    private String mailInviteSubject;

    @Value("${mail.recover.subject}")
    private String mailRecoverSubject;

    /**
     * Send recover email to user of provided email id
     *
     * http://bharatonjava.wordpress.com/2012/08/27/sending-email-using-java-mail-api/
     * @param emailId
     */
    public boolean mailRecoverLink(String emailId) {
        UserProfileEntity userProfileEntity =  accountService.findIfUserExists(emailId);
        if(userProfileEntity != null) {
            try {
                ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(userProfileEntity);

                Map<String, String> rootMap = new HashMap<>();
                rootMap.put("to", userProfileEntity.getName());
                rootMap.put("link", forgotRecoverEntity.getAuthenticationKey());
                rootMap.put("domain", domain);
                rootMap.put("https", https);

                try {
                    Configuration cfg = freemarkerConfiguration.createConfiguration();
                    Template template = cfg.getTemplate("text-account-recover.ftl");
                    final String text = processTemplateIntoString(template, rootMap);

                    MimeMessage message = mailSender.createMimeMessage();

                    // use the true flag to indicate you need a multipart message
                    MimeMessageHelper helper = new MimeMessageHelper(message, true , "UTF-8");
                    helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));

                    String sentTo = !StringUtils.isEmpty(devSentTo) ? devSentTo : emailId;
                    if(!sentTo.equalsIgnoreCase(devSentTo)) {
                        helper.setTo(new InternetAddress(emailId, userProfileEntity.getName()));
                    } else {
                        helper.setTo(new InternetAddress(devSentTo, emailAddressName));
                    }
                    log.info("Mail recovery send to : " + (!StringUtils.isEmpty(devSentTo) ? devSentTo : emailId));

                    // use the true flag to indicate the text included is HTML
                    helper.setText(text, true);
                    helper.setSubject(userProfileEntity.getName() + ": " + mailRecoverSubject);

                    //Attach image always at the end
                    URL url = this.getClass().getClassLoader().getResource("../jsp/images/receipt-o-fi.logo.jpg");
                    assert url != null;
                    FileSystemResource res = new FileSystemResource(url.getPath());
                    helper.addInline("receiptofi.logo", res);

                    mailSender.send(message);
                } catch (IOException | TemplateException exception) {
                    log.error("Exception during sending and formulating email: " + exception.getLocalizedMessage());
                    return false;
                }
            } catch(Exception exception) {
                log.error("Exception occurred during persisting ForgotRecoverEntity: " + exception.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Used in sending the invitation for the first time
     *
     * @param emailId Invited users email address
     * @param userProfileEmailId Existing users email address
     * @return
     */
    public boolean sendInvitation(String emailId, String userProfileEmailId) {
        UserProfileEntity userProfileEntity =  accountService.findIfUserExists(userProfileEmailId);
        if(userProfileEntity != null) {
            InviteEntity inviteEntity = null;
            try {
                inviteEntity = inviteService.initiateInvite(emailId, userProfileEntity);
                formulateInvitationEmail(emailId, userProfileEntity, inviteEntity);
            } catch (Exception exception) {
                if(inviteEntity != null) {
                    deleteInvite(inviteEntity);
                    log.info("Due to failure in sending the invitation email. Deleting Invite: " + inviteEntity.getId() + ", for: " + inviteEntity.getEmailId());
                }
                log.error("Exception occurred during persisting InviteEntity: " + exception.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Helps in re-sending the invitation or to send new invitation to existing (pending) invitation by a new user.
     *
     * @param emailId Invited users email address
     * @param userProfileEmailId Existing users email address
     * @return
     */
    public boolean reSendInvitation(String emailId, String userProfileEmailId) {
        UserProfileEntity userProfileEntity =  accountService.findIfUserExists(userProfileEmailId);
        if(userProfileEntity != null) {
            try {
                InviteEntity inviteEntity = inviteService.reInviteActiveInvite(emailId, userProfileEntity);
                boolean isNewInvite = false;
                if(inviteEntity == null) {
                    //Means invite exist by another user. Better to create a new invite for the requesting user
                    inviteEntity = reCreateAnotherInvite(emailId, userProfileEntity);
                    isNewInvite = true;
                }
                formulateInvitationEmail(emailId, userProfileEntity, inviteEntity);
                if(!isNewInvite) {
                    inviteManager.save(inviteEntity);
                }
            } catch (Exception exception) {
                log.error("Exception occurred during persisting InviteEntity: " + exception.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Invitation is created by the new user
     *
     * @param emailId
     * @param userProfile
     * @return
     */
    public InviteEntity reCreateAnotherInvite(String emailId, UserProfileEntity userProfile) {
        InviteEntity inviteEntity = inviteService.find(emailId);
        try {
            String auth = HashText.computeBCrypt(RandomString.newInstance().nextString());
            inviteEntity = InviteEntity.newInstance(emailId, auth, inviteEntity.getInvited(), userProfile);
            inviteManager.save(inviteEntity);
            return inviteEntity;
        } catch (Exception exception) {
            log.error("Error occurred during creation of invited user: " + exception.getLocalizedMessage());
            return null;
        }
    }

    private void formulateInvitationEmail(String emailId, UserProfileEntity userProfileEntity, InviteEntity inviteEntity) throws Exception {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("from", userProfileEntity.getName());
        rootMap.put("fromEmail", userProfileEntity.getEmail());
        rootMap.put("to", emailId);
        rootMap.put("link", inviteEntity.getAuthenticationKey());
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            Configuration cfg = freemarkerConfiguration.createConfiguration();
            Template template = cfg.getTemplate("text-invite.ftl");
            final String text = processTemplateIntoString(template, rootMap);

            MimeMessage message = mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(inviteeEmail, emailAddressName));
            helper.setTo(!StringUtils.isEmpty(devSentTo) ? devSentTo : emailId);
            log.info("Invitation send to : " + (!StringUtils.isEmpty(devSentTo) ? devSentTo : emailId));

            // use the true flag to indicate the text included is HTML
            helper.setText(text, true);
            helper.setSubject(mailInviteSubject + " - " + userProfileEntity.getName());

            //Attach image always at the end
            URL url = this.getClass().getClassLoader().getResource("../jsp/images/receipt-o-fi.logo.jpg");
            assert url != null;
            FileSystemResource res = new FileSystemResource(url.getPath());
            helper.addInline("receiptofi.logo", res);

            mailSender.send(message);
        } catch(TemplateException | IOException exception) {
            log.error("Exception during sending and formulating email: " + exception.getLocalizedMessage());
            log.info("Failure in sending the invitation email: " + inviteEntity.getId() + ", for: " + inviteEntity.getEmailId());
            throw new Exception(exception);
        }
    }

    /**
     * When invitation fails remove all the reference to the Invitation and the new user
     *
     * @param inviteEntity
     */
    private void deleteInvite(InviteEntity inviteEntity) {
        log.info("Deleting: Profile, Auth, Preferences, Invite as the invitation message failed to sent");
        UserProfileEntity userProfile = accountService.findIfUserExists(inviteEntity.getEmailId());
        UserAccountEntity userAccount = loginService.loadUserAccount(userProfile.getReceiptUserId());
        UserAuthenticationEntity userAuthenticationEntity = userAccount.getUserAuthentication();
        UserPreferenceEntity userPreferenceEntity = accountService.getPreference(userProfile);
        userPreferenceManager.deleteHard(userPreferenceEntity);
        userAuthenticationManager.deleteHard(userAuthenticationEntity);
        userAccountManager.deleteHard(userAccount);
        userProfileManager.deleteHard(userProfile);
        inviteManager.deleteHard(inviteEntity);
    }
}
