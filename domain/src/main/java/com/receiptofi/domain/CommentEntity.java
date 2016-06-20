package com.receiptofi.domain;

import com.receiptofi.domain.types.CommentTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(CommentEntity.class);

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

    /**
     * Keep me for old data model when 'RID' did not existed.
     * @param commentType
     */
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

    public CommentEntity setText(String text) {
        if (null != commentType) {
            switch (commentType) {
                case C:
                    this.text = text;
                    break;
                case N:
                case R:
                    this.text = StringUtils.substring(StringUtils.trim(text), 0, commentType.getTextLength());
                    break;
                default:
                    LOG.error("Reached unsupported rid={} commentType={}", receiptUserId, commentType.getDescription());
                    throw new UnsupportedOperationException("Reached unsupported condition");
            }
        } else {
            this.text = StringUtils.substring(StringUtils.trim(text), 0, CommentTypeEnum.C.getTextLength());
        }
        return this;
    }

    public CommentTypeEnum getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentTypeEnum commentType) {
        this.commentType = commentType;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "textLength=" + (commentType != null ? commentType.getTextLength() : "0") +
                ", text='" + text + '\'' +
                ", commentType=" + commentType +
                '}';
    }
}
