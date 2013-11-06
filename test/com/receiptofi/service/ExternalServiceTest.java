package com.receiptofi.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.receiptofi.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 5/9/13
 * Time: 11:51 PM
 */
public class ExternalServiceTest {

    ExternalService externalService;
    BizStoreEntity bizStoreEntity;

    @Before
    public void setUp() throws Exception {
        bizStoreEntity = BizStoreEntity.newInstance();
        bizStoreEntity.setAddress("1600 Amphitheatre Parkway Mountain View, CA");

        externalService = new ExternalService();
    }

    @After
    public void tearDown() throws Exception {
        bizStoreEntity = null;
        externalService = null;
    }

    @Test
    public void testDecodeAddress() throws Exception {
        externalService.decodeAddress(bizStoreEntity);
        assertEquals("1600 Amphitheatre Parkway, Mountain View, CA 94043, USA", bizStoreEntity.getAddress());
        assertEquals(37.422_857_6, bizStoreEntity.getLat() , 0.000_0005);
        assertEquals(-122.085_064_7, bizStoreEntity.getLng(), 0.000_0005);
    }
}
