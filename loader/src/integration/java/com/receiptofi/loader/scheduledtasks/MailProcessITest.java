package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.ITest;

import org.junit.Before;

/**
 * User: hitender
 * Date: 7/11/16 12:57 PM
 */
public class MailProcessITest extends ITest {

    private MailProcess mailProcess;

    @Before
    public void setup() {
        String appendToSubject = "::" + properties.getProperty("braintree.environment") + ":: ";
        String baseDirectory = this.getClass().getResource(".").getPath().split("classes")[0];

        mailProcess = new MailProcess(
                properties.getProperty("do.not.reply.email"),
                properties.getProperty("email.address.name"),
                properties.getProperty("dev.sent.to"),
                appendToSubject + properties.getProperty("mail.invite.subject"),
                baseDirectory + "resources/test/smallGoogle.jpg",
                baseDirectory + "resources/test/googlePlay151x47.jpg",
                baseDirectory + "resources/test/smallFacebook.jpg",
                baseDirectory + "resources/test/app-store151x48.jpg",
                properties.getProperty("MailProcess.emailSwitch"),
                Integer.parseInt(properties.getProperty("MailProcess.sendAttempt")),
                "dkimPath",
                mailSender,
                mailManager,
                cronStatsService
        );
    }
}
