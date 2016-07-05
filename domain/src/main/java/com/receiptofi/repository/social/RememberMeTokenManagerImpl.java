package com.receiptofi.repository.social;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.social.RememberMeTokenEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 3/30/14 7:38 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class RememberMeTokenManagerImpl implements RememberMeTokenManager {
    private static final Logger LOG = LoggerFactory.getLogger(RememberMeTokenManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            RememberMeTokenEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public RememberMeTokenEntity findBySeries(String series) {
        Criteria criteria = where("S").is(series);
        return mongoTemplate.findOne(Query.query(criteria), RememberMeTokenEntity.class, TABLE);
    }

    @Override
    public void deleteTokensWithUsername(String username) {
        mongoTemplate.remove(Query.query(where("UN").is(username)), TABLE);
    }

    @Override
    public void save(RememberMeTokenEntity rememberMeToken) {
        try {
            mongoTemplate.save(rememberMeToken);
        } catch (Exception e) {
            LOG.error("Failed saving rememberMeToken un={} reason={}", rememberMeToken.getUsername(), e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void deleteHard(RememberMeTokenEntity rememberMeToken) {
        mongoTemplate.remove(rememberMeToken);
    }
}
