package com.receiptofi.web.flow;

import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.CampaignTypeEnum;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.CampaignService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.flow.exception.BusinessCampaignException;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
                .setLive(DateUtil.dateToString(utcDate.plusDays(1).toDate()))
                .setStart(DateUtil.dateToString(utcDate.plusDays(1).toDate()))
                .setEnd(DateUtil.dateToString(utcDate.plusDays(8).toDate()));
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
                .setLive(DateUtil.dateToString(businessCampaign.getLive()))
                .setStart(DateUtil.dateToString(businessCampaign.getStart()))
                .setEnd(DateUtil.dateToString(businessCampaign.getEnd()))
                .setFreeText(businessCampaign.getFreeText())
                .setAdditionalInfo(businessCampaign.getAdditionalInfo() != null ? businessCampaign.getAdditionalInfo().getText() : "")
                .setDistributionPercentPatrons(businessCampaign.getCampaignStats() == null ? "25" + "%" : businessCampaign.getCampaignStats().get(CampaignTypeEnum.P.getName()).getDistributionPercent() + "%")
                .setDistributionPercentNonPatrons(businessCampaign.getCampaignStats() == null ? "25" + "%" : businessCampaign.getCampaignStats().get(CampaignTypeEnum.NP.getName()).getDistributionPercent() + "%")
                .setCampaignStatus(businessCampaign.getCampaignStatus())
                .setFileSystemEntities(businessCampaign.getFileSystemEntities());
    }

    @SuppressWarnings ("unused")
    public boolean isCampaignPendingApproval(CouponCampaign couponCampaign) {
        return couponCampaign.getCampaignStatus() == CampaignStatusEnum.P
                || couponCampaign.getCampaignStatus() == CampaignStatusEnum.A
                || couponCampaign.getCampaignStatus() == CampaignStatusEnum.L;
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

    @SuppressWarnings ("unused")
    public void completeCampaign(String campaignId, String bizId) {
        try {
            campaignService.completeCampaign(campaignId, bizId);
        } catch (Exception e) {
            LOG.error("Error marking campaign complete id={} bizId={} reason={}",
                    campaignId, bizId, e.getLocalizedMessage(), e);
            throw new BusinessCampaignException("Error saving campaign", e);
        }
    }

    @SuppressWarnings ("unused")
    public void stopCampaign(String campaignId, String bizId) {
        try {
            campaignService.stopCampaign(campaignId, bizId);
        } catch (Exception e) {
            LOG.error("Error marking campaign stop id={} bizId={} reason={}",
                    campaignId, bizId, e.getLocalizedMessage(), e);
            throw new BusinessCampaignException("Error saving campaign", e);
        }
    }
}
