package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CronStatsEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 4/21/15 12:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class CronStatsManagerImpl implements CronStatsManager {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            CronStatsEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(CronStatsEntity object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(CronStatsEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<String> getUniqueCronTasks() {
        return mongoTemplate.getCollection(TABLE).distinct("TN");
    }

    @Override
    public List<CronStatsEntity> getHistoricalData(String task, int limit) {
        return mongoTemplate.find(
                query(where("TN").is(task)).with(new Sort(Sort.Direction.DESC, "C")).limit(10),
                CronStatsEntity.class,
                TABLE
        );
    }
}
