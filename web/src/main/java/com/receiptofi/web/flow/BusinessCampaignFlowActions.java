package com.receiptofi.web.flow;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.service.CampaignService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.controller.access.LandingController;
import com.receiptofi.web.flow.exception.BusinessCampaignException;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

/**
 * User: hitender
 * Date: 6/9/16 11:47 AM
 */
@Component
public class BusinessCampaignFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCampaignFlowActions.class);

    private BusinessUserService businessUserService;
    private CampaignService campaignService;

    @SuppressWarnings ("all")
    @Autowired
    public BusinessCampaignFlowActions(
            BusinessUserService businessUserService,
            CampaignService campaignService) {
        this.businessUserService = businessUserService;
        this.campaignService = campaignService;
    }

    /**
     * End day is 8 because its ends after 7 days, i.e start of 8th day it has ended.
     *
     * @return
     */
    @SuppressWarnings ("unused")
    public CouponCampaign startCampaign() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();
        DateTime utcDate = new DateTime(DateUtil.getUTCDate());

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        return new CouponCampaign(null)
                .setRid(rid)
                .setBusinessName(businessUser.getBizName().getBusinessName())
                .setBizId(businessUser.getBizName().getId())
                .setLive(DF_MMDDYYYY.format(utcDate.plusDays(1).toDate()))
                .setStart(DF_MMDDYYYY.format(utcDate.plusDays(1).toDate()))
                .setEnd(DF_MMDDYYYY.format(utcDate.plusDays(8).toDate()));
    }

    @SuppressWarnings ("unused")
    public CouponCampaign editCampaign(String campaignId) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        CampaignEntity businessCampaign = campaignService.findById(campaignId, businessUser.getBizName().getId());
        return new CouponCampaign(businessCampaign.getId())
                .setRid(rid)
                .setBusinessName(businessUser.getBizName().getBusinessName())
                .setBizId(businessUser.getBizName().getId())
                .setLive(DF_MMDDYYYY.format(businessCampaign.getLive()))
                .setStart(DF_MMDDYYYY.format(businessCampaign.getStart()))
                .setEnd(DF_MMDDYYYY.format(businessCampaign.getEnd()))
                .setFreeText(new ScrubbedInput(businessCampaign.getFreeText()))
                .setAdditionalInfo(businessCampaign.getAdditionalInfo() != null ? new ScrubbedInput(businessCampaign.getAdditionalInfo().getText()) : new ScrubbedInput(""))
                .setDistributionPercent(businessCampaign.getDistributionPercent() + "%")
                .setCampaignStatus(businessCampaign.getCampaignStatus())
                .setFileSystemEntities(businessCampaign.getFileSystemEntities());
    }

    @SuppressWarnings ("unused")
    public void createUpdateCampaign(CouponCampaign couponCampaign) {
        try {
            campaignService.save(couponCampaign);
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    couponCampaign.getRid(), e.getLocalizedMessage(), e);
            throw new BusinessCampaignException("Error saving campaign", e);
        }
    }

    public void completeCampaign(String campaignId, String bizId) {
        try {
            campaignService.completeCampaign(campaignId, bizId);
        } catch (Exception e) {
            LOG.error("Error marking campaign complete id={} bizId={} reason={}",
                    campaignId, bizId, e.getLocalizedMessage(), e);
            throw new BusinessCampaignException("Error saving campaign", e);
        }
    }

    public void stopCampaign(String campaignId, String bizId) {
        try {
            campaignService.stopCampaign(campaignId, bizId);
        } catch (Exception e) {
            LOG.error("Error marking campaign stop id={} bizId={} reason={}",
                    campaignId, bizId, e.getLocalizedMessage(), e);
            throw new BusinessCampaignException("Error saving campaign", e);
        }
    }


    public boolean isCampaignPendingApproval(CouponCampaign couponCampaign) {
        return couponCampaign.getCampaignStatus() == CampaignStatusEnum.P
                || couponCampaign.getCampaignStatus() == CampaignStatusEnum.A
                || couponCampaign.getCampaignStatus() == CampaignStatusEnum.L;
    }
}
