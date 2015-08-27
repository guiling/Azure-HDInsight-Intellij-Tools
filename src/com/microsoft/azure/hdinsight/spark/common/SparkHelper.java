package com.microsoft.azure.hdinsight.spark.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guizha on 8/20/2015.
 */
public class SparkHelper {

    public static String getResultFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer result = new StringBuffer();
        try {
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        finally {
            inputStream.close();
            reader.close();
        }

        return result.toString();
    }
}
