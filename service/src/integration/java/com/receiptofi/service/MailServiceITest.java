package com.receiptofi.service;

import com.receiptofi.ITest;
import com.receiptofi.LoadProperties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * User: hitender
 * Date: 4/8/16 11:05 AM
 */
public class MailServiceITest extends ITest {

    private JavaMailSenderImpl mailSender;
    private FreeMarkerConfigurationFactory freemarkerConfiguration;

    private MailService mailService;

    @Before
    public void classSetup() throws IOException {
        if (!properties.getProperty("braintree.environment").equalsIgnoreCase("PRODUCTION")) {
            mailSender = new JavaMailSenderImpl();
            Properties mailProperties = new Properties();
            mailProperties.put("mail.transport.protocol", properties.get("smtpProtocol"));
            mailProperties.put("mail.smtp.auth", true);
            mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailProperties.put("mail.smtp.port", properties.get("sslMailPort"));
            mailProperties.put("mail.smtp.socketFactory.port", properties.get("sslMailPort"));
            mailProperties.put("mail.debug", true);
            mailSender.setJavaMailProperties(mailProperties);

            mailSender.setPort(Integer.parseInt(properties.getProperty("sslMailPort")));
            mailSender.setDefaultEncoding(properties.getProperty("mailEncoding"));
            mailSender.setHost(properties.getProperty("goDaddyMailHost"));
            mailSender.setUsername(properties.getProperty("goDaddyUsername"));
            mailSender.setPassword(properties.getProperty("goDaddy"));
        } else {
            mailSender = super.mailSender;
        }

        freemarkerConfiguration = new FreeMarkerConfigurationFactory();
        TemplateLoader templateLoader = new FileTemplateLoader(LoadProperties.getFreemarkerLocation());
        freemarkerConfiguration.setPreTemplateLoaders(templateLoader);

        mailService = new MailService(
                properties.getProperty("do.not.reply.email"),
                properties.getProperty("dev.sent.to"),
                properties.getProperty("invitee.email"),
                properties.getProperty("email.address.name"),
                properties.getProperty("domain"),
                properties.getProperty("https"),
                properties.getProperty("mail.invite.subject"),
                properties.getProperty("mail.recover.subject"),
                properties.getProperty("mail.validate.subject"),
                properties.getProperty("mail.registration.active.subject"),
                properties.getProperty("mail.account.not.found"),
                getFile().getPath(),
                getFile().getPath(),
                getFile().getPath(),
                getFile().getPath(),
                accountService,
                inviteService,
                mailSender,
                freemarkerConfiguration,
                emailValidateService,
                friendService,
                loginService,
                userAuthenticationManager,
                userAccountManager,
                userProfilePreferenceService,
                notificationService
        );
    }

    @Test
    public void registrationCompleteEmail() throws Exception {
        mailService.registrationCompleteEmail("test@receiptofi.com", "Test");
    }

    @Test
    public void accountValidationMail() throws Exception {

    }

    @Test
    public void mailRecoverLink() throws Exception {

    }

    @Test
    public void sendInvite() throws Exception {

    }
}
