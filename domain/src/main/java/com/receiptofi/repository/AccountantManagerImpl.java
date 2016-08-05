package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.AccessHistory;
import com.receiptofi.domain.AccountantEntity;
import com.receiptofi.domain.BaseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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
public class AccountantManagerImpl implements AccountantManager {
    private static final Logger LOG = LoggerFactory.getLogger(AccountantManagerImpl.class);
    public static final String TABLE = BaseEntity.getClassAnnotationValue(
            AccountantEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public AccountantManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void save(AccountantEntity object) {

    }

    @Override
    public void deleteHard(AccountantEntity object) {

    }

    @Override
    public List<AccountantEntity> getUsersSubscribedToAccountant(String aid, int limit) {
        return mongoTemplate.find(
                query(where("AID").is(aid).and("CON").is(true)).limit(limit),
                AccountantEntity.class,
                TABLE);
    }

    @Override
    public AccountantEntity getUserForAccountant(String rid, String auth, String aid, String ip) {
        return mongoTemplate.findAndModify(
                query(where("RID").is(rid).and("AUTH").is(auth).and("AID").is(aid).and("CON").is(true)),
                entityUpdate(new Update().push("AH").slice(10).each(new AccessHistory(ip, new Date()))),
                FindAndModifyOptions.options().returnNew(true),
                AccountantEntity.class,
                TABLE);
    }
}
