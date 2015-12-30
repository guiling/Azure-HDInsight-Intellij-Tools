package com.microsoft.azure.hdinsight.sdk.storage;

/**
 * Created by joezhang on 15-11-27.
 */
public class StorageAccount {
    private final String Default_Protocol = "https";
    private String storageName;
    private String storageKey;
    private String protocol;
    private String fullStorageBlobName;

    public StorageAccount(String name, String key){
        this.fullStorageBlobName = name;
        this.storageName = name.replace(".blob.core.windows.net", "");
        this.storageKey = key;
        this.protocol = Default_Protocol;
    }

    public String getStorageName(){
        return storageName;
    }

    public String getStorageKey(){
        return storageKey;
    }

    public String getProtocol(){
        return protocol;
    }

    public String getFullStoragBlobName(){
        return fullStorageBlobName;
    }

    public String getConnection(){
        return String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s",
                new Object[]{this.getProtocol(), this.getStorageName(), this.getStorageKey()});
    }

}