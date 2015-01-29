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
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public final class ExpenseTypeValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTypeValidator.class);
    private static final int EXPENSE_TAG_MAX_CHAR = 12;
    private static final int EXPENSE_COLOR_TAG_MAX_CHAR = 7;

    @Override
    public boolean supports(Class<?> clazz) {
        return ExpenseTagEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tagName", "field.required", new Object[]{"Tag Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tagColor", "field.required", new Object[]{"Tag Color"});

        ExpenseTypeForm expenseTypeForm = (ExpenseTypeForm) obj;
        if (expenseTypeForm.getTagName() != null && expenseTypeForm.getTagName().length() > EXPENSE_TAG_MAX_CHAR) {
            LOG.error("Expense Tag '{}' greater than size={} ", expenseTypeForm.getTagName(), EXPENSE_TAG_MAX_CHAR);
            errors.rejectValue(
                    "tagName",
                    "expenseTag.tagName",
                    new Object[]{EXPENSE_TAG_MAX_CHAR},
                    "Tag Name cannot extend " + EXPENSE_TAG_MAX_CHAR + " characters");
        }

        if (expenseTypeForm.getTagColor() != null && expenseTypeForm.getTagColor().length() > EXPENSE_COLOR_TAG_MAX_CHAR) {
            LOG.error("Expense Tag '{}' greater than size={} ", expenseTypeForm.getTagName(), EXPENSE_COLOR_TAG_MAX_CHAR);
            errors.rejectValue(
                    "tagColor",
                    "expenseTag.tagColor",
                    new Object[]{EXPENSE_COLOR_TAG_MAX_CHAR},
                    "Tag Color cannot extend " + EXPENSE_COLOR_TAG_MAX_CHAR + " characters");
        }

        if (expenseTypeForm.getTagColor() != null && !expenseTypeForm.getTagColor().startsWith("#")) {
            LOG.error("Expense Tag '{}' invalid", expenseTypeForm.getTagColor());
            errors.rejectValue(
                    "tagColor",
                    "expenseTag.tagColor",
                    new Object[]{EXPENSE_COLOR_TAG_MAX_CHAR},
                    "Tag color is missing character ''#'' from hex code");
        }
    }
}
