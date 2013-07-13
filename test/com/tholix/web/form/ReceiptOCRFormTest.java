/**
 *
 */
package com.tholix.web.form;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;

/**
 * @author hitender
 * @since Mar 19, 2013 7:02:05 PM
 *
 */
public class ReceiptOCRFormTest {
	private ReceiptEntityOCR receipt;
	private List<ItemEntityOCR> items;
	private ReceiptOCRForm receiptOCRForm;
	private ReceiptEntity receiptEntity;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		receipt = null;

		items = new ArrayList<ItemEntityOCR>();

        //Will not work
		items.add(ItemEntityOCR.newInstance());


		receiptOCRForm = ReceiptOCRForm.newInstance(receipt, items);
		receiptEntity = receiptOCRForm.getReceiptEntity();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		items.clear();
		items = null;
		receipt = null;
		receiptOCRForm = null;
		receiptEntity = null;
	}

	/**
	 * Test method for {@link ReceiptOCRForm#newInstance(com.tholix.domain.ReceiptEntityOCR, java.util.List)}.
	 */
	@Test
	public void testNewInstanceReceiptEntityOCRListOfItemEntityOCR() {
		assertNotNull(ReceiptOCRForm.newInstance(null, null));
	}

	/**
	 * Test method for {@link ReceiptOCRForm#newInstance()}.
	 */
	@Test
	public void testNewInstance() {
		assertNotNull(ReceiptOCRForm.newInstance());
	}

	/**
	 * Test method for {@link ReceiptOCRForm#getReceiptOCR()}.
	 */
	@Test
	public void testGetReceipt() {
		assertNotNull(receiptOCRForm.getReceiptOCR());
	}

	/**
	 * Test method for {@link ReceiptOCRForm#setReceiptOCR(com.tholix.domain.ReceiptEntityOCR)}.
	 */
	@Test
	public void testSetReceipt() {
		ReceiptOCRForm form = ReceiptOCRForm.newInstance(null, null);
		assertNull(form.getReceiptOCR());
		form.setReceiptOCR(receipt);
		assertNotNull(form.getReceiptOCR());
	}

	/**
	 * Test method for {@link ReceiptOCRForm#getItems()}.
	 */
	@Test
	public void testGetItems() {
		assertNotNull(receiptOCRForm.getItems());
	}

	/**
	 * Test method for {@link ReceiptOCRForm#setItems(java.util.List)}.
	 */
	@Test
	public void testSetItems() {
		ReceiptOCRForm form = ReceiptOCRForm.newInstance(null, null);
		assertNull(form.getItems());
		form.setItems(items);
		assertNotNull(form.getItems());
	}

	/**
	 * Test method for {@link ReceiptOCRForm#toString()}.
	 */
	@Test
	public void testToString() {
		String expected = "ReceiptOCRForm [receipt=ReceiptEntityOCR [description=Test Description, title=Receipt Title, receiptStatus=Turk Processed, receiptBlobId=507f1f77bcf86cd799439011, receiptDate=01/01/13 12:01, total=80.00, tax=20.00, userProfileId=test@test.com, receiptOCRTranslation=null], items=[ItemEntity [name=Item1, price=80.00, taxed=Taxed]]]";
		assertEquals(expected, receiptOCRForm.toString());
	}

	/**
	 * Test method for {@link ReceiptOCRForm#getReceiptEntity()}.
	 */
	@Test
	public void testGetReceiptEntity() {
		try {
			ReceiptEntity receiptEntity = receiptOCRForm.getReceiptEntity();
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
	 * Test method for {@link ReceiptOCRForm#getItemEntity(com.tholix.domain.ReceiptEntity)}.
	 */
	@Test
	public void testGetItemEntity() {
		try {
			List<ItemEntity> listOfItems = receiptOCRForm.getItemEntity(receiptEntity);
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
