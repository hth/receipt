package com.receiptofi.repository;

import com.receiptofi.domain.MileageEntity;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.joda.time.DateTime;

import com.mongodb.WriteResult;

/**
 * User: hitender
 * Date: 12/25/13 4:16 AM
 */
public class MileageManagerImpl implements MileageManager {

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<MileageEntity> getAllObjects() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(MileageEntity object) throws Exception {
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public MileageEntity findOne(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), MileageEntity.class, TABLE);
    }

    @Override
    public MileageEntity findOne(String id, String userProfileId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)).addCriteria(Criteria.where("USER_PROFILE_ID").is(userProfileId)), MileageEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteHard(MileageEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public void createCollection() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dropCollection() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long collectionSize() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MileageEntity> getMileageForThisMonth(String userProfileId, DateTime monthYear) {
        Criteria criteria = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Criteria criteria1 = Criteria.where("C").gte(monthYear.dayOfMonth().withMinimumValue().toDate()).lt(monthYear.plusMonths(1).dayOfMonth().withMinimumValue().toDate());

        Sort sort = new Sort(Sort.Direction.DESC, "S");
        Query query = Query.query(criteria).addCriteria(criteria1).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.find(query.with(sort), MileageEntity.class, TABLE);
    }
}
