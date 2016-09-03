package com.receiptofi.web.validator;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.CreditCardEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.types.CardNetworkEnum;
import com.receiptofi.service.BizService;
import com.receiptofi.service.CreditCardService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.Maths;
import com.receiptofi.web.form.ReceiptDocumentForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author hitender
 * @since Jan 10, 2013 10:00:24 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class ReceiptDocumentValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptDocumentValidator.class);

    private ExternalService externalService;
    private BizService bizService;
    private CreditCardService creditCardService;

    @Autowired
    public ReceiptDocumentValidator(
            ExternalService externalService,
            BizService bizService,
            CreditCardService creditCardService
    ) {
        this.externalService = externalService;
        this.bizService = bizService;
        this.creditCardService = creditCardService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ReceiptDocumentForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ReceiptDocumentForm receiptDocumentForm = (ReceiptDocumentForm) obj;
        LOG.debug("Validating receiptDocument={}", receiptDocumentForm.getReceiptDocument().getId());

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.bizName.businessName",
                "field.required",
                new Object[]{"Biz Name"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.receiptDate",
                "field.required",
                new Object[]{"Date"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.bizStore.address",
                "field.required",
                new Object[]{"Address"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.bizStore.phone",
                "field.required",
                new Object[]{"Phone"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.total",
                "field.required",
                new Object[]{"Total"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.subTotal",
                "field.required",
                new Object[]{"Sub Total"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "receiptDocument.tax",
                "field.required",
                new Object[]{"Tax"}
        );

        validateDate(errors, receiptDocumentForm);
        validateAddressAndPhone(errors, receiptDocumentForm);

        int count = 0;
        BigDecimal subTotal = BigDecimal.ZERO;
        if (null == receiptDocumentForm.getItems()) {
            LOG.error(
                    "No items found for receipt={}",
                    receiptDocumentForm.getReceiptDocument().getId()
            );
            errors.rejectValue(
                    "receiptDocumentForm",
                    "item.required",
                    new Object[]{"Item(s)"},
                    "Items required to submit a receipt"
            );
        } else {
            boolean conditionFailed = false;
            int conditionFailedCounter = 0;
            for (ItemEntityOCR item : receiptDocumentForm.getItems()) {
                if (StringUtils.isNotEmpty(item.getName()) &&
                        StringUtils.isNotEmpty(item.getPrice()) &&
                        item.getQuantity() != null) {
                    try {
                        subTotal = Maths.add(
                                subTotal,
                                Maths.multiply(Formatter.getCurrencyFormatted(item.getPrice()), item.getQuantity())
                        );
                    } catch (ParseException | NumberFormatException exception) {
                        LOG.warn(
                                "Validation exception during update of receipt={}, with error message={}",
                                receiptDocumentForm.getReceiptDocument().getId(),
                                exception.getLocalizedMessage(),
                                exception
                        );
                        errors.rejectValue(
                                "items[" + count + "].price",
                                "field.currency",
                                new Object[]{item.getPrice()},
                                "Unsupported currency format"
                        );
                    }
                } else {
                    /** Count need to check the condition below */
                    conditionFailed = true;
                    conditionFailedCounter++;
                }
                count++;
            }

            /** This condition is added to make sure no receipt is added without at least one valid item in the list */
            if (conditionFailed && receiptDocumentForm.getItems().size() == conditionFailedCounter) {
                LOG.warn(
                        "Validation exception during update of receipt={}, as no items were found",
                        receiptDocumentForm.getReceiptDocument().getId()
                );
                errors.rejectValue(
                        "receiptDocument",
                        "item.required",
                        new Object[]{"Item(s)"},
                        "Items required to submit a receipt"
                );
            }
        }

        BigDecimal submittedSubTotal = null;
        if (StringUtils.isNotEmpty(receiptDocumentForm.getReceiptDocument().getSubTotal())) {
            try {
                submittedSubTotal = Formatter.getCurrencyFormatted(receiptDocumentForm.getReceiptDocument().getSubTotal());
                subTotal = Maths.adjustScale(subTotal);
                int comparedValue = submittedSubTotal.compareTo(subTotal);
                if (comparedValue > 0) {
                    if (Maths.withInRange(submittedSubTotal, subTotal)) {
                        LOG.warn("Found difference in Calculated subTotal: " + subTotal +
                                ", submittedSubTotal: " + submittedSubTotal +
                                ". Which is less than application specified diff of " +
                                Maths.ACCEPTED_RANGE_IN_LOWEST_DENOMINATION);
                    } else {
                        errors.rejectValue("receiptDocument.subTotal", "field.currency.match.first",
                                new Object[]{receiptDocumentForm.getReceiptDocument().getSubTotal(), subTotal.toString()},
                                "Summation not adding up");
                    }

                } else if (comparedValue < 0) {
                    if (Maths.withInRange(submittedSubTotal, subTotal)) {
                        LOG.warn("Found difference in Calculated subTotal: " + subTotal +
                                ", submittedSubTotal: " + submittedSubTotal +
                                ". Which is less than application specified diff of " +
                                Maths.ACCEPTED_RANGE_IN_LOWEST_DENOMINATION);
                    } else {
                        errors.rejectValue("receiptDocument.subTotal", "field.currency.match.second",
                                new Object[]{receiptDocumentForm.getReceiptDocument().getSubTotal(), subTotal.toString()},
                                "Summation not adding up");
                    }
                }
            } catch (ParseException | NumberFormatException e) {
                errors.rejectValue(
                        "receiptDocument.subTotal",
                        "field.currency",
                        new Object[]{receiptDocumentForm.getReceiptDocument().getSubTotal()},
                        "Unsupported currency format"
                );
            }
        }

        /** Compute total = tax + subtotal with provided total */
        BigDecimal total = null;
        if (StringUtils.isNotEmpty(receiptDocumentForm.getReceiptDocument().getTotal())) {
            try {
                total = Formatter.getCurrencyFormatted(receiptDocumentForm.getReceiptDocument().getTotal());
            } catch (ParseException | NumberFormatException e) {
                errors.rejectValue(
                        "receiptDocument.total",
                        "field.currency",
                        new Object[]{receiptDocumentForm.getReceiptDocument().getTotal()},
                        "Unsupported currency format"
                );
            }

            try {
                if (null == submittedSubTotal || null == total) {
                    errors.rejectValue(
                            "receiptDocument.total",
                            "field.currency.cannot.compute",
                            new Object[]{receiptDocumentForm.getReceiptDocument().getTotal()},
                            "Cannot compute because of previous error(s)"
                    );
                } else {
                    if (StringUtils.isNotBlank(receiptDocumentForm.getReceiptDocument().getTax())) {
                        BigDecimal tax = Formatter.getCurrencyFormatted(receiptDocumentForm.getReceiptDocument().getTax());
                        //Since this is going to be displayed to user setting the scale to two.
                        BigDecimal calculatedTotal = Maths.add(submittedSubTotal, tax).setScale(Maths.SCALE_TWO);
                        if (calculatedTotal.compareTo(total) != 0) {
                            errors.rejectValue("receiptDocument.total", "field.receipt.total",
                                    new Object[]{receiptDocumentForm.getReceiptDocument().getTotal(), calculatedTotal.toString()},
                                    "Summation not adding up");
                        }
                    }
                }
            } catch (ParseException | NumberFormatException exception) {
                LOG.warn(
                        "Validation exception during update of receipt={}, with error message={}",
                        receiptDocumentForm.getReceiptDocument().getId(),
                        exception.getLocalizedMessage(),
                        exception
                );
                errors.rejectValue(
                        "receiptDocument.tax",
                        "field.currency",
                        new Object[]{receiptDocumentForm.getReceiptDocument().getTax()},
                        "Unsupported currency format"
                );
            }
        }
    }

    private void validateDate(Errors errors, ReceiptDocumentForm receiptDocumentForm) {
        try {
            Date receiptDate = DateUtil.getDateFromString(receiptDocumentForm.getReceiptDocument().getReceiptDate());
            /* Since mid-night hence two days minus 60 seconds for previous day. */
            Date nextDay = Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant().minusSeconds(60));
            if (receiptDate.after(nextDay)) {
                errors.rejectValue(
                        "receiptDocument.receiptDate",
                        "field.date.future",
                        new Object[]{receiptDocumentForm.getReceiptDocument().getReceiptDate()},
                        "Date is set in future. Format should be MM/dd/yyyy 11:59:59 PM. Check for month and day.");
            }

            Date previousDay = Date.from(LocalDate.now().minusDays(60).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(60));
            if(receiptDate.before(previousDay)) {
                errors.rejectValue(
                        "receiptDocument.receiptDate",
                        "field.date.past",
                        new Object[]{receiptDocumentForm.getReceiptDocument().getReceiptDate()},
                        "Date is set more than 60 days in past. Format should be MM/dd/yyyy 11:59:59 PM. Check for month and day.");
            }
        } catch (IllegalArgumentException exce) {
            errors.rejectValue(
                    "receiptDocument.receiptDate",
                    "field.date",
                    new Object[]{receiptDocumentForm.getReceiptDocument().getReceiptDate()},
                    "Unsupported date format");
        }
    }

    /**
     * Validate if business address and phone exists for another business name.
     *
     * @param errors
     * @param receiptDocumentForm
     */
    private void validateAddressAndPhone(Errors errors, ReceiptDocumentForm receiptDocumentForm) {
        BizNameEntity bizName = receiptDocumentForm.getReceiptDocument().getBizName();
        BizStoreEntity bizStore = receiptDocumentForm.getReceiptDocument().getBizStore();

        BizStoreEntity foundStore = bizService.findMatchingStore(bizStore.getAddress(), bizStore.getPhone());
        if (null == foundStore && StringUtils.isNotBlank(bizStore.getAddress())) {
            externalService.decodeAddress(bizStore);
            foundStore = bizService.findMatchingStore(bizStore.getAddress(), bizStore.getPhone());
        }

        if (null != foundStore && !foundStore.getBizName().getBusinessName().equals(bizName.getBusinessName())) {
            errors.rejectValue(
                    "receiptDocument.bizName.businessName",
                    "field.businessName",
                    new Object[]{foundStore.getBizName().getBusinessName(), bizName.getBusinessName()},
                    foundStore.getBizName().getBusinessName() + " is sharing the same address and phone number as " + bizName.getBusinessName());
        }
    }
}
