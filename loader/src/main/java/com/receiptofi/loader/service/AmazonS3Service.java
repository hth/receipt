package com.receiptofi.loader.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * User: hitender
 * Date: 12/1/14 2:21 AM
 */
@Service
public class AmazonS3Service {
    private final AmazonS3 s3client;

    @Autowired
    public AmazonS3Service(
            @Value ("${aws.s3.accessKey}")
            String accessKey,

            @Value ("${aws.s3.secretKey}")
            String secretKey,

            @Value ("${aws.s3.bucketName}")
            String bucketName
    ) {
        Assert.hasLength(accessKey, "AccessKey is blank");
        Assert.hasLength(secretKey, "SecretKey is blank");
        Assert.hasLength(bucketName, "BucketName is blank");
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTPS);

        final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3client = new AmazonS3Client(credentials, clientConfiguration);

        Assert.isTrue(s3client.doesBucketExist(bucketName), "bucketName " + bucketName + " exists");
    }

    public AmazonS3 getS3client() {
        return s3client;
    }
}
