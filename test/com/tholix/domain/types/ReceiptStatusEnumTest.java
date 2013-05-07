package com.tholix.domain.types;

import static com.mongodb.util.MyAsserts.assertEquals;

import org.junit.Test;

/**
 * User: hitender
 * Date: 5/2/13
 * Time: 10:06 PM
 */
public class ReceiptStatusEnumTest {
    @Test
    public void testGetValue() throws Exception {

    }

    @Test
    public void testGetDescription() throws Exception {

    }

    @Test
    public void testName() throws Exception {

    }

    @Test
    public void testOrdinal() throws Exception {
        assertEquals(0, ReceiptStatusEnum.OCR_PROCESSED.ordinal());
        assertEquals(1, ReceiptStatusEnum.TURK_PROCESSED.ordinal());
        assertEquals(2, ReceiptStatusEnum.TURK_REQUEST.ordinal());
    }
}
