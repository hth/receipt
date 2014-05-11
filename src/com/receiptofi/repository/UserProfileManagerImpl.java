/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.UserLevelEnum;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

/**
 * @author hitender
 * @since Dec 23, 2012 3:45:47 AM
 *
 */
@Repository
public final class UserProfileManagerImpl implements UserProfileManager {
	private static final long serialVersionUID = 7078530488197339683L;
	private static final Logger log = LoggerFactory.getLogger(UserProfileManagerImpl.class);

	private MongoTemplate mongoTemplate;

    @Autowired
    public UserProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

	@Override
	public List<UserProfileEntity> getAllObjects() {
		return mongoTemplate.findAll(UserProfileEntity.class, TABLE);
	}

	@Override
	public void save(UserProfileEntity object) {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
//            if(getObjectUsingEmail(object.getEmailId()) == null)
//			    mongoTemplate.save(object, TABLE);
//            else {
//                log.error("User seems to be already registered: " + object.getEmailId());
//                throw new Exception("User already registered with email: " + object.getEmailId());
//            }
            if(object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for UserProfileEntity={}", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object) {
		return mongoTemplate.findOne(query(where("USER_AUTHENTICATION.$id").is(new ObjectId(object.getId()))), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity getObjectUsingEmail(String emailId) {
		return mongoTemplate.findOne(query(where("EM").is(emailId).andOperator(isActive())), UserProfileEntity.class, TABLE);
	}

    @Override
    public UserProfileEntity getUsingId(String receiptUserId) {
        return mongoTemplate.findOne(query(where("RID").is(receiptUserId).andOperator(isActive())), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity getUsingUserId(String userId) {
        return mongoTemplate.findOne(query(where("UID").is(userId).andOperator(isActive())), UserProfileEntity.class, TABLE);
    }

	@Override
	public UserProfileEntity findOne(String id) {
		return mongoTemplate.findOne(query(where("id").is(id)), UserProfileEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// return mongoTemplate.updateFirst(
		// Query.query(Criteria.where("id").is(id)),
		// Update.update("level", name), TABLE);
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public WriteResult updateObject(String id, UserLevelEnum level) {
		return mongoTemplate.updateFirst(query(where("id").is(id)), entityUpdate(update("USER_LEVEL_ENUM", level)), UserProfileEntity.class);
	}

	@Override
    public void deleteHard(UserProfileEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
	public List<UserProfileEntity> searchAllByName(String name) {
		//TODO look into PageRequest for limit data
		//PageRequest request = new PageRequest(0, 1, new Sort("created", Directions.DESC));

        //Can add "^" + to force search only the names starting with
		Criteria a = where("FN").regex(name, "i");
		Criteria b = where("LN").regex(name, "i");
		return mongoTemplate.find(query(new Criteria().orOperator(a, b)), UserProfileEntity.class, TABLE);
	}

    @Override
    public UserProfileEntity findOneByEmail(String emailId) {
        return mongoTemplate.findOne(query(where("EM").is(emailId)), UserProfileEntity.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
