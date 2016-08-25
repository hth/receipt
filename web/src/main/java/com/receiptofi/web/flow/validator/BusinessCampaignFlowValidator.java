package com.receiptofi.web.flow.validator;

import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.controller.access.LandingController;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * User: hitender
 * Date: 8/5/16 6:49 AM
 */
@Component
public class BusinessCampaignFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCampaignFlowValidator.class);

    @SuppressWarnings ("unused")
    public String validateCampaignDetails(CouponCampaign couponCampaign, MessageContext messageContext) {
        LOG.info("Validate campaign details rid={} bizId={} bizName={}",
                couponCampaign.getRid(),
                couponCampaign.getBizId(),
                couponCampaign.getBusinessName());

        String status = LandingController.SUCCESS;
        Date live = DateUtil.convertToDate(couponCampaign.getLive());
        Date start = DateUtil.convertToDate(couponCampaign.getStart());
        Date end = DateUtil.convertToDate(couponCampaign.getEnd());

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

        if (live.compareTo(DateUtil.getUTCDate()) < 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("live")
                            .defaultText("First Available has to be greater than " +
                                    DateUtil.dateToString(new DateTime(DateUtil.getUTCDate()).plusDays(1).toDate()))
                            .build());
            status = "failure";
        }

        if (start.compareTo(live) < 0) {
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

        if (end.compareTo(start) < 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("start")
                            .defaultText("Valid From cannot start and end on the same or before the start date " +
                                    couponCampaign.getStart())
                            .build());
            status = "failure";
        }

        if (end.compareTo(start) == 0) {
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
    public String validateCampaignCoupon(CouponCampaign couponCampaign, MessageContext messageContext) {
        LOG.info("Validate campaign details rid={} bizId={} bizName={}",
                couponCampaign.getRid(),
                couponCampaign.getBizId(),
                couponCampaign.getBusinessName());

        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponCampaign.getDistributionPercentPatrons())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("distributionPercent")
                            .defaultText("Distribution cannot be empty.")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(couponCampaign.getDistributionPercentNonPatrons())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("distributionPercent")
                            .defaultText("Distribution cannot be empty.")
                            .build());
            status = "failure";
        }

        LOG.info("Completed validating campaign coupon ");
        return status;
    }

}
