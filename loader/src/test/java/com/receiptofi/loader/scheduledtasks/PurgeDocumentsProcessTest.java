package com.receiptofi.loader.scheduledtasks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.repository.DocumentManager;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PurgeDocumentsProcessTest {

    private PurgeDocumentsProcess purgeDocumentsProcess;
    @Mock private DocumentManager documentManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenPurgeIsTurnedOn() throws Exception {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, 1, "ON", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        purgeDocumentsProcess.purgeRejectedDocument();
        verify(documentManager, atMost(1)).deleteHard(any(DocumentEntity.class));
    }

    @Test
    public void whenPurgeIsTurnedOff() throws Exception {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, 1, "OFF", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        purgeDocumentsProcess.purgeRejectedDocument();
        verify(documentManager, never()).deleteHard(any(DocumentEntity.class));
    }

    @Test
    public void purgeAll() throws Exception {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, -1, "ON", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        purgeDocumentsProcess.purgeRejectedDocument();
        verify(documentManager, atMost(2)).deleteHard(any(DocumentEntity.class));
    }

    @Test(expected = Exception.class)
    public void purgeException() throws Exception {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, -1, "ON", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        doThrow(new Exception()).when(documentManager).deleteHard((DocumentEntity) anyObject());
        purgeDocumentsProcess.purgeRejectedDocument();
    }
}
