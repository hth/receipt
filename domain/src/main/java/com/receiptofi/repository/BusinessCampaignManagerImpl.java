package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.*;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.BusinessUserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

    }

    @Override
    public void deleteHard(BusinessCampaignEntity object) {

    }

    @Override
    public BusinessCampaignEntity findById(String campaignId) {
        return mongoTemplate.findOne(
                query(where("id").is(campaignId)),
                BusinessCampaignEntity.class,
                TABLE
        );
    }
}
