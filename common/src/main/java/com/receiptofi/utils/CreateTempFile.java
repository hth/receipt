package com.receiptofi.utils;

import org.apache.commons.lang3.RandomStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


/**
 * User: hitender
 * Date: 9/21/13 10:16 PM
 */
public final class CreateTempFile {
    public static final String TEMP_FILE_START_WITH = "Receiptofi";
    private static final Logger LOG = LoggerFactory.getLogger(CreateTempFile.class);

    public static File file(String name, String ext) throws IOException {
        try {
            if (name.startsWith(TEMP_FILE_START_WITH)) {
                return File.createTempFile(name + "-", ext.startsWith(".") ? ext : "." + ext);
            } else {
                return File.createTempFile(
                        TEMP_FILE_START_WITH + "-" + name + "-",
                        ext.startsWith(".") ? ext : "." + ext);
            }
        } catch (IOException e) {
            LOG.error("Error creating temp file: " + e.getLocalizedMessage());
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
     * @param fileExtensionTypeEnum
     * @return
     */
    private static String createRandomFilename(FileExtensionTypeEnum fileExtensionTypeEnum) {
        return addFileExtension(createRandomFilename(), fileExtensionTypeEnum);
    }

    private static String addFileExtension(String filename, FileExtensionTypeEnum fileExtensionTypeEnum) {
        if (fileExtensionTypeEnum != null) {
            switch (fileExtensionTypeEnum) {
                case XLS:
                    return filename + ".xls";
                case TXT:
                    return filename + ".txt";
                case JPEG:
                    return filename + ".jpeg";
                case JPG:
                    return filename + ".jpg";
                case PNG:
                    return filename + ".png";
                case PDF:
                    return filename + ".pdf";
            }
        }
        return filename;
    }

    private enum FileExtensionTypeEnum {
        XLS, TXT, JPEG, JPG, PNG, PDF
    }
}
