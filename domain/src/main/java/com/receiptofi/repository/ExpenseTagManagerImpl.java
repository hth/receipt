package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ExpenseTagEntity;

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

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(ExpenseTagEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for ExpenseTag={}", object, e);
            throw new RuntimeException("Tag Name: " + object.getTagName() + ", already exists");
        }
    }

    @Override
    public ExpenseTagEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), ExpenseTagEntity.class, TABLE);
    }

    @Override
    public void deleteHard(ExpenseTagEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<ExpenseTagEntity> allExpenseTypes(String userProfileId) {
        return mongoTemplate.find(
                query(where("RID").is(userProfileId)).with(new Sort(ASC, "TAG")),
                ExpenseTagEntity.class,
                TABLE
        );
    }

    @Override
    public List<ExpenseTagEntity> activeExpenseTypes(String receiptUserId) {
        return mongoTemplate.find(
                query(where("RID").is(receiptUserId)
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
    public void changeVisibility(String expenseTypeId, boolean changeTo, String receiptUserId) {
        mongoTemplate.updateFirst(
                query(where("id").is(new ObjectId(expenseTypeId)).and("RID").is(receiptUserId)),
                entityUpdate(update("A", changeTo)),
                ExpenseTagEntity.class);
    }

    @Override
    public void updateExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid) {
        try {
            mongoTemplate.updateFirst(
                    query(where("id").is(new ObjectId(expenseTypeId)).and("RID").is(rid)),
                    entityUpdate(update("TAG", expenseTagName).set("CLR", expenseTagColor)),
                    ExpenseTagEntity.class);
        } catch (DuplicateKeyException e) {
            LOG.error("Duplicate record entry for TagName={} rid={}", expenseTagName, rid, e);
            throw new RuntimeException("Tag Name: " + expenseTagName + ", already exists");
        }
    }
}
