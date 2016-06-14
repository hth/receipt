package com.receiptofi.web.flow;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.flow.BusinessCampaign;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.BusinessCampaignService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.utils.DateUtil;

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
    public BusinessCampaign startCampaign() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        return new BusinessCampaign()
                .setRid(businessUser.getReceiptUserId())
                .setBusinessName(businessUser.getBizName().getBusinessName())
                .setBizId(businessUser.getBizName().getId())
                .setLive(DF_MMDDYYYY.format(new DateTime(DateUtil.getUTCDate()).plusDays(1).toDate()))
                .setStart(DF_MMDDYYYY.format(new DateTime(DateUtil.getUTCDate()).plusDays(1).toDate()))
                .setEnd(DF_MMDDYYYY.format(new DateTime(DateUtil.getUTCDate()).plusDays(8).toDate()));
    }

    @SuppressWarnings ("unused")
    public BusinessCampaign editCampaign(String campaignId) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();

        BusinessCampaignEntity businessCampaign = businessCampaignService.findById(campaignId);
        return new BusinessCampaign()
                .setLive(DF_MMDDYYYY.format(businessCampaign.getLive()))
                .setStart(DF_MMDDYYYY.format(businessCampaign.getStart()))
                .setEnd(DF_MMDDYYYY.format(businessCampaign.getEnd()));
    }

    @SuppressWarnings ("unused")
    public String validateCampaignDetails(BusinessCampaign businessCampaign, MessageContext messageContext) {
        LOG.info("Validate campaign details rid={} bizId={} bizName={}",
                businessCampaign.getRid(),
                businessCampaign.getBizId(),
                businessCampaign.getBusinessName());

        String status = "success";

        Date live = null, start = null, end = null;
        try {
            live = DF_MMDDYYYY.parse(businessCampaign.getLive());
            start = DF_MMDDYYYY.parse(businessCampaign.getStart());
            end = DF_MMDDYYYY.parse(businessCampaign.getEnd());
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage());
        }

        if (StringUtils.isBlank(businessCampaign.getFreeText())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("freeText")
                            .defaultText("Please enter Coupon Text to help explain the deal to your customers")
                            .build());
            status = "failure";
        } else if (businessCampaign.getFreeText().length() > 30) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("freeText")
                            .defaultText("Please keep Coupon Text under 30 characters")
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
                                    businessCampaign.getLive() +
                                    ". Change Valid From date to match " +
                                    businessCampaign.getLive())
                            .build());
            status = "failure";
        }

        if (null != start && null != end && end.compareTo(start) < 0) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("start")
                            .defaultText("Valid From cannot start and end on the same or before the start date " +
                                    businessCampaign.getStart())
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
                                    businessCampaign.getEnd())
                            .build());
            status = "failure";
        }

        LOG.info("Validate campaign details rid={} status={} bizId={} ",
                businessCampaign.getRid(),
                status,
                businessCampaign.getBizId());
        LOG.info("Completed validating campaign details ");
        return status;
    }
}
