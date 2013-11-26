/**
 *
 */
package com.receiptofi.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author hitender
 * @since Mar 25, 2013 2:49:12 PM
 *
 */
public class HashMeTest {
	private static final Logger log = LoggerFactory.getLogger(HashMeTest.class);

	/**
	 * Test method for {@link com.receiptofi.utils.HashMe#code(java.lang.String)}.
	 */
	@Test
	public void testCode() {
		assertEquals(8195390, HashMe.code("ThisIsATest"));
	}

}
