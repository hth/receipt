package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 8/24/14 11:27 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
@Mobile
public class JsonFileSystem {

    @JsonProperty ("blobId")
    private String blobId;

    @JsonProperty ("orientation")
    private int imageOrientation;

    @JsonProperty ("sequence")
    private int sequence;

    private JsonFileSystem(String blobId, int imageOrientation, int sequence) {
        this.blobId = blobId;
        this.imageOrientation = imageOrientation;
        this.sequence = sequence;
    }

    public static JsonFileSystem newInstance(FileSystemEntity fileSystemEntity) {
        return new JsonFileSystem(
                fileSystemEntity.getBlobId(),
                fileSystemEntity.getImageOrientation(),
                fileSystemEntity.getSequence()
        );
    }
}
