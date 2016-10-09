package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isDeleted;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.types.ExpenseTagIconEnum;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ExpenseTagManagerImpl implements ExpenseTagManager {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTagManagerImpl.class);
    public static final String TABLE = BaseEntity.getClassAnnotationValue(
            ExpenseTagEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ExpenseTagManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ExpenseTagEntity object) {
        try {
            if (null != object.getId()) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for ExpenseTag={}", object, e);
            throw new RuntimeException("Tag Name: " + object.getTagName() + ", already exists");
        }
    }

    @Override
    public void deleteHard(ExpenseTagEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public List<ExpenseTagEntity> getAllExpenseTags(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)).with(new Sort(ASC, "TAG")),
                ExpenseTagEntity.class,
                TABLE
        );
    }

    @Override
    public List<ExpenseTagEntity> getExpenseTags(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ).with(new Sort(ASC, "TAG")),
                ExpenseTagEntity.class,
                TABLE
        );
    }

    @Override
    public ExpenseTagEntity getExpenseTag(String rid, String expenseTagId) {
        Assert.hasText(expenseTagId, "ExpenseTagId is empty");
        return mongoTemplate.findOne(
                query(where("id").is(new ObjectId(expenseTagId))
                                .and("RID").is(rid)
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ),
                ExpenseTagEntity.class,
                TABLE
        );
    }

    @Override
    public ExpenseTagEntity getExpenseTagByName(String rid, String expenseTagName) {
        Assert.hasText(expenseTagName, "ExpenseTagName is empty");
        return mongoTemplate.findOne(
                query(where("TAG").is(expenseTagName)
                                .and("RID").is(rid)
                                .andOperator(
                                        isNotActive(),
                                        isDeleted()
                                )
                ),
                ExpenseTagEntity.class,
                TABLE
        );
    }

    @Override
    public void updateExpenseTag(String expenseTagId, String expenseTagName, String expenseTagColor, ExpenseTagIconEnum expenseTagIcon, String rid) {
        try {
            mongoTemplate.updateFirst(
                    query(where("id").is(new ObjectId(expenseTagId)).and("RID").is(rid)),
                    entityUpdate(update("TAG", expenseTagName).set("CLR", expenseTagColor).set("IC", expenseTagIcon)),
                    ExpenseTagEntity.class);
        } catch (DuplicateKeyException e) {
            LOG.error("Duplicate record entry for TagName={} rid={}", expenseTagName, rid, e);
            throw new RuntimeException("Tag Name: " + expenseTagName + ", already exists");
        }
    }

    @Override
    public boolean softDeleteExpenseTag(String expenseTagId, String expenseTagName, String rid) {
        WriteResult writeResult = mongoTemplate.updateFirst(
                query(where("id").is(new ObjectId(expenseTagId))
                        .and("RID").is(rid)
                        .and("TAG").is(expenseTagName)),
                entityUpdate(update("A", false).set("D", true)),
                ExpenseTagEntity.class);
        return writeResult.getN() > 0;
    }
}
