package com.tholix.repository;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

import com.tholix.domain.ExpenseTypeEntity;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
public class ExpenseTypeManagerImpl implements ExpenseTypeManager {
    private static final Logger log = Logger.getLogger(ExpenseTypeManagerImpl.class);

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<ExpenseTypeEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(ExpenseTypeEntity object) throws Exception {
        try {
            mongoTemplate.save(object);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate record entry for ExpenseType: " + e.getLocalizedMessage());
            throw new Exception("Expense Name: " + object.getExpName() + ", already exists");
        }
    }

    @Override
    public ExpenseTypeEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void delete(ExpenseTypeEntity object) {
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
        Query query = Query.query(Criteria.where("userProfileId").is(userProfileId));
        Sort sort = new Sort(Sort.Direction.ASC, "expName");

        return mongoTemplate.find(query.with(sort), ExpenseTypeEntity.class, TABLE);
    }

    @Override
    public void changeVisibility(String expenseTypeId, boolean changeTo) {
        Query query = Query.query(Criteria.where("id").is(expenseTypeId));

        Update update = new Update()
                .set("active", changeTo);

        mongoTemplate.updateFirst(query, update, ExpenseTypeEntity.class);
    }
}
