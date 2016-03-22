package com.receiptofi.service;

import static junit.framework.Assert.assertEquals;

import com.receiptofi.IntegrationTests;
import com.receiptofi.LoadProperties;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.repository.*;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Date;

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
    private DocumentService documentService;
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
    private DocumentManager documentManager;
    private ExpenseTagManager expenseTagManager;
    private UserAccountManager userAccountManager;
    private UserAuthenticationManager userAuthenticationManager;
    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;
    private ForgotRecoverManager forgotRecoverManager;
    private GenerateUserIdManager generateUserIdManager;
    private EmailValidateManager emailValidateManager;
    private EmailValidateService emailValidateService;

    private BillingAccountManager billingAccountManager;
    private BillingHistoryManager billingHistoryManager;
    private PaymentGatewayService paymentGatewayService;
    private BillingService billingService;

    private JmsTemplate jmsTemplate;
    private CommentManager commentManager;

    private RegistrationService registrationService;
    private CloudFileManager cloudFileManager;
    private NotificationManager notificationManager;

    private UserProfilePreferenceService userProfilePreferenceService;
    private FriendManager friendManager;
    private SplitExpensesManager splitExpenseManager;

    private ExternalService externalService;
    private BizStoreManager bizStoreManager;

    private BizService bizService;
    private ReceiptService receiptService;

    @Before
    public void setup() throws IOException {
        LoadProperties loadProperties = new LoadProperties();
        loadProperties.setUp();
        Assert.notNull(loadProperties.getProp().getProperty("google-server-api-key"));

        bizNameManager = new BizNameManagerImpl(getMongoTemplate());
        itemManager = new ItemManagerImpl(bizNameManager, getMongoTemplate());
        fileSystemManager = new FileSystemManagerImpl(getMongoTemplate());
        storageManager = new StorageManagerImpl(getDB());
        documentManager = new DocumentManagerImpl(getMongoTemplate());
        itemOCRManager = new ItemOCRManagerImpl(getMongoTemplate());
        expenseTagManager = new ExpenseTagManagerImpl(getMongoTemplate());

        userAccountManager = new UserAccountManagerImpl(getMongoTemplate());
        userAuthenticationManager = new UserAuthenticationManagerImpl(getMongoTemplate());
        userProfileManager = new UserProfileManagerImpl(getMongoTemplate());
        userPreferenceManager = new UserPreferenceManagerImpl(getMongoTemplate());
        forgotRecoverManager = new ForgotRecoverManagerImpl(getMongoTemplate());
        generateUserIdManager = new GenerateUserIdManagerImpl(getMongoTemplate());
        emailValidateManager = new EmailValidateManagerImpl(getMongoTemplate());
        emailValidateService = new EmailValidateService(emailValidateManager);
        registrationService = new RegistrationService(
                Boolean.getBoolean(loadProperties.getProp().getProperty("registration.turned.on")),
                "/open/login.htm"
        );
        expensesService = new ExpensesService(expenseTagManager, receiptManager, itemManager);

        billingAccountManager = new BillingAccountManagerImpl(getMongoTemplate());
        billingHistoryManager = new BillingHistoryManagerImpl(getMongoTemplate());

        paymentGatewayService = new PaymentGatewayService(
                loadProperties.getProp().getProperty("braintree.environment"),
                loadProperties.getProp().getProperty("braintree.merchant_id"),
                loadProperties.getProp().getProperty("braintree.public_key"),
                loadProperties.getProp().getProperty("braintree.private_key")
        );
        billingService = new BillingService(
                userAccountManager,
                billingAccountManager,
                billingHistoryManager,
                paymentGatewayService
        );

        receiptManager = new ReceiptManagerImpl(itemManager, fileSystemManager, storageManager, getMongoTemplate());
        documentService = new DocumentService(documentManager, itemOCRManager);
        itemService = new ItemService(itemManager, expenseTagManager);
        accountService = new AccountService(
                userAccountManager,
                userAuthenticationManager,
                userProfileManager,
                userPreferenceManager,
                forgotRecoverManager,
                generateUserIdManager,
                emailValidateService,
                registrationService,
                expensesService,
                billingService,
                notificationService
        );

        jmsTemplate = new JmsTemplate();
        senderJMS = new FileUploadDocumentSenderJMS("", jmsTemplate);

        commentManager = new CommentManagerImpl(getMongoTemplate());
        commentService = new CommentService(commentManager);
        fileSystemService = new FileSystemService(fileSystemManager);

        cloudFileManager = new CloudFileManagerImpl(getMongoTemplate());
        cloudFileService = new CloudFileService(cloudFileManager);

        notificationManager = new NotificationManagerImpl(getMongoTemplate());
        notificationService = new NotificationService(notificationManager);

        friendManager = new FriendManagerImpl(getMongoTemplate());
        userProfilePreferenceService = new UserProfilePreferenceService(userProfileManager, userPreferenceManager);
        friendService = new FriendService(10, 0, friendManager, userProfilePreferenceService);

        splitExpenseManager = new SplitExpensesManagerImpl(getMongoTemplate());
        splitExpensesService = new SplitExpensesService(splitExpenseManager, userProfilePreferenceService);

        receiptService = new ReceiptService(
                receiptManager,
                documentService,
                itemService,
                itemOCRManager,
                accountService,
                senderJMS,
                commentService,
                fileSystemService,
                cloudFileService,
                expensesService,
                notificationService,
                friendService,
                splitExpensesService);

        externalService = new ExternalService(loadProperties.getProp().getProperty("google-server-api-key"));
        bizStoreManager = new BizStoreManagerImpl(getMongoTemplate());
        bizService = new BizService(bizNameManager, bizStoreManager, externalService);
    }

    @Test
    public void testDeleteReceipt() throws Exception {
        ReceiptEntity receipt = populateReceipt();
        bizService.saveNewBusinessAndOrStore(receipt);
        receiptManager.save(receipt);
        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());

        assertEquals("Deleted receipt", 0, receiptManager.collectionSize());
    }

    private ReceiptEntity populateReceipt() {
        ReceiptEntity receiptEntity = new ReceiptEntity();
        receiptEntity.setReceiptUserId("10000000001");
        receiptEntity.setTotal(1.0);
        receiptEntity.setReceiptDate(new Date());

        BizNameEntity bizNameEntity = BizNameEntity.newInstance();
        bizNameEntity.setBusinessName("Costco");
        receiptEntity.setBizName(bizNameEntity);

        BizStoreEntity bizStoreEntity = BizStoreEntity.newInstance();
        bizStoreEntity.setAddress("150 Lawrence Station Rd, Sunnyvale, CA 94086");
        bizStoreEntity.setPhone("(408) 730-1892");
        receiptEntity.setBizStore(bizStoreEntity);

        return receiptEntity;
    }
}
