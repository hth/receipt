package com.receiptofi.service;

import com.receiptofi.IntegrationTests;
import com.receiptofi.LoadProperties;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.repository.BizNameManagerImpl;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.DocumentManagerImpl;
import com.receiptofi.repository.FileSystemManager;
import com.receiptofi.repository.FileSystemManagerImpl;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemManagerImpl;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.ReceiptManagerImpl;
import com.receiptofi.repository.StorageManager;
import com.receiptofi.repository.StorageManagerImpl;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;

import org.junit.Before;
import org.junit.experimental.categories.Category;

/**
 * User: hitender
 * Date: 3/20/16 1:00 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Category (IntegrationTests.class)
public class ReceiptServiceITest extends RealMongoForTests {

    private ReceiptManager receiptManager;
    private DocumentManager documentManager;
    private DocumentUpdateService documentUpdateService;
    private ItemService itemService;
    private ItemOCRManager itemOCRManager;
    private AccountService accountService;
    private FileUploadDocumentSenderJMS senderJMS;
    private CommentService commentService;
    private FileSystemService fileSystemService;
    private CloudFileService cloudFileService;
    private ExpensesService expensesService;
    private NotificationService notificationService;
    private FriendService friendService;
    private SplitExpensesService splitExpensesService;

    private BizNameManager bizNameManager;
    private ItemManager itemManager;
    private FileSystemManager fileSystemManager;
    private StorageManager storageManager;

    @Before
    public void setup() {
        LoadProperties loadProperties = new LoadProperties();

        bizNameManager = new BizNameManagerImpl(getMongoTemplate());
        itemManager = new ItemManagerImpl(bizNameManager, getMongoTemplate());
        fileSystemManager = new FileSystemManagerImpl(getMongoTemplate());
        storageManager = new StorageManagerImpl(getDB());

        receiptManager = new ReceiptManagerImpl(itemManager, fileSystemManager, storageManager, getMongoTemplate());
        documentManager = new DocumentManagerImpl(getMongoTemplate());

    }
}
