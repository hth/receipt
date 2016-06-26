package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.utils.DateUtil;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 11/20/14 3:03 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class DocumentDailyStatManagerImpl implements DocumentDailyStatManager {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            DocumentDailyStatEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(DocumentDailyStatEntity object) {
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(DocumentDailyStatEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<DocumentDailyStatEntity> getStatsForDays(int days) {
        return mongoTemplate.find(
                query(
                        where("DT").gte(DateUtil.midnight(DateTime.now().minusDays(days)))
                ).with(new Sort(Sort.Direction.DESC, "DT")),
                DocumentDailyStatEntity.class,
                TABLE
        );
    }

    @Override
    public DocumentDailyStatEntity getLastEntry() {
        return mongoTemplate.findOne(
                new Query().with(new Sort(Sort.Direction.DESC, "DT")).limit(1),
                DocumentDailyStatEntity.class);
    }
}
