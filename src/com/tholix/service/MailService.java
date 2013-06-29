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
import com.tholix.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 10:20 AM
 */
@Service
public class MailService {
    private static Logger log = Logger.getLogger(MailService.class);

    private static final String MAIL_RECOVER_SUBJECT = "How to reset your Receipt-O-Fi ID password";
    private static final String MAIL_INVITE_SUBJECT = "Receipt-O-Fi invites you on behalf of";

    @Autowired private AccountService accountService;
    @Autowired private InviteService inviteService;
    @Autowired private MailSender mailSender;
    @Autowired private SimpleMailMessage simpleMailMessage;

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
            try {
                InviteEntity inviteEntity = inviteService.initiateInvite(emailId, userProfileEntity);

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
                    return false;
                }
            } catch (Exception exception) {
                log.error("Exception occurred during persisting InviteEntity: " + exception.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }
}
