package com.receiptofi.service;

import com.receiptofi.domain.CommentEntity;
import com.receiptofi.repository.CommentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 4/5/15 3:00 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CommentService {

    private CommentManager commentManager;

    @Autowired
    public CommentService(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    public CommentEntity getById(String id) {
        return commentManager.getById(id);
    }

    public void save(CommentEntity comment) {
        commentManager.save(comment);
    }

    public void deleteHard(CommentEntity comment) {
        commentManager.deleteHard(comment);
    }
}
