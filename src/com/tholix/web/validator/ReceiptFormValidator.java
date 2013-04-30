/**
 *
 */
package com.tholix.web.validator;

import java.text.ParseException;

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
 * @when Jan 10, 2013 10:00:24 PM
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

		ReceiptForm receiptForm = (ReceiptForm) obj;
		try {
			DateUtil.getDateFromString(receiptForm.getReceipt().getReceiptDate());
		} catch(Exception exce) {
			errors.rejectValue("receipt.receiptDate", "field.date", new Object[] { receiptForm.getReceipt().getReceiptDate() }, "Unsupported date format");
		}

		int count = 0;
        Double subTotal = 0.00;
		for(ItemEntityOCR item : receiptForm.getItems()) {
			if(!item.getName().isEmpty()) {
				 try {
					subTotal = subTotal + Formatter.getCurrencyFormatted(item.getPrice());
				} catch (ParseException e) {
					errors.rejectValue("items["+count+"].price", "field.currency", new Object[] { item.getPrice() }, "Unsupported currency format");
				}
				count++;
			}
		}

        if(!receiptForm.getReceipt().getSubTotal().isEmpty()) {
            try {
                Double submittedSubTotal = Formatter.getCurrencyFormatted(receiptForm.getReceipt().getSubTotal());
                int comparedValue = submittedSubTotal.compareTo(subTotal);
                if (comparedValue > 0) {
                    errors.rejectValue("receipt.subTotal", "field.currency.match.first", new Object[] { receiptForm.getReceipt().getSubTotal() }, "Summation not adding up");
                    System.out.println("First is grater");
                } else if (comparedValue < 0) {
                    errors.rejectValue("receipt.subTotal", "field.currency.match.second", new Object[] { receiptForm.getReceipt().getSubTotal() }, "Summation not adding up");
                    System.out.println("Second is grater");
                }
            } catch (ParseException e) {
                errors.rejectValue("receipt.subTotal", "field.currency", new Object[] { receiptForm.getReceipt().getSubTotal() }, "Unsupported currency format");
            }
        }

		if(!receiptForm.getReceipt().getTotal().isEmpty()) {
			try {
				Formatter.getCurrencyFormatted(receiptForm.getReceipt().getTotal());
			} catch (ParseException e) {
				errors.rejectValue("receipt.total", "field.currency", new Object[] { receiptForm.getReceipt().getTotal() }, "Unsupported currency format");
			}
		}
	}

}
