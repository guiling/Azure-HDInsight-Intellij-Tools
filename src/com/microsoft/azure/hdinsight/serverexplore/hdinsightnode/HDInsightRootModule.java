package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterManager;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterType;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.AggregatedException;
import com.microsoft.azure.hdinsight.sdk.common.AuthenticationErrorHandler;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.serverexplore.AzureManager;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.microsoft.azure.hdinsight.serverexplore.node.RefreshableNode;

import java.util.List;

/**
 * Created by joezhang on 15-12-2.
 */
public class HDInsightRootModule extends RefreshableNode {
    private static final String HDInsight_SERVICE_MODULE_ID = HDInsightRootModule.class.getName();
    private static final String ICON_PATH = PluginUtil.HDInsightIConPath;
    private static final String BASE_MODULE_NAME = "HDInsight";

    private Object project;
    private EventHelper.EventWaitHandle subscriptionsChanged;
    private boolean registeredSubscriptionsChanged;
    private final Object subscriptionsChangedSync = new Object();

    public HDInsightRootModule(Node parent, String iconPath, Object data) {
        super(HDInsight_SERVICE_MODULE_ID, BASE_MODULE_NAME, parent, iconPath);
    }

    public HDInsightRootModule(Object project) {
        this(null, ICON_PATH, null);
        this.project = project;
    }

    @Override
    protected void refreshItems() throws HDExploreException {
        removeAllChildNodes();
        List<Subscription> subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
        List<IClusterDetail> clusterDetailList = null;
        try{
            clusterDetailList = ClusterManager.getInstance().getHDInsightClusersWithSpecificType(subscriptionList, ClusterType.spark);
        }catch (AggregatedException aggregateException){
           if (dealWithAggregatedException(aggregateException)) {
               subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
               try {
                   clusterDetailList = ClusterManager.getInstance().getHDInsightClusersWithSpecificType(subscriptionList, ClusterType.spark);
               } catch (Exception exception) {
                   DefaultLoader.getUIHelper().showError("Failed to list HDInsight cluster", "List HDInsight Cluster");
               }
           }
        }

        if(clusterDetailList != null) {
            for (IClusterDetail clusterDetail : clusterDetailList) {
                addChildNode(new ClusterNode(this, clusterDetail));
            }
        }
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
                                    "HDInsight Explorer - Error Signing In", false, true);
                        } finally {
                        break;
                    }
                }
            }
        }

        return isReAuth;
    }

    @Override
    public Object getProject() {
        return project;
    }

    public void registerSubscriptionsChanged()
            throws HDExploreException {
        synchronized (subscriptionsChangedSync) {
            if (subscriptionsChanged == null) {
                subscriptionsChanged = AzureManagerImpl.getManager().registerSubscriptionsChanged();
            }

            registeredSubscriptionsChanged = true;

            DefaultLoader.getIdeHelper().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    while (registeredSubscriptionsChanged) {
                        try {
                            subscriptionsChanged.waitEvent(new Runnable() {
                                @Override
                                public void run() {
                                    if (registeredSubscriptionsChanged) {
                                        removeAllChildNodes();
                                        load();
                                    }
                                }
                            });
                        } catch (HDExploreException ignored) {
                            break;
                        }
                    }
                }
            });
        }
    }
}