package com.receiptofi.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.DeviceTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.MessageDocumentManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * User: hitender
 * Date: 3/8/16 7:07 PM
 */
public class DocumentUpdateServiceTest {
    @Mock private DocumentManager documentManager;
    @Mock private ItemOCRManager itemOCRManager;
    @Mock private ReceiptManager receiptManager;
    @Mock private ItemManager itemManager;
    @Mock private MessageDocumentManager messageDocumentManager;
    @Mock private BizService bizService;
    @Mock private UserProfilePreferenceService userProfilePreferenceService;
    @Mock private CommentService commentService;
    @Mock private NotificationService notificationService;
    @Mock private StorageManager storageManager;
    @Mock private FileSystemService fileSystemService;
    @Mock private MileageService mileageService;
    @Mock private BillingService billingService;
    @Mock private ExpensesService expensesService;

    @Mock private ReceiptEntity receipt;
    @Mock private DocumentEntity document;
    @Mock private BizNameEntity bizName;

    private DocumentUpdateService documentUpdateService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        documentUpdateService = new DocumentUpdateService(
                documentManager,
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
                expensesService
        );
    }


    @Test
    public void testProcessDocumentForReceipt() throws Exception {
        when(documentManager.findActiveOne(anyString())).thenReturn(document);
        when(receipt.getBizName()).thenReturn(bizName);
        documentUpdateService.processDocumentForReceipt("technicianId", receipt, new ArrayList<>(), document);
        verify(notificationService, atLeastOnce()).addNotification(
                anyString(),
                Matchers.any(NotificationTypeEnum.class),
                Matchers.any(NotificationGroupEnum.class),
                any(ReceiptEntity.class));
    }

    @Test
    public void testProcessDocumentReceiptReCheck() throws Exception {

    }

    @Test
    public void testProcessDocumentForReject() throws Exception {

    }
}