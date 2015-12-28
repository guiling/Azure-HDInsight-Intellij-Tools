package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.hdinsight.common.CommonConst;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.common.StringHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.HDInsightClusterDetail;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.microsoft.azure.hdinsight.serverexplore.node.RefreshableNode;

import java.util.ArrayList;
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

    private List<IClusterDetail> clusterDetailList = new ArrayList<IClusterDetail>();
    private List<IClusterDetail> hdinsightAdditionalList = new ArrayList<IClusterDetail>();

    public HDInsightRootModule(Node parent, String iconPath, Object data) {
        super(HDInsight_SERVICE_MODULE_ID, BASE_MODULE_NAME, parent, iconPath);
    }

    public HDInsightRootModule(Object project) {
        this(null, ICON_PATH, null);
        this.project = project;
    }

    public void addHDInsightAdditionalCluster(HDInsightClusterDetail hdInsightClusterDetail) {

        hdinsightAdditionalList.add(hdInsightClusterDetail);
        refreshWithoutAsync();
        saveAdditionalClusters();
    }

    public void removeHDInsightAdditionalCluster(HDInsightClusterDetail hdInsightClusterDetail)
    {
        hdinsightAdditionalList.remove(hdInsightClusterDetail);
        refreshWithoutAsync();
        saveAdditionalClusters();
    }

    public boolean IsHDInsightAdditionalClusterExist(String clusterName) {

        for(IClusterDetail clusterDetail : hdinsightAdditionalList)
        {
            if(clusterDetail.getName().equals(clusterName)) {
                return true;
            }
        }

        for (IClusterDetail clusterDetail : hdinsightAdditionalList) {
            if (clusterDetail.getName().equals(clusterName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void refreshItems() throws HDExploreException {
        removeAllChildNodes();
        clusterDetailList = HDInsightHelper.getInstance().getClusterDetails();

        if(clusterDetailList != null) {
            for (IClusterDetail clusterDetail : clusterDetailList) {
                addChildNode(new ClusterNode(this, clusterDetail));
            }
        }

        hdinsightAdditionalList = getAdditionalClusters();
        if(hdinsightAdditionalList != null)
        {
            for(IClusterDetail clusterDetail : hdinsightAdditionalList)
            {
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

    private void refreshWithoutAsync() {
        removeAllChildNodes();

        for (IClusterDetail clusterDetail : clusterDetailList) {
            addChildNode(new ClusterNode(this, clusterDetail));
        }

        for(IClusterDetail clusterDetail : hdinsightAdditionalList)
        {
            addChildNode(new ClusterNode(this, clusterDetail));
        }
    }

    private void saveAdditionalClusters()
    {
        Gson gson = new Gson();
        String json = gson.toJson(hdinsightAdditionalList);
        DefaultLoader.getIdeHelper().setProperty(CommonConst.HDINSIGHT_ADDITIONAL_CLUSTERS,json);
    }

    private List<IClusterDetail> getAdditionalClusters()
    {
        Gson gson = new Gson();
        String json = DefaultLoader.getIdeHelper().getProperty(CommonConst.HDINSIGHT_ADDITIONAL_CLUSTERS);
        List<IClusterDetail> hdiLocalClusters = new ArrayList<IClusterDetail>();

        if(StringHelper.isNullOrWhiteSpace(json))
        {
            return hdiLocalClusters;
        }

        try{
            hdiLocalClusters = gson.fromJson(json, new TypeToken<ArrayList<HDInsightClusterDetail>>() {}.getType());
        }
        catch (JsonSyntaxException e)
        {
            //do nothing if we cannot get it from json
        }

        return hdiLocalClusters;
    }


}