package com.microsoft.azure.hdinsight.sdk.cluster;

import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import java.util.List;

/**
 * Created by joezhang on 15-11-27.
 */
public interface IClusterDetail {

    String getName();

    String getState();

    String getLocation();

    String getConnectionUrl();

    String getCreateDate();

    ClusterType getType();

    String getVersion();

    Subscription getSubscription();

    int getDataNodes();

    String getHttpUserName() throws HDIException;

    String getHttpPassword() throws HDIException;

    String getOSType();

    StorageAccount getStorageAccount() throws HDIException;

    List<StorageAccount> getAdditionalStorageAccounts();
}