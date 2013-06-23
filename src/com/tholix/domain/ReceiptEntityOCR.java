/**
 *
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * @author hitender
 * @since Jan 6, 2013 1:04:43 PM
 *
 */
@Document(collection = "RECEIPT_OCR")
@CompoundIndexes({ @CompoundIndex(name = "user_receipt_ocr_idx", def = "{'receiptBlobId': 1, 'userProfileId': 1}") })
public class ReceiptEntityOCR extends BaseEntity {
	private static final long serialVersionUID = 5258538763598321136L;

	@NotNull
	private ReceiptStatusEnum receiptStatus;

	@NotNull
	private String receiptBlobId;

	@NotNull
	private String receiptDate;

    @Transient
    private String subTotal;

	@NotNull
	private String total;

	@NotNull
	private String tax = "0.00";

	@NotNull
	private String userProfileId;

	@NotNull
	private String receiptOCRTranslation;

    @DBRef
    private BizNameEntity bizName;

    @DBRef
    private BizStoreEntity bizStore;

    private String receiptId;

    @DBRef
    private CommentEntity comment;

    /** To keep bean happy */
	public ReceiptEntityOCR() {}

	private ReceiptEntityOCR(String receiptDate, String total, String tax) {
		super();
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
	}

	private ReceiptEntityOCR(ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId, String receiptOCRTranslation) {
		super();
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;
		this.receiptOCRTranslation = receiptOCRTranslation;
	}

	private ReceiptEntityOCR(String receiptDate, String total, String tax, ReceiptStatusEnum receiptStatus, String receiptBlobId,
			String userProfileId) {
		super();
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 *
	 * @param receiptDate
	 * @param total
	 * @param tax
	 * @return
	 */
	public static ReceiptEntityOCR updateInstance(String receiptDate, String total, String tax) {
		return new ReceiptEntityOCR(receiptDate, total, tax);
	}

	public static ReceiptEntityOCR newInstance(ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId, String receiptOCRTranslation) {
		return new ReceiptEntityOCR(receiptStatus, receiptBlobId, userProfileId, receiptOCRTranslation);
	}

	public static ReceiptEntityOCR newInstance(String receiptDate, String total, String tax, ReceiptStatusEnum receiptStatus,
			String receiptBlobId, String userProfileId) {
		return new ReceiptEntityOCR(receiptDate, total, tax, receiptStatus, receiptBlobId, userProfileId);
	}

	public static ReceiptEntityOCR newInstance() {
		return new ReceiptEntityOCR();
	}

	public ReceiptStatusEnum getReceiptStatus() {
		return receiptStatus;
	}

	public void setReceiptStatus(ReceiptStatusEnum receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	public String getReceiptBlobId() {
		return receiptBlobId;
	}

	public void setReceiptBlobId(String receiptBlobId) {
		this.receiptBlobId = receiptBlobId;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getReceiptOCRTranslation() {
		return receiptOCRTranslation;
	}

	public void setReceiptOCRTranslation(String receiptOCRTranslation) {
		this.receiptOCRTranslation = receiptOCRTranslation;
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

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public CommentEntity getComment() {
        return comment;
    }

    public void setComment(CommentEntity comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return Objects.toString(this);
    }
}
