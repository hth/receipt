/**
 * 
 */
package com.tholix.utils;

import org.apache.log4j.Logger;

/**
 * @author hitender 
 * @when Mar 25, 2013 2:36:27 PM
 *
 */
public class HashMe {
	private static final Logger log = Logger.getLogger(HashMe.class);
	public static final int PRIME = 16908799;
	
	public static int code(String key) {
		int hashVal = 0;
		char[] a = key.toCharArray();
		for(char chara : a) {
			hashVal = (127 * hashVal + chara) % PRIME;
		}
		log.debug("Hash value : " + hashVal);
		return hashVal;
	}

}
