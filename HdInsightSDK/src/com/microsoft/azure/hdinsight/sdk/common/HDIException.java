package com.microsoft.azure.hdinsight.sdk.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by joezhang on 15-11-27.
 */

public class HDIException extends Exception {
    private String mErrorLog;

    public HDIException(String message) {
        super(message);

        mErrorLog = "";
    }

    public HDIException(String message, String errorLog) {
        super(message);

        mErrorLog = errorLog;
    }

    public HDIException(String message, Throwable throwable) {
        super(message, throwable);

        if (throwable instanceof HDIException) {
            mErrorLog = ((HDIException) throwable).getErrorLog();
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);

            throwable.printStackTrace(writer);
            writer.flush();

            mErrorLog = sw.toString();
        }
    }

    public String getErrorLog() {
        return mErrorLog;
    }
}