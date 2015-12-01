package com.microsoft.azure.hdinsight.sdk.cluster;

/**
 * Created by joezhang on 15-11-26.
 */
public class ClusterRawInfo {
    private String id;
    private String name;
    private String type;
    private String location;
    private String etag;
    private ClusterProperties properties;

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public String getLocation(){
        return location;
    }

    public String getEtag(){
        return etag;
    }

    public ClusterProperties getProperties() {
        return properties;
    }
}
