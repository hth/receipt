package com.receiptofi.web.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;

import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts daily stats data to JSON.
 * User: hitender
 * Date: 11/21/14 11:15 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
public class DocumentDailyStat {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yy");

    @JsonProperty ("documentStats")
    private Map<String, List> documentStats = new LinkedHashMap<>();

    public DocumentDailyStat(List<DocumentDailyStatEntity> dailyStatsEntities) {
        Assert.notNull(dailyStatsEntities);
        init(dailyStatsEntities);
    }

    private void init(List<DocumentDailyStatEntity> dailyStatsEntities) {
        Map<DocumentStatusEnum, Map<String, Integer>> stats = new LinkedHashMap<>();

        for (DocumentDailyStatEntity documentDailyStatEntity : dailyStatsEntities) {
            for (DocumentStatusEnum key : documentDailyStatEntity.getDocumentProcessed().keySet()) {
                if (stats.containsKey(key)) {
                    Map<String, Integer> data = stats.get(key);
                    data.put(
                            SDF.format(documentDailyStatEntity.getDate()),
                            documentDailyStatEntity.getDocumentProcessed().get(key)
                    );
                } else {
                    Map<String, Integer> data = new LinkedHashMap<>();
                    data.put(
                            SDF.format(documentDailyStatEntity.getDate()),
                            documentDailyStatEntity.getDocumentProcessed().get(key)
                    );
                    stats.put(key, data);
                }
            }
        }

        Set<String> dates = new LinkedHashSet<>();
        for (DocumentStatusEnum key : stats.keySet()) {
            Map<String, Integer> stat = stats.get(key);
            if (dates.isEmpty()) {
                dates.addAll(stat.keySet());
            }
            List s = new LinkedList<>(stat.values());
            /** Reverse list in important for charting */
            Collections.reverse(s);
            documentStats.put(key.getDescription(), s);
        }
        List l = new LinkedList<>(dates);
        /** Reverse list in important for charting */
        Collections.reverse(l);
        documentStats.put("dates", l);
    }
}
