package com.receiptofi.domain;

import com.receiptofi.utils.FileUtil;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

/**
 * File to be deleted from S3.
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
    @Field ("KEY")
    private String key;

    private CloudFileEntity(String key) {
        super();
        this.key = key;
        this.markAsDeleted();
    }

    /**
     * New instance created is marked as deleted.
     * @param key
     * @return
     */
    public static CloudFileEntity newInstance(String key) {
        Assert.isTrue(key.contains(FileUtil.DOT));
        return new CloudFileEntity(key);
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "CloudFileEntity{" +
                "key='" + key + '\'' +
                '}';
    }
}
