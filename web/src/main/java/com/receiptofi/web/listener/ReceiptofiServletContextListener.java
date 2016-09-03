package com.receiptofi.web.listener;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: hitender
 * Date: 9/21/13 8:15 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class ReceiptofiServletContextListener implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptofiServletContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        //TODO make clean shutdown for quartz. This prevent now from tomcat shutdown
        LOG.info("Receiptofi context destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        LOG.info("Receiptofi context initialized");

        Properties messages = new Properties();
        Properties environment = new Properties();
        Properties config = new Properties();

        try {
            messages.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("messages.properties"));

            if (StringUtils.equals(messages.getProperty("build.env"), "prod")) {
                environment.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/prod.properties"));
            } else if (StringUtils.equals(messages.getProperty("build.env"), "test")) {
                environment.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/test.properties"));
            } else {
                environment.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/dev.properties"));
            }

            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/config.properties"));
            URL url = Thread.currentThread().getContextClassLoader().getResource("..//jsp//images//smallGoogle.jpg");
            Assert.notNull(url, "Images for email exists");
        } catch (IOException e) {
            LOG.error("could not load config properties file reason={}", e.getLocalizedMessage(), e);
        }

        checkEnvironment(messages);
        checkIfPropertiesExists(environment);

        if (hasAccessToFileSystem(config)) {
            LOG.info("Found and has access, to directory={}", config.getProperty("expensofiReportLocation"));
        }
    }

    private void checkIfPropertiesExists(Properties environment) {
        if (environment.getProperty("FilesUploadToS3.receipt.switch").isEmpty()) {
            throw new RuntimeException("Could not find property");
        } else {
            LOG.info("AWS S3 receipt upload status={}", environment.getProperty("FilesUploadToS3.receipt.switch"));
        }

        if (environment.getProperty("FilesUploadToS3.coupon.switch").isEmpty()) {
            throw new RuntimeException("Could not find property");
        } else {
            LOG.info("AWS S3 coupon upload status={}", environment.getProperty("FilesUploadToS3.coupon.switch"));
        }

        if (environment.getProperty("FilesUploadToS3.campaign.switch").isEmpty()) {
            throw new RuntimeException("Could not find property");
        } else {
            LOG.info("AWS S3 campaign upload status={}", environment.getProperty("FilesUploadToS3.campaign.switch"));
        }
    }

    private void checkEnvironment(Properties properties) {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String buildEnvironment = properties.getProperty("build.env");

            LOG.info("Deploying on environment={} and host={}", buildEnvironment, hostName);
            if (StringUtils.equals(buildEnvironment, "prod") && !hostName.equals("live")) {
                LOG.error("Mismatch environment. Found env={} on host={}", buildEnvironment, hostName);
                throw new RuntimeException("Mismatch environment. Found env=" + buildEnvironment + " on host=" + hostName);
            } else if (StringUtils.equals(buildEnvironment, "test") && !hostName.equals("receiptofi.com")) {
                LOG.error("Mismatch environment. Found env={} on host={}", buildEnvironment, hostName);
                throw new RuntimeException("Mismatch environment. Found env=" + buildEnvironment + " on host=" + hostName);
            }
        } catch (UnknownHostException e) {
            LOG.error("Could not get hostname reason={}", e.getLocalizedMessage(), e);
        }
    }

    private boolean hasAccessToFileSystem(Properties properties) {
        String expensofiReportLocation = properties.getProperty("expensofiReportLocation");
        Assert.notNull(expensofiReportLocation);

        File directory = new File(expensofiReportLocation);
        if (directory.exists() && directory.isDirectory()) {
            File file = new File(expensofiReportLocation + File.separator + "receiptofi-expensofi.temp.delete.me");
            try {
                if (!file.createNewFile()) {
                    throw new AccessDeniedException("Cannot create, to location=" + expensofiReportLocation);
                }
                if (!file.canWrite()) {
                    throw new AccessDeniedException("Cannot write, to location=" + expensofiReportLocation);
                }
                if (!file.canRead()) {
                    throw new AccessDeniedException("Cannot read, to location=" + expensofiReportLocation);
                }
                if (!file.delete()) {
                    throw new AccessDeniedException("Cannot delete, from location=" + expensofiReportLocation);
                }
            } catch (IOException e) {
                LOG.error(
                        "Possible permission deny to location={}, reason={}",
                        expensofiReportLocation,
                        e.getLocalizedMessage(),
                        e
                );
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            LOG.error("File system directory does not exists, location={}", expensofiReportLocation);
            throw new RuntimeException("File system directory does not exists, location=" + expensofiReportLocation);
        }
        return true;
    }
}
