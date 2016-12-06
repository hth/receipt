package com.receiptofi.web.listener;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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

        Properties environment = new Properties();
        Properties system = new Properties();

        try {
            environment.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("build-info.properties"));

            if (StringUtils.equals(environment.getProperty("build.env"), "prod")) {
                system.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/prod.properties"));
            } else if (StringUtils.equals(environment.getProperty("build.env"), "sandbox")) {
                system.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/sandbox.properties"));
            } else {
                system.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/dev.properties"));
            }

            URL url = Thread.currentThread().getContextClassLoader().getResource("..//jsp//images//smallGoogle.jpg");
            Assert.notNull(url, "Images for email exists");
        } catch (IOException e) {
            LOG.error("could not load config properties file reason={}", e.getLocalizedMessage(), e);
        }

        checkEnvironment(environment, system);
        checkIfPropertiesExists(system);
    }

    private void checkIfPropertiesExists(Properties system) {
        if (system.getProperty("FilesUploadToS3.receipt.switch").isEmpty()) {
            throw new RuntimeException("Could not find property");
        } else {
            LOG.info("AWS S3 receipt upload status={}", system.getProperty("FilesUploadToS3.receipt.switch"));
        }

        if (system.getProperty("FilesUploadToS3.coupon.switch").isEmpty()) {
            throw new RuntimeException("Could not find property");
        } else {
            LOG.info("AWS S3 coupon upload status={}", system.getProperty("FilesUploadToS3.coupon.switch"));
        }

        if (system.getProperty("FilesUploadToS3.campaign.switch").isEmpty()) {
            throw new RuntimeException("Could not find property");
        } else {
            LOG.info("AWS S3 campaign upload status={}", system.getProperty("FilesUploadToS3.campaign.switch"));
        }
    }

    private void checkEnvironment(Properties environment, Properties system) {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String buildEnvironment = environment.getProperty("build.env");
            String hostname = system.getProperty("hostname.starts.with");

            LOG.info("Deploying on system={} and host={}", buildEnvironment, hostName);
            if (StringUtils.equals(buildEnvironment, "prod") && !hostName.startsWith(hostname)) {
                LOG.error("Mismatch system. Found env={} on host={}", buildEnvironment, hostName);
                throw new RuntimeException("Mismatch system. Found env=" + buildEnvironment + " on host=" + hostName);
            } else if (StringUtils.equals(buildEnvironment, "sandbox") && !hostName.startsWith(hostname)) {
                LOG.error("Mismatch system. Found env={} on host={}", buildEnvironment, hostName);
                throw new RuntimeException("Mismatch system. Found env=" + buildEnvironment + " on host=" + hostName);
            }
        } catch (UnknownHostException e) {
            LOG.error("Could not get hostname reason={}", e.getLocalizedMessage(), e);
        }
    }
}
