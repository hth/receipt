/**
 *
 */
package com.tholix.web.form;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;

/**
 * @author hitender
 * @since Jan 7, 2013 9:30:32 AM
 *
 * This is a Form Backing Object (FBO) for showing the receipt and its items
 */
public class ReceiptOCRForm {
	ReceiptEntityOCR receiptOCR;
	List<ItemEntityOCR> items;

	/**
	 * Need for bean instantiation in ReceiptUpdateForm
	 */
	private ReceiptOCRForm() {

	}

	private ReceiptOCRForm(ReceiptEntityOCR receipt, List<ItemEntityOCR> items) {
		this.receiptOCR = receipt;
		this.items = items;
	}

	public static ReceiptOCRForm newInstance(ReceiptEntityOCR receipt, List<ItemEntityOCR> items) {
		return new ReceiptOCRForm(receipt, items);
	}

	public static ReceiptOCRForm newInstance() {
		return new ReceiptOCRForm();
	}

	public ReceiptEntityOCR getReceipt() {
		return receiptOCR;
	}

	public void setReceipt(ReceiptEntityOCR receiptOCR) {
		this.receiptOCR = receiptOCR;
	}

	public List<ItemEntityOCR> getItems() {
		return items;
	}

	public void setItems(List<ItemEntityOCR> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ReceiptOCRForm [receiptOCR=" + receiptOCR + ", items=" + items + "]";
	}

	public ReceiptEntity getReceiptEntity() throws NumberFormatException, Exception {
        //TODO this code has to be redone as it just difficult to understand after a while
		ReceiptEntity receipt = ReceiptEntity.newInstance(DateUtil.getDateFromString(receiptOCR.getReceiptDate()),
										Formatter.getCurrencyFormatted(receiptOCR.getTotal()).doubleValue(),
                                        Formatter.getCurrencyFormatted(receiptOCR.getTax()).doubleValue(),
										receiptOCR.getDescription(), ReceiptStatusEnum.TURK_PROCESSED, receiptOCR.getReceiptBlobId(),
										receiptOCR.getUserProfileId());
		receipt.setCreated(receiptOCR.getCreated());

        receipt.setBizName(receiptOCR.getBizName());
        receipt.setBizStore(receiptOCR.getBizStore());
        receipt.setReceiptOCRId(receiptOCR.getId());

        //This condition is mostly true for receipt recheck
        if(StringUtils.isNotEmpty(receiptOCR.getReceiptId())) {
            receipt.setId(receiptOCR.getReceiptId());
        }

		return receipt;
	}

	/**
	 *
	 * @param receipt - Required receipt with Id
	 * @return
	 * @throws ParseException
	 */
	public List<ItemEntity> getItemEntity(ReceiptEntity receipt) throws ParseException {
		List<ItemEntity> listOfItems = new ArrayList<>();

		for(ItemEntityOCR itemOCR : items) {
			if(itemOCR.getName().length() != 0) {
                String name = itemOCR.getName().trim();
                name = StringUtils.replace(name, "\t", " ");
                name = name.replaceAll("\\s+", " ");

				ItemEntity item = ItemEntity.newInstance(
                        name, Formatter.getCurrencyFormatted(itemOCR.getPrice()).doubleValue(),
                        itemOCR.getTaxed(), itemOCR.getSequence(), receipt, receipt.getUserProfileId());

				item.setExpenseType(itemOCR.getExpenseType());
                item.setCreated(itemOCR.getCreated());
				item.setUpdated();

                item.setBizName(receipt.getBizName());
				listOfItems.add(item);
			}
		}

		return listOfItems;
	}
}
