package com.receiptofi.loader.scheduledtasks;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CouponEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.loader.service.AffineTransformService;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.CouponService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentService;
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
    private final String receiptFolderName;
    private final String couponFolderName;
    private final String filesUploadSwitch;

    private DocumentService documentService;
    private FileDBService fileDBService;
    private ImageSplitService imageSplitService;
    private AmazonS3Service amazonS3Service;
    private FileSystemService fileSystemService;
    private AffineTransformService affineTransformService;
    private CronStatsService cronStatsService;
    private CouponService couponService;

    @Autowired
    public FilesUploadToS3(
            @Value ("${aws.s3.bucketName}")
            String bucketName,

            @Value ("${aws.s3.bucketName}")
            String receiptFolderName,

            @Value ("${aws.s3.couponBucketName}")
            String couponFolderName,

            @Value ("${FilesUploadToS3.filesUploadSwitch}")
            String filesUploadSwitch,

            DocumentService documentService,
            FileDBService fileDBService,
            ImageSplitService imageSplitService,
            AmazonS3Service amazonS3Service,
            FileSystemService fileSystemService,
            AffineTransformService affineTransformService,
            CronStatsService cronStatsService,
            CouponService couponService) {
        this.bucketName = bucketName;
        this.receiptFolderName = receiptFolderName;
        this.couponFolderName = couponFolderName;
        this.filesUploadSwitch = filesUploadSwitch;

        this.documentService = documentService;
        this.fileDBService = fileDBService;
        this.imageSplitService = imageSplitService;
        this.amazonS3Service = amazonS3Service;
        this.fileSystemService = fileSystemService;
        this.affineTransformService = affineTransformService;
        this.cronStatsService = cronStatsService;
        this.couponService = couponService;
    }

    /**
     * Note: Cron string blow run every 5 minutes.
     */
    @Scheduled (fixedDelayString = "${loader.FilesUploadToS3.receiptUpload}")
    public void receiptUpload() {
        CronStatsEntity cronStats = new CronStatsEntity(
                FilesUploadToS3.class.getName(),
                "ReceiptUpload",
                filesUploadSwitch);

        /**
         * TODO prevent test db connection from dev. As this moves files to 'dev' bucket in S3 and test environment fails to upload to 'test' bucket.
         * NOTE: This is one of the reason you should not connect to test database from dev environment. Or have a
         * fail safe to prevent uploading to dev bucket when connected to test database.
         */
        if ("OFF".equalsIgnoreCase(filesUploadSwitch)) {
            LOG.info("feature is {}", filesUploadSwitch);
            return;
        }

        List<DocumentEntity> documents = documentService.getAllProcessedDocuments();
        if (documents.isEmpty()) {
            /** No documents to upload. */
            return;
        } else {
            LOG.info("Documents to upload to cloud, count={}", documents.size());
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (DocumentEntity document : documents) {
                try {
                    Collection<FileSystemEntity> fileSystems = document.getFileSystemEntities();
                    for (FileSystemEntity fileSystem : fileSystems) {
                        if (0L == fileSystem.getScaledFileLength()) {

                            GridFSDBFile fs = fileDBService.getFile(fileSystem.getBlobId());
                            if (null != fs) {
                                success = uploadToS3(success, document, fileSystem, fs);
                            } else {
                                //TODO keep an eye on this issue. Should not happen.
                                skipped++;
                                LOG.error("Skipped file={} as it does not exists in GridFSDBFile", fileSystem.getBlobId());
                            }
                        } else {
                            skipped++;
                            LOG.warn("Skipped file={} as it exists in S3 SNL={}",
                                    fileSystem.getBlobId(),
                                    fileSystem.getScaledFileLength());
                        }
                    }
                    documentService.cloudUploadSuccessful(document.getId());
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
                    LOG.error("S3 image upload failure document={} reason={}", document, e.getLocalizedMessage(), e);

                    failure++;

                    for (FileSystemEntity fileSystem : document.getFileSystemEntities()) {
                        amazonS3Service.getS3client().deleteObject(bucketName, receiptFolderName + "/" + fileSystem.getKey());
                        LOG.warn("On failure removed files from cloud filename={}", fileSystem.getKey());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error S3 uploading document reason={}", e.getLocalizedMessage(), e);
        } finally {
            saveUploadStats(cronStats, success, failure, skipped, documents.size());
        }
    }

    @Scheduled (fixedDelayString = "${loader.FilesUploadToS3.couponUpload}")
    public void couponUpload() {
        CronStatsEntity cronStats = new CronStatsEntity(
                FilesUploadToS3.class.getName(),
                "CouponUpload",
                filesUploadSwitch);

        /**
         * TODO prevent test db connection from dev. As this moves files to 'dev' bucket in S3 and test environment fails to upload to 'test' bucket.
         * NOTE: This is one of the reason you should not connect to test database from dev environment. Or have a
         * fail safe to prevent uploading to dev bucket when connected to test database.
         */
        if ("OFF".equalsIgnoreCase(filesUploadSwitch)) {
            LOG.info("feature is {}", filesUploadSwitch);
            return;
        }

        List<CouponEntity> coupons = couponService.findCouponToUpload();
        if (coupons.isEmpty()) {
            /** No coupons to upload. */
            return;
        } else {
            LOG.info("Coupons to upload to cloud, count={}", coupons.size());
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (CouponEntity coupon : coupons) {
                try {
                    Collection<FileSystemEntity> fileSystems = coupon.getFileSystemEntities();
                    for (FileSystemEntity fileSystem : fileSystems) {
                        GridFSDBFile fs = fileDBService.getFile(fileSystem.getBlobId());
                        if (null != fs) {
                            success = uploadToS3(success, coupon, fileSystem, fs);
                        } else {
                            //TODO keep an eye on this issue. Should not happen.
                            skipped++;
                            LOG.error("Skipped file={} as it does not exists in GridFSDBFile", fileSystem.getBlobId());
                        }

                    }
                    couponService.cloudUploadSuccessful(coupon.getId());
                    fileDBService.deleteHard(coupon.getFileSystemEntities());
                } catch (AmazonServiceException e) {
                    LOG.error("Amazon S3 rejected request with an error response for some reason " +
                                    "document:{} " +
                                    "Error Message:{} " +
                                    "HTTP Status Code:{} " +
                                    "AWS Error Code:{} " +
                                    "Error Type:{} " +
                                    "Request ID:{}",
                            coupon,
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
                            coupon,
                            e.getLocalizedMessage(),
                            e);

                    failure++;
                } catch (Exception e) {
                    LOG.error("S3 image upload failure document={} reason={}", coupon, e.getLocalizedMessage(), e);

                    failure++;

                    for (FileSystemEntity fileSystem : coupon.getFileSystemEntities()) {
                        amazonS3Service.getS3client().deleteObject(bucketName, couponFolderName + "/" + fileSystem.getKey());
                        LOG.warn("On failure removed files from cloud filename={}", fileSystem.getKey());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error S3 uploading document reason={}", e.getLocalizedMessage(), e);
        } finally {
            saveUploadStats(cronStats, success, failure, skipped, coupons.size());
        }
    }

    private void saveUploadStats(CronStatsEntity cronStats, int success, int failure, int skipped, int size) {
        cronStats.addStats("success", success);
        cronStats.addStats("skipped", skipped);
        cronStats.addStats("failure", failure);
        cronStats.addStats("found", size);
        cronStatsService.save(cronStats);

        LOG.info("S3 upload success={} skipped={} failure={} total={}", success, skipped, failure, size);
    }

    private int uploadToS3(int success, BaseEntity document, FileSystemEntity fileSystem, GridFSDBFile fs) throws IOException {
        PutObjectRequest putObject = createPutObjectRequest(document, fileSystem, fs);
        amazonS3Service.getS3client().putObject(putObject);

        /**
         * Reason: Your socket connection to the server was not read from or written to within
         * the timeout period. Idle connections will be closed.
         *
         * On successful update persist changes to FileSystemEntity.
         */
        fileSystemService.save(fileSystem);
        success++;
        return success;
    }

    private PutObjectRequest createPutObjectRequest(BaseEntity document, FileSystemEntity fileSystem, GridFSDBFile fs) throws IOException {
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
        return getPutObjectRequest(document, fileSystem, fileForS3);
    }

    private PutObjectRequest createPutObjectRequest(CouponEntity coupon, FileSystemEntity fileSystem, GridFSDBFile fs) throws IOException {
        LOG.info("fileSystemID={} filename={} newFilename={} originalLength={}",
                fileSystem.getId(),
                fileSystem.getOriginalFilename(),
                fileSystem.getBlobId(),
                FileUtil.fileSizeInMB(fileSystem.getFileLength()));

        File fileForS3 = FileUtil.createTempFile(
                FilenameUtils.getBaseName(fileSystem.getOriginalFilename()),
                FileUtil.getFileExtension(fileSystem.getOriginalFilename()));;
        fs.writeTo(fileForS3);
        updateFileSystemWithScaledImageForS3(fileSystem, fileForS3);
        return getPutObjectRequest(coupon, fileSystem, fileForS3);
    }

    /**
     * Updates FileSystemEntity with scaled image info.
     *
     * @param fileSystem
     * @param fileForS3
     * @throws IOException
     */
    private void updateFileSystemWithScaledImageForS3(FileSystemEntity fileSystem, File fileForS3) throws IOException {
        BufferedImage bufferedImage = imageSplitService.bufferedImage(fileForS3);

        fileSystem.setImageOrientation(0);
        fileSystem.setScaledFileLength(fileForS3.length());
        fileSystem.setScaledHeight(bufferedImage.getHeight());
        fileSystem.setScaledWidth(bufferedImage.getWidth());
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
    private PutObjectRequest getPutObjectRequest(BaseEntity document, FileSystemEntity fileSystem, File file) {
        PutObjectRequest putObject = null;

        if (document instanceof DocumentEntity) {
            putObject = new PutObjectRequest(bucketName, receiptFolderName + "/" + fileSystem.getKey(), file);
            putObject.setMetadata(getObjectMetadata(file.length(), document, fileSystem));
        } else if (document instanceof CouponEntity) {
            putObject = new PutObjectRequest(bucketName, couponFolderName + "/" + fileSystem.getKey(), file);
            putObject.setMetadata(getObjectMetadata(file.length(), document, fileSystem));
        }

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
    private ObjectMetadata getObjectMetadata(long fileLength, BaseEntity document, FileSystemEntity fileSystem) {
        ObjectMetadata metaData = new ObjectMetadata();

        if (document instanceof DocumentEntity) {
            metaData.setContentType(fileSystem.getContentType());
            metaData.addUserMetadata("X-RID", ((DocumentEntity) document).getReceiptUserId());
            metaData.addUserMetadata("X-RDID", ((DocumentEntity) document).getReferenceDocumentId());
            metaData.addUserMetadata("X-RTXD", ((DocumentEntity) document).getReceiptDate());
            metaData.addUserMetadata("X-CL", String.valueOf(fileLength));
        } else if (document instanceof CouponEntity) {
            metaData.setContentType(fileSystem.getContentType());
            metaData.addUserMetadata("X-RID", ((CouponEntity) document).getRid());
            metaData.addUserMetadata("X-CL", String.valueOf(fileLength));
        }

        return metaData;
    }
}
