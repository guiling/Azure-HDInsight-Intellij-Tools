package com.microsoft.azure.hdinsight.sdk.subscription;
import java.util.List;

/**
 * Created by joezhang on 15-11-25.
 */
public class TenantList {

    private List<Tenant> value;

    public List<Tenant> getValue(){
        return value;
    }

    public TenantList(List<Tenant> tenantList){
        value = tenantList;
    }
}
