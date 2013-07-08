package com.tholix.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import com.tholix.domain.ForgotRecoverEntity;
import com.tholix.domain.InviteEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.InviteManager;
import com.tholix.repository.UserAuthenticationManager;
import com.tholix.repository.UserPreferenceManager;
import com.tholix.repository.UserProfileManager;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 10:20 AM
 */
@Service
public class MailService {
    private static Logger log = Logger.getLogger(MailService.class);

    private static final String MAIL_RECOVER_SUBJECT = "[r] How to reset your receipt-o-fi ID password";
    private static final String MAIL_INVITE_SUBJECT = "[r] receipt-o-fi invites you on behalf of";

    @Autowired private AccountService accountService;
    @Autowired private InviteService inviteService;
    @Autowired private MailSender mailSender;
    @Autowired private SimpleMailMessage simpleMailMessage;

    @Autowired private InviteManager inviteManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserAuthenticationManager userAuthenticationManager;
    @Autowired private UserPreferenceManager userPreferenceManager;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;

    @Value("${do.not.reply.email}")
    private String doNotReplyEmail;

    @Value("${dev.sent.to}")
    private String devSentTo;

    @Value("${invitee.email}")
    private String inviteeEmail;

    /**
     * Send recover email to user of provided email id
     *
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

                try {
                    Configuration cfg = freemarkerConfiguration.createConfiguration();
                    Template template = cfg.getTemplate("text-account-recover.ftl");
                    final String text = processTemplateIntoString(template, rootMap);

                    simpleMailMessage.setFrom(doNotReplyEmail);
                    simpleMailMessage.setTo(!StringUtils.isEmpty(devSentTo) ? devSentTo : emailId);

                    simpleMailMessage.setText(text);
                    simpleMailMessage.setSubject(MAIL_RECOVER_SUBJECT);

                    mailSender.send(simpleMailMessage);
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

    public boolean sendInvitation(String emailId, String userProfileEmailId) {
        UserProfileEntity userProfileEntity =  accountService.findIfUserExists(userProfileEmailId);
        if(userProfileEntity != null) {
            InviteEntity inviteEntity = null;
            try {
                inviteEntity = inviteService.initiateInvite(emailId, userProfileEntity);

                Map<String, String> rootMap = new HashMap<>();
                rootMap.put("from", userProfileEntity.getName());
                rootMap.put("fromEmail", userProfileEntity.getEmailId());
                rootMap.put("to", emailId);
                rootMap.put("link", inviteEntity.getAuthenticationKey());

                try {
                    Configuration cfg = freemarkerConfiguration.createConfiguration();
                    Template template = cfg.getTemplate("text-invite.ftl");
                    final String text = processTemplateIntoString(template, rootMap);

                    simpleMailMessage.setFrom(inviteeEmail);
                    simpleMailMessage.setTo(!StringUtils.isEmpty(devSentTo) ? devSentTo : emailId);

                    simpleMailMessage.setSubject(MAIL_INVITE_SUBJECT + " - " + userProfileEntity.getName());
                    simpleMailMessage.setText(text);

                    mailSender.send(simpleMailMessage);
                } catch(TemplateException | IOException exception) {
                    log.error("Exception during sending and formulating email: " + exception.getLocalizedMessage());
                    deleteInvite(inviteEntity);
                    log.info("Due to failure in sending the invitation email. Deleting Invite: " + inviteEntity.getId() + ", for: " + inviteEntity.getEmailId());
                    return false;
                }
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
     * When invitation fails remove all the reference to the Invitation and the new user
     *
     * @param inviteEntity
     */
    private void deleteInvite(InviteEntity inviteEntity) {
        log.info("Deleting: Profile, Auth, Preferences, Invite as the invitation message failed to sent");
        UserProfileEntity userProfileEntity = accountService.findIfUserExists(inviteEntity.getEmailId());
        UserAuthenticationEntity userAuthenticationEntity = userProfileEntity.getUserAuthentication();
        UserPreferenceEntity userPreferenceEntity = accountService.getPreference(userProfileEntity);
        userPreferenceManager.deleteHard(userPreferenceEntity);
        userAuthenticationManager.deleteHard(userAuthenticationEntity);
        userProfileManager.deleteHard(userProfileEntity);
        inviteManager.deleteHard(inviteEntity);
    }
}
