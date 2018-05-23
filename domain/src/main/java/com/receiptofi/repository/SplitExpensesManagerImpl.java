package com.receiptofi.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.types.SplitStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

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

    private MongoTemplate mongoTemplate;

    @Autowired
    public SplitExpensesManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public SplitExpensesEntity getById(String id, String rid) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id).and("RID").is(rid)), SplitExpensesEntity.class);
    }

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
        DeleteResult deleteResult = mongoTemplate.remove(
                query(where("RDID").is(rdid)
                        .and("RID").is(rid)
                        .and("FID").is(fid)
                        .and("SS").is(SplitStatusEnum.U)),
                SplitExpensesEntity.class
        );
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public List<SplitExpensesEntity> getSplitExpensesFriendsForReceipt(String rdid) {
        return mongoTemplate.find(
                query(where("RDID").is(rdid)),
                SplitExpensesEntity.class
        );
    }

    @Override
    public boolean doesExists(String rdid, String rid, String fid) {
        return mongoTemplate.exists(
                query(where("RDID").is(rdid).and("RID").is(rid).and("FID").is(fid)),
                SplitExpensesEntity.class
        );
    }

    @Override
    public List<SplitExpensesEntity> getOwesMe(String rid) {
        TypedAggregation<SplitExpensesEntity> agg = newAggregation(SplitExpensesEntity.class,
                match(where("RID").is(rid)
                        .and("SS").ne(SplitStatusEnum.S)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )),
                group("friendUserId")
                        .first("friendUserId").as("FID")
                        .sum("splitTotal").as("ST"),
                sort(DESC, previousOperation())
        );

        return mongoTemplate.aggregate(agg, TABLE, SplitExpensesEntity.class).getMappedResults();
    }

    @Override
    public List<SplitExpensesEntity> getOwesOthers(String rid) {
        TypedAggregation<SplitExpensesEntity> agg = newAggregation(SplitExpensesEntity.class,
                match(where("FID").is(rid)
                        .and("SS").ne(SplitStatusEnum.S)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )),
                group("receiptUserId")
                        .first("receiptUserId").as("RID")
                        .sum("splitTotal").as("ST"),
                sort(DESC, previousOperation())
        );

        return mongoTemplate.aggregate(agg, TABLE, SplitExpensesEntity.class).getMappedResults();
    }

    @Override
    public boolean updateSplitTotal(String rdid, Double splitTotal) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
                query(where("RDID").is(rdid)),
                entityUpdate(update("ST", splitTotal)),
                SplitExpensesEntity.class
        );
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public List<SplitExpensesEntity> getSplitExpenses(String rid, String fid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .and("FID").is(fid)
                        .and("SS").ne(SplitStatusEnum.S)
                ),
                SplitExpensesEntity.class
        );
    }

    @Override
    public SplitExpensesEntity findSplitExpensesToSettle(String fid, String rid, Double splitTotal) {
        return mongoTemplate.findOne(
                query(where("RID").is(fid)
                        .and("FID").is(rid)
                        .and("SS").ne(SplitStatusEnum.S)),
                SplitExpensesEntity.class
        );
    }

    @Override
    public boolean hasSettleProcessStarted(String rdid) {
        return mongoTemplate.count(
                query(where("RDID").is(rdid)
                        .and("SS").ne(SplitStatusEnum.U)),
                SplitExpensesEntity.class
        ) > 0;
    }
}
