/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.tholix.domain.types.TaxEnum;

/**
 * Represents each individual item on a receipt.
 * 
 * @author hitender
 * @when Dec 25, 2012 11:43:10 PM
 * 
 */
// mongoTemplate.ensureIndex(new Index().on("lastName",Order.ASCENDING), Customer.class);
@Document(collection = "ITEM")
@CompoundIndexes({ @CompoundIndex(name = "user_item_idx", def = "{'receipt': -1, 'user': 1}") })
public class ItemEntity extends BaseEntity {

	private static final long serialVersionUID = 1031429034359059354L;

	@Size(min = 1, max = 128)
	private String name;

	@NumberFormat(style = Style.CURRENCY)
	private Double price;

	private TaxEnum taxed = TaxEnum.NOT_TAXED;

	@DBRef
	private ReceiptEntity receipt;

	@NotNull
	private String userProfileId;
	
	public ItemEntity() {
		
	}

	private ItemEntity(String name, Double price, TaxEnum taxed, ReceiptEntity receipt, String userProfileId) {
		super();
		this.name = name;
		this.price = price;
		this.taxed = taxed;
		this.receipt = receipt;
		this.userProfileId = userProfileId;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 * 
	 * @param quantity
	 * @param name
	 * @param price
	 * @param taxed
	 * @param receipt
	 * @param user
	 * @return
	 */
	public static ItemEntity newInstance(String name, Double price, TaxEnum taxed, ReceiptEntity receipt, String userProfileId) {
		return new ItemEntity(name, price, taxed, receipt, userProfileId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public TaxEnum getTaxed() {
		return taxed;
	}

	public void setTaxed(TaxEnum taxed) {
		this.taxed = taxed;
	}

	public ReceiptEntity getReceipt() {
		return this.receipt;
	}

	public void setReceipt(ReceiptEntity receipt) {
		this.receipt = receipt;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

	@Override
	public String toString() {
		return "ItemEntity [name=" + name + ", price=" + price + ", taxed=" + taxed + "]";
	}
}
