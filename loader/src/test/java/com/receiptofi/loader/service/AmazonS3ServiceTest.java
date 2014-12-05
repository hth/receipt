package com.receiptofi.loader.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.springframework.util.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Properties;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Ignore
public class AmazonS3ServiceTest {
    public static final String BUILD = "receipt" +
            File.separator +
            "build" +
            File.separator +
            "explodedWar/";

    public static final String CONF = File.separator +
            "WEB-INF" +
            File.separator +
            "classes" +
            File.separator +
            "conf";

    private AmazonS3Service amazonS3Service;
    private FileFilter profile = new WildcardFileFilter(Arrays.asList("dev", "test"));
    private FileFilter properties = new WildcardFileFilter(Arrays.asList("dev.properties", "test.properties"));
    private Properties p = new Properties();

    @Before
    public void setUp() throws Exception {
        if (p.keySet().isEmpty()) {
            ClassLoader classLoader = AmazonS3ServiceTest.class.getClassLoader();
            File[] profileDir = findFiles(classLoader.getResource("").getPath().split("receipt")[0] + BUILD, profile);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, properties);
            p.load(new FileReader(propertiesFiles[0]));
        }

        amazonS3Service = new AmazonS3Service(
                p.getProperty("aws.s3.accessKey"),
                p.getProperty("aws.s3.secretKey"),
                p.getProperty("aws.s3.bucketName")
        );
    }

    private File[] findFiles(String location, FileFilter fileFilter) {
        File directory = new File(location);
        File[] files = directory.listFiles(fileFilter);
        Assert.isTrue(files.length == 1);
        return files;
    }

    @Test
    public void testGetS3client() {
        assertNotNull("AmazonS3 is initialized", amazonS3Service.getS3client());
    }

    @Test
    public void testIfBucketExists() {
        assertTrue("bucket exists", amazonS3Service.getS3client().doesBucketExist("chk.test"));
    }
}
