package com.receiptofi.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BrowserEntity;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 4:08 PM
 */
public interface BrowserManager extends RepositoryManager<BrowserEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(BrowserEntity.class, Document.class, "collection");

}
