package com.tholix.domain;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

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
    private static final int TEXT_LENGTH = 250;

    @Size(min = 0, max = TEXT_LENGTH)
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
        text = StringUtils.trim(text);
        this.text = StringUtils.substring(text, 0, TEXT_LENGTH);
    }
}
