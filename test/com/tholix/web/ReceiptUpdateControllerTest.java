/**
 *
 */
package com.tholix.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tholix.domain.UserSession;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ItemOCRManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.web.validator.ReceiptOCRFormValidator;

/**
 * @author hitender
 * @since Mar 24, 2013 10:08:19 PM
 *
 */
public class ReceiptUpdateControllerTest {

	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ReceiptOCRManager receiptOCRManager;
	@Autowired private ItemOCRManager itemOCRManager;
	@Autowired private ReceiptOCRFormValidator receiptOCRFormValidator;

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
	 * Test method for {@link com.tholix.web.ReceiptUpdateController#loadForm(java.lang.String, com.tholix.web.form.ReceiptOCRForm)}.
	 */
	@Test
	public void testLoadForm() {
		//controller.loadForm(id, receiptOCRForm);
	}

	/**
	 * Test method for {@link com.tholix.web.ReceiptUpdateController#post(com.tholix.web.form.ReceiptOCRForm, javax.servlet.http.HttpSession, org.springframework.validation.BindingResult, org.springframework.web.servlet.mvc.support.RedirectAttributes)}.
	 */
	@Test
	public void testPost() {
		//controller.post(receiptOCRForm, session, result, redirectAttrs);
	}

}
