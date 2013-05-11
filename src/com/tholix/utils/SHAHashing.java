/**
 *
 */
package com.tholix.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

/**
 * @author hitender
 * @since Dec 22, 2012 11:52:04 PM
 *
 */
public final class SHAHashing {
	private static final Logger log = Logger.getLogger(SHAHashing.class);

	private static MessageDigest md1;
    private static MessageDigest md5;

	static {
		try {
            md1 = MessageDigest.getInstance("SHA-1");
            md5 = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException exce) {

		}
	}

    public static String hashCodeSHA1(String text) {
        return hashCode(text, md1) ;
    }

    public static String hashCodeSHA512(String text) {
        return hashCode(text, md5) ;
    }

	private static String hashCode(String text, MessageDigest md) {
        DateTime time = DateUtil.now();
		if (md != null) {
			md.update(text.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			// convert the byte to hex format method 2
			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
            PerformanceProfiling.log(SHAHashing.class, time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
			return hexString.toString();
		} else {
			log.info("Un-Initialized MessageDigest");
            PerformanceProfiling.log(SHAHashing.class, time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return null;
		}
	}

}
