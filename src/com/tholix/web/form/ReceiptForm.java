/**
 * 
 */
package com.tholix.web.form;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;

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
	
	public ReceiptEntity getReceiptEntity() throws NumberFormatException, Exception {
		ReceiptEntity receiptEntity = ReceiptEntity.newInstance(receipt.getTitle(), DateUtil.getDateFromString(receipt.getReceiptDate()), 
										Formatter.getCurrencyFormatted(receipt.getTotal()), Formatter.getCurrencyFormatted(receipt.getTax()), 
										receipt.getDescription(), ReceiptStatusEnum.TURK_PROCESSED, receipt.getReceiptBlobId(), 
										receipt.getUserProfileId());
		receiptEntity.setCreated(receipt.getCreated());
		receiptEntity.setUpdated();
		return receiptEntity;
	}
	
	/**
	 * 
	 * @param receipt - Required receipt with Id
	 * @return
	 * @throws ParseException 
	 */
	public List<ItemEntity> getItemEntity(ReceiptEntity receipt) throws ParseException {
		List<ItemEntity> listOfItems = new ArrayList<ItemEntity>();
		
		for(ItemEntityOCR item : items) {
			if(item.getName().length() != 0) {
				ItemEntity ie = ItemEntity.newInstance(item.getName(), Formatter.getCurrencyFormatted(item.getPrice()), item.getTaxed(), item.getSequence(), receipt, receipt.getUserProfileId());
				ie.setCreated(item.getCreated());
				ie.setUpdated();
				
				listOfItems.add(ie);
			}
		}
		
		return listOfItems;
	}
}
