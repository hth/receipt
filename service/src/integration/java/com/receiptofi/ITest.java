package com.receiptofi;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.repository.*;
import com.receiptofi.service.*;

import org.apache.commons.io.IOUtils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.multipart.MultipartFile;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.mail.internet.MimeMessage;

/**
 * User: hitender
 * Date: 4/5/16 9:25 PM
 */
public class ITest extends RealMongoForTests {
    public ReceiptManager receiptManager;
    public DocumentService documentService;
    public ItemService itemService;
    public ItemOCRManager itemOCRManager;
    public AccountService accountService;
    public CommentService commentService;
    public FileSystemService fileSystemService;
    public CloudFileManager cloudFileManager;
    public CloudFileService cloudFileService;
    public ExpensesService expensesService;
    public NotificationService notificationService;
    public FriendService friendService;
    public SplitExpensesService splitExpensesService;

    public BizNameManager bizNameManager;
    public ItemManager itemManager;
    public FileSystemManager fileSystemManager;
    public StorageManager storageManager;
    public DocumentManager documentManager;
    public ExpenseTagManager expenseTagManager;
    public UserAccountManager userAccountManager;
    public UserAuthenticationManager userAuthenticationManager;
    public UserProfileManager userProfileManager;
    public UserPreferenceManager userPreferenceManager;
    public ForgotRecoverManager forgotRecoverManager;
    public GenerateUserIdManager generateUserIdManager;
    public GenerateUserIdService generateUserIdService;
    public SkippedRidsService skippedRidsService;
    public EmailValidateManager emailValidateManager;
    public EmailValidateService emailValidateService;

    public BillingAccountManager billingAccountManager;
    public BillingHistoryManager billingHistoryManager;
    public PaymentGatewayService paymentGatewayService;
    public BillingService billingService;

    public CommentManager commentManager;
    public RegistrationService registrationService;
    public NotificationManager notificationManager;
    public UserProfilePreferenceService userProfilePreferenceService;
    public FriendManager friendManager;
    public SplitExpensesManager splitExpenseManager;
    public ExternalService externalService;
    public BizStoreManager bizStoreManager;
    public BizService bizService;
    public ReceiptService receiptService;
    public Properties properties = new Properties();
    public InviteManager inviteManager;
    public InviteService inviteService;
    public LoginService loginService;
    public BrowserManager browserManager;
    public MailService mailService;
    public LandingService landingService;
    public ImageSplitService imageSplitService;
    public FileDBService fileDBService;
    public ReceiptParserService receiptParserService;
    public DocumentUpdateService documentUpdateService;
    public MessageDocumentManager messageDocumentManager;
    public MailManager mailManager;
    public PaymentCardManager paymentCardManager;
    public PaymentCardService paymentCardService;

    public BusinessUserManager businessUserManager;
    public BusinessUserService businessUserService;

    @Mock public JavaMailSenderImpl mailSender;
    @Mock public FreeMarkerConfigurationFactoryBean freemarkerConfiguration;
    @Mock public MimeMessage message;
    @Mock public Configuration configuration;
    @Mock public Template template;
    @Mock public RedisTemplate<String, Object> redisTemplate;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        LoadResource.loadProperties(properties);
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
        skippedRidsService = new SkippedRidsService(100, "SKIPPED_RIDS", generateUserIdManager, userAccountManager, redisTemplate);
        generateUserIdService = new GenerateUserIdService(generateUserIdManager, skippedRidsService);
        emailValidateManager = new EmailValidateManagerImpl(getMongoTemplate());
        emailValidateService = new EmailValidateService(emailValidateManager);
        registrationService = new RegistrationService(
                Boolean.getBoolean(properties.getProperty("registration.turned.on")),
                "/open/login.htm"
        );
        expensesService = new ExpensesService(expenseTagManager, receiptManager, itemManager);
        mailManager = new MailManagerImpl(getMongoTemplate());

        billingAccountManager = new BillingAccountManagerImpl(getMongoTemplate());
        billingHistoryManager = new BillingHistoryManagerImpl(getMongoTemplate());
        cloudFileManager = new CloudFileManagerImpl(getMongoTemplate());
        cloudFileService = new CloudFileService(cloudFileManager);

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
        businessUserManager = new BusinessUserManagerImpl(getMongoTemplate());
        businessUserService = new BusinessUserService(businessUserManager);
        accountService = new AccountService(
                new String[]{"HOME", "BUSINESS"},
                new String[]{"#1a9af9", "#b492e8"},
                new String[]{"V101", "V102"},
                3,
                userAccountManager,
                userAuthenticationManager,
                userProfileManager,
                userPreferenceManager,
                forgotRecoverManager,
                generateUserIdService,
                emailValidateService,
                registrationService,
                expensesService,
                billingService,
                notificationService,
                inviteManager);

        commentManager = new CommentManagerImpl(getMongoTemplate());
        commentService = new CommentService(commentManager);
        fileSystemService = new FileSystemService(fileSystemManager, cloudFileService);

        friendManager = new FriendManagerImpl(getMongoTemplate());
        userProfilePreferenceService = new UserProfilePreferenceService(userProfileManager, userPreferenceManager);
        friendService = new FriendService(10, 0, friendManager, userProfilePreferenceService);

        splitExpenseManager = new SplitExpensesManagerImpl(getMongoTemplate());
        splitExpensesService = new SplitExpensesService(splitExpenseManager, userProfilePreferenceService);
        messageDocumentManager = new MessageDocumentManagerImpl(10, getMongoTemplate());

        receiptService = new ReceiptService(
                receiptManager,
                documentService,
                itemService,
                itemOCRManager,
                accountService,
                commentService,
                fileSystemService,
                expensesService,
                notificationService,
                friendService,
                splitExpensesService,
                messageDocumentManager);

        externalService = new ExternalService(properties.getProperty("google-server-api-key"));
        bizStoreManager = new BizStoreManagerImpl(getMongoTemplate());
        bizService = new BizService(69.172, 111.321, bizNameManager, bizStoreManager, externalService);

        inviteManager = new InviteManagerImpl(getMongoTemplate());
        inviteService = new InviteService(accountService, inviteManager, userProfileManager, userAccountManager);
        loginService = new LoginService(userAuthenticationManager, browserManager);
        browserManager = new BrowserManagerImpl(getMongoTemplate());

        mailService = new MailService(
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
                accountService,
                inviteService,
                freemarkerConfiguration,
                emailValidateService,
                friendService,
                userAuthenticationManager,
                userAccountManager,
                userProfilePreferenceService,
                notificationService,
                mailManager,
                1);


        imageSplitService = new ImageSplitService();
        fileDBService = new FileDBService(storageManager);
        receiptParserService = new ReceiptParserService();
        landingService = new LandingService(
                receiptManager,
                documentManager,
                itemOCRManager,
                bizNameManager,
                bizStoreManager,
                userProfileManager,
                fileDBService,
                itemService,
                notificationService,
                fileSystemService,
                imageSplitService,
                receiptParserService,
                messageDocumentManager);

        paymentCardManager = new PaymentCardManagerImpl(getMongoTemplate());
        paymentCardService = new PaymentCardService(paymentCardManager);
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
                billingService,
                expensesService,
                paymentCardService);
    }

    public ReceiptEntity populateReceiptWithComments(UserAccountEntity userAccount) throws IOException {
        ReceiptEntity receipt = populateReceipt(userAccount);
        receipt.setNotes(createComment(userAccount.getReceiptUserId(), CommentTypeEnum.N));
        receipt.setRecheckComment(createComment(userAccount.getReceiptUserId(), CommentTypeEnum.R));
        return receipt;
    }

    public ReceiptEntity populateReceipt(UserAccountEntity userAccount) throws IOException {
        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setReceiptUserId(userAccount.getReceiptUserId());
        receipt.setTotal(1.0);
        receipt.setReceiptDate(new Date());

        BizNameEntity bizName = getBizName("Costco");
        receipt.setBizName(bizName);
        BizStoreEntity bizStore = getBizStore(
                bizName,
                "150 Lawrence Station Rd, Sunnyvale, CA 94086",
                "(408) 730-1892");
        receipt.setBizStore(bizStore);

        receipt.setFileSystemEntities(createFileSystemEntities(receipt));
        return receipt;
    }

    public BizNameEntity getBizName(String name) {
        BizNameEntity bizName = BizNameEntity.newInstance();
        bizName.setBusinessName(name);
        List<BizNameEntity> bizNames = bizNameManager.findAllBizWithMatchingName(name);
        if (!bizNames.isEmpty()) {
            bizName = bizNames.get(0);
        }
        return bizName;
    }

    public BizStoreEntity getBizStore(BizNameEntity bizName, String address, String phone) {
        BizStoreEntity bizStore = BizStoreEntity.newInstance();
        bizStore.setAddress(address);
        bizStore.setPhone(phone);

        Set<BizStoreEntity> bizStores = bizService.bizSearch(
                bizName.getBusinessName(),
                bizStore.getAddress(),
                bizStore.getPhone());

        if (!bizStores.isEmpty()) {
            bizStore = bizStores.iterator().next();
        }
        return bizStore;
    }

    private List<FileSystemEntity> createFileSystemEntities(ReceiptEntity receipt) throws IOException {
        FileSystemEntity fileSystem = new FileSystemEntity(
                "562f4904f320266bd916d89f",
                receipt.getReceiptUserId(),
                ImageIO.read(getFile()),
                0,
                0,
                getMultipartFile(receipt.getReceiptUserId()),
                FileTypeEnum.R);

        fileSystemService.save(fileSystem);
        List<FileSystemEntity> fileSystems = new ArrayList<>();
        fileSystems.add(fileSystem);
        return fileSystems;
    }

    private CommentEntity createComment(String rid, CommentTypeEnum commentType) {
        CommentEntity commentEntity = CommentEntity.newInstance(rid, commentType);
        commentEntity.setText("This is notes");
        commentService.save(commentEntity);
        return commentEntity;
    }

    public File getFile() {
        File file = new File("service/src/integration/resources/test-image.png");
        if (!file.exists()) {
            file = new File("build/resources/integration/test-image.png");
        }
        Assert.assertTrue("File does not exists=" + file.getAbsolutePath(), file.exists());
        return file;
    }

    public MultipartFile getMultipartFile(String rid) throws IOException {
        File file = getFile();
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile(
                rid + "_" + file.getName(),
                file.getName(),
                "image/png",
                IOUtils.toByteArray(input));
    }

    public void createReceipt(ReceiptEntity receipt) {
        bizService.saveNewBusinessAndOrStore(receipt);
        receiptService.save(receipt);
    }

    public void createReceiptWithItems(ReceiptEntity receipt) {
        createReceipt(receipt);
        itemManager.saveObjects(populateItems(receipt));
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
}
