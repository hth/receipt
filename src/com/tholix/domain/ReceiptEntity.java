/**
 * 
 */
package com.tholix.domain;

import java.util.Date;

import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * @author hitender
 * @when Dec 26, 2012 12:09:01 AM
 * 
 */
@Document(collection = "RECEIPT")
@CompoundIndexes({ @CompoundIndex(name = "user_receipt_idx", def = "{'receiptDate': -1, 'user': 1}") })
public class ReceiptEntity extends BaseEntity {
	private static final long serialVersionUID = -7218588762395325831L;

	@Size(min = 1, max = 128)
	private String title;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date receiptDate;

	@NumberFormat(style = Style.CURRENCY)
	private Double total;

	@NumberFormat(style = Style.CURRENCY)
	private Double tax;

	@DBRef
	private UserEntity user;

	private ReceiptEntity(String title, Date receiptDate, Double total, Double tax, UserEntity user) {
		super();
		this.title = title;
		this.receiptDate = receiptDate;
		this.total = total;
		this.tax = tax;
		this.user = user;
		
	}
	
	/**
	 * This method is used when the Entity is created for the first time. 
	 * 
	 * @param title
	 * @param receiptDate
	 * @param total
	 * @param tax
	 * @param user
	 * @return
	 */
	public static ReceiptEntity newInstance(String title, Date receiptDate, Double total, Double tax, UserEntity user) {
		return new ReceiptEntity(title, receiptDate, total, tax, user);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
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

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}
