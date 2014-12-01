package com.receiptofi.loader.scheduledtasks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.repository.DocumentManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class PurgeDocumentsProcessTest {

    private PurgeDocumentsProcess purgeDocumentsProcess;
    @Mock private DocumentManager documentManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenPurgeIsTurnedOn() {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, 1, "ON", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        purgeDocumentsProcess.purgeRejectedDocument();
        verify(documentManager, times(1)).deleteHard(any(DocumentEntity.class));
    }

    @Test
    public void whenPurgeIsTurnedOff() {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, 1, "OFF", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        purgeDocumentsProcess.purgeRejectedDocument();
        verify(documentManager, never()).deleteHard(any(DocumentEntity.class));
    }

    @Test
    public void purgeAll() {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, -1, "ON", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        purgeDocumentsProcess.purgeRejectedDocument();
        verify(documentManager, times(2)).deleteHard(any(DocumentEntity.class));
    }

    @Test
    public void purgeException() {
        purgeDocumentsProcess = new PurgeDocumentsProcess(1, -1, "ON", documentManager);
        when(documentManager.getAllRejected(1)).thenReturn(Arrays.asList(new DocumentEntity(), new DocumentEntity()));
        doThrow(Exception.class).when(documentManager).deleteHard((DocumentEntity) anyObject());
        purgeDocumentsProcess.purgeRejectedDocument();
        assertEquals(0, purgeDocumentsProcess.getCount());
    }
}
