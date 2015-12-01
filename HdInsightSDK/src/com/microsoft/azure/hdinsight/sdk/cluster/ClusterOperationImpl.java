package com.microsoft.azure.hdinsight.sdk.cluster;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.azure.hdinsight.sdk.common.AzureAADRequestHelper;
import com.microsoft.azure.hdinsight.sdk.common.CommonConstant;
import com.microsoft.azure.hdinsight.sdk.common.RestServiceManagerBaseImpl;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by joezhang on 15-11-26.
 */
public class ClusterOperationImpl implements IClusterOperation {

    public List<ClusterRawInfo> listCluster(Subscription subscription) throws IOException{
        String response = AzureAADRequestHelper.executeRequest(
                CommonConstant.managementUri,
                "subscriptions/" + subscription.getSubscriptionId() + "/providers/Microsoft.HDInsight/clusters?api-version=2015-03-01-preview",
                null,
                "GET",
                null,
                subscription.getAccessToken(),
                new RestServiceManagerBaseImpl() {
                });

        Type listType = new TypeToken<ClusterRawInfoList>() {}.getType();
        ClusterRawInfoList clusterRawInfoList =  new Gson().fromJson(response, listType);
        return clusterRawInfoList.getValue();
    }

    public ClusterConfiguration getClusterConfiguration(Subscription subscription, String clusterId) throws IOException{
        String response = AzureAADRequestHelper.executeRequest(
                CommonConstant.managementUri,
                clusterId.replaceAll("/+$", "") + "/configurations?api-version=2015-03-01-preview",
                null,
                "GET",
                null,
                subscription.getAccessToken(),
                new RestServiceManagerBaseImpl() {
                });

        Type listType = new TypeToken<ClusterConfiguration>() {}.getType();
        ClusterConfiguration clusterConfiguration = new Gson().fromJson(response, listType);
        return clusterConfiguration;
    }
}
