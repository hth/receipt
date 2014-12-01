package com.receiptofi.loader.scheduledtasks;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.service.DocumentDailyStatService;

import org.joda.time.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RunWith (MockitoJUnitRunner.class)
public class DocumentStatProcessedTest {

    @Mock private DocumentDailyStatService documentDailyStatService;
    @Mock private DocumentDailyStatEntity documentDailyStatEntity;

    private DocumentStatProcessed documentStatProcessed;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        documentStatProcessed = new DocumentStatProcessed(
                DateTime.now().minusDays(1).toString(),
                "ON",
                documentDailyStatService
        );
    }

    @Test
    public void testComputeDocumentDailyStat_Is_Off() throws Exception {
        documentStatProcessed = new DocumentStatProcessed(
                DateTime.now().minusDays(1).toString(),
                "OFF",
                documentDailyStatService
        );

        documentStatProcessed.computeDocumentDailyStat();
        verify(documentDailyStatService, never()).getLastEntry();
    }

    @Test
    public void testComputeDocumentDailyStat() throws Exception {
        when(documentDailyStatService.getLastEntry()).thenReturn(null).thenReturn(documentDailyStatEntity);
        when(documentDailyStatEntity.getDate()).thenReturn(DateTime.now().minusDays(3).toDate());

        Date date = new Date();
        Map<Date, DocumentDailyStatEntity> map = new HashMap<>();
        map.put(date, new DocumentDailyStatEntity(date));
        when(documentDailyStatService.computeDailyStats(any(Date.class))).thenReturn(map);

        documentStatProcessed.computeDocumentDailyStat();
        verify(documentDailyStatService, atMost(2)).getLastEntry();
        verify(documentDailyStatService, atLeastOnce()).save(map.get(any(Date.class)));
    }
}
