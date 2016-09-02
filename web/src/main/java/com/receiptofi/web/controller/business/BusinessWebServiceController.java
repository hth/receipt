package com.receiptofi.web.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.analytic.UserDimensionService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.utils.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 8/28/16 3:04 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/business/api")
public class BusinessWebServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessWebServiceController.class);

    private String brainTreeEnvironment;
    private UserDimensionService userDimensionService;
    private BusinessUserService businessUserService;

    @Autowired
    public BusinessWebServiceController(
            @Value ("${braintree.environment}")
            String brainTreeEnvironment,

            UserDimensionService userDimensionService,
            BusinessUserService businessUserService
    ) {
        this.brainTreeEnvironment = brainTreeEnvironment;
        this.userDimensionService = userDimensionService;
        this.businessUserService = businessUserService;
    }

    @RequestMapping (
            value = "/pc/{bizId}/{percent}",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    public String getBusinessPatronCount(
            @PathVariable
            ScrubbedInput bizId,

            @PathVariable
            ScrubbedInput percent,

            HttpServletResponse response
    ) throws IOException {
        if (validateRequest(bizId, response)) return "";
        return translateCount(userDimensionService.getBusinessUserCount(bizId.getText(), Integer.parseInt(percent.getText())));
    }

    @RequestMapping (
            value = "/npc/{bizId}/{percent}",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    public String getBusinessNonPatronCount(
            @PathVariable
            ScrubbedInput bizId,

            @PathVariable
            ScrubbedInput percent,

            HttpServletResponse response
    ) throws IOException {
        if (validateRequest(bizId, response)) return "";
        return translateCount(userDimensionService.getBusinessUserCount(bizId.getText(), Integer.parseInt(percent.getText())));
    }

    private boolean validateRequest(ScrubbedInput bizId, HttpServletResponse response) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Validate.isValidObjectId(bizId.getText())) {
            LOG.error("Found invalid objectId={} rid={}", bizId.getText(), receiptUser.getRid());
            response.sendError(SC_NOT_FOUND, "Cannot access directly");
            return true;
        }

        boolean businessUserExists = businessUserService.doesBusinessUserExists(receiptUser.getRid(), bizId.getText());
        if (!businessUserExists) {
            response.sendError(SC_FORBIDDEN, "Cannot access directly");
            return true;
        }
        return false;
    }

    private String translateCount(int count) {
        String m;
        if (count < 10) {
            m = "Delivered to few";
        } else if (count < 100) {
            m = "Delivery in hundreds";
        } else if (count < 10_00) {
            m = "Delivery in tens of hundreds";
        } else if (count < 10_000) {
            m = "Delivery in tens of thousands";
        } else if (count < 100_000) {
            m = "Delivery in hundreds of thousands";
        } else if (count < 1_000_000) {
            m = "Delivery in millions";
        } else if (count < 10_000_000) {
            m = "Delivery in tens of millions";
        } else if (count < 100_000_000) {
            m = "Delivery in hundreds of millions";
        } else {
            m = "Delivery in billions";
        }

        /* TODO(hth) Remove SANDBOX and PRODUCTION condition here as this can slow the response time. */
        JsonObject jsonObject = new JsonObject();
        if ("PRODUCTION".equals(brainTreeEnvironment)) {
            jsonObject.addProperty("m", m);
        } else {
            jsonObject.addProperty("m", m + " (" + count + ")");
        }
        return new Gson().toJson(jsonObject);
    }
}
