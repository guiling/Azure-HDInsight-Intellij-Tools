package com.microsoft.azure.hdinsight.sdk.subscription;

/**
 * Created by joezhang on 15-11-25.
 */

public class Tenant {
    private String id;
    private String tenantId;

    public String getId(){
        return id;
    }

    public String getTenantId(){
        return tenantId;
    }

    public Tenant(String id, String tenantId){
        this.id= id;
        this.tenantId = tenantId;
    }
}
