package com.microsoft.azure.hdinsight.common;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joezhang on 15-12-2.
 */
public class StringHelper {

    private static final String URL_PREFIX = "https://";

    private static final Pattern pattern = Pattern.compile("https://([^/.]\\.)+[^/.]+/?$");

    public static boolean isNullOrWhiteSpace(String str) {
        if (str == null) {
            return true;
        } else {
            int len = str.length();

            for (int i = 0; i < len; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String getClusterNameFromEndPoint(@NotNull String endpoint) {
        if (pattern.matcher(endpoint).find()) {
            return endpoint.split("\\.")[0].substring(8);
        }

        return null;
    }
}