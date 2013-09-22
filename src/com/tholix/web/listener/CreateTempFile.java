package com.tholix.web.listener;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * User: hitender
 * Date: 9/21/13 10:16 PM
 */
public final class CreateTempFile {
    private static final Logger log = Logger.getLogger(CreateTempFile.class);

    public static File file(String name, String ext) throws IOException {
        try {
            return File.createTempFile("Receiptofi-" + name + "-", (ext.startsWith(".")) ? ext : "." + ext);
        } catch (IOException e) {
            log.error("Error creating temp file: " + e.getLocalizedMessage());
            throw e;
        }
    }
}
