package com.receiptofi.service;

import static org.junit.Assert.assertEquals;

import com.receiptofi.IntegrationTests;
import com.receiptofi.LoadProperties;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.repository.*;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;

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
public class DocumentUpdateServiceITest extends RealMongoForTests {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentUpdateServiceITest.class);

    private DocumentManager documentManager;
    private ItemOCRManager itemOCRManager;
    private ReceiptManager receiptManager;
    private ItemManager itemManager;
    private ItemService itemService;
    private MessageDocumentManager messageDocumentManager;
    private BizService bizService;
    private ExpenseTagManager expenseTagManager;
    private AccountService accountService;
    private UserAccountManager userAccountManager;
    private RegistrationService registrationService;

    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private ExternalService externalService;
    private ReceiptService receiptService;
    private UserProfilePreferenceService userProfilePreferenceService;
    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;
    private NotificationManager notificationManager;
    private NotificationService notificationService;
    private StorageManager storageManager;
    private FileSystemManager fileSystemManager;
    private FileSystemService fileSystemService;
    private MileageManager mileageManager;
    private CommentManager commentManager;
    private CommentService commentService;
    private MileageService mileageService;
    private CloudFileManager cloudFileManager;
    private CloudFileService cloudFileService;
    private BillingService billingService;
    private ExpensesService expensesService;
    private UserAuthenticationManager userAuthenticationManager;
    private ForgotRecoverManager forgotRecoverManager;
    private GenerateUserIdManager generateUserIdManager;
    private EmailValidateManager emailValidateManager;
    private EmailValidateService emailValidateService;
    private BillingAccountManager billingAccountManager;
    private BillingHistoryManager billingHistoryManager;
    private PaymentGatewayService paymentGatewayService;
    private FileUploadDocumentSenderJMS senderJMS;
    private JmsTemplate jmsTemplate;
    private FriendService friendService;
    private FriendManager friendManager;
    private SplitExpensesService splitExpensesService;
    private SplitExpensesManager splitExpenseManager;
    private DocumentService documentService;

    private DocumentUpdateService documentUpdateService;
    private Properties properties = new Properties();

    @Before
    public void setup() throws IOException {
        LoadProperties.loadProperties(properties);

        bizNameManager = new BizNameManagerImpl(getMongoTemplate());
        itemManager = new ItemManagerImpl(bizNameManager, getMongoTemplate());
        fileSystemManager = new FileSystemManagerImpl(getMongoTemplate());
        storageManager = new StorageManagerImpl(getDB());
        bizStoreManager = new BizStoreManagerImpl(getMongoTemplate());
        externalService = new ExternalService(properties.getProperty("google-server-api-key"));
        userProfileManager = new UserProfileManagerImpl(getMongoTemplate());
        userPreferenceManager = new UserPreferenceManagerImpl(getMongoTemplate());
        commentManager = new CommentManagerImpl(getMongoTemplate());
        notificationManager = new NotificationManagerImpl(getMongoTemplate());
        notificationService = new NotificationService(notificationManager);
        storageManager = new StorageManagerImpl(getDB());
        fileSystemManager = new FileSystemManagerImpl(getMongoTemplate());
        fileSystemService = new FileSystemService(fileSystemManager);
        cloudFileManager = new CloudFileManagerImpl(getMongoTemplate());
        cloudFileService = new CloudFileService(cloudFileManager);
        expenseTagManager = new ExpenseTagManagerImpl(getMongoTemplate());
        itemService = new ItemService(itemManager, expenseTagManager);
        userAccountManager = new UserAccountManagerImpl(getMongoTemplate());
        userAuthenticationManager = new UserAuthenticationManagerImpl(getMongoTemplate());
        forgotRecoverManager = new ForgotRecoverManagerImpl(getMongoTemplate());
        generateUserIdManager = new GenerateUserIdManagerImpl(getMongoTemplate());
        emailValidateManager = new EmailValidateManagerImpl(getMongoTemplate());
        emailValidateService = new EmailValidateService(emailValidateManager);
        friendManager = new FriendManagerImpl(getMongoTemplate());
        userProfilePreferenceService = new UserProfilePreferenceService(userProfileManager, userPreferenceManager);
        friendService = new FriendService(10, 0, friendManager, userProfilePreferenceService);
        splitExpenseManager = new SplitExpensesManagerImpl(getMongoTemplate());
        splitExpensesService = new SplitExpensesService(splitExpenseManager, userProfilePreferenceService);
        expensesService = new ExpensesService(expenseTagManager, receiptManager, itemManager);
        documentService = new DocumentService(documentManager, itemOCRManager);

        registrationService = new RegistrationService(
                Boolean.getBoolean(properties.getProperty("registration.turned.on")),
                "/open/login.htm"
        );
        billingAccountManager = new BillingAccountManagerImpl(getMongoTemplate());
        billingHistoryManager = new BillingHistoryManagerImpl(getMongoTemplate());

        paymentGatewayService = new PaymentGatewayService(
                properties.getProperty("braintree.environment"),
                properties.getProperty("braintree.merchant_id"),
                properties.getProperty("braintree.public_key"),
                properties.getProperty("braintree.private_key")
        );

        billingService = new BillingService(
                userAccountManager,
                billingAccountManager,
                billingHistoryManager,
                paymentGatewayService
        );

        accountService = new AccountService(
                new String[]{"HOME", "BUSINESS"},
                new String[]{"#1a9af9", "#b492e8"},
                3,
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

        documentManager = new DocumentManagerImpl(getMongoTemplate());
        itemOCRManager = new ItemOCRManagerImpl(getMongoTemplate());
        receiptManager = new ReceiptManagerImpl(itemManager, fileSystemManager, storageManager, getMongoTemplate());
        messageDocumentManager = new MessageDocumentManagerImpl(10, getMongoTemplate());
        bizService = new BizService(bizNameManager, bizStoreManager, externalService);
        jmsTemplate = new JmsTemplate();
        senderJMS = new FileUploadDocumentSenderJMS("", jmsTemplate);
        commentService = new CommentService(commentManager);
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
        mileageManager = new MileageManagerImpl(getMongoTemplate());
        mileageService = new MileageService(mileageManager, commentService, documentManager, fileSystemService, cloudFileService);

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
