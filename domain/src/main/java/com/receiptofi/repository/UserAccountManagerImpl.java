package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.*;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.repository.util.AppendAdditionalFields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 4/23/14 6:43 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class UserAccountManagerImpl implements UserAccountManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserAccountEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserAccountManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserAccountEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserAccountEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), UserAccountEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserAccountEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public UserAccountEntity findByReceiptUserId(String rid) {
        return mongoTemplate.findOne(query(where("RID").is(rid)), UserAccountEntity.class, TABLE);
    }

    @Override
    public UserAccountEntity findByUserId(String mail) {
        return mongoTemplate.findOne(query(where("UID").is(mail)), UserAccountEntity.class, TABLE);
    }

    @Override
    public UserAccountEntity findByProviderUserId(String providerUserId) {
        return mongoTemplate.findOne(query(where("PUID").is(providerUserId)), UserAccountEntity.class, TABLE);
    }

    @Override
    public UserAccountEntity findByAuthorizationCode(ProviderEnum provider, String authorizationCode) {
        return mongoTemplate.findOne(
                query(where("PID").is(provider).and("AC").is(authorizationCode)),
                UserAccountEntity.class, TABLE
        );
    }

    @Override
    public int inactiveNonValidatedAccount(Date pastActivationDate) {
        WriteResult writeResult = mongoTemplate.updateMulti(
                query(where("AV").is(false).and("AVD").lt(pastActivationDate).and("A").is(true)),
                AppendAdditionalFields.entityUpdate(update("A", false).set("AIR", AccountInactiveReasonEnum.ANV)),
                UserAccountEntity.class
        );

        return writeResult.getN();
    }

    @Override
    public List<UserAccountEntity> findRegisteredAccountWhenRegistrationIsOff(int registrationInviteDailyLimit) {
        return mongoTemplate.find(
                query(where("RIO").exists(true).andOperator(where("RIO").is(true))).limit(registrationInviteDailyLimit),
                UserAccountEntity.class
        );
    }

    @Override
    public void removeRegistrationIsOffFrom(String id) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                AppendAdditionalFields.entityUpdate(new Update().unset("RIO")),
                UserAccountEntity.class
        );
    }

    @Override
    public void updateAccountToValidated(String id, AccountInactiveReasonEnum air) {
        mongoTemplate.updateFirst(
                query(where("id").is(id).and("AIR").is(air)),
                AppendAdditionalFields.entityUpdate(update("A", true).unset("AIR")),
                UserAccountEntity.class
        );
    }
}
