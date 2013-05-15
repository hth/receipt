/**
 *
 */
package com.tholix.web.validator;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.domain.ItemEntityOCR;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;
import com.tholix.web.form.ReceiptForm;

/**
 * @author hitender
 * @since Jan 10, 2013 10:00:24 PM
 *
 */
public class ReceiptFormValidator implements Validator {
	private static final Logger log = Logger.getLogger(ReceiptFormValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return ReceiptForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.info("Executing validation for new receiptForm");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.bizName.name",   "field.required", new Object[] { "Name" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.receiptDate",    "field.required", new Object[] { "Date" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.total",          "field.required", new Object[] { "Total" });
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.subTotal",       "field.required", new Object[] { "Sub Total" });

		ReceiptForm receiptForm = (ReceiptForm) obj;
		try {
			DateUtil.getDateFromString(receiptForm.getReceipt().getReceiptDate());
		} catch(Exception exce) {
			errors.rejectValue("receipt.receiptDate", "field.date", new Object[] { receiptForm.getReceipt().getReceiptDate() }, "Unsupported date format");
		}

		int count = 0;
        Double subTotal = 0.00;
		for(ItemEntityOCR item : receiptForm.getItems()) {
			if(StringUtils.isNotEmpty(item.getName()) && StringUtils.isNotEmpty(item.getPrice())) {
				 try {
					subTotal = subTotal + Formatter.getCurrencyFormatted(item.getPrice());
				} catch (ParseException e) {
					errors.rejectValue("items["+count+"].price", "field.currency", new Object[] { item.getPrice() }, "Unsupported currency format");
				}
				count++;
			}
		}

        if(StringUtils.isNotEmpty(receiptForm.getReceipt().getSubTotal())) {
            try {
                Double submittedSubTotal = Formatter.getCurrencyFormatted(receiptForm.getReceipt().getSubTotal());
                int comparedValue = submittedSubTotal.compareTo(subTotal);
                if (comparedValue > 0) {
                    errors.rejectValue("receipt.subTotal", "field.currency.match.first", new Object[] { receiptForm.getReceipt().getSubTotal() }, "Summation not adding up");
                } else if (comparedValue < 0) {
                    errors.rejectValue("receipt.subTotal", "field.currency.match.second", new Object[] { receiptForm.getReceipt().getSubTotal() }, "Summation not adding up");
                }
            } catch (ParseException e) {
                errors.rejectValue("receipt.subTotal", "field.currency", new Object[] { receiptForm.getReceipt().getSubTotal() }, "Unsupported currency format");
            }
        }

		if(StringUtils.isNotEmpty(receiptForm.getReceipt().getTotal())) {
			try {
				Formatter.getCurrencyFormatted(receiptForm.getReceipt().getTotal());
			} catch (ParseException e) {
				errors.rejectValue("receipt.total", "field.currency", new Object[] { receiptForm.getReceipt().getTotal() }, "Unsupported currency format");
			}
		}
	}

}
