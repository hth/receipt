package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 7/19/13
 * Time: 8:32 AM
 */
@Document(collection = "EVAL_FEEDBACK")
@CompoundIndexes(value = {
        @CompoundIndex(name = "eval_feedback_idx",    def = "{'USER_PROFILE_ID': 1, 'CREATE': 1}",  unique=true),
} )
public class EvalFeedbackEntity extends BaseEntity {

    @DBRef
    @Field("COMMENT")
    private CommentEntity comment;

    @Field("ATTACHMENT_BLOB_ID")
    private String attachmentBlobId;

    @NotNull
    @Field("RATE")
    private int rating;

    @NotNull
    @Field("USER_PROFILE_ID")
    private String userProfileId;

    private EvalFeedbackEntity() {}

    public static EvalFeedbackEntity newInstance(CommentEntity comment, int rating, String userProfileId) {
        EvalFeedbackEntity evalFeedbackEntity = new EvalFeedbackEntity();
        evalFeedbackEntity.setComment(comment);
        evalFeedbackEntity.setRating(rating);
        evalFeedbackEntity.setUserProfileId(userProfileId);
        return evalFeedbackEntity;
    }

    public CommentEntity getComment() {
        return comment;
    }

    public void setComment(CommentEntity comment) {
        this.comment = comment;
    }

    public String getAttachmentBlobId() {
        return attachmentBlobId;
    }

    public void setAttachmentBlobId(String attachmentBlobId) {
        this.attachmentBlobId = attachmentBlobId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }
}
