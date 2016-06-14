package com.receiptofi.web.controller.business;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.BizService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.analytic.BizDimensionService;
import com.receiptofi.utils.Maths;
import com.receiptofi.web.form.business.BusinessLandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * For Businesses.
 * User: hitender
 * Date: 5/13/16 1:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business")
public class BusinessLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLandingController.class);

    private String nextPage;
    private String businessRegistrationFlow;
    private BusinessUserService businessUserService;
    private BizDimensionService bizDimensionService;

    @Autowired
    public BusinessLandingController(
            @Value ("${nextPage:/business/landing}")
            String nextPage,

            @Value ("${businessRegistrationFlow:redirect:/business/registration.htm}")
            String businessRegistrationFlow,

            BusinessUserService businessUserService,
            BizDimensionService bizDimensionService) {
        this.nextPage = nextPage;
        this.businessRegistrationFlow = businessRegistrationFlow;
        this.businessUserService = businessUserService;
        this.bizDimensionService = bizDimensionService;
    }

    /**
     * Loading landing page for business.
     *
     * @param businessLandingForm
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_BUSINESS')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm(@ModelAttribute ("businessLandingForm") BusinessLandingForm businessLandingForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(receiptUser.getRid());
        return nextPage(receiptUser, businessUser, businessLandingForm);
    }

    private String nextPage(
            ReceiptUser receiptUser,
            BusinessUserEntity businessUser,
            BusinessLandingForm businessLandingForm) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                populateBusinessLandingForm(businessLandingForm, businessUser);
                return nextPage;
            case C:
            case I:
            case N:
                LOG.info("Business Registration rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
                return businessRegistrationFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    private void populateBusinessLandingForm(BusinessLandingForm businessLandingForm, BusinessUserEntity businessUser) {
        Assert.notNull(businessUser, "Business user should not be null");
        BizNameEntity bizName = businessUser.getBizName();
        String bizNameId = bizName.getId();
        LOG.info("Loading dashboard for bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());

        BizDimensionEntity bizDimension = bizDimensionService.findBy(bizNameId);
        businessLandingForm.setBizName(bizDimension.getBizName())
                .setCustomerCount(bizDimension.getUserCount())
                .setStoreCount(bizDimension.getStoreCount())
                .setTotalCustomerPurchases(Maths.adjustScale(bizDimension.getBizTotal()))
                .setVisitCount(bizDimension.getVisitCount());
    }
}
