package com.receiptofi.loader.service;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class AmazonS3ServiceTest {
    private AmazonS3Service amazonS3Service;

    @Before
    public void setUp() throws Exception {
        amazonS3Service = new AmazonS3Service(
            "AKIAIKXLLL5H2ASME3YQ",
            "Dy3ipS3lEdGkZfzVDiyal6QYusdTR6TZ4T9AEl5M",
            "chk.test"
        );
    }

    @Test
    public void testGetS3client() {
        assertNotNull(amazonS3Service.getS3client());
        assertTrue("bucket exists", amazonS3Service.getS3client().doesBucketExist("chk.test"));
    }
}
