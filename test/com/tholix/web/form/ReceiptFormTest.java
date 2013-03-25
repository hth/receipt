/**
 * 
 */
package com.tholix.web.form;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tholix.BaseTest;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.TaxEnum;

/**
 * @author hitender 
 * @when Mar 19, 2013 7:02:05 PM
 *
 */
public class ReceiptFormTest {
	private ReceiptEntityOCR receipt;
	private List<ItemEntityOCR> items;	
	private ReceiptForm receiptForm;
	private ReceiptEntity receiptEntity;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		receipt = ReceiptEntityOCR.newInstance(BaseTest.title, BaseTest.receiptDate, BaseTest.total, BaseTest.tax, 
					BaseTest.description, BaseTest.receiptStatus, BaseTest.receiptBlobId, BaseTest.userProfileId); 
		
		items = new ArrayList<ItemEntityOCR>();
		items.add(ItemEntityOCR.newInstance("Item1", "80.00", TaxEnum.TAXED, 1, receipt, BaseTest.userProfileId));	
		
		receiptForm = ReceiptForm.newInstance(receipt, items);		
		receiptEntity = receiptForm.getReceiptEntity();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		items.clear();
		items = null;
		receipt = null;
		receiptForm = null;
		receiptEntity = null;
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#newInstance(com.tholix.domain.ReceiptEntityOCR, java.util.List)}.
	 */
	@Test
	public void testNewInstanceReceiptEntityOCRListOfItemEntityOCR() {
		assertNotNull(ReceiptForm.newInstance(null, null));
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#newInstance()}.
	 */
	@Test
	public void testNewInstance() {
		assertNotNull(ReceiptForm.newInstance());
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#getReceipt()}.
	 */
	@Test
	public void testGetReceipt() {
		assertNotNull(receiptForm.getReceipt());
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#setReceipt(com.tholix.domain.ReceiptEntityOCR)}.
	 */
	@Test
	public void testSetReceipt() {
		ReceiptForm form = ReceiptForm.newInstance(null, null);
		assertNull(form.getReceipt());
		form.setReceipt(receipt);
		assertNotNull(form.getReceipt());
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#getItems()}.
	 */
	@Test
	public void testGetItems() {
		assertNotNull(receiptForm.getItems());
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#setItems(java.util.List)}.
	 */
	@Test
	public void testSetItems() {
		ReceiptForm form = ReceiptForm.newInstance(null, null);
		assertNull(form.getItems());
		form.setItems(items);
		assertNotNull(form.getItems());
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#toString()}.
	 */
	@Test
	public void testToString() {
		String expected = "ReceiptForm [receipt=ReceiptEntityOCR [description=Test Description, title=Receipt Title, receiptStatus=Turk Processed, receiptBlobId=507f1f77bcf86cd799439011, receiptDate=01/01/13 12:01, total=80.00, tax=20.00, userProfileId=test@test.com, receiptOCRTranslation=null], items=[ItemEntity [name=Item1, price=80.00, taxed=Taxed]]]";
		assertEquals(expected, receiptForm.toString());
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#getReceiptEntity()}.
	 */
	@Test
	public void testGetReceiptEntity() {
		try {
			ReceiptEntity receiptEntity = receiptForm.getReceiptEntity();
			assertNotNull(receiptEntity);
			assertEquals(this.receiptEntity.toString(), receiptEntity.toString());
		} catch (NumberFormatException e) {
			assertNotSame("Encountered NumberFormatException " + e.getLocalizedMessage(), e, null);
			fail("Encountered NumberFormatException " + e.getLocalizedMessage());
		} catch (Exception e) {
			assertNotSame("Encountered exception " + e.getLocalizedMessage(), e, null);
			fail("Encountered exception " + e.getLocalizedMessage());
		} 
	}

	/**
	 * Test method for {@link com.tholix.web.form.ReceiptForm#getItemEntity(com.tholix.domain.ReceiptEntity)}.
	 */
	@Test
	public void testGetItemEntity() {
		try {
			List<ItemEntity> listOfItems = receiptForm.getItemEntity(receiptEntity);
			assertNotNull(listOfItems);
			assertEquals(items.size(), listOfItems.size());
			assertEquals(items.iterator().next().getName(), listOfItems.iterator().next().getName());
			assertEquals("ItemEntity [name=Item1, price=80.0, taxed=Taxed]", listOfItems.iterator().next().toString());
		} catch (ParseException e) {
			assertNotSame("Encountered ParseException " + e.getLocalizedMessage(), e, null);
			fail("Encountered ParseException " + e.getLocalizedMessage());
		}
	}
}
