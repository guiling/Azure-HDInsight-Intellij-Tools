package com.microsoft.azure.hdinsight.sdk.cluster;

import com.google.gson.annotations.Expose;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;

import java.io.IOException;
import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltian on 12/23/2015.
 */
public class HDInsightAdditionalClusterDetail implements IClusterDetail {

    private String clusterName;
    private String userName;
    private String passWord;

    @Expose
    private StorageAccount defaultStorageAccount;
    @Expose
    private List<StorageAccount> additionalStorageAccounts;

    public HDInsightAdditionalClusterDetail(String clusterName, String userName, String passWord, List<StorageAccount> storageAccounts)
    {
        this.clusterName = clusterName;
        this.userName = userName;
        this.passWord = passWord;
        defaultStorageAccount = storageAccounts.get(0);
        additionalStorageAccounts = storageAccounts.subList(1, storageAccounts.size());
    }

    @Override
    public boolean isConfigInfoAvailable() {
        return false;
    }

    @Override
    public String getName() {
        return clusterName;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public String getConnectionUrl() {
        return String.format("https://%s.azurehingight.net", getName());
    }

    @Override
    public String getCreateDate() {
        return null;
    }

    @Override
    public ClusterType getType() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Subscription getSubscription() {
        return null;
    }

    @Override
    public int getDataNodes() {
        return 0;
    }

    @Override
    public String getHttpUserName() throws HDIException {
        return userName;
    }

    @Override
    public String getHttpPassword() throws HDIException {
        return passWord;
    }

    @Override
    public String getOSType() {
        return null;
    }

    @Override
    public StorageAccount getStorageAccount() throws HDIException {
        return defaultStorageAccount;
    }

    @Override
    public List<StorageAccount> getAdditionalStorageAccounts() {
        return additionalStorageAccounts;
    }

    @Override
    public void getConfigurationInfo() throws IOException, HDIException {

    }
}
