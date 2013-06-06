/**
 *
 */
package com.tholix.repository;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

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
import org.joda.time.format.ISODateTimeFormat;

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
public class ItemManagerImpl implements ItemManager {
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
			object.setUpdated();
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

	@Override
	public ItemEntity findOne(String id) {
		Sort sort = new Sort(Direction.ASC, "sequence");
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)).with(sort), ItemEntity.class, TABLE);
	}

	@Override
	public List<ItemEntity> getWhereReceipt(ReceiptEntity receipt) {
		Sort sort = new Sort(Direction.ASC, "sequence");
		return mongoTemplate.find(Query.query(Criteria.where("receipt").is(receipt)).with(sort), ItemEntity.class, TABLE);
	}

    /**
     * db.ITEM.find( {"name" : "509906212284 Podium Bottle 24 oz" , "created" : ISODate("2013-06-03T03:38:44.818Z")} )
     *
     * @param name - Name of the item
     * @param untilThisDay - Show result from this day onwards
     * @return
     */
	@Override
	public List<ItemEntity> findAllByNameLimitByDays(String name, DateTime untilThisDay) {
        // Can choose Item create date but if needs accuracy then find receipts for these items and filter receipts by date provided.
        // Not sure how much beneficial it would be other than more data crunching.

        Criteria criteriaA = Criteria.where("name").is(name);
        Criteria criteriaB = Criteria.where("created").gte(ISODateTimeFormat.dateTime().print(untilThisDay));
        Sort sort = new Sort(Direction.DESC, "created");
        Query query = Query.query(criteriaA).addCriteria(criteriaB).with(sort);

		return mongoTemplate.find(query, ItemEntity.class, TABLE);
	}

    @Override
    public List<ItemEntity> findAllByName(ItemEntity itemEntity, String userProfileId) {
        if(itemEntity.getReceipt().getUserProfileId().equals(userProfileId)) {
            Criteria criteriaA = Criteria.where("name").is(itemEntity.getName());
            //Criteria criteriaB = Criteria.where("receipt.userProfileId").is(itemEntity.getReceipt().getUserProfileId());

            Sort sort = new Sort(Direction.DESC, "created");
            Query query = Query.query(criteriaA).with(sort);

            List<ItemEntity> orderedList = new LinkedList<>();
            for (ItemEntity item : mongoTemplate.find(query, ItemEntity.class, TABLE)) {
                if (itemEntity.getReceipt().getUserProfileId().equals(userProfileId)) {
                    orderedList.add(item);
                }
            }
            return orderedList;
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
	public void delete(ItemEntity object) {
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
		Update update = Update.update("name", object.getName());
		return mongoTemplate.updateFirst(query, update, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteWhereReceipt(ReceiptEntity receipt) {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		mongoTemplate.remove(Query.query(Criteria.where("receipt").is(receipt)), ItemEntity.class);
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<ItemEntity> findItems(String name, String bizName) {
        Criteria criteriaI = Criteria.where("name").regex(new StringTokenizer("^" + name).nextToken(), "i");
        Query query;

        BizNameEntity bizNameEntity = bizNameManager.findOneByName(bizName);
        if(bizNameEntity == null) {
            //query = Query.query(criteriaI);
            return new ArrayList<>();
        } else {
            Criteria criteriaB = Criteria.where("bizName").is(bizNameEntity);
            query = Query.query(criteriaI).addCriteria(criteriaB);
        }

        //This makes just one of the field populated
        query.fields().include("name");
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
        Query query = Query.query(Criteria.where("expenseType.id").is(expenseTypeId));
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
        Query query = Query.query(Criteria.where("expenseType.$id").is(new ObjectId(expenseType.getId())));
        return mongoTemplate.find(query, ItemEntity.class);
    }

    @Override
    public List<ItemEntity> getItemEntitiesForUnAssignedExpenseType(String userProfileId) {
        return mongoTemplate.find(Query.query(Criteria.where("expenseType").is(StringUtils.trimToNull(null)).and("userProfileId").is(userProfileId)), ItemEntity.class);
    }
}
