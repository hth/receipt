package com.receiptofi.repository;

import com.receiptofi.domain.ExpenseTagEntity;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
public final class ExpenseTypeManagerImpl implements ExpenseTypeManager {
    private static final Logger log = LoggerFactory.getLogger(ExpenseTypeManagerImpl.class);

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<ExpenseTagEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(ExpenseTagEntity object) throws Exception {
        try {
            if(object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate record entry for ExpenseType: " + e.getLocalizedMessage());
            throw new Exception("Expense Name: " + object.getTagName() + ", already exists");
        }
    }

    @Override
    public ExpenseTagEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), ExpenseTagEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(ExpenseTagEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<ExpenseTagEntity> allExpenseTypes(String userProfileId) {
        return mongoTemplate.find(
                query(where("USER_PROFILE_ID").is(userProfileId)).with(new Sort(ASC, "TAG")),
                ExpenseTagEntity.class,
                TABLE
        );
    }

    @Override
    public List<ExpenseTagEntity> activeExpenseTypes(String userProfileId) {
        return mongoTemplate.find(
                query(where("USER_PROFILE_ID").is(userProfileId)
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
    public void changeVisibility(String expenseTypeId, boolean changeTo, String userProfileId) {
        Query query = query(where("id").is(new ObjectId(expenseTypeId)).and("USER_PROFILE_ID").is(userProfileId));
        Update update = update("A", changeTo);

        //TODO try using writeResult to check for condition
        WriteResult writeResult = mongoTemplate.updateFirst(query, entityUpdate(update), ExpenseTagEntity.class);
        log.info("changeVisibility WriteResult: ", writeResult);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
