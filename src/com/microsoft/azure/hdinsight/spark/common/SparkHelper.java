package com.microsoft.azure.hdinsight.spark.common;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guizha on 8/20/2015.
 */
public class SparkHelper {

    public static String getResultFromInputStream(InputStream inputStream) throws IOException {
//      change string buffer to string builder for thread-safe
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }

    public static HttpResponse getResultFromHttpResponse(CloseableHttpResponse response) throws  IOException{
        int code = response.getStatusLine().getStatusCode();
        String reason = response.getStatusLine().getReasonPhrase();
        HttpEntity entity = response.getEntity();
        try(InputStream inputStream = entity.getContent()) {
            String response_content = SparkHelper.getResultFromInputStream(inputStream);
            return new HttpResponse(code,response_content,reason);
        }
    }
}
