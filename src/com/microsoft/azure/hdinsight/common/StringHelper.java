package com.microsoft.azure.hdinsight.common;

/**
 * Created by joezhang on 15-12-2.
 */
public class StringHelper {
    public static boolean isNullOrWhiteSpace(String str) {
        if(str == null) {
            return true;
        } else {
            int len = str.length();

            for(int i = 0; i < len; ++i) {
                if(!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }
}