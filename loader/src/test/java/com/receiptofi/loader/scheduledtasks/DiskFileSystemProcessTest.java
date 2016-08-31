package com.receiptofi.loader.scheduledtasks;

import static org.junit.Assert.assertEquals;
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
public class DiskFileSystemProcessTest {

    @Rule public TemporaryFolder folder = new TemporaryFolder();
    @Mock private ReceiptService receiptService;
    @Mock private CronStatsService cronStatsService;
    private DiskFileSystemProcess diskFileSystemProcess;
    private File createdFile;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        createdFile = folder.newFile("file.xml");
        diskFileSystemProcess = new DiskFileSystemProcess(createdFile.getParent(), 0, "ON", receiptService, cronStatsService);
    }

    @Test
    public void whenRemoveExpiredExcelFilesIsTurnedOn() {
        diskFileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, times(1)).removeExpensofiFilenameReference(any(String.class));
    }

    @Test
    public void whenRemoveExpiredExcelFilesIsTurnedOff() {
        diskFileSystemProcess = new DiskFileSystemProcess(createdFile.getParent(), 0, "OFF", receiptService, cronStatsService);
        diskFileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, never()).removeExpensofiFilenameReference(any(String.class));
    }

    @Test
    public void whenRemoveExpiredExcelException() {
        doThrow(Exception.class).when(receiptService).removeExpensofiFilenameReference(anyString());
        diskFileSystemProcess.removeExpiredExcelFiles();
        verify(receiptService, times(1)).removeExpensofiFilenameReference(any(String.class));
        assertTrue(diskFileSystemProcess.getCronStats().getStats().containsKey("deletedExcelFiles"));
        assertEquals("0", diskFileSystemProcess.getCronStats().getStats().get("deletedExcelFiles"));
    }

    @Test
    public void removeTempFiles() throws Exception {
        diskFileSystemProcess.removeTempFiles();
        assertTrue(diskFileSystemProcess.getCronStats().getStats().containsKey("totalXmlFiles"));
        assertTrue("deleted files successfully", Integer.parseInt(diskFileSystemProcess.getCronStats().getStats().get("totalXmlFiles")) > 0);
    }
}
