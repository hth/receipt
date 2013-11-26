package com.receiptofi.repository;

import com.receiptofi.domain.ExpenseTypeEntity;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.*;

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
    public List<ExpenseTypeEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(ExpenseTypeEntity object) throws Exception {
        try {
            if(object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate record entry for ExpenseType: " + e.getLocalizedMessage());
            throw new Exception("Expense Name: " + object.getExpName() + ", already exists");
        }
    }

    @Override
    public ExpenseTypeEntity findOne(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), ExpenseTypeEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(ExpenseTypeEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void createCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void dropCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<ExpenseTypeEntity> allExpenseTypes(String userProfileId) {
        Query query = Query.query(Criteria.where("USER_PROFILE_ID").is(userProfileId));
        Sort sort = new Sort(Sort.Direction.ASC, "EXP_NAME");

        return mongoTemplate.find(query.with(sort), ExpenseTypeEntity.class, TABLE);
    }

    @Override
    public List<ExpenseTypeEntity> activeExpenseTypes(String userProfileId) {
        Criteria criteria1 = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Query query = Query.query(criteria1).addCriteria(isActive()).addCriteria(isNotDeleted());
        Sort sort = new Sort(Sort.Direction.ASC, "EXP_NAME");

        return mongoTemplate.find(query.with(sort), ExpenseTypeEntity.class, TABLE);
    }

    @Override
    public void changeVisibility(String expenseTypeId, boolean changeTo, String userProfileId) {
        Criteria criteria1 = Criteria.where("id").is(new ObjectId(expenseTypeId));
        Criteria criteria2 = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Query query = Query.query(criteria1).addCriteria(criteria2);
        Update update = Update.update("ACTIVE", changeTo);

        //TODO try using writeResult to check for condition
        WriteResult writeResult = mongoTemplate.updateFirst(query, update(update), ExpenseTypeEntity.class);
        log.info("changeVisibility WriteResult: ", writeResult);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
