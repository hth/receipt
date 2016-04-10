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
import java.util.Collections;
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
public class LoadResource {
    private static final Logger LOG = LoggerFactory.getLogger(LoadResource.class);

    public static final String BUILD = "build" +
            File.separator +
            "activeProfile" +
            File.separator;

    public static final String WEBINF = File.separator +
            "WEB-INF";

    public static final String CLASSES = WEBINF +
            File.separator +
            "classes";

    public static final String FREEMARKER = WEBINF +
            File.separator +
            "freemarker";

    public static final String CONF = CLASSES +
            File.separator +
            "conf";

    public static final FileFilter profileF = new WildcardFileFilter(Arrays.asList("dev", "test", "prod"));
    public static final FileFilter propertiesF = new WildcardFileFilter(
            Arrays.asList(
                    "dev.properties",
                    "test.properties",
                    "prod.properties",
                    "application-messages.properties",
                    "config.properties",
                    /** Prod passwords are in saved in pass.properties */
                    "pass.properties"
            )
    );
    public static final FileFilter applicationServlet = new WildcardFileFilter(Arrays.asList("receipt-servlet.xml"));
    public static final FileFilter message_propertiesF = new WildcardFileFilter(
            Collections.singletonList("messages.properties")
    );

    public static void loadProperties(Properties properties) throws IOException {
        if (properties.keySet().isEmpty()) {
            /** service is the path name for this class. */
            File[] profileDir = findFiles(ReceiptServiceITest.class.getResource("").getPath().split("service")[0] + LoadResource.BUILD, LoadResource.profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
            for (File file : propertiesFiles) {
                properties.load(new FileReader(file));
            }

            propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CLASSES, message_propertiesF);
            for (File file : propertiesFiles) {
                properties.load(new FileReader(file));
            }
        }
    }

    @SuppressWarnings ("unused")
    public static String getApplicationServletLocation() throws IOException {
        File[] profileDir = findFiles(ReceiptServiceITest.class.getResource("").getPath().split("service")[0] + LoadResource.BUILD, LoadResource.profileF);
        File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + WEBINF, applicationServlet);
        assertTrue("Application Servlet exists", propertiesFiles[0].exists());
        String fileLocation = propertiesFiles[0].getCanonicalPath();
        return File.separator + fileLocation.split("/receipt/")[1];
    }

    public static File getFreemarkerLocation() throws IOException {
        File[] profileDir = findFiles(ReceiptServiceITest.class.getResource("").getPath().split("service")[0] + LoadResource.BUILD, LoadResource.profileF);
        File file  = new File(profileDir[0].getAbsolutePath() + FREEMARKER);
        assertTrue("Freemarker exists", file.exists());
        return file;
    }

    private static File[] findFiles(String location, FileFilter fileFilter) {
        File directory = new File(location);
        File[] files = directory.listFiles(fileFilter);
        assertTrue("number of files ", files.length > 0);
        return files;
    }
}
