package com.receiptofi.service;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.repository.BusinessCampaignManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * User: hitender
 * Date: 6/10/16 4:30 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BusinessCampaignService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCampaignService.class);

    private BusinessCampaignManager businessCampaignManager;
    private CommentService commentService;

    @Autowired
    public BusinessCampaignService(
            BusinessCampaignManager businessCampaignManager,
            CommentService commentService) {
        this.businessCampaignManager = businessCampaignManager;
        this.commentService = commentService;
    }

    public BusinessCampaignEntity findById(String campaignId, String bizId) {
        return businessCampaignManager.findById(campaignId, bizId);
    }

    public void save(CouponCampaign couponCampaign) throws ParseException {
        try {
            BusinessCampaignEntity bce;
            CommentEntity comment = null;
            if (StringUtils.isNotBlank(couponCampaign.getCampaignId())) {
                bce = businessCampaignManager.findById(couponCampaign.getCampaignId(), couponCampaign.getBizId());
                bce.setRid(couponCampaign.getRid())
                        .setBizId(couponCampaign.getBizId())
                        .setFreeText(couponCampaign.getFreeText().getText())
                        .setStart(DF_MMDDYYYY.parse(couponCampaign.getStart()))
                        .setEnd(DF_MMDDYYYY.parse(couponCampaign.getEnd()))
                        .setLive(DF_MMDDYYYY.parse(couponCampaign.getLive()))
                        .setDistributionPercent(couponCampaign.getDistributionPercentAsInt());

                comment = bce.getAdditionalInfo();
                if (comment != null) {
                    comment.setText(couponCampaign.getAdditionalInfo().getText());
                } else if (StringUtils.isNotBlank(couponCampaign.getAdditionalInfo().getText())) {
                    comment = createNewComment(couponCampaign);
                }
            } else {
                bce = BusinessCampaignEntity.newInstance(
                        couponCampaign.getRid(),
                        couponCampaign.getBizId(),
                        couponCampaign.getFreeText().toString(),
                        DF_MMDDYYYY.parse(couponCampaign.getStart()),
                        DF_MMDDYYYY.parse(couponCampaign.getEnd()),
                        DF_MMDDYYYY.parse(couponCampaign.getLive())
                );

                if (StringUtils.isNotBlank(couponCampaign.getAdditionalInfo().getText())) {
                    comment = createNewComment(couponCampaign);
                }
            }

            if (null != comment) {
                commentService.save(comment);
                bce.setAdditionalInfo(comment);
            }
            save(bce);
            couponCampaign.setCampaignId(bce.getId());
        } catch (ParseException e) {
            LOG.error("Error saving reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    public void save(BusinessCampaignEntity businessCampaign) {
        businessCampaignManager.save(businessCampaign);
    }

    private CommentEntity createNewComment(CouponCampaign businessCampaign) {
        return CommentEntity.newInstance(businessCampaign.getRid(), CommentTypeEnum.C)
                .setText(businessCampaign.getAdditionalInfo().getText());
    }
}
