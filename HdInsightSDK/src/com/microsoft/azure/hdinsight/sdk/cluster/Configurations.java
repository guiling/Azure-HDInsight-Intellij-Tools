package com.microsoft.azure.hdinsight.sdk.cluster;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by joezhang on 15-11-26.
 */
public class Configurations  {

    @SerializedName("core-site")
    private Map<String, String> coresite;
    private ClusterIdentity clusterIdentity;
    private Gateway gateway;

    public Map<String,String> getCoresite(){
        return coresite;
    }

    public ClusterIdentity getClusterIdentity(){
        return clusterIdentity;
    }

    public Gateway getGateway(){
        return gateway;
    }
}
