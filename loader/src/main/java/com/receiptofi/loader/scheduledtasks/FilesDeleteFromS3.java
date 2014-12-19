package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.CloudFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;

import java.util.ArrayList;
import java.util.List;

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

    private AmazonS3Service amazonS3Service;
    private CloudFileService cloudFileService;

    @Autowired
    public FilesDeleteFromS3(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            CloudFileService cloudFileService,
            AmazonS3Service amazonS3Service
    ) {
        this.bucketName = bucketName;
        this.cloudFileService = cloudFileService;
        this.amazonS3Service = amazonS3Service;
    }

    /**
     * Delete files from S3.
     */
    @Scheduled (cron = "${loader.FilesDeleteFromS3.delete}")
    public void delete() {
        DeleteObjectsResult deleteObjectsResult = null;
        List<CloudFileEntity> cloudFileEntities = cloudFileService.getAllMarkedAsDeleted();
        if (!cloudFileEntities.isEmpty()) {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();
            for (CloudFileEntity cloudFile : cloudFileEntities) {
                keys.add(new DeleteObjectsRequest.KeyVersion(cloudFile.getKey()));
            }
            deleteObjectsRequest.setKeys(keys);
            try {
                deleteObjectsResult = amazonS3Service.getS3client().deleteObjects(deleteObjectsRequest);
                for (CloudFileEntity cloudFile : cloudFileEntities) {
                    cloudFileService.deleteHard(cloudFile);
                }
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
                    LOG.warn("Successfully found count={} and deleted count failure", cloudFileEntities.size());
                } else {
                    LOG.info("Successfully found count={} and deleted count={}",
                            cloudFileEntities.size(),
                            deleteObjectsResult.getDeletedObjects().size());
                }
            }
        }
    }
}
