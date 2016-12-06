package com.receiptofi.web.validator;

import com.receiptofi.domain.types.DocumentRejectReasonEnum;
import com.receiptofi.web.form.ReceiptDocumentForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 9/13/15 3:10 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class DocumentRejectValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentRejectValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return ReceiptDocumentForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ReceiptDocumentForm receiptDocumentForm = (ReceiptDocumentForm) target;
        LOG.debug("Executing validation for new receiptDocument: " + receiptDocumentForm.getReceiptDocument().getId());

        if (DocumentRejectReasonEnum.G == receiptDocumentForm.getReceiptDocument().getDocumentRejectReason()) {
            errors.rejectValue(
                    "receiptDocument.documentRejectReason",
                    "document.reject.reason",
                    new Object[]{DocumentRejectReasonEnum.G.getDescription()},
                    "Document reject reason cannot be " + DocumentRejectReasonEnum.G.getDescription() + ". Pick a valid reason from drop down.");
        }
    }
}
