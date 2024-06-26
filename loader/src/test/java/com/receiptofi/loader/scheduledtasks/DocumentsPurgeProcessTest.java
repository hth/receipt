package com.receiptofi.loader.scheduledtasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentUpdateService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class DocumentsPurgeProcessTest {

    int purgeMaxDocumentsADay = 1;
    int purgeRejectedDocumentAfterDay = 1;
    private DocumentsPurgeProcess documentsPurgeProcess;
    @Mock private DocumentManager documentManager;
    @Mock private DocumentUpdateService documentUpdateService;
    @Mock private CronStatsService cronStatsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenPurgeIsTurnedOn() {
        documentsPurgeProcess = new DocumentsPurgeProcess(
                purgeRejectedDocumentAfterDay,
                purgeMaxDocumentsADay,
                "ON",
                documentManager,
                documentUpdateService,
                cronStatsService);

        when(documentManager.getAllRejected(purgeRejectedDocumentAfterDay)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        documentsPurgeProcess.purgeRejectedDocument();
        verify(documentUpdateService, times(1)).deleteRejectedDocument(any(DocumentEntity.class));
    }

    @Test
    public void whenPurgeIsTurnedOff() {
        documentsPurgeProcess = new DocumentsPurgeProcess(
                purgeRejectedDocumentAfterDay,
                purgeMaxDocumentsADay,
                "OFF",
                documentManager,
                documentUpdateService,
                cronStatsService);

        when(documentManager.getAllRejected(purgeRejectedDocumentAfterDay)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        documentsPurgeProcess.purgeRejectedDocument();
        verify(documentUpdateService, never()).deleteRejectedDocument(any(DocumentEntity.class));
    }

    @Test
    public void purgeAll() {
        documentsPurgeProcess = new DocumentsPurgeProcess(
                purgeRejectedDocumentAfterDay,
                -purgeMaxDocumentsADay,
                "ON",
                documentManager,
                documentUpdateService,
                cronStatsService);

        when(documentManager.getAllRejected(purgeRejectedDocumentAfterDay)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        documentsPurgeProcess.purgeRejectedDocument();
        verify(documentUpdateService, times(2)).deleteRejectedDocument(any(DocumentEntity.class));
    }

    @Test
    public void purgeEmpty() {
        documentsPurgeProcess = new DocumentsPurgeProcess(
                purgeRejectedDocumentAfterDay,
                -purgeMaxDocumentsADay,
                "ON",
                documentManager,
                documentUpdateService,
                cronStatsService);

        when(documentManager.getAllRejected(purgeRejectedDocumentAfterDay)).thenReturn(new ArrayList<>());
        documentsPurgeProcess.purgeRejectedDocument();
        verify(documentUpdateService, never()).deleteRejectedDocument(any(DocumentEntity.class));
    }

    @Test
    public void purgeException() {
        documentsPurgeProcess = new DocumentsPurgeProcess(
                purgeRejectedDocumentAfterDay,
                -purgeMaxDocumentsADay,
                "ON",
                documentManager,
                documentUpdateService,
                cronStatsService);

        when(documentManager.getAllRejected(purgeRejectedDocumentAfterDay)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        doThrow(Exception.class).when(documentUpdateService).deleteRejectedDocument(anyObject());
        documentsPurgeProcess.purgeRejectedDocument();
        assertTrue(documentsPurgeProcess.getCronStats().getStats().containsKey("failure"));
        assertEquals("1", documentsPurgeProcess.getCronStats().getStats().get("failure"));
    }
}
