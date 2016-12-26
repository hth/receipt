package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.types.PaymentGatewayEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 3/19/15 2:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BillingAccountManagerImpl implements BillingAccountManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BillingAccountEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BillingAccountManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BillingAccountEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(BillingAccountEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BillingAccountEntity getBillingAccount(String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid)
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ).with(new Sort(Sort.Direction.DESC, "C")),
                BillingAccountEntity.class
        );
    }

    @Override
    public List<BillingAccountEntity> getAllBillingAccount(String rid) {
        return mongoTemplate.find(query(where("RID").is(rid)), BillingAccountEntity.class);
    }

    //TODO is the PGU incorrect?
    @Override
    public BillingAccountEntity getBySubscription(String subscriptionId, PaymentGatewayEnum paymentGateway) {
        return mongoTemplate.findOne(
                query(where("PGU.SD").is(subscriptionId).and("PGU.PG").is(paymentGateway)),
                BillingAccountEntity.class
        );
    }

    @Override
    public List<BillingAccountEntity> getAllBilling() {
        return mongoTemplate.findAll(BillingAccountEntity.class);
    }
}
