package com.receiptofi.web.controller.emp.campaign;

import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.CampaignTypeEnum;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.CampaignService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.business.CampaignListForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User: hitender
 * Date: 6/24/16 3:00 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp/campaign")
public class CampaignLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(CampaignLandingController.class);

    private String campaignLanding;
    private String loadCampaign;
    private CampaignService campaignService;
    private BusinessUserService businessUserService;

    @Autowired
    public CampaignLandingController(
            @Value ("${campaignLanding:/emp/campaign/landing}")
            String campaignLanding,

            @Value ("${approveCampaign:/emp/campaign/loadCampaign}")
            String loadCampaign,

            CampaignService campaignService,
            BusinessUserService businessUserService) {
        this.campaignLanding = campaignLanding;
        this.loadCampaign = loadCampaign;
        this.campaignService = campaignService;
        this.businessUserService = businessUserService;
    }

    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String empLanding(@ModelAttribute CampaignListForm campaignListForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("employee landed rid={}", receiptUser.getRid());

        campaignListForm.setCampaignCount(campaignService.countPendingApproval())
                .setCampaigns(campaignService.findAllPendingApproval());
        return campaignLanding;
    }

    @RequestMapping (value = "/{campaignId}", method = RequestMethod.GET)
    public String loadCampaign(
            @ModelAttribute ("couponCampaign")
            CouponCampaign couponCampaign,

            @PathVariable ("campaignId")
            ScrubbedInput campaignId,

            ModelMap model
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("load campaign rid={} campaignId={}", receiptUser.getRid(), campaignId);

        if (model.containsKey("result")) {
            BeanPropertyBindingResult result = (BeanPropertyBindingResult) model.get("result");

            /* PRG pattern. */
            if ("couponCampaign".equalsIgnoreCase(result.getObjectName())) {
                model.addAttribute("org.springframework.validation.BindingResult.couponCampaign", result);
            }
        }

        CampaignEntity campaign = campaignService.findById(campaignId.getText(), receiptUser.getUserLevel());
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(campaign.getRid());

        couponCampaign.setCampaignId(campaign.getId())
                .setBusinessName(businessUser.getBizName().getBusinessName())
                .setRid(campaign.getRid())
                .setBizId(campaign.getBizId())
                .setLive(DateUtil.dateToString(campaign.getLive()))
                .setStart(DateUtil.dateToString(campaign.getStart()))
                .setEnd(DateUtil.dateToString(campaign.getEnd()))
                .setFreeText(campaign.getFreeText())
                .setAdditionalInfo(campaign.getAdditionalInfo() != null ? campaign.getAdditionalInfo().getText() : "")
                .setDistributionPercentPatrons(campaign.getCampaignStats().get(CampaignTypeEnum.P.getName()).getDistributionPercent() + "%")
                .setDistributionPercentNonPatrons(campaign.getCampaignStats().get(CampaignTypeEnum.NP.getName()).getDistributionPercent() + "%")
                .setCampaignStatus(campaign.getCampaignStatus())
                .setFileSystemEntities(campaign.getFileSystemEntities());

        return loadCampaign;
    }

    @RequestMapping (
            value = "/{campaignId}",
            method = RequestMethod.POST,
            params = "campaign-decline")
    public String declineCampaign(
            @PathVariable ("campaignId")
            ScrubbedInput campaignId,

            CouponCampaign couponCampaign,
            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("{} campaign campaignId={} by rid={}",
                CampaignStatusEnum.D.getDescription(), campaignId, receiptUser.getRid());

        /* Validate Campaign. */
        if (StringUtils.isBlank(couponCampaign.getReason())) {
            result.rejectValue("reason",
                    "field.reason",
                    new Object[]{"Reason", Integer.valueOf("15")},
                    "Reason cannot be left blank, required length 15 characters");

            redirectAttrs.addFlashAttribute("result", result);

            /* PRG pattern. */
            return "redirect:" + "/emp/campaign/" + campaignId.getText() + ".htm";
        }

        campaignService.updateCampaignStatus(
                campaignId.getText(),
                receiptUser.getRid(),
                receiptUser.getUserLevel(),
                CampaignStatusEnum.D,
                couponCampaign.getReason());

        return "redirect:" + campaignLanding + ".htm";
    }

    @RequestMapping (
            value = "/{campaignId}",
            method = RequestMethod.POST,
            params = "campaign-approve")
    public String approveCampaign(
            @PathVariable ("campaignId")
            String campaignId
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("{} campaign campaignId={} by rid={}",
                CampaignStatusEnum.A.getDescription(), campaignId, receiptUser.getRid());

        campaignService.updateCampaignStatus(
                campaignId,
                receiptUser.getRid(),
                receiptUser.getUserLevel(),
                CampaignStatusEnum.A,
                "");

        return "redirect:" + campaignLanding + ".htm";
    }
}
