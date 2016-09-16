/**
 *
 */
package com.receiptofi.domain;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.receiptofi.domain.types.TaxEnum;
import com.receiptofi.utils.LocaleUtil;
import com.receiptofi.utils.Maths;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents each individual item on a receipt.
 *
 * @author hitender
 * @since Dec 25, 2012 11:43:10 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
// mongoTemplate.ensureIndex(new Index().on("lastName",Order.ASCENDING), Customer.class);
@Document (collection = "ITEM")
@CompoundIndexes ({@CompoundIndex (name = "item_ri_bi_rid_idx", def = "{'RI': -1, 'BI' : -1, 'RID': -1}")})
public class ItemEntity extends BaseEntity {
    private static final int ITEM_NAME_MAX_LENGTH = 26;

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
    private TaxEnum taxed = TaxEnum.NT;

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

    @Field ("RI")
    private String receiptId;

    @Field ("BI")
    private String businessNameId;

    public ItemEntity() {
        super();
    }

    public String getName() {
        return name;
    }

    /**
     * Any name greater than ITEM_NAME_MAX_LENGTH would be abbreviated.
     *
     * @return
     */
    @Transient
    public String getNameAbb() {
        if (name.length() > ITEM_NAME_MAX_LENGTH) {
            return StringUtils.abbreviate(name, ITEM_NAME_MAX_LENGTH);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    @SuppressWarnings("unused")
    @Transient
    public String getPriceString() {
        return LocaleUtil.getNumberFormat(receipt.getCountryShortName()).format(price);
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

    @SuppressWarnings("unused")
    @Transient
    public String getTotalTaxString() {
        return LocaleUtil.getNumberFormat(receipt.getCountryShortName()).format(Maths.multiply(tax, quantity));
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

    @SuppressWarnings("unused")
    @Transient
    public String getTotalPriceWithoutTaxString() {
        return LocaleUtil.getNumberFormat(receipt.getCountryShortName()).format(Maths.multiply(price, quantity));
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
        if (null != receipt && null != receipt.getReceiptDate()) {
            //receipt null during reflection when just one of the field is populated
            this.receiptDate = receipt.getReceiptDate();
            this.receiptId = receipt.getId();
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
        this.businessNameId = bizName.getId();
    }

    public ExpenseTagEntity getExpenseTag() {
        return expenseTag;
    }

    public void setExpenseTag(ExpenseTagEntity expenseTag) {
        this.expenseTag = expenseTag;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getBusinessNameId() {
        return businessNameId;
    }

    @Override
    public String toString() {
        return "ItemEntity [name=" + name + ", price=" + price + ", taxed=" + taxed + ", receipt=" + receipt + "]";
    }
}
