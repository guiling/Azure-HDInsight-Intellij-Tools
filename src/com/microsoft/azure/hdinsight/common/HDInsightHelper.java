package com.microsoft.azure.hdinsight.common;

import com.microsoft.azure.hdinsight.sdk.cluster.ClusterManager;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterType;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.AggregatedException;
import com.microsoft.azure.hdinsight.sdk.common.AuthenticationErrorHandler;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import com.microsoft.azure.hdinsight.serverexplore.AzureManager;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;

import java.util.List;

/**
 * Created by joezhang on 15-12-23.
 */
public class HDInsightHelper {

    private HDInsightHelper(){}

    private static HDInsightHelper instance = null;

    public static HDInsightHelper getInstance(){
        if(instance == null){
            synchronized (HDInsightHelper.class){
                if(instance == null){
                    instance = new HDInsightHelper();
                }
            }
        }

        return instance;
    }

    private List<IClusterDetail> cachedClusterDetails;

    public List<IClusterDetail> getcachedClusterDetails(){
        return cachedClusterDetails;
    }

    public synchronized List<IClusterDetail> getClusterDetails() throws HDExploreException{
        List<IClusterDetail> clusterDetailList = null;
        List<Subscription> subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
        try {
            clusterDetailList = ClusterManager.getInstance().getHDInsightClusersWithSpecificType(subscriptionList, ClusterType.spark);
        } catch (AggregatedException aggregateException) {
            if (dealWithAggregatedException(aggregateException)) {
                subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
                try {
                    clusterDetailList = ClusterManager.getInstance().getHDInsightClusersWithSpecificType(subscriptionList, ClusterType.spark);
                } catch (Exception exception) {
                    DefaultLoader.getUIHelper().showError("Failed to list HDInsight cluster", "List HDInsight Cluster");
                }
            }
        }

        cachedClusterDetails = clusterDetailList;
        return clusterDetailList;
    }

    private boolean dealWithAggregatedException(AggregatedException aggregateException) {
        boolean isReAuth = false;
        for(Exception exception : aggregateException.getExceptionList()){
            if(exception instanceof HDIException) {
                if(((HDIException)exception).getErrorCode() == AuthenticationErrorHandler.AUTH_ERROR_CODE){
                    try {
                        AzureManager apiManager = AzureManagerImpl.getManager();
                        apiManager.authenticate();
                        isReAuth = true;
                    } catch (HDExploreException e1) {
                        DefaultLoader.getUIHelper().showException(
                                "An error occurred while attempting to sign in to your account.", e1,
                                "Error Signing In", false, true);
                    } finally {
                        break;
                    }
                }
            }
        }

        return isReAuth;
    }
}
