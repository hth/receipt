package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.types.PaginationEnum;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

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
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizStoreEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(BizStoreEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public BizStoreEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), BizStoreEntity.class, TABLE);
    }

    @Override
    public void deleteHard(BizStoreEntity object) {
        mongoTemplate.remove(object);
    }

    public BizStoreEntity noStore() {
        return mongoTemplate.findOne(query(where("AD").is(StringUtils.EMPTY)), BizStoreEntity.class, TABLE);
    }

    public BizStoreEntity findOne(BizStoreEntity bizStoreEntity) {
        Query query = query(where("AD").is(bizStoreEntity.getAddress()));

        if (StringUtils.isNotEmpty(bizStoreEntity.getPhone())) {
            query.addCriteria(where("PH").is(bizStoreEntity.getPhone()));
        }

        return mongoTemplate.findOne(query, BizStoreEntity.class, TABLE);
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
                    BizStoreEntity.class,
                    TABLE);
        } else {
            return mongoTemplate.find(
                    query(criteriaA).limit(PaginationEnum.TEN.getLimit()),
                    BizStoreEntity.class,
                    TABLE);
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
        return mongoTemplate.find(query.limit(PaginationEnum.TEN.getLimit()), BizStoreEntity.class, TABLE);
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
        return mongoTemplate.find(query, BizStoreEntity.class, TABLE);
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
        return mongoTemplate.find(query, BizStoreEntity.class, TABLE);
    }

    @Override
    public List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit) {
        Sort sort = new Sort(Sort.Direction.DESC, "C");
        return mongoTemplate.find(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()))).with(sort).limit(limit),
                BizStoreEntity.class,
                TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
