package com.receiptofi.utils;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 9/1/15 9:44 PM
 */
public final class CommonUtil {

    private CommonUtil() {
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Strip all the characters other than number.
     *
     * @param phone
     * @return
     */
    public static String phoneCleanup(String phone) {
        if (StringUtils.isNotEmpty(phone)) {
            return phone.replaceAll("[^0-9]", "");
        }
        return phone;
    }

    public static String phoneFormatter(String phone, String countryShortName) {
        return Formatter.phone(phone, countryShortName);
    }

    public static List<ObjectId> convertStringArrayToObjectIdArray(List<String> ids) {
        List<ObjectId> objectIds = new ArrayList<>();
        if (null != ids) {
            objectIds.addAll(ids.stream().map(ObjectId::new).collect(Collectors.toList()));
        }
        return objectIds;
    }
}
