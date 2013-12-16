package com.receiptofi.web;

import com.receiptofi.domain.types.DocumentStatusEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.receiptofi.BaseTest;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.TaxEnum;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.controller.ReceiptController;
import com.receiptofi.web.form.ReceiptForm;

/**
 * @author hitender
 * @since Mar 23, 2013 11:04:26 AM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class ReceiptControllerTest {

	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;

	@Autowired private StorageManager storageManager;
	@Autowired private UserProfileManager userProfileManager;

	private ReceiptController controller;

    @Mock private BindingResult result;
	@Mock private RedirectAttributes redirectAttrs;

	@Before
	public void setUp() throws Exception {
		controller = new ReceiptController();

		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);
	}

	@After
	public void tearDown() throws Exception {
		receiptManager = null;
		itemManager = null;

		storageManager = null;
		userProfileManager = null;

		controller = null;

		result = null;
		redirectAttrs = null;
	}

	@Test
	public void testLoadForm() throws Exception {
//		controller.setItemManager(itemManager);
//		controller.setReceiptManager(receiptManager);
//		controller.setStorageManager(storageManager);

		UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
		String userProfileId = userProfile.getId();

		String title = "Sample Receipt";
		Date receiptDate = DateUtil.getDateFromString(BaseTest.receiptDate);
		Double total = 100.00;
		Double tax = 20.00;
		String description = "Description";
		DocumentStatusEnum receiptStatus = DocumentStatusEnum.TURK_PROCESSED;

		/** Save the image */
		InputStream inputStream = FileUtils.openInputStream(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/20130112_164807.jpg"));
//		String receiptBlobId = storageManager.save(inputStream, "text/html", "20130112_164807.jpg");
        String receiptBlobId = "45745745764457";

		ReceiptEntity receipt = ReceiptEntity.newInstance(receiptDate, total, tax, receiptStatus, receiptBlobId, userProfileId);
		receiptManager.save(receipt);

		ItemEntity item = ItemEntity.newInstance("Item1", 80.00, TaxEnum.TAXED, 1, receipt, "test@test.com");
		itemManager.save(item);

		ModelAndView modelAndView = controller.loadForm(receipt.getId(), ReceiptForm.newInstance(null, null, null), null);
		assertNotNull(modelAndView);

		ReceiptEntity receiptActual = (ReceiptEntity) modelAndView.getModelMap().get("receipt");
		assertEquals(receipt.getId(), receiptActual.getId());

		@SuppressWarnings("unchecked")
		List<ItemEntity> itemsActual = (List<ItemEntity>) modelAndView.getModelMap().get("items");
		assertEquals(item.getId(), itemsActual.iterator().next().getId());

		assertEquals("/receipt", modelAndView.getViewName());

		/** Delete operation */
		storageManager.deleteHard(receiptBlobId);
		itemManager.deleteWhereReceipt(receipt);
		receiptManager.deleteHard(receipt);
		assertNull(null, storageManager.get(receiptBlobId));
	}

	@Test
	public void testDelete() throws Exception {
//		controller.setItemManager(itemManager);
//		controller.setReceiptManager(receiptManager);
//		controller.setStorageManager(storageManager);

		UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
		String userProfileId = userProfile.getId();

		String title = "Sample Receipt";
		Date receiptDate = DateUtil.getDateFromString(BaseTest.receiptDate);
		Double total = 100.00;
		Double tax = 20.00;
		String description = "Description";
		DocumentStatusEnum receiptStatus = DocumentStatusEnum.TURK_PROCESSED;

		/** Save the image */
		InputStream inputStream = FileUtils.openInputStream(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/20130112_164807.jpg"));
//		String receiptBlobId = storageManager.save(inputStream, "text/html", "20130112_164807.jpg");
        String receiptBlobId = "4534755675476";

		ReceiptEntity receiptEntity = ReceiptEntity.newInstance(receiptDate, total, tax, receiptStatus, receiptBlobId, userProfileId);
		receiptManager.save(receiptEntity);

		ItemEntity item = ItemEntity.newInstance("Item1", 80.00, TaxEnum.TAXED, 1, receiptEntity, "test@test.com");
		itemManager.save(item);

		//assertEquals("redirect:/landing.htm", controller.delete(receiptEntity));
	}

}
