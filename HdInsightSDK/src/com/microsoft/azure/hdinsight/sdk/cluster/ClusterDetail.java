package com.microsoft.azure.hdinsight.sdk.cluster;

import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joezhang on 15-11-27.
 */
public class ClusterDetail implements IClusterDetail {
    private final String WorkerNodeName = "workernode";
    private final String DefaultFS = "fs.defaultFS";
    private final String FSDefaultName = "fs.default.name";
    private final String StorageAccountKeyPrefix = "fs.azure.account.key.";
    private final String StorageAccountNamePattern = "^wasb://(.*)@(.*)$";

    private Subscription subscription;
    private ClusterRawInfo clusterRawInfo;

    private int dataNodes;
    private String userName;
    private String passWord;
    private StorageAccount defaultStorageAccount;
    private List<StorageAccount> additionalStorageAccounts;

    private boolean isConfigInfoAvailable = false;

    public ClusterDetail(Subscription paramSubscription, ClusterRawInfo paramClusterRawInfo){
        this.subscription = paramSubscription;
        this.clusterRawInfo = paramClusterRawInfo;
        ExtractInfoFromComputeProfile();
    }

    public boolean isConfigInfoAvailable(){
        return isConfigInfoAvailable;
    }

    public String getName(){
        return this.clusterRawInfo.getName();
    }

    public String getState(){
        ClusterProperties clusterProperties = this.clusterRawInfo.getProperties();
        return clusterProperties == null ? null : clusterProperties.getClusterState();
    }

    public String getLocation(){
        return this.clusterRawInfo.getLocation();
    }

    public String getConnectionUrl(){
        return String.format("https://%s.azurehdinsight.net", getName());
    }

    public String getCreateDate() {
        ClusterProperties clusterProperties = this.clusterRawInfo.getProperties();
        return clusterProperties == null ? null : clusterProperties.getCreatedDate();
    }

    public ClusterType getType(){
        ClusterType type =  ClusterType.valueOf(this.clusterRawInfo.getProperties().getClusterDefinition().getKind().toLowerCase());
        return type == null ? ClusterType.unkown : type;
    }

    public String getVersion(){
        ClusterProperties clusterProperties = this.clusterRawInfo.getProperties();
        return clusterProperties == null ? null : clusterProperties.getClusterVersion();
    }

    public Subscription getSubscription(){
        return subscription;
    }

    public int getDataNodes(){
        return dataNodes;
    }

    public String getHttpUserName() throws HDIException{
        if(userName == null){
            throw new HDIException("username is null, please call getConfigurationInfo first");
        }

        return userName;
    }

    public String getHttpPassword() throws HDIException{
        if(passWord == null){
            throw new HDIException("passWord is null, please call getConfigurationInfo first");
        }

        return passWord;
    }

    public String getOSType(){
        ClusterProperties clusterProperties = this.clusterRawInfo.getProperties();
        return clusterProperties == null ? null : clusterProperties.getOsType();
    }

    public StorageAccount getStorageAccount() throws HDIException{
        if(defaultStorageAccount == null){
            throw new HDIException("default storage account is null, please call getConfigurationInfo first");
        }

        return this.defaultStorageAccount;
    }

    public List<StorageAccount> getAdditionalStorageAccounts(){
        return this.additionalStorageAccounts;
    }

    private void ExtractInfoFromComputeProfile(){
        List<Role> roles = this.clusterRawInfo.getProperties().getComputeProfile().getRoles();
        for(Role role : roles){
            if(role.getName().equals(WorkerNodeName)){
                this.dataNodes = role.getTargetInstanceCount();
                break;
            }
        }
    }

    public void getConfigurationInfo() throws IOException, HDIException{
        IClusterOperation clusterOperation = new ClusterOperationImpl();
        ClusterConfiguration clusterConfiguration =
                clusterOperation.getClusterConfiguration(subscription, clusterRawInfo.getId());
        if(clusterConfiguration != null && clusterConfiguration.getConfigurations() != null){
            Configurations configurations = clusterConfiguration.getConfigurations();
            Gateway gateway = configurations.getGateway();
            if(gateway != null){
                this.userName = gateway.getUsername();
                this.passWord = gateway.getPassword();
            }

            Map<String,String> coresSiteMap = configurations.getCoresite();
            if(coresSiteMap!= null){
                this.defaultStorageAccount = getDefaultStorageAccount(coresSiteMap);
                this.additionalStorageAccounts = getAdditionalStorageAccounts(coresSiteMap);
            }
        }

        isConfigInfoAvailable = true;
    }

    private StorageAccount getDefaultStorageAccount(Map<String, String> coresiteMap) throws HDIException{
        String containerAddress = null;
        if(coresiteMap.containsKey(DefaultFS)){
            containerAddress = coresiteMap.get(DefaultFS);
        }else if(coresiteMap.containsKey(FSDefaultName)){
            containerAddress = coresiteMap.get(FSDefaultName);
        }

        if(containerAddress == null){
            throw new HDIException("Failed to get default storage account");
        }

        String storageAccountName = getStorageAccountName(containerAddress);
        if(storageAccountName == null){
            throw new HDIException("Failed to get default storage account name");
        }

        String defaultContainerName = getDefaultContainerName(containerAddress);

        String keyNameOfDefaultStorageAccountKey = StorageAccountKeyPrefix + storageAccountName;
        String storageAccountKey = null;
        if(coresiteMap.containsKey(keyNameOfDefaultStorageAccountKey)){
            storageAccountKey = coresiteMap.get(keyNameOfDefaultStorageAccountKey);
        }

        if(storageAccountKey == null){
            throw new HDIException("Failed to get default storage account key");
        }
        
        return new StorageAccount(storageAccountName, storageAccountKey,true, defaultContainerName);
    }

    private List<StorageAccount> getAdditionalStorageAccounts(Map<String, String> coresiteMap){
        if(coresiteMap.size() <= 2)
        {
            return null;
        }

        List<StorageAccount> storageAccounts = new ArrayList<>();
        for (Map.Entry<String, String> entry : coresiteMap.entrySet()){
            if(entry.getKey().toLowerCase().equals(DefaultFS) || entry.getKey().toLowerCase().equals(FSDefaultName)){
                continue;
            }

            if(entry.getKey().contains(StorageAccountKeyPrefix)){
                StorageAccount account =
                        new StorageAccount(entry.getKey().substring(StorageAccountKeyPrefix.length()), entry.getValue(), false, null);
                storageAccounts.add(account);
            }
        }

        return storageAccounts;
    }

    private String getStorageAccountName(String containerAddress){
        Pattern r = Pattern.compile(StorageAccountNamePattern);
        Matcher m = r.matcher(containerAddress);
        if(m.find())
        {
            return m.group(2);
        }

        return null;
    }

    private String getDefaultContainerName(String containerAddress){
        Pattern r = Pattern.compile(StorageAccountNamePattern);
        Matcher m = r.matcher(containerAddress);
        if(m.find())
        {
            return m.group(1);
        }

        return null;
    }
}
