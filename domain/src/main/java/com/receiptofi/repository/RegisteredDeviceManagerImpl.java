package com.receiptofi.repository;

import java.util.Date;
import java.util.List;

import com.receiptofi.domain.RegisteredDeviceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import org.joda.time.DateTime;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * User: hitender
 * Date: 8/12/14 10:11 PM
 */
@Repository
public class RegisteredDeviceManagerImpl implements RegisteredDeviceManager {
    private static final Logger LOG = LoggerFactory.getLogger(RegisteredDeviceManagerImpl.class);
    private MongoTemplate mongoTemplate;

    @Value ("${production.switch}")
    private String productionSwitch;

    @Autowired
    public RegisteredDeviceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<RegisteredDeviceEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(RegisteredDeviceEntity object) {
        mongoTemplate.save(object);
    }

    private RegisteredDeviceEntity find(String rid, String did) {
        return mongoTemplate.findOne(query(where("RID").is(rid).and("DID").is(did)), RegisteredDeviceEntity.class);
    }

    @Override
    public RegisteredDeviceEntity registerDevice(String rid, String did) {
        RegisteredDeviceEntity registeredDevice = RegisteredDeviceEntity.newInstance(rid, did);
        if (find(rid, did) == null) {
            save(registeredDevice);
            LOG.info("registered device for rid={} did={}", rid, did);
        }
        return registeredDevice;
    }

    /**
     * Returns old document with old date when last accessed. And updates with new date
     *
     * @param rid
     * @param did
     * @return
     */
    @Override
    public RegisteredDeviceEntity lastAccessed(String rid, String did) {
        return mongoTemplate.findAndModify(
                query(where("RID").is(rid).and("DID").is(did)),
                update("U", productionSwitch.equals("ON") ? new Date() : DateTime.now().minusYears(1).toDate()),
                RegisteredDeviceEntity.class
        );
    }

    @Override
    public RegisteredDeviceEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(RegisteredDeviceEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public long collectionSize() {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
