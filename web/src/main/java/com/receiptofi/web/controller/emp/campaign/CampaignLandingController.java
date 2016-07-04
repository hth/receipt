package com.receiptofi.web.controller.emp.campaign;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.service.BusinessCampaignService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.business.CampaignListForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    private BusinessCampaignService businessCampaignService;

    @Autowired
    public CampaignLandingController(
            @Value ("${campaignLanding:/emp/campaign/landing}")
            String campaignLanding,

            @Value ("${approveCampaign:/emp/campaign/loadCampaign}")
            String loadCampaign,

            BusinessCampaignService businessCampaignService) {
        this.campaignLanding = campaignLanding;
        this.loadCampaign = loadCampaign;
        this.businessCampaignService = businessCampaignService;
    }

    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String empLanding(@ModelAttribute CampaignListForm campaignListForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("employee landed rid={}", receiptUser.getRid());

        campaignListForm.setCampaignCount(businessCampaignService.countPendingApproval())
                .setBusinessCampaigns(businessCampaignService.findAllPendingApproval());
        return campaignLanding;
    }

    @RequestMapping (value = "/{campaignId}", method = RequestMethod.GET)
    public String loadCampaign(
            @ModelAttribute ("couponCampaign")
            CouponCampaign couponCampaign,

            @PathVariable ("campaignId")
            String campaignId
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("load campaign rid={} campaignId={}", receiptUser.getRid(), campaignId);

        BusinessCampaignEntity businessCampaign = businessCampaignService.findById(campaignId, receiptUser.getUserLevel());

        couponCampaign.setCampaignId(businessCampaign.getId())
                .setRid(businessCampaign.getRid())
                .setBizId(businessCampaign.getBizId())
                .setLive(DF_MMDDYYYY.format(businessCampaign.getLive()))
                .setStart(DF_MMDDYYYY.format(businessCampaign.getStart()))
                .setEnd(DF_MMDDYYYY.format(businessCampaign.getEnd()))
                .setFreeText(new ScrubbedInput(businessCampaign.getFreeText()))
                .setAdditionalInfo(businessCampaign.getAdditionalInfo() != null ? new ScrubbedInput(businessCampaign.getAdditionalInfo().getText()) : new ScrubbedInput(""))
                .setDistributionPercent(businessCampaign.getDistributionPercent() + "%")
                .setBusinessCampaignStatus(businessCampaign.getBusinessCampaignStatus())
                .setFileSystemEntities(businessCampaign.getFileSystemEntities());

        return loadCampaign;
    }

    @RequestMapping (
            value = "/{campaignId}",
            method = RequestMethod.POST,
            params = "campaign-decline")
    public String declineCampaign(@PathVariable ("campaignId") String campaignId) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("{} campaign campaignId={} by rid={}",
                CampaignStatusEnum.D.getDescription(), campaignId, receiptUser.getRid());

        businessCampaignService.updateCampaignStatus(
                campaignId,
                receiptUser.getUserLevel(),
                CampaignStatusEnum.D);

        return "redirect:" + campaignLanding + ".htm";
    }

    @RequestMapping (
            value = "/{campaignId}",
            method = RequestMethod.POST,
            params = "campaign-approve")
    public String approveCampaign(@PathVariable ("campaignId") String campaignId) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("{} campaign campaignId={} by rid={}",
                CampaignStatusEnum.A.getDescription(), campaignId, receiptUser.getRid());

        businessCampaignService.updateCampaignStatus(
                campaignId,
                receiptUser.getUserLevel(),
                CampaignStatusEnum.A);

        return "redirect:" + campaignLanding + ".htm";
    }
}
