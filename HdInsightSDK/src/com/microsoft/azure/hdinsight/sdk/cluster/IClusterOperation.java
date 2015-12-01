package com.microsoft.azure.hdinsight.sdk.cluster;

import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;

import java.io.IOException;
import java.util.List;

/**
 * Created by joezhang on 15-11-26.
 */
public interface IClusterOperation {

    List<ClusterRawInfo> listCluster(Subscription subscription) throws IOException;

    ClusterConfiguration getClusterConfiguration(Subscription subscription, String clusterId) throws IOException;
}
