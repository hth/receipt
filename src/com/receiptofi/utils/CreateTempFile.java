package com.receiptofi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;


/**
 * User: hitender
 * Date: 9/21/13 10:16 PM
 */
public final class CreateTempFile {
    private static final Logger log = LoggerFactory.getLogger(CreateTempFile.class);

    public enum FileTypeEnum {
        XLS, TXT, JPEG, JPG, PNG
    }

    public static final String TEMP_FILE_START_WITH = "Receiptofi";

    public static File file(String name, String ext) throws IOException {
        try {
            if(name.startsWith(TEMP_FILE_START_WITH)) {
                return File.createTempFile(name + "-", (ext.startsWith(".")) ? ext : "." + ext);
            } else {
                return File.createTempFile(TEMP_FILE_START_WITH + "-" + name + "-", (ext.startsWith(".")) ? ext : "." + ext);
            }
        } catch (IOException e) {
            log.error("Error creating temp file: " + e.getLocalizedMessage());
            throw e;
        }
    }

    public static String createRandomFilename(FileTypeEnum fileTypeEnum) {
        String filename = RandomStringUtils.randomAlphanumeric(16);
        switch(fileTypeEnum) {
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
            default:
                return filename;
        }
    }
}
