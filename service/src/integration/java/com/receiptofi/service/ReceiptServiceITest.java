package com.receiptofi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.receiptofi.IntegrationTests;
import com.receiptofi.LoadProperties;
import com.receiptofi.RealMongoForTests;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.SplitActionEnum;
import com.receiptofi.repository.*;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.multipart.MultipartFile;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.mail.internet.MimeMessage;

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

    private InviteManager inviteManager;
    private InviteService inviteService;
    private LoginService loginService;
    private BrowserManager browserManager;
    private MailService mailService;

    @Mock private JavaMailSenderImpl mailSender;
    @Mock private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;
    @Mock private MimeMessage message;
    @Mock private Configuration configuration;
    @Mock private Template template;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
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

        notificationManager = new NotificationManagerImpl(getMongoTemplate());
        notificationService = new NotificationService(notificationManager);

        receiptManager = new ReceiptManagerImpl(itemManager, fileSystemManager, storageManager, getMongoTemplate());
        documentService = new DocumentService(documentManager, itemOCRManager);
        itemService = new ItemService(itemManager, expenseTagManager);
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

        jmsTemplate = new JmsTemplate();
        senderJMS = new FileUploadDocumentSenderJMS("", jmsTemplate);

        commentManager = new CommentManagerImpl(getMongoTemplate());
        commentService = new CommentService(commentManager);
        fileSystemService = new FileSystemService(fileSystemManager);

        cloudFileManager = new CloudFileManagerImpl(getMongoTemplate());
        cloudFileService = new CloudFileService(cloudFileManager);

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

        inviteManager = new InviteManagerImpl(getMongoTemplate());
        inviteService = new InviteService(accountService, inviteManager, userProfileManager, userAccountManager);
        loginService = new LoginService(userAuthenticationManager, userAccountManager, browserManager);
        browserManager = new BrowserManagerImpl(getMongoTemplate());

        mailService = new MailService(
                properties.getProperty("do.not.reply.email"),
                properties.getProperty("dev.sent.to"),
                properties.getProperty("invitee.email"),
                properties.getProperty("email.address.name"),
                properties.getProperty("domain"),
                properties.getProperty("https"),
                properties.getProperty("mail.invite.subject"),
                properties.getProperty("mail.recover.subject"),
                properties.getProperty("mail.validate.subject"),
                properties.getProperty("mail.registration.active.subject"),
                properties.getProperty("mail.account.not.found"),
                "",
                "",
                "",
                "",
                accountService,
                inviteService,
                mailSender,
                freemarkerConfiguration,
                emailValidateService,
                friendService,
                loginService,
                userAuthenticationManager,
                userAccountManager,
                userProfilePreferenceService,
                notificationService);
    }

    @Test
    public void testDeleteReceiptUnAuthorized() throws Exception {
        assertFalse("Cannot delete receipt not owned", receiptService.deleteReceipt("5620057df4a3b612d7018894", "10000000002"));
    }

    @Test
    public void testDeleteReceipt() throws Exception {
        ReceiptEntity receipt = populateReceiptWithComments();
        createReceipt(receipt);
        assertNotNull("Re-Check comment is not null", commentService.getById(receipt.getRecheckComment().getId()));
        assertNotNull("Notes is not null", commentService.getById(receipt.getNotes().getId()));

        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());
        assertEquals("Deleted receipt", true, receiptManager.getReceipt(receipt.getId(), receipt.getReceiptUserId()).isDeleted());

        List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());
        assertEquals("Items count", 1, items.size());
        ItemEntity item = items.get(0);
        assertEquals("Item deleted", true, item.isDeleted());

        assertNull("Re-Check comment null", commentService.getById(receipt.getRecheckComment().getId()));
        assertNull("Notes null", commentService.getById(receipt.getNotes().getId()));

        List<NotificationEntity> notifications = notificationService.getAllNotifications(receipt.getReceiptUserId());
        assertEquals("Delete notification count", 1, notifications.size());
        NotificationEntity notification = notifications.get(0);
        assertEquals("Notification Type", NotificationTypeEnum.RECEIPT_DELETED, notification.getNotificationType());
        assertEquals("Notification Group", NotificationGroupEnum.R, notification.getNotificationGroup());
    }

    private void createReceipt(ReceiptEntity receipt) throws Exception {
        bizService.saveNewBusinessAndOrStore(receipt);
        receiptService.save(receipt);
        itemManager.saveObjects(populateItems(receipt));
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

    private ReceiptEntity populateReceiptWithComments() throws IOException {
        ReceiptEntity receipt = populateReceipt();
        receipt.setNotes(createComment(CommentTypeEnum.NOTES));
        receipt.setRecheckComment(createComment(CommentTypeEnum.RECHECK));
        return receipt;
    }

    private CommentEntity createComment(CommentTypeEnum commentType) {
        CommentEntity commentEntity = CommentEntity.newInstance(commentType);
        commentEntity.setText("This is notes");
        commentService.save(commentEntity);
        return commentEntity;
    }

    private List<ItemEntity> populateItems(ReceiptEntity receipt) {
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
