/**
 *
 */
package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.ReceiptOfEnum;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author hitender
 * @since Jan 6, 2013 1:04:43 PM
 *
 */
@Document(collection = "DOCUMENT")
@CompoundIndexes({ @CompoundIndex(name = "receipt_ocr_idx", def = "{'RECEIPT_BLOB_ID': 1, 'USER_PROFILE_ID': 1}") })
public class DocumentEntity extends DocumentBaseEntity {
	private static final long serialVersionUID = 5258538763598321136L;

	@NotNull
    @Field("DOCUMENT_STATUS_ENUM")
	private DocumentStatusEnum documentStatus;

    @NotNull
    @Field("RECEIPT_OF_ENUM")
    private ReceiptOfEnum receiptOf;

    @DBRef
    @Field("RECEIPT_BLOB_ID")
	private Collection<FileSystemEntity> receiptBlobId;

	@NotNull
    @Field("RECEIPT_DATE")
	private String receiptDate;

    @Transient
    @Field("SUB_TOTAL")
    private String subTotal;

	@NotNull
    @Field("TOTAL")
	private String total;

	@NotNull
    @Field("TAX")
	private String tax = "0.00";

	@NotNull
    @Field("USER_PROFILE_ID")
	private String userProfileId;

	@NotNull
    @Field("OCR_TRANSLATION")
	private String receiptOCRTranslation;

    @DBRef
    @Field("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field("BIZ_STORE")
    private BizStoreEntity bizStore;

    @Field("RECEIPT_ID")
    private String receiptId;

    @DBRef
    @Field("COMMENT_RECHECK")
    private CommentEntity recheckComment;

    @DBRef
    @Field("COMMENT_NOTES")
    private CommentEntity notes;

    @NotNull
    @Field("ORIENTATION")
    private int imageOrientation = 0;

    /** To keep bean happy */
	public DocumentEntity() {}

	public static DocumentEntity newInstance() {
		return new DocumentEntity();
	}

	public DocumentStatusEnum getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(DocumentStatusEnum documentStatus) {
		this.documentStatus = documentStatus;
	}

    public ReceiptOfEnum getReceiptOf() {
        return receiptOf;
    }

    public void setReceiptOf(ReceiptOfEnum receiptOf) {
        this.receiptOf = receiptOf;
    }

    public Collection<FileSystemEntity> getReceiptBlobId() {
		return receiptBlobId;
	}

	public void addReceiptBlobId(FileSystemEntity receiptBlobId) {
        if(this.receiptBlobId == null) {
            this.receiptBlobId = new ArrayList<>();
        }
		this.receiptBlobId.add(receiptBlobId);
	}

    public void setReceiptBlobId(Collection<FileSystemEntity> receiptBlobId) {
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

    public int getImageOrientation() {
        return imageOrientation;
    }

    @Deprecated
    public void setImageOrientation(int imageOrientation) {
        this.imageOrientation = imageOrientation;
    }

    @Override
    public String toString() {
        return Objects.toString(this);
    }
}
