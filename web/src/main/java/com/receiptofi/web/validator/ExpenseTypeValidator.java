package com.receiptofi.web.validator;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.web.form.ExpenseTypeForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 5/14/13
 * Time: 7:08 PM
 */
@Component
public final class ExpenseTypeValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTypeValidator.class);
    private static final int EXPENSE_TAG_MAX_CHAR = 6;

    @Override
    public boolean supports(Class<?> clazz) {
        return ExpenseTagEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tagName", "field.required", new Object[]{"Tag Name"});

        ExpenseTypeForm expenseTypeForm = (ExpenseTypeForm) obj;
        if (expenseTypeForm.getTagName() != null && expenseTypeForm.getTagName().length() > EXPENSE_TAG_MAX_CHAR) {
            LOG.error("Expense Tag '{}' greater than size={} ", expenseTypeForm.getTagName(), EXPENSE_TAG_MAX_CHAR);
            errors.rejectValue(
                    "tagName",
                    "expenseTag.tagName",
                    new Object[]{EXPENSE_TAG_MAX_CHAR},
                    "Tag Name cannot extend " + EXPENSE_TAG_MAX_CHAR + " characters ");
        }
    }
}
