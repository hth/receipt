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

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

/**
 * @author hitender
 * @since Dec 23, 2012 3:45:47 AM
 *
 */
@Repository
@Transactional(readOnly = true)
public final class UserProfileManagerImpl implements UserProfileManager {
	private static final long serialVersionUID = 7078530488197339683L;
	private static final Logger log = LoggerFactory.getLogger(UserProfileManagerImpl.class);

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<UserProfileEntity> getAllObjects() {
		return mongoTemplate.findAll(UserProfileEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(UserProfileEntity object) throws Exception {
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
			log.error("Duplicate record entry for UserProfileEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object) {
		return mongoTemplate.findOne(Query.query(Criteria.where("USER_AUTHENTICATION.$id").is(new ObjectId(object.getId()))), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity getObjectUsingEmail(String emailId) {
		return mongoTemplate.findOne(Query.query(Criteria.where("EMAIL").is(emailId).andOperator(isActive())), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity findOne(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), UserProfileEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		// return mongoTemplate.updateFirst(
		// Query.query(Criteria.where("id").is(id)),
		// Update.update("level", name), TABLE);
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, UserLevelEnum level) {
		return mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update(Update.update("USER_LEVEL_ENUM", level)), UserProfileEntity.class);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteHard(UserProfileEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
	public List<UserProfileEntity> searchAllByName(String name) {
		//TODO look into PageRequest for limit data
		//PageRequest request = new PageRequest(0, 1, new Sort("created", Directions.DESC));

        //Can add "^" + to force search only the names starting with
		Criteria a = Criteria.where("FIRST_NAME").regex(name, "i");
		Criteria b = Criteria.where("LAST_NAME").regex(name, "i");
		return mongoTemplate.find(Query.query(new Criteria().orOperator(a, b)), UserProfileEntity.class, TABLE);
	}

    @Override
    public UserProfileEntity findOneByEmail(String emailId) {
        Criteria a = Criteria.where("EMAIL").is(emailId);
        return mongoTemplate.findOne(Query.query(a), UserProfileEntity.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
