package com.microsoft.azure.hdinsight.sdk.cluster;

/**
 * Created by joezhang on 15-11-27.
 */
public class StorageAccount {
    private String storageName;
    private String storageKey;

    public StorageAccount(String name, String key){
        this.storageName = name;
        this.storageKey = key;
    }

    public String getStorageName(){
        return storageName;
    }

    public String getStorageKey(){
        return storageKey;
    }
}