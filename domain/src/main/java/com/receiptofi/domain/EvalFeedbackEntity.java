package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 7/19/13
 * Time: 8:32 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "EVAL_FEEDBACK")
@CompoundIndexes (value = {
        @CompoundIndex (name = "eval_feedback_idx", def = "{'RID': 1, 'C': 1}", unique = true),
})
public class EvalFeedbackEntity extends BaseEntity {

    @Field ("FBK")
    private String feedback;

    @Field ("ABI")
    private String attachmentBlobId;

    @NotNull
    @Field ("RT")
    private int rating;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @SuppressWarnings("unused")
    private EvalFeedbackEntity() {
        super();
    }

    private EvalFeedbackEntity(String feedback, int rating, String receiptUserId) {
        super();
        this.feedback = feedback;
        this.rating = rating;
        this.receiptUserId = receiptUserId;
    }

    public static EvalFeedbackEntity newInstance(String feedback, int rating, String receiptUserId) {
        return new EvalFeedbackEntity(feedback, rating, receiptUserId);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }
}
