package com.receiptofi.loader.scheduledtasks;

import static com.receiptofi.loader.service.AmazonS3ServiceTest.BUILD;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.CONF;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.findFiles;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.profileF;
import static com.receiptofi.loader.service.AmazonS3ServiceTest.propertiesF;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.loader.service.AmazonS3Service;
import com.receiptofi.service.CloudFileService;

import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@ActiveProfiles ({"dev", "test"})
public class FilesDeleteFromS3Test {
    private static final Logger LOG = LoggerFactory.getLogger(FilesDeleteFromS3Test.class);

    @Mock private AmazonS3 s3Client;
    @Mock private CloudFileService cloudFileService;
    @Mock private AmazonS3Service amazonS3Service;
    @Mock private CloudFileEntity cloudFileEntity;
    @Mock private DeleteObjectsResult deleteObjectsResult;
    @Mock private DeleteObjectsRequest deleteObjectsRequest;
    @Mock private MultiObjectDeleteException multiObjectDeleteException;

    private FilesDeleteFromS3 filesDeleteFromS3;
    private Properties prop = new Properties();

    @Before
    public void setUp() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        LOG.info("activeProfiles={}", ArrayUtils.toString(activeProfiles));

        /**
         * Loading properties file for junit.
         */
        if (prop.keySet().isEmpty()) {
            File[] profileDir = findFiles(FilesUploadToS3Test.class.getResource("").getPath().split("loader")[0] + BUILD, profileF);
            File[] propertiesFiles = findFiles(profileDir[0].getAbsolutePath() + CONF, propertiesF);
            for (File file : propertiesFiles) {
                prop.load(new FileReader(file));
            }
        }

        assertNotNull(prop.getProperty("aws.s3.bucketName"));
        MockitoAnnotations.initMocks(this);
        filesDeleteFromS3 = new FilesDeleteFromS3(
                prop.getProperty("aws.s3.bucketName"),
                cloudFileService,
                amazonS3Service);

        when(amazonS3Service.getS3client()).thenReturn(s3Client);
    }

    @Test
    public void deleteWhenEmpty() {
        when(cloudFileService.getAllMarkedAsDeleted()).thenReturn(new ArrayList<CloudFileEntity>());

        filesDeleteFromS3.delete();
        verify(amazonS3Service, never()).getS3client();
        verify(cloudFileService, never()).deleteHard(any(CloudFileEntity.class));
    }

    @Test
    public void deleteException() {
        when(cloudFileService.getAllMarkedAsDeleted()).thenReturn(Arrays.asList(cloudFileEntity));
        when(amazonS3Service.getS3client().deleteObjects(any(DeleteObjectsRequest.class))).thenThrow(multiObjectDeleteException);

        filesDeleteFromS3.delete();
        verify(amazonS3Service.getS3client(), times(1)).deleteObjects(any(DeleteObjectsRequest.class));
        verify(cloudFileService, never()).deleteHard(any(CloudFileEntity.class));
    }

    @Test
    public void delete() {
        when(cloudFileService.getAllMarkedAsDeleted()).thenReturn(Arrays.asList(cloudFileEntity));
        when(amazonS3Service.getS3client().deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(deleteObjectsResult);

        filesDeleteFromS3.delete();
        verify(amazonS3Service.getS3client(), times(1)).deleteObjects(any(DeleteObjectsRequest.class));
        verify(cloudFileService, times(1)).deleteHard(any(CloudFileEntity.class));
    }
}
