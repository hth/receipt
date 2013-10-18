/**
 *
 */
package com.tholix.web.validator;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.domain.ItemEntityOCR;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;
import com.tholix.utils.Maths;
import com.tholix.web.form.ReceiptOCRForm;

/**
 * @author hitender
 * @since Jan 10, 2013 10:00:24 PM
 */
public final class ReceiptOCRValidator implements Validator {
    private static final Logger log = Logger.getLogger(ReceiptOCRValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return ReceiptOCRForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ReceiptOCRForm receiptOCRForm = (ReceiptOCRForm) obj;
        log.info("Executing validation for new receiptOCRForm: " + receiptOCRForm.getReceiptOCR().getId());

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receiptOCR.bizName.name",    "field.required", new Object[]{"Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receiptOCR.receiptDate",     "field.required", new Object[]{"Date"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receiptOCR.total",           "field.required", new Object[]{"Total"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receiptOCR.subTotal",        "field.required", new Object[]{"Sub Total"});

        try {
            DateUtil.getDateFromString(receiptOCRForm.getReceiptOCR().getReceiptDate());
        } catch (IllegalArgumentException exce) {
            errors.rejectValue("receiptOCR.receiptDate", "field.date", new Object[]{receiptOCRForm.getReceiptOCR().getReceiptDate()}, "Unsupported date format");
        }

        int count = 0;
        BigDecimal subTotal = BigDecimal.ZERO;
        if (receiptOCRForm.getItems() != null) {
            boolean conditionFailed = false;
            int conditionFailedCounter = 0;
            for (ItemEntityOCR item : receiptOCRForm.getItems()) {
                if (StringUtils.isNotEmpty(item.getName()) && StringUtils.isNotEmpty(item.getPrice()) && item.getQuantity() != null) {
                    try {
                        subTotal = Maths.add(subTotal, Maths.multiply(Formatter.getCurrencyFormatted(item.getPrice()), item.getQuantity()));
                    } catch (ParseException | NumberFormatException exception) {
                        log.error("Exception during update of receipt: " + receiptOCRForm.getReceiptOCR().getId() + ", with error message: " + exception.getLocalizedMessage());
                        errors.rejectValue("items[" + count + "].price", "field.currency", new Object[]{item.getPrice()}, "Unsupported currency format");
                    }
                } else {
                    /** Count need to check the condition below */
                    conditionFailed = true;
                    conditionFailedCounter ++;
                }
                count++;
            }

            /** This condition is added to make sure no receipt is added without at least one valid item in the list */
            if(conditionFailed && (receiptOCRForm.getItems().size() == conditionFailedCounter)) {
                log.error("Exception during update of receipt: " + receiptOCRForm.getReceiptOCR().getId() + ", as no items were found");
                errors.rejectValue("receiptOCR", "item.required", new Object[]{"Item(s)"}, "Items required to submit a receipt");
            }
        } else {
            log.error("Exception during update of receipt: " + receiptOCRForm.getReceiptOCR().getId() + ", as no items were found");
            errors.rejectValue("receiptOCR", "item.required", new Object[]{"Item(s)"}, "Items required to submit a receipt");
        }

        BigDecimal submittedSubTotal = null;
        if (StringUtils.isNotEmpty(receiptOCRForm.getReceiptOCR().getSubTotal())) {
            try {
                submittedSubTotal = Formatter.getCurrencyFormatted(receiptOCRForm.getReceiptOCR().getSubTotal());
                subTotal = Maths.adjustScale(subTotal);
                int comparedValue = submittedSubTotal.compareTo(subTotal);
                if (comparedValue > 0) {
                    if(!Maths.withInRange(submittedSubTotal, subTotal)) {
                        errors.rejectValue("receiptOCR.subTotal", "field.currency.match.first",
                                new Object[]{receiptOCRForm.getReceiptOCR().getSubTotal(), subTotal.toString()},
                                "Summation not adding up");
                    } else {
                        log.warn("Found difference in Calculated subTotal: " + subTotal +
                                ", submittedSubTotal: " + submittedSubTotal +
                                ". Which is less than application specified diff of " +
                                Maths.ACCEPTED_RANGE_IN_LOWEST_DENOMINATION);
                    }

                } else if (comparedValue < 0) {
                    if(!Maths.withInRange(submittedSubTotal, subTotal)) {
                        errors.rejectValue("receiptOCR.subTotal", "field.currency.match.second",
                            new Object[]{receiptOCRForm.getReceiptOCR().getSubTotal(), subTotal.toString()},
                            "Summation not adding up");
                    } else {
                        log.warn("Found difference in Calculated subTotal: " + subTotal +
                                ", submittedSubTotal: " + submittedSubTotal +
                                ". Which is less than application specified diff of " +
                                Maths.ACCEPTED_RANGE_IN_LOWEST_DENOMINATION);
                    }
                }
            } catch (ParseException | NumberFormatException e) {
                errors.rejectValue("receiptOCR.subTotal", "field.currency", new Object[]{receiptOCRForm.getReceiptOCR().getSubTotal()}, "Unsupported currency format");
            }
        }

        /** Compute total = tax + subtotal with provided total */
        BigDecimal total = null;
        if (StringUtils.isNotEmpty(receiptOCRForm.getReceiptOCR().getTotal())) {
            try {
                total = Formatter.getCurrencyFormatted(receiptOCRForm.getReceiptOCR().getTotal());
            } catch (ParseException | NumberFormatException e) {
                errors.rejectValue("receiptOCR.total", "field.currency", new Object[]{receiptOCRForm.getReceiptOCR().getTotal()}, "Unsupported currency format");
            }

            try {
                if(submittedSubTotal != null && total != null) {
                    BigDecimal tax = Formatter.getCurrencyFormatted(receiptOCRForm.getReceiptOCR().getTax());
                    //Since this is going to be displayed to user setting the scale to two.
                    BigDecimal calculatedTotal = Maths.add(submittedSubTotal, tax).setScale(Maths.SCALE_TWO);
                    if(calculatedTotal.compareTo(total) != 0) {
                        errors.rejectValue("receiptOCR.total", "field.receipt.total",
                                new Object[]{receiptOCRForm.getReceiptOCR().getTotal(), calculatedTotal.toString()},
                                "Summation not adding up");
                    }
                } else {
                    errors.rejectValue("receiptOCR.total", "field.currency.cannot.compute", new Object[]{receiptOCRForm.getReceiptOCR().getTotal()}, "Cannot compute because of previous error(s)");
                }
            } catch (ParseException | NumberFormatException exception) {
                log.error("Exception during update of receipt: " + receiptOCRForm.getReceiptOCR().getId() + ", with error message: " + exception.getLocalizedMessage());
                errors.rejectValue("receiptOCR.tax", "field.currency", new Object[]{receiptOCRForm.getReceiptOCR().getTax()}, "Unsupported currency format");
            }
        }
    }
}
