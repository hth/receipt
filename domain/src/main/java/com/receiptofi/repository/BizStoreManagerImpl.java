package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.types.PaginationEnum;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:21 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class BizStoreManagerImpl implements BizStoreManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizStoreEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizStoreManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizStoreEntity object) {
        if (null != object.getBizName() && null != object.getBizName().getId()) {
            mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save bizStore without bizName");
            throw new RuntimeException("Missing BizName for BizStore " + object.getAddress());
        }
    }

    @Override
    public BizStoreEntity getById(String id) {
        Assert.hasText(id, "Id empty for BizStore");
        return mongoTemplate.findOne(query(where("id").is(id)), BizStoreEntity.class);
    }

    @Override
    public void deleteHard(BizStoreEntity object) {
        mongoTemplate.remove(object);
    }

    public BizStoreEntity noStore() {
        return mongoTemplate.findOne(query(where("AD").is(StringUtils.EMPTY)), BizStoreEntity.class);
    }

    public BizStoreEntity findMatchingStore(String address, String phone) {
        /** Constrain is based on address and if it has phone number which makes it unique. */
        Criteria criteria;
        if (StringUtils.isEmpty(phone)) {
            criteria = where("AD").is(address);
        } else {
            criteria = where("AD").is(address).and("PH").is(phone);
        }

        return mongoTemplate.findOne(query(criteria), BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> findAllWithAnyAddressAnyPhone(
            String bizAddress,
            String bizPhone,
            BizNameEntity bizNameEntity
    ) {
        Criteria criteriaA = new Criteria();
        if (StringUtils.isNotEmpty(bizAddress)) {
            criteriaA.and("AD").regex(bizAddress, "i");
        }
        if (StringUtils.isNotEmpty(bizPhone)) {
            criteriaA.and("PH").regex(bizPhone, "i");
        }

        if (bizNameEntity != null && StringUtils.isNotEmpty(bizNameEntity.getId())) {
            Criteria criteriaB = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            return mongoTemplate.find(
                    query(criteriaB).addCriteria(criteriaA).limit(PaginationEnum.TEN.getLimit()),
                    BizStoreEntity.class
            );
        } else {
            return mongoTemplate.find(
                    query(criteriaA).limit(PaginationEnum.TEN.getLimit()),
                    BizStoreEntity.class
            );
        }
    }

    @Override
    public List<BizStoreEntity> findAllWithStartingAddressStartingPhone(
            String bizAddress,
            String bizPhone,
            BizNameEntity bizNameEntity
    ) {
        Query query = null;
        if (StringUtils.isNotEmpty(bizAddress)) {
            query = query(where("AD").regex("^" + bizAddress, "i"));
        }
        if (StringUtils.isNotEmpty(bizPhone)) {
            Criteria criteria = where("PH").regex("^" + bizPhone, "i");
            if (null == query) {
                query = query(criteria);
            } else {
                query.addCriteria(criteria);
            }
        }

        if (bizNameEntity != null && StringUtils.isNotEmpty(bizNameEntity.getId())) {
            Criteria criteriaA = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            if (null == query) {
                query = query(criteriaA);
            } else {
                query.addCriteria(criteriaA);
            }
        }
        Assert.notNull(query);
        return mongoTemplate.find(query.limit(PaginationEnum.TEN.getLimit()), BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> getAllWithJustSpecificField(
            String bizAddress,
            BizNameEntity bizNameEntity,
            String fieldName
    ) {
        Query query;
        if (StringUtils.isBlank(bizAddress)) {
            query = query(where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId())));
        } else {
            query = query(where("AD").regex("^" + bizAddress, "i")
                            .and("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()))
            );
        }
        query.fields().include(fieldName);
        return mongoTemplate.find(query, BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> getAllWithJustSpecificField(
            String bizPhone,
            String bizAddress,
            BizNameEntity bizNameEntity,
            String fieldName
    ) {
        Query query;
        if (StringUtils.isBlank(bizPhone)) {
            Criteria criteriaB = where("AD").is(bizAddress);
            Criteria criteriaC = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));

            query = query(criteriaC).addCriteria(criteriaB);
        } else {
            Criteria criteriaA = where("PH").regex("^" + bizPhone, "i");
            Criteria criteriaB = where("AD").is(bizAddress);
            Criteria criteriaC = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));

            query = query(criteriaC).addCriteria(criteriaB).addCriteria(criteriaA);
        }
        query.fields().include(fieldName);
        return mongoTemplate.find(query, BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit) {
        return mongoTemplate.find(
                query(
                        where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId())))
                        .with(new Sort(Sort.Direction.DESC, "C"))
                        .limit(limit),
                BizStoreEntity.class
        );
    }

    @Override
    public BizStoreEntity findOne(String bizNameId) {
        return mongoTemplate.findOne(
                query(
                        where("BIZ_NAME.$id").is(new ObjectId(bizNameId)))
                        .with(new Sort(Sort.Direction.DESC, "C"))
                ,
                BizStoreEntity.class
        );
    }

    @Override
    public List<BizStoreEntity> getAll(int skip, int limit) {
        return mongoTemplate.find(new Query().skip(skip).limit(limit), BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> getAllWhereNotValidatedUsingExternalAPI(int skip, int limit) {
        return mongoTemplate.find(
                query(where("EA").is(false)).skip(skip).limit(limit),
                BizStoreEntity.class
        );
    }
}
