package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.MailEntity;
import com.receiptofi.domain.types.MailStatusEnum;
import com.receiptofi.repository.MailManager;
import com.receiptofi.service.CronStatsService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * User: hitender
 * Date: 7/11/16 9:51 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class MailProcess {
    private static final Logger LOG = LoggerFactory.getLogger(MailProcess.class);

    private final String doNotReplyEmail;
    private final String emailAddressName;
    private final String devSentTo;
    private final String mailInviteSubject;
    private final String googleSmall;
    private final String facebookSmall;
    private final String appStore;
    private final String googlePlay;
    private final String emailSwitch;
    private final int sendAttempt;
    private final String dkimPath;

    private JavaMailSenderImpl mailSender;
    private MailManager mailManager;
    private CronStatsService cronStatsService;

    @Autowired
    public MailProcess(
            @Value ("${do.not.reply.email}")
            String doNotReplyEmail,

            @Value ("${email.address.name}")
            String emailAddressName,

            @Value ("${dev.sent.to}")
            String devSentTo,

            @Value ("${mail.invite.subject}")
            String mailInviteSubject,

            @Value ("${mail.googleSmall:..//jsp//images//smallGoogle.jpg}")
            String googleSmall,

            @Value ("${mail.googlePlay:..//jsp//images//googlePlay151x47.jpg}")
            String googlePlay,

            @Value ("${mail.facebookSmall:..//jsp//images//smallFacebook.jpg}")
            String facebookSmall,

            @Value ("${mail.appStore:..//jsp//images//app-store151x48.jpg}")
            String appStore,

            @Value ("${MailProcess.emailSwitch}")
            String emailSwitch,

            @Value ("${MailProcess.sendAttempt}")
            int sendAttempt,

            @Value ("${MailProcess.dkim.der.path}")
            String dkimPath,

            JavaMailSenderImpl mailSender,
            MailManager mailManager,
            CronStatsService cronStatsService
    ) {
        this.doNotReplyEmail = doNotReplyEmail;
        this.emailAddressName = emailAddressName;
        this.devSentTo = devSentTo;
        this.mailInviteSubject = mailInviteSubject;
        this.googleSmall = googleSmall;
        this.googlePlay = googlePlay;
        this.facebookSmall = facebookSmall;
        this.appStore = appStore;
        this.emailSwitch = emailSwitch;
        this.sendAttempt = sendAttempt;
        this.dkimPath = dkimPath;

        this.mailSender = mailSender;
        this.mailManager = mailManager;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (fixedDelayString = "${loader.MailProcess.sendMail}")
    public void sendMail() {
        CronStatsEntity cronStats = new CronStatsEntity(
                MailProcess.class.getName(),
                "SendMail",
                emailSwitch);

        if ("OFF".equalsIgnoreCase(emailSwitch)) {
            return;
        }

        List<MailEntity> mails = mailManager.pendingMails();
        if (mails.isEmpty()) {
            /** No documents to upload. */
            return;
        } else {
            LOG.info("Mail to send, count={}", mails.size());
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (MailEntity mail : mails) {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = populateMessageBody(mail, message);
                    sendMail(mail, message, helper);
                    mailManager.updateMail(mail.getId(), MailStatusEnum.S);
                    success++;
                } catch (MessagingException | UnsupportedEncodingException e) {
                    LOG.error("Failure sending email={} subject={} reason={}", mail.getToMail(), mail.getSubject(), e.getLocalizedMessage(), e);
                    if (sendAttempt < mail.getAttempts()) {
                        mailManager.updateMail(mail.getId(), MailStatusEnum.N);
                        failure++;
                    } else {
                        mailManager.updateMail(mail.getId(), MailStatusEnum.F);
                        skipped++;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending mail reason={}", e.getLocalizedMessage(), e);
        } finally {
            if (0 < skipped) {
                LOG.error("Skipped sending mail. Number of attempts exceeded. Take a look.");
            }
            saveUploadStats(cronStats, success, failure, skipped, mails.size());
        }
    }

    private void sendMail(
            MailEntity mail,
            MimeMessage message,
            MimeMessageHelper helper
    ) throws MessagingException {
        /** Use the true flag to indicate the text included is HTML. */
        helper.setText(mail.getMessage(), true);
        helper.setSubject(mail.getSubject());

        if (mail.getSubject().startsWith(mailInviteSubject)) {
            /** Attach image always at the end. */
            helper.addInline("googlePlus.logo", getFileSystemResource(googleSmall));
            helper.addInline("facebook.logo", getFileSystemResource(facebookSmall));
            helper.addInline("ios.logo", getFileSystemResource(appStore));
            helper.addInline("android.logo", getFileSystemResource(googlePlay));
        }

        try {
            int count = 0;
            boolean connected = false;
            while (!connected && count < 10) {
                count++;
                try {
                    mailSender.testConnection();
                    connected = true;
                } catch (MessagingException m) {
                    LOG.error("Failed to connect with mail server count={} reason={}", count, m.getLocalizedMessage(), m);
                }
            }

            count = 0;
            boolean noAuthenticationException = false;
            mailManager.save(mail);

            while (!noAuthenticationException && count < 10) {
                count++;
                try {
                    MimeMessage dkimSignedMessage = dkimSignMessage(message, dkimPath, "receiptofi.com", "receiptapp");
                    mailSender.send(dkimSignedMessage);
                    noAuthenticationException = true;
                    LOG.info("Mail success... subject={}", mail.getSubject());
                    return;
                } catch (MailAuthenticationException | MailSendException e) {
                    LOG.error("Failed to send mail server count={} reason={}", count, e.getLocalizedMessage(), e);
                } catch (Exception e) {
                    LOG.error(e.getLocalizedMessage());
                }
                LOG.warn("Mail fail... subject={}", mail.getSubject());
            }
        } catch (MailSendException mailSendException) {
            LOG.error("Mail send exception={}", mailSendException.getLocalizedMessage());
            throw new MessagingException(mailSendException.getLocalizedMessage(), mailSendException);
        }
    }

    private FileSystemResource getFileSystemResource(String location) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(location);
        if (url == null) {
            try {
                File file = new File(location);
                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            } catch (MalformedURLException e) {
                LOG.error("URL for file at location={} reason={}", location, e.getLocalizedMessage(), e);
            }
        }
        Assert.notNull(url, "File not found at location " + location);
        return new FileSystemResource(url.getPath());
    }

    private MimeMessageHelper populateMessageBody(MailEntity mail, MimeMessage message) throws MessagingException, UnsupportedEncodingException {
        /** Use the true flag to indicate you need a multipart message. */
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        if (StringUtils.isBlank(mail.getFromMail())) {
            helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));
        } else {
            helper.setFrom(new InternetAddress(mail.getFromMail(), mail.getFromName()));
        }

        String sentTo = StringUtils.isEmpty(devSentTo) ? mail.getToMail() : devSentTo;
        if (sentTo.equalsIgnoreCase(devSentTo)) {
            helper.setTo(new InternetAddress(devSentTo, emailAddressName));
        } else {
            helper.setTo(new InternetAddress(mail.getToMail(), mail.getToName()));
        }
        return helper;
    }

    /**
     * Signing message with dkim.
     *
     * @param message
     * @param dkimPath
     * @param signingDomain
     * @param selector
     * @return
     * @throws Exception
     */
    private MimeMessage dkimSignMessage(MimeMessage message, String dkimPath, String signingDomain, String selector) throws Exception {
        DkimSigner dkimSigner = new DkimSigner(signingDomain, selector, getDkimPrivateKeyFileForSender(dkimPath));
        dkimSigner.setIdentity(doNotReplyEmail);
        dkimSigner.setHeaderCanonicalization(Canonicalization.SIMPLE);
        dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
        dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA256_WITH_RSA);
        dkimSigner.setLengthParam(true);
        dkimSigner.setZParam(false);
        return new DkimMessage(message, dkimSigner);
    }

    private InputStream getDkimPrivateKeyFileForSender(String dkimPath) {
        return this.getClass().getClassLoader().getResourceAsStream(dkimPath);
    }

    private void saveUploadStats(CronStatsEntity cronStats, int success, int failure, int skipped, int size) {
        cronStats.addStats("success", success);
        cronStats.addStats("skipped", skipped);
        cronStats.addStats("failure", failure);
        cronStats.addStats("found", size);
        cronStatsService.save(cronStats);

        LOG.info("Mail sent success={} skipped={} failure={} total={}", success, skipped, failure, size);
    }
}
