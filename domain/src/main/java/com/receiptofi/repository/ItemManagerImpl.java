/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.DBRef;
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
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author hitender
 * @since Dec 26, 2012 9:16:44 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class ItemManagerImpl implements ItemManager {
    private static final Logger LOG = LoggerFactory.getLogger(ItemManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ItemEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;
    private BizNameManager bizNameManager;

    @Autowired
    public ItemManagerImpl(BizNameManager bizNameManager, MongoTemplate mongoTemplate) {
        this.bizNameManager = bizNameManager;
        this.mongoTemplate = mongoTemplate;
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
    public void saveObjects(List<ItemEntity> objects) {
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
            LOG.error("Duplicate record entry for ItemEntity reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemEntity findItem(String itemId, String rid) {
        return mongoTemplate.findOne(query(where("id").is(itemId).and("RID").is(rid)), ItemEntity.class, TABLE);
    }

    @Override
    public List<ItemEntity> getAllItemsOfReceipt(String receiptId) {
        return mongoTemplate.find(
                query(where("RECEIPT.$id").is(new ObjectId(receiptId))).with(new Sort(Direction.ASC, "SEQ")),
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
        Query query = query(where("IN").is(name));

        if (null != receiptUserId) {
            query.addCriteria(where("RID").is(receiptUserId).andOperator(isNotDeleted()));
        }

        return mongoTemplate.find(query, ItemEntity.class, TABLE);
    }

    /**
     * This method in future could be very memory extensive when there would be tons of similar items. To fix it, add
     * receipt date to items.
     * Note: Added limit to reduce number of items fetched.
     *
     * @param item
     * @param rid
     * @param limit         - Number of items per query
     * @return
     */
    @Override
    public List<ItemEntity> findAllByName(ItemEntity item, String rid, int limit) {
        List<ItemEntity> items;
        if (item.getReceipt().getReceiptUserId().equals(rid)) {
            items = mongoTemplate.find(
                    queryToFindByName(item.getName(), rid).limit(limit),
                    ItemEntity.class,
                    TABLE);
        } else {
            LOG.error("Found different rid={} item={}", rid, item.getId());
            items = new ArrayList<>();
        }
        return items;
    }

    public long findAllByNameCount(ItemEntity item, String rid) {
        long count = 0;
        if (item.getReceipt().getReceiptUserId().equals(rid)) {
            count = mongoTemplate.count(queryToFindByName(item.getName(), rid), ItemEntity.class, TABLE);
        } else {
            LOG.error("Found different rid={} item={}", rid, item.getId());
        }
        return count;
    }

    private Query queryToFindByName(String itemName, String rid) {
        return query(where("IN").is(itemName)
                        .and("RID").is(rid)
                        .andOperator(
                                isNotDeleted()
                        )
        );
    }

    @Override
    public void deleteHard(ItemEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public WriteResult updateObject(ItemEntity object) {
        return mongoTemplate.updateFirst(
                query(where("id").is(object.getId())),
                entityUpdate(update("IN", object.getName())),
                TABLE);
    }

    @Override
    public void deleteWhereReceipt(ReceiptEntity receipt) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        mongoTemplate.remove(query(where("RECEIPT.$id").is(new ObjectId(receipt.getId()))), ItemEntity.class);
    }

    @Override
    public void deleteSoft(ReceiptEntity receipt) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        mongoTemplate.updateMulti(
                query(where("RECEIPT.$id").is(new ObjectId(receipt.getId()))),
                entityUpdate(update("D", true)),
                ItemEntity.class
        );
    }

    @Override
    public List<ItemEntity> findItems(String name, String bizName) {
        Criteria criteriaI = where("IN").regex(
                name.length() > 4 ? (new StringTokenizer("^" + name).nextToken()) : "^" + name,
                "i");

        Query query;
        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if (null == bizNameEntity) {
            //query = Query.query(criteriaI);
            return new ArrayList<>();
        } else {
            Criteria criteriaB = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            query = query(criteriaI).addCriteria(criteriaB);
        }

        /** This makes just one of the field populated. */
        query.fields().include("IN");
        return mongoTemplate.find(query, ItemEntity.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public void updateAllItemWithExpenseTag(String receiptId, String expenseTagId) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        mongoTemplate.updateMulti(
                query(where("RECEIPT.$id").is(new ObjectId(receiptId))),
                update("EXPENSE_TAG", new DBRef(ExpenseTagManagerImpl.TABLE, new ObjectId(expenseTagId))),
                ItemEntity.class
        );
    }

    @Override
    public void updateItemWithExpenseTag(String itemId, String expenseTagId) {
        mongoTemplate.updateFirst(
                query(where("id").is(itemId)),
                update("EXPENSE_TAG", new DBRef(ExpenseTagManagerImpl.TABLE, new ObjectId(expenseTagId))),
                ItemEntity.class
        );
    }

    @Override
    public long countItemsUsingExpenseType(String expenseTypeId, String rid) {
        return mongoTemplate.count(
                query(where("EXPENSE_TAG.$id").is(new ObjectId(expenseTypeId))
                        .and("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ItemEntity.class
        );
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
        return mongoTemplate.find(
                query(where("EXPENSE_TAG.$id").is(new ObjectId(expenseType.getId()))
                                .and("RTX").gte(DateUtil.startOfYear())
                                .andOperator(
                                        isActive(),
                                        isNotDeleted()
                                )
                ),
                ItemEntity.class
        );
    }

    @Override
    public List<ItemEntity> getItemEntitiesForUnAssignedExpenseTypeForTheYear(String rid) {
        return mongoTemplate.find(
                query(where("EXPENSE_TAG").is(StringUtils.trimToNull(null))
                        .and("RID").is(rid)
                        .and("RTX").gte(DateUtil.startOfYear())
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ItemEntity.class
        );
    }

    @Override
    public boolean removeExpenseTagReferences(String rid, String expenseTagId) {
        return mongoTemplate.updateMulti(
                query(where("RID").is(rid).and("EXPENSE_TAG.$id").is(new ObjectId(expenseTagId))),
                entityUpdate(new Update().unset("EXPENSE_TAG")),
                TABLE
        ).getN() > 0;
    }
}
