package com.receiptofi.web.scheduledtasks;

import com.receiptofi.utils.CreateTempFile;
import com.receiptofi.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.filefilter.AgeFileFilter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 12/7/13 2:18 PM
 */
@Component
public class FileSystemProcessor {
    private static final Logger log = LoggerFactory.getLogger(FileSystemProcessor.class);

    private static final int EXPIRY_TIME = 7;

    //File system location;
    public static final String EXPENSOFI_FILE_SYSTEM = "/opt/receiptofi/expensofi";

    @Scheduled(cron="0 0 0 * * ?")
    public void removeExpiredExcelFiles() {
        AgeFileFilter cutoff = new AgeFileFilter(DateUtil.now().minusDays(EXPIRY_TIME).toDate());
        File directory = new File(EXPENSOFI_FILE_SYSTEM);
        String[] files = directory.list(cutoff);
        for(String filename : files) {
            new File(EXPENSOFI_FILE_SYSTEM + File.separator + filename).delete();
        }
        log.info("Removed expired excel files: count " + files.length);
    }

    @Scheduled(cron="0 0 9 * * ?")
    public void removeTempFiles() throws IOException {
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
            log.info("Removed total temp files: count '" + numberOfFiles);
        }

        file.delete();
    }
}
