/**
 *
 */
package com.tholix.repository;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static com.tholix.repository.util.AppendAdditionalFields.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.joda.time.DateTime;

import com.mongodb.WriteResult;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @since Dec 26, 2012 9:16:44 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public final class ItemManagerImpl implements ItemManager {
	private static final Logger log = Logger.getLogger(ItemManagerImpl.class);

	private static final long serialVersionUID = 5734660649481504610L;

	@Autowired private MongoTemplate mongoTemplate;
    @Autowired private BizNameManager bizNameManager;

	@Override
	public List<ItemEntity> getAllObjects() {
		return mongoTemplate.findAll(ItemEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(ItemEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
            if(object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveObjects(List<ItemEntity> objects) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			//TODO reflection error saving the list
			//mongoTemplate.insert(objects, TABLE);
            int sequence = 1;
			for(ItemEntity object : objects) {
                object.setSequence(sequence);
				save(object);
                sequence ++;
			}
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ItemEntity: " + e.getLocalizedMessage());
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
		Sort sort = new Sort(Direction.ASC, "SEQUENCE");
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)).with(sort), ItemEntity.class, TABLE);
	}

    /**
     * Use this method instead of findOne
     *
     * @param itemId
     * @param userProfileId
     * @return
     */
    @Override
    public ItemEntity findItem(String itemId, String userProfileId) {
        Query query = Query.query(Criteria.where("id").is(itemId).andOperator(Criteria.where("USER_PROFILE_ID").is(userProfileId)));
        return mongoTemplate.findOne(query, ItemEntity.class, TABLE);
    }

	@Override
	public List<ItemEntity> getWhereReceipt(ReceiptEntity receipt) {
		Sort sort = new Sort(Direction.ASC, "SEQUENCE");
		return mongoTemplate.find(Query.query(Criteria.where("RECEIPT.$id").is(new ObjectId(receipt.getId()))).with(sort), ItemEntity.class, TABLE);
	}

    /**
     * This method in future could be very memory extensive when there would be tons of similar items. To fix it, add
     * receipt date to items
     *
     * db.ITEM.find( {"name" : "509906212284 Podium Bottle 24 oz" , "created" : ISODate("2013-06-03T03:38:44.818Z")} )
     *
     * @param name - Name of the item
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
     *
     * db.ITEM.find( {"name" : "509906212284 Podium Bottle 24 oz" , "created" : ISODate("2013-06-03T03:38:44.818Z")} )
     *
     * @param name - Name of the item
     * @param untilThisDay - Show result from this day onwards
     * @return
     */
    @Override
    public List<ItemEntity> findAllByNameLimitByDays(String name, String userProfileId, DateTime untilThisDay) {
        // Can choose Item create date but if needs accuracy then find receipts for these items and filter receipts by date provided.
        // Not sure how much beneficial it would be other than more data crunching.
        Criteria criteriaA = Criteria.where("NAME").is(name);
        Query query = Query.query(criteriaA);

        Criteria criteriaB;
        if(userProfileId != null) {
            criteriaB = Criteria.where("USER_PROFILE_ID").is(userProfileId);
            query = Query.query(criteriaA.andOperator(criteriaB.andOperator(isNotDeleted())));
        }

        return mongoTemplate.find(query, ItemEntity.class, TABLE);
    }

    /**
     * This method in future could be very memory extensive when there would be tons of similar items. To fix it, add
     * receipt date to items
     *
     * @param itemEntity
     * @param userProfileId
     * @return
     */
    @Override
    public List<ItemEntity> findAllByName(ItemEntity itemEntity, String userProfileId) {
        if(itemEntity.getReceipt().getUserProfileId().equals(userProfileId)) {
            Criteria criteriaA = Criteria.where("NAME").is(itemEntity.getName());
            Criteria criteriaB = Criteria.where("USER_PROFILE_ID").is(userProfileId);

            Query query = Query.query(criteriaA.andOperator(criteriaB.andOperator(isNotDeleted())));
            return mongoTemplate.find(query, ItemEntity.class, TABLE);
        } else {
            log.error("One of the query is trying to get items for different User Profile Id: " + userProfileId + ", Item Id: " + itemEntity.getId());
            return new LinkedList<>();
        }
    }

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(ItemEntity object) {
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(ItemEntity object) {
		Query query = Query.query(Criteria.where("id").is(object.getId()));
		Update update = Update.update("NAME", object.getName());
		return mongoTemplate.updateFirst(query, update(update), TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteWhereReceipt(ReceiptEntity receipt) {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		mongoTemplate.remove(Query.query(Criteria.where("RECEIPT.$id").is(new ObjectId(receipt.getId()))), ItemEntity.class);
	}

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteSoft(ReceiptEntity receipt) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = Query.query(Criteria.where("RECEIPT.$id").is(new ObjectId(receipt.getId())));
        Update update = Update.update("DELETE", true);
        mongoTemplate.updateMulti(query, update(update), ItemEntity.class);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<ItemEntity> findItems(String name, String bizName) {
        Criteria criteriaI = Criteria.where("NAME").regex(new StringTokenizer("^" + name).nextToken(), "i");
        Query query;

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if(bizNameEntity == null) {
            //query = Query.query(criteriaI);
            return new ArrayList<>();
        } else {
            Criteria criteriaB = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            query = Query.query(criteriaI).addCriteria(criteriaB);
        }

        //This makes just one of the field populated
        query.fields().include("NAME");
        return mongoTemplate.find(query, ItemEntity.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public void updateItemWithExpenseType(ItemEntity item) throws Exception {
        ItemEntity foundItem = findOne(item.getId());
        if(foundItem != null) {
            foundItem.setExpenseType(item.getExpenseType());
            save(foundItem);
        } else {
            log.error("Could not update ExpenseType as no ItemEntity with Id was found: " + item.getId());
            throw new Exception("Could not update ExpenseType as no ItemEntity with Id was found: " + item.getId());
        }
    }

    @Override
    public long countItemsUsingExpenseType(String expenseTypeId) {
        Criteria criteria = Criteria.where("EXPENSE_TYPE.$id").is(new ObjectId(expenseTypeId));
        Query query = Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.count(query, ItemEntity.class);
    }

    /**
     * Example to fetch Entity based on DBRef
     *      db.ITEM.find( {'expenseType.$id':  ObjectId('51a6d366036487b899cc31fc')} )
     *
     * @param expenseType
     * @return
     */
    @Override
    public List<ItemEntity> getItemEntitiesForSpecificExpenseType(ExpenseTypeEntity expenseType) {
        Criteria criteria = Criteria.where("EXPENSE_TYPE.$id").is(new ObjectId(expenseType.getId()));
        Query query = Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.find(query, ItemEntity.class);
    }

    @Override
    public List<ItemEntity> getItemEntitiesForUnAssignedExpenseType(String userProfileId) {
        Criteria criteria = Criteria.where("EXPENSE_TYPE").is(StringUtils.trimToNull(null)).and("USER_PROFILE_ID").is(userProfileId);
        return mongoTemplate.find(Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted()), ItemEntity.class);
    }
}
