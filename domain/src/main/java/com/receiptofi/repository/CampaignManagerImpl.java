package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
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
@Repository
public class CampaignManagerImpl implements CampaignManager {
    private static final Logger LOG = LoggerFactory.getLogger(CampaignManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            CampaignEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public CampaignManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CampaignEntity object) {
        if (StringUtils.isNotBlank(object.getBizId())) {
            mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save BusinessCampaign without bizId");
            throw new RuntimeException("Missing BizId " + object.getBizId());
        }
    }

    @Override
    public void deleteHard(CampaignEntity object) {

    }

    @Override
    public CampaignEntity findById(String campaignId, String bizId) {
        LOG.info("campaignId={} bizId={}", campaignId, bizId);
        return mongoTemplate.findOne(
                query(where("id").is(campaignId).and("BID").is(bizId)),
                CampaignEntity.class,
                TABLE
        );
    }

    @Override
    public CampaignEntity findById(String campaignId, UserLevelEnum userLevel) {
        LOG.info("campaignId={} userLevel={}", campaignId, userLevel);

        switch (userLevel) {
            case SUPERVISOR:
            case TECH_CAMPAIGN:
                return mongoTemplate.findOne(
                        query(where("id").is(campaignId)),
                        CampaignEntity.class,
                        TABLE
                );
            default:
                LOG.error("Not authorized to load campaign {}",userLevel);
                throw new UnsupportedOperationException("Not authorized to load campaign");
        }
    }

    @Override
    public List<CampaignEntity> findBy(String bizId) {
        return mongoTemplate.find(
                query(
                        where("BID").is(bizId)
                ).with(new Sort(Sort.Direction.DESC, "LP")),
                CampaignEntity.class,
                TABLE
        );
    }

    @Override
    public List<CampaignEntity> findAllPendingApproval(int limit) {
        return mongoTemplate.find(
                query(
                        where("CS").is(CampaignStatusEnum.P)
                ).limit(5).with(new Sort(Sort.Direction.DESC, "U")),
                CampaignEntity.class,
                TABLE
        );
    }

    @Override
    public long countPendingApproval() {
        return mongoTemplate.count(
                query(
                        where("CS").is(CampaignStatusEnum.P)
                ).with(new Sort(Sort.Direction.DESC, "U")),
                CampaignEntity.class,
                TABLE
        );
    }

    //TODO check if spring security role propagates all the way to Repository layer
    @Override
    public void updateCampaignStatus(
            String campaignId,
            String validateByRid,
            UserLevelEnum userLevel,
            CampaignStatusEnum campaignStatus,
            String reason
    ) {
        LOG.info("campaignId={} userLevel={}", campaignId, userLevel);

        switch (userLevel) {
            case SUPERVISOR:
            case TECH_CAMPAIGN:
                if (CampaignStatusEnum.D == campaignStatus) {
                    Assert.hasText(reason, "Reason cannot be empty when " + campaignStatus.getDescription());
                    mongoTemplate.updateFirst(
                            query(where("id").is(campaignId)),
                            entityUpdate(update("CS", campaignStatus).set("VB", validateByRid).set("RS", reason)),
                            CampaignEntity.class,
                            TABLE
                    );
                } else {
                    mongoTemplate.updateFirst(
                            query(where("id").is(campaignId)),
                            entityUpdate(update("CS", campaignStatus).set("VB", validateByRid)),
                            CampaignEntity.class,
                            TABLE
                    );
                }
                break;
            default:
                LOG.error("Reached unsupported userLevel={}", userLevel);
                throw new UnsupportedOperationException("Not authorized to modify campaign");
        }
    }

    @Override
    public List<CampaignEntity> findCampaignWithStatus(int limit, CampaignStatusEnum campaignStatus, Date since) {
        return mongoTemplate.find(
                query(where("CS").is(campaignStatus).and("LP").lt(since))
                        .limit(limit)
                        .with(new Sort(Sort.Direction.ASC, "LP")),
                CampaignEntity.class,
                TABLE
        );
    }
}
