package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentOfTypeEnum;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 12/14/13 11:40 PM
 */
public class DocumentEntity extends BaseEntity {

    @NotNull
    @Field("DOCUMENT_TYPE")
    private DocumentOfTypeEnum documentOfType;

    public DocumentOfTypeEnum getDocumentOfType() {
        return documentOfType;
    }

    public void setDocumentOfType(DocumentOfTypeEnum documentOfType) {
        this.documentOfType = documentOfType;
    }
}
