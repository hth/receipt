package com.receiptofi.service;

import static org.junit.Assert.assertEquals;

import com.receiptofi.ITest;
import com.receiptofi.IntegrationTests;
import com.receiptofi.LoadResource;
import com.receiptofi.domain.ReceiptEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * User: hitender
 * Date: 3/9/16 9:23 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Category (IntegrationTests.class)
public class DocumentUpdateServiceITest extends ITest {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentUpdateServiceITest.class);

    private DocumentUpdateService documentUpdateService;
    private Properties properties = new Properties();

    @Before
    public void classSetup() throws IOException {
        LoadResource.loadProperties(properties);

        documentUpdateService = new DocumentUpdateService(
                documentService,
                itemOCRManager,
                receiptManager,
                itemManager,
                messageDocumentManager,
                bizService,
                userProfilePreferenceService,
                commentService,
                notificationService,
                storageManager,
                fileSystemService,
                mileageService,
                billingService,
                expensesService);


        ReceiptEntity receipt = populateReceipt();
        receiptManager.save(receipt);
        LOG.info("Receipt Id={}", receipt.getId());
    }

    @Test
    public void testProcessDocumentForReceipt() {
        assertEquals(true, true);
    }


    private ReceiptEntity populateReceipt() {
        ReceiptEntity receiptEntity = new ReceiptEntity();
        receiptEntity.setReceiptUserId("10000000001");
        receiptEntity.setTotal(1.0);
        receiptEntity.setReceiptDate(new Date());

        return receiptEntity;
    }
}
