package com.receiptofi.repository;

import static com.receiptofi.domain.types.UserLevelEnum.SUPERVISOR;
import static com.receiptofi.domain.types.UserLevelEnum.TECH_CAMPAIGN;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.types.BusinessCampaignStatusEnum;
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
public class BusinessCampaignManagerImpl implements BusinessCampaignManager {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCampaignManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BusinessCampaignEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BusinessCampaignManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BusinessCampaignEntity object) {
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
    public void deleteHard(BusinessCampaignEntity object) {

    }

    @Override
    public BusinessCampaignEntity findById(String campaignId, String bizId) {
        LOG.info("campaignId={} bizId={}", campaignId, bizId);
        return mongoTemplate.findOne(
                query(where("id").is(campaignId).and("BID").is(bizId)),
                BusinessCampaignEntity.class,
                TABLE
        );
    }

    @Override
    public BusinessCampaignEntity findById(String campaignId, UserLevelEnum userLevel) {
        LOG.info("campaignId={} userLevel={}", campaignId, userLevel);

        switch (userLevel) {
            case SUPERVISOR:
            case TECH_CAMPAIGN:
                return mongoTemplate.findOne(
                        query(where("id").is(campaignId)),
                        BusinessCampaignEntity.class,
                        TABLE
                );
            default:
                throw new UnsupportedOperationException("Not authorized to load campaign");
        }
    }

    @Override
    public List<BusinessCampaignEntity> findBy(String bizId) {
        return mongoTemplate.find(
                query(
                        where("BID").is(bizId)
                ).with(new Sort(Sort.Direction.DESC, "LP")),
                BusinessCampaignEntity.class,
                TABLE
        );
    }

    @Override
    public List<BusinessCampaignEntity> findAllPendingApproval(int limit) {
        return mongoTemplate.find(
                query(
                        where("CS").is(BusinessCampaignStatusEnum.P)
                ).limit(5).with(new Sort(Sort.Direction.DESC, "U")),
                BusinessCampaignEntity.class,
                TABLE
        );
    }

    @Override
    public long countPendingApproval() {
        return mongoTemplate.count(
                query(
                        where("CS").is(BusinessCampaignStatusEnum.P)
                ).with(new Sort(Sort.Direction.DESC, "U")),
                BusinessCampaignEntity.class,
                TABLE
        );
    }
}
