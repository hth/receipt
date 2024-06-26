package com.receiptofi.service;

import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.CampaignManager;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
public class CampaignService {
    private static final Logger LOG = LoggerFactory.getLogger(CampaignService.class);

    private int limit;
    private CampaignManager campaignManager;
    private CommentService commentService;
    private FileDBService fileDBService;
    private FileSystemService fileSystemService;
    private CouponService couponService;

    private long bringForwardCampaignLiveByDays = 7;

    @Autowired
    public CampaignService(
            @Value ("${limit: 5}")
            int limit,

            @Value ("${braintree.environment}")
            String brainTreeEnvironment,

            CampaignManager campaignManager,
            CommentService commentService,
            FileDBService fileDBService,
            FileSystemService fileSystemService,
            CouponService couponService) {
        this.limit = limit;
        this.campaignManager = campaignManager;
        this.commentService = commentService;
        this.fileDBService = fileDBService;
        this.fileSystemService = fileSystemService;
        this.couponService = couponService;

        if ("PRODUCTION".equals(brainTreeEnvironment)) {
            /* On production, keep the days as now. */
            bringForwardCampaignLiveByDays = 0;
        }
    }

    public CampaignEntity findById(String campaignId, String bizId) {
        return campaignManager.findById(campaignId, bizId);
    }

    public CampaignEntity findById(String campaignId, UserLevelEnum userLevel) {
        return campaignManager.findById(campaignId, userLevel);
    }

    public void save(CouponCampaign couponCampaign) {
        CampaignEntity campaign;
        CommentEntity comment = null;
        if (StringUtils.isNotBlank(couponCampaign.getCampaignId())) {
            campaign = campaignManager.findById(couponCampaign.getCampaignId(), couponCampaign.getBizId());
            campaign.setRid(couponCampaign.getRid())
                    .setBizId(couponCampaign.getBizId())
                    .setFreeText(couponCampaign.getFreeText())
                    .setStart(DateUtil.convertToDate(couponCampaign.getStart()))
                    .setEnd(DateUtil.convertToDate(couponCampaign.getEnd()))
                    .setLive(DateUtil.convertToDate(couponCampaign.getLive()))
                    .setCampaignStats(couponCampaign.getCampaignStats());

            comment = campaign.getAdditionalInfo();
            if (comment != null) {
                comment.setText(couponCampaign.getAdditionalInfo());
            } else if (StringUtils.isNotBlank(couponCampaign.getAdditionalInfo())) {
                comment = createNewComment(couponCampaign);
            }
        } else {
            campaign = CampaignEntity.newInstance(
                    couponCampaign.getRid(),
                    couponCampaign.getBizId(),
                    couponCampaign.getFreeText(),
                    DateUtil.convertToDate(couponCampaign.getStart()),
                    DateUtil.convertToDate(couponCampaign.getEnd()),
                    DateUtil.convertToDate(couponCampaign.getLive())
            );

            if (StringUtils.isNotBlank(couponCampaign.getAdditionalInfo())) {
                comment = createNewComment(couponCampaign);
            }
        }

        if (null != comment) {
            commentService.save(comment);
            campaign.setAdditionalInfo(comment);
        }
        switch(campaign.getCampaignStatus()) {
            case A:
            case P:
                campaign.setCampaignStatus(CampaignStatusEnum.N);
                break;
            default:
                break;
        }

        save(campaign);

        couponCampaign.setCampaignId(campaign.getId())
                .setCampaignStatus(campaign.getCampaignStatus())
                .setFileSystemEntities(campaign.getFileSystemEntities());
    }

    public void save(CampaignEntity businessCampaign) {
        campaignManager.save(businessCampaign);
    }

    public void completeCampaign(String campaignId, String bizId) {
        CampaignEntity campaign = campaignManager.findById(campaignId, bizId);
        campaign.setCampaignStatus(CampaignStatusEnum.P);
        save(campaign);
    }

    public void stopCampaign(String campaignId, String bizId) {
        CampaignEntity campaign = campaignManager.findById(campaignId, bizId);
        if (null != campaign && campaign.getCampaignStatus() == CampaignStatusEnum.L) {
            couponService.markCampaignCouponsInactive(campaign.getId());
            campaign.setCampaignStatus(CampaignStatusEnum.E);
            save(campaign);
        } else {
            LOG.error("Fail to end the campaignId={} bizId={}", campaignId, bizId);
        }
    }

    private CommentEntity createNewComment(CouponCampaign businessCampaign) {
        return CommentEntity.newInstance(businessCampaign.getRid(), CommentTypeEnum.C)
                .setText(businessCampaign.getAdditionalInfo());
    }

    /**
     * Note: All campaign and coupons are uploaded by default to S3 ASAP.
     *
     * @param bufferedImage
     * @param image
     * @param fileSystems
     * @return
     * @throws IOException
     */
    public Collection<FileSystemEntity> deleteAndCreateNewImage(
            BufferedImage bufferedImage,
            UploadDocumentImage image,
            Collection<FileSystemEntity> fileSystems
    ) throws IOException {
        /* Save image first. */
        String blobId = fileDBService.saveFile(image);
        image.setBlobId(blobId);
        LOG.info("Saved new image rid={}", image.getRid());

        FileSystemEntity fileSystem;
        if (null != fileSystems) {
            fileDBService.deleteHard(fileSystems);
            fileSystemService.deleteHard(fileSystems);
            LOG.info("Deleted old image rid={}", image.getRid());
        }

        fileSystems = new LinkedList<>();
        fileSystem = new FileSystemEntity(
                blobId,
                image.getRid(),
                bufferedImage,
                0,
                0,
                image.getFileData(),
                FileTypeEnum.C);
        fileSystemService.save(fileSystem);
        fileSystems.add(fileSystem);
        LOG.info("Created new filesystem rid={}", image.getRid());
        return fileSystems;
    }

    public List<CampaignEntity> findBy(String bizId) {
        return campaignManager.findBy(bizId);
    }

    public List<CampaignEntity> findAllPendingApproval() {
        return campaignManager.findAllPendingApproval(limit);
    }

    public List<CampaignEntity> findCampaignWithStatus(CampaignStatusEnum campaignStatus) {
        return campaignManager.findCampaignWithStatus(limit, campaignStatus, DateUtil.getDateMinusDay(bringForwardCampaignLiveByDays));
    }

    public long countPendingApproval() {
        return campaignManager.countPendingApproval();
    }

    public void updateCampaignStatus(
            String campaignId,
            String validateByRid,
            UserLevelEnum userLevel,
            CampaignStatusEnum campaignStatus,
            String reason) {
        campaignManager.updateCampaignStatus(campaignId, validateByRid, userLevel, campaignStatus, reason);
    }
}
