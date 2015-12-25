package com.microsoft.azure.hdinsight.spark.common;

import com.microsoft.azure.hdinsight.common.StringHelper;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ltian on 12/22/2015.
 */
public class AddNewClusterImpl {

    //for Linux and window cluster, configure file has a different location
    private static final String LinuxClusterConfigureFileUrl = "https://%s.azurehdinsight.net/api/v1/clusters/%s/configurations/service_config_versions?service_name=HDFS&service_config_version=1";
    private static  final String WindowClusterConfirgureFileUrl = "https://%s.azurehdinsight.net/ambari/api/v1/clusters/%s.azurehdinsight.net/configurations?type=core-site&tag=default";


    private static final String STORSGE_KEY_PREFIX = "\"fs.azure.account.key.";

    private static final Pattern PATTERN_DEFAULT_STORAGE = Pattern.compile("\"fs\\.defaultFS\":\"wasb://[^\"]*\"", Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
    private static final Pattern PATTER_STORAGE_KEY = Pattern.compile("\"fs\\.azure\\.account\\.key\\.[^\"]*\":\"[^\"]*=\"", Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);

    private static CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private static CloseableHttpResponse tryGetHttpResponse(CloseableHttpClient httpClient, String url) throws IOException
    {
        HttpGet httpGet = new HttpGet(url);
        return httpClient.execute(httpGet);
    }

    private static List<StorageAccount> getStorageAccountsFromResponseMessage(String responseMessage) throws StorageAccountResolveException
    {

        responseMessage = responseMessage.replace(" ","");
        Matcher matcher = PATTERN_DEFAULT_STORAGE.matcher(responseMessage);
        String defaultStorageName = "";

        try {
            if (matcher.find())
            {
                String str = matcher.group();
                defaultStorageName = str.split("[@.]")[2];
            }
        }
        catch (Exception e)
        {
            throw new StorageAccountResolveException();
        }

        matcher = PATTER_STORAGE_KEY.matcher(responseMessage);
        HashMap<String, String> storageKeysMap = new HashMap<String, String>();

        while(matcher.find())
        {
            String str = matcher.group();
            String[] strs = str.replace("\"","").split(":");
            String storageName = strs[0].split("\\.")[4];

            storageKeysMap.put(storageName, strs[1]);
        }

        if(StringHelper.isNullOrWhiteSpace(defaultStorageName) || !storageKeysMap.containsKey(defaultStorageName))
        {
            throw new StorageAccountResolveException();
        }

        List<StorageAccount> storageAccounts = new ArrayList<StorageAccount>();
        storageAccounts.add(new StorageAccount(defaultStorageName, storageKeysMap.get(defaultStorageName)));

        for(String storageName : storageKeysMap.keySet())
        {
            if(!storageName.equals(defaultStorageName))
            {
                storageAccounts.add(new StorageAccount(storageName, storageKeysMap.get(storageName)));
            }
        }

        return storageAccounts;
    }

    public static List<StorageAccount> getStorageAccounts(String clusterEndpoint, String userName, String passwd)
            throws StorageAccountResolveException, UnAuthorizedException, UnknownHostException, IOException
    {
        String windowsClusterConfigureFileUrl = String.format(WindowClusterConfirgureFileUrl, clusterEndpoint, clusterEndpoint);
        String linuxClusterConfigureFileUrl = String.format(LinuxClusterConfigureFileUrl, clusterEndpoint, clusterEndpoint);

        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName.trim(), passwd));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

        CloseableHttpResponse response = tryGetHttpResponse(httpClient, LinuxClusterConfigureFileUrl);

        String message = "";

        int responseCode = response.getStatusLine().getStatusCode();

        if(responseCode == 404)
        {
            response = tryGetHttpResponse(httpClient, WindowClusterConfirgureFileUrl);
            responseCode = response.getStatusLine().getStatusCode();
        }

        if(responseCode == 200)
        {
            message = SparkHelper.getResultFromHttpResponse(response);
        }
        else if(responseCode == 401 || responseCode == 403)
        {
            throw new UnAuthorizedException();
        }
        else
        {
            throw new IOException();
        }

        if(!StringHelper.isNullOrWhiteSpace(message))
        {
            return getStorageAccountsFromResponseMessage(message);
        }
        else {
            return null;
        }
    }
}
