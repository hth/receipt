package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.FriendEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
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
    public List<FriendEntity> findConnections(String rid) {
        return this.mongoTemplate.find(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(rid),
                        Criteria.where("RID").is(rid))),
                FriendEntity.class
        );
    }

    @Override
    public List<FriendEntity> findFriends(String rid) {
        return this.mongoTemplate.find(
                query(where("CON").is(true).orOperator(
                        Criteria.where("FID").is(rid),
                        Criteria.where("RID").is(rid))),
                FriendEntity.class
        );
    }

    @Override
    public List<FriendEntity> findPendingFriends(String rid) {
        return this.mongoTemplate.find(
                query(where("RID").is(rid)
                        .and("CON").is(false)
                        .and("AC").is(false)
                        .and("AUTH").exists(true)),
                FriendEntity.class
        );
    }

    @Override
    public List<FriendEntity> findAwaitingFriends(String rid) {
        return this.mongoTemplate.find(
                query(where("FID").is(rid)
                        .and("CON").is(false)
                        .and("AC").is(false)
                        .and("AUTH").exists(true)
                ),
                FriendEntity.class
        );
    }

    @Override
    public boolean hasConnection(String receiptUserId, String friendUserId) {
        return this.mongoTemplate.exists(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(receiptUserId).and("RID").is(friendUserId),
                        Criteria.where("RID").is(receiptUserId).and("FID").is(friendUserId))),
                FriendEntity.class
        );
    }

    @Override
    public void deleteHard(String receiptUserId, String friendUserId) {
        this.mongoTemplate.remove(query(where("RID").is(receiptUserId).and("FID").is(friendUserId)));
    }

    @Override
    public boolean updateResponse(String id, String authenticationKey, boolean acceptConnection, String rid) {
        WriteResult writeResult = this.mongoTemplate.updateFirst(
                query(where("id").is(id).and("AUTH").is(authenticationKey).and("AC").is(false).and("FID").is(rid)),
                entityUpdate(update("AC", acceptConnection).set("CON", acceptConnection).unset("AUTH")),
                FriendEntity.class);

        return writeResult.getN() > 0;
    }

    @Override
    public boolean cancelInvite(String id, String authenticationKey) {
        WriteResult writeResult = this.mongoTemplate.updateFirst(
                query(where("id").is(id).and("AUTH").is(authenticationKey)),
                entityUpdate(new Update().unset("AUTH")),
                FriendEntity.class);

        return writeResult.getN() > 0;
    }

    @Override
    public FriendEntity getConnection(String receiptUserId, String friendUserId) {
        return this.mongoTemplate.findOne(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(receiptUserId).and("RID").is(friendUserId),
                        Criteria.where("RID").is(receiptUserId).and("FID").is(friendUserId))),
                FriendEntity.class
        );
    }

    @Override
    public boolean unfriend(String receiptUserId, String friendUserId) {
        WriteResult writeResult = this.mongoTemplate.updateFirst(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(receiptUserId).and("RID").is(friendUserId).and("CON").is(true),
                        Criteria.where("RID").is(receiptUserId).and("FID").is(friendUserId).and("CON").is(true))),
                entityUpdate(update("CON", false).set("UNF", receiptUserId)),
                FriendEntity.class
        );

        return writeResult.getN() > 0;
    }
}
