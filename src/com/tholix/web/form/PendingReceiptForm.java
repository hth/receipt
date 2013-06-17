package com.tholix.web.form;

import com.tholix.domain.ReceiptEntityOCR;

/**
 * User: hitender
 * Date: 6/15/13
 * Time: 11:15 PM
 */
public final class PendingReceiptForm {

    private String fileName;
    private long fileSize;
    private ReceiptEntityOCR receiptEntityOCR;

    private PendingReceiptForm() {}

    public static PendingReceiptForm newInstance(String fileName, long fileSize, ReceiptEntityOCR receiptEntityOCR) {
        PendingReceiptForm pendingReceiptForm = new PendingReceiptForm();
        pendingReceiptForm.setFileName(fileName);
        pendingReceiptForm.setFileSize(fileSize);
        pendingReceiptForm.setReceiptEntityOCR(receiptEntityOCR);
        return pendingReceiptForm;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public ReceiptEntityOCR getReceiptEntityOCR() {
        return receiptEntityOCR;
    }

    public void setReceiptEntityOCR(ReceiptEntityOCR receiptEntityOCR) {
        this.receiptEntityOCR = receiptEntityOCR;
    }
}
