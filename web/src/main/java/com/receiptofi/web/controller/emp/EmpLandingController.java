package com.receiptofi.web.controller.emp;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.service.EmpLandingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * User: hitender
 * Date: 4/7/13
 * Time: 11:32 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp")
public class EmpLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(EmpLandingController.class);
    private static final String nextPage = "/emp/landing1";

    private EmpLandingService empLandingService;

    @Autowired
    public EmpLandingController(EmpLandingService empLandingService) {
        this.empLandingService = empLandingService;
    }

    @PreAuthorize ("hasAnyRole('ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public ModelAndView empLanding() {
        LOG.info("employee landed");
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ModelAndView modelAndView = new ModelAndView(nextPage);

        /**
         * Note: findPending has to be before findUpdateWithLimit because records are update in the second query
         * and this gets duplicates
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
