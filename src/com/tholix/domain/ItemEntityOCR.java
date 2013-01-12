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
 * @author hitender
 * @when Jan 6, 2013 1:17:12 PM
 * 
 */
@Document(collection = "ITEM_OCR")
@CompoundIndexes({ @CompoundIndex(name = "user_item_ocr_idx", def = "{'receipt': -1, 'userProfileId': 1}") })
public class ItemEntityOCR extends BaseEntity {
	private static final long serialVersionUID = -8655601429195675799L;

	@Size(min = 1, max = 128)
	private String name;

	@NumberFormat(style = Style.CURRENCY)
	private String price;

	@NotNull
	private TaxEnum taxed = TaxEnum.NOT_TAXED;
	
	@NotNull
	private int sequence;

	@DBRef
	private ReceiptEntityOCR receipt;

	@NotNull
	private String userProfileId;
	
	/** To keep spring happy in recreating the bean from form during submit action */
	public ItemEntityOCR() {
		
	}

	private ItemEntityOCR(String name, String price, TaxEnum taxed, int sequence, ReceiptEntityOCR receipt, String userProfileId) {
		super();
		this.name = name;
		this.price = price;
		this.taxed = taxed;
		this.receipt = receipt;
		this.userProfileId = userProfileId;
		this.sequence = sequence;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 * 
	 * @param name
	 * @param price
	 * @param taxed
	 * @param receipt
	 * @param userProfileId
	 * @return
	 */
	public static ItemEntityOCR newInstance(String name, String price, TaxEnum taxed, int sequence, ReceiptEntityOCR receipt, String userProfileId) {
		return new ItemEntityOCR(name, price, taxed, sequence, receipt, userProfileId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public TaxEnum getTaxed() {
		return taxed;
	}

	public void setTaxed(TaxEnum taxed) {
		this.taxed = taxed;
	}	

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public ReceiptEntityOCR getReceipt() {
		return this.receipt;
	}

	public void setReceipt(ReceiptEntityOCR receipt) {
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
