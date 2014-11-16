package com.receiptofi.utils;

import org.apache.commons.lang3.RandomStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

/**
 * User: hitender
 * Date: 9/21/13 10:16 PM
 */
public class CreateTempFile {
    public static final String TEMP_FILE_START_WITH = "Receiptofi";
    public static final String DOT = ".";
    public static final String DASH = "-";

    private static final Logger LOG = LoggerFactory.getLogger(CreateTempFile.class);

    public static File file(String name, String ext) throws IOException {
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
    private String createRandomFilename(final FileExtensionTypeEnum fileExtension) {
        return addFileExtension(createRandomFilename(), fileExtension);
    }

    private String addFileExtension(final String filename, FileExtensionTypeEnum fileExtension) {
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

    private enum FileExtensionTypeEnum {
        XLS, TXT, JPEG, JPG, PNG, PDF
    }
}
