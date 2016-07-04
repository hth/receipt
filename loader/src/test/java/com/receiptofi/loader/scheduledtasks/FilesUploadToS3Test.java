package com.receiptofi.loader.scheduledtasks;

import static com.receiptofi.loader.service.AmazonS3ServiceTest.BUILD;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.CONF;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.findFiles;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.profileF;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.propertiesF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.loader.service.AffineTransformService;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.BusinessCampaignService;
import com.receiptofi.service.CouponService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.FileSystemService;
import com.receiptofi.service.ImageSplitService;

import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * ActiveProfiles makes sure upload test does not run on PROD.
 * Note: Run this test through gradle and not IntelliJ
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@ActiveProfiles ({"dev", "test"})
public class FilesUploadToS3Test {
    private static final Logger LOG = LoggerFactory.getLogger(FilesUploadToS3Test.class);

    @Mock private AmazonS3 s3Client;
    @Mock private DocumentService documentService;
    @Mock private FileDBService fileDBService;
    @Mock private ImageSplitService imageSplitService;
    @Mock private FileSystemEntity fileSystemEntity1;
    @Mock private FileSystemEntity fileSystemEntity2;
    @Mock private DocumentEntity documentEntity;
    @Mock private GridFSDBFile gridFSDBFile;
    @Mock private InputStream inputStream;
    @Mock private AmazonS3Service amazonS3Service;
    @Mock private FileSystemService fileSystemService;
    @Mock private AffineTransformService affineTransformService;
    @Mock private BufferedImage bufferedImage;
    @Mock private CronStatsService cronStatsService;
    @Mock private CouponService couponService;
    @Mock private BusinessCampaignService businessCampaignService;

    private FilesUploadToS3 filesUploadToS3;
    private Properties prop = new Properties();

    @Before
    public void setUp() throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        LOG.info("activeProfiles={}", ArrayUtils.toString(activeProfiles));

        /**
         * Loading properties file for junit.
         */
        if (prop.keySet().isEmpty()) {
            /** loader is the path name for this class. */
            File[] profileDir = findFiles(FilesUploadToS3Test.class.getResource("").getPath().split("loader")[0] + BUILD, profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
            for (File file : propertiesFiles) {
                prop.load(new FileReader(file));
            }
        }

        assertNotNull("Bucket name has to exists", prop.getProperty("aws.s3.bucketName"));
        MockitoAnnotations.initMocks(this);
        filesUploadToS3 = new FilesUploadToS3(
                prop.getProperty("aws.s3.bucketName"),
                prop.getProperty("aws.s3.bucketName"),
                prop.getProperty("aws.s3.couponBucketName"),
                prop.getProperty("filesUploadToS3"),
                documentService,
                fileDBService,
                imageSplitService,
                amazonS3Service,
                fileSystemService,
                affineTransformService,
                cronStatsService,
                couponService,
                businessCampaignService);
        when(gridFSDBFile.getInputStream()).thenReturn(inputStream);
        when(fileDBService.getFile(anyString())).thenReturn(gridFSDBFile);

        when(fileSystemEntity1.getOriginalFilename()).thenReturn("test-fileA.jpg");
        when(fileSystemEntity1.getBlobId()).thenReturn("1234567890A");
        when(fileSystemEntity1.getContentType()).thenReturn("image/jpeg");
        when(fileSystemEntity1.getFileLength()).thenReturn(123L);

        when(fileSystemEntity2.getOriginalFilename()).thenReturn("test-fileB.png");
        when(fileSystemEntity2.getBlobId()).thenReturn("1234567890B");
        when(fileSystemEntity2.getContentType()).thenReturn("image/png");
        when(fileSystemEntity2.getFileLength()).thenReturn(123L);

        when(documentEntity.getFileSystemEntities()).thenReturn(Arrays.asList(fileSystemEntity1, fileSystemEntity2));
        when(documentEntity.getReceiptUserId()).thenReturn("rid");
        when(documentEntity.getReferenceDocumentId()).thenReturn("rdid");
        when(documentEntity.getReceiptDate()).thenReturn("01/01/2014");
    }

    @Test
    public void testEmptyDocumentList() {
        when(documentService.getAllProcessedDocuments()).thenReturn(new ArrayList<>());
        filesUploadToS3.receiptUpload();
        assertEquals(0, documentService.getAllProcessedDocuments().size());
        verify(documentService, never()).cloudUploadSuccessful(any(String.class));
    }

    @Test
    public void testAmazonServiceException() throws IOException {
        when(documentService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        when(imageSplitService.bufferedImage(any(File.class))).thenReturn(bufferedImage);
        doThrow(AmazonServiceException.class).when(amazonS3Service).getS3client();

        doNothing().when(documentService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.receiptUpload();
        assertNotEquals(0, documentService.getAllProcessedDocuments().size());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
        verify(documentService, never()).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testAmazonClientException() throws IOException {
        when(documentService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        when(imageSplitService.bufferedImage(any(File.class))).thenReturn(bufferedImage);
        doThrow(AmazonClientException.class).when(amazonS3Service).getS3client();

        doNothing().when(documentService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.receiptUpload();
        assertNotEquals(0, documentService.getAllProcessedDocuments().size());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
        verify(documentService, never()).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testException() throws IOException {
        when(documentService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        when(imageSplitService.bufferedImage(any(File.class))).thenReturn(bufferedImage);

        doThrow(Exception.class).when(documentService).cloudUploadSuccessful(anyString());

        filesUploadToS3.receiptUpload();
        assertNotEquals(0, documentService.getAllProcessedDocuments().size());
        verify(s3Client, atMost(2)).putObject(any(PutObjectRequest.class));
        verify(documentService, times(1)).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testUpload() throws IOException {
        when(documentService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        when(imageSplitService.bufferedImage(any(File.class))).thenReturn(bufferedImage);

        doNothing().when(documentService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.receiptUpload();
        assertNotEquals(0, documentService.getAllProcessedDocuments().size());
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(documentService, times(1)).cloudUploadSuccessful(documentEntity.getId());
        verify(fileDBService, times(1)).deleteHard(documentEntity.getFileSystemEntities());
    }

    @Test
    public void testImageRotation() throws IOException {
        when(documentService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        when(imageSplitService.bufferedImage(any(File.class))).thenReturn(bufferedImage);
        when(bufferedImage.getWidth()).thenReturn(100);
        when(bufferedImage.getHeight()).thenReturn(300);
        when(bufferedImage.getType()).thenReturn(6);
        when(fileSystemEntity1.getImageOrientation()).thenReturn(-180);
        when(fileSystemEntity2.getImageOrientation()).thenReturn(90);
        when(imageSplitService.writeToFile(anyString(), any(BufferedImage.class))).thenReturn(new File(""));

        doNothing().when(affineTransformService).affineTransform(
                any(BufferedImage.class),
                any(BufferedImage.class),
                any(AffineTransform.class));
        doNothing().when(documentService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.receiptUpload();
        assertNotEquals(0, documentService.getAllProcessedDocuments().size());
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(documentService, times(1)).cloudUploadSuccessful(documentEntity.getId());
        verify(fileDBService, times(1)).deleteHard(documentEntity.getFileSystemEntities());
    }
}
