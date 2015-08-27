package com.microsoft.azure.hdinsight.spark.common;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guizha on 8/27/2015.
 */
public class SparkBatchSubmission {
    // Singleton Instance
    private static SparkBatchSubmission instance = null;

    public static SparkBatchSubmission getInstance() {
        if(instance == null){
            synchronized (SparkBatchSubmission.class){
                if(instance == null){
                    instance = new SparkBatchSubmission();
                }
            }
        }

        return instance;
    }

    /**
     *
     * @param connectUrl : eg http://localhost:8998/batches
     * @return
     * @throws IOException
     */
    public String getAllBatchesSparkJobs(String connectUrl)throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(connectUrl);
        httpGet.addHeader("Content-Type", "application/json");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            return SparkHelper.getResultFromInputStream(entity.getContent());
        }finally {
            response.close();
        }
    }

    /**
     * @param connectUrl : eg http://localhost:8998/batches
     * @param submissonParameter : spark submission parameter
     * @return
     */
    public String createBatchSparkJob(String connectUrl,SparkSubmissonParameter submissonParameter)throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(connectUrl);
        httpPost.addHeader("Content-Type", "application/json");

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        // TODO : Support more parameters
        nvps.add(new BasicNameValuePair("file", submissonParameter.getFile()));
        nvps.add(new BasicNameValuePair("className", submissonParameter.getClassName()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            HttpEntity entity = response.getEntity();
            return SparkHelper.getResultFromInputStream(entity.getContent());
        }finally {
            response.close();
        }
    }

    /**
     *
     * @param connectUrl : eg http://localhost:8998/batches
     * @param batchId : batch Id
     * @return
     * @throws IOException
     */
    public String getBatchSparkJobStatus(String connectUrl, String batchId)throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(connectUrl + "/" + batchId);
        httpGet.addHeader("Content-Type", "application/json");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            return SparkHelper.getResultFromInputStream(entity.getContent());
        }finally {
            response.close();
        }
    }

    public String killBatchJob(String connectUrl, String batchId)throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(connectUrl +  "/" + batchId);
        httpDelete.addHeader("Content-Type", "application/json");

        CloseableHttpResponse response = httpclient.execute(httpDelete);
        try {
            HttpEntity entity = response.getEntity();
            return SparkHelper.getResultFromInputStream(entity.getContent());
        }finally {
            response.close();
        }
    }

}
