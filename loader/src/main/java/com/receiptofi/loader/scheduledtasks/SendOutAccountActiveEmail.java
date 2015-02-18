package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: hitender
 * Date: 2/18/15 2:14 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class SendOutAccountActiveEmail {
    private static final Logger LOG = LoggerFactory.getLogger(SendOutAccountActiveEmail.class);

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @Autowired private AccountService accountService;
    @Autowired private MailService mailService;

    @Scheduled (cron = "${loader.SendOutAccountActiveEmail.registrationCompleteEmail}")
    public void registrationCompleteEmail() {
        LOG.info("begins");
        if (registrationTurnedOn) {
            List<UserAccountEntity> userAccounts = accountService.findRegisteredAccountWhenRegistrationIsOff();

            int success = 0, failure = 0, skipped = 0;
            for (UserAccountEntity userAccount : userAccounts) {
                if (userAccount.isRegisteredWhenRegistrationIsOff()) {
                    if (mailService.registrationCompleteEmail(userAccount.getUserId(), userAccount.getName())) {
                        success++;
                    } else {
                        failure++;
                    }
                } else {
                    skipped++;
                }

                accountService.removeRegistrationIsOffFrom(userAccount.getId());
            }
            LOG.info("Registration complete mail sent success={} skipped={}  failure={} total={}", success, skipped, failure, userAccounts.size());
        } else {
            LOG.info("feature is {}", registrationTurnedOn);
        }
    }
}
