package com.microsoft.azure.hdinsight.sdk.subscription;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.azure.hdinsight.sdk.cluster.*;
import com.microsoft.azure.hdinsight.sdk.common.AzureAADRequestHelper;
import com.microsoft.azure.hdinsight.sdk.common.CommonConstant;
import com.microsoft.azure.hdinsight.sdk.common.RestServiceManagerBaseImpl;
import com.microsoftopentechnologies.auth.AuthenticationContext;
import com.microsoftopentechnologies.auth.AuthenticationResult;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by joezhang on 15-11-25.
 */
public class SubscriptionManager {

    // Singleton Instance
    private static SubscriptionManager instance = null;

    public static SubscriptionManager getInstance() {
        if(instance == null){
            synchronized (SubscriptionManager.class){
                if(instance == null){
                    instance = new SubscriptionManager();
                }
            }
        }

        return instance;
    }

    private SubscriptionManager(){

    }

    public List<Subscription> getSubscriptions() throws IOException, InterruptedException, ExecutionException{
        List<Subscription> subscriptions = new ArrayList<>();

        AuthenticationContext context = new AuthenticationContext(CommonConstant.authority);
        AuthenticationResult authenticationResult = context.acquireTokenInteractiveAsync(
                        CommonConstant.commonTenantName,
                        CommonConstant.resource, CommonConstant.clientID,
                        CommonConstant.redirectURI,
                        CommonConstant.login_promteValue).get();

        if(authenticationResult != null) {
            List<Tenant> tenantList = getTenant(authenticationResult.getAccessToken());
            for (Tenant tenant : tenantList) {
                    List<Subscription> subscriptionListForTenant = getSubscriptionsForSpecificTenant(tenant.getTenantId());
                    subscriptions.addAll(subscriptionListForTenant);
            }
        }

        ClusterManager.getInstance().getHDInsightClusers(subscriptions, null);
        return subscriptions;
    }

    private List<Tenant> getTenant(String accessToken) throws IOException{
       String response = AzureAADRequestHelper.executeRequest(
               CommonConstant.managementUri,
                "tenants?api-version=2014-04-01-preview",
                null,
                "GET",
                null,
                accessToken,
                new RestServiceManagerBaseImpl(){});

        Type listType = new TypeToken<TenantList>(){}.getType();
        TenantList tenantList = new Gson().fromJson(response, listType);
        return tenantList.getValue();
    }

    public List<Subscription> getSubscriptionsForSpecificTenant(String tenantId) throws IOException, InterruptedException, ExecutionException{
        AuthenticationContext context = new AuthenticationContext(CommonConstant.authority);

        AuthenticationResult authenticationResult =
                 context.acquireTokenInteractiveAsync(tenantId, CommonConstant.resource, CommonConstant.clientID, CommonConstant.redirectURI, CommonConstant.login_promteValue).get();

        if(authenticationResult != null) {
            return getSubscriptionsUsingSpecificTenantToken(authenticationResult.getAccessToken());
        }

        return null;
    }

    private List<Subscription> getSubscriptionsUsingSpecificTenantToken(String accessToken) throws IOException{
        String response = AzureAADRequestHelper.executeRequest(
                CommonConstant.managementUri,
                "subscriptions?api-version=2014-04-01",
                null,
                "GET",
                null,
                accessToken,
                new RestServiceManagerBaseImpl(){});

        Type listType = new TypeToken<SubscriptionList>() {}.getType();
        SubscriptionList subscriptionList =  new Gson().fromJson(response, listType);

        // set access token for each subscription
        for(Subscription subscription : subscriptionList.getValue()){
                subscription.setAccessToken(accessToken);
        }

        return subscriptionList.getValue();
    }

    public static void main(String[] args){
        try {
            SubscriptionManager.getInstance().getSubscriptions();
        }
        catch (IOException e1){}
        catch (InterruptedException e2){}
        catch (ExecutionException e3){}
    }
}
