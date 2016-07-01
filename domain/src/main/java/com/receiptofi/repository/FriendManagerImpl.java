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
    public boolean hasConnection(String rid, String fid) {
        return this.mongoTemplate.exists(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(rid).and("RID").is(fid),
                        Criteria.where("RID").is(rid).and("FID").is(fid))),
                FriendEntity.class
        );
    }

    @Override
    public void deleteHard(String rid, String fid) {
        this.mongoTemplate.remove(
                query(where("RID").is(rid).and("FID").is(fid)),
                FriendEntity.class,
                TABLE);
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
    public FriendEntity getConnection(String rid, String fid) {
        return this.mongoTemplate.findOne(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(rid).and("RID").is(fid),
                        Criteria.where("RID").is(rid).and("FID").is(fid))),
                FriendEntity.class
        );
    }

    @Override
    public boolean unfriend(String rid, String fid) {
        WriteResult writeResult = this.mongoTemplate.updateFirst(
                query(new Criteria().orOperator(
                        Criteria.where("FID").is(rid).and("RID").is(fid).and("CON").is(true),
                        Criteria.where("RID").is(rid).and("FID").is(fid).and("CON").is(true))),
                entityUpdate(update("CON", false).set("UNF", rid)),
                FriendEntity.class
        );

        return writeResult.getN() > 0;
    }

    @Override
    public boolean inviteAgain(String id, String authKey) {
        WriteResult writeResult = this.mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("AUTH", authKey)),
                FriendEntity.class
        );

        return writeResult.getN() > 0;
    }
}
