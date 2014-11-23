package com.receiptofi.domain.value;

import com.receiptofi.domain.types.DocumentStatusEnum;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: hitender
 * Date: 11/20/14 1:20 AM
 */
public class DocumentGrouped {
    private Date day;
    private DocumentStatusEnum documentStatusEnum;

    @SuppressWarnings("unused")
    private DocumentGrouped() {
    }

    private DocumentGrouped(Date day, DocumentStatusEnum documentStatusEnum) {
        this.day = day;
        this.documentStatusEnum = documentStatusEnum;
    }

    public static DocumentGrouped newInstance(Date day, DocumentStatusEnum documentStatusEnum) {
        return new DocumentGrouped(day, documentStatusEnum);
    }

    public Date getDay() {
        return day;
    }

    public DocumentStatusEnum getDocumentStatusEnum() {
        return documentStatusEnum;
    }
}
