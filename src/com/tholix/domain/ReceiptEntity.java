/**
 *
 */
package com.tholix.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import org.joda.time.DateTime;

import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * @author hitender
 * @when Dec 26, 2012 12:09:01 AM
 *
 */
@Document(collection = "RECEIPT")
@CompoundIndexes({ @CompoundIndex(name = "user_receipt_idx", def = "{'receiptBlobId': 1, 'userProfileId': 1}") })
public class ReceiptEntity extends BaseEntity {
	private static final long serialVersionUID = -7218588762395325831L;

	/**
	 * Description is provided by the user. This can be empty.
	 */
	@Size(min = 0, max = 128)
	private String description;

    //TODO substitute with location.name
	@NotNull
	@Size(min = 1, max = 128)
	private String title;

	@NotNull
	private ReceiptStatusEnum receiptStatus;

	@NotNull
	private String receiptBlobId;

	@NotNull
	private Date receiptDate;

	@NotNull
	private int year;

	@NotNull
	private int month;

	@NotNull
	private int day;

	@NotNull
	@NumberFormat(style = Style.CURRENCY)
	private Double total;

	@NotNull
	@NumberFormat(style = Style.CURRENCY)
	private Double tax = 0.00;

	@NotNull
	private String userProfileId;

    @DBRef
    private Location location;

	public ReceiptEntity() {

	}

	private ReceiptEntity(String title, Date receiptDate, Double total, Double tax) {
		super();
		this.title = title;
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
	}

	private ReceiptEntity(String description, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId) {
		super();
		this.description = description;
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;
	}

	private ReceiptEntity(String title, Date receiptDate, Double total, Double tax, String description, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId) {
		super();
		this.title = title;
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
		this.description = description;
		this.receiptStatus = receiptStatus;
		this.receiptBlobId = receiptBlobId;
		this.userProfileId = userProfileId;

		DateTime dt = new DateTime(receiptDate);
		this.year = dt.getYear();
		this.month = dt.getMonthOfYear();
		this.day = dt.getDayOfMonth();
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
	public static ReceiptEntity updateInstance(String title, Date receiptDate, Double total, Double tax) {
		return new ReceiptEntity(title, receiptDate, total, tax);
	}

	public static ReceiptEntity newInstance(String description, ReceiptStatusEnum receiptStatus, String receiptBlobId, String userProfileId) {
		return new ReceiptEntity(description, receiptStatus, receiptBlobId, userProfileId);
	}

	/**
	 * Use this method to create the Entity for OCR Entity
	 *
	 * @param title
	 * @param receiptDate
	 * @param total
	 * @param tax
	 * @param description
	 * @param receiptStatus
	 * @param receiptBlobId
	 * @param userProfileId
	 * @return
	 */
	public static ReceiptEntity newInstance(String title, Date receiptDate, Double total, Double tax, String description, ReceiptStatusEnum receiptStatus, String receiptBlobId,
			String userProfileId) {
		return new ReceiptEntity(title, receiptDate, total, tax, description, receiptStatus, receiptBlobId, userProfileId);
	}

	public static ReceiptEntity newInstance() {
		return new ReceiptEntity();
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

	@DateTimeFormat(iso = ISO.NONE)
	public Date getReceiptDate() {
		return receiptDate;
	}

	@DateTimeFormat(iso = ISO.DATE_TIME)
	public void setReceiptDate(Date receiptDate) {
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

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

	@Override
	public String toString() {
		return "ReceiptEntity [title=" + title + ", receiptDate=" + receiptDate + ", year=" + year + ", month=" + month
				+ ", day=" + day + ", total=" + total + ", tax=" + tax
				+ "]";
	}


}
