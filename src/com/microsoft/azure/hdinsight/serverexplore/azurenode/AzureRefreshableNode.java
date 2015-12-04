package com.microsoft.azure.hdinsight.serverexplore.azurenode;

/**
 * Created by joezhang on 15-12-2.
 */

import com.microsoft.azure.hdinsight.common.AzureCmdException;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.microsoft.azure.hdinsight.serverexplore.node.RefreshableNode;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public abstract class AzureRefreshableNode extends RefreshableNode {
    public AzureRefreshableNode(String id, String name, Node parent, String iconPath) {
        super(id, name, parent, iconPath);
    }

    public AzureRefreshableNode(String id, String name, Node parent, String iconPath, boolean delayActionLoading) {
        super(id, name, parent, iconPath, delayActionLoading);
    }

    @Override
    protected void refreshItems()
            throws AzureCmdException {
        EventHelper.runInterruptible(new EventHelper.EventHandler() {
            @Override
            public EventHelper.EventWaitHandle registerEvent()
                    throws AzureCmdException {
                return AzureManagerImpl.getManager().registerSubscriptionsChanged();
            }

            @Override
            public void unregisterEvent(@NotNull EventHelper.EventWaitHandle waitHandle)
                    throws AzureCmdException {
                AzureManagerImpl.getManager().unregisterSubscriptionsChanged(waitHandle);
            }

            @Override
            public void interruptibleAction(@NotNull EventHelper.EventStateHandle eventState)
                    throws AzureCmdException {
                refresh(eventState);
            }

            @Override
            public void eventTriggeredAction()
                    throws AzureCmdException {
            }
        });
    }

    protected abstract void refresh(@NotNull EventHelper.EventStateHandle eventState)
            throws AzureCmdException;
}