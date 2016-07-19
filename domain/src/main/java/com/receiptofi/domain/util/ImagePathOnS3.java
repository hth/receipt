package com.receiptofi.domain.util;

import com.receiptofi.domain.FileSystemEntity;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * User: hitender
 * Date: 7/18/16 6:50 PM
 */
public class ImagePathOnS3 {

    private ImagePathOnS3() {
    }

    public static String populateImagePath(Collection<FileSystemEntity> fileSystems) {
        if (null != fileSystems) {
            StringBuilder sb = new StringBuilder();
            for (FileSystemEntity fileSystem : fileSystems) {
                sb.append(fileSystem.getKey()).append(",");
            }
            return StringUtils.chop(sb.toString());
        }
        return null;
    }
}
