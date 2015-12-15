package com.microsoft.azure.hdinsight.sdk.storage;

import java.util.concurrent.Callable;

/**
 * Created by guizha on 12/11/2015.
 */
public abstract class CallableSingleArg<T, TArg> implements Callable<T> {
    @Override
    public final T call() throws Exception {
        return call(null);
    }

    public abstract T call(TArg argument) throws Exception;
}
