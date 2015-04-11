package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CommentEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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

    /**
     * Note: comment should not be marked updated with new date time as its already updated by Ajax.
     *
     * @param object
     * @throws Exception
     */
    @Override
    public void save(CommentEntity object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public CommentEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id)), CommentEntity.class);
    }

    @Override
    public void deleteHard(CommentEntity object) {
        mongoTemplate.remove(object);
    }
}
