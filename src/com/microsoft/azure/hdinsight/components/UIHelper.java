package com.microsoft.azure.hdinsight.components;

import org.jetbrains.annotations.NotNull;

/**
 * Created by joezhang on 15-12-2.
 */
public interface UIHelper {
    void showException(@NotNull String message,
                       Throwable ex,
                       @NotNull String title,
                       boolean appendEx,
                       boolean suggestDetail);

    void showError(@NotNull String message, @NotNull String title);
}