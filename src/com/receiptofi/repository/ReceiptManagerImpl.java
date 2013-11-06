/**
 *
 */
package com.receiptofi.repository;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.*;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.joda.time.DateTime;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.utils.DateUtil;

/**
 * @author hitender
 * @since Dec 26, 2012 9:17:04 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public final class ReceiptManagerImpl implements ReceiptManager {
	private static final Logger log = Logger.getLogger(ReceiptManagerImpl.class);

	private static final long serialVersionUID = -8812261440000722447L;

	@Autowired private MongoTemplate mongoTemplate;

	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<ReceiptEntity> getAllObjects() {
		return mongoTemplate.findAll(ReceiptEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<ReceiptEntity> getAllReceipts(String userProfileId) {
        Criteria criteria = Criteria.where("USER_PROFILE_ID").is(userProfileId);

		Sort sort = new Sort(Direction.DESC, "RECEIPT_DATE").and(new Sort(Direction.DESC, "CREATE"));
		return mongoTemplate.find(Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted()).with(sort), ReceiptEntity.class, TABLE);
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<ReceiptEntity> getAllReceiptsForThisMonth(String userProfileId, DateTime monthYear) {
        Criteria criteria = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Criteria criteria1 = Criteria.where("MONTH").is(monthYear.getMonthOfYear());
        Criteria criteria2 = Criteria.where("YEAR").is(monthYear.getYear());

        Sort sort = new Sort(Direction.DESC, "RECEIPT_DATE").and(new Sort(Direction.DESC, "CREATE"));
        Query query = Query.query(criteria).addCriteria(criteria1).addCriteria(criteria2).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.find(query.with(sort), ReceiptEntity.class, TABLE);
    }

	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String userProfileId) {
        GroupBy groupBy = GroupBy.key("DAY", "MONTH", "YEAR")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.day = obj.DAY; " +
                        "  result.month = obj.MONTH; " +
                        "  result.year = obj.YEAR; " +
                        "  result.total += obj.TOTAL; " +
                        "}");

        Criteria criteria = Criteria.where("USER_PROFILE_ID").is(userProfileId).andOperator(isActive().andOperator(isNotDeleted()));
        GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
        return results.iterator();
	}

    //TODO find a way to format the total in group by
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByMonth(String userProfileId) {
        GroupBy groupBy = GroupBy.key("MONTH", "YEAR")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.month = obj.MONTH; " +
                        "  result.year = obj.YEAR; " +
                        "  result.total += obj.TOTAL; " +
                        "}");

        DateTime date = DateUtil.now().minusMonths(SHOW_DATA_FOR_LAST_X_MONTHS);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        Criteria criteriaA = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Criteria criteriaB = Criteria.where("RECEIPT_DATE").gte(since.toDate());
        Criteria criteria = criteriaA.andOperator(criteriaB.andOperator(isActive().andOperator(isNotDeleted())));

        GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
        return results.iterator();
    }

    public Iterator<ReceiptGroupedByBizLocation> getAllReceiptGroupedByBizLocation(String userProfileId) {
        GroupBy groupBy = GroupBy.key("BIZ_STORE", "BIZ_NAME")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.total += obj.TOTAL; " +
                        "  result.bizStore = obj.BIZ_STORE; " +
                        "  result.bizName = obj.BIZ_NAME; " +
                        "}");


        DateTime date = DateUtil.now().minusMonths(SHOW_DATA_FOR_LAST_X_MONTHS);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        Criteria criteriaA = Criteria.where("USER_PROFILE_ID").is(userProfileId);
        Criteria criteriaB = Criteria.where("RECEIPT_DATE").gte(since.toDate());
        Criteria criteria = criteriaA.andOperator(criteriaB.andOperator(isActive().andOperator(isNotDeleted())));

        GroupByResults<ReceiptGroupedByBizLocation> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGroupedByBizLocation.class);
        return results.iterator();
    }

    //http://stackoverflow.com/questions/12949870/spring-mongotemplate-find-special-column
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<String> findTitles(String title) {
        Criteria criteria = Criteria.where("TITLE").regex(title, "i");
        Query query = Query.query(criteria);

        //This makes just one of the field populated
        query.fields().include("TITLE");
        List<ReceiptEntity> list = mongoTemplate.find(query, ReceiptEntity.class, TABLE);

        List<String> titles = new ArrayList<>();
        for(ReceiptEntity re : list) {
            titles.add(re.getBizName().getName());
        }

        return titles;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(ReceiptEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			// Cannot use insert because insert does not perform update like save.
			// Save will always try to update or create new record.
			// mongoTemplate.insert(object, TABLE);

            if(object.getId() != null) {
                object.setUpdated();
            }
            object.checkSum();
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for ReceiptEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

    /**
     * Use findReceipt method instead of findOne
     *
     * @param id
     * @return
     */
    @Deprecated
	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public ReceiptEntity findOne(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), ReceiptEntity.class, TABLE);
	}

    /**
     * Use this method instead of findOne
     *
     * @param receiptId
     * @param userProfileId
     * @return
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public ReceiptEntity findReceipt(String receiptId, String userProfileId) {
        Query query = Query.query(Criteria.where("id").is(receiptId))
                .addCriteria(Criteria.where("USER_PROFILE_ID").is(userProfileId))
                .addCriteria(isActive())
                .addCriteria(isNotDeleted());
        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

    @Override
    public List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String userProfileId) {
        Criteria criteria1 = Criteria.where("userProfileId").is(userProfileId);
        Criteria criteria2 = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
        criteria2.andOperator(isActive().andOperator(isNotDeleted()));

        Sort sort = new Sort(Direction.DESC, "RECEIPT_DATE");

        Query query = Query.query(criteria1).addCriteria(criteria2).with(sort);
        return mongoTemplate.find(query, ReceiptEntity.class, TABLE);
    }

    @Override
    public ReceiptEntity findWithReceiptOCR(String receiptOCRId) {
        Query query = Query.query(Criteria.where("RECEIPT_OCR_ID").is(receiptOCRId));
        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(ReceiptEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteSoft(ReceiptEntity object) {
        //Deleted check sum need re-calculation
        object.markAsDeleted();

        //Re-calculate check sum for deleted object
        object.checkSum();
        String checkSum = object.getCheckSum();

        if(existCheckSum(checkSum)) {
            Criteria criteria = Criteria.where("CHECK_SUM").is(checkSum);
            List<ReceiptEntity> duplicateDeletedReceipts = mongoTemplate.find(Query.query(criteria), ReceiptEntity.class, TABLE);
            for(ReceiptEntity receiptEntity : duplicateDeletedReceipts) {
                deleteHard(receiptEntity);
            }
        }

        Query query = Query.query(Criteria.where("id").is(object.getId()));
        Update update = Update.update("DELETE", true).set("CHECK_SUM", checkSum);
        mongoTemplate.updateFirst(query, update(update), ReceiptEntity.class);
    }

    @Override
    public long countAllReceiptForAStore(BizStoreEntity bizStoreEntity) {
        Criteria criteria = Criteria.where("BIZ_STORE.$id").is(new ObjectId(bizStoreEntity.getId()));
        return mongoTemplate.count(Query.query(criteria), TABLE);
    }

    @Override
    public long countAllReceiptForABizName(BizNameEntity bizNameEntity) {
        Criteria criteria = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
        return mongoTemplate.count(Query.query(criteria), TABLE);
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

    @Override
    public List<ReceiptEntity> findThisDayReceipts(int year, int month, int day, String userProfileId) {
        Criteria criteria = Criteria.where("USER_PROFILE_ID").is(userProfileId)
                .andOperator(Criteria.where("YEAR").is(year),
                        Criteria.where("MONTH").is(month),
                        Criteria.where("DAY").is(day));

        Sort sort = new Sort(Direction.DESC, "RECEIPT_DATE");
        return mongoTemplate.find(Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted()).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public boolean existCheckSum(String checkSum) {
        Criteria criteria = Criteria.where("CHECK_SUM").is(checkSum);
        //Active condition is required for re-check criteria
        return mongoTemplate.find(Query.query(criteria).addCriteria(isActive()), ReceiptEntity.class, TABLE).size() > 0;
    }
}
