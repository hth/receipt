package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BrowserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 4:08 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class BrowserManagerImpl implements BrowserManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BrowserEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(BrowserEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public BrowserEntity getByCookie(String cookie) {
        Assert.hasText(cookie, "Cookie is empty");
        return mongoTemplate.findOne(query(where("CK").is(cookie)), BrowserEntity.class);
    }

    @Override
    public void deleteHard(BrowserEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
