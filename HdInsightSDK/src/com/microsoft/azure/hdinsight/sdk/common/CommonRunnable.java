package com.microsoft.azure.hdinsight.sdk.common;

/**
 * Created by joezhang on 15-11-30.
 */
public abstract class CommonRunnable<T,E extends Exception> implements Runnable {
    private final T parameter;

    public CommonRunnable(T parameter) {
        this.parameter = parameter;
    }

    public abstract void runSpecificParameter(T parameter) throws E;

    public abstract void exceptionHandle(Exception e);

    public void run(){
        try {
            runSpecificParameter(this.parameter);
        }
        catch (Exception e) {
            exceptionHandle(e);
        }
    }
}
