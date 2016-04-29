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
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "COMMENT")
public class CommentEntity extends BaseEntity {

    //TODO(hth) @Value annotation is not working, find why
    @Value ("${textLength:250}")
    private int textLength = 250;

    @Field ("RID")
    private String receiptUserId;

    @Field ("T")
    private String text;

    @Field ("CT")
    private CommentTypeEnum commentType;

    /**
     * To keep bean happy
     */
    public CommentEntity() {
        super();
    }

    private CommentEntity(String receiptUserId, CommentTypeEnum commentType) {
        super();
        this.receiptUserId = receiptUserId;
        this.commentType = commentType;
    }

    private CommentEntity(CommentTypeEnum commentType) {
        super();
        this.commentType = commentType;
    }

    public static CommentEntity newInstance(String receiptUserId, CommentTypeEnum commentType) {
        return new CommentEntity(receiptUserId, commentType);
    }

    public static CommentEntity newInstance(CommentTypeEnum commentType) {
        return new CommentEntity(commentType);
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

    public String getReceiptUserId() {
        return receiptUserId;
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
