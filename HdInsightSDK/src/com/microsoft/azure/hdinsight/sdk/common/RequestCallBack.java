package com.microsoft.azure.hdinsight.sdk.common;

import java.util.List;

/**
 * Created by joezhang on 15-11-30.
 */
public interface RequestCallBack<T> {
    void execute(List<T> t);
}
