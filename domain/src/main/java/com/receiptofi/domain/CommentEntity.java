package com.receiptofi.domain;

import com.receiptofi.domain.types.CommentTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 3:22 AM
 */
@Document (collection = "COMMENT")
public final class CommentEntity extends BaseEntity {

    //TODO(hth) @Value annotation is not working, find why
    @Value ("${textLength:250}")
    private int textLength = 250;

    @Field ("T")
    private String text;

    @Field ("CT")
    private CommentTypeEnum commentType;

    public CommentEntity() {
    }

    public static CommentEntity newInstance(CommentTypeEnum commentType) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentType(commentType);
        return commentEntity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = StringUtils.substring(StringUtils.trim(text), 0, textLength);
    }

    public CommentTypeEnum getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentTypeEnum commentType) {
        this.commentType = commentType;
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "textLength=" + textLength +
                ", text='" + text + '\'' +
                ", commentType=" + commentType +
                '}';
    }
}
