/**
 *
 */
package com.receiptofi.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.receiptofi.domain.ItemFeatureEntity;

/**
 * @author hitender
 * @since Dec 26, 2012 9:21:35 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public final class ItemFeatureManagerImpl implements ItemFeatureManager {
	private static final long serialVersionUID = -2211419786590573846L;

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<ItemFeatureEntity> getAllObjects() {
		return mongoTemplate.findAll(ItemFeatureEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(ItemFeatureEntity object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if(object.getId() != null) {
            object.setUpdated();
        }
		mongoTemplate.save(object, TABLE);
	}

	@Override
	public ItemFeatureEntity findOne(String id) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(ItemFeatureEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
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
