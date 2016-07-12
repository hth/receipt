package com.receiptofi.repository;

import com.receiptofi.domain.MailEntity;
import com.receiptofi.domain.types.MailStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 7/10/16 3:25 PM
 */
public interface MailManager extends RepositoryManager<MailEntity> {

    List<MailEntity> pendingMails();

    void updateMail(String id, MailStatusEnum mailStatus);
}
