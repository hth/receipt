package com.receiptofi.web.controller.webapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.service.RegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 3/3/15 4:24 AM
 */
@RestController
@RequestMapping (value = "/webapi/mobile/registration")
public class MobileRegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(MobileRegistrationController.class);

    @Value ("${web.access.api.token}")
    private String webApiAccessToken;

    private final RegistrationService registrationService;

    @Autowired
    public MobileRegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @RequestMapping (
            value = "/accepting",
            method = RequestMethod.GET,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public String isRegistrationTurnedOn(
            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.debug("isRegistrationTurnedOn initiated");
        if (webApiAccessToken.equals(apiAccessToken)) {
            boolean registrationTurnedOn = registrationService.isRegistrationTurnedOn();
            LOG.debug("registrationTurnedOn={}", registrationTurnedOn);
            JsonObject result = new JsonObject();
            result.addProperty("RTO", registrationTurnedOn);
            return new Gson().toJson(result);
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
            return null;
        }
    }
}
