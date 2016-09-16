package com.receiptofi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.receiptofi.ITest;
import com.receiptofi.IntegrationTests;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptListView;
import com.receiptofi.domain.value.ReceiptListViewGrouped;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;

import org.joda.time.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
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

    private UserAccountEntity primaryUserAccount;
    private static String COUNTRY_SHORT_NAME = "IN";

    @Before
    public void classSetup() throws IOException {
        primaryUserAccount = accountService.findByUserId("landingService@receiptofi.com");
        /** Create New User. */
        if (primaryUserAccount == null) {
            primaryUserAccount = accountService.createNewAccount(
                    "landingService@receiptofi.com",
                    "Landing",
                    "Service",
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
            receipt1.setCountryShortName(COUNTRY_SHORT_NAME);
            createReceiptWithItems(receipt1);

            ReceiptEntity receipt2 = populateReceiptWithComments(primaryUserAccount);
            receipt2.setTotal(1.65);
            receipt2.setTax(1.00);
            receipt2.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1)).plusHours(1).toDate());
            receipt2.setCountryShortName(COUNTRY_SHORT_NAME);
            createReceiptWithItems(receipt2);

            /** Last year receipt. */
            ReceiptEntity receipt3 = populateReceiptWithComments(primaryUserAccount);
            receipt3.setTotal(3.33);
            receipt3.setTax(1.00);
            receipt3.setReceiptDate(DateUtil.midnight(receiptTransactionDate.withDayOfMonth(1).minusYears(1)).toDate());
            receipt3.setCountryShortName(COUNTRY_SHORT_NAME);
            createReceiptWithItems(receipt3);
        }
    }

    @Test
    public void pendingReceipt() throws Exception {

    }

    @Test
    public void rejectedReceipt() throws Exception {

    }

    @Test
    public void getAllReceipts() throws Exception {
        assertEquals("Size of the existing receipts",
                3,
                landingService.getAllReceipts(primaryUserAccount.getReceiptUserId()).size());
    }

    @Test
    public void getAllReceiptsForTheYear() throws Exception {
        assertEquals("Number of receipt for the year",
                2,
                landingService.getAllReceiptsForTheYear(primaryUserAccount.getReceiptUserId(), DateUtil.startOfYear()).size());
    }

    @Test
    public void getAllReceiptsForThisMonth() throws Exception {
        assertEquals("Number of receipt for the month",
                2,
                landingService.getAllReceiptsForThisMonth(primaryUserAccount.getReceiptUserId(), DateUtil.now()).size());
    }

    @Test
    public void getReceiptGroupedByDate() throws Exception {
        Iterator<ReceiptGrouped> receipts = landingService.getReceiptGroupedByDate(primaryUserAccount.getReceiptUserId());

        ReceiptGrouped receiptGrouped1 = receipts.next();
        assertEquals("Total for this year", Maths.adjustScale(new BigDecimal(5.30)), receiptGrouped1.getSplitTotal());

        ReceiptGrouped receiptGrouped2 = receipts.next();
        assertEquals("Total for last year", Maths.adjustScale(new BigDecimal(3.33)), receiptGrouped2.getSplitTotal());

        assertEquals("Difference in year", 1, receiptGrouped1.getYear() - receiptGrouped2.getYear());

        assertEquals("Country matching", COUNTRY_SHORT_NAME, receiptGrouped1.getCountryShortName());
    }

    @Test
    public void getAllItemExpenseForTheYear() throws Exception {

    }

    @Test
    public void getReceiptGroupedByMonth() throws Exception {
        List<ReceiptGrouped> receipts = landingService.getReceiptGroupedByMonth(primaryUserAccount.getReceiptUserId());
        assertEquals("Number of months", 1, receipts.size());
        assertEquals("Month of the year", DateTime.now().getMonthOfYear(), receipts.get(0).getMonth());
        assertEquals("SplitTotal for the month", Maths.adjustScale(new BigDecimal(5.30)), receipts.get(0).getSplitTotal());
    }

    @Test
    public void getReceiptsForMonths() throws Exception {
        List<ReceiptGrouped> receipts = landingService.getReceiptGroupedByMonth(primaryUserAccount.getReceiptUserId());
        List<ReceiptListView> receiptListViews = landingService.getReceiptsForMonths(primaryUserAccount.getReceiptUserId(), receipts);
        assertEquals("Grouped receipts for the month", 1, receiptListViews.size());
        assertEquals("Month of the year", DateTime.now().getMonthOfYear(), receiptListViews.get(0).getMonth());
        assertEquals("SplitTotal for the month", Maths.adjustScale(new BigDecimal(5.30)), receiptListViews.get(0).getSplitTotal());

        List<ReceiptListViewGrouped> receiptListViewGroupedList = receiptListViews.get(0).getReceiptListViewGroupedList();
        assertEquals("Size of receipts", 2, receiptListViewGroupedList.size());
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
        Map<String, BigDecimal> ytdExpense = landingService.computeYearToDateExpense(primaryUserAccount.getReceiptUserId());

        assertEquals("Tax", Maths.adjustScale(new BigDecimal(2)), ytdExpense.get("tax"));
        assertEquals("Total without tax", Maths.adjustScale(new BigDecimal(3.30)), ytdExpense.get("totalWithoutTax"));
        assertEquals("Total", Maths.adjustScale(new BigDecimal(5.30)), ytdExpense.get("total"));
    }

    @Test
    public void uploadDocument() throws Exception {

    }

    @Test (expected = UnsupportedOperationException.class)
    public void appendMileage() throws Exception {
        landingService.appendMileage("", "", null);
    }

    @Test
    public void setEmptyBiz() throws Exception {

    }
}