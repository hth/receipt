/**
 *
 */
package com.tholix.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 3:09:48 PM
 *
 */
public interface ReceiptManager extends RepositoryManager<ReceiptEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntity.class, Document.class, "collection");

	public List<ReceiptEntity> getAllObjectsForUser(String userProfileId);

	//public List<ReceiptGrouped> getAllObjectsGroupedByDate(String userProfileId);
	public Map<Date, BigDecimal> getAllObjectsGroupedByDate(String userProfileId);

    public List<String> findTitles(String title);

    public long collectionSize();
}
