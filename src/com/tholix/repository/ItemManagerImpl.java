/**
 *
 */
package com.tholix.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.mongodb.WriteResult;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.types.TaxEnum;

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
    @Autowired private ExpenseTypeManager expenseTypeManager;

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
			for(ItemEntity object : objects) {
				save(object);

                // This has to be done to make the reference object available otherwise only id is available
                // which can cause an issue during query. As with just id we will have to query twice. This
                // saves us second query but forces us to do double update
                if(object.getExpenseType() != null && object.getExpenseType().getId() != null) {
                    updateItemExpenseType(object);
                }
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

	@Override
	public List<ItemEntity> getAllObjectWithName(String name) {
		return mongoTemplate.find(Query.query(Criteria.where("name").is(name)), ItemEntity.class, TABLE);
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
            query = Query.query(criteriaI);
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
    public void updateItemExpenseType(ItemEntity item) {
        Query query = Query.query(Criteria.where("id").is(item.getId()));
        Update update = Update.update("expenseType", item.getExpenseType());
        mongoTemplate.updateFirst(query, update, ItemEntity.class);
    }

    @Override
    public long countItemsUsingExpenseType(String expenseTypeId) {
        Query query = Query.query(Criteria.where("expenseType.id").is(expenseTypeId));
        return mongoTemplate.count(query, ItemEntity.class);
    }

    @Override
    public Map<String, BigDecimal> getAllItemExpense(String profileId) {
        Map<String, BigDecimal> expenseItems = new HashMap<>();
        BigDecimal netSum = new BigDecimal("0.00");

        //Find sum of all items for particular expense
        List<ExpenseTypeEntity> expenseTypeEntities = expenseTypeManager.activeExpenseTypes(profileId);
        for(ExpenseTypeEntity expenseTypeEntity : expenseTypeEntities) {

            BigDecimal sum = new BigDecimal("0.00");
            List<ItemEntity> items = getItemEntitiesForSpecificExpenseType(expenseTypeEntity);
            sum = calculateSum(sum, items);
            netSum = netSum.add(sum);
            expenseItems.put(expenseTypeEntity.getExpName(), sum);
        }


        netSum = populateWithUnAssignedItems(expenseItems, netSum, profileId);

        // Calculate percentage
        for(String key : expenseItems.keySet()) {
            BigDecimal percent = (expenseItems.get(key).multiply(new BigDecimal("100.00")).divide(netSum, 2, BigDecimal.ROUND_HALF_UP)).stripTrailingZeros();
            percent = percent.setScale(1, BigDecimal.ROUND_FLOOR);
            expenseItems.put(key, percent);
        }

        return expenseItems;
    }

    @Override
    public List<ItemEntity> getItemEntitiesForSpecificExpenseType(ExpenseTypeEntity expenseTypeEntity) {
        return getItemEntitiesForSpecificExpenseType(expenseTypeEntity.getId());
    }

    @Override
    public List<ItemEntity> getItemEntitiesForSpecificExpenseType(String expenseTypeId) {
        return mongoTemplate.find(Query.query(Criteria.where("expenseType.id").is(expenseTypeId)), ItemEntity.class);
    }

    /**
     * Finds all the un-assigned items for the user
     *
     * @param expenseItems
     * @param netSum
     * @return
     */
    private BigDecimal populateWithUnAssignedItems(Map<String, BigDecimal> expenseItems, BigDecimal netSum, String profileId) {
        List<ItemEntity> unassignedItems = getItemEntitiesForUnAssignedExpenseType(profileId);
        if(unassignedItems.size() > 0) {
            BigDecimal sum = calculateSum(new BigDecimal("0.00"), unassignedItems);
            netSum = netSum.add(sum);
            expenseItems.put("Un-Assigned", sum);
        }
        return netSum;
    }

    /**
     * Calculate sum for all the items
     *
     * @param sum
     * @param items
     * @return
     */
    private BigDecimal calculateSum(BigDecimal sum, List<ItemEntity> items) {
        for(ItemEntity item : items) {
            String receiptId = item.getReceipt().getId();
            ReceiptEntity receiptEntity = mongoTemplate.findOne(Query.query(Criteria.where("id").is(receiptId)), ReceiptEntity.class);
            if(item.getTaxed() == TaxEnum.TAXED) {
                sum = sum.add(new BigDecimal(item.getPrice().toString()).multiply(receiptEntity.getTaxInPercentage()));
            } else {
                sum = sum.add(new BigDecimal(item.getPrice().toString()));
            }
        }
        return sum;
    }

    @Override
    public List<ItemEntity> getItemEntitiesForUnAssignedExpenseType(String userProfileId) {
        return mongoTemplate.find(Query.query(Criteria.where("expenseType").is(StringUtils.trimToNull(null)).and("userProfileId").is(userProfileId)), ItemEntity.class);
    }
}
