package com.tholix.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
@Repository
@Transactional(readOnly = true)
public class MessageManagerImpl implements MessageManager {
    private static final Logger log = Logger.getLogger(MessageManagerImpl.class);

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
        Query query = Query.query(Criteria.where("recordLocked").is(false))
                .addCriteria(Criteria.where("receiptStatus").is(status));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "level"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "created"));
        Sort sort = new Sort(orders);

        query.with(sort).limit(limit);
        return mongoTemplate.find(query, MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String profileId, ReceiptStatusEnum status) {
        return findUpdateWithLimit(emailId, profileId, status, QUERY_LIMIT);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String profileId, ReceiptStatusEnum status, int limit) {
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
//                .append("receiptStatus", "OCR_PROCESSED");

        List<MessageReceiptEntityOCR> list = findWithLimit(status);
        for(MessageReceiptEntityOCR object : list) {
            object.setEmailId(emailId);
            object.setUserProfileId(profileId);
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
    public List<MessageReceiptEntityOCR> findPending(String emailId, String profileId, ReceiptStatusEnum status) {
        Query query = Query.query(Criteria.where("recordLocked").is(true))
                .addCriteria(Criteria.where("receiptStatus").is(status))
                .addCriteria(Criteria.where("emailId").is(emailId))
                .addCriteria(Criteria.where("userProfileId").is(profileId));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "level"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "created"));
        Sort sort = new Sort(orders);

        query.with(sort);
        return mongoTemplate.find(query, MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    public List<MessageReceiptEntityOCR> findAllPending() {
        Query query = Query.query(Criteria.where("recordLocked").is(true))
                .addCriteria(Criteria.where("receiptStatus").is(ReceiptStatusEnum.OCR_PROCESSED));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "level"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "created"));
        Sort sort = new Sort(orders);

        query.with(sort);
        return mongoTemplate.find(query, MessageReceiptEntityOCR.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(MessageReceiptEntityOCR object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        object.setUpdated(); //TODO why force the update date. Should it not be handled by the system just like versioning.
        mongoTemplate.save(object);
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
    public WriteResult updateObject(String id, ReceiptStatusEnum statusFind, ReceiptStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = Query.query(Criteria.where("recordLocked").is(true))
                .addCriteria(Criteria.where("receiptStatus").is(statusFind))
                .addCriteria(Criteria.where("idReceiptOCR").is(id));

        Update update = new Update()
                .set("receiptStatus", statusSet)
                .set("active", false);

        return mongoTemplate.updateFirst(query, update, MessageReceiptEntityOCR.class);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WriteResult undoUpdateObject(String id, boolean value, ReceiptStatusEnum statusFind, ReceiptStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = Query.query(Criteria.where("recordLocked").is(true))
                .addCriteria(Criteria.where("receiptStatus").is(statusFind))
                .addCriteria(Criteria.where("active").is(false))
                .addCriteria(Criteria.where("idReceiptOCR").is(id));

        Update update = new Update()
                .set("recordLocked", false)
                .set("active", true)
                .set("receiptStatus", statusSet);

        return mongoTemplate.updateFirst(query, update, MessageReceiptEntityOCR.class);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(MessageReceiptEntityOCR object) {
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
        throw new UnsupportedOperationException("Method not implemented");
    }
}
