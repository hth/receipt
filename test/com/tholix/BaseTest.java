/**
 * 
 */
package com.tholix;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * @author hitender
 * @when Dec 29, 2012 2:24:02 PM
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/CreateAccountFormControllerTests-context.xml" })
public class BaseTest {
	public final static String title = "Receipt Title";
	//public final static Date receiptDate = DateTime.parse("01/01/13 12:01", DateTimeFormat.forPattern("MM/dd/yy kk:mm")).toDate(); 
	public final static String receiptDate = "01/01/13 12:01"; 
	public final static String total = "80.00"; 
	public final static String tax = "20.00";
	public final static String description = "Test Description";
	public final static ReceiptStatusEnum receiptStatus = ReceiptStatusEnum.TURK_PROCESSED;
	public final static String receiptBlobId = "507f1f77bcf86cd799439011";
	public final static String userProfileId = "test@test.com"; 
	
	@Test
	public void someTest() {
		assertNull(null);
	}
}
