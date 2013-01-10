/**
 * 
 */
package com.tholix.web.form;

import java.util.List;

import org.springframework.util.AutoPopulatingList;

import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntityOCR;

/**
 * @author hitender
 * @when Jan 7, 2013 9:30:32 AM
 * 
 * This is a Form Backing Object (FBO) for showing the receipt and its items 
 */
public class ReceiptForm {
	ReceiptEntityOCR receipt;
	List<ItemEntityOCR> items;	
	
	/**
	 * Need for bean instantiation in ReceiptUpdateForm
	 */
	private ReceiptForm() {
		
	}

	private ReceiptForm(ReceiptEntityOCR receipt, List<ItemEntityOCR> items) {
		super();
		this.receipt = receipt;
		this.items = items;
	}

	public static ReceiptForm newInstance(ReceiptEntityOCR receipt, List<ItemEntityOCR> items) {
		return new ReceiptForm(receipt, items);
	}
	
	public static ReceiptForm newInstance() {
		return new ReceiptForm();
	}

	public ReceiptEntityOCR getReceipt() {
		return receipt;
	}

	public void setReceipt(ReceiptEntityOCR receipt) {
		this.receipt = receipt;
	}

	public List<ItemEntityOCR> getItems() {
		return items;
	}

	public void setItems(List<ItemEntityOCR> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ReceiptForm [receipt=" + receipt + ", items=" + items + "]";
	}
	
	
}
