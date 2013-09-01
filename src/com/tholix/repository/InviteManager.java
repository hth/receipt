package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.InviteEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:15 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(InviteEntity.class, Document.class, "collection");

    /**
     * Find InviteEntity by authentication key
     *
     * @param auth
     * @return
     */
    InviteEntity findByAuthenticationKey(String auth);

    /**
     * Make all the existing request invalid
     *
     * @param object
     */
    void invalidateAllEntries(InviteEntity object);

    /**
     * Find the user who has been invited and the invite is active
     *
     * @param emailId
     */
    InviteEntity reInviteActiveInvite(String emailId, UserProfileEntity invitedBy);

    /**
     * Search existing invite by email id that is active and not deleted
     *
     * @param emailId
     * @return
     */
    InviteEntity find(String emailId);
}
