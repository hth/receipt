package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ExhibitExpenseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 7/23/16 9:21 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ExhibitExpenseManagerImpl implements ExhibitExpenseManager {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTagManagerImpl.class);
    public static final String TABLE = BaseEntity.getClassAnnotationValue(
            ExhibitExpenseEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ExhibitExpenseManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void save(ExhibitExpenseEntity object) {

    }

    @Override
    public void deleteHard(ExhibitExpenseEntity object) {

    }
}
