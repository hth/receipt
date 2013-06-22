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
 *
 */
public class ReceiptOCRFormValidator implements Validator {
	private static final Logger log = Logger.getLogger(ReceiptOCRFormValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return ReceiptOCRForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.info("Executing validation for new receiptOCRForm");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.bizName.name",   "field.required", new Object[] { "Name" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.receiptDate",    "field.required", new Object[] { "Date" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.total",          "field.required", new Object[] { "Total" });
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.subTotal",       "field.required", new Object[] { "Sub Total" });

		ReceiptOCRForm receiptOCRForm = (ReceiptOCRForm) obj;
		try {
			DateUtil.getDateFromString(receiptOCRForm.getReceipt().getReceiptDate());
		} catch(Exception exce) {
			errors.rejectValue("receipt.receiptDate", "field.date", new Object[] { receiptOCRForm.getReceipt().getReceiptDate() }, "Unsupported date format");
		}

		int count = 0;
        BigDecimal subTotal = BigDecimal.ZERO;
		for(ItemEntityOCR item : receiptOCRForm.getItems()) {
			if(StringUtils.isNotEmpty(item.getName()) && StringUtils.isNotEmpty(item.getPrice())) {
				 try {
					subTotal = Maths.add(subTotal, Formatter.getCurrencyFormatted(item.getPrice()));
				} catch (ParseException | NumberFormatException exception) {
                     log.error("Exception during update of receipt: " + receiptOCRForm.getReceipt().getId() + ", with error message: " + exception.getLocalizedMessage());
					errors.rejectValue("items["+count+"].price", "field.currency", new Object[] { item.getPrice() }, "Unsupported currency format");
				}
				count++;
			}
		}

        if(StringUtils.isNotEmpty(receiptOCRForm.getReceipt().getSubTotal())) {
            try {
                BigDecimal submittedSubTotal = Formatter.getCurrencyFormatted(receiptOCRForm.getReceipt().getSubTotal());
                int comparedValue = submittedSubTotal.compareTo(subTotal);
                if (comparedValue > 0) {
                    errors.rejectValue("receipt.subTotal", "field.currency.match.first", new Object[] { receiptOCRForm.getReceipt().getSubTotal() }, "Summation not adding up");
                } else if (comparedValue < 0) {
                    errors.rejectValue("receipt.subTotal", "field.currency.match.second", new Object[] { receiptOCRForm.getReceipt().getSubTotal() }, "Summation not adding up");
                }
            } catch (ParseException e) {
                errors.rejectValue("receipt.subTotal", "field.currency", new Object[] { receiptOCRForm.getReceipt().getSubTotal() }, "Unsupported currency format");
            }
        }

		if(StringUtils.isNotEmpty(receiptOCRForm.getReceipt().getTotal())) {
			try {
				Formatter.getCurrencyFormatted(receiptOCRForm.getReceipt().getTotal());
			} catch (ParseException e) {
				errors.rejectValue("receipt.total", "field.currency", new Object[] { receiptOCRForm.getReceipt().getTotal() }, "Unsupported currency format");
			}
		}
	}
}
