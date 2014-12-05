package com.receiptofi.loader.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * For this test it is imperative to have properties files in place before executing any test.
 * Based on environment profile, appropriate properties files are copied under $build/$explodedWar.
 * For this reason build.gradle file has task called $setUpProperties.
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@Profile ({"dev", "test"})
public class AmazonS3ServiceTest {
    public static final String BUILD = "build" +
            File.separator +
            "explodedWar" +
            File.separator;

    public static final String CONF = File.separator +
            "WEB-INF" +
            File.separator +
            "classes" +
            File.separator +
            "conf";

    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ServiceTest.class);
    public static final FileFilter profileF = new WildcardFileFilter(Arrays.asList("dev", "test"));
    public static final FileFilter propertiesF = new WildcardFileFilter(Arrays.asList("dev.properties", "test.properties"));

    private Properties prop = new Properties();
    private AmazonS3Service amazonS3Service;

    @Before
    public void setUp() throws IOException {
        /**
         * Loading properties file for junit.
         */
        if (prop.keySet().isEmpty()) {
            ClassLoader classLoader = AmazonS3ServiceTest.class.getClassLoader();
            File[] profileDir = findFiles(classLoader.getResource("").getPath().split("loader")[0] + BUILD, profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
            prop.load(new FileReader(propertiesFiles[0]));
        }

        LOG.info("bucketName={}", prop.getProperty("aws.s3.bucketName"));
        assertTrue("properties populated", !prop.keySet().isEmpty());
        amazonS3Service = new AmazonS3Service(
                prop.getProperty("aws.s3.accessKey"),
                prop.getProperty("aws.s3.secretKey"),
                prop.getProperty("aws.s3.bucketName")
        );
    }

    @Test
    public void testGetS3client() {
        assertNotNull("AmazonS3 is initialized", amazonS3Service.getS3client());
    }

    @Test
    public void testIfBucketExists() {
        assertTrue("exists", amazonS3Service.getS3client().doesBucketExist(prop.getProperty("aws.s3.bucketName")));
    }

    public static File[] findFiles(String location, FileFilter fileFilter) {
        File directory = new File(location);
        File[] files = directory.listFiles(fileFilter);
        Assert.isTrue(files.length == 1);
        return files;
    }
}
