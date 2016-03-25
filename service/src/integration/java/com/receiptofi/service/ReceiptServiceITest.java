package com.receiptofi.service;

import static org.junit.Assert.assertEquals;

import com.receiptofi.IntegrationTests;
import com.receiptofi.LoadProperties;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.*;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

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
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptServiceITest.class);

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
    private Properties properties = new Properties();

    @Before
    public void setup() throws IOException {
        LoadProperties.loadProperties(properties);
        Assert.assertNotNull(properties.getProperty("google-server-api-key"));

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
                Boolean.getBoolean(properties.getProperty("registration.turned.on")),
                "/open/login.htm"
        );
        expensesService = new ExpensesService(expenseTagManager, receiptManager, itemManager);

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

        externalService = new ExternalService(properties.getProperty("google-server-api-key"));
        bizStoreManager = new BizStoreManagerImpl(getMongoTemplate());
        bizService = new BizService(bizNameManager, bizStoreManager, externalService);
    }

    @Test
    public void testDeleteReceipt() throws Exception {
        ReceiptEntity receipt = getReceipt();
        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());

        assertEquals("Deleted receipt", true, receiptManager.getReceipt(receipt.getId(), receipt.getReceiptUserId()).isDeleted());
        List<NotificationEntity> notifications = notificationService.getAllNotifications(receipt.getReceiptUserId());
        assertEquals("Delete notification count", 1, notifications.size());

        NotificationEntity notification = notifications.get(0);
        assertEquals("Notification Type", NotificationTypeEnum.RECEIPT_DELETED, notification.getNotificationType());
        assertEquals("Notification Group", NotificationGroupEnum.R, notification.getNotificationGroup());
    }

    private ReceiptEntity getReceipt() throws Exception {
        ReceiptEntity receipt = populateReceipt();
        bizService.saveNewBusinessAndOrStore(receipt);
        receiptService.save(receipt);
        itemManager.saveObjects(createItems(receipt));
        return receipt;
    }

    private ReceiptEntity populateReceipt() throws IOException {
        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setReceiptUserId("10000000001");
        receipt.setTotal(1.0);
        receipt.setReceiptDate(new Date());

        BizNameEntity bizNameEntity = BizNameEntity.newInstance();
        bizNameEntity.setBusinessName("Costco");
        receipt.setBizName(bizNameEntity);

        BizStoreEntity bizStoreEntity = BizStoreEntity.newInstance();
        bizStoreEntity.setAddress("150 Lawrence Station Rd, Sunnyvale, CA 94086");
        bizStoreEntity.setPhone("(408) 730-1892");
        receipt.setBizStore(bizStoreEntity);

        receipt.setFileSystemEntities(createFileSystemEntities(receipt));
        return receipt;
    }

    private List<ItemEntity> createItems(ReceiptEntity receipt) {
        ItemEntity item = new ItemEntity();
        item.setBizName(receipt.getBizName());
        item.setName("Milk");
        item.setPrice(1.0);
        item.setReceipt(receipt);
        item.setReceiptUserId(receipt.getReceiptUserId());

        List<ItemEntity> items = new ArrayList<>();
        items.add(item);
        return items;
    }

    private List<FileSystemEntity> createFileSystemEntities(ReceiptEntity receipt) throws IOException {
        File file = new File("service/src/integration/resources/test-image.png");
        if (!file.exists()) {
            file = new File("build/resources/test/test-image.png");
        }
        Assert.assertTrue("File does not exists=" + file.getAbsolutePath(), file.exists());
        BufferedImage bufferedImage = ImageIO.read(file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(
                receipt.getReceiptUserId() + "_" + file.getName(),
                file.getName(),
                "image/png",
                IOUtils.toByteArray(input));

        FileSystemEntity fileSystem = new FileSystemEntity(
                "562f4904f320266bd916d89f",
                receipt.getReceiptUserId(),
                bufferedImage,
                0,
                0,
                multipartFile);

        fileSystemService.save(fileSystem);
        List<FileSystemEntity> fileSystems = new ArrayList<>();
        fileSystems.add(fileSystem);
        return fileSystems;
    }
}
