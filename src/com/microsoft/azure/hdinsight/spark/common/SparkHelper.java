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

    public static String getResultFromHttpResponse(CloseableHttpResponse response) throws  IOException{
        int code = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();
        try(InputStream inputStream = entity.getContent()) {
            String response_content = SparkHelper.getResultFromInputStream(inputStream);
            if(code == 200 || code == 201) {
                return response_content;
            }
            else {
                return new Gson().toJson(new HttpErrorStatus(code,response_content));
            }
        }
    }
}
