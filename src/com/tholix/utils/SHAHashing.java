/**
 * 
 */
package com.tholix.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hitender
 * @when Dec 22, 2012 11:52:04 PM
 * 
 */
public class SHAHashing {
	private static final Log log = LogFactory.getLog(SHAHashing.class);

	private static MessageDigest md;

	static {
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException exce) {

		}
	}

	public static String hashCode(String text) {
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
			return hexString.toString();
		} else {
			log.info("Un-Initailized MessageDigest");
			return null;
		}
	}

}
