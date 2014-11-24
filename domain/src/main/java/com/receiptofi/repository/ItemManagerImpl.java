/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author hitender
 * @since Dec 26, 2012 9:16:44 PM
 */
@Repository
public final class ItemManagerImpl implements ItemManager {
    private static final Logger LOG = LoggerFactory.getLogger(ItemManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ItemEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private BizNameManager bizNameManager;

    @Override
    public List<ItemEntity> getAllObjects() {
        return mongoTemplate.findAll(ItemEntity.class, TABLE);
    }

    @Override
    public void save(ItemEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for ItemEntity={}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void saveObjects(List<ItemEntity> objects) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            //TODO reflection error saving the list
            //mongoTemplate.insert(objects, TABLE);
            int sequence = 1;
            for (ItemEntity object : objects) {
                object.setSequence(sequence);
                save(object);
                sequence++;
            }
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for ItemEntity: " + e.getLocalizedMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * User findItem instead of findOne as this is not a secure call without user profile id
     *
     * @param id
     * @return
     */
    @Deprecated
    @Override
    public ItemEntity findOne(String id) {
        Sort sort = new Sort(Direction.ASC, "SEQ");
        return mongoTemplate.findOne(query(where("id").is(id)).with(sort), ItemEntity.class, TABLE);
    }

    /**
     * Use this method instead of findOne
     *
     * @param itemId
     * @param receiptUserId
     * @return
     */
    @Override
    public ItemEntity findItem(String itemId, String receiptUserId) {
        Query query = query(where("id").is(itemId).and("RID").is(receiptUserId));
        return mongoTemplate.findOne(query, ItemEntity.class, TABLE);
    }

    @Override
    public List<ItemEntity> getAllItemsOfReceipt(String receiptId) {
        Sort sort = new Sort(Direction.ASC, "SEQ");
        return mongoTemplate.find(
                query(where("RECEIPT.$id").is(new ObjectId(receiptId))).with(sort),
                ItemEntity.class, TABLE);
    }

    /**
     * This method in future could be very memory extensive when there would be tons of similar items. To fix it, add
     * receipt date to items
     * db.ITEM.find( {"name" : "509906212284 Podium Bottle 24 oz" , "created" : ISODate("2013-06-03T03:38:44.818Z")} )
     *
     * @param name         - Name of the item
     * @param untilThisDay - Show result from this day onwards
     * @return
     */
    @Override
    public List<ItemEntity> findAllByNameLimitByDays(String name, DateTime untilThisDay) {
        return findAllByNameLimitByDays(name, null, untilThisDay);
    }

    /**
     * This method in future could be very memory extensive when there would be tons of similar items. To fix it, add
     * receipt date to items
     * db.ITEM.find( {"name" : "509906212284 Podium Bottle 24 oz" , "created" : ISODate("2013-06-03T03:38:44.818Z")} )
     *
     * @param name         - Name of the item
     * @param untilThisDay - Show result from this day onwards
     * @return
     */
    @Override
    public List<ItemEntity> findAllByNameLimitByDays(String name, String receiptUserId, DateTime untilThisDay) {
        // Can choose Item create date but if needs accuracy then find receipts for these items and filter
        // receipts by date provided.
        // Not sure how much beneficial it would be other than more data crunching.
        Criteria criteriaA = where("IN").is(name);
        Query query = query(criteriaA);

        Criteria criteriaB;
        if (receiptUserId != null) {
            criteriaB = where("RID").is(receiptUserId);
            query = query(criteriaA.andOperator(criteriaB.andOperator(isNotDeleted())));
        }

        return mongoTemplate.find(query, ItemEntity.class, TABLE);
    }

    /**
     * This method in future could be very memory extensive when there would be tons of similar items. To fix it, add
     * receipt date to items
     *
     * @param itemEntity
     * @param receiptUserId
     * @return
     */
    @Override
    public List<ItemEntity> findAllByName(ItemEntity itemEntity, String receiptUserId) {
        if (itemEntity.getReceipt().getReceiptUserId().equals(receiptUserId)) {
            Criteria criteria = where("IN").is(itemEntity.getName())
                    .and("RID").is(receiptUserId)
                    .andOperator(
                            isNotDeleted()
                    );

            return mongoTemplate.find(query(criteria), ItemEntity.class, TABLE);
        } else {
            LOG.error("One of the query is trying to get items for different rid={} item={}",
                    receiptUserId, itemEntity.getId());
            return new LinkedList<>();
        }
    }

    @Override
    public void deleteHard(ItemEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public WriteResult updateObject(ItemEntity object) {
        Query query = query(where("id").is(object.getId()));
        Update update = Update.update("IN", object.getName());
        return mongoTemplate.updateFirst(query, entityUpdate(update), TABLE);
    }

    @Override
    public void deleteWhereReceipt(ReceiptEntity receipt) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        mongoTemplate.remove(query(where("RECEIPT.$id").is(new ObjectId(receipt.getId()))), ItemEntity.class);
    }

    @Override
    public void deleteSoft(ReceiptEntity receipt) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = query(where("RECEIPT.$id").is(new ObjectId(receipt.getId())));
        Update update = Update.update("D", true);
        mongoTemplate.updateMulti(query, entityUpdate(update), ItemEntity.class);
    }

    @Override
    public List<ItemEntity> findItems(String name, String bizName) {
        Criteria criteriaI = where("IN").regex(new StringTokenizer("^" + name).nextToken(), "i");
        Query query;

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if (null == bizNameEntity) {
            //query = Query.query(criteriaI);
            return new ArrayList<>();
        } else {
            Criteria criteriaB = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            query = query(criteriaI).addCriteria(criteriaB);
        }

        //This makes just one of the field populated
        query.fields().include("IN");
        return mongoTemplate.find(query, ItemEntity.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public void updateItemWithExpenseType(ItemEntity item) throws Exception {
        ItemEntity foundItem = findOne(item.getId());
        if (null == foundItem) {
            LOG.error("Could not update ExpenseType as no ItemEntity with Id was found: " + item.getId());
            throw new Exception("Could not update ExpenseType as no ItemEntity with Id was found: " + item.getId());
        } else {
            foundItem.setExpenseTag(item.getExpenseTag());
            save(foundItem);
        }
    }

    @Override
    public long countItemsUsingExpenseType(String expenseTypeId, String receiptUserId) {
        Criteria criteria = where("EXPENSE_TAG.$id").is(new ObjectId(expenseTypeId))
                .and("RID").is(receiptUserId);

        Query query = query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.count(query, ItemEntity.class);
    }

    /**
     * Example to fetch Entity based on DBRef
     * db.ITEM.find( {'expenseType.$id':  ObjectId('51a6d366036487b899cc31fc')} )
     *
     * @param expenseType
     * @return
     */
    @Override
    public List<ItemEntity> getItemEntitiesForSpecificExpenseTypeForTheYear(ExpenseTagEntity expenseType) {
        Criteria criteria = where("EXPENSE_TAG.$id").is(new ObjectId(expenseType.getId()))
                .and("RTX").gte(DateUtil.startOfYear());

        Query query = query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.find(query, ItemEntity.class);
    }

    @Override
    public List<ItemEntity> getItemEntitiesForUnAssignedExpenseTypeForTheYear(String receiptUserId) {
        Criteria criteria = where("EXPENSE_TAG").is(StringUtils.trimToNull(null))
                .and("RID").is(receiptUserId)
                .and("RTX").gte(DateUtil.startOfYear());

        return mongoTemplate.find(
                query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted()),
                ItemEntity.class);
    }
}
