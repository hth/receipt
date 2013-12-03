package com.receiptofi.web.listener;

import com.receiptofi.utils.CreateTempFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

/**
 * User: hitender
 * Date: 9/21/13 8:15 PM
 */
public class ReceiptofiServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ReceiptofiServletContextListener.class);
    private static final String EXPENSOFI_FILE_SYSTEM = "/opt/receiptofi/expensofi";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        log.info("Receiptofi context destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        log.info("Receiptofi context initialized");
        try {
            deleteTempFiles();
        } catch (IOException e) {
            log.error("Failure in deleting temp files: " + e.getLocalizedMessage());
        }

        try {
            if(hasAccessToFileSystem(EXPENSOFI_FILE_SYSTEM)) {
                log.info("Found and has access to directory: " + EXPENSOFI_FILE_SYSTEM);
            }
        } catch (IOException e) {
            log.error("Failure in creating new files: " + e.getLocalizedMessage());
        }
    }

    private void deleteTempFiles() throws IOException {
        File file = CreateTempFile.file("delete", ".xml");
        File directory = file.getParentFile();

        if(!directory.exists()) {
            log.info(directory + " Directory doesn't exists");
        } else {
            FilenameFilter textFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(CreateTempFile.TEMP_FILE_START_WITH);
                }
            };

            int numberOfFiles = directory.listFiles(textFilter).length;
            for(File f : directory.listFiles(textFilter)) {
                log.debug("File: " + directory + File.separator + f.getName());
                f.delete();
            }
            log.info("Deleted total number of '" + numberOfFiles + "' files");
        }

        file.delete();
    }

    private boolean hasAccessToFileSystem(String directoryLocation) throws IOException {
        File directory = new File(directoryLocation);
        if(directory.exists() && directory.isDirectory()) {
            File file = new File(directoryLocation + File.separator + "receiptofi-expensofi.temp.delete.me");
            if(!file.createNewFile() && !file.canWrite() && !file.canRead()) {
                throw new AccessDeniedException("Cannot create, read or write to location: " + EXPENSOFI_FILE_SYSTEM);
            }
            if(!file.delete()) {
                throw new AccessDeniedException("Could not delete file from location: " + EXPENSOFI_FILE_SYSTEM);
            }
        } else {
            throw new AccessDeniedException("File system directory does not exists: " + EXPENSOFI_FILE_SYSTEM);
        }
        return true;
    }
}
