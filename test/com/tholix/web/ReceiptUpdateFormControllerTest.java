/**
 * 
 */
package com.tholix.web;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.UserSession;
import com.tholix.service.ItemManager;
import com.tholix.service.ItemOCRManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.service.validator.ReceiptFormValidator;

/**
 * @author hitender 
 * @when Mar 24, 2013 10:08:19 PM
 *
 */
public class ReceiptUpdateFormControllerTest {
	
	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ReceiptOCRManager receiptOCRManager;	
	@Autowired private ItemOCRManager itemOCRManager;
	@Autowired private ReceiptFormValidator receiptFormValidator;
	
	private ReceiptUpdateFormController controller;
	private UserSession userSession;
    
    @Mock private BindingResult result;
    @Mock RedirectAttributes redirectAttrs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		controller = new ReceiptUpdateFormController();
		
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
	 * Test method for {@link com.tholix.web.ReceiptUpdateFormController#loadForm(java.lang.String, com.tholix.web.form.ReceiptForm)}.
	 */
	@Test
	public void testLoadForm() {
		//controller.loadForm(id, receiptForm);
	}

	/**
	 * Test method for {@link com.tholix.web.ReceiptUpdateFormController#post(com.tholix.web.form.ReceiptForm, javax.servlet.http.HttpSession, org.springframework.validation.BindingResult, org.springframework.web.servlet.mvc.support.RedirectAttributes)}.
	 */
	@Test
	public void testPost() {
		//controller.post(receiptForm, session, result, redirectAttrs);
	}

}
