package com.microsoft.azure.hdinsight.spark.common;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;

/**
 * Created by guizha on 8/27/2015.
 */
public class SparkInteractiveSessions {

    // Singleton Instance
    private static SparkInteractiveSessions instance = null;

    public static SparkInteractiveSessions getInstance() {
        if(instance == null){
            synchronized (SparkInteractiveSessions.class){
                if(instance == null){
                    instance = new SparkInteractiveSessions();
                }
            }
        }

        return instance;
    }

    private CredentialsProvider credentialsProvider =  new BasicCredentialsProvider();

    /**
     * Set http request credential using username and password
     * @param username : username
     * @param password : password
     */
    public void setCredentialsProvider(String username, String password){
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
    }

    /**
     * get all sessions
     * @param connectUrl : eg http://localhost:8998/sessions
     * @return response result
     * @throws IOException
     */
    public String getAllSessions(String connectUrl) throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpGet httpGet = new HttpGet(connectUrl);
        httpGet.addHeader("Content-Type", "application/json");
        try(CloseableHttpResponse response = httpclient.execute(httpGet)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * create new session
     * @param connectUrl : eg http://localhost:8998/sessions
     * @param kind : spark or pyspark or sparkr
     * @return response result
     * @throws IOException
     */
    public String createNewSession(String connectUrl, String kind) throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpPost httpPost = new HttpPost(connectUrl);
        httpPost.addHeader("Content-Type", "application/json");
        String jsonString = "{\"kind\" : \"" + kind + "\"}";
        StringEntity postingString =new StringEntity(jsonString);
        httpPost.setEntity(postingString);

        try(CloseableHttpResponse response = httpclient.execute(httpPost)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * @param connectUrl : eg http://localhost:8998/sessions
     * @param sessionId : session Id
     * @return response result
     * @throws IOException
     */
    public String getSessionState(String connectUrl, String sessionId) throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpGet httpGet = new HttpGet(connectUrl + "/" + sessionId);
        httpGet.addHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpGet)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * @param connectUrl : eg http://localhost:8998/sessions
     * @param sessionId  : session Id
     * @return response result
     * @throws IOException
     */
    public String getSessionFullLog(String connectUrl, String sessionId) throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpGet httpGet = new HttpGet(connectUrl + "/" + sessionId + "/log?from=0&size=" + Integer.MAX_VALUE);
        httpGet.addHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpGet)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * Return all the statements in a session.
     * @param connectUrl
     * @param sessionId
     * @return response result
     * @throws IOException
     */
    public String getAllStatementInSession(String connectUrl, String sessionId) throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpGet httpGet = new HttpGet(connectUrl + "/" + sessionId + "/statements");
        httpGet.addHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpGet)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * @param connectUrl : eg http://localhost:8998/sessions
     * @param sessionId : session id
     * @param code scala or python code segment
     * @return response result
     * @throws IOException
     */
    public String executeInSession(String connectUrl, String sessionId, String code)throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpPost httpPost = new HttpPost(connectUrl + "/" + sessionId + "/statements");
        httpPost.addHeader("Content-Type", "application/json");
        String jsonString = "{\"code\" : \"" + code + "\"}";
        StringEntity postingString =new StringEntity(jsonString);
        httpPost.setEntity(postingString);

        try(CloseableHttpResponse response = httpclient.execute(httpPost)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * get job execution status
     * @param connectUrl eg http://localhost:8998/sessions
     * @param sessionId session id
     * @param statementId statement id
     * @return response result
     * @throws IOException
     */
    public String getExecutionState(String connectUrl, String sessionId, String statementId)throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpGet httpGet = new HttpGet(connectUrl + "/" + sessionId + "/statements/" + statementId);
        httpGet.addHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpGet)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }

    /**
     * kill session
     * @param connectUrl : eg http://localhost:8998/sessions
     * @param sessionId : session id
     * @return response result
     * @throws IOException
     */
    public String killSession(String connectUrl, String sessionId) throws IOException{
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpDelete httpDelete = new HttpDelete(connectUrl +  "/" + sessionId);
        httpDelete.addHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpDelete)) {
            return SparkHelper.getResultFromHttpResponse(response);
        }
    }
}
