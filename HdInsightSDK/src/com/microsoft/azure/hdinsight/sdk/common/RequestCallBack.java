package com.microsoft.azure.hdinsight.sdk.common;

/**
 * Created by joezhang on 15-11-30.
 */
public interface RequestCallBack<T> {
    void execute(T t);
}
