package com.receiptofi.loader.scheduledtasks;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


import com.receiptofi.service.ReceiptService;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FileSystemProcessTest {

    private FileSystemProcess fileSystemProcess;

    @Mock private ReceiptService receiptService;
    @Rule public TemporaryFolder folder = new TemporaryFolder();
    private File createdFile;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        createdFile = folder.newFile("file.xml");
        fileSystemProcess = new FileSystemProcess(createdFile.getParent(), 0, "ON", receiptService);
    }

    @Test
    public void whenRemoveExpiredExcelFilesIsTurnedOn() {
        fileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, atMost(1)).removeExpensofiFilenameReference(any(String.class));
    }

    @Test
    public void whenRemoveExpiredExcelFilesIsTurnedOff() {
        fileSystemProcess = new FileSystemProcess(createdFile.getParent(), 0, "OFF", receiptService);
        fileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, never()).removeExpensofiFilenameReference(any(String.class));
    }

    @Test
    public void whenRemoveExpiredExcelException() {
        doThrow(Exception.class).when(receiptService).removeExpensofiFilenameReference(anyString());
        fileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, atMost(1)).removeExpensofiFilenameReference(any(String.class));
        assertEquals(0, fileSystemProcess.getCountOfDeletedExcelFiles());
    }

    @Test
    public void removeExpiredExcel() {
        fileSystemProcess.removeExpiredExcel(createdFile.getName());
        assertFalse(createdFile.exists());
    }

    @Test
    public void removeTempFiles() throws Exception {
        fileSystemProcess.removeTempFiles();
        assertEquals(1, fileSystemProcess.getCountOfDeletedXmlFiles());
    }
}
