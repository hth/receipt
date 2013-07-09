package com.tholix.domain;

import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 3:22 AM
 */
@Document(collection = "COMMENT")
@CompoundIndexes(value = {
        @CompoundIndex(name = "comment_idx", def = "{'ID': 1}"),
} )
public class CommentEntity extends BaseEntity {

    @Size(min = 0, max  = 256)
    @Field("TEXT")
    private String text;

    public CommentEntity() {}

    public static CommentEntity newInstance() {
        return new CommentEntity();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
