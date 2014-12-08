package com.receiptofi.web.controller.webapi;

import com.receiptofi.service.MailService;
import com.receiptofi.utils.ParseJsonStringToMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
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

    @RequestMapping (
            value = "/accountValidation",
            method = RequestMethod.POST
    )
    @ResponseBody
    public void accountValidationMail(
            @RequestBody
            String mailJson,

            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("webApiAccessToken={}", webApiAccessToken);

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, String> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(mailJson);
            } catch (IOException e) {
                LOG.error("could not parse mailJson={} reason={}", mailJson, e.getLocalizedMessage(), e);
            }
            Assert.notNull(map);
            if (mailService.accountValidationMail(map.get("userId"), map.get("name"), map.get("auth"))) {
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }

        }
        LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
    }
}
