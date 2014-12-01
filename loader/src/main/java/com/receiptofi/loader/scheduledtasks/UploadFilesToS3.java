package com.receiptofi.loader.scheduledtasks;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.ImageSplitService;
import com.receiptofi.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * User: hitender
 * Date: 11/28/14 11:35 AM
 */
@Component
public class UploadFilesToS3 {
    private static final Logger LOG = LoggerFactory.getLogger(UploadFilesToS3.class);

    private final String bucketName;

    private DocumentUpdateService documentUpdateService;
    private FileDBService fileDBService;
    private ImageSplitService imageSplitService;
    private AmazonS3Service amazonS3Service;

    @Autowired
    public UploadFilesToS3(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            DocumentUpdateService documentUpdateService,
            FileDBService fileDBService,
            ImageSplitService imageSplitService,
            AmazonS3Service amazonS3Service
    ) {
        this.bucketName = bucketName;

        this.documentUpdateService = documentUpdateService;
        this.fileDBService = fileDBService;
        this.imageSplitService = imageSplitService;
        this.amazonS3Service = amazonS3Service;
    }

    /**
     * Note: For every two second use *\/2 * * * * ? where as cron string blow run every 5 minutes.
     */
    @Scheduled (cron = "0/300 * * * * ?")
    public void upload() {
        List<DocumentEntity> documents = documentUpdateService.getAllProcessedDocuments();
        if(!documents.isEmpty()) {
            LOG.info("Documents to upload to cloud, count={}", documents.size());
        }

        int count = 0, failure = 0;
        for (DocumentEntity document : documents) {
            try {
                Collection<FileSystemEntity> fileSystemEntities = document.getFileSystemEntities();
                for (FileSystemEntity fileSystem : fileSystemEntities) {
                    File file = FileUtil.createTempFile(
                            fileSystem.getOriginalFilename(),
                            FileUtil.getFileExtension(fileSystem.getOriginalFilename()));

                    OutputStream os = new FileOutputStream(file);
                    GridFSDBFile gridFSDBFile = fileDBService.getFile(fileSystem.getBlobId());
                    imageSplitService.decreaseResolution(gridFSDBFile.getInputStream(), os);

                    //Set orientation of the image too
                    //imageSplitService.bufferedImage(file);

                    PutObjectRequest putObject = new PutObjectRequest(bucketName, getKey(fileSystem), file);
                    putObject.setMetadata(getObjectMetadata(document, fileSystem));

                    amazonS3Service.getS3client().putObject(putObject);
                }
                documentUpdateService.cloudUploadSuccessful(document.getId());
                fileDBService.deleteHard(document.getFileSystemEntities());
                count ++;
            } catch (AmazonServiceException e) {
                LOG.error("Amazon S3 rejected request with an error response for some reason " +
                                "document:{} " +
                                "Error Message:{} " +
                                "HTTP Status Code:{} " +
                                "AWS Error Code:{} " +
                                "Error Type:{} " +
                                "Request ID:{}",
                        document,
                        e.getLocalizedMessage(),
                        e.getStatusCode(),
                        e.getErrorCode(),
                        e.getErrorType(),
                        e.getRequestId(),
                        e);

                failure ++;
            } catch (AmazonClientException e) {
                LOG.error("Client encountered an internal error while trying to communicate with S3 " +
                        "document:{} " +
                        "reason={}",
                        document,
                        e.getLocalizedMessage(),
                        e);

                failure ++;
            } catch (Exception e) {
                LOG.error("image upload failure document={} reason={}", document, e.getLocalizedMessage(), e);

                failure ++;

                for(FileSystemEntity fileSystem : document.getFileSystemEntities()) {
                    amazonS3Service.getS3client().deleteObject(bucketName, getKey(fileSystem));
                    LOG.warn("on failure removed files from cloud filename={}", getKey(fileSystem));
                }
            } finally {
                LOG.info("Documents upload success={} failure={} total={}", count, failure, documents.size());
            }
        }
    }

    /**
     * Adds metadata like Receipt User Id, Receipt Id and Receipt Date to file.
     * @param document
     * @param fileSystem
     * @return
     */
    private ObjectMetadata getObjectMetadata(DocumentEntity document, FileSystemEntity fileSystem) {
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(fileSystem.getContentType());
        metaData.addUserMetadata("X-RID", document.getReceiptUserId());
        metaData.addUserMetadata("X-DID", document.getReferenceDocumentId());
        metaData.addUserMetadata("X-RTXD", document.getReceiptDate());
        metaData.addUserMetadata("X-CL", String.valueOf(fileSystem.getFileLength()));

        return metaData;
    }

    /**
     * Name of the file on cloud.
     * @param fileSystemEntity
     * @return
     */
    private String getKey(FileSystemEntity fileSystemEntity) {
        return fileSystemEntity.getBlobId() +
                FileUtil.DOT +
                FileUtil.getFileExtension(fileSystemEntity.getOriginalFilename());
    }

    /**
     * Helps in rotating image.
     * link https://stackoverflow.com/questions/5905868/am-i-making-this-too-complicated-image-rotation
     * link https://stackoverflow.com/questions/20275424/rotating-image-with-affinetransform
     * @param image
     * @param transform
     * @return
     * @throws Exception
     */
    //TODO(hth) complete image rotation before uploading to cloud
    public static BufferedImage transformImage(BufferedImage image, AffineTransform transform) throws Exception {
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

        BufferedImage destinationImage = op.createCompatibleDestImage(
                image,
                image.getType() == BufferedImage.TYPE_BYTE_GRAY? image.getColorModel() : null);

        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        destinationImage = op.filter(image, destinationImage);
        return destinationImage;
    }
}
