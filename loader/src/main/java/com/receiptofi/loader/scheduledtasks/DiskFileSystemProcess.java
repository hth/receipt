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
public class DiskFileSystemProcess {
    private static final Logger LOG = LoggerFactory.getLogger(DiskFileSystemProcess.class);

    private ReceiptService receiptService;
    private CronStatsService cronStatsService;

    private String expensofiReportLocation;
    private int deleteExcelFileAfterDay;

    //TODO(hth) add to AOP to turn on and off instead
    private String removeExpiredExcelFiles;

    private CronStatsEntity cronStats;

    @Autowired
    public DiskFileSystemProcess(
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

    @Scheduled (cron = "${loader.DiskFileSystemProcess.removeExpiredExcelFiles}")
    public void removeExpiredExcelFiles() {
        cronStats = new CronStatsEntity(
                DiskFileSystemProcess.class.getName(),
                "Remove_Expired_Excel_Files",
                removeExpiredExcelFiles);

        if ("ON".equalsIgnoreCase(removeExpiredExcelFiles)) {
            LOG.info("feature is {}", removeExpiredExcelFiles);
            int found = 0, failure = 0, deletedExcelFiles = 0;
            try {
                AgeFileFilter cutoff = new AgeFileFilter(DateUtil.now().minusDays(deleteExcelFileAfterDay).toDate());
                File directory = new File(expensofiReportLocation);
                String[] files = directory.list(cutoff);
                found = files.length;
                for (String filename : files) {
                    removeExpiredExcel(FileUtil.getExcelFile(expensofiReportLocation, filename));
                    receiptService.removeExpensofiFilenameReference(filename);
                    deletedExcelFiles++;
                }
            } catch (Exception e) {
                LOG.error("totalXmlFiles error={}", e.getLocalizedMessage(), e);
                failure++;
            } finally {
                cronStats.addStats("foundExcelFile", found);
                cronStats.addStats("failure", failure);
                cronStats.addStats("deletedExcelFiles", deletedExcelFiles);
                cronStatsService.save(cronStats);

                LOG.info("complete foundExcelFile={} failure={} deletedExcelFiles={}",
                        found, failure, deletedExcelFiles);
            }
        } else {
            LOG.info("feature is {}", removeExpiredExcelFiles);
        }
    }

    private void removeExpiredExcel(File file) {
        if (!FileUtils.deleteQuietly(file)) {
            LOG.error("Failed to delete file={} from disk location={}", file, expensofiReportLocation);
        }
    }

    /**
     * Run this every morning at 9:00 AM
     *
     * @throws IOException
     */
    @Scheduled (cron = "${loader.DiskFileSystemProcess.removeTempFiles}")
    public void removeTempFiles() throws IOException {
        File file = FileUtil.createTempFile("delete", ".xml");
        File directory = file.getParentFile();

        cronStats = new CronStatsEntity(
                DiskFileSystemProcess.class.getName(),
                "Remove_Temp_Files",
                removeExpiredExcelFiles);

        if (directory.exists()) {
            int deleted = 0;
            FilenameFilter textFilter = (dir, name) -> name.startsWith(FileUtil.TEMP_FILE_START_WITH);
            int totalXmlFiles = directory.listFiles(textFilter).length;
            for (File f : directory.listFiles(textFilter)) {
                LOG.debug("File={}{}{}", directory, File.separator, f.getName());
                FileUtils.deleteQuietly(f);
                deleted++;
            }

            cronStats.addStats("totalXmlFiles", totalXmlFiles);
            cronStats.addStats("deleted", deleted);
            cronStatsService.save(cronStats);

            LOG.info("removed total temp files totalXmlFiles={} deleted={}", totalXmlFiles, deleted);
        } else {
            LOG.info("{} directory doesn't exists", directory);
        }

        FileUtils.deleteQuietly(file);
    }

    protected CronStatsEntity getCronStats() {
        return cronStats;
    }
}
