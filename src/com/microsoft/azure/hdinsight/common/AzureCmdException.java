package com.microsoft.azure.hdinsight.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by joezhang on 15-12-2.
 */
public class AzureCmdException extends Exception {
    private String mErrorLog;

    public AzureCmdException(String message) {
        super(message);

        mErrorLog = "";
    }

    public AzureCmdException(String message, String errorLog) {
        super(message);

        mErrorLog = errorLog;
    }

    public AzureCmdException(String message, Throwable throwable) {
        super(message, throwable);

        if (throwable instanceof AzureCmdException) {
            mErrorLog = ((AzureCmdException) throwable).getErrorLog();
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