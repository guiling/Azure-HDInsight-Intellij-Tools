package com.microsoft.azure.hdinsight.serverexplore;

import org.jetbrains.annotations.NotNull;

/**
 * Created by joezhang on 15-12-3.
 */
public interface AADManagerRequestCallback<T> {
    @NotNull
    T execute(@NotNull String accessToken)
            throws Throwable;
}