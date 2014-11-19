package com.receiptofi.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parse JSON string to map.
 * User: hitender
 * Date: 1/21/14 7:17 PM
 */
public final class ParseJsonStringToMap {

    private ParseJsonStringToMap() {
    }

    public static Map<String, String> jsonStringToMap(String ids) throws IOException {
        return new ObjectMapper().readValue(ids, new TypeReference<HashMap<String, String>>() {
            //Blank
        });
    }
}
