package com.tholix.web;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tholix.BaseTest;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.domain.types.TaxEnum;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.StorageManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.utils.DateUtil;

/**
 * @author hitender
 * @when Mar 22, 2013 8:14:26 PM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class ItemAnalyticControllerTest {

	@Autowired private ItemManager itemManager;
	@Autowired private StorageManager storageManager;
	@Autowired private ReceiptManager receiptManager;
	@Autowired private UserProfileManager userProfileManager;

	private ItemAnalyticController controller;
	@Mock private BindingResult result;

	@Before
	public void setUp() throws Exception {
		controller = new ItemAnalyticController();

		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);
	}

	@After
	public void tearDown() throws Exception {
		itemManager = null;
		storageManager = null;
		receiptManager = null;
		userProfileManager = null;
		controller = null;
		result = null;
	}

	@Test
	public void testLoadForm() throws Exception {
		controller.setItemManager(itemManager);

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

		ReceiptEntity receipt = ReceiptEntity.newInstance(receiptDate, total, tax, description, receiptStatus, receiptBlobId, userProfileId);
		receiptManager.save(receipt);

		ItemEntity item1 = ItemEntity.newInstance("Item-Test1", 80.00, TaxEnum.TAXED, 1, receipt, "test@test.com");
		itemManager.save(item1);

		ItemEntity item2 = ItemEntity.newInstance("Item-Test1", 40.00, TaxEnum.TAXED, 1, receipt, "test@test.com");
		itemManager.save(item2);

		ModelAndView modelAndView = controller.loadForm(item1.getId());
		assertEquals("/itemanalytic", modelAndView.getViewName());
		assertEquals(60.00, modelAndView.getModel().get("averagePrice"));

		storageManager.deleteObject(receiptBlobId);
		itemManager.deleteWhereReceipt(receipt);
		receiptManager.delete(receipt);
	}

}
