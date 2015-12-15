package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.sdk.cluster.*;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by guizha on 12/11/2015.
 */

public class StorageAccountFolderNode extends HDInsightRefreshNode {
    private static final String STORAGE_ACCOUNT_FOLDER_MODULE_ID = StorageAccountFolderNode.class.getName();
    private static final String STORAGE_ACCOUNT_NAME = "Storage Accounts";
    private static final String ICON_PATH = PluginUtil.StorageAccountFoldIConPath;

    private IClusterDetail clusterDetail;
    public StorageAccountFolderNode(Node parent, IClusterDetail clusterDetail) {
        super(STORAGE_ACCOUNT_FOLDER_MODULE_ID, STORAGE_ACCOUNT_NAME, parent, ICON_PATH);
        this.clusterDetail = clusterDetail;
    }

    @Override
    protected void refresh(@NotNull EventHelper.EventStateHandle eventState)
            throws HDExploreException {
        removeAllChildNodes();
        if(clusterDetail != null){
            try {
                clusterDetail.getConfigurationInfo();
                addChildNode(new StorageAccountNode(this,clusterDetail.getStorageAccount()));
                List<StorageAccount> additionalStorageAccount = clusterDetail.getAdditionalStorageAccounts();
                if(additionalStorageAccount != null) {
                    for (StorageAccount account:additionalStorageAccount){
                        addChildNode(new StorageAccountNode(this,account));
                    }
                }
            }
            catch (Exception exception) {
                DefaultLoader.getUIHelper().showException(
                        "Failed to get HDInsight cluster configuration.", exception,
                        "HDInsight Explorer", false, true);
            }
        }
    }
}
