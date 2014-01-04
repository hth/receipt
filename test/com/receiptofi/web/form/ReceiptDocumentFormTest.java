/**
 *
 */
package com.receiptofi.web.form;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author hitender
 * @since Mar 19, 2013 7:02:05 PM
 *
 */
public class ReceiptDocumentFormTest {
	private DocumentEntity receipt;
	private List<ItemEntityOCR> items;
	private ReceiptDocumentForm receiptDocumentForm;
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


		receiptDocumentForm = ReceiptDocumentForm.newInstance(receipt, items);
		receiptEntity = receiptDocumentForm.getReceiptEntity();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		items.clear();
		items = null;
		receipt = null;
		receiptDocumentForm = null;
		receiptEntity = null;
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#newInstance(com.receiptofi.domain.DocumentEntity, java.util.List)}.
	 */
	@Test
	public void testNewInstanceReceiptEntityOCRListOfItemEntityOCR() {
		assertNotNull(ReceiptDocumentForm.newInstance(null, null));
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#newInstance()}.
	 */
	@Test
	public void testNewInstance() {
		assertNotNull(ReceiptDocumentForm.newInstance());
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#getReceiptDocument()}.
	 */
	@Test
	public void testGetReceipt() {
		assertNotNull(receiptDocumentForm.getReceiptDocument());
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#setReceiptDocument(com.receiptofi.domain.DocumentEntity)}.
	 */
	@Test
	public void testSetReceipt() {
		ReceiptDocumentForm form = ReceiptDocumentForm.newInstance(null, null);
		assertNull(form.getReceiptDocument());
		form.setReceiptDocument(receipt);
		assertNotNull(form.getReceiptDocument());
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#getItems()}.
	 */
	@Test
	public void testGetItems() {
		assertNotNull(receiptDocumentForm.getItems());
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#setItems(java.util.List)}.
	 */
	@Test
	public void testSetItems() {
		ReceiptDocumentForm form = ReceiptDocumentForm.newInstance(null, null);
		assertNull(form.getItems());
		form.setItems(items);
		assertNotNull(form.getItems());
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#toString()}.
	 */
	@Test
	public void testToString() {
		String expected = "ReceiptDocumentForm [receipt=DocumentEntity [description=Test Description, title=Receipt Title, receiptStatus=Turk Processed, receiptBlobId=507f1f77bcf86cd799439011, receiptDate=01/01/13 12:01, total=80.00, tax=20.00, userProfileId=test@test.com, receiptOCRTranslation=null], items=[ItemEntity [name=Item1, price=80.00, taxed=Taxed]]]";
		assertEquals(expected, receiptDocumentForm.toString());
	}

	/**
	 * Test method for {@link ReceiptDocumentForm#getReceiptEntity()}.
	 */
	@Test
	public void testGetReceiptEntity() {
		try {
			ReceiptEntity receiptEntity = receiptDocumentForm.getReceiptEntity();
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
	 * Test method for {@link ReceiptDocumentForm#getItemEntity(com.receiptofi.domain.ReceiptEntity)}.
	 */
	@Test
	public void testGetItemEntity() {
		try {
			List<ItemEntity> listOfItems = receiptDocumentForm.getItemEntity(receiptEntity);
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
