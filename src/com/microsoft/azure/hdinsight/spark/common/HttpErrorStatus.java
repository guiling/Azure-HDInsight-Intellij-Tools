package com.microsoft.azure.hdinsight.spark.common;

/**
 * Created by joezhang on 15-10-27.
 */
public class HttpErrorStatus {

    private int code;
    private String message;

    public HttpErrorStatus(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getStatusCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }
}
