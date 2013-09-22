package com.tholix.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;

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
        deleteTempFiles();
    }

    private void deleteTempFiles() {
        try {
            File file = File.createTempFile("Receiptofi-delete-", ".xml");
            File directory = file.getParentFile();

            if(!directory.exists()) {
                log.info(directory + " Directory doesn't exists");
            } else {
                FilenameFilter textFilter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("XML-Report") || name.startsWith("Receiptofi");
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
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
