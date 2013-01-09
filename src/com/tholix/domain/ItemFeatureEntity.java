/**
 * 
 */
package com.tholix.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.types.FeaturesOnItemEnum;

/**
 * Add various features to an existing Item. It could be like price check for next thirty days. Or add a return reminder on an item.
 * 
 * @author hitender
 * @when Dec 26, 2012 1:47:36 PM
 * 
 */
@Document(collection = "ITEM_FEATURE")
public class ItemFeatureEntity extends BaseEntity {
	private static final long serialVersionUID = -4231361664120744038L;

	@DBRef
	private ItemEntity itemEntity;

	private FeaturesOnItemEnum featureOnItem;

	private ItemFeatureEntity(FeaturesOnItemEnum featureOnItem, ItemEntity itemEntity) {
		super();
		this.featureOnItem = featureOnItem;
		this.itemEntity = itemEntity;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 * 
	 * @param featureOnItem
	 * @param itemEntity
	 * @return
	 */
	public static ItemFeatureEntity newInstance(FeaturesOnItemEnum featureOnItem, ItemEntity itemEntity) {
		return new ItemFeatureEntity(featureOnItem, itemEntity);
	}

	public ItemEntity getItemEntity() {
		return itemEntity;
	}

	public FeaturesOnItemEnum getFeatureOnItem() {
		return featureOnItem;
	}

}
