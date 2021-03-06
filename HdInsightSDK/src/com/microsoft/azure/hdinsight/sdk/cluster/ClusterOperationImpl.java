package com.microsoft.azure.hdinsight.sdk.cluster;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.azure.hdinsight.sdk.common.*;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by joezhang on 15-11-26.
 */
public class ClusterOperationImpl implements IClusterOperation {

    private final String VERSION =  "2015-03-01-preview";

    /**
     * list hdinsight cluster
     * @param subscription
     * @return cluster raw data info
     * @throws IOException
     */
    public List<ClusterRawInfo> listCluster(Subscription subscription) throws IOException, HDIException{
        String response = AzureAADRequestHelper.executeRequest(
                CommonConstant.managementUri,
                String.format("subscriptions/%s/providers/Microsoft.HDInsight/clusters?api-version=%s",subscription.getSubscriptionId(), VERSION),
                null,
                "GET",
                null,
                subscription.getAccessToken(),
                new RestServiceManagerBaseImpl() {
                });

        return new AuthenticationErrorHandler<List<ClusterRawInfo>>(){
            @Override
            public List<ClusterRawInfo> execute(String response){
                Type listType = new TypeToken<ClusterRawInfoList>() {}.getType();
                ClusterRawInfoList clusterRawInfoList =  new Gson().fromJson(response, listType);
                return clusterRawInfoList.getValue();
            }
        }.run(response);
    }

    /**
     * get cluster configuration including http username, password, storage and additional storage account
     * @param subscription
     * @param clusterId
     * @return cluster configuration info
     * @throws IOException
     */
    public ClusterConfiguration getClusterConfiguration(Subscription subscription, String clusterId) throws IOException, HDIException {
        String response = AzureAADRequestHelper.executeRequest(
                CommonConstant.managementUri,
                String.format("%s/configurations?api-version=%s", clusterId.replaceAll("/+$", ""), VERSION),
                null,
                "GET",
                null,
                subscription.getAccessToken(),
                new RestServiceManagerBaseImpl() {
                });

        return new AuthenticationErrorHandler<ClusterConfiguration>() {
            @Override
            public ClusterConfiguration execute(String response) {
                Type listType = new TypeToken<ClusterConfiguration>() {
                }.getType();
                ClusterConfiguration clusterConfiguration = new Gson().fromJson(response, listType);
                return clusterConfiguration;
            }
        }.run(response);
    }
}
