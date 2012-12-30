/**
 * 
 */
package com.tholix.domain;

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
//mongoTemplate.ensureIndex(new Index().on("lastName",Order.ASCENDING), Customer.class);
@Document(collection = "ITEM")
@CompoundIndexes({ @CompoundIndex(name = "user_item_idx", def = "{'receipt': -1, 'user': 1}") })
public class ItemEntity extends BaseEntity {

	private static final long serialVersionUID = 1031429034359059354L;

	@NumberFormat(style = Style.NUMBER)
	private int quantity;

	@Size(min = 1, max = 128)
	private String name;

	@NumberFormat(style = Style.CURRENCY)
	private Double price;

	private TaxEnum taxed = TaxEnum.NOT_TAXED;

	@DBRef
	private ReceiptEntity receipt;

	@DBRef
	private UserEntity user;

	private ItemEntity(int quantity, String name, Double price, TaxEnum taxed, ReceiptEntity receipt, UserEntity user) {
		super();
		this.quantity = quantity;
		this.name = name;
		this.price = price;
		this.taxed = taxed;
		this.receipt = receipt;
		this.user = user;
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
	public static ItemEntity newInstance(int quantity, String name, Double price, TaxEnum taxed, ReceiptEntity receipt, UserEntity user) {
		return new ItemEntity(quantity, name, price, taxed, receipt, user);
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}
