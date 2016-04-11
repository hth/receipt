package com.receiptofi.service;

import static com.receiptofi.domain.types.MailTypeEnum.ACCOUNT_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.receiptofi.ITest;
import com.receiptofi.LoadResource;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.utils.DateUtil;

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

        mailService = new MailService(
                properties.getProperty("do.not.reply.email"),
                properties.getProperty("dev.sent.to"),
                properties.getProperty("invitee.email"),
                properties.getProperty("email.address.name"),
                properties.getProperty("domain"),
                properties.getProperty("https"),
                "TEST::" + properties.getProperty("mail.invite.subject"),
                "TEST::" + properties.getProperty("mail.recover.subject"),
                "TEST::" + properties.getProperty("mail.validate.subject"),
                "TEST::" + properties.getProperty("mail.registration.active.subject"),
                "TEST::" + properties.getProperty("mail.account.not.found"),
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
        assertTrue("Sent invitation", mailService.registrationCompleteEmail("test@receiptofi.com", "Test"));
    }

    @Test
    public void accountValidationMail() throws Exception {
        assertTrue("Account Validation", mailService.accountValidationMail("test@receiptofi.com", "Test", "$someCodeForAuth"));
    }

    @Test
    public void mailRecoverLink() throws Exception {
        assertEquals("Account not found but success in sending email",
                MailTypeEnum.SUCCESS,
                mailService.mailRecoverLink("delete@receiptofi.com"));

        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "delete@receiptofi.com",
                "First",
                "Name",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        assertEquals("Account not validated",
                MailTypeEnum.ACCOUNT_NOT_VALIDATED,
                mailService.mailRecoverLink("delete@receiptofi.com"));

        /** Validated account. */
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());
        assertEquals("Account recovery link sent",
                MailTypeEnum.SUCCESS,
                mailService.mailRecoverLink("delete@receiptofi.com"));
    }

    @Test
    public void sendInvite() throws Exception {

    }
}
