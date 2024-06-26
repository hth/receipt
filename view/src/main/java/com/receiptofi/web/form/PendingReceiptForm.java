package com.receiptofi.web.form;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.web.helper.ReceiptOCRHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 6/15/13
 * Time: 11:15 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class PendingReceiptForm {

    List<ReceiptOCRHelper> pending = new ArrayList<>();
    List<ReceiptOCRHelper> rejected = new ArrayList<>();

    private PendingReceiptForm() {
    }

    public static PendingReceiptForm newInstance() {
        return new PendingReceiptForm();
    }

    public List<ReceiptOCRHelper> getPending() {
        return pending;
    }

    public void addPending(String fileName, long fileSize, DocumentEntity documentEntity) {
        this.pending.add(ReceiptOCRHelper.newInstance(fileName, fileSize, documentEntity));
    }

    public List<ReceiptOCRHelper> getRejected() {
        return rejected;
    }

    public void addRejected(String fileName, long fileSize, DocumentEntity documentEntity) {
        this.rejected.add(ReceiptOCRHelper.newInstance(fileName, fileSize, documentEntity));
    }
}
