package com.receiptofi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.ITest;
import com.receiptofi.IntegrationTests;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;

import org.joda.time.DateTime;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: hitender
 * Date: 4/4/16 5:29 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Category (IntegrationTests.class)
public class LandingServiceITest extends ITest {

    @Test
    public void pendingReceipt() throws Exception {

    }

    @Test
    public void rejectedReceipt() throws Exception {

    }

    @Test
    public void getAllReceipts() throws Exception {
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

        DateTime receiptTransactionDate = new DateTime();
        ReceiptEntity receipt1 = populateReceiptWithComments(primaryUserAccount);
        receipt1.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).toDate());
        createReceiptWithItems(receipt1);

        ReceiptEntity receipt2 = populateReceiptWithComments(primaryUserAccount);
        receipt2.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1).minusMonths(1)).toDate());
        createReceiptWithItems(receipt2);

        ReceiptEntity receipt3 = populateReceiptWithComments(primaryUserAccount);
        receipt3.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1).minusMonths(2)).toDate());
        createReceiptWithItems(receipt3);

        assertEquals("Size of the existing receipts",
                3,
                landingService.getAllReceipts(primaryUserAccount.getReceiptUserId()).size());
    }

    @Test
    public void getAllReceiptsForTheYear() throws Exception {
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

        DateTime receiptTransactionDate = new DateTime();
        ReceiptEntity receipt1 = populateReceiptWithComments(primaryUserAccount);
        receipt1.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).toDate());
        createReceiptWithItems(receipt1);

        ReceiptEntity receipt2 = populateReceiptWithComments(primaryUserAccount);
        receipt2.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).plusHours(1).toDate());
        createReceiptWithItems(receipt2);

        /** Last year receipt. */
        ReceiptEntity receipt3 = populateReceiptWithComments(primaryUserAccount);
        receipt3.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1).minusYears(1)).toDate());
        createReceiptWithItems(receipt3);

        assertEquals("Number of receipt for the year",
                2,
                landingService.getAllReceiptsForTheYear(primaryUserAccount.getReceiptUserId(), DateUtil.startOfYear()).size());
    }

    @Test
    public void getAllReceiptsForThisMonth() throws Exception {
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

        DateTime receiptTransactionDate = new DateTime();
        ReceiptEntity receipt1 = populateReceiptWithComments(primaryUserAccount);
        receipt1.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).toDate());
        createReceiptWithItems(receipt1);

        ReceiptEntity receipt2 = populateReceiptWithComments(primaryUserAccount);
        receipt2.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).plusHours(1).toDate());
        createReceiptWithItems(receipt2);

        /** Last month receipt. */
        ReceiptEntity receipt3 = populateReceiptWithComments(primaryUserAccount);
        receipt3.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1).minusMonths(1)).plusHours(2).toDate());
        createReceiptWithItems(receipt3);

        assertEquals("Number of receipt for the month",
                2,
                landingService.getAllReceiptsForThisMonth(primaryUserAccount.getReceiptUserId(), DateUtil.now()).size());
    }

    @Test
    public void getReceiptGroupedByDate() throws Exception {
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

        DateTime receiptTransactionDate = new DateTime();
        ReceiptEntity receipt1 = populateReceiptWithComments(primaryUserAccount);
        receipt1.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).toDate());
        createReceiptWithItems(receipt1);

        ReceiptEntity receipt2 = populateReceiptWithComments(primaryUserAccount);
        receipt2.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).plusHours(1).toDate());
        createReceiptWithItems(receipt2);

        /** Last month receipt. */
        ReceiptEntity receipt3 = populateReceiptWithComments(primaryUserAccount);
        receipt3.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1).minusMonths(1)).plusHours(2).toDate());
        createReceiptWithItems(receipt3);

        assertEquals("Number of receipt for the month",
                2,
                landingService.getReceiptGroupedByDate(primaryUserAccount.getReceiptUserId()));
    }

    @Test
    public void getAllItemExpenseForTheYear() throws Exception {

    }

    @Test
    public void getReceiptGroupedByMonth() throws Exception {

    }

    @Test
    public void getReceiptsForMonths() throws Exception {

    }

    @Test
    public void addMonthsIfLessThanThree() throws Exception {

    }

    @Test
    public void getAllObjectsGroupedByBizLocation() throws Exception {

    }

    @Test
    public void allBusinessByExpenseType() throws Exception {

    }

    @Test
    public void computeTotalExpense() throws Exception {

    }

    @Test
    public void computeYearToDateExpense() throws Exception {
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

        DateTime receiptTransactionDate = new DateTime();
        ReceiptEntity receipt1 = populateReceiptWithComments(primaryUserAccount);
        receipt1.setTotal(3.65);
        receipt1.setTax(1.00);
        receipt1.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).toDate());
        createReceiptWithItems(receipt1);

        ReceiptEntity receipt2 = populateReceiptWithComments(primaryUserAccount);
        receipt2.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).plusHours(1).toDate());
        receipt2.setTotal(1.65);
        receipt2.setTax(1.00);
        createReceiptWithItems(receipt2);

        /** Last month receipt. */
        ReceiptEntity receipt3 = populateReceiptWithComments(primaryUserAccount);
        receipt3.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).plusHours(2).toDate());
        receipt3.setTotal(3.33);
        receipt3.setTax(1.00);
        createReceiptWithItems(receipt3);

        Map<String, BigDecimal> ytdExpense = landingService.computeYearToDateExpense(primaryUserAccount.getReceiptUserId());

        assertEquals("Tax", Maths.adjustScale(new BigDecimal(3)), ytdExpense.get("tax"));
        assertEquals("Total without tax", Maths.adjustScale(new BigDecimal(5.63)), ytdExpense.get("totalWithoutTax"));
        assertEquals("Total", Maths.adjustScale(new BigDecimal(8.63)), ytdExpense.get("total"));
    }

    @Test
    public void uploadDocument() throws Exception {

    }

    @Test(expected = UnsupportedOperationException.class)
    public void appendMileage() throws Exception {
        landingService.appendMileage("", "", null);
    }

    @Test
    public void setEmptyBiz() throws Exception {

    }
}