package com.receiptofi.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * User: hitender
 * Date: 4/3/16 12:12 AM
 */
public class ScrubbedInputTest {
    @Test
    public void getText() throws Exception {
        ScrubbedInput scrubbedInput = new ScrubbedInput("Golden Circle 25% DR 2 Litre");
        assertEquals("Golden Circle 25% DR 2 Litre", scrubbedInput.getText());
    }

}