package com.tholix.web;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mongodb.gridfs.GridFSDBFile;
import com.tholix.BaseTest;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.domain.types.TaxEnum;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.ReceiptManagerImpl;
import com.tholix.service.StorageManager;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserProfileManager;
import com.tholix.utils.DateUtil;
import com.tholix.web.form.ReceiptForm;

/**
 * @author hitender 
 * @when Mar 23, 2013 11:04:26 AM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class ReceiptFormControllerTest {
	
	@Autowired private UserAuthenticationManager userAuthenticationManager;
	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ItemFeatureManager itemFeatureManager;
	
	@Autowired private StorageManager storageManager;
	@Autowired private UserProfileManager userProfileManager;
	
	private ReceiptFormController controller;
    private MockHttpSession session;
    
    @Mock private BindingResult result;
	@Mock private RedirectAttributes redirectAttrs;

	@Before
	public void setUp() throws Exception {
		controller = new ReceiptFormController();
		session = new MockHttpSession();
		
		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);	
	}

	@After
	public void tearDown() throws Exception {
		userAuthenticationManager = null;
		receiptManager = null;
		itemManager = null;
		itemFeatureManager = null;
		
		storageManager = null;
		userProfileManager = null;		
		
		controller = null;
		session = null;
		
		result = null;
		redirectAttrs = null;
	}

	@Test
	public void testLoadForm() throws Exception {	
		controller.setUserAuthenticationManager(userAuthenticationManager);
		controller.setItemManager(itemManager);
		controller.setItemFeatureManager(itemFeatureManager);
		controller.setReceiptManager(receiptManager);
		controller.setStorageManager(storageManager);
		
		UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
		String userProfileId = userProfile.getId();
		
		String title = "Sample Receipt";
		Date receiptDate = DateUtil.getDateFromString(BaseTest.receiptDate);
		Double total = 100.00;
		Double tax = 20.00;
		String description = "Description";
		ReceiptStatusEnum receiptStatus = ReceiptStatusEnum.TURK_PROCESSED;	

		/** Save the image */
		InputStream inputStream = FileUtils.openInputStream(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/20130112_164807.jpg"));
		String receiptBlobId = storageManager.save(inputStream, "text/html", "20130112_164807.jpg");
		
		ReceiptEntity receipt = ReceiptEntity.newInstance(title, receiptDate, total, tax, description, receiptStatus, receiptBlobId, userProfileId);
		receiptManager.saveObject(receipt);

		ItemEntity item = ItemEntity.newInstance("Item1", 80.00, TaxEnum.TAXED, 1, receipt, "test@test.com");		
		itemManager.saveObject(item);
		
		ModelAndView modelAndView = controller.loadForm(receipt.getId(), ReceiptEntity.newInstance());
		assertNotNull(modelAndView);
		
		ReceiptEntity receiptActual = (ReceiptEntity) modelAndView.getModelMap().get("receipt");
		assertEquals(receipt.getId(), receiptActual.getId());
		
		@SuppressWarnings("unchecked")
		List<ItemEntity> itemsActual = (List<ItemEntity>) modelAndView.getModelMap().get("items");
		assertEquals(item.getId(), itemsActual.iterator().next().getId());
		
		assertEquals("/receipt", modelAndView.getViewName());		
		
		/** Delete operation */
		storageManager.deleteObject(receiptBlobId);
		itemManager.deleteWhereReceipt(receipt);
		receiptManager.deleteObject(receipt);
		assertNull(null, storageManager.get(receiptBlobId));
	}

	@Test
	public void testDelete() throws Exception {
		controller.setUserAuthenticationManager(userAuthenticationManager);
		controller.setItemManager(itemManager);
		controller.setItemFeatureManager(itemFeatureManager);
		controller.setReceiptManager(receiptManager);
		controller.setStorageManager(storageManager);
		
		UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
		String userProfileId = userProfile.getId();
		
		String title = "Sample Receipt";
		Date receiptDate = DateUtil.getDateFromString(BaseTest.receiptDate);
		Double total = 100.00;
		Double tax = 20.00;
		String description = "Description";
		ReceiptStatusEnum receiptStatus = ReceiptStatusEnum.TURK_PROCESSED;	

		/** Save the image */
		InputStream inputStream = FileUtils.openInputStream(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/20130112_164807.jpg"));
		String receiptBlobId = storageManager.save(inputStream, "text/html", "20130112_164807.jpg");
		
		ReceiptEntity receipt = ReceiptEntity.newInstance(title, receiptDate, total, tax, description, receiptStatus, receiptBlobId, userProfileId);
		receiptManager.saveObject(receipt);

		ItemEntity item = ItemEntity.newInstance("Item1", 80.00, TaxEnum.TAXED, 1, receipt, "test@test.com");		
		itemManager.saveObject(item);
		
		assertEquals("redirect:/landing.htm", controller.delete(receipt, session, redirectAttrs));
	}

}
