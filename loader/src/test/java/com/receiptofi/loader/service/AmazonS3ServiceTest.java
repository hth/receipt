package com.receiptofi.loader.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * For this test it is imperative to have properties files in place before executing any test.
 * Based on environment profile, appropriate properties files are copied under $build/$explodedWar.
 * For this reason build.gradle file has task called $setUpProperties.
 * Note: Run this test through gradle and not IntelliJ
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@ActiveProfiles ({"dev", "sandbox", "prod"})
public class AmazonS3ServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ServiceTest.class);

    public static final String BUILD = "build" +
            File.separator +
            "activeProfile" +
            File.separator;

    public static final String CLASSES = File.separator +
            "WEB-INF" +
            File.separator +
            "classes";

    public static final String CONF = CLASSES +
            File.separator +
            "conf";

    public static final FileFilter profileF = new WildcardFileFilter(Arrays.asList("dev", "sandbox", "prod"));
    public static final FileFilter propertiesF = new WildcardFileFilter(
            Arrays.asList(
                    "dev.properties",
                    "test.properties",
                    "prod.properties",
                    "application-messages.properties",
                    /** Prod passwords are in saved in pass.properties */
                    "pass.properties"
            )
    );

    public static final FileFilter message_propertiesF = new WildcardFileFilter(
            Collections.singletonList("messages.properties")
    );

    private Properties properties = new Properties();
    private AmazonS3Service amazonS3Service;

    @Before
    public void setUp() throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        LOG.info("activeProfiles={}", ArrayUtils.toString(activeProfiles));

        /**
         * Loading properties file for junit.
         */
        if (properties.keySet().isEmpty()) {
            /** loader is the path name for this class. */
            File[] profileDir = findFiles(AmazonS3ServiceTest.class.getResource("").getPath().split("loader")[0] + BUILD, profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
            for (File file : propertiesFiles) {
                properties.load(new FileReader(file));
            }

            propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CLASSES, message_propertiesF);
            for (File file : propertiesFiles) {
                properties.load(new FileReader(file));
            }
        }

        LOG.info("bucketName={}", properties.getProperty("aws.s3.bucketName"));
        assertTrue("properties populated", !properties.keySet().isEmpty());
        amazonS3Service = new AmazonS3Service(
                properties.getProperty("aws.s3.accessKey"),
                properties.getProperty("aws.s3.secretKey"),
                properties.getProperty("aws.s3.bucketName")
        );
    }

    @Test
    public void testGetS3client() {
        assertNotNull("AmazonS3 is initialized", amazonS3Service.getS3client());
    }

    @Test
    public void testIfBucketExists() {
        assertTrue("exists", amazonS3Service.getS3client().doesBucketExist(properties.getProperty("aws.s3.bucketName")));
    }

    public static File[] findFiles(String location, FileFilter fileFilter) {
        File directory = new File(location);
        File[] files = directory.listFiles(fileFilter);
        assertTrue("number of files ", files.length > 0);
        return files;
    }
}
