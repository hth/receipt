package com.receiptofi.domain.util;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Formatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 3/31/16 4:43 PM
 */
public class DeepCopy {
    private DeepCopy() {
    }

    public static ReceiptEntity getReceiptEntity(DocumentEntity document) throws NumberFormatException, ParseException {
        ReceiptEntity receipt = ReceiptEntity.newInstance();
        receipt.setReceiptDate(DateUtil.getDateFromString(document.getReceiptDate()));
        receipt.setTotal(Formatter.getCurrencyFormatted(document.getTotal()).doubleValue());
        receipt.setTax(Formatter.getCurrencyFormatted(document.getTax()).doubleValue());
        receipt.setReceiptStatus(DocumentStatusEnum.PROCESSED);
        receipt.setReceiptUserId(document.getReceiptUserId());
        receipt.setCreated(document.getCreated());
        receipt.setUpdated();
        receipt.setBizName(document.getBizName());
        receipt.setBizStore(document.getBizStore());
        receipt.computeChecksum();

        //If this is not set then user cannot reopen the a receipt for re-check.
        //TODO When deleting historical receiptDocument make sure to remove this id from receipt referencing Document
        receipt.setDocumentId(document.getId());
        receipt.setRecheckComment(document.getRecheckComment());
        receipt.setNotes(document.getNotes());

        //This condition is mostly true for receipt when re-checked
        if (StringUtils.isNotEmpty(document.getReferenceDocumentId())) {
            receipt.setId(document.getReferenceDocumentId());
        }

        return receipt;
    }

    /**
     * @param receipt - Required receipt with Id
     * @return
     * @throws ParseException
     */
    public static List<ItemEntity> getItemEntity(ReceiptEntity receipt, List<ItemEntityOCR> items) throws ParseException, NumberFormatException {
        List<ItemEntity> listOfItems = new LinkedList<>();

        for (ItemEntityOCR itemOCR : items) {
            if (itemOCR.getName().length() != 0) {
                String name = itemOCR.getName().trim();
                name = StringUtils.replace(name, "\t", " ");
                name = name.replaceAll("\\s+", " ");

                ItemEntity item = new ItemEntity();
                item.setName(WordUtils.capitalize(WordUtils.capitalizeFully(name), '.', '(', ')'));
                item.setPrice(Formatter.getCurrencyFormatted(itemOCR.getPrice()).doubleValue());
                item.setQuantity(itemOCR.getQuantity());
                item.setTaxed(itemOCR.getTaxed());
                item.setSequence(itemOCR.getSequence());
                item.setReceipt(receipt);
                item.setReceiptUserId(receipt.getReceiptUserId());
                item.setExpenseTag(itemOCR.getExpenseTag());
                item.setCreated(itemOCR.getCreated());
                item.setUpdated();

                item.setBizName(receipt.getBizName());
                listOfItems.add(item);
            }
        }

        return listOfItems;
    }
}
