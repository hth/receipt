package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.RegisteredDeviceEntity;
import com.receiptofi.domain.types.DeviceTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 8/12/14 10:11 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class RegisteredDeviceManagerImpl implements RegisteredDeviceManager {
    private static final Logger LOG = LoggerFactory.getLogger(RegisteredDeviceManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            RegisteredDeviceEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Value ("${device.lastAccessed.now}")
    private String deviceLastAccessedNow;

    @Autowired
    public RegisteredDeviceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(RegisteredDeviceEntity object) {
        mongoTemplate.save(object);
    }

    @Override
    public RegisteredDeviceEntity find(String rid, String did) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("DID").is(did)),
                RegisteredDeviceEntity.class,
                TABLE);
    }

    @Override
    public RegisteredDeviceEntity registerDevice(String rid, String did, DeviceTypeEnum deviceType, String token) {
        RegisteredDeviceEntity newRegisteredDevice = RegisteredDeviceEntity.newInstance(rid, did, deviceType, token);
        RegisteredDeviceEntity registeredDevice = find(rid, did);

        if (null == registeredDevice) {
            save(newRegisteredDevice);
            LOG.info("registered device for rid={} did={}", rid, did);
        } else if (StringUtils.isNotBlank(token)) {
            registeredDevice.setDeviceType(deviceType);
            registeredDevice.setToken(token);
            save(newRegisteredDevice);
            LOG.info("updated registered device for rid={} did={} token={}", rid, did, token);
        }
        return newRegisteredDevice;
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
                update("U", "ON".equals(deviceLastAccessedNow) ? new Date() : DateTime.now().minusYears(1).toDate()),
                RegisteredDeviceEntity.class,
                TABLE
        );
    }

    /**
     * Gets all the registered device ids for rid.
     *
     * @param rid
     * @return
     */
    public List<RegisteredDeviceEntity> getDevicesForRid(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)),
                RegisteredDeviceEntity.class,
                TABLE);
    }

    @Override
    public void deleteHard(RegisteredDeviceEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public void deleteHard(String rid, String token) {
        mongoTemplate.remove(
                query(where("RID").is(rid).and("TK").is(token)),
                RegisteredDeviceEntity.class,
                TABLE);
    }

    @Override
    public void increaseCountOnInactiveDevice(String rid, String token) {
        mongoTemplate.updateMulti(
                query(where("RID").is(rid).and("TK").is(token)),
                update("U", new Date()).inc("CN", 1),
                RegisteredDeviceEntity.class,
                TABLE);
    }

    @Override
    public void resetCountOnInactiveDevice(String rid, String token) {
        mongoTemplate.updateMulti(
                query(where("RID").is(rid).and("TK").is(token)),
                update("CN", 0).set("U", new Date()),
                RegisteredDeviceEntity.class,
                TABLE);
    }
}
