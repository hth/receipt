package com.receiptofi.web.validator;

import com.receiptofi.domain.ExpenseTypeEntity;
import com.receiptofi.web.form.ExpenseTypeForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 5/14/13
 * Time: 7:08 PM
 */
public final class ExpenseTypeValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(ExpenseTypeValidator.class);
    private static int EXPENSE_TYPE_MAX_CHAR = 6;

    @Override
    public boolean supports(Class<?> clazz) {
        return ExpenseTypeEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new ExpenseTypeEntity");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expName", "field.required", new Object[] { "Expense Name" });

        ExpenseTypeForm expenseTypeForm = (ExpenseTypeForm) obj;
        if(expenseTypeForm.getExpName() != null && expenseTypeForm.getExpName().length() > EXPENSE_TYPE_MAX_CHAR) {
            log.error("Size of the Expense Type larger than " + EXPENSE_TYPE_MAX_CHAR + " : " + expenseTypeForm.getExpName());
            errors.rejectValue("expName", "expenseType.expName", new Object[] { EXPENSE_TYPE_MAX_CHAR }, "Expense Name cannot extend " + EXPENSE_TYPE_MAX_CHAR + " characters ");
        }
    }
}
