package com.receiptofi.repository;

import com.receiptofi.domain.MessageReceiptEntityOCR;
import com.receiptofi.domain.types.ReceiptStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
@Repository
@Transactional(readOnly = true)
public final class MessageManagerImpl implements MessageManager {
    private static final Logger log = LoggerFactory.getLogger(MessageManagerImpl.class);

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<MessageReceiptEntityOCR> getAllObjects() {
        return mongoTemplate.findAll(MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    public List<MessageReceiptEntityOCR> findWithLimit(ReceiptStatusEnum status) {
        return findWithLimit(status, QUERY_LIMIT);
    }

    @Override
    public List<MessageReceiptEntityOCR> findWithLimit(ReceiptStatusEnum status, int limit) {
        Query query = Query.query(Criteria.where("LOCKED").is(false))
                .addCriteria(Criteria.where("RECEIPT_STATUS_ENUM").is(status));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "USER_LEVEL_ENUM"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "CREATE"));
        Sort sort = new Sort(orders);

        query.with(sort).limit(limit);
        return mongoTemplate.find(query, MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String userProfileId, ReceiptStatusEnum status) {
        return findUpdateWithLimit(emailId, userProfileId, status, QUERY_LIMIT);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String userProfileId, ReceiptStatusEnum status, int limit) {
//        String updateQuery = "{ " +
//                "set : " +
//                    "{" +
//                    "'emailId' : '" + emailId + "', " +
//                    "'profileId' : '" + profileId + "', " +
//                    "'recordLocked' : " + true +
//                    "} " +
//                "}";
//
//        String sortQuery  = "{ sort : { 'level' : " + -1 + ", 'created' : " + 1 + "} }";
//        String limitQuery = "{ limit : " + QUERY_LIMIT + "}";

//        BasicDBObject basicDBObject = new BasicDBObject()
//                .append("recordLocked", false)
//                .append("RECEIPT_STATUS_ENUM", "OCR_PROCESSED");

        List<MessageReceiptEntityOCR> list = findWithLimit(status);
        for(MessageReceiptEntityOCR object : list) {
            object.setEmailId(emailId);
            object.setUserProfileId(userProfileId);
            object.setRecordLocked(true);
            try {
                save(object);
            } catch (Exception e) {
                object.setRecordLocked(false);
                object.setUserProfileId("");
                object.setEmailId("");
                try {
                    save(object);
                } catch (Exception e1) {
                    log.error("Update failed: " + object.toString());
                }
            }
        }

        return list;
    }

    @Override
    public List<MessageReceiptEntityOCR> findPending(String emailId, String userProfileId, ReceiptStatusEnum status) {
        Query query = Query.query(Criteria.where("LOCKED").is(true))
                .addCriteria(Criteria.where("RECEIPT_STATUS_ENUM").is(status))
                .addCriteria(Criteria.where("EMAIL").is(emailId))
                .addCriteria(Criteria.where("USER_PROFILE_ID").is(userProfileId));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "USER_LEVEL_ENUM"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "CREATE"));
        Sort sort = new Sort(orders);

        query.with(sort);
        return mongoTemplate.find(query, MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    public List<MessageReceiptEntityOCR> findAllPending() {
        Query query = Query.query(Criteria.where("LOCKED").is(true))
                .addCriteria(Criteria.where("RECEIPT_STATUS_ENUM").is(ReceiptStatusEnum.OCR_PROCESSED));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "USER_LEVEL_ENUM"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "CREATE"));
        Sort sort = new Sort(orders);

        query.with(sort);
        return mongoTemplate.find(query, MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(MessageReceiptEntityOCR object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if(object.getId() != null) {
            object.setUpdated(); //TODO why force the update date. Should it not be handled by the system just like versioning.
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public MessageReceiptEntityOCR findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WriteResult updateObject(String receiptOCRId, ReceiptStatusEnum statusFind, ReceiptStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = Query.query(Criteria.where("LOCKED").is(true))
                .addCriteria(Criteria.where("RECEIPT_STATUS_ENUM").is(statusFind))
                .addCriteria(Criteria.where("RECEIPT_OCR_ID").is(receiptOCRId));

        Update update = Update.update("RECEIPT_STATUS_ENUM", statusSet).set("ACTIVE", false);

        return mongoTemplate.updateFirst(query, update(update), MessageReceiptEntityOCR.class);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WriteResult undoUpdateObject(String receiptOCRId, boolean value, ReceiptStatusEnum statusFind, ReceiptStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = Query.query(Criteria.where("LOCKED").is(true))
                .addCriteria(Criteria.where("RECEIPT_STATUS_ENUM").is(statusFind))
                .addCriteria(Criteria.where("ACTIVE").is(false))
                .addCriteria(Criteria.where("RECEIPT_OCR_ID").is(receiptOCRId));

        Update update = Update.update("recordLocked", false)
                .set("ACTIVE", true)
                .set("RECEIPT_STATUS_ENUM", statusSet);

        return mongoTemplate.updateFirst(query, update(update), MessageReceiptEntityOCR.class);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteHard(MessageReceiptEntityOCR object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void deleteAllForReceiptOCR(String receiptOCRId) {
        Query query = Query.query(Criteria.where("RECEIPT_OCR_ID").is(receiptOCRId));
        mongoTemplate.remove(query, MessageReceiptEntityOCR.class);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void dropCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
