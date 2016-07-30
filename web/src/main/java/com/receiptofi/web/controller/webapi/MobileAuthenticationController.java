package com.receiptofi.web.controller.webapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.social.UserAccountDuplicateException;
import com.receiptofi.social.service.CustomUserDetailsService;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.util.MobileSystemErrorCodeEnum;

import org.apache.commons.lang3.StringUtils;

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
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Mobile social authentication service.
 * User: hitender
 * Date: 6/29/14 7:56 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/webapi/mobile")
public class MobileAuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(MobileAuthenticationController.class);

    @Value ("${web.access.api.token}")
    private String webApiAccessToken;

    @Autowired private CustomUserDetailsService customUserDetailsService;

    @RequestMapping (
            value = "/auth-create",
            method = RequestMethod.POST
    )
    public String authenticateOrCreate(
            @RequestBody
            String authenticationJson,

            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.debug("authenticatedOrCreate initiated from mobile");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(authenticationJson);
            } catch (IOException e) {
                LOG.error("could not parse authenticationJson={} reason={}",
                        authenticationJson, e.getLocalizedMessage(), e);
            }
            Assert.notNull(map);
            try {
                return customUserDetailsService.signInOrSignup(ProviderEnum.valueOf(map.get("pid").getText()), map.get("at").getText());
            } catch (HttpClientErrorException e) {
                LOG.error("error pid={} reason={} responseHeader={}",
                        map.get("pid").getText(),
                        e.getLocalizedMessage(),
                        e.getResponseHeaders().toSingleValueMap(),
                        e);
                String appendToReason = StringUtils.isBlank(map.get("pid").getText()) ? "" : " " + map.get("pid");

                JsonObject error = new JsonObject();
                error.addProperty("httpStatusCode", e.getStatusCode().value());
                error.addProperty("httpStatus", e.getStatusCode().name());
                error.addProperty("reason", MobileSystemErrorCodeEnum.AUTHENTICATION.getMessage() + appendToReason);
                error.addProperty("systemErrorCode", MobileSystemErrorCodeEnum.AUTHENTICATION.getCode());
                error.addProperty("systemError", MobileSystemErrorCodeEnum.AUTHENTICATION.name());

                JsonObject result = new JsonObject();
                result.add("error", error);

                return new Gson().toJson(result);
            } catch (UserAccountDuplicateException e) {
                LOG.error("duplicate account error pid={} reason={}", map.get("pid"), e.getLocalizedMessage(), e);

                JsonObject error = new JsonObject();
                error.addProperty("reason", MobileSystemErrorCodeEnum.SEVERE_ACCOUNT_DUPLICATE.getMessage());
                error.addProperty("systemErrorCode", MobileSystemErrorCodeEnum.SEVERE_ACCOUNT_DUPLICATE.getCode());
                error.addProperty("systemError", MobileSystemErrorCodeEnum.SEVERE_ACCOUNT_DUPLICATE.name());

                JsonObject result = new JsonObject();
                result.add("error", error);

                return new Gson().toJson(result);
            } catch (Exception e) {
                LOG.error("internal error pid={} reason={}", map.get("pid"), e.getLocalizedMessage(), e);

                JsonObject error = new JsonObject();
                error.addProperty("reason", MobileSystemErrorCodeEnum.SOCIAL_LOGIN_ERROR.getMessage());
                error.addProperty("systemErrorCode", MobileSystemErrorCodeEnum.SOCIAL_LOGIN_ERROR.getCode());
                error.addProperty("systemError", MobileSystemErrorCodeEnum.SOCIAL_LOGIN_ERROR.name());

                JsonObject result = new JsonObject();
                result.add("error", error);

                return new Gson().toJson(result);
            }
        }
        LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        return null;
    }
}
