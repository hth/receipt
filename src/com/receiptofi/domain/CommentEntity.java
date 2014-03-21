package com.receiptofi.domain;

import com.receiptofi.domain.types.CommentTypeEnum;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.google.common.base.Objects;

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
    @Field("T")
    private String text;

    @Field("CT_E")
    private CommentTypeEnum commentType;

    public CommentEntity() {}

    public static CommentEntity newInstance(CommentTypeEnum commentType) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentType(commentType);
        return commentEntity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = StringUtils.substring(StringUtils.trim(text), 0, TEXT_LENGTH);
    }

    public CommentTypeEnum getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentTypeEnum commentType) {
        this.commentType = commentType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("text", text)
                .add("updated", getUpdated())
                .toString();
    }
}
