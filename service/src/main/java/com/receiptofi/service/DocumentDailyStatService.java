package com.receiptofi.service;

import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.domain.value.DocumentGrouped;
import com.receiptofi.repository.DocumentDailyStatManager;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.utils.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 11/20/14 3:10 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class DocumentDailyStatService {

    private DocumentManager documentManager;
    private DocumentDailyStatManager documentDailyStatManager;

    @Autowired
    public DocumentDailyStatService(DocumentManager documentManager, DocumentDailyStatManager documentDailyStatManager) {
        this.documentManager = documentManager;
        this.documentDailyStatManager = documentDailyStatManager;
    }

    public DocumentDailyStatEntity getLastEntry() {
        return documentDailyStatManager.getLastEntry();
    }

    public List<DocumentDailyStatEntity> getDailyStatForDays(int days) {
        return documentDailyStatManager.getStatsForDays(days);
    }

    /**
     * Compute stats from this date onwards.
     * @param since
     * @return
     */
    public Map<Date, DocumentDailyStatEntity> computeDailyStats(Date since) {
        Map<Date, DocumentDailyStatEntity> daily = new HashMap<>();
        Iterator<DocumentGrouped> documents = documentManager.getHistoricalStat(since);
        while (documents.hasNext()) {
            DocumentGrouped documentGrouped = documents.next();
            Date day = DateUtil.midnight(documentGrouped.getDay());
            if (daily.containsKey(day)) {
                /* DocumentDailyStatEntity is always populated with all Enum */
                DocumentDailyStatEntity dailyStat = daily.get(day);
                int newCount = dailyStat.getDocumentProcessed().get(documentGrouped.getDocumentStatusEnum()) + 1;
                dailyStat.getDocumentProcessed().put(documentGrouped.getDocumentStatusEnum(), newCount);
                daily.put(day, dailyStat);
            } else {
                DocumentDailyStatEntity dailyStat = new DocumentDailyStatEntity(day);
                dailyStat.getDocumentProcessed().put(documentGrouped.getDocumentStatusEnum(), 1);
                daily.put(day, dailyStat);
            }
        }
        return daily;
    }

    public void save(DocumentDailyStatEntity documentDailyStat) {
        documentDailyStatManager.save(documentDailyStat);
    }
}
