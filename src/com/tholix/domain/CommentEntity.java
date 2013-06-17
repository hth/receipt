package com.tholix.domain;

import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 3:22 AM
 */
@Document(collection = "COMMENT")
@CompoundIndexes(value = {
        @CompoundIndex(name = "comment_idx", def = "{'id': 1}", unique=true),
} )
public class CommentEntity extends BaseEntity {

    @Size(min = 0, max  = 256)
    private String comment;

    public CommentEntity() {

    }

    public static CommentEntity newInstance() {
        return new CommentEntity();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
