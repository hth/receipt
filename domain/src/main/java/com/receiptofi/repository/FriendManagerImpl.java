package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.FriendEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 9/13/15 10:11 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class FriendManagerImpl implements FriendManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            FriendEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public FriendManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(FriendEntity object) {
        this.mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(FriendEntity object) {
        this.mongoTemplate.remove(object);
    }

    @Override
    public List<FriendEntity> findFriends(String rid) {
        return this.mongoTemplate.find(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(rid),
                        Criteria.where("RID").is(rid))),
                FriendEntity.class
        );
    }
}
