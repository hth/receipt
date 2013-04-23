/**
 *
 */
package com.tholix.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.value.ReceiptGrouped;

/**
 * @author hitender
 * @when Dec 26, 2012 9:17:04 PM
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
    public List<ReceiptEntity> getAllObjectsForUser(String userProfileId) {
		Sort sort = new Sort(Direction.DESC, "receiptDate").and(new Sort(Direction.DESC, "created"));
		return mongoTemplate.find(Query.query(Criteria.where("userProfileId").is(userProfileId)).with(sort), ReceiptEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public Map<Date, BigDecimal> getAllObjectsGroupedByDate(String userProfileId) {
//		String map = "function() {"
//					  + " date = Date.UTC(this.receiptDate.getFullYear(), this.receiptDate.getMonth(), this.receiptDate.getDate());"
//					  + " emit({date: date}, {total: 0});"
//					  + "}"
//					  ;
//
//		String reduce = "function(obj, result) { result.total += obj.total; } return {total: result.total};";
//
//		GroupBy groupBy = GroupBy.key("{'day' : 1, 'month' : 1}").initialDocument("{ total: 0 }").reduceFunction("function(obj, result) { result.total += obj.total; }");
//		GroupByResults<ReceiptEntity> results = mongoTemplate.group(Criteria.where("userProfileId").is(userProfileId), TABLE, groupBy, ReceiptEntity.class);

//		MapReduceResults<ReceiptGrouped> results = mongoTemplate.mapReduce(Query.query(Criteria.where("userProfileId").is(userProfileId)), TABLE, map, reduce, ReceiptGrouped.class);

		List<ReceiptEntity> receipts = getAllObjectsForUser(userProfileId);

		Map<Date, BigDecimal> receiptGroupedMap = new HashMap<>();
		for(ReceiptEntity receipt : receipts) {
			ReceiptGrouped.getGroupedReceiptTotal(receipt, receiptGroupedMap);
		}

		//return Lists.<ReceiptGrouped>newArrayList(results);
		return receiptGroupedMap;
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
            titles.add(re.getTitle());
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

	@Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public ReceiptEntity findOne(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), ReceiptEntity.class, TABLE);
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
}
