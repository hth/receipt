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
 */
public class ReceiptForm {
	ReceiptEntityOCR receipt;
	AutoPopulatingList<ItemEntityOCR> items;	
	
	/**
	 * Need for bean instantiation in ReceiptUpdateForm
	 */
	private ReceiptForm() {
		
	}

	private ReceiptForm(ReceiptEntityOCR receipt, AutoPopulatingList<ItemEntityOCR> items) {
		super();
		this.receipt = receipt;
		this.items = items;
	}

	public static ReceiptForm newInstance(ReceiptEntityOCR receipt, AutoPopulatingList<ItemEntityOCR> items) {
		return new ReceiptForm(receipt, items);
	}

	public ReceiptEntityOCR getReceipt() {
		return receipt;
	}

	public void setReceipt(ReceiptEntityOCR receipt) {
		this.receipt = receipt;
	}

	public AutoPopulatingList<ItemEntityOCR> getItems() {
		return items;
	}

	public void setItems(AutoPopulatingList<ItemEntityOCR> items) {
		this.items = items;
	}
}
