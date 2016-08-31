package com.receiptofi.web.controller.ajax;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.google.gson.JsonObject;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FetcherService;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.LandingService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.helper.json.ReceiptExpenseTag;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 4/19/13
 * Time: 11:44 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/ws/r")
public class ReceiptWebService {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptWebService.class);

    private FetcherService fetcherService;
    private LandingService landingService;
    private DocumentUpdateService documentUpdateService;
    private ReceiptService receiptService;
    private ItemService itemService;

    @Autowired
    public ReceiptWebService(
            FetcherService fetcherService,
            LandingService landingService,
            DocumentUpdateService documentUpdateService,
            ReceiptService receiptService,
            ItemService itemService
    ) {
        this.fetcherService = fetcherService;
        this.landingService = landingService;
        this.documentUpdateService = documentUpdateService;
        this.receiptService = receiptService;
        this.itemService = itemService;
    }

    /**
     * @param businessName
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/find_company",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "searchBusinessWithBusinessName", keyGenerator = "customKeyGenerator")
    public Set<String> searchBusinessWithBusinessName(
            @RequestParam ("term")
            ScrubbedInput businessName
    ) {
        try {
            LOG.info("searchBusinessWithBusinessName businessName={}", businessName.getText());
            return fetcherService.findDistinctBizName(StringUtils.stripToEmpty(businessName.getText()));
        } catch (Exception fetchBusinessName) {
            LOG.warn("Error fetching business number, error={}", fetchBusinessName);
            return new HashSet<>();
        }
    }

    /**
     * @param bizAddress
     * @param businessName
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/find_address",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "searchBiz", keyGenerator = "customKeyGenerator")
    public Set<String> searchBiz(
            @RequestParam ("term")
            ScrubbedInput bizAddress,

            @RequestParam ("nameParam")
            ScrubbedInput businessName,

            @RequestParam (value = "phoneParam")
            ScrubbedInput bizPhone
    ) {
        try {
            LOG.info("searchBiz bizAddress={} businessName={}",
                    bizAddress.getText(), businessName.getText());

            return fetcherService.findDistinctBizAddress(
                    StringUtils.stripToEmpty(bizAddress.getText()),
                    StringUtils.stripToEmpty(businessName.getText()),
                    CommonUtil.phoneCleanup(StringUtils.stripToEmpty(bizPhone.getText()))
            );
        } catch (Exception fetchBusinessAddress) {
            LOG.warn("Error fetching business address, error={}", fetchBusinessAddress);
            return new HashSet<>();
        }
    }

    /**
     * @param bizPhone
     * @param businessName
     * @param bizAddress
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/find_phone",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "searchPhone", keyGenerator = "customKeyGenerator")
    public Set<String> searchPhone(
            @RequestParam ("term")
            ScrubbedInput bizPhone,

            @RequestParam ("nameParam")
            ScrubbedInput businessName,

            @RequestParam ("addressParam")
            ScrubbedInput bizAddress
    ) {
        try {
            LOG.info("searchPhone bizPhone={} bizAddress={} businessName={}",
                    bizPhone.getText(), bizAddress.getText(), businessName.getText());

            return fetcherService.findDistinctBizPhone(
                    CommonUtil.phoneCleanup(StringUtils.stripToEmpty(bizPhone.getText())),
                    StringUtils.stripToEmpty(bizAddress.getText()),
                    StringUtils.stripToEmpty(businessName.getText())
            );
        } catch (Exception fetchingPhone) {
            LOG.warn("Error fetching phone number, error={}", fetchingPhone);
            return new HashSet<>();
        }
    }

    /**
     * @param itemName
     * @param businessName
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/find_item",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "searchItem", keyGenerator = "customKeyGenerator")
    public Set<String> searchItem(
            @RequestParam ("term")
            ScrubbedInput itemName,

            @RequestParam ("nameParam")
            ScrubbedInput businessName
    ) {
        try {
            LOG.info("searchItem itemName={} businessName={}",
                    itemName.getText(), businessName.getText());

            return fetcherService.findDistinctItems(
                    StringUtils.stripToEmpty(itemName.getText()),
                    StringUtils.stripToEmpty(businessName.getText()));
        } catch (Exception fetchingItems) {
            LOG.warn("Error fetching items, error={}", fetchingItems);
            return new HashSet<>();
        }
    }

    /**
     * Gets all the pending receipt after a receipt is successfully uploaded.
     *
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/pending",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json")
    public String pendingReceipts() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("PENDING", landingService.pendingReceipt(receiptUser.getRid()));
            jsonObject.addProperty("REJECTED", landingService.rejectedReceipt(receiptUser.getRid()));
            return jsonObject.toString();
        } catch (Exception pendingReceipt) {
            LOG.error("Error fetching items, error={}", pendingReceipt);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("PENDING", 0);
            jsonObject.addProperty("REJECTED", 0);
            return jsonObject.toString();
        }
    }

    /**
     * Check if a duplicate receipt exists for the user.
     *
     * @param date
     * @param total
     * @param receiptUserId
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/check_for_duplicate",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    //@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicate Account")  // 409 //TODO something to think about
    public boolean checkForDuplicate(
            @RequestParam ("date")
            ScrubbedInput date,

            @RequestParam ("total")
            ScrubbedInput total,

            @RequestParam ("receiptUserId")
            ScrubbedInput receiptUserId
    ) throws IOException, ParseException, NumberFormatException {
        try {
            Date receiptDate = DateUtil.getDateFromString(StringUtils.stripToEmpty(date.getText()));
            Double receiptTotal = Formatter.getCurrencyFormatted(StringUtils.stripToEmpty(total.getText())).doubleValue();

            String checkSum = HashText.calculateChecksumForNotDeleted(
                    StringUtils.stripToEmpty(receiptUserId.getText()),
                    receiptDate,
                    receiptTotal
            );

            return documentUpdateService.hasReceiptWithSimilarChecksum(checkSum);
        } catch (ParseException parseException) {
            LOG.error("Ajax checkForDuplicate failed to parse total, error={}", parseException);
            throw parseException;
        }
    }

    /**
     * Update the orientation of the image.
     *
     * @param fileSystemId
     * @param imageOrientation
     * @param blobId
     * @param receiptUserId
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasAnyRole('ROLE_USER', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    @RequestMapping (
            value = "/change_fs_image_orientation",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json")
    public boolean changeFSImageOrientation(
            @RequestParam ("fileSystemId")
            ScrubbedInput fileSystemId,

            @RequestParam ("orientation")
            ScrubbedInput imageOrientation,

            @RequestParam ("blobId")
            ScrubbedInput blobId,

            @RequestParam ("receiptUserId")
            ScrubbedInput receiptUserId,

            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (request.isUserInRole("ROLE_ADMIN") ||
                request.isUserInRole("ROLE_TECHNICIAN") ||
                request.isUserInRole("ROLE_SUPERVISOR") ||
                receiptUserId.getText().equalsIgnoreCase(receiptUser.getRid())) {
            try {
                fetcherService.changeFSImageOrientation(
                        StringUtils.stripToEmpty(fileSystemId.getText()),
                        Integer.parseInt(StringUtils.stripToEmpty(imageOrientation.getText())),
                        blobId.getText()
                );
                return true;
            } catch (Exception e) {
                //Do nothing with the error message
                LOG.error("Failed to change orientation of the image, error={}", e.getLocalizedMessage(), e);
                return false;
            }
        } else {
            response.sendError(SC_FORBIDDEN, "Cannot access directly");
            return false;
        }
    }

    @PreAuthorize ("hasAnyRole('ROLE_USER')")
    @RequestMapping (
            value = "/updateReceiptExpenseTag",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    public String updateExpenseTagOfReceipt(
            @RequestParam ("receiptId")
            ScrubbedInput receiptId,

            @RequestParam ("expenseTagId")
            ScrubbedInput expenseTagId,

            HttpServletResponse response
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ReceiptEntity receipt = receiptService.findReceipt(receiptId.getText(), receiptUser.getRid());
        ReceiptExpenseTag receiptExpenseTag = new ReceiptExpenseTag("");
        if (null != receipt) {
            ExpenseTagEntity expenseTag = receiptService.updateReceiptExpenseTag(receipt, expenseTagId.getText());
            Assert.notNull(expenseTag, "ExpenseTag should not be null");
            receiptExpenseTag = new ReceiptExpenseTag(expenseTag.getTagColor());
            receiptExpenseTag.isSuccess();
        } else {
            response.sendError(SC_NOT_FOUND, "Could not find");
        }
        return receiptExpenseTag.asJson();
    }

    @PreAuthorize ("hasAnyRole('ROLE_USER')")
    @RequestMapping (
            value = "/updateItemExpenseTag",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    public void updateItemExpenseTag(
            @RequestParam ("itemId")
            ScrubbedInput itemId,

            @RequestParam ("expenseTagId")
            ScrubbedInput expenseTagId,

            HttpServletResponse response
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ItemEntity item = itemService.findItem(itemId.getText(), receiptUser.getRid());
        if (null != item) {
            itemService.updateItemWithExpenseTag(item.getId(), expenseTagId.getText());
        } else {
            response.sendError(SC_NOT_FOUND, "Could not find");
        }
    }
}
