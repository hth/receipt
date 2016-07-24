package com.receiptofi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import com.receiptofi.ITest;
import com.receiptofi.IntegrationTests;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.DocumentOfTypeEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.domain.types.ConnectionTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.SplitActionEnum;
import com.receiptofi.domain.util.DeepCopy;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

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
public class ReceiptServiceITest extends ITest {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptServiceITest.class);

    @Test
    public void testDeleteReceiptUnAuthorized() throws Exception {
        assertFalse("Cannot delete receipt not owned", receiptService.deleteReceipt("5620057df4a3b612d7018894", "10000000002"));
    }

    @Test
    public void testDeleteReceipt() throws Exception {
        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "delete@receiptofi.com",
                "First",
                "Name",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());

        ReceiptEntity receipt = populateReceiptWithComments(primaryUserAccount);
        createReceiptWithItems(receipt);
        assertNotNull("Re-Check comment is not null", commentService.getById(receipt.getRecheckComment().getId()));
        assertNotNull("Notes is not null", commentService.getById(receipt.getNotes().getId()));

        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());
        assertEquals("Deleted receipt", true, receiptManager.getReceipt(receipt.getId(), receipt.getReceiptUserId()).isDeleted());
        assertNull("Re-Check comment null", commentService.getById(receipt.getRecheckComment().getId()));
        assertNull("Notes null", commentService.getById(receipt.getNotes().getId()));

        List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());
        assertEquals("Items count", 1, items.size());
        ItemEntity item = items.get(0);
        assertEquals("Item deleted", true, item.isDeleted());

        List<NotificationEntity> notifications = notificationService.getAllNotifications(receipt.getReceiptUserId());
        assertEquals("Delete notification count", 2, notifications.size());
        NotificationEntity notification = notifications.get(0);
        assertEquals("Notification Type", NotificationTypeEnum.RECEIPT_DELETED, notification.getNotificationType());
        assertEquals("Notification Group", NotificationGroupEnum.R, notification.getNotificationGroup());
        assertEquals("Notification Text",
                "$1.00 'Costco' receipt deleted",
                notification.getMessage());

        notification = notifications.get(1);
        assertEquals("Notification Type", NotificationTypeEnum.PUSH_NOTIFICATION, notification.getNotificationType());
        assertEquals("Notification Group", NotificationGroupEnum.N, notification.getNotificationGroup());
        assertEquals("Notification Text",
                "Welcome First Name. Next step, take a picture of your receipt from app to process it.",
                notification.getMessage());
    }

    @Test
    public void testDeleteSplitReceipt() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(freemarkerConfiguration.createConfiguration()).thenReturn(configuration);
        when(configuration.getTemplate(anyString())).thenReturn(template);

        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "deleteSplit@receiptofi.com",
                "First",
                "Name",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());

        ReceiptEntity receipt = populateReceiptWithComments(primaryUserAccount);
        createReceiptWithItems(receipt);
        assertNotNull("Re-Check comment is not null", commentService.getById(receipt.getRecheckComment().getId()));
        assertNotNull("Notes is not null", commentService.getById(receipt.getNotes().getId()));

        /** Invite new user. */
        UserAccountEntity userAccount = inviteNewUser("second@receiptofi.com", primaryUserAccount);
        List<NotificationEntity> notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 1, notifications.size());
        assertEquals("First notification",
                "Welcome second@receiptofi.com. Next step, take a picture of your receipt from app to process it.",
                notifications.get(0).getMessage());

        /** Accept connection. */
        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), userAccount.getReceiptUserId());
        friendService.updateConnection(
                friend.getId(),
                friend.getAuthenticationKey(),
                ConnectionTypeEnum.A,
                userAccount.getReceiptUserId());

        /** Split receipt with fid. */
        boolean splitAction = receiptService.splitAction(userAccount.getReceiptUserId(), SplitActionEnum.A, receipt);
        ReceiptEntity receiptAfterSplit = receiptService.findReceipt(receipt.getId());
        assertEquals("Split Added Successful", true, splitAction);
        assertEquals("Split Count", 2, receiptAfterSplit.getSplitCount());
        assertEquals("Receipt created", false, receiptService.findAllReceipts(userAccount.getReceiptUserId()).get(0).isDeleted());
        assertEquals("After split",
                Maths.divide(receipt.getTotal(), receipt.getSplitCount() + 1).doubleValue(),
                receiptAfterSplit.getSplitTotal(),
                0.00);
        List<ReceiptEntity> receipts = receiptService.findAllReceipts(userAccount.getReceiptUserId());
        assertEquals("Found receipt for " + userAccount.getUserId(), 1, receipts.size());
        compareReceiptAfterSplit(receiptAfterSplit, receipts.get(0));
        notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 2, notifications.size());
        assertEquals("First notification",
                "$1.00 'Costco' receipt was split by First Name with you",
                notifications.get(0).getMessage());

        /** Delete split receipt by fid. */
        splitAction = receiptService.splitAction(userAccount.getReceiptUserId(), SplitActionEnum.R, receiptAfterSplit);
        receiptAfterSplit = receiptService.findReceipt(receipt.getId());
        assertEquals("Split Removed Successful", true, splitAction);
        assertEquals("Split Count", 1, receiptAfterSplit.getSplitCount());
        assertEquals("Receipt deleted", true, receiptService.findAllReceipts(userAccount.getReceiptUserId()).get(0).isDeleted());
        assertEquals("After split",
                Maths.divide(receipt.getTotal(), receipt.getSplitCount()).doubleValue(),
                receiptAfterSplit.getSplitTotal(),
                0.00);
        receipts = receiptService.findAllReceipts(userAccount.getReceiptUserId());
        assertEquals("Found receipt for " + userAccount.getUserId(), 1, receipts.size());
        compareReceiptAfterSplitDelete(receiptAfterSplit, receipts.get(0));

        notifications = notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId());
        assertEquals("Number of notification", 4, notifications.size());
        assertEquals("Delcine split receipt",
                "$1.00 'Costco' receipt declined by second@receiptofi.com",
                notifications.get(0).getMessage());

        /** Delete the original receipt. Clean Up. */
        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());
    }

    @Test
    public void testDeleteSharedReceipt() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(freemarkerConfiguration.createConfiguration()).thenReturn(configuration);
        when(configuration.getTemplate(anyString())).thenReturn(template);

        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "deleteShared@receiptofi.com",
                "Delete",
                "Shared",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());

        ReceiptEntity receipt = populateReceipt(primaryUserAccount);
        createReceiptWithItems(receipt);
        assertNull("Re-Check comment is not null", receipt.getRecheckComment());
        assertNull("Notes is not null", receipt.getNotes());

        /** Invite new user. */
        UserAccountEntity userAccount = inviteNewUser("third@receiptofi.com", primaryUserAccount);
        List<NotificationEntity> notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 1, notifications.size());
        assertEquals("First notification",
                "Welcome third@receiptofi.com. Next step, take a picture of your receipt from app to process it.",
                notifications.get(0).getMessage());

        /** Accept connection. */
        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), userAccount.getReceiptUserId());
        friendService.updateConnection(
                friend.getId(),
                friend.getAuthenticationKey(),
                ConnectionTypeEnum.A,
                userAccount.getReceiptUserId());

        /** Split receipt with fid. */
        boolean splitAction = receiptService.splitAction(userAccount.getReceiptUserId(), SplitActionEnum.A, receipt);
        ReceiptEntity receiptAfterSplit = receiptService.findReceipt(receipt.getId());
        assertEquals("Split Added Successful", true, splitAction);
        assertEquals("Split Count", 2, receiptAfterSplit.getSplitCount());
        assertEquals("Receipt created", false, receiptService.findAllReceipts(userAccount.getReceiptUserId()).get(0).isDeleted());
        assertEquals("After split",
                Maths.divide(receipt.getTotal(), receipt.getSplitCount() + 1).doubleValue(),
                receiptAfterSplit.getSplitTotal(),
                0.00);
        List<ReceiptEntity> receipts = receiptService.findAllReceipts(userAccount.getReceiptUserId());
        assertEquals("Found receipt for " + userAccount.getUserId(), 1, receipts.size());
        compareReceiptAfterSplit(receiptAfterSplit, receipts.get(0));
        notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 2, notifications.size());
        assertEquals("First notification",
                "$1.00 'Costco' receipt was split by Delete Shared with you",
                notifications.get(0).getMessage());

        /** Delete the original receipt. */
        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());
        assertEquals("None of the referred receipt", 0, receiptService.findAllReceiptWithMatchingReferReceiptId(receipt.getId()).size());
        notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 3, notifications.size());
        assertEquals("Unshared receipt notification",
                "$1.00 'Costco' receipt removed from splitting with you by Delete Shared",
                notifications.get(0).getMessage());
    }

    @Test
    public void testRecheck() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(freemarkerConfiguration.createConfiguration()).thenReturn(configuration);
        when(configuration.getTemplate(anyString())).thenReturn(template);

        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "testRecheck@receiptofi.com",
                "Test",
                "Recheck",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());

        /** New Document. */
        UploadDocumentImage image = UploadDocumentImage.newInstance(FileTypeEnum.R)
                .setFileData(getMultipartFile(primaryUserAccount.getReceiptUserId()))
                .setRid(primaryUserAccount.getReceiptUserId());
        DocumentEntity document = landingService.uploadDocument(image);

        /** Process Document to Receipt. */
        populateDocument(document);
        ReceiptEntity receipt = DeepCopy.getReceiptEntity(document);
        createReceipt(receipt);
        List<ItemEntityOCR> itemOCRs = itemOCRManager.getWhereReceipt(document);
        populateItemsOCR(document, itemOCRs);
        List<ItemEntity> items = DeepCopy.getItemEntity(receipt, itemOCRs);
        documentUpdateService.processDocumentForReceipt(primaryUserAccount.getReceiptUserId(), receipt, items, document);
        assertNull("Re-Check comment is not null", receipt.getRecheckComment());
        assertNull("Notes is not null", receipt.getNotes());
        List<NotificationEntity> primaryNotifications = notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId());

        /** Invite new user. */
        UserAccountEntity userAccount = inviteNewUser("fourth@receiptofi.com", primaryUserAccount);
        List<NotificationEntity> notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 1, notifications.size());
        assertEquals("First notification",
                "Welcome fourth@receiptofi.com. Next step, take a picture of your receipt from app to process it.",
                notifications.get(0).getMessage());

        /** Accept connection. */
        FriendEntity friend = friendService.getConnection(primaryUserAccount.getReceiptUserId(), userAccount.getReceiptUserId());
        friendService.updateConnection(
                friend.getId(),
                friend.getAuthenticationKey(),
                ConnectionTypeEnum.A,
                userAccount.getReceiptUserId());

        /** Split receipt with fid. */
        boolean splitAction = receiptService.splitAction(userAccount.getReceiptUserId(), SplitActionEnum.A, receipt);
        ReceiptEntity receiptAfterSplit = receiptService.findReceipt(receipt.getId());
        assertEquals("Split Added Successful", true, splitAction);
        assertEquals("Split Count", 2, receiptAfterSplit.getSplitCount());
        assertEquals("Receipt created", false, receiptService.findAllReceipts(userAccount.getReceiptUserId()).get(0).isDeleted());
        assertEquals("After split",
                Maths.divide(receipt.getTotal(), receipt.getSplitCount() + 1).doubleValue(),
                receiptAfterSplit.getSplitTotal(),
                0.00);
        List<ReceiptEntity> receipts = receiptService.findAllReceipts(userAccount.getReceiptUserId());
        assertEquals("Found receipt for " + userAccount.getUserId(), 1, receipts.size());
        compareReceiptAfterSplit(receiptAfterSplit, receipts.get(0));
        notifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Number of notification", 2, notifications.size());
        assertEquals("First notification",
                "$1.00 'Costco' receipt was split by Test Recheck with you",
                notifications.get(0).getMessage());

        /** Recheck receipt. */
        assertFalse("Settlement has not started", splitExpensesService.hasSettleProcessStarted(receipt.getId()));
        receiptService.recheck(receipt.getId(), receipt.getReceiptUserId());
        List<NotificationEntity> recheckNotifications = notificationService.getAllNotifications(userAccount.getReceiptUserId());
        assertEquals("Size of notification", notifications.size() + 1, recheckNotifications.size());
        assertEquals("After recheck " + userAccount.getReceiptUserId(), "$1.00 'Costco' receipt removed from splitting with you by Test Recheck", recheckNotifications.get(0).getMessage());
        List<NotificationEntity> primaryRecheckNotifications = notificationService.getAllNotifications(primaryUserAccount.getReceiptUserId());
        assertEquals("Size of notification", primaryNotifications.size() + 4, primaryRecheckNotifications.size());
        assertEquals("Last message of notification", "$1.00 'Costco' receipt sent for verification", primaryRecheckNotifications.get(0).getMessage());
        //TODO This message has to be removed
        assertEquals("Last message of notification", "$1.00 'Costco' receipt declined by fourth@receiptofi.com", primaryRecheckNotifications.get(1).getMessage());

        DocumentEntity reCheckDocument = documentService.findDocumentByRid(document.getId(), document.getReceiptUserId());
        assertEquals("Ids match", document.getId(), reCheckDocument.getId());
        assertEquals("REPROCESS", DocumentStatusEnum.REPROCESS, reCheckDocument.getDocumentStatus());
        assertEquals("Modified twice", 2, reCheckDocument.getVersion().intValue());
        assertFalse("Active document", document.isActive());
        assertTrue("Active recheck document", reCheckDocument.isActive());

        List<ItemEntityOCR> recheckItemOCRs = itemOCRManager.getWhereReceipt(document);
        compareItemOCRs(itemOCRs, recheckItemOCRs);

        ReceiptEntity recheckReceipt = receiptManager.getReceipt(receipt.getId(), receipt.getReceiptUserId());
        compareReceiptAfterSplitRecheck(receipt, recheckReceipt);

        List<ItemEntity> recheckItems = itemService.getAllItemsOfReceipt(receipt.getId());
        assertTrue("Empty when receipt is under recheck", recheckItems.isEmpty());
    }

    @Test
    public void testUpdateReceiptNotes() throws Exception {
        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "notes@receiptofi.com",
                "First",
                "Name",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());

        ReceiptEntity receipt = populateReceipt(primaryUserAccount);
        createReceiptWithItems(receipt);
        assertNull("Re-Check comment is not null", receipt.getRecheckComment());
        assertNull("Notes is not null", receipt.getNotes());

        receiptService.updateReceiptNotes("My new receipt note", receipt.getId(), receipt.getReceiptUserId());
        ReceiptEntity receiptAfterCommentUpdate = receiptService.findReceipt(receipt.getId());
        assertEquals("Receipt comment type", CommentTypeEnum.N, receiptAfterCommentUpdate.getNotes().getCommentType());
        assertEquals("Receipt Note", "My new receipt note", receiptAfterCommentUpdate.getNotes().getText());

        receiptService.updateReceiptNotes("Updated receipt note", receipt.getId(), receipt.getReceiptUserId());
        receiptAfterCommentUpdate = receiptService.findReceipt(receipt.getId());
        assertEquals("Receipt comment type", CommentTypeEnum.N, receiptAfterCommentUpdate.getNotes().getCommentType());
        assertEquals("Receipt Note", "Updated receipt note", receiptAfterCommentUpdate.getNotes().getText());

        /** Delete the original receipt. Clean Up. */
        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());
    }

    @Test
    public void testUpdateReceiptComments() throws Exception {
        /** Create New User. */
        UserAccountEntity primaryUserAccount = accountService.createNewAccount(
                "comments@receiptofi.com",
                "First",
                "Name",
                "testtest",
                DateUtil.parseAgeForBirthday("25"));
        assertFalse("Account validated", primaryUserAccount.isAccountValidated());
        EmailValidateEntity emailValidate = emailValidateService.saveAccountValidate(primaryUserAccount.getReceiptUserId(), primaryUserAccount.getUserId());
        accountService.validateAccount(emailValidate, primaryUserAccount);
        primaryUserAccount = accountService.findByUserId(primaryUserAccount.getUserId());
        assertTrue("Account validated", primaryUserAccount.isAccountValidated());

        ReceiptEntity receipt = populateReceipt(primaryUserAccount);
        createReceiptWithItems(receipt);
        assertNull("Re-Check comment is not null", receipt.getRecheckComment());
        assertNull("Notes is not null", receipt.getNotes());

        receiptService.updateReceiptComment("My new recheck comment", receipt.getId(), receipt.getReceiptUserId());
        ReceiptEntity receiptAfterCommentUpdate = receiptService.findReceipt(receipt.getId());
        assertEquals("Receipt comment type", CommentTypeEnum.R, receiptAfterCommentUpdate.getRecheckComment().getCommentType());
        assertEquals("Receipt Re-Check comment", "My new recheck comment", receiptAfterCommentUpdate.getRecheckComment().getText());

        receiptService.updateReceiptComment("Updated recheck comment", receipt.getId(), receipt.getReceiptUserId());
        receiptAfterCommentUpdate = receiptService.findReceipt(receipt.getId());
        assertEquals("Receipt comment type", CommentTypeEnum.R, receiptAfterCommentUpdate.getRecheckComment().getCommentType());
        assertEquals("Receipt Re-Check comment", "Updated recheck comment", receiptAfterCommentUpdate.getRecheckComment().getText());

        /** Delete the original receipt. Clean Up. */
        receiptService.deleteReceipt(receipt.getId(), receipt.getReceiptUserId());
    }

    private void populateItemsOCR(DocumentEntity document, List<ItemEntityOCR> items) {
        for (ItemEntityOCR item : items) {
            item.setBizName(document.getBizName());
            item.setName("Milk");
            item.setPrice("1.0");
            item.setDocument(document);
        }
    }

    private UserAccountEntity inviteNewUser(String invitedUserEmail, UserAccountEntity userAccount) {
        EmailValidateEntity emailValidate; /** Send invite to new user. */
        String inviteResponse = mailService.sendInvite(invitedUserEmail, userAccount.getReceiptUserId(), userAccount.getUserId());
        DBObject dbObject = (DBObject) JSON.parse(inviteResponse);
        assertTrue("Sent invite successfully", (boolean) dbObject.get("status"));
        assertEquals("Invitation message", "Invitation Sent to: " + invitedUserEmail, dbObject.get("message"));
        assertEquals("Number of pending friends", 1, friendService.getPendingConnections(userAccount.getReceiptUserId()).size());

        /** Re-Send invite to same new user. */
        inviteResponse = mailService.sendInvite(invitedUserEmail, userAccount.getReceiptUserId(), userAccount.getUserId());
        dbObject = (DBObject) JSON.parse(inviteResponse);
        assertTrue("Sent invite successfully", (boolean) dbObject.get("status"));
        assertEquals("Invitation message", "Invitation Sent to: " + invitedUserEmail, dbObject.get("message"));
        assertEquals("Number of pending friends", 1, friendService.getPendingConnections(userAccount.getReceiptUserId()).size());

        /** Validate and activate second user. */
        UserAccountEntity createdUserAccount = accountService.findByUserId(invitedUserEmail);
        assertFalse("Account validated", createdUserAccount.isAccountValidated());
        emailValidate = emailValidateService.saveAccountValidate(createdUserAccount.getReceiptUserId(), createdUserAccount.getUserId());
        userAccount.setFirstName("Blue");
        userAccount.setLastName("Whale");
        accountService.validateAccount(emailValidate, createdUserAccount);
        createdUserAccount = accountService.findByUserId(invitedUserEmail);
        assertTrue("Account validated", createdUserAccount.isAccountValidated());
        return createdUserAccount;
    }

    private void compareReceiptAfterSplit(ReceiptEntity original, ReceiptEntity split) {
        assertEquals(original.getSplitCount(), split.getSplitCount());
        assertEquals(original.getId(), split.getReferReceiptId());
        assertEquals(original.getBizName().getBusinessName(), split.getBizName().getBusinessName());
        assertEquals(original.getBizStore().getAddress(), split.getBizStore().getAddress());
        assertEquals(original.getTotal(), split.getTotal());
        assertEquals(original.getSplitTotal(), split.getSplitTotal());
        assertEquals(original.isActive(), split.isActive());
        assertEquals(original.isDeleted(), split.isDeleted());
    }

    private void compareReceiptAfterSplitDelete(ReceiptEntity original, ReceiptEntity split) {
        assertNotEquals(original.getSplitCount(), split.getSplitCount());
        assertEquals(original.getId(), split.getReferReceiptId());
        assertEquals(original.getBizName().getBusinessName(), split.getBizName().getBusinessName());
        assertEquals(original.getBizStore().getAddress(), split.getBizStore().getAddress());
        assertEquals(original.getTotal(), split.getTotal());
        assertNotEquals(original.getSplitTotal(), split.getSplitTotal());
        assertNotEquals(original.isActive(), split.isActive());
        assertNotEquals(original.isDeleted(), split.isDeleted());
    }

    private void compareReceiptAfterSplitRecheck(ReceiptEntity original, ReceiptEntity split) {
        assertEquals(DocumentStatusEnum.PROCESSED, original.getReceiptStatus());
        assertEquals(DocumentStatusEnum.REPROCESS, split.getReceiptStatus());
        assertEquals(original.getSplitCount(), split.getSplitCount());
        assertEquals(1, original.getVersion().intValue());
        assertEquals(4, split.getVersion().intValue());
        assertNotEquals(original.isActive(), split.isActive());
        assertEquals(original.isDeleted(), split.isDeleted());
        assertNull("No refer receipt as its original", split.getReferReceiptId());
        assertEquals(original.getReferReceiptId(), split.getReferReceiptId());
    }

    private void compareItemOCRs(List<ItemEntityOCR> beforeRechecks, List<ItemEntityOCR> afterRechecks) {
        ItemEntityOCR itemOCRs = beforeRechecks.get(0);
        ItemEntityOCR recheckItemOCRs = afterRechecks.get(0);
        assertEquals(itemOCRs.isActive(), recheckItemOCRs.isActive());
        assertEquals(itemOCRs.isDeleted(), recheckItemOCRs.isDeleted());
        assertEquals(itemOCRs.getVersion(), recheckItemOCRs.getVersion());
    }

    private void populateDocument(DocumentEntity document) {
        document.setTotal("1.00");
        document.setSubTotal("1.00");
        document.setDocumentOfType(DocumentOfTypeEnum.RECEIPT);
        document.setReceiptDate(DateUtil.DateType.FRM_1.getFormatter().print(DateUtil.now()));
        BizNameEntity bizName = getBizName("Costco");
        document.setBizName(bizName);
        BizStoreEntity bizStore = getBizStore(
                bizName,
                "150 Lawrence Station Rd, Sunnyvale, CA 94086",
                "(408) 730-1892");
        document.setBizStore(bizStore);
    }
}
