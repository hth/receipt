package com.receiptofi.repository.social;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.social.RememberMeTokenEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
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

    private final MongoTemplate mongoTemplate;

    @Autowired
    public RememberMeTokenManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public RememberMeTokenEntity findBySeries(String series) {
        return mongoTemplate.findOne(query(where("S").is(series)), RememberMeTokenEntity.class, TABLE);
    }

    @Override
    public void deleteTokensWithUsername(String username) {
        mongoTemplate.remove(query(where("UN").is(username)), RememberMeTokenEntity.class, TABLE);
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
    public void updateToken(String tokenId, String tokenValue) {
        mongoTemplate.updateFirst(
                query(where("id").is(tokenId)),
                entityUpdate(update("TV", tokenValue)),
                RememberMeTokenEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(RememberMeTokenEntity rememberMeToken) {
        mongoTemplate.remove(rememberMeToken);
    }
}
