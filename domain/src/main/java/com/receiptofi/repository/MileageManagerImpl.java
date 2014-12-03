package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.MileageEntity;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 12/25/13 4:16 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MileageManagerImpl implements MileageManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MileageEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(MileageEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public MileageEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), MileageEntity.class, TABLE);
    }

    @Override
    public MileageEntity findOne(String id, String receiptUserId) {
        return mongoTemplate.findOne(query(where("id").is(id)).addCriteria(where("RID").is(receiptUserId)), MileageEntity.class, TABLE);
    }

    @Override
    public void deleteHard(MileageEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public List<MileageEntity> getMileageForThisMonth(String receiptUserId, DateTime startMonth, DateTime endMonth) {
        Criteria criteria = where("RID").is(receiptUserId).and("C").gte(startMonth.toDate()).lt(endMonth.toDate());

        Sort sort = new Sort(Sort.Direction.DESC, "S");
        Query query = query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.find(query.with(sort), MileageEntity.class, TABLE);
    }

    @Override
    public boolean updateStartDate(String mileageId, DateTime startDate, String receiptUserId) {
        return updateDateInRecord(mileageId, "SD", startDate, receiptUserId).getLastError().ok();
    }

    @Override
    public boolean updateEndDate(String mileageId, DateTime endDate, String receiptUserId) {
        return updateDateInRecord(mileageId, "ED", endDate, receiptUserId).getLastError().ok();
    }

    private WriteResult updateDateInRecord(String mileageId, String fieldName, DateTime dateTime, String receiptUserId) {
        return mongoTemplate.updateFirst(
                query(where("id").is(mileageId).and("RID").is(receiptUserId)),
                update(fieldName, dateTime.toDate()),
                MileageEntity.class
        );
    }
}
