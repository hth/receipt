package com.receiptofi.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * Auth keys generator.
 * User: hitender
 * Date: 4/15/13
 * Time: 2:02 AM
 * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
 * You can tweak the "SYMBOLS" if you want to use more characters.
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class RandomString {

    private static final int CHARACTER_SIZE = 32;
    private static final char[] SYMBOLS = new char[36];
    private static final String RID_SHORTEN = "^10+(?!$)";

    static {
        for (int idx = 0; idx < 10; ++idx) {
            SYMBOLS[idx] = (char) ('0' + idx);
        }

        for (int idx = 10; idx < 36; ++idx) {
            SYMBOLS[idx] = (char) ('a' + idx - 10);
        }
    }

    private final Random random = new Random();
    private final char[] buf;

    private RandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        buf = new char[length];
    }

    public static RandomString newInstance() {
        return new RandomString(CHARACTER_SIZE);
    }

    public static RandomString newInstance(int sizeOfString) {
        return new RandomString(sizeOfString);
    }

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = SYMBOLS[random.nextInt(SYMBOLS.length)];
        }
        return new String(buf);
    }

    private static String generateEmailAddress(ScrubbedInput firstName, ScrubbedInput lastName, String rid) {
        String shortenedRid = rid.replaceFirst(RID_SHORTEN, "");

        if (StringUtils.isNotBlank(firstName.getText()) && StringUtils.isNotBlank(lastName.getText())) {
            return StringUtils.lowerCase(firstName.getText()) + "." + StringUtils.lowerCase(lastName.getText()) + "." + shortenedRid;
        } else if (StringUtils.isNotBlank(firstName.getText())) {
            return StringUtils.lowerCase(firstName.getText()) + "." + shortenedRid;
        } else if (StringUtils.isNotBlank(lastName.getText())) {
            return StringUtils.lowerCase(lastName.getText()) + "." + shortenedRid;
        } else {
           return StringUtils.lowerCase(RandomString.newInstance(6).nextString()) + "." + shortenedRid;
        }
    }

    public static String generateEmailAddressWithDomain(ScrubbedInput firstName, ScrubbedInput lastName, String rid) {
        return generateEmailAddress(firstName, lastName, rid) + "@receiptofi.com";
    }
}
