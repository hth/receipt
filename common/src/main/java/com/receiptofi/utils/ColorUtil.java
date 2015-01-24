package com.receiptofi.utils;

import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.util.Random;

/**
 * User: hitender
 * Date: 1/23/15 11:45 PM
 */
public class ColorUtil {
    private static final Random RANDOM = new Random();
    private static final float LUMINANCE = 0.9f;
    private static final String POUND_SYMBOL = "#";

    private ColorUtil() {
    }

    public static String getRandom() {
        float hue = RANDOM.nextFloat();

        // Saturation between 0.1 and 0.3
        float saturation = (RANDOM.nextInt(2000) + 1000) / 10000f;
        Color color = new Color(hue, saturation, LUMINANCE);
        return POUND_SYMBOL + WordUtils.capitalizeFully(Integer.toHexString(color.getRGB() & 0x00ffffff));
    }
}
