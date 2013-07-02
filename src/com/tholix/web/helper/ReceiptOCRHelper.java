package com.tholix.web.helper;

import com.tholix.domain.ReceiptEntityOCR;

/**
 * User: hitender
 * Date: 7/2/13
 * Time: 1:55 AM
 */
public final class ReceiptOCRHelper {
    private String fileName;
    private long fileSize;
    private ReceiptEntityOCR receiptEntityOCR;

    private ReceiptOCRHelper() {}

    public static ReceiptOCRHelper newInstance(String fileName, long fileSize, ReceiptEntityOCR receiptEntityOCR) {
        ReceiptOCRHelper receiptOCRHelper = new ReceiptOCRHelper();
        receiptOCRHelper.setFileName(fileName);
        receiptOCRHelper.setFileSize(fileSize);
        receiptOCRHelper.setReceiptEntityOCR(receiptEntityOCR);
        return receiptOCRHelper;
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
