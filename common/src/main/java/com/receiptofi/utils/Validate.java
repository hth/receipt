package com.receiptofi.utils;

import org.springframework.util.Assert;

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
public class Validate {

    private static Pattern objectIdPattern = Pattern.compile("^[0-9a-fA-F]{24}$");
    private static Pattern mailPattern = Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");
    private static Pattern otherMailPattern = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
    private static Pattern namePattern = Pattern.compile("^[\\p{L} .'-]+$");

    private Validate() {
    }

    public static boolean isValidObjectId(String text) {
        Assert.hasText(text);
        Matcher m = objectIdPattern.matcher(text);
        return m.matches();
    }

    public static boolean isValidMail(String text) {
        Assert.hasText(text);
        Matcher m = mailPattern.matcher(text);
        return m.matches();
    }

    public static boolean isValidName(String text) {
        Assert.hasText(text);
        Matcher m = namePattern.matcher(text);
        return m.matches();
    }
}
