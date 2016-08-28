package com.receiptofi.repository.analytic;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.UserDimensionEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Autowired
    public UserDimensionManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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
}
