package com.microsoft.azure.hdinsight.serverexplore;

import java.io.IOException;

/**
 * Created by joezhang on 15-12-3.
 */
public interface AzureManagerRequestCallback<T> {
    T execute() throws Throwable;
}
