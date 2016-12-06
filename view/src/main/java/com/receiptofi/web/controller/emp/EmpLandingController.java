package com.receiptofi.web.controller.emp;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.CampaignService;
import com.receiptofi.service.DocumentPendingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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

    private String empLanding;
    private DocumentPendingService documentPendingService;
    private CampaignService campaignService;

    @Autowired
    public EmpLandingController(
            @Value ("${empLanding:/emp/landing}")
            String empLanding,

            DocumentPendingService documentPendingService,
            CampaignService campaignService) {
        this.empLanding = empLanding;
        this.documentPendingService = documentPendingService;
        this.campaignService = campaignService;
    }

    @PreAuthorize ("hasRole('ROLE_SUPERVISOR')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public ModelAndView empLanding() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Employee landed rid={}", receiptUser.getRid());

        ModelAndView modelAndView = new ModelAndView(empLanding);
        modelAndView.addObject("documentPending", documentPendingService.getTotalPending());
        modelAndView.addObject("campaignPending", campaignService.countPendingApproval());
        return modelAndView;
    }
}
