package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.loader.service.AffineTransformService;
import com.receiptofi.loader.service.AmazonS3Service;
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

    private DocumentUpdateService documentUpdateService;
    private FileDBService fileDBService;
    private ImageSplitService imageSplitService;
    private AmazonS3Service amazonS3Service;
    private FileSystemService fileSystemService;
    private AffineTransformService affineTransformService;

    @Autowired
    public FilesUploadToS3(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            @Value ("${aws.s3.bucketName}")
            String folderName,

            DocumentUpdateService documentUpdateService,
            FileDBService fileDBService,
            ImageSplitService imageSplitService,
            AmazonS3Service amazonS3Service,
            FileSystemService fileSystemService,
            AffineTransformService affineTransformService
    ) {
        this.bucketName = bucketName;
        this.folderName = folderName;

        this.documentUpdateService = documentUpdateService;
        this.fileDBService = fileDBService;
        this.imageSplitService = imageSplitService;
        this.amazonS3Service = amazonS3Service;
        this.fileSystemService = fileSystemService;
        this.affineTransformService = affineTransformService;
    }

    /**
     * Note: Cron string blow run every 5 minutes.
     */
    @Scheduled (fixedDelayString = "${loader.FilesUploadToS3.upload}")
    public void upload() {
        List<DocumentEntity> documents = documentUpdateService.getAllProcessedDocuments();
        if (!documents.isEmpty()) {
            LOG.info("Documents to upload to cloud, count={}", documents.size());
        }

        int count = 0, failure = 0;
        for (DocumentEntity document : documents) {
            try {
                Collection<FileSystemEntity> fileSystemEntities = document.getFileSystemEntities();
                for (FileSystemEntity fileSystem : fileSystemEntities) {
                    if (0L == fileSystem.getScaledFileLength()) {
                        File scaledImage = FileUtil.createTempFile(
                                FilenameUtils.getBaseName(fileSystem.getOriginalFilename()),
                                FileUtil.getFileExtension(fileSystem.getOriginalFilename()));

                        imageSplitService.decreaseResolution(
                                fileDBService.getFile(fileSystem.getBlobId()).getInputStream(),
                                new FileOutputStream(scaledImage));

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
                    } else {
                        LOG.info("Skipped file={} as it exists in S3 SNL={}",
                                fileSystem.getBlobId(),
                                fileSystem.getScaledFileLength());
                    }
                }
                documentUpdateService.cloudUploadSuccessful(document.getId());
                fileDBService.deleteHard(document.getFileSystemEntities());
                count++;
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
                LOG.error("image upload failure document={} reason={}", document, e.getLocalizedMessage(), e);

                failure++;

                for (FileSystemEntity fileSystem : document.getFileSystemEntities()) {
                    amazonS3Service.getS3client().deleteObject(bucketName, folderName + "/" + fileSystem.getKey());
                    LOG.warn("on failure removed files from cloud filename={}", fileSystem.getKey());
                }
            } finally {
                LOG.info("Documents upload success={} failure={} total={}", count, failure, documents.size());
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
