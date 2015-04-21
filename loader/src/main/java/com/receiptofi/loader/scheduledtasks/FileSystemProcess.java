package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.FileUtil;

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
 * Deletes excel report files.
 * Deletes xml file generate for supporting excel files.
 * User: hitender
 * Date: 12/7/13 2:18 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class FileSystemProcess {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemProcess.class);

    private ReceiptService receiptService;
    private CronStatsService cronStatsService;

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

            ReceiptService receiptService,
            CronStatsService cronStatsService
    ) {
        this.expensofiReportLocation = expensofiReportLocation;
        this.deleteExcelFileAfterDay = deleteExcelFileAfterDay;
        this.removeExpiredExcelFiles = removeExpiredExcelFiles;
        this.receiptService = receiptService;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (cron = "${loader.FileSystemProcess.removeExpiredExcelFiles}")
    public void removeExpiredExcelFiles() {
        CronStatsEntity cronStats = new CronStatsEntity(
                FileSystemProcess.class,
                "removeExpiredExcelFiles",
                removeExpiredExcelFiles);

        if ("ON".equalsIgnoreCase(removeExpiredExcelFiles)) {
            LOG.info("feature is {}", removeExpiredExcelFiles);
            int found = 0, failure = 0;
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
                failure++;
            } finally {
                cronStats.addStats("foundExcelFile", found);
                cronStats.addStats("failure", failure);
                cronStats.addStats("deletedExcelFile", countOfDeletedExcelFiles);
                cronStatsService.save(cronStats);

                LOG.info("complete foundExcelFile={} failure={} deletedExcelFile={}",
                        found, failure, countOfDeletedExcelFiles);
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
    @Scheduled (cron = "${loader.FileSystemProcess.removeTempFiles}")
    public void removeTempFiles() throws IOException {
        File file = FileUtil.createTempFile("delete", ".xml");
        File directory = file.getParentFile();

        if (directory.exists()) {
            FilenameFilter textFilter = (dir, name) -> name.startsWith(FileUtil.TEMP_FILE_START_WITH);
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
