package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import com.microsoft.azure.hdinsight.common.CommonConst;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by joezhang on 15-12-2.
 */
public class HDInsightRootModule extends RefreshableNode {
    private static final String HDInsight_SERVICE_MODULE_ID = HDInsightRootModule.class.getName();
    private static final String ICON_PATH = CommonConst.HDInsightIConPath;
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
        List<IClusterDetail> clusterDetailList = HDInsightHelper.getInstance().getClusterDetails();

        if(clusterDetailList != null) {
            for (IClusterDetail clusterDetail : clusterDetailList) {
                addChildNode(new ClusterNode(this, clusterDetail));
            }
        }
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