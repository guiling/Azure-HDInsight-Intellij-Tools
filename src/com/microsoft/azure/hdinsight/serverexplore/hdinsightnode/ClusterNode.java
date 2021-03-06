package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.microsoft.azure.hdinsight.common.CommonConst;
import com.microsoft.azure.hdinsight.sdk.cluster.HDInsightAdditionalClusterDetail;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.serverexplore.UI.DeleteHDInsightClusterDialog;
import com.microsoft.azure.hdinsight.serverexplore.node.*;
import com.sun.istack.internal.NotNull;

/**
 * Created by guizha on 12/7/2015.
 */
public class ClusterNode extends HDInsightRefreshNode {
    private static final String CLUSTER_MODULE_ID = ClusterNode.class.getName();
    private static final String ICON_PATH = CommonConst .ClusterIConPath;

    private IClusterDetail clusterDetail;

    public ClusterNode(Node parent, IClusterDetail clusterDetail) {
        super(CLUSTER_MODULE_ID, clusterDetail.getName(), parent, ICON_PATH);
        RefreshableNode storageAccountNode = new StorageAccountFolderNode(this, clusterDetail);
        this.clusterDetail = clusterDetail;
        addChildNode(storageAccountNode);
        storageAccountNode.load();
    }

    @Override
    protected void loadActions() {
        super.loadActions();

        if(clusterDetail instanceof HDInsightAdditionalClusterDetail)
        {
            addAction("Delete", new NodeActionListener() {
                @Override
                protected void actionPerformed(NodeActionEvent e) throws HDExploreException {
                    DeleteHDInsightClusterDialog deleteHDInsightClusterDialog = new DeleteHDInsightClusterDialog((HDInsightAdditionalClusterDetail) clusterDetail);
                    deleteHDInsightClusterDialog.pack();
                    deleteHDInsightClusterDialog.setVisible(true);
                }
            });
        }
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
