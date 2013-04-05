/**
 * 
 */
package com.tholix.web;

import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tholix.service.StorageManager;

/**
 * @author hitender 
 * @when Mar 23, 2013 11:04:26 PM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class ReceiptImageControllerTest {

	@Autowired private StorageManager storageManager;

    @Mock private BindingResult result;
    
	private MockHttpServletRequest request;  
    private MockHttpServletResponse response;  
    private MockHttpSession session;
	
	private ReceiptImageController controller;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		controller = new ReceiptImageController();		
		session = new MockHttpSession();		
		request = new MockHttpServletRequest();  
	    response = new MockHttpServletResponse(); 
		
		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		storageManager = null;
		result = null;
		request = null;
		response = null;
		session = null;
		controller = null;
	}

	/**
	 * Test method for {@link com.tholix.web.ReceiptImageController#getReceipt(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetReceipt() throws IOException {		
		controller.setStorageManager(storageManager);
		
		/** Save the image */
		InputStream inputStream = FileUtils.openInputStream(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/20130112_164807.jpg"));
		String receiptBlobId = storageManager.save(inputStream, "text/html", "20130112_164807.jpg");
		
		assertNull(controller.getReceipt(receiptBlobId, request, response, session));	
		
		//Move the image to classes/build - images/no_image
		storageManager.deleteObject(receiptBlobId);	
		request = new MockHttpServletRequest();  
	    response = new MockHttpServletResponse(); 		
		assertNull(controller.getReceipt(receiptBlobId, request, response, session));
	}

}
