/**
 *
 */
package com.receiptofi.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.receiptofi.domain.types.TaxEnum;
import com.receiptofi.utils.Maths;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * Represents each individual item on a receipt.
 *
 * @author hitender
 * @since Dec 25, 2012 11:43:10 PM
 */
// mongoTemplate.ensureIndex(new Index().on("lastName",Order.ASCENDING), Customer.class);
@Document (collection = "ITEM")
//TODO(hth) @DBRef RECEIPT index does not look correct to me
@CompoundIndexes ({@CompoundIndex (name = "user_item_idx", def = "{'RECEIPT': -1, 'RID': 1}")})
public final class ItemEntity extends BaseEntity {

    @Size (min = 1, max = 128)
    @Field ("IN")
    private String name;

    @NumberFormat (style = Style.CURRENCY)
    @Field ("PRC")
    private Double price;

    /**
     * Paid tax on an item
     */
    @NumberFormat (style = Style.CURRENCY)
    @Field ("TAX")
    private Double tax;

    @Field ("QN")
    private Double quantity = 1.00;

    @NotNull
    @Field ("TT")
    private TaxEnum taxed = TaxEnum.NOT_TAXED;

    @NotNull
    @Field ("SEQ")
    private int sequence;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @DBRef
    @Field ("RECEIPT")
    private ReceiptEntity receipt;

    @DateTimeFormat (iso = ISO.DATE_TIME)
    @Field ("RTX")
    private Date receiptDate;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field ("EXPENSE_TAG")
    private ExpenseTagEntity expenseTag;

    public ItemEntity() {
    }

    private ItemEntity(
            String name,
            Double price,
            TaxEnum taxed,
            int sequence,
            ReceiptEntity receipt,
            String receiptUserId
    ) {
        super();
        this.name = name;
        this.price = price;
        this.taxed = taxed;
        this.receipt = receipt;
        this.receiptUserId = receiptUserId;
        this.sequence = sequence;
        this.receiptDate = receipt.getReceiptDate();
    }

    public static ItemEntity newInstance() {
        return new ItemEntity();
    }

    /**
     * This method is used when the Entity is created for the first time.
     *
     * @param name
     * @param price
     * @param taxed
     * @param sequence
     * @param receipt
     * @param userProfileId
     * @return
     */
    @Deprecated
    public static ItemEntity newInstance(
            String name,
            Double price,
            TaxEnum taxed,
            int sequence,
            ReceiptEntity receipt,
            String userProfileId
    ) {
        return new ItemEntity(name, price, taxed, sequence, receipt, userProfileId);
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

    /**
     * Paid tax on an item
     *
     * @return
     */
    public Double getTax() {
        return tax;
    }

    /**
     * Set computed tax for the item
     *
     * @param tax
     */
    public void setTax(Double tax) {
        this.tax = tax;
    }

    @NumberFormat (style = Style.CURRENCY)
    @Transient
    public BigDecimal getTotalTax() {
        return Maths.multiply(tax, quantity);
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @NumberFormat (style = Style.CURRENCY)
    @Transient
    public BigDecimal getTotalPriceWithoutTax() {
        return Maths.multiply(price, quantity);
    }

    @NumberFormat (style = Style.CURRENCY)
    @Transient
    public BigDecimal getTotalPriceWithTax() {
        return Maths.add(getTotalPriceWithoutTax(), getTotalTax());
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

    public ReceiptEntity getReceipt() {
        return this.receipt;
    }

    public void setReceipt(ReceiptEntity receipt) {
        this.receipt = receipt;
        if (receipt != null && receipt.getReceiptDate() != null) {
            //receipt null during reflection when just one of the field is populated
            this.receiptDate = receipt.getReceiptDate();
        }
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
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
        return "ItemEntity [name=" + name + ", price=" + price + ", taxed=" + taxed + ", receipt=" + receipt + "]";
    }
}
