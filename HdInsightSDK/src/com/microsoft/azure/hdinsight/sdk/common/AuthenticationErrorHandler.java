package com.microsoft.azure.hdinsight.sdk.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by guizha on 12/7/2015.
 */
public abstract class AuthenticationErrorHandler<T> {
    public final static int AUTH_ERROR_CODE = 401;

    public abstract T execute(String response);

    // add more logic for authentication error
    public T run(String response)throws HDIException{
        T result = execute(response);
        if (result == null) {
               Type errorType = new TypeToken<AuthenticationError>() {}.getType();
               AuthenticationError authenticationError = new Gson().fromJson(response, errorType);
                if(authenticationError != null && authenticationError.getErrorDetail() != null) {
                    throw new HDIException(authenticationError.getError(), AUTH_ERROR_CODE);
                }
           }

        return result;
    }
}
