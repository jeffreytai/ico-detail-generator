package com.crypto.util;

import java.util.Map;

public class StringUtils {

    /**
     * Alias for an empty string
     */
    public static final String EMPTY_STRING = "";

    /**
     * Remove all non-alphabetical characters from the string,
     * and change the string to all lower-case characters.
     * @param value
     * @return
     */
    public static String sanitizeAlphanumericStringValue(String value) {
        return value.replaceAll("[^0-9a-zA-Z]", "");
    }

    /**
     * Retrieve a value from the map based on the key, but if it doesn't exist, return an empty string.
     * @param key
     * @param map
     * @return
     */
    public static String extractValueFromMap(String key, Map<String, String> map) {
        return map.containsKey(key) ? map.get(key) : StringUtils.EMPTY_STRING;
    }

    /**
     * Returns true if the two strings are equal if casing is ignored
     * @param value1
     * @param value2
     * @return
     */
    public static boolean areStringsEqualIgnoreCase(String value1, String value2) {
        return value1.toLowerCase().equals(value2.toLowerCase());
    }

    /**
     * Check if a string is in the format of a url
     * @param value
     * @return
     */
    public static boolean isUrlFormat(String value) {
        String http = "http";
        return value.toLowerCase().indexOf(http) == 0;
    }
}
