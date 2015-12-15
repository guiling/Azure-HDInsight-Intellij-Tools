package com.microsoft.azure.hdinsight.sdk.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guizha on 12/11/2015.
 */
public class AggregatedException extends Exception{
    private List<Exception> exceptionList = new ArrayList<>();
    public AggregatedException(List<Exception> exceptionList){
        this.exceptionList = exceptionList;
    }

    public List<Exception> getExceptionList(){
        return exceptionList;
    }
}
