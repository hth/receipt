package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class BizNameManagerImpl implements BizNameManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizNameManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizNameEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizNameManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizNameEntity object) {
        if (StringUtils.isNotBlank(object.getBusinessName())) {
            if (null != object.getId()) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save BizName with empty name");
            throw new RuntimeException("Found no name for business");
        }
    }

    @Override
    public BizNameEntity getById(String id) {
        Assert.hasText(id, "Id empty for BizNameEntity");
        return mongoTemplate.findOne(query(where("id").is(id)), BizNameEntity.class, TABLE);
    }

    @Override
    public BizNameEntity findOneByName(String businessName) {
        return mongoTemplate.findOne(query(where("N").is(businessName)), BizNameEntity.class, TABLE);
    }

    @Override
    public void deleteHard(BizNameEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BizNameEntity noName() {
        return mongoTemplate.findOne(query(where("N").is("")), BizNameEntity.class, TABLE);
    }

    @Override
    public List<BizNameEntity> findAllBizWithMatchingName(String businessName) {
        return mongoTemplate.find(query(where("N").regex("^" + businessName, "i")), BizNameEntity.class, TABLE);
    }

    @Override
    public Set<String> findAllDistinctBizStr(String businessName) {
        return findAllBizWithMatchingName(businessName).stream().map(BizNameEntity::getBusinessName).collect(Collectors.toSet());
    }

    public List<BizNameEntity> findAll(int skip, int limit) {
        return mongoTemplate.find(
                new Query().skip(skip).limit(limit),
                BizNameEntity.class
        );
    }
}
