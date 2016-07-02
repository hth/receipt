package com.receiptofi.web.controller.emp.receipt;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.service.EmpLandingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * User: hitender
 * Date: 6/24/16 3:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp/receipt")
public class ReceiptLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptLandingController.class);

    private String receiptLanding;
    private EmpLandingService empLandingService;

    @Autowired
    public ReceiptLandingController(
            @Value ("${receiptLanding:/emp/receipt/landing}")
            String receiptLanding,

            EmpLandingService empLandingService
    ) {
        this.receiptLanding = receiptLanding;
        this.empLandingService = empLandingService;
    }

    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public ModelAndView empLanding() {
        LOG.info("employee landed");
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ModelAndView modelAndView = new ModelAndView(receiptLanding);

        /**
         * Note: findPending has to be before findUpdateWithLimit because records are update in the second query
         * and this gets duplicates
         *
         * Note: Inactive and PROCESSED marked records are not captured in the queries below.
         */
        List<MessageDocumentEntity> pending = empLandingService.pendingReceipts(
                receiptUser.getUsername(),
                receiptUser.getRid(),
                DocumentStatusEnum.PENDING);
        modelAndView.addObject("pending", pending);

        List<MessageDocumentEntity> queue = empLandingService.queuedReceipts(
                receiptUser.getUsername(),
                receiptUser.getRid());
        modelAndView.addObject("queue", queue);

        List<MessageDocumentEntity> recheckPending = empLandingService.pendingReceipts(
                receiptUser.getUsername(),
                receiptUser.getRid(),
                DocumentStatusEnum.REPROCESS);
        modelAndView.addObject("recheckPending", recheckPending);

        List<MessageDocumentEntity> recheck = empLandingService.recheck(
                receiptUser.getUsername(),
                receiptUser.getRid());
        modelAndView.addObject("recheck", recheck);
        return modelAndView;
    }
}
