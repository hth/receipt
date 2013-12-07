package com.receiptofi.web.listener;

import com.receiptofi.utils.CreateTempFile;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.scheduledtasks.FileSystemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.apache.commons.io.filefilter.AgeFileFilter;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * User: hitender
 * Date: 9/21/13 8:15 PM
 */
public class ReceiptofiServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ReceiptofiServletContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        log.info("Receiptofi context destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        log.info("Receiptofi context initialized");
        try {
            if(hasAccessToFileSystem()) {
                log.info("Found and has access to directory: " + FileSystemProcessor.EXPENSOFI_FILE_SYSTEM);
            }
        } catch (IOException e) {
            log.error("Failure in creating new files: " + e.getLocalizedMessage());
        }
    }



    private boolean hasAccessToFileSystem() throws IOException {
        File directory = new File(FileSystemProcessor.EXPENSOFI_FILE_SYSTEM);
        if(directory.exists() && directory.isDirectory()) {
            File file = new File(FileSystemProcessor.EXPENSOFI_FILE_SYSTEM + File.separator + "receiptofi-expensofi.temp.delete.me");
            if(!file.createNewFile() && !file.canWrite() && !file.canRead()) {
                throw new AccessDeniedException("Cannot create, read or write to location: " + FileSystemProcessor.EXPENSOFI_FILE_SYSTEM);
            }
            if(!file.delete()) {
                throw new AccessDeniedException("Could not delete file from location: " + FileSystemProcessor.EXPENSOFI_FILE_SYSTEM);
            }
        } else {
            throw new AccessDeniedException("File system directory does not exists: " + FileSystemProcessor.EXPENSOFI_FILE_SYSTEM);
        }
        return true;
    }
}
