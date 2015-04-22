package com.receiptofi.loader.scheduledtasks;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.loader.service.AffineTransformService;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.FileSystemService;
import com.receiptofi.service.ImageSplitService;
import com.receiptofi.utils.FileUtil;

import org.apache.commons.io.FilenameUtils;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * User: hitender
 * Date: 11/28/14 11:35 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class FilesUploadToS3 {
    private static final Logger LOG = LoggerFactory.getLogger(FilesUploadToS3.class);
    private static final int ROTATE_AT_CENTER = 2;

    private final String bucketName;
    private final String folderName;
    private final String filesUploadToS3;

    private DocumentUpdateService documentUpdateService;
    private FileDBService fileDBService;
    private ImageSplitService imageSplitService;
    private AmazonS3Service amazonS3Service;
    private FileSystemService fileSystemService;
    private AffineTransformService affineTransformService;
    private CronStatsService cronStatsService;

    @Autowired
    public FilesUploadToS3(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            @Value ("${aws.s3.bucketName}")
            String folderName,

            @Value ("${filesUploadToS3}")
            String filesUploadToS3,

            DocumentUpdateService documentUpdateService,
            FileDBService fileDBService,
            ImageSplitService imageSplitService,
            AmazonS3Service amazonS3Service,
            FileSystemService fileSystemService,
            AffineTransformService affineTransformService,
            CronStatsService cronStatsService
    ) {
        this.bucketName = bucketName;
        this.folderName = folderName;
        this.filesUploadToS3 = filesUploadToS3;

        this.documentUpdateService = documentUpdateService;
        this.fileDBService = fileDBService;
        this.imageSplitService = imageSplitService;
        this.amazonS3Service = amazonS3Service;
        this.fileSystemService = fileSystemService;
        this.affineTransformService = affineTransformService;
        this.cronStatsService = cronStatsService;
    }

    /**
     * Note: Cron string blow run every 5 minutes.
     */
    @Scheduled (fixedDelayString = "${loader.FilesUploadToS3.upload}")
    public void upload() {
        CronStatsEntity cronStats = new CronStatsEntity(
                FilesUploadToS3.class.getName(),
                "Upload",
                filesUploadToS3);

        /**
         * TODO prevent test db connection from dev. As this moves files to 'dev' bucket in S3 and test environment fails to upload to 'test' bucket.
         * NOTE: This is one of the reason you should not connect to test database from dev environment. Or have a
         * fail safe to prevent uploading to dev bucket when connected to test database.
         */
        if ("OFF".equalsIgnoreCase(filesUploadToS3)) {
            LOG.info("feature is {}", filesUploadToS3);
            return;
        }

        List<DocumentEntity> documents = documentUpdateService.getAllProcessedDocuments();
        if (!documents.isEmpty()) {
            LOG.info("Documents to upload to cloud, count={}", documents.size());
        }

        int success = 0, failure = 0, skipped = 0;
        for (DocumentEntity document : documents) {
            try {
                Collection<FileSystemEntity> fileSystemEntities = document.getFileSystemEntities();
                for (FileSystemEntity fileSystem : fileSystemEntities) {
                    if (0L == fileSystem.getScaledFileLength()) {

                        GridFSDBFile fs = fileDBService.getFile(fileSystem.getBlobId());
                        if (null != fs) {
                            File scaledImage = FileUtil.createTempFile(
                                    FilenameUtils.getBaseName(fileSystem.getOriginalFilename()),
                                    FileUtil.getFileExtension(fileSystem.getOriginalFilename()));

                            imageSplitService.decreaseResolution(fs.getInputStream(), new FileOutputStream(scaledImage));
                            LOG.info("fileSystemID={} filename={} newFilename={} originalLength={} newLength={}",
                                    fileSystem.getId(),
                                    fileSystem.getOriginalFilename(),
                                    fileSystem.getBlobId(),
                                    FileUtil.fileSizeInMB(fileSystem.getFileLength()),
                                    FileUtil.fileSizeInMB(scaledImage.length()));

                            File fileForS3;
                            if (fileSystem.getImageOrientation() == FileSystemEntity.DEFAULT_ORIENTATION_ANGLE) {
                                fileForS3 = scaledImage;
                            } else {
                                fileForS3 = rotate(fileSystem.getImageOrientation(), scaledImage);
                            }
                            updateFileSystemWithScaledImageForS3(fileSystem, fileForS3);
                            PutObjectRequest putObject = getPutObjectRequest(document, fileSystem, fileForS3);
                            amazonS3Service.getS3client().putObject(putObject);
                            success++;
                        } else {
                            //TODO keep an eye on this issue. Should not happen.
                            skipped++;
                            LOG.error("Skipped file={} as it does not exists in GridFSDBFile", fileSystem.getBlobId());
                        }
                    } else {
                        skipped++;
                        LOG.info("Skipped file={} as it exists in S3 SNL={}",
                                fileSystem.getBlobId(),
                                fileSystem.getScaledFileLength());
                    }
                }
                documentUpdateService.cloudUploadSuccessful(document.getId());
                fileDBService.deleteHard(document.getFileSystemEntities());
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

                failure++;
            } catch (AmazonClientException e) {
                LOG.error("Client encountered an internal error while trying to communicate with S3 " +
                                "document:{} " +
                                "reason={}",
                        document,
                        e.getLocalizedMessage(),
                        e);

                failure++;
            } catch (Exception e) {
                LOG.error("Image upload failure document={} reason={}", document, e.getLocalizedMessage(), e);

                failure++;

                for (FileSystemEntity fileSystem : document.getFileSystemEntities()) {
                    amazonS3Service.getS3client().deleteObject(bucketName, folderName + "/" + fileSystem.getKey());
                    LOG.warn("On failure removed files from cloud filename={}", fileSystem.getKey());
                }
            } finally {
                cronStats.addStats("success", success);
                cronStats.addStats("skipped", skipped);
                cronStats.addStats("failure", failure);
                cronStats.addStats("found", documents.size());
                cronStatsService.save(cronStats);

                LOG.info("Documents upload success={} skipped={} failure={} total={}", success, skipped, failure, documents.size());
            }
        }
    }

    /**
     * Updates FileSystemEntity with scaled image info.
     *
     * @param fileSystem
     * @param fileForS3
     * @throws IOException
     */
    private void updateFileSystemWithScaledImageForS3(FileSystemEntity fileSystem, File fileForS3) throws Exception {
        BufferedImage bufferedImage = imageSplitService.bufferedImage(fileForS3);

        fileSystem.setImageOrientation(0);
        fileSystem.setScaledFileLength(fileForS3.length());
        fileSystem.setScaledHeight(bufferedImage.getHeight());
        fileSystem.setScaledWidth(bufferedImage.getWidth());
        fileSystemService.save(fileSystem);
    }

    /**
     * Rotates file by specified orientation.
     *
     * @param imageOrientation angle of rotation
     * @param file             original file
     * @return rotated new file
     * @throws IOException
     */
    private File rotate(int imageOrientation, File file) throws IOException {
        BufferedImage src = imageSplitService.bufferedImage(file);
        AffineTransform t = new AffineTransform();
        t.setToRotation(
                Math.toRadians(imageOrientation),
                src.getWidth() / ROTATE_AT_CENTER,
                src.getHeight() / ROTATE_AT_CENTER);

        // source image rectangle
        Point[] points = {
                new Point(0, 0),
                new Point(src.getWidth(), 0),
                new Point(src.getWidth(), src.getHeight()),
                new Point(0, src.getHeight())
        };

        // transform to destination rectangle
        t.transform(points, 0, points, 0, 4);

        // get destination rectangle bounding box
        Point min = new Point(points[0]);
        Point max = new Point(points[0]);
        for (int i = 1, n = points.length; i < n; i++) {
            Point p = points[i];
            double pX = p.getX(), pY = p.getY();

            // update min/max x
            if (pX < min.getX()) {
                min.setLocation(pX, min.getY());
            }
            if (pX > max.getX()) {
                max.setLocation(pX, max.getY());
            }

            // update min/max y
            if (pY < min.getY()) {
                min.setLocation(min.getX(), pY);
            }
            if (pY > max.getY()) {
                max.setLocation(max.getX(), pY);
            }
        }

        // determine new width, height
        int w = (int) (max.getX() - min.getX());
        int h = (int) (max.getY() - min.getY());

        // determine required translation
        double tx = min.getX();
        double ty = min.getY();

        // append required translation
        AffineTransform translation = new AffineTransform();
        translation.translate(-tx, -ty);

        t.preConcatenate(translation);

        BufferedImage dst = new BufferedImage(w, h, src.getType());
        affineTransformService.affineTransform(src, dst, t);
        return imageSplitService.writeToFile(file.getName(), dst);
    }

    /**
     * Populates PutObjectRequest.
     *
     * @param document
     * @param fileSystem
     * @param file
     * @return
     */
    private PutObjectRequest getPutObjectRequest(DocumentEntity document, FileSystemEntity fileSystem, File file) {
        PutObjectRequest putObject = new PutObjectRequest(bucketName, folderName + "/" + fileSystem.getKey(), file);
        putObject.setMetadata(getObjectMetadata(file.length(), document, fileSystem));
        return putObject;
    }

    /**
     * Adds metadata like Receipt User Id, Receipt Id and Receipt Date to file.
     *
     * @param fileLength
     * @param document
     * @param fileSystem
     * @return
     */
    private ObjectMetadata getObjectMetadata(long fileLength, DocumentEntity document, FileSystemEntity fileSystem) {
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(fileSystem.getContentType());
        metaData.addUserMetadata("X-RID", document.getReceiptUserId());
        metaData.addUserMetadata("X-RDID", document.getReferenceDocumentId());
        metaData.addUserMetadata("X-RTXD", document.getReceiptDate());
        metaData.addUserMetadata("X-CL", String.valueOf(fileLength));

        return metaData;
    }
}
