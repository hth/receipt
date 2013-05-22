package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.domain.ExpenseTypeEntity;

/**
 * User: hitender
 * Date: 5/14/13
 * Time: 7:08 PM
 */
public class ExpenseTypeValidator implements Validator {
    private static final Logger log = Logger.getLogger(ExpenseTypeValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return ExpenseTypeEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new ExpenseTypeEntity");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expName",   "field.required", new Object[]{"Expense Name"});

        ExpenseTypeEntity expenseType = (ExpenseTypeEntity) obj;
        if(expenseType.getExpName() !=null && expenseType.getExpName().length() > 12) {
            log.error("Size of the Expense Type larger than 12 : " + expenseType.getExpName());
            errors.rejectValue("expName", "expenseType.expName", new Object[] { expenseType.getExpName() }, "Expense Name cannot extend 12 characters ");
        }
    }
}
