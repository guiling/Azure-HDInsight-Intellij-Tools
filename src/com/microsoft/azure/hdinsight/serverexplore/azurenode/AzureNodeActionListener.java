package com.microsoft.azure.hdinsight.serverexplore.azurenode;

import com.microsoft.azure.hdinsight.common.AzureCmdException;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.node.*;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

/**
 * Created by joezhang on 15-12-2.
 */
public abstract class AzureNodeActionListener extends NodeActionListenerAsync {
    protected Node azureNode;

    public AzureNodeActionListener(@NotNull Node azureNode,
                                   @NotNull String progressMessage) {
        super(progressMessage);
        this.azureNode = azureNode;
    }

    @NotNull
    @Override
    protected Callable<Boolean> beforeAsyncActionPerfomed() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        };
    }

    @Override
    protected void actionPerformed(final NodeActionEvent e) throws AzureCmdException {
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
            public void interruptibleAction(@NotNull EventHelper.EventStateHandle stateHandle)
                    throws AzureCmdException {
                azureNodeAction(e, stateHandle);
            }

            @Override
            public void eventTriggeredAction()
                    throws AzureCmdException {
                onSubscriptionsChanged(e);
            }
        });
    }

    protected abstract void azureNodeAction(NodeActionEvent e, @NotNull EventHelper.EventStateHandle stateHandle)
            throws AzureCmdException;

    protected abstract void onSubscriptionsChanged(NodeActionEvent e)
            throws AzureCmdException;
}