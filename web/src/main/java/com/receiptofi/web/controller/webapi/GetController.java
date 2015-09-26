package com.receiptofi.web.controller.webapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Generate CSRF token.
 * User: hitender
 * Date: 7/2/14 11:54 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/webapi/mobile/get")
public class GetController {
    private static final Logger LOG = LoggerFactory.getLogger(GetController.class);

    @Value ("${web.access.api.token}")
    private String webApiAccessToken;

    /**
     * Dummy call to populate header with CSRF token in filter.
     *
     * @return
     */
    @RequestMapping (
            method = RequestMethod.GET,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public String get(
            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.debug("CSRF invoked to create token for Mobile");
        if (webApiAccessToken.equals(apiAccessToken)) {
            return "{}";
        }
        LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        return null;
    }
}
