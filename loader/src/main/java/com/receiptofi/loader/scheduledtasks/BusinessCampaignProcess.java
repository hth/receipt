package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.CouponEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.domain.analytic.UserDimensionEntity;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.CouponTypeEnum;
import com.receiptofi.domain.types.CouponUploadStatusEnum;
import com.receiptofi.service.BizService;
import com.receiptofi.service.BusinessCampaignService;
import com.receiptofi.service.CouponService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.analytic.BizDimensionService;
import com.receiptofi.service.analytic.UserDimensionService;
import com.receiptofi.utils.Maths;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * User: hitender
 * Date: 7/2/16 10:43 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class BusinessCampaignProcess {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCampaignProcess.class);

    private final String filesUploadSwitch;
    private BusinessCampaignService businessCampaignService;
    private BizDimensionService bizDimensionService;
    private CronStatsService cronStatsService;
    private BizService bizService;
    private UserDimensionService userDimensionService;
    private CouponService couponService;

    @Autowired
    public BusinessCampaignProcess(
            @Value ("${FilesUploadToS3.filesUploadSwitch}")
            String filesUploadSwitch,

            BusinessCampaignService businessCampaignService,
            BizDimensionService bizDimensionService,
            CronStatsService cronStatsService, BizService bizService,
            UserDimensionService userDimensionService,
            CouponService couponService) {
        this.filesUploadSwitch = filesUploadSwitch;
        this.businessCampaignService = businessCampaignService;
        this.bizDimensionService = bizDimensionService;
        this.cronStatsService = cronStatsService;
        this.bizService = bizService;
        this.userDimensionService = userDimensionService;
        this.couponService = couponService;
    }

    /**
     * Create coupons from campaign and mark Campaign as LIVE.
     * Note: Cron string blow run every 2 minute.
     */
    @Scheduled (fixedDelayString = "${loader.BusinessCampaign.setToLive}")
    public void setToLiveCampaign() {
        CronStatsEntity cronStats = new CronStatsEntity(
                BusinessCampaignProcess.class.getName(),
                "setToLiveCampaign",
                filesUploadSwitch);

        /**
         * TODO prevent test db connection from dev. As this moves files to 'dev' bucket in S3 and test environment fails to upload to 'test' bucket.
         * NOTE: This is one of the reason you should not connect to test database from dev environment. Or have a
         * fail safe to prevent uploading to dev bucket when connected to test database.
         */
        if ("OFF".equalsIgnoreCase(filesUploadSwitch)) {
            LOG.info("feature is {}", filesUploadSwitch);
            return;
        }

        List<BusinessCampaignEntity> businessCampaigns = businessCampaignService.findCampaignWithStatus(CampaignStatusEnum.S);
        if (businessCampaigns.isEmpty()) {
            /** No campaigns to upload. */
            return;
        } else {
            LOG.info("Campaigns to upload to live, count={}", businessCampaigns.size());
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (BusinessCampaignEntity businessCampaign : businessCampaigns) {
                String businessCampaignId = businessCampaign.getId();
                int distribution = businessCampaign.getDistributionPercent();
                String bizId = businessCampaign.getBizId();
                BizNameEntity bizName = bizService.getByBizNameId(bizId);
                String businessName = bizName.getBusinessName();

                StringBuilder sb = new StringBuilder();
                Collection<FileSystemEntity> fileSystemEntities = businessCampaign.getFileSystemEntities();
                if (null != fileSystemEntities) {
                    for (FileSystemEntity fileSystem : fileSystemEntities) {
                        sb.append(fileSystem.getKey()).append(",");
                    }
                }
                String imagePath = StringUtils.chop(sb.toString());

                BizDimensionEntity bizDimension = bizDimensionService.findBy(bizId);
                if (bizDimension != null) {
                    Double totalDistribution = Math.ceil(
                            Maths.divide(
                                    Maths.multiply(new BigDecimal(bizDimension.getUserCount()),
                                            distribution),
                                    100
                            ).doubleValue());

                    int distributionCount = 0;
                    List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizId);
                    for (BizStoreEntity bizStore : bizStores) {
                        List<UserDimensionEntity> userDimensions = userDimensionService.getAllStoreUsers(bizStore.getId());
                        for (UserDimensionEntity userDimension : userDimensions) {
                            CouponEntity coupon = new CouponEntity()
                                    .setRid(userDimension.getRid())
                                    .setBusinessName(businessName)
                                    .setFreeText(businessCampaign.getFreeText())
                                    .setAvailable(businessCampaign.getLive())
                                    .setExpire(businessCampaign.getEnd())
                                    .setCouponType(CouponTypeEnum.B)
                                    .setImagePath(imagePath)
                                    .setInitiatedFromId(businessCampaignId)
                                    .setCouponUploadStatus(CouponUploadStatusEnum.I);

                            try {
                                couponService.save(coupon);
                                success ++;
                                distributionCount ++;
                            } catch(Exception e) {
                                LOG.warn("Failed to save coupon rid={} bizId={} campaignId={}",
                                        userDimension.getRid(), bizId, businessCampaignId);
                                failure ++;
                            }

                            if (distributionCount > totalDistribution) {
                                break;
                            }
                        }
                    }

                    /** Set campaign to live and live campaign cannot be modified but only END. */
                    businessCampaign.setBusinessCampaignStatus(CampaignStatusEnum.L);
                    businessCampaignService.save(businessCampaign);
                } else {
                    LOG.warn("No biz dimension found for id={} bizId={}", businessCampaign.getId(), businessCampaign.getBizId());
                    skipped++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error creating coupons reason={}", e.getLocalizedMessage(), e);
            failure ++;
        } finally {
            saveUploadStats(cronStats, success, failure, skipped, businessCampaigns.size());
        }
    }

    private void saveUploadStats(CronStatsEntity cronStats, int success, int failure, int skipped, int size) {
        cronStats.addStats("success", success);
        cronStats.addStats("skipped", skipped);
        cronStats.addStats("failure", failure);
        cronStats.addStats("found", size);
        cronStatsService.save(cronStats);

        LOG.info("S3 upload success={} skipped={} failure={} total={}", success, skipped, failure, size);
    }
}
