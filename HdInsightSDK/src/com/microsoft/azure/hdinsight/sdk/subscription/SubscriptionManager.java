package com.microsoft.azure.hdinsight.sdk.subscription;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.azure.hdinsight.sdk.common.*;
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

    /**
     * get subscriptions based on interactive AAD auth
     * @return  azure subscriptions
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public List<Subscription> getSubscriptionsInteractively() throws IOException, HDIException, InterruptedException, ExecutionException{
        List<Subscription> subscriptions = new ArrayList<>();

        AuthenticationContext context = new AuthenticationContext(CommonConstant.authority);
        AuthenticationResult authenticationResult = context.acquireTokenInteractiveAsync(
                        CommonConstant.commonTenantName,
                        CommonConstant.resource, CommonConstant.clientID,
                        CommonConstant.redirectURI,
                        CommonConstant.login_promteValue).get();

        if(authenticationResult != null) {
            List<Tenant> tenantList = this.getTenants(authenticationResult.getAccessToken());
            for (Tenant tenant : tenantList) {
                    List<Subscription> subscriptionListForTenant = this.getSubscriptionsForTenantInteractively(tenant.getTenantId());
                    subscriptions.addAll(subscriptionListForTenant);
            }
        }

        return subscriptions;
    }

    private List<Subscription> getSubscriptionsForTenantInteractively(String tenantId) throws IOException, HDIException, InterruptedException, ExecutionException{
        AuthenticationContext context = new AuthenticationContext(CommonConstant.authority);

        AuthenticationResult authenticationResult =
                context.acquireTokenInteractiveAsync(
                        tenantId,
                        CommonConstant.resource,
                        CommonConstant.clientID,
                        CommonConstant.redirectURI,
                        CommonConstant.attemptnone_promoteValue).get();

        if(authenticationResult != null) {
            return getSubscriptions(authenticationResult.getAccessToken());
        }

        return null;
    }

    /**
     * get subscriptions based on token
     * @param accessToken
     * @return  azure subscriptions
     * @throws IOException
     */
    public List<Subscription> getSubscriptions(String accessToken) throws IOException,HDIException{
        String response = AzureAADRequestHelper.executeRequest(
                CommonConstant.managementUri,
                "subscriptions?api-version=2014-04-01",
                null,
                "GET",
                null,
                accessToken,
                new RestServiceManagerBaseImpl(){});
        try {
            Type listType = new TypeToken<SubscriptionList>() {}.getType();
            SubscriptionList subscriptionList = new Gson().fromJson(response, listType);
            // set access token for each subscription
            for(Subscription subscription : subscriptionList.getValue()){
                subscription.setAccessToken(accessToken);
            }

            return subscriptionList.getValue();
        }catch (Exception e){
            Type errorType = new TypeToken<AuthenticationError>() {}.getType();
            AuthenticationError authenticationError = new Gson().fromJson(response, errorType);
            throw new HDIException(authenticationError.getError());
        }
    }

    /**
     * get tenants based on token
     * @param accessToken
     * @return azure tenants
     * @throws IOException
     */
    public List<Tenant> getTenants(String accessToken) throws IOException{
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
}