package com.receiptofi.web.validator;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.web.form.ExpenseTagForm;

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
public class ExpenseTagValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTagValidator.class);
    private static final int EXPENSE_TAG_MAX_CHAR = 12;
    private static final int EXPENSE_COLOR_TAG_MAX_CHAR = 7;

    @Override
    public boolean supports(Class<?> clazz) {
        return ExpenseTagEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tagName", "field.required", new Object[]{"Tag name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tagColor", "field.required", new Object[]{"Tag color"});

        ExpenseTagForm expenseTagForm = (ExpenseTagForm) obj;
        if (expenseTagForm.getTagName() != null && expenseTagForm.getTagName().length() > EXPENSE_TAG_MAX_CHAR) {
            LOG.error("Expense Tag '{}' greater than size={} ", expenseTagForm.getTagName(), EXPENSE_TAG_MAX_CHAR);
            errors.rejectValue(
                    "tagName",
                    "expenseTag.tagName",
                    new Object[]{EXPENSE_TAG_MAX_CHAR},
                    "Tag name cannot exceed " + EXPENSE_TAG_MAX_CHAR + " characters.");
        }

        if (expenseTagForm.getTagColor() != null && expenseTagForm.getTagColor().length() > EXPENSE_COLOR_TAG_MAX_CHAR) {
            LOG.error("Expense Tag '{}' greater than size={} ", expenseTagForm.getTagName(), EXPENSE_COLOR_TAG_MAX_CHAR);
            errors.rejectValue(
                    "tagColor",
                    "expenseTag.tagColor",
                    new Object[]{EXPENSE_COLOR_TAG_MAX_CHAR},
                    "Tag color cannot extend " + EXPENSE_COLOR_TAG_MAX_CHAR + " characters.");
        }

        if (expenseTagForm.getTagColor() != null && !expenseTagForm.getTagColor().startsWith("#")) {
            LOG.error("Expense Tag '{}' invalid", expenseTagForm.getTagColor());
            errors.rejectValue(
                    "tagColor",
                    "expenseTag.tagColor",
                    new Object[]{EXPENSE_COLOR_TAG_MAX_CHAR},
                    "Tag color is missing character ''#'' from hex code.");
        }
    }
}
