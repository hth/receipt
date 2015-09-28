package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.SplitExpensesEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 9/27/15 2:37 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class SplitExpensesManagerImpl implements SplitExpensesManager {
    private static final Logger LOG = LoggerFactory.getLogger(SplitExpensesManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            SplitExpensesEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(SplitExpensesEntity object) {
        try {
            if (null != object.getId()) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for SplitExpenses={}", object, e);
            throw new RuntimeException("SplitExpenses: " + object.getId() + ", already exists");
        }
    }

    @Override
    public void deleteHard(SplitExpensesEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public boolean deleteHard(String rdid, String rid, String fid) {
        WriteResult writeResult = mongoTemplate.remove(
                query(where("RDID").is(rdid).and("RID").is(rid).and("FID").is(fid)),
                SplitExpensesEntity.class
        );
        return writeResult.getN() > 0;
    }

    @Override
    public List<SplitExpensesEntity> getSplitExpensesFriendsForReceipt(String rdid) {
        return mongoTemplate.find(
                query(where("RDID").is(rdid)),
                SplitExpensesEntity.class
        );
    }
}
