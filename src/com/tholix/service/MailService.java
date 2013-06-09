package com.tholix.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import com.tholix.domain.ForgotRecoverEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 10:20 AM
 */
@Service
public class MailService {
    private static Logger log = Logger.getLogger(MailService.class);

    private static final String MAIL_RECOVER_SUBJECT = "How to reset your Receipt-O-Fi ID password.";

    @Autowired private AccountService accountService;
    @Autowired private MailSender mailSender;
    @Autowired private SimpleMailMessage simpleMailMessage;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;

    /**
     * Send recover email to user of provided email id
     *
     * @param emailId
     */
    public void mailRecoverLink(String emailId) {
        UserProfileEntity userProfileEntity =  accountService.findIfUserExists(emailId);
        if(userProfileEntity != null) {
            try {
                Configuration cfg = freemarkerConfiguration.createConfiguration();
                Template template = cfg.getTemplate("text-account-recover.ftl");
                final String text = processPasswordRest(template, userProfileEntity);

                try {
                    //TODO change this to real user id instead
                    simpleMailMessage.setTo("admin@tholix.com");
                    simpleMailMessage.setSubject(MAIL_RECOVER_SUBJECT);
                    simpleMailMessage.setText(text);

                    mailSender.send(simpleMailMessage);
                } catch(MailException exception) {
                    log.error("Eat exception during sending and formulating email: " + exception.getLocalizedMessage());
                }
            } catch (IOException | TemplateException exception) {
                log.error("Eat exception during sending and formulating email: " + exception.getLocalizedMessage());
            }
        }
    }

    private String processPasswordRest(Template template, UserProfileEntity userProfileEntity) throws IOException, TemplateException {
        ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(userProfileEntity);

        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", userProfileEntity.getName());
        rootMap.put("link", forgotRecoverEntity.getAuthenticationKey());
        return processTemplateIntoString(template, rootMap);
    }
}
