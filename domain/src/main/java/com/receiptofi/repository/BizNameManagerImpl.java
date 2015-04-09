package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizNameEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(BizNameEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if (null != object.getId()) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public BizNameEntity findOne(String id) {
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
        return mongoTemplate.findOne(query(where("N").is(StringUtils.EMPTY)), BizNameEntity.class, TABLE);
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
