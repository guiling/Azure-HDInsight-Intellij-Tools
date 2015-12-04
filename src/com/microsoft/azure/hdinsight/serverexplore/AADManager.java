package com.microsoft.azure.hdinsight.serverexplore;

import com.microsoft.azure.hdinsight.common.AzureCmdException;
import org.jetbrains.annotations.NotNull;

/**
 * Created by joezhang on 15-12-3.
 */
public interface AADManager {
    @NotNull
    UserInfo authenticate(@NotNull String resource, @NotNull String title)
            throws AzureCmdException;

    void authenticate(@NotNull UserInfo userInfo,
                      @NotNull String resource,
                      @NotNull String title)
            throws AzureCmdException;

    @NotNull
    <T> T request(@NotNull UserInfo userInfo,
                  @NotNull String resource,
                  @NotNull String title,
                  @NotNull AADManagerRequestCallback<T> AADManagerRequestCallback)
            throws AzureCmdException;
}