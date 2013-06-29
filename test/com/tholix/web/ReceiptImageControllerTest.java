/**
 *
 */
package com.tholix.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

import com.tholix.repository.StorageManager;
import com.tholix.repository.UserPreferenceManager;
import com.tholix.repository.UserProfileManager;

/**
 * @author hitender
 * @since Mar 23, 2013 11:04:26 PM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class ReceiptImageControllerTest {

	@Autowired private StorageManager storageManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserPreferenceManager userPreferenceManager;

    @Mock private BindingResult result;

	private MockHttpServletRequest request;
    private MockHttpServletResponse response;

	private ReceiptImageController controller;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		controller = new ReceiptImageController();
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
		controller = null;
	}

	/**
	 * Test method for {@link com.tholix.web.ReceiptImageController#getReceipt(String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)}.
	 * @throws IOException
	 */
	@Test
	public void testGetReceipt() throws IOException {
//		controller.setStorageManager(storageManager);
//
//		/** Save the image */
//		InputStream inputStream = FileUtils.openInputStream(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/20130112_164807.jpg"));
//		String receiptBlobId = storageManager.save(inputStream, "text/html", "20130112_164807.jpg");
//
//        UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
//        UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId(), UserLevelEnum.USER);
//
//		assertNull(controller.getReceipt(receiptBlobId, userSession, request, response));
//
//		//Move the image to classes/build - images/no_image
//		storageManager.deleteHard(receiptBlobId);
//		request = new MockHttpServletRequest();
//	    response = new MockHttpServletResponse();
//		assertNull(controller.getReceipt(receiptBlobId, userSession, request, response));
	}

}
