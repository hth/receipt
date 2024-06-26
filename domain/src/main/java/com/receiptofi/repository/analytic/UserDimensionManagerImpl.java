package com.receiptofi.repository.analytic;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.domain.analytic.UserDimensionEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 7/2/16 3:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class UserDimensionManagerImpl implements UserDimensionManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizDimensionManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserDimensionEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;
    private BizDimensionManager bizDimensionManager;

    @Autowired
    public UserDimensionManagerImpl(MongoTemplate mongoTemplate, BizDimensionManager bizDimensionManager) {
        this.mongoTemplate = mongoTemplate;
        this.bizDimensionManager = bizDimensionManager;
    }

    @Override
    public void save(UserDimensionEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void deleteHard(UserDimensionEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<UserDimensionEntity> getAllStoreUsers(String storeId) {
        Query query = query(where("storeId").is(storeId));
        query.fields().include("RID");

        return mongoTemplate.find(
                query,
                UserDimensionEntity.class,
                TABLE
        );
    }

    @Override
    @Cacheable (value = "businessUserCount", keyGenerator = "customKeyGenerator")
    public long getBusinessUserCount(String bizId) {
        LOG.info("Getting user count for bizId={}", bizId);
        return mongoTemplate.count(
                query(where("bizId").is(bizId)),
                UserDimensionEntity.class,
                TABLE
        );
    }

    @Override
    public GeoResults<UserDimensionEntity> findAllNonPatrons(
            double longitude,
            double latitude,
            int distributionRadius,
            String storeId,
            String countryShortName
    ) {
        NearQuery nearQuery = NearQuery
                .near(new Point(longitude, latitude))
                .maxDistance(new Distance(distributionRadius, Metrics.MILES));

        Query query = query(
                where("storeId").ne(storeId)
                        .and("country").is(countryShortName)
        );
        query.fields().include("RID");

        /* Includes more than RID. So include is not needed. */
        return mongoTemplate.geoNear(nearQuery.query(query), UserDimensionEntity.class);
    }

    @Override
    public Set<String> findUserAssociatedAllDistinctBizStr(String rid) {
        Query query = query(where("RID").is(rid));
        query.fields().include("bizId");

        List<UserDimensionEntity> userDimensions = mongoTemplate.find(
                query,
                UserDimensionEntity.class,
                TABLE
        );

        Set<String> bizNames = new HashSet<>();
        for (UserDimensionEntity userDimensionEntity : userDimensions) {
            query = query(where("bizId").is(userDimensionEntity.getBizId()));
            query.fields().include("bizName");

            BizDimensionEntity bizDimension = mongoTemplate.findOne(query, BizDimensionEntity.class);
            if (null != bizDimension) {
                bizNames.add(bizDimension.getBizName());
            }
        }

        return bizNames;
    }

    @Override
    public Set<String> findUserAssociatedBizName(String bizName, String rid) {
        Query query = query(where("RID").is(rid));
        query.fields().include("bizId");

        List<UserDimensionEntity> userDimensions = mongoTemplate.find(
                query,
                UserDimensionEntity.class,
                TABLE
        );

        Set<String> bizNames = new HashSet<>();
        for (UserDimensionEntity userDimensionEntity : userDimensions) {
            query = query(where("bizId").is(userDimensionEntity.getBizId()).and("bizName").regex("^" + bizName, "i"));
            query.fields().include("bizName");

            BizDimensionEntity bizDimension = mongoTemplate.findOne(query, BizDimensionEntity.class);
            if (null != bizDimension) {
                bizNames.add(bizDimension.getBizName());
            }
        }

        return bizNames;
    }
}
