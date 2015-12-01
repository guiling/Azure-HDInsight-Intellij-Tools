package com.microsoft.azure.hdinsight.sdk.subscription;

/**
 * Created by joezhang on 15-11-25.
 */
public class SubscriptionPolicies  {
    private String locationPlacementId;
    private String quotaId;

    public String getLocationPlacementId(){
        return locationPlacementId;
    }

    public String getQuotaId(){
        return quotaId;
    }
}
