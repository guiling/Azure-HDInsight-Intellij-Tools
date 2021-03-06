package com.microsoft.azure.hdinsight.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterManager;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterType;
import com.microsoft.azure.hdinsight.sdk.cluster.HDInsightAdditionalClusterDetail;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.AggregatedException;
import com.microsoft.azure.hdinsight.sdk.common.AuthenticationErrorHandler;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import com.microsoft.azure.hdinsight.serverexplore.AzureManager;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.serverexplore.ServerExplorerToolWindowFactory;
import com.microsoft.azure.hdinsight.serverexplore.hdinsightnode.HDInsightRootModule;
import com.microsoft.azure.hdinsight.spark.UI.SparkSubmissionToolWindowFactory;

import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<String, ToolWindowFactory> toolWindowFactoryCollection = new HashMap<>();
    private List<IClusterDetail> cachedClusterDetails;

    private boolean isListClusterSuccess = false;
    public boolean isListClusterSuccess(){
        return isListClusterSuccess;
    }

    public List<IClusterDetail> getCachedClusterDetails(){
        return cachedClusterDetails;
    }

    public synchronized void registerToolWindowFactory(String toolWindowFactoryId, ToolWindowFactory toolWindowFactory) {
        toolWindowFactoryCollection.put(toolWindowFactoryId, toolWindowFactory);
    }

    public ToolWindowFactory getToolWindowFactory(String toolWindowFactoryId) {
        return toolWindowFactoryCollection.get(toolWindowFactoryId);
    }

    public HDInsightRootModule getServerExplorerRootModule() {
        ToolWindowFactory toolWindowFactory = getToolWindowFactory(ServerExplorerToolWindowFactory.TOOLWINDOW_FACTORY_ID);

        if (toolWindowFactory != null) {
            return ((ServerExplorerToolWindowFactory) toolWindowFactory).getAzureServiceModule();

        }

        return null;
    }


    public synchronized List<IClusterDetail> getClusterDetails(){
        List<IClusterDetail> clusterDetailList = null;
        isListClusterSuccess = false;
        List<Subscription> subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
        try {
            clusterDetailList = ClusterManager.getInstance().getHDInsightClusersWithSpecificType(subscriptionList, ClusterType.spark);
            isListClusterSuccess = true;
        } catch (AggregatedException aggregateException) {
            if (dealWithAggregatedException(aggregateException)) {
                subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
                try {
                    clusterDetailList = ClusterManager.getInstance().getHDInsightClusersWithSpecificType(subscriptionList, ClusterType.spark);
                    isListClusterSuccess = true;
                } catch (Exception exception) {
                    DefaultLoader.getUIHelper().showException("Failed to list HDInsight cluster", exception, "List HDInsight Cluster", false, true);
                }
            }
        }

        List<IClusterDetail> additionalClusterDetailList = getAdditionalClusters();
        if(clusterDetailList != null) {
            if (additionalClusterDetailList != null) {
                clusterDetailList.addAll(additionalClusterDetailList);
            }
        }else{
            clusterDetailList = additionalClusterDetailList;
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

    private List<IClusterDetail> getAdditionalClusters() {
        Gson gson = new Gson();
        String json = DefaultLoader.getIdeHelper().getProperty(CommonConst.HDINSIGHT_ADDITIONAL_CLUSTERS);
        List<IClusterDetail> hdiLocalClusters = new ArrayList<>();

        if (!StringHelper.isNullOrWhiteSpace(json)) {
            try {
                hdiLocalClusters = gson.fromJson(json, new TypeToken<ArrayList<HDInsightAdditionalClusterDetail>>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                // do nothing if we cannot get it from json
            }
        }

        return hdiLocalClusters;
    }

    public SparkSubmissionToolWindowFactory getSparkSubmissionToolWindowFactory(){
        return (SparkSubmissionToolWindowFactory)getToolWindowFactory(SparkSubmissionToolWindowFactory.SPARK_SUBMISSION_WINDOW);
    }
}

