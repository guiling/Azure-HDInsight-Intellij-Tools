package com.microsoft.azure.hdinsight.serverexplore;

/**
 * Created by joezhang on 15-12-3.
 */
public interface AzureManagerRequestCallback<T> {
    T execute() throws Throwable;
}
