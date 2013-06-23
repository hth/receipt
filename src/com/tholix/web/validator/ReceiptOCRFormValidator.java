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
public class ReceiptOCRFormValidator implements Validator {
    private static final Logger log = Logger.getLogger(ReceiptOCRFormValidator.class);

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
            for (ItemEntityOCR item : receiptOCRForm.getItems()) {
                if (StringUtils.isNotEmpty(item.getName()) && StringUtils.isNotEmpty(item.getPrice())) {
                    try {

                        subTotal = Maths.add(subTotal, Formatter.getCurrencyFormatted(item.getPrice()));
                    } catch (ParseException | NumberFormatException exception) {
                        log.error("Exception during update of receipt: " + receiptOCRForm.getReceiptOCR().getId() + ", with error message: " + exception.getLocalizedMessage());
                        errors.rejectValue("items[" + count + "].price", "field.currency", new Object[]{item.getPrice()}, "Unsupported currency format");
                    }
                    count++;
                }
            }
        } else {
            log.error("Exception during update of receipt: " + receiptOCRForm.getReceiptOCR().getId() + ", as no items were found");
            errors.rejectValue("receiptOCR", "field.required", new Object[]{"Item(s)"}, "Items required to submit a receipt");
        }

        if (StringUtils.isNotEmpty(receiptOCRForm.getReceiptOCR().getSubTotal())) {
            try {
                BigDecimal submittedSubTotal = Formatter.getCurrencyFormatted(receiptOCRForm.getReceiptOCR().getSubTotal());
                int comparedValue = submittedSubTotal.compareTo(subTotal);
                if (comparedValue > 0) {
                    errors.rejectValue("receiptOCR.subTotal", "field.currency.match.first", new Object[]{receiptOCRForm.getReceiptOCR().getSubTotal()}, "Summation not adding up");
                } else if (comparedValue < 0) {
                    errors.rejectValue("receiptOCR.subTotal", "field.currency.match.second", new Object[]{receiptOCRForm.getReceiptOCR().getSubTotal()}, "Summation not adding up");
                }
            } catch (ParseException e) {
                errors.rejectValue("receiptOCR.subTotal", "field.currency", new Object[]{receiptOCRForm.getReceiptOCR().getSubTotal()}, "Unsupported currency format");
            }
        }

        if (StringUtils.isNotEmpty(receiptOCRForm.getReceiptOCR().getTotal())) {
            try {
                Formatter.getCurrencyFormatted(receiptOCRForm.getReceiptOCR().getTotal());
            } catch (ParseException e) {
                errors.rejectValue("receiptOCR.total", "field.currency", new Object[]{receiptOCRForm.getReceiptOCR().getTotal()}, "Unsupported currency format");
            }
        }
    }
}
