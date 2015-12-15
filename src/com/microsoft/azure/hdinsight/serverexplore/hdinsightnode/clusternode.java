package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.microsoft.azure.hdinsight.serverexplore.node.NodeActionEvent;
import com.microsoft.azure.hdinsight.serverexplore.node.RefreshableNode;
import com.sun.istack.internal.NotNull;

/**
 * Created by guizha on 12/7/2015.
 */
public class ClusterNode extends HDInsightRefreshNode {
    private static final String CLUSTER_MODULE_ID = ClusterNode.class.getName();
    private static final String ICON_PATH = PluginUtil.ClusterIConPath;

    public ClusterNode(Node parent, IClusterDetail clusterDetail) {
        super(CLUSTER_MODULE_ID, clusterDetail.getName(), parent, ICON_PATH);
        RefreshableNode storageAccountNode = new StorageAccountFolderNode(this, clusterDetail);
        addChildNode(storageAccountNode);
        storageAccountNode.load();
    }

    @Override
    protected void refresh(@NotNull EventHelper.EventStateHandle eventState)
            throws HDExploreException {
        for(Node childNode : childNodes){
            RefreshableNode refreshableNode = (RefreshableNode)childNode;
            if(refreshableNode != null){
                refreshableNode.load();
            }
        }
    }
}
