package com.microsoft.azure.hdinsight.sdk.storage;

/**
 * Created by guizha on 12/11/2015.
 */
public interface BlobItem {
    String getName();

    String getUri();

    String getContainerName();

    String getPath();

    BlobItem.BlobItemType getItemType();

    public enum BlobItemType {
        BlobFile,
        BlobDirectory
    }
}