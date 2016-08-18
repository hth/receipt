package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.CouponEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.domain.analytic.UserDimensionEntity;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.CouponTypeEnum;
import com.receiptofi.domain.types.CouponUploadStatusEnum;
import com.receiptofi.domain.util.ImagePathOnS3;
import com.receiptofi.service.BizService;
import com.receiptofi.service.CampaignService;
import com.receiptofi.service.CouponService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.analytic.BizDimensionService;
import com.receiptofi.service.analytic.UserDimensionService;
import com.receiptofi.utils.Maths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
public class CampaignProcess {
    private static final Logger LOG = LoggerFactory.getLogger(CampaignProcess.class);

    private final String goLiveSwitch;
    private CampaignService campaignService;
    private BizDimensionService bizDimensionService;
    private CronStatsService cronStatsService;
    private BizService bizService;
    private UserDimensionService userDimensionService;
    private CouponService couponService;

    @Autowired
    public CampaignProcess(
            @Value ("${CampaignProcess.go.live.switch}")
            String goLiveSwitch,

            CampaignService campaignService,
            BizDimensionService bizDimensionService,
            CronStatsService cronStatsService, BizService bizService,
            UserDimensionService userDimensionService,
            CouponService couponService) {
        this.goLiveSwitch = goLiveSwitch;
        this.campaignService = campaignService;
        this.bizDimensionService = bizDimensionService;
        this.cronStatsService = cronStatsService;
        this.bizService = bizService;
        this.userDimensionService = userDimensionService;
        this.couponService = couponService;
    }

    /**
     * Create coupons from campaign and mark Campaign as LIVE.
     * Note: Cron string blow run every 2 minute.
     *
     * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled">http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled</a>
     */
    @Scheduled (fixedDelayString = "${loader.Campaign.setToLive}")
    public void setToLiveCampaign() {
        CronStatsEntity cronStats = new CronStatsEntity(
                CampaignProcess.class.getName(),
                "setToLiveCampaign",
                goLiveSwitch);

        if ("OFF".equalsIgnoreCase(goLiveSwitch)) {
            LOG.info("feature is {}", goLiveSwitch);
            return;
        }

        List<CampaignEntity> campaigns = campaignService.findCampaignWithStatus(CampaignStatusEnum.S);
        if (campaigns.isEmpty()) {
            /** No campaigns set to live found. */
            return;
        } else {
            LOG.info("Campaigns set to go live, count={}", campaigns.size());
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (CampaignEntity campaign : campaigns) {
                String businessCampaignId = campaign.getId();
                int distribution = campaign.getDistributionPercent();
                String bizId = campaign.getBizId();
                BizNameEntity bizName = bizService.getByBizNameId(bizId);
                String businessName = bizName.getBusinessName();
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
                                    .setFreeText(campaign.getFreeText())
                                    .setAvailable(campaign.getLive())
                                    .setExpire(campaign.getEnd())
                                    .setCouponType(CouponTypeEnum.B)
                                    .setImagePath(ImagePathOnS3.populateImagePath(campaign.getFileSystemEntities()))
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
                    campaign.setCampaignStatus(CampaignStatusEnum.L);
                    campaignService.save(campaign);
                } else {
                    LOG.warn("No biz dimension found for id={} bizId={}", campaign.getId(), campaign.getBizId());
                    skipped++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error creating coupons reason={}", e.getLocalizedMessage(), e);
            failure ++;
        } finally {
            saveUploadStats(cronStats, success, failure, skipped, campaigns.size());
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
