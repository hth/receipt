/**
 *
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import org.joda.time.DateTime;

import com.tholix.domain.types.ReceiptOfEnum;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.utils.SHAHashing;

/**
 * @author hitender
 * @since Dec 26, 2012 12:09:01 AM
 *
 */
@Document(collection = "RECEIPT")
@CompoundIndexes(value = {
        @CompoundIndex(name = "user_receipt_idx",           def = "{'RECEIPT_BLOB_ID': -1, 'USER_PROFILE_ID': -1}"),
        @CompoundIndex(name = "user_receipt_unique_idx",    def = "{'CHECK_SUM': -1}", unique = true)
} )
public class ReceiptEntity extends BaseEntity {
	private static final long serialVersionUID = -7218588762395325831L;

	@NotNull
    @Field("RECEIPT_STATUS_ENUM")
	private ReceiptStatusEnum receiptStatus;

    @NotNull
    @Field("RECEIPT_OF_ENUM")
    private ReceiptOfEnum receiptOf;

	@NotNull
    @Field("RECEIPT_BLOB_ID")
	private String receiptBlobId;

    @NotNull
    @Field("RECEIPT_SCALED_BLOB_ID")
    private String receiptScaledBlobId;

	@NotNull
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Field("RECEIPT_DATE")
	private Date receiptDate;

	@NotNull
    @Field("YEAR")
	private int year;

	@NotNull
    @Field("MONTH")
	private int month;

	@NotNull
    @Field("DAY")
	private int day;

	@NotNull
	@NumberFormat(style = Style.CURRENCY)
    @Field("TOTAL")
	private Double total;

	@NotNull
	@NumberFormat(style = Style.CURRENCY)
    @Field("TAX")
	private Double tax = 0.00;

    @NotNull
    @NumberFormat(style = Style.PERCENT)
    @Field("PERCENT_TAX")
    private String percentTax;

	@NotNull
    @Field("USER_PROFILE_ID")
	private String userProfileId;

    @DBRef
    @Field("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field("BIZ_STORE")
    private BizStoreEntity bizStore;

    @NotNull
    @Field("RECEIPT_OCR_ID")
    private String receiptOCRId;

    @DBRef
    @Field("COMMENT_RECHECK")
    private CommentEntity recheckComment;

    @DBRef
    @Field("COMMENT_NOTES")
    private CommentEntity notes;

    /**
     * Used to flush or avoid duplicate receipt entry
     */
    @Field("CHECK_SUM")
    private String checkSum;

    /** To keep bean happy */
	public ReceiptEntity() {}

    @Deprecated
	private ReceiptEntity(Date receiptDate, Double total, Double tax, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId) {
		super();
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;
	}

	/**
	 * Use this method to create the Entity for OCR Entity
	 *
	 * @param receiptDate
	 * @param total
	 * @param tax
	 * @param receiptStatus
	 * @param receiptBlobId
	 * @param userProfileId
	 * @return
	 */
    @Deprecated
	public static ReceiptEntity newInstance(Date receiptDate, Double total, Double tax, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId) {
		return new ReceiptEntity(receiptDate, total, tax, receiptStatus, receiptBlobId, userProfileId);
	}

	public static ReceiptEntity newInstance() {
		return new ReceiptEntity();
	}

	public ReceiptStatusEnum getReceiptStatus() {
		return receiptStatus;
	}

	public void setReceiptStatus(ReceiptStatusEnum receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

    public ReceiptOfEnum getReceiptOf() {
        return receiptOf;
    }

    public void setReceiptOf(ReceiptOfEnum receiptOf) {
        this.receiptOf = receiptOf;
    }

	public String getReceiptBlobId() {
		return receiptBlobId;
	}

	public void setReceiptBlobId(String receiptBlobId) {
		this.receiptBlobId = receiptBlobId;
	}

    public String getReceiptScaledBlobId() {
        return receiptScaledBlobId;
    }

    public void setReceiptScaledBlobId(String receiptScaledBlobId) {
        this.receiptScaledBlobId = receiptScaledBlobId;
    }

    public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
        DateTime dt = new DateTime(receiptDate);
        this.year = dt.getYear();
        this.month = dt.getMonthOfYear();
        this.day = dt.getDayOfMonth();
		this.receiptDate = receiptDate;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

    /**
     * Used to show the value in notification
     *
     * @return
     */
    @Transient
    public String getTotalString() {
        //TODO try using JODA currency
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance();
        return currencyFormatter.format(getTotal());
    }

	public Double getTax() {
		return tax;
	}

    public void setTax(Double tax) {
		this.tax = tax;
	}

    /**
     * Percentage of tax paid for all the items that were taxed
     *
     * @return
     */
    public String getPercentTax() {
        return percentTax;
    }

    /**
     * Percentage of tax paid for all the items that were taxed. Scaled till 6th value
     * @param percentTax - 0.666667
     */
    public void setPercentTax(String percentTax) {
        this.percentTax = percentTax;
    }

    /**
     * Used for displaying on receipt page till 4th decimal. 0.666
     *
     * @return
     */
    @Transient
    public String getPercentTax4Display() {
        return StringUtils.substring(this.percentTax, 0, 5);
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

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public void setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
    }

    public String getReceiptOCRId() {
        return receiptOCRId;
    }

    public void setReceiptOCRId(String receiptOCRId) {
        this.receiptOCRId = receiptOCRId;
    }

    public CommentEntity getRecheckComment() {
        return recheckComment;
    }

    public void setRecheckComment(CommentEntity recheckComment) {
        this.recheckComment = recheckComment;
    }

    public CommentEntity getNotes() {
        return notes;
    }

    public void setNotes(CommentEntity notes) {
        this.notes = notes;
    }

    public String getCheckSum() {
        return checkSum;
    }

    /**
     * Create for making sure no duplicate receipt could be entered for
     */
    public void checkSum() {
        this.checkSum = SHAHashing.calculateCheckSum(userProfileId, receiptDate, total, isDeleted());
    }

    @Override
    public String toString() {
        return Objects.toString(this);
    }
}
