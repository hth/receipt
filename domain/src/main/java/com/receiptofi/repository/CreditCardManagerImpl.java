package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CreditCardEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 8/30/16 6:27 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class CreditCardManagerImpl implements CreditCardManager {
    private static final Logger LOG = LoggerFactory.getLogger(CreditCardManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            CreditCardEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public CreditCardManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CreditCardEntity object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void updateLastUsed(String rid, String cardDigit, Date lastUsed) {
        CreditCardEntity creditCard = findCard(rid, cardDigit);

        if (null != creditCard && creditCard.getLastUsed().before(lastUsed)) {
            mongoTemplate.updateFirst(
                    query(where("RID").is(rid).and("CD").is(cardDigit)),
                    entityUpdate(update("LU", lastUsed).inc("UC", 1)),
                    CreditCardEntity.class,
                    TABLE
            );
        } else {
            increaseUsed(rid, cardDigit);
        }
    }

    @Override
    public void decreaseUsed(String rid, String cardDigit) {
        mongoTemplate.updateFirst(
                query(where("RID").is(rid).and("CD").is(cardDigit)),
                entityUpdate(new Update().inc("UC", -1)),
                CreditCardEntity.class,
                TABLE
        );
    }

    @Override
    public void increaseUsed(String rid, String cardDigit) {
        mongoTemplate.updateFirst(
                query(where("RID").is(rid).and("CD").is(cardDigit)),
                entityUpdate(new Update().inc("UC", 1)),
                CreditCardEntity.class,
                TABLE
        );
    }

    @Override
    public List<CreditCardEntity> getCreditCards(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)),
                CreditCardEntity.class,
                TABLE
        );
    }

    @Override
    public CreditCardEntity findCard(String rid, String cardDigit) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("CD").is(cardDigit)),
                CreditCardEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(CreditCardEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
