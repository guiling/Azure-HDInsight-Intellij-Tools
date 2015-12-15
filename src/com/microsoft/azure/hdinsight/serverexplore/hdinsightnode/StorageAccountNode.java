package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.sdk.storage.BlobContainer;
import com.microsoft.azure.hdinsight.sdk.storage.IStorageClient;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.sdk.storage.StorageClientImpl;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.serverexplore.node.*;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by guizha on 12/11/2015.
 */

public class StorageAccountNode extends HDInsightRefreshNode {
    private static final String STORAGE_ACCOUNT_MODULE_ID = StorageAccountNode.class.getName();
    private static final String ICON_PATH = PluginUtil.StorageAccountIConPath;

    private StorageAccount storageAccount;

    public StorageAccountNode(Node parent, StorageAccount storageAccount) {
        super(STORAGE_ACCOUNT_MODULE_ID, storageAccount.getStorageName(), parent, ICON_PATH);
        this.storageAccount = storageAccount;
        load();
    }

    @Override
    protected void refresh(@NotNull EventHelper.EventStateHandle eventState)
            throws HDExploreException {
        removeAllChildNodes();
        IStorageClient storageClient = new StorageClientImpl();
        try {
            List<BlobContainer> containerList = storageClient.getBlobContainers(storageAccount);
            for(BlobContainer blobContainer : containerList){
                addChildNode(new BlobContainerNode(this, storageAccount, blobContainer));
            }
        }
        catch (HDIException hdiException){
            DefaultLoader.getUIHelper().showException(
                    "Failed to list blob containers.", hdiException,
                    "HDInsight Explorer", false, true);
        }
    }
}


