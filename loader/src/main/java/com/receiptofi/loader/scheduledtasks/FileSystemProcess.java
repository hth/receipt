package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.CreateTempFile;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Can be used for deleting various kinds of files.
 *  Deletes excel report files.
 *  Deletes xml file generate for supporting excel files.
 * User: hitender
 * Date: 12/7/13 2:18 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Component
public class FileSystemProcess {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemProcess.class);

    private ReceiptService receiptService;

    private String expensofiReportLocation;
    private int deleteExcelFileAfterDay;

    //TODO(hth) add to AOP to turn on and off instead
    private String removeExpiredExcelFiles;

    private int countOfDeletedExcelFiles;
    private int countOfDeletedXmlFiles;

    @Autowired
    public FileSystemProcess(
            @Value ("${expensofiReportLocation}")
            String expensofiReportLocation,

            @Value ("${deleteExcelFileAfterDay:7}")
            int deleteExcelFileAfterDay,

            @Value ("${removeExpiredExcelFiles:ON}")
            String removeExpiredExcelFiles,

            ReceiptService receiptService
    ) {
        this.expensofiReportLocation = expensofiReportLocation;
        this.deleteExcelFileAfterDay = deleteExcelFileAfterDay;
        this.removeExpiredExcelFiles = removeExpiredExcelFiles;
        this.receiptService = receiptService;
    }

    //for every two second use */2 * * * * ? where as cron string blow run every day at 12:00 AM
    @Scheduled (cron = "0 0 0 * * ?")
    public void removeExpiredExcelFiles() {
        if ("ON".equalsIgnoreCase(removeExpiredExcelFiles)) {
            LOG.info("feature is {}", removeExpiredExcelFiles);
            int found = 0;
            try {
                AgeFileFilter cutoff = new AgeFileFilter(DateUtil.now().minusDays(deleteExcelFileAfterDay).toDate());
                File directory = new File(expensofiReportLocation);
                String[] files = directory.list(cutoff);
                found = files.length;
                for (String filename : files) {
                    removeExpiredExcel(getExcelFile(filename));
                    receiptService.removeExpensofiFilenameReference(filename);
                    countOfDeletedExcelFiles++;
                }
            } catch (Exception e) {
                LOG.error("found error={}", e.getLocalizedMessage(), e);
            } finally {
                LOG.info("complete deletedExcelFile={}, foundExcelFile={}", countOfDeletedExcelFiles, found);
            }
        } else {
            LOG.info("feature is {}", removeExpiredExcelFiles);
        }
    }

    public File getExcelFile(String filename) {
        return new File(expensofiReportLocation + File.separator + filename);
    }

    public void removeExpiredExcel(File file) {
        FileUtils.deleteQuietly(file);
    }

    public void removeExpiredExcel(String filename) {
        removeExpiredExcel(getExcelFile(filename));
    }

    /**
     * Run this every morning at 9:00 AM
     *
     * @throws IOException
     */
    @Scheduled (cron = "0 0 9 * * ?")
    public void removeTempFiles() throws IOException {
        File file = CreateTempFile.file("delete", ".xml");
        File directory = file.getParentFile();

        if (directory.exists()) {
            FilenameFilter textFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(CreateTempFile.TEMP_FILE_START_WITH);
                }
            };

            countOfDeletedXmlFiles = directory.listFiles(textFilter).length;
            for (File f : directory.listFiles(textFilter)) {
                LOG.debug("File={}{}{}", directory, File.separator, f.getName());
                FileUtils.deleteQuietly(f);
            }
            LOG.info("removed total temp files count={}", countOfDeletedXmlFiles);
        } else {
            LOG.info("{} directory doesn't exists", directory);
        }

        FileUtils.deleteQuietly(file);
    }

    protected int getCountOfDeletedExcelFiles() {
        return countOfDeletedExcelFiles;
    }

    protected int getCountOfDeletedXmlFiles() {
        return countOfDeletedXmlFiles;
    }
}
