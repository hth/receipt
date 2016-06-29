/**
 *
 */
package com.receiptofi.web.form;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.TaxEnum;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a Form Backing Object (FBO) for showing the receipt and its items.
 *
 * @author hitender
 * @since Jan 7, 2013 9:30:32 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ReceiptDocumentForm {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptDocumentForm.class);

    private DocumentEntity receiptDocument;
    private List<ItemEntityOCR> items;
    private Map<Date, UserProfileEntity> processedBy = new LinkedHashMap<>();

    /** Used for showing error messages to user when the request action fails to execute */
    private String errorMessage;

    /**
     * Need for bean instantiation in ReceiptUpdateForm
     */
    private ReceiptDocumentForm() {
    }

    private ReceiptDocumentForm(DocumentEntity receiptDocument, List<ItemEntityOCR> items) {
        this.receiptDocument = receiptDocument;
        this.items = items;
    }

    public static ReceiptDocumentForm newInstance(DocumentEntity receipt, List<ItemEntityOCR> items) {
        return new ReceiptDocumentForm(receipt, items);
    }

    public static ReceiptDocumentForm newInstance() {
        return new ReceiptDocumentForm();
    }

    public DocumentEntity getReceiptDocument() {
        return receiptDocument;
    }

    public void setReceiptDocument(DocumentEntity receiptDocument) {
        this.receiptDocument = receiptDocument;
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

    public Map<Date, UserProfileEntity> getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Map<Date, UserProfileEntity> processedBy) {
        this.processedBy = processedBy;
    }

    @Override
    public String toString() {
        return "ReceiptDocumentForm [receiptDocument=" + receiptDocument + ", items=" + items + "]";
    }

    /**
     * Used for calculating individual item tax calculation
     *
     * @param items
     * @param receipt
     */
    public void updateItemWithTaxAmount(List<ItemEntity> items, ReceiptEntity receipt) {
        BigDecimal taxedItemTotalWithoutTax = BigDecimal.ZERO;

        for (ItemEntity item : items) {
            if (TaxEnum.T == item.getTaxed()) {
                taxedItemTotalWithoutTax = Maths.add(taxedItemTotalWithoutTax, item.getTotalPriceWithoutTax());
            }
        }

        BigDecimal tax = Maths.calculateTax(receipt.getTax(), taxedItemTotalWithoutTax);
        if (0 == tax.compareTo(BigDecimal.ZERO)) {
            receipt.setPercentTax("0.0000");
        } else {
            receipt.setPercentTax(tax.toString());
        }

        for (ItemEntity item : items) {
            if (TaxEnum.T == item.getTaxed()) {
                BigDecimal taxedAmount = Maths.multiply(item.getPrice().toString(), receipt.getPercentTax());
                item.setTax(new Double(taxedAmount.toString()));
            } else {
                item.setTax(0.00);
            }
        }
    }
}
