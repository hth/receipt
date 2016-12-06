package com.receiptofi.web.controller.accountant;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.receiptofi.domain.AccountantEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.AccountantService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.util.HttpRequestResponseParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 7/23/16 4:50 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/accountant/report")
public class AccountantReportController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountantReportController.class);

    private String report;
    private AccountantService accountantService;
    private ReceiptService receiptService;

    public AccountantReportController(
            @Value ("${report:/accountant/report}")
            String report,

            AccountantService accountantService,
            ReceiptService receiptService) {
        this.report = report;
        this.accountantService = accountantService;
        this.receiptService = receiptService;
    }

    @RequestMapping (
            value = "/{rid}/{auth}",
            method = RequestMethod.GET)
    public ModelAndView approveCampaign(
            @PathVariable ("rid")
            ScrubbedInput rid,

            @PathVariable ("auth")
            ScrubbedInput auth,

            HttpServletRequest request,

            HttpServletResponse response
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business expenses tally page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());

        String ip = HttpRequestResponseParser.getClientIpAddress(request);
        AccountantEntity accountant = accountantService.getUserForAccountant(rid.getText(), auth.getText(), receiptUser.getRid(), ip);
        if (accountant == null) {
            LOG.warn("Attempted to access account of rid={} by tid={}", rid.getText(), receiptUser.getRid());
            response.sendError(SC_NOT_FOUND, "Could not find");
        }

        List<ReceiptEntity> receipts = receiptService.getReceiptsWithExpenseTags(
                rid.getText(),
                accountant.getExpenseTags(),
                accountant.getDelayDuration());

        ModelAndView modelAndView = new ModelAndView(report);
        modelAndView.addObject("receipts", receipts);
        return modelAndView;
    }
}
