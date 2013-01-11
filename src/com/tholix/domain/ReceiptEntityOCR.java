/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * @author hitender
 * @when Jan 6, 2013 1:04:43 PM
 * 
 */
@Document(collection = "RECEIPT_OCR")
@CompoundIndexes({ @CompoundIndex(name = "user_receipt_ocr_idx", def = "{'receiptBlobId': 1, 'userProfileId': 1}") })
public class ReceiptEntityOCR extends BaseEntity {
	private static final long serialVersionUID = 5258538763598321136L;

	/**
	 * Description is provided by the user. Description can be empty.
	 */
	@Size(min = 0, max = 128)
	private String description;

	@NotNull
	@Size(min = 1, max = 128)
	private String title;

	@NotNull
	private ReceiptStatusEnum receiptStatus;

	@NotNull
	private String receiptBlobId;

	@NotNull
	private String receiptDate;

	@NotNull
	private String total;

	@NotNull
	private String tax = "0.00";

	@NotNull
	private String userProfileId;

	@NotNull
	private String receiptOCRTranslation;

	public ReceiptEntityOCR() {

	}

	private ReceiptEntityOCR(String title, String receiptDate, String total, String tax) {
		super();
		this.title = title;
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
	}

	private ReceiptEntityOCR(String description, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId, String receiptOCRTranslation) {
		super();
		this.description = description;
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;
		this.receiptOCRTranslation = receiptOCRTranslation;
	}

	private ReceiptEntityOCR(String title, String receiptDate, String total, String tax, String description, ReceiptStatusEnum receiptStatus, String receiptBlobId,
			String userProfileId) {
		super();
		this.title = title;
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
		this.description = description;
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 * 
	 * @param title
	 * @param receiptDate
	 * @param total
	 * @param tax
	 * @return
	 */
	public static ReceiptEntityOCR updateInstance(String title, String receiptDate, String total, String tax) {
		return new ReceiptEntityOCR(title, receiptDate, total, tax);
	}

	public static ReceiptEntityOCR newInstance(String description, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId, String receiptOCRTranslation) {
		return new ReceiptEntityOCR(description, receiptStatus, receiptBlobId, userProfileId, receiptOCRTranslation);
	}

	public static ReceiptEntityOCR newInstance(String title, String receiptDate, String total, String tax, String description, ReceiptStatusEnum receiptStatus,
			String receiptBlobId, String userProfileId) {
		return new ReceiptEntityOCR(title, receiptDate, total, tax, description, receiptStatus, receiptBlobId, userProfileId);
	}

	public static ReceiptEntityOCR newInstance() {
		return new ReceiptEntityOCR();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	@Override
	public String toString() {
		return "ReceiptEntityOCR [description=" + description + ", title=" + title + ", receiptStatus=" + receiptStatus + ", receiptBlobId=" + receiptBlobId + ", receiptDate="
				+ receiptDate + ", total=" + total + ", tax=" + tax + ", userProfileId=" + userProfileId + ", receiptOCRTranslation=" + receiptOCRTranslation + "]";
	}
	
	
}
