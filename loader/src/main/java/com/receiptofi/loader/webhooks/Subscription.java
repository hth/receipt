package com.receiptofi.loader.webhooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: hitender
 * Date: 6/1/15 11:12 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/webhooks/subscription")
public class Subscription {
    private static final Logger LOG = LoggerFactory.getLogger(Subscription.class);

    @RequestMapping (method = RequestMethod.GET)
    public String getSubscription() {
        LOG.info("Subscription called");
        return "";
    }
}
