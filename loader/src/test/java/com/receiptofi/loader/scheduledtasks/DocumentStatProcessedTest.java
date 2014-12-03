package com.receiptofi.loader.scheduledtasks;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.service.DocumentDailyStatService;

import org.joda.time.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
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
    public void testComputeDocumentDailyStat_Is_Off() {
        documentStatProcessed = new DocumentStatProcessed(
                DateTime.now().minusDays(1).toString(),
                "OFF",
                documentDailyStatService
        );

        documentStatProcessed.computeDocumentDailyStat();
        verify(documentDailyStatService, never()).getLastEntry();
        verify(documentDailyStatService, never()).save(any(DocumentDailyStatEntity.class));
    }

    @Test
    public void testComputeDocumentDailyStat_When_Null() {
        /** Note: private initialized dailyStatService.save(..) is called too. */
        when(documentDailyStatService.getLastEntry()).thenReturn(null).thenReturn(documentDailyStatEntity);
        when(documentDailyStatEntity.getDate()).thenReturn(DateTime.now().minusDays(3).toDate());

        Date date = new Date();
        Map<Date, DocumentDailyStatEntity> map = new HashMap<>();
        map.put(date, new DocumentDailyStatEntity(date));
        when(documentDailyStatService.computeDailyStats(any(Date.class))).thenReturn(map);

        documentStatProcessed.computeDocumentDailyStat();
        verify(documentDailyStatService, times(2)).getLastEntry();
        /** Note: private initialized is called too. */
        verify(documentDailyStatService, times(2)).save(map.get(any(Date.class)));
    }

    @Test
    public void testComputeDocumentDailyStat() {
        when(documentDailyStatService.getLastEntry()).thenReturn(documentDailyStatEntity);
        when(documentDailyStatEntity.getDate()).thenReturn(DateTime.now().minusDays(3).toDate());

        Date date = new Date();
        Map<Date, DocumentDailyStatEntity> map = new HashMap<>();
        map.put(date, new DocumentDailyStatEntity(date));
        when(documentDailyStatService.computeDailyStats(any(Date.class))).thenReturn(map);

        documentStatProcessed.computeDocumentDailyStat();
        verify(documentDailyStatService, times(1)).getLastEntry();
        verify(documentDailyStatService, times(1)).save(map.get(any(Date.class)));
    }

    @Test
    public void testComputeDocumentDailyStat_Days_Not_Greater_Than_One() {
        /** Note: private initialized dailyStatService.save(..) is called too. */
        when(documentDailyStatService.getLastEntry()).thenReturn(null).thenReturn(documentDailyStatEntity);
        when(documentDailyStatEntity.getDate()).thenReturn(DateTime.now().minusDays(2).toDate());

        Date date = new Date();
        Map<Date, DocumentDailyStatEntity> map = new HashMap<>();
        map.put(date, new DocumentDailyStatEntity(date));
        when(documentDailyStatService.computeDailyStats(any(Date.class))).thenReturn(map);

        documentStatProcessed.computeDocumentDailyStat();
        verify(documentDailyStatService, times(2)).getLastEntry();
        /** Note: private initialized is called too. */
        verify(documentDailyStatService, times(1)).save(map.get(any(Date.class)));
    }
}
