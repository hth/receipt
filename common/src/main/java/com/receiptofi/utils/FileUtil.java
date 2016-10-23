package com.receiptofi.utils;

import com.receiptofi.type.FileExtensionTypeEnum;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: hitender
 * Date: 9/21/13 10:16 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class FileUtil {
    public static final String TEMP_FILE_START_WITH = "Receiptofi";
    public static final String DOT = ".";
    public static final String DASH = "-";

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private static final Detector DETECTOR = new DefaultDetector(MimeTypes.getDefaultMimeTypes());
    private static final int FILE_SIZE_IN_MB = 1024 * 1024;

    private FileUtil() {
    }

    public static File createTempFile(String name, String ext) throws IOException {
        try {
            if (name.startsWith(TEMP_FILE_START_WITH)) {
                return File.createTempFile(name + DASH, ext.startsWith(DOT) ? ext : DOT + ext);
            } else {
                return File.createTempFile(
                        TEMP_FILE_START_WITH + DASH + name + DASH,
                        ext.startsWith(DOT) ? ext : DOT + ext);
            }
        } catch (IOException e) {
            LOG.error("Error creating temp file, reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    public static String createRandomFilename() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    /**
     * Avoid unless required on file system to save file with file extension. User can be presented with correct file
     * extension when set with correct content type in the response header.
     *
     * @param fileExtension
     * @return
     */
    private String createRandomFilename(FileExtensionTypeEnum fileExtension) {
        return addFileExtension(createRandomFilename(), fileExtension);
    }

    private String addFileExtension(String filename, FileExtensionTypeEnum fileExtension) {
        String filenameWithExtension = filename;
        if (fileExtension != null) {
            switch (fileExtension) {
                case XLS:
                case TXT:
                case JPEG:
                case JPG:
                case PNG:
                case PDF:
                    filenameWithExtension = filename + DOT + StringUtils.lowerCase(fileExtension.name());
                    break;
                default:
                    LOG.error("reached unsupported file extension={}", fileExtension);
                    throw new RuntimeException("reached unsupported file extension " + fileExtension.name());
            }
        }
        return filenameWithExtension;
    }

    /**
     * From filename with extension, returns extension.
     *
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename) {
        Assert.isTrue(filename.contains("."));

        String extension = FilenameUtils.getExtension(filename);
        if (extension.endsWith("jpeg")) {
            extension = "jpg";
        }
        return extension;
    }

    /**
     * Finds content type of a file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    @SuppressWarnings ("unused")
    public static String detectMimeType(final File file) throws IOException {
        return detectMimeType(FileUtils.openInputStream(file));
    }

    /**
     * Finds content type of a file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String detectMimeType(final InputStream file) throws IOException {
        TikaInputStream tikaIS = null;
        try {
            tikaIS = TikaInputStream.get(file);

            /**
             * You might not want to provide the file's name. If you provide an Excel
             * document with a .xls extension, it will get it correct right away; but
             * if you provide an Excel document with .doc extension, it will guess it
             * to be a Word document.
             */
            final Metadata metadata = new Metadata();
            // metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());

            return DETECTOR.detect(tikaIS, metadata).toString();
        } finally {
            if (tikaIS != null) {
                tikaIS.close();
            }
        }
    }

    public static double fileSizeInMB(long length) {
        return length / FILE_SIZE_IN_MB;
    }

    public static File getExcelFile(String expensofiReportLocation, String filename) {
        return new File(expensofiReportLocation + File.separator + filename);
    }
}
