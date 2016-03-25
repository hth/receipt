package com.receiptofi;

import static org.junit.Assert.assertTrue;

import com.receiptofi.service.ReceiptServiceITest;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

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

    public static void loadProperties(Properties properties) throws IOException {
        if (properties.keySet().isEmpty()) {
            /** service is the path name for this class. */
            File[] profileDir = findFiles(ReceiptServiceITest.class.getResource("").getPath().split("service")[0] + LoadProperties.BUILD, LoadProperties.profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + LoadProperties.CONF, LoadProperties.propertiesF);
            for (File file : propertiesFiles) {
                properties.load(new FileReader(file));
            }
        }
    }

    public static File[] findFiles(String location, FileFilter fileFilter) {
        File directory = new File(location);
        File[] files = directory.listFiles(fileFilter);
        assertTrue("number of files ", files.length > 0);
        return files;
    }
}
