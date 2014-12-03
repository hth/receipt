package com.receiptofi.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 12/2/14 6:17 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "CLOUD_FILE")
public class CloudFileEntity extends BaseEntity {

    @NotNull
    @Field ("BID")
    private String blobId;

    private CloudFileEntity(String blobId) {
        this.blobId = blobId;
    }

    public static CloudFileEntity newInstance(String blobId) {
        return new CloudFileEntity(blobId);
    }

    public String getBlobId() {
        return blobId;
    }

    @Override
    public String toString() {
        return "CloudFilesEntity{" +
                "blobId='" + blobId + '\'' +
                '}';
    }
}
