/**
 *
 */
package com.tholix.web.form;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

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
public final class ReceiptOCRForm {

	ReceiptEntityOCR receiptOCR;
	List<ItemEntityOCR> items;

    /** Used for showing error messages to user when the request action fails to execute */
    String errorMessage;

	/**
	 * Need for bean instantiation in ReceiptUpdateForm
	 */
	private ReceiptOCRForm() {}

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

	public ReceiptEntityOCR getReceiptOCR() {
		return receiptOCR;
	}

	public void setReceiptOCR(ReceiptEntityOCR receiptOCR) {
		this.receiptOCR = receiptOCR;
	}

	public List<ItemEntityOCR> getItems() {
		return items;
	}

	public void setItems(List<ItemEntityOCR> items) {
		this.items = items;
	}

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

	@Override
	public String toString() {
		return "ReceiptOCRForm [receiptOCR=" + receiptOCR + ", items=" + items + "]";
	}

	public ReceiptEntity getReceiptEntity() throws NumberFormatException, ParseException {
        ReceiptEntity receipt = ReceiptEntity.newInstance();
        receipt.setReceiptDate(DateUtil.getDateFromString(receiptOCR.getReceiptDate()));
        receipt.setTotal(Formatter.getCurrencyFormatted(receiptOCR.getTotal()).doubleValue());
        receipt.setTax(Formatter.getCurrencyFormatted(receiptOCR.getTax()).doubleValue());
        receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
        receipt.setReceiptBlobId(receiptOCR.getReceiptBlobId());
        receipt.setUserProfileId(receiptOCR.getUserProfileId());
		receipt.setCreated(receiptOCR.getCreated());
        receipt.setUpdated();
        receipt.setBizName(receiptOCR.getBizName());
        receipt.setBizStore(receiptOCR.getBizStore());
        receipt.setReceiptOf(receiptOCR.getReceiptOf());

        //If this is not set then user cannot reopen the a receipt for re-check.
        //TODO When deleting historical receiptOCR make sure to remove this id from receipt referencing receipt OCR
        receipt.setReceiptOCRId(receiptOCR.getId());
        receipt.setRecheckComment(receiptOCR.getRecheckComment());
        receipt.setNotes(receiptOCR.getNotes());

        //This condition is mostly true for receipt when re-checked
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

                ItemEntity item = ItemEntity.newInstance();
                item.setName(WordUtils.capitalizeFully(name));
                item.setPrice(Formatter.getCurrencyFormatted(itemOCR.getPrice()).doubleValue());
                item.setQuantity(itemOCR.getQuantity());
                item.setTaxed(itemOCR.getTaxed());
                item.setSequence(itemOCR.getSequence());
                item.setReceipt(receipt);
                item.setUserProfileId(receipt.getUserProfileId());
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
