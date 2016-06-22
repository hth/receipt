package com.receiptofi.service;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.flow.CouponCampaign;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.BusinessCampaignStatusEnum;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.repository.BusinessCampaignManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;

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
    private FileDBService fileDBService;
    private FileSystemService fileSystemService;
    private ImageSplitService imageSplitService;

    @Autowired
    public BusinessCampaignService(
            BusinessCampaignManager businessCampaignManager,
            CommentService commentService,
            FileDBService fileDBService,
            FileSystemService fileSystemService,
            ImageSplitService imageSplitService) {
        this.businessCampaignManager = businessCampaignManager;
        this.commentService = commentService;
        this.fileDBService = fileDBService;
        this.fileSystemService = fileSystemService;
        this.imageSplitService = imageSplitService;
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

            couponCampaign.setCampaignId(bce.getId())
                    .setBusinessCampaignStatus(bce.getBusinessCampaignStatus())
                    .setFileSystemEntities(bce.getFileSystemEntities());
        } catch (ParseException e) {
            LOG.error("Error saving reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    public void save(BusinessCampaignEntity businessCampaign) {
        businessCampaignManager.save(businessCampaign);
    }

    public void completeCampaign(String campaignId, String bizId) {
        BusinessCampaignEntity businessCampaign = businessCampaignManager.findById(campaignId, bizId);
        businessCampaign.setBusinessCampaignStatus(BusinessCampaignStatusEnum.C);
        save(businessCampaign);
    }

    private CommentEntity createNewComment(CouponCampaign businessCampaign) {
        return CommentEntity.newInstance(businessCampaign.getRid(), CommentTypeEnum.C)
                .setText(businessCampaign.getAdditionalInfo().getText());
    }

    public Collection<FileSystemEntity> deleteAndCreateNewImage(
            BufferedImage bufferedImage,
            UploadDocumentImage image,
            Collection<FileSystemEntity> fileSystems
    ) throws IOException {
        /** Save image first. */
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
}
