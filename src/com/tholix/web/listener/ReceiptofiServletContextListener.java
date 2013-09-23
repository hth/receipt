package com.tholix.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.tholix.utils.CreateTempFile;

/**
 * User: hitender
 * Date: 9/21/13 8:15 PM
 */
public class ReceiptofiServletContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(ReceiptofiServletContextListener.class);

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
}
