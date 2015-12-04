package com.microsoft.azure.hdinsight.components;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by joezhang on 15-12-2.
 */
public interface IDEHelper {
    void executeOnPooledThread(@NotNull Runnable runnable);

    void invokeLater(@NotNull Runnable runnable);

    void invokeAndWait(@NotNull Runnable runnable);

    void runInBackground(@Nullable Object project, @NotNull String name, boolean canBeCancelled,
                         boolean isIndeterminate, @Nullable String indicatorText,
                         Runnable runnable);
    @Nullable
    String getProperty(@NotNull Object projectObject, @NotNull String name);

    @NotNull
    String getProperty(@NotNull Object projectObject, @NotNull String name, @NotNull String defaultValue);

    void setProperty(@NotNull Object projectObject, @NotNull String name, @NotNull String value);

    void unsetProperty(@NotNull Object projectObject, @NotNull String name);

    boolean isPropertySet(@NotNull Object projectObject, @NotNull String name);

    @Nullable
    String getProperty(@NotNull String name);

    @NotNull
    String getProperty(@NotNull String name, @NotNull String defaultValue);

    void setProperty(@NotNull String name, @NotNull String value);

    void unsetProperty(@NotNull String name);

    boolean isPropertySet(@NotNull String name);

    @Nullable
    String[] getProperties(@NotNull String name);

    void setProperties(@NotNull String name, @NotNull String[] value);
}