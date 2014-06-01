package com.receiptofi.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Value;

/**
 * User: hitender
 * Date: 9/21/13 8:15 PM
 */
public class ReceiptofiServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ReceiptofiServletContextListener.class);

    @Value("${expensofiReportLocation}")
    private String expensofiReportLocation;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        //TODO make clean shutdown for quartz. This prevent now from tomcat shutdown
        log.info("Receiptofi context destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        log.info("Receiptofi context initialized");
        try {
            if(hasAccessToFileSystem()) {
                log.info("Found and has access to directory={}", expensofiReportLocation);
            }
        } catch (IOException e) {
            log.error("Failure in creating new files: " + e.getLocalizedMessage());
        }
    }

    private boolean hasAccessToFileSystem() throws IOException {
        File directory = new File(expensofiReportLocation);
        if(directory.exists() && directory.isDirectory()) {
            File file = new File(expensofiReportLocation + File.separator + "receiptofi-expensofi.temp.delete.me");
            if(!file.createNewFile() && !file.canWrite() && !file.canRead()) {
                throw new AccessDeniedException("Cannot create, read or write to location: " + expensofiReportLocation);
            }
            if(!file.delete()) {
                throw new AccessDeniedException("Could not delete file from location: " + expensofiReportLocation);
            }
        } else {
            throw new AccessDeniedException("File system directory does not exists: " + expensofiReportLocation);
        }
        return true;
    }
}
