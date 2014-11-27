package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CommentEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 7:13 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class CommentManagerImpl implements CommentManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            CommentEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<CommentEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Note: comment should not be marked updated with new time as its already updated by Ajax
     *
     * @param object
     * @throws Exception
     */
    @Override
    public void save(CommentEntity object) {
        //Note: comment should not be marked updated with new time as its already updated by Ajax
//        if(object.getId() != null) {
//            object.setUpdated();
//        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public CommentEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), CommentEntity.class);
    }

    @Override
    public void deleteHard(CommentEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
