package com.microsoft.azure.hdinsight.sdk.cluster;

/**
 * Created by joezhang on 15-11-26.
 */
public class ConnectivityEndpoint {
    private String name;
    private String protocol;
    private String location;
    private int port;

    public String getName(){
        return name;
    }

    public String getProtocol(){
        return protocol;
    }

    public String getLocation(){
        return location;
    }

    public int getPort(){
        return port;
    }
}
