package com.receiptofi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates Object ID.
 * User: hitender
 * Date: 4/15/13
 * Time: 2:36 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class ValidateObjectID {

    private static Pattern p = Pattern.compile("^[0-9a-fA-F]{24}$");

    private ValidateObjectID() {
    }

    public static boolean isValid(String id) {
        Matcher m = p.matcher(id);
        return m.matches();
    }
}
