/**
 *
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.tholix.domain.types.TaxEnum;
import com.tholix.utils.Maths;

/**
 * Represents each individual item on a receipt.
 *
 * @author hitender
 * @since Dec 25, 2012 11:43:10 PM
 *
 */
// mongoTemplate.ensureIndex(new Index().on("lastName",Order.ASCENDING), Customer.class);
@Document(collection = "ITEM")
@CompoundIndexes({ @CompoundIndex(name = "user_item_idx", def = "{'RECEIPT': -1, 'USER_PROFILE_ID': 1}") })
public class ItemEntity extends BaseEntity {
	private static final long serialVersionUID = 1031429034359059354L;

	@Size(min = 1, max = 128)
    @Field("NAME")
	private String name;

	@NumberFormat(style = Style.CURRENCY)
    @Field("PRICE")
	private Double price;

    /**
     * Paid tax on an item
     */
    @NumberFormat(style = Style.CURRENCY)
    @Field("TAX")
    private Double tax;

    @Field("QUANTITY")
    private int quantity = 1;

	@NotNull
    @Field("TAX_ENUM")
	private TaxEnum taxed = TaxEnum.NOT_TAXED;

	@NotNull
    @Field("SEQUENCE")
	private int sequence;

	@NotNull
    @Field("USER_PROFILE_ID")
	private String userProfileId;

    @DBRef
    @Field("RECEIPT")
    private ReceiptEntity receipt;

    @DBRef
    @Field("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field("EXPENSE_TYPE")
    private ExpenseTypeEntity expenseType;

	public ItemEntity() {}

    public static ItemEntity newInstance() {
        return new ItemEntity();
    }

	private ItemEntity(String name, Double price, TaxEnum taxed, int sequence, ReceiptEntity receipt, String userProfileId) {
		super();
		this.name = name;
		this.price = price;
		this.taxed = taxed;
		this.receipt = receipt;
		this.userProfileId = userProfileId;
		this.sequence = sequence;
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
	public static ItemEntity newInstance(String name, Double price, TaxEnum taxed, int sequence, ReceiptEntity receipt, String userProfileId) {
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
     * @return
     */
    public Double getTax() {
        return tax;
    }

    /**
     * Set computed tax for the item
     * @param tax
     */
    public void setTax(Double tax) {
        this.tax = tax;
    }

    @NumberFormat(style = Style.CURRENCY)
    @Transient
    public BigDecimal getTotalTax() {
        return Maths.multiply(tax, quantity);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @NumberFormat(style = Style.CURRENCY)
    @Transient
    public BigDecimal getTotalPriceWithoutTax() {
        return Maths.multiply(price, quantity);
    }

    @NumberFormat(style = Style.CURRENCY)
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
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public ExpenseTypeEntity getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseTypeEntity expenseType) {
        this.expenseType = expenseType;
    }

    @Override
	public String toString() {
		return "ItemEntity [name=" + name + ", price=" + price + ", taxed=" + taxed + "]";
	}
}
