package com.receiptofi.loader.scheduledtasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.ReceiptService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class FileSystemProcessTest {

    @Rule public TemporaryFolder folder = new TemporaryFolder();
    @Mock private ReceiptService receiptService;
    @Mock private CronStatsService cronStatsService;
    private FileSystemProcess fileSystemProcess;
    private File createdFile;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        createdFile = folder.newFile("file.xml");
        fileSystemProcess = new FileSystemProcess(createdFile.getParent(), 0, "ON", receiptService, cronStatsService);
    }

    @Test
    public void whenRemoveExpiredExcelFilesIsTurnedOn() {
        fileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, times(1)).removeExpensofiFilenameReference(any(String.class));
    }

    @Test
    public void whenRemoveExpiredExcelFilesIsTurnedOff() {
        fileSystemProcess = new FileSystemProcess(createdFile.getParent(), 0, "OFF", receiptService, cronStatsService);
        fileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, never()).removeExpensofiFilenameReference(any(String.class));
    }

    @Test
    public void whenRemoveExpiredExcelException() {
        doThrow(Exception.class).when(receiptService).removeExpensofiFilenameReference(anyString());
        fileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, times(1)).removeExpensofiFilenameReference(any(String.class));
        assertTrue(fileSystemProcess.getCronStats().getStats().containsKey("deletedExcelFiles"));
        assertEquals("0", fileSystemProcess.getCronStats().getStats().get("deletedExcelFiles"));
    }

    @Test
    public void removeExpiredExcel() {
        fileSystemProcess.removeExpiredExcel(createdFile.getName());
        assertFalse(createdFile.exists());
    }

    @Test
    public void removeTempFiles() throws Exception {
        fileSystemProcess.removeTempFiles();
        assertTrue(fileSystemProcess.getCronStats().getStats().containsKey("totalXmlFiles"));
        assertTrue("deleted files successfully", Integer.parseInt(fileSystemProcess.getCronStats().getStats().get("totalXmlFiles")) > 0);
    }
}
