package com.receiptofi.web.flow;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.BusinessCampaignService;
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
    private BusinessCampaignService businessCampaignService;

    @SuppressWarnings ("all")
    @Autowired
    public BusinessCampaignFlowActions(
            BusinessUserService businessUserService,
            BusinessCampaignService businessCampaignService) {
        this.businessUserService = businessUserService;
        this.businessCampaignService = businessCampaignService;
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
        BusinessCampaignEntity businessCampaign = businessCampaignService.findById(campaignId, businessUser.getBizName().getId());
        return new CouponCampaign(businessCampaign.getId())
                .setRid(rid)
                .setBusinessName(businessUser.getBizName().getBusinessName())
                .setBizId(businessUser.getBizName().getId())
                .setLive(DF_MMDDYYYY.format(businessCampaign.getLive()))
                .setStart(DF_MMDDYYYY.format(businessCampaign.getStart()))
                .setEnd(DF_MMDDYYYY.format(businessCampaign.getEnd()))
                .setFreeText(new ScrubbedInput(businessCampaign.getFreeText()))
                .setAdditionalInfo(businessCampaign.getAdditionalInfo() != null ? new ScrubbedInput(businessCampaign.getAdditionalInfo().getText()) : new ScrubbedInput(""))
                .setDistributionPercent(businessCampaign.getDistributionPercent() + "%");
    }

    @SuppressWarnings ("unused")
    public String validateCampaignDetails(CouponCampaign couponCampaign, MessageContext messageContext) {
        LOG.info("Validate campaign details rid={} bizId={} bizName={}",
                couponCampaign.getRid(),
                couponCampaign.getBizId(),
                couponCampaign.getBusinessName());

        String status = LandingController.SUCCESS;

        Date live = null, start = null, end = null;
        try {
            live = DF_MMDDYYYY.parse(couponCampaign.getLive());
            start = DF_MMDDYYYY.parse(couponCampaign.getStart());
            end = DF_MMDDYYYY.parse(couponCampaign.getEnd());
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage());
        }

        if (StringUtils.isBlank(couponCampaign.getFreeText().getText())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("freeText")
                            .defaultText("Please enter Coupon Text to help explain the deal to your customers")
                            .build());
            status = "failure";
        } else if (couponCampaign.getFreeText().getText().length() > 30) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("freeText")
                            .defaultText("Please keep Coupon Text under 30 characters")
                            .build());
            status = "failure";
        }

        if (couponCampaign.getAdditionalInfo().getText().length() > 600) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("additionalInfo")
                            .defaultText("Please shorten the content to fit under 600 characters")
                            .build());
            status = "failure";
        }

        if (null != live && live.compareTo(DateUtil.getUTCDate()) < 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("live")
                            .defaultText("First Available has to be greater than " +
                                    DF_MMDDYYYY.format(new DateTime(DateUtil.getUTCDate()).plusDays(1).toDate()))
                            .build());
            status = "failure";
        }

        if (null != start && null != live && start.compareTo(live) < 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("start")
                            .defaultText("Valid From cannot be before first available date " +
                                    couponCampaign.getLive() +
                                    ". Change Valid From date to match " +
                                    couponCampaign.getLive())
                            .build());
            status = "failure";
        }

        if (null != start && null != end && end.compareTo(start) < 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("start")
                            .defaultText("Valid From cannot start and end on the same or before the start date " +
                                    couponCampaign.getStart())
                            .build());
            status = "failure";
        }

        if (null != start && null != end && end.compareTo(start) == 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("start")
                            .defaultText("Valid From cannot start and end on the same. " +
                                    "Change the end date to higher than " +
                                    couponCampaign.getEnd())
                            .build());
            status = "failure";
        }

        LOG.info("Validate campaign details rid={} status={} bizId={} ",
                couponCampaign.getRid(),
                status,
                couponCampaign.getBizId());

        LOG.info("Completed validating campaign details ");
        return status;
    }

    @SuppressWarnings ("unused")
    public void createUpdateCampaign(CouponCampaign couponCampaign) {
        try {
            businessCampaignService.save(couponCampaign);
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    couponCampaign.getRid(), e.getLocalizedMessage(), e);
            throw new BusinessCampaignException("Error saving campaign", e);
        }
    }

    @SuppressWarnings ("unused")
    public String validateCampaignCoupon(CouponCampaign couponCampaign, MessageContext messageContext) {
        LOG.info("Validate campaign details rid={} bizId={} bizName={}",
                couponCampaign.getRid(),
                couponCampaign.getBizId(),
                couponCampaign.getBusinessName());

        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponCampaign.getDistributionPercent())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("distributionPercent")
                            .defaultText("Distribution cannot be empty.")
                            .build());
            status = "failure";
        }

        if (couponCampaign.getDistributionPercentAsInt() <= 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("distributionPercent")
                            .defaultText("Distribution cannot be set to zero.")
                            .build());
            status = "failure";
        }

        LOG.info("Completed validating campaign coupon ");
        return status;
    }
}
