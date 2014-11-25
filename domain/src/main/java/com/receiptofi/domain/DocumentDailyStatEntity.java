package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentStatusEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/20/14 12:58 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Document (collection = "DOCUMENT_DAILY_STAT")
@CompoundIndexes ({@CompoundIndex (name = "document_daily_stat_idx", def = "{'DT': 1}", unique = true)})
public class DocumentDailyStatEntity extends BaseEntity {

    @NotNull
    @Field ("DT")
    private Date date;

    @Field ("DPS")
    private Map<DocumentStatusEnum, Integer> documentProcessed = new LinkedHashMap<>();

    private DocumentDailyStatEntity() {
        documentProcessed.put(DocumentStatusEnum.PENDING, 0);
        documentProcessed.put(DocumentStatusEnum.PROCESSED, 0);
        documentProcessed.put(DocumentStatusEnum.REPROCESS, 0);
        documentProcessed.put(DocumentStatusEnum.REJECT, 0);
        documentProcessed.put(DocumentStatusEnum.DUPLICATE, 0);
    }

    public DocumentDailyStatEntity(Date date) {
        this();

        Assert.notNull(date);
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Map<DocumentStatusEnum, Integer> getDocumentProcessed() {
        return documentProcessed;
    }
}
