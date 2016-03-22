package com.receiptofi;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import org.junit.Before;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * User: hitender
 * Date: 3/9/16 1:23 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@ActiveProfiles ({"dev", "test", "prod"})
public class LoadProperties {
    private static final Logger LOG = LoggerFactory.getLogger(LoadProperties.class);

    public static final String BUILD = "build" +
            File.separator +
            "activeProfile" +
            File.separator;

    public static final String CONF = File.separator +
            "WEB-INF" +
            File.separator +
            "classes" +
            File.separator +
            "conf";

    public static final FileFilter profileF = new WildcardFileFilter(Arrays.asList("dev", "test", "prod"));
    public static final FileFilter propertiesF = new WildcardFileFilter(
            Arrays.asList(
                    "dev.properties",
                    "test.properties",
                    "prod.properties",
                    /** Prod passwords are in saved in pass.properties */
                    "pass.properties"
            )
    );

    private Properties prop = new Properties();

    @Before
    public void setUp() throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        LOG.info("activeProfiles={}", ArrayUtils.toString(activeProfiles));

        /**
         * Loading properties file for junit.
         */
        if (prop.keySet().isEmpty()) {
            File[] profileDir = findFiles(LoadProperties.class.getResource("").getPath().split("loader")[0] + BUILD, profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
            for (File file : propertiesFiles) {
                prop.load(new FileReader(file));
            }
        }
    }

    public static File[] findFiles(String location, FileFilter fileFilter) {
        File directory = new File(location);
        File[] files = directory.listFiles(fileFilter);
        assertTrue("number of files ", files.length > 0);
        return files;
    }

    public Properties getProp() {
        return prop;
    }
}
