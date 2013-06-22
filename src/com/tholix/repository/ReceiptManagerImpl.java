/**
 *
 */
package com.tholix.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.joda.time.DateTime;

import com.mongodb.WriteResult;

import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.value.ReceiptGrouped;
import com.tholix.utils.DateUtil;

/**
 * @author hitender
 * @since Dec 26, 2012 9:17:04 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public class ReceiptManagerImpl implements ReceiptManager {
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
        Criteria criteria = Criteria.where("userProfileId").is(userProfileId)
                .andOperator(Criteria.where("active").is(true));

		Sort sort = new Sort(Direction.DESC, "receiptDate").and(new Sort(Direction.DESC, "created"));
		return mongoTemplate.find(Query.query(criteria).with(sort), ReceiptEntity.class, TABLE);
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<ReceiptEntity> getAllReceiptsForThisMonth(String userProfileId) {
        DateTime dateTime = DateUtil.now();
        Criteria criteria = Criteria.where("userProfileId").is(userProfileId);
        Criteria criteria1 = Criteria.where("month").is(dateTime.getMonthOfYear());
        Criteria criteria2 = Criteria.where("year").is(dateTime.getYear());
        Criteria criteria3 = Criteria.where("active").is(true);

        Sort sort = new Sort(Direction.DESC, "receiptDate").and(new Sort(Direction.DESC, "created"));
        Query query = Query.query(criteria).addCriteria(criteria1).addCriteria(criteria2).addCriteria(criteria3);
        return mongoTemplate.find(query.with(sort), ReceiptEntity.class, TABLE);
    }

	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String userProfileId) {
        GroupBy groupBy = GroupBy.key("day", "month", "year")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.day = obj.day; " +
                        "  result.month = obj.month; " +
                        "  result.year = obj.year; " +
                        "  result.total += obj.total; " +
                        "}");

        Criteria criteria = Criteria.where("userProfileId").is(userProfileId).andOperator(Criteria.where("active").is(true));
        GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
        return results.iterator();
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByMonth(String userProfileId) {
        GroupBy groupBy = GroupBy.key("month", "year")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.month = obj.month; " +
                        "  result.year = obj.year; " +
                        "  result.total += obj.total; " +
                        "}");

        DateTime date = DateUtil.now().minusMonths(13);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        Criteria criteria = Criteria.where("userProfileId").is(userProfileId)
                .andOperator(Criteria.where("receiptDate").gte(since.toDate())
                        .andOperator(Criteria.where("active").is(true)));
        GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
        return results.iterator();
    }

    //http://stackoverflow.com/questions/12949870/spring-mongotemplate-find-special-column
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public List<String> findTitles(String title) {
        Criteria criteria = Criteria.where("title").regex(title, "i");
        Query query = Query.query(criteria);

        //This makes just one of the field populated
        query.fields().include("title");
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

			object.setUpdated();
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
                .addCriteria(Criteria.where("userProfileId").is(userProfileId))
                .addCriteria(Criteria.where("active").is(true));
        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

    @Override
    public ReceiptEntity findWithReceiptOCR(String receiptOCRId) {
        Query query = Query.query(Criteria.where("receiptOCRId").is(receiptOCRId));
        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(ReceiptEntity object) {
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
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public List<ReceiptEntity> findThisDayReceipts(int year, int month, int day, String userProfileId) {
        Criteria criteria = Criteria.where("userProfileId").is(userProfileId)
                .andOperator(Criteria.where("year").is(year),
                        Criteria.where("month").is(month),
                        Criteria.where("day").is(day),
                        Criteria.where("active").is(true));

        Sort sort = new Sort(Direction.DESC, "receiptDate");
        return mongoTemplate.find(Query.query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }
}
