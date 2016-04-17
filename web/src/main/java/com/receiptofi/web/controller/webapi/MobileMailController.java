package com.receiptofi.web.controller.webapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.MailTypeEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.MailService;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Invoke email from mobile. Cannot send email from mobile service hence need this controller to send email.
 * User: hitender
 * Date: 11/7/14 11:43 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/webapi/mobile/mail")
public class MobileMailController {
    private static final Logger LOG = LoggerFactory.getLogger(MobileMailController.class);

    @Value ("${web.access.api.token}")
    private String webApiAccessToken;

    @Autowired private MailService mailService;
    @Autowired private AccountService accountService;

    @RequestMapping (
            value = "/accountSignup",
            method = RequestMethod.POST
    )
    public void accountValidationMail(
            @RequestBody
            String mailJson,

            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("starting to send accountValidationMail");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(mailJson);
            } catch (IOException e) {
                LOG.error("could not parse mailJson={} reason={}", mailJson, e.getLocalizedMessage(), e);
            }
            Assert.notEmpty(map);
            if (mailService.accountValidationMail(
                    map.get("userId").getText(),
                    map.get("name").getText(),
                    map.get("auth").getText())) {
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }

    @RequestMapping (
            value = "/accountRecover",
            method = RequestMethod.POST
    )
    public void accountRecover(
            @RequestBody
            String recoverJson,

            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.debug("accountRecover initiated from mobile");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(recoverJson);
            } catch (IOException e) {
                LOG.error("could not parse mailJson={} reason={}", recoverJson, e.getLocalizedMessage(), e);
            }
            Assert.notEmpty(map);
            MailTypeEnum mailType = mailService.mailRecoverLink(map.get("userId").getText());
            switch (mailType) {
                case SUCCESS:
                case ACCOUNT_NOT_VALIDATED:
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    break;
                case SOCIAL_ACCOUNT:
                case FAILURE:
                    httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error occurred");
                    break;
                case ACCOUNT_NOT_FOUND:
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    break;
                default:
                    LOG.error("Reached unsupported condition={}", mailType);
                    throw new UnsupportedOperationException("Reached unsupported condition " + mailType);
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }

    @RequestMapping (
            value = "/invite",
            method = RequestMethod.POST
    )
    public void invite(
            @RequestBody
            String recoverJson,

            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("Invite initiated from mobile");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(recoverJson);
            } catch (IOException e) {
                LOG.error("could not parse mailJson={} reason={}", recoverJson, e.getLocalizedMessage(), e);
            }
            Assert.notEmpty(map, "Invite data in map");
            String inviteEmail = map.get("inviteEmail").getText();
            String rid = map.get("rid").getText();
            UserAccountEntity userAccount = accountService.findByReceiptUserId(rid);
            String response = mailService.sendInvite(inviteEmail, rid, userAccount.getUserId());

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(response);

            if (jsonObject.get("status").getAsBoolean()) {
                LOG.info("Invite status={}", jsonObject.get("status").getAsString());
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                LOG.warn("Invite failed sending 500 status={}", jsonObject.get("status").getAsString());
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error occurred");
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }
}
