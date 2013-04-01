/**
 * 
 */
package com.tholix.utils;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hitender 
 * @when Mar 25, 2013 2:49:12 PM
 *
 */
public class HashMeTest {
	private static final Logger log = Logger.getLogger(HashMeTest.class);
	
	/**
	 * Test method for {@link com.tholix.utils.HashMe#code(java.lang.String)}.
	 */
	@Test
	public void testCode() {		
		assertEquals(8195390, HashMe.code("ThisIsATest"));
	}

}
