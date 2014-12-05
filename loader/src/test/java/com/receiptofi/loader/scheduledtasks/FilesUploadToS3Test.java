package com.receiptofi.loader.scheduledtasks;

import static com.receiptofi.loader.service.AmazonS3ServiceTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.loader.service.AmazonS3ServiceTest;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.FileSystemService;
import com.receiptofi.service.ImageSplitService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@Profile ({"dev", "test", "prod"})
public class FilesUploadToS3Test {
    private static final Logger LOG = LoggerFactory.getLogger(FilesUploadToS3Test.class);

    @Mock private AmazonS3 s3Client;
    @Mock private DocumentUpdateService documentUpdateService;
    @Mock private FileDBService fileDBService;
    @Mock private ImageSplitService imageSplitService;
    @Mock private FileSystemEntity fileSystemEntity1;
    @Mock private FileSystemEntity fileSystemEntity2;
    @Mock private DocumentEntity documentEntity;
    @Mock private GridFSDBFile gridFSDBFile;
    @Mock private InputStream inputStream;
    @Mock private AmazonS3Service amazonS3Service;
    @Mock private FileSystemService fileSystemService;

    private FilesUploadToS3 filesUploadToS3;
    private Properties prop = new Properties();

    @Before
    public void setUp() throws IOException {
        /**
         * Loading properties file for junit.
         */
        if (prop.keySet().isEmpty()) {
            ClassLoader classLoader = AmazonS3ServiceTest.class.getClassLoader();
            try {
                File[] profileDir = findFiles(classLoader.getResource("").getPath().split("loader")[0] + BUILD, profileF);
                File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
                prop.load(new FileReader(propertiesFiles[0]));
            } catch(IOException e) {
                LOG.error("setup reason={}", e.getLocalizedMessage(), e);
                throw e;
            }
        }


        MockitoAnnotations.initMocks(this);
        filesUploadToS3 = new FilesUploadToS3(
                prop.getProperty("aws.s3.bucketName"),
                documentUpdateService,
                fileDBService,
                imageSplitService,
                amazonS3Service,
                fileSystemService
        );
        when(gridFSDBFile.getInputStream()).thenReturn(inputStream);
        when(fileDBService.getFile(anyString())).thenReturn(gridFSDBFile);

        when(fileSystemEntity1.getOriginalFilename()).thenReturn("fileA.jpg");
        when(fileSystemEntity1.getBlobId()).thenReturn("1234567890A");
        when(fileSystemEntity1.getContentType()).thenReturn("image/jpeg");
        when(fileSystemEntity1.getFileLength()).thenReturn(123L);

        when(fileSystemEntity2.getOriginalFilename()).thenReturn("fileB.png");
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
        when(documentUpdateService.getAllProcessedDocuments()).thenReturn(new ArrayList<DocumentEntity>());
        filesUploadToS3.upload();
        assertEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(documentUpdateService, never()).cloudUploadSuccessful(any(String.class));
    }

    @Test
    public void testAmazonServiceException() {
        when(documentUpdateService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        doThrow(AmazonServiceException.class).when(amazonS3Service).getS3client();

        doNothing().when(documentUpdateService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.upload();
        assertNotEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
        verify(documentUpdateService, never()).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testAmazonClientException() {
        when(documentUpdateService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);
        doThrow(AmazonClientException.class).when(amazonS3Service).getS3client();

        doNothing().when(documentUpdateService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.upload();
        assertNotEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
        verify(documentUpdateService, never()).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testException() {
        when(documentUpdateService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);

        doThrow(Exception.class).when(documentUpdateService).cloudUploadSuccessful(anyString());

        filesUploadToS3.upload();
        assertNotEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(s3Client, atMost(2)).putObject(any(PutObjectRequest.class));
        verify(documentUpdateService, times(1)).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testUpload() {
        when(documentUpdateService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);

        doNothing().when(documentUpdateService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        filesUploadToS3.upload();
        assertNotEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(documentUpdateService, times(1)).cloudUploadSuccessful(documentEntity.getId());
        verify(fileDBService, times(1)).deleteHard(documentEntity.getFileSystemEntities());
    }
}
