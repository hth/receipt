package com.receiptofi.repository;


import com.mongodb.client.result.UpdateResult;
import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.types.NotificationMarkerEnum;
import com.receiptofi.domain.types.NotificationStateEnum;
import com.receiptofi.domain.types.PaginationEnum;
import com.receiptofi.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.receiptofi.domain.types.NotificationTypeEnum.*;
import static com.receiptofi.repository.util.AppendAdditionalFields.*;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

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

    private MongoTemplate mongoTemplate;

    @Autowired
    public NotificationManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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
                where("RID").is(rid)
                        .and("NM").ne(NotificationMarkerEnum.I)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
        ).skip(start).with(new Sort(Direction.DESC, "C"));
        if (limit != PaginationEnum.ALL.getLimit()) {
            query.limit(limit);
        }
        return mongoTemplate.find(query, NotificationEntity.class);
    }

    @Override
    public long notificationCount(String rid) {
        return mongoTemplate.count(
                query(where("RID").is(rid)
                                .and("NM").ne(NotificationMarkerEnum.I)
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ),
                NotificationEntity.class
        );
    }

    @Override
    public long deleteHardInactiveNotification(Date sinceDate) {
        return mongoTemplate.remove(
                query(where("A").is(false).and("C").lte(sinceDate)),
                NotificationEntity.class
        ).getDeletedCount();
    }

    @Override
    public long setNotificationInactive(Date sinceDate) {
        return mongoTemplate.updateMulti(
                query(where("C").lte(sinceDate)
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ),
                entityUpdate(update("A", false)),
                NotificationEntity.class
        ).getModifiedCount();
    }

    @Override
    public List<NotificationEntity> getAllPushNotifications(int notificationRetryCount) {
        return mongoTemplate.find(
                query(where("NM").is(NotificationMarkerEnum.P)
                        .orOperator(
                                where("C").lte(DateUtil.getDateMinusMinutes(PUSH_NOTIFICATION.delayNotifying)).and("NNE").is(PUSH_NOTIFICATION),
                                where("C").lte(DateUtil.getDateMinusMinutes(EXPENSE_REPORT.delayNotifying)).and("NNE").is(EXPENSE_REPORT),
                                where("C").lte(DateUtil.getDateMinusMinutes(DOCUMENT_REJECTED.delayNotifying)).and("NNE").is(DOCUMENT_REJECTED),
                                where("C").lte(DateUtil.getDateMinusMinutes(RECEIPT.delayNotifying)).and("NNE").is(RECEIPT)
                        )
                        .and("NS").is(NotificationStateEnum.F)
                        .and("CN").lt(notificationRetryCount)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(Direction.DESC, "C")),
                NotificationEntity.class);
    }

    @Override
    public void markNotificationRead(List<String> notificationIds, String rid) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
                query(where("RID").is(rid)
                        .orOperator(
                            where("MR").exists(false),
                            where("MR").is(false)
                        ).and("id").in(notificationIds)),
                update("MR", true).set("U", new Date()),
                NotificationEntity.class,
                TABLE
        );

        LOG.debug("Marked read notification actual={} expected={}", updateResult.getModifiedCount(), notificationIds.size());
    }
}
