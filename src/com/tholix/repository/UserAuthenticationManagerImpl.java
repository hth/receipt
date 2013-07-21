/**
 *
 */
package com.tholix.repository;

import java.util.List;

import static com.tholix.repository.util.AppendAdditionalFields.update;

import org.apache.log4j.Logger;

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

import com.tholix.domain.UserAuthenticationEntity;

/**
 * @author hitender
 * @since Dec 16, 2012 1:20:53 PM
 */
@Repository
@Transactional(readOnly = true)
public final class UserAuthenticationManagerImpl implements UserAuthenticationManager {
	private static final Logger log = Logger.getLogger(UserAuthenticationManagerImpl.class);

	private static final long serialVersionUID = 5745317401200234475L;

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<UserAuthenticationEntity> getAllObjects() {
		return mongoTemplate.findAll(UserAuthenticationEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(UserAuthenticationEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for UserAuthenticationEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public UserAuthenticationEntity findOne(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), UserAuthenticationEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		return mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update(Update.update("NAME", name)), TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(UserAuthenticationEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createCollection() {
		if (!mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.createCollection(TABLE);
		}

	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}
	}

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
