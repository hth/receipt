package com.receiptofi.web.validator;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.types.CardNetworkEnum;
import com.receiptofi.service.BizService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.PaymentCardService;
import com.receiptofi.web.form.ReceiptDocumentForm;

import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

/**
 * User: hitender
 * Date: 12/18/16 11:09 AM
 */
public class ReceiptDocumentValidatorTest {

    @Mock private ExternalService externalService;
    @Mock private BizService bizService;
    @Mock private PaymentCardService paymentCardService;

    private ReceiptDocumentValidator receiptDocumentValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        receiptDocumentValidator = new ReceiptDocumentValidator(externalService, bizService, paymentCardService);
    }

    @Test
    public void validatePaymentCard() throws Exception {
        ReceiptDocumentForm receiptDocument = ReceiptDocumentForm.newInstance();
        DocumentEntity document = DocumentEntity.newInstance();
        document.setCardDigit("0909");
        document.setCardNetwork(CardNetworkEnum.A);
        document.setReceiptUserId("123");

        ItemEntityOCR itemEntityOCR = ItemEntityOCR.newInstance();
        itemEntityOCR.setReceiptUserId("123");
        receiptDocument.setReceiptDocument(document);
        receiptDocument.setItems(Collections.singletonList(itemEntityOCR));

        Errors errors = new BeanPropertyBindingResult(receiptDocument, "receiptDocumentForm");
        receiptDocumentValidator.validate(receiptDocument, errors);

        Assert.isNull(errors.getFieldError("receiptDocument.cardNetwork"));
        Assert.isNull(errors.getFieldError("receiptDocument.cardDigit"));
    }

}