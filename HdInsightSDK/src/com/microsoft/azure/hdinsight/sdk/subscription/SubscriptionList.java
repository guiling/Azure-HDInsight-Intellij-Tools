package com.microsoft.azure.hdinsight.sdk.subscription;

import java.util.List;

/**
 * Created by joezhang on 15-11-25.
 */

public class SubscriptionList {

    private List<Subscription> value;

    public List<Subscription> getValue(){
        return value;
    }

    public SubscriptionList(List<Subscription> subscriptionList){
        value = subscriptionList;
    }
}
