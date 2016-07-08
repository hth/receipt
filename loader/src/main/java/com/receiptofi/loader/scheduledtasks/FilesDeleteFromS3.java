package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.CloudFileService;
import com.receiptofi.service.CronStatsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 12/1/14 6:13 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class FilesDeleteFromS3 {
    private static final Logger LOG = LoggerFactory.getLogger(FilesDeleteFromS3.class);

    private final String bucketName;
    private final String receiptFolderName;
    private final String couponFolderName;

    private AmazonS3Service amazonS3Service;
    private CloudFileService cloudFileService;
    private CronStatsService cronStatsService;

    @Autowired
    public FilesDeleteFromS3(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            @Value ("${aws.s3.bucketName}")
            String receiptFolderName,

            @Value ("${aws.s3.couponBucketName}")
            String couponFolderName,

            CloudFileService cloudFileService,
            AmazonS3Service amazonS3Service,
            CronStatsService cronStatsService
    ) {
        this.bucketName = bucketName;
        this.receiptFolderName = receiptFolderName;
        this.couponFolderName = couponFolderName;
        this.cloudFileService = cloudFileService;
        this.amazonS3Service = amazonS3Service;
        this.cronStatsService = cronStatsService;
    }

    /**
     * Delete files from S3.
     */
    @Scheduled (cron = "${loader.FilesDeleteFromS3.delete}")
    public void delete() {
        CronStatsEntity cronStats = new CronStatsEntity(
                FilesDeleteFromS3.class.getName(),
                "Delete",
                "ON");

        DeleteObjectsResult deleteObjectsResult = null;
        List<CloudFileEntity> cloudFiles = cloudFileService.getAllMarkedAsDeleted();
        if (!cloudFiles.isEmpty()) {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            List<DeleteObjectsRequest.KeyVersion> keys = cloudFiles.stream().map(this::populateDeleteObject).collect(Collectors.toList());
            deleteObjectsRequest.setKeys(keys);
            try {
                deleteObjectsResult = amazonS3Service.getS3client().deleteObjects(deleteObjectsRequest);
                cloudFiles.forEach(cloudFileService::deleteHard);
            } catch (MultiObjectDeleteException e) {
                LOG.error("Failed to delete files on S3 reason={}", e.getMessage(), e);
                LOG.error("Objects successfully deleted count={}", e.getDeletedObjects().size());
                LOG.error("Objects failed to delete count={}", e.getErrors().size());
                LOG.error("Printing error data...");
                for (MultiObjectDeleteException.DeleteError deleteError : e.getErrors()) {
                    LOG.warn("Object Key: {} {} {}",
                            deleteError.getKey(),
                            deleteError.getCode(),
                            deleteError.getMessage());
                }
            } finally {
                if (deleteObjectsResult == null) {
                    cronStats.addStats("found", cloudFiles.size());
                    cronStats.addStats("deleted", 0);
                    cronStatsService.save(cronStats);

                    LOG.warn("Failed to find count={} and deleted count failure", cloudFiles.size());
                } else {
                    cronStats.addStats("found", cloudFiles.size());
                    cronStats.addStats("deleted", deleteObjectsResult.getDeletedObjects().size());
                    cronStatsService.save(cronStats);

                    LOG.info("Successfully found count={} and deleted count={}",
                            cloudFiles.size(),
                            deleteObjectsResult.getDeletedObjects().size());
                }
            }
        }
    }

    private DeleteObjectsRequest.KeyVersion populateDeleteObject(CloudFileEntity cloudFile) {
        switch (cloudFile.getFileType()) {
            case R:
                return new DeleteObjectsRequest.KeyVersion(receiptFolderName + "/" + cloudFile.getKey());
            case C:
                return new DeleteObjectsRequest.KeyVersion(couponFolderName + "/" + cloudFile.getKey());
            default:
                LOG.error("Not supported File type={}", cloudFile.getFileType());
                throw new UnsupportedOperationException("Any other operation is not supported");
        }

    }
}
