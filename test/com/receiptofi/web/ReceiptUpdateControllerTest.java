/**
 *
 */
package com.receiptofi.web;

import com.receiptofi.domain.UserSession;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.web.controller.ReceiptUpdateController;
import com.receiptofi.web.validator.ReceiptDocumentValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author hitender
 * @since Mar 24, 2013 10:08:19 PM
 *
 */
public class ReceiptUpdateControllerTest {

	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;
	@Autowired private DocumentManager documentManager;
	@Autowired private ItemOCRManager itemOCRManager;
	@Autowired private ReceiptDocumentValidator receiptDocumentValidator;

	private ReceiptUpdateController controller;
	private UserSession userSession;

    @Mock private BindingResult result;
    @Mock RedirectAttributes redirectAttrs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		controller = new ReceiptUpdateController();

		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.receiptofi.web.controller.ReceiptUpdateController#loadForm(java.lang.String, com.receiptofi.web.form.ReceiptDocumentForm)}.
	 */
	@Test
	public void testLoadForm() {
		//controller.loadForm(id, receiptDocumentForm);
	}

	/**
	 * Test method for {@link com.receiptofi.web.controller.ReceiptUpdateController#post(com.receiptofi.web.form.ReceiptDocumentForm, javax.servlet.http.HttpSession, org.springframework.validation.BindingResult, org.springframework.web.servlet.mvc.support.RedirectAttributes)}.
	 */
	@Test
	public void testPost() {
		//controller.post(receiptDocumentForm, session, result, redirectAttrs);
	}

}
