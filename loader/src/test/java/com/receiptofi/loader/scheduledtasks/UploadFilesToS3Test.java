package com.receiptofi.loader.scheduledtasks;

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
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.ImageSplitService;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RunWith (MockitoJUnitRunner.class)
public class UploadFilesToS3Test {

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

    private UploadFilesToS3 uploadFilesToS3;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        uploadFilesToS3 = new UploadFilesToS3(
                "chk.test",
                documentUpdateService,
                fileDBService,
                imageSplitService,
                amazonS3Service
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
        uploadFilesToS3.upload();
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

        uploadFilesToS3.upload();
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

        uploadFilesToS3.upload();
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

        uploadFilesToS3.upload();
        assertNotEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(s3Client, atMost(2)).putObject(any(PutObjectRequest.class));
        verify(documentUpdateService, atMost(1)).cloudUploadSuccessful(anyString());
        verify(fileDBService, never()).deleteHard(anyCollectionOf(FileSystemEntity.class));
    }

    @Test
    public void testUpload() {
        when(documentUpdateService.getAllProcessedDocuments()).thenReturn(Arrays.asList(documentEntity));
        when(amazonS3Service.getS3client()).thenReturn(s3Client);

        doNothing().when(documentUpdateService).cloudUploadSuccessful(anyString());
        doNothing().when(fileDBService).deleteHard(anyCollectionOf(FileSystemEntity.class));

        uploadFilesToS3.upload();
        assertNotEquals(0, documentUpdateService.getAllProcessedDocuments().size());
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(documentUpdateService, atMost(1)).cloudUploadSuccessful(documentEntity.getId());
        verify(fileDBService, atMost(1)).deleteHard(documentEntity.getFileSystemEntities());
    }
}
