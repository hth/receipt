/**
 *
 */
package com.receiptofi.domain;

import com.receiptofi.domain.types.TaxEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author hitender
 * @since Jan 6, 2013 1:17:12 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "ITEM_OCR")
@CompoundIndexes ({@CompoundIndex (name = "user_item_ocr_idx", def = "{'RECEIPT': -1, 'RID': 1}")})
public class ItemEntityOCR extends BaseEntity {

    @Size (min = 1, max = 128)
    @Field ("IN")
    private String name;

    @NumberFormat (style = Style.CURRENCY)
    @Field ("PRC")
    private String price;

    @Field ("QN")
    private Double quantity = 1.00;

    @NotNull
    @Field ("TT")
    private TaxEnum taxed = TaxEnum.NT;

    @NotNull
    @Field ("SEQ")
    private int sequence;

    @DBRef
    @Field ("DOCUMENT")
    private DocumentEntity document;

    @Field ("RTX")
    private String receiptDate;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field ("EXPENSE_TAG")
    private ExpenseTagEntity expenseTag;

    /**
     * This method is used when the Entity is created for the first time or during receipt re-check.
     */
    public static ItemEntityOCR newInstance() {
        return new ItemEntityOCR();
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

    public DocumentEntity getDocument() {
        return this.document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
        this.receiptDate = document.getReceiptDate();
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public ExpenseTagEntity getExpenseTag() {
        return expenseTag;
    }

    public void setExpenseTag(ExpenseTagEntity expenseTag) {
        this.expenseTag = expenseTag;
    }

    @Override
    public String toString() {
        return "ItemEntity [name=" + name + ", price=" + price + ", taxed=" + taxed + "]";
    }
}
