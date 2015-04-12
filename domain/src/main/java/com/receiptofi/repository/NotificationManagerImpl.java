package com.receiptofi.repository;


import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.types.PaginationEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class NotificationManagerImpl implements NotificationManager {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            NotificationEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(NotificationEntity object) {
        if (null != object.getId()) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(NotificationEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<NotificationEntity> getNotifications(String rid, int start, int limit) {
        Query query = query(
                where("RID").is(rid).and("ND").is(true))
                .addCriteria(isNotDeleted())
                .skip(start)
                .with(new Sort(Sort.Direction.DESC, "C"));

        if (limit != PaginationEnum.ALL.getLimit()) {
            query.limit(limit);
        }
        return mongoTemplate.find(query, NotificationEntity.class);
    }

    @Override
    public long notificationCount(String rid) {
        return mongoTemplate.count(
                query(where("RID").is(rid)
                        .and("ND").is(true))
                        .addCriteria(isNotDeleted()),
                NotificationEntity.class
        );
    }

    @Override
    public int deleteHardInactiveNotification(Date sinceDate) {
        return mongoTemplate.remove(
                query(where("A").is(false).and("C").lte(sinceDate)),
                        NotificationEntity.class
        ).getN();
    }

    @Override
    public int setNotificationInactive(Date sinceDate) {
        return mongoTemplate.updateMulti(
                query(where("C").lte(sinceDate)
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ),
                entityUpdate(update("A", false)),
                NotificationEntity.class
        ).getN();
    }
}
