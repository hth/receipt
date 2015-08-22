package com.receiptofi.utils;

import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 8/22/15 9:32 AM
 */
public class Constants {

    public static final Pattern AGE_RANGE = Pattern.compile("^(\\d?[0-9]|[0-9])?(-\\d?[0-9]|[0-9])");

    private Constants() {
    }
}
