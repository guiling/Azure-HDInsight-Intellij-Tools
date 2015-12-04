package com.microsoft.azure.hdinsight.serverexplore.azurenode;

import com.microsoft.azure.hdinsight.common.AzureCmdException;
import com.microsoft.azure.hdinsight.common.Resources;
import com.microsoft.azure.hdinsight.components.DefaultLoader;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.microsoft.azure.hdinsight.serverexplore.node.RefreshableNode;

import java.util.List;

/**
 * Created by joezhang on 15-12-2.
 */
public class AzureServiceModule extends RefreshableNode {
    private static final String AZURE_SERVICE_MODULE_ID = AzureServiceModule.class.getName();
    private static final String ICON_PATH = Resources.HDInsightIConPath;
    private static final String BASE_MODULE_NAME = "HDInsight";

    private Object project;
    private EventHelper.EventWaitHandle subscriptionsChanged;
    private boolean registeredSubscriptionsChanged;
    private final Object subscriptionsChangedSync = new Object();

    public AzureServiceModule(Object project) {
        this(null, ICON_PATH, null);
        this.project = project;
    }

    public AzureServiceModule(Node parent, String iconPath, Object data) {
        super(AZURE_SERVICE_MODULE_ID, BASE_MODULE_NAME, parent, iconPath);
    }

    @Override
    public String getName() {
        try {
            List<Subscription> subscriptionList = AzureManagerImpl.getManager().getSubscriptionList();
            if (subscriptionList.size() > 0) {
                return String.format("%s (%s)", BASE_MODULE_NAME, subscriptionList.size() > 1
                        ? String.format("%s subscriptions", subscriptionList.size())
                        : subscriptionList.get(0).getDisplayName());
            }
        } catch (AzureCmdException e) {
            DefaultLoader.getUIHelper().showException("An error occurred while attempting to get the subscription list.", e,
                    "MS Services - Error Getting Subscriptions", false, true);
        }
        return BASE_MODULE_NAME;
    }

    @Override
    protected void refreshItems() throws AzureCmdException {
        // add the mobile service module; we check if the node has
        // already been added first because this method can be called
        // multiple times when the user clicks the "Refresh" context
        // menu item

//        if (!mobileServiceModule.isLoading()) {
//            if (!isDirectChild(mobileServiceModule)) {
//                addChildNode(mobileServiceModule);
//            }
//
//            mobileServiceModule.load();
//        }
//
//        if (!vmServiceModule.isLoading()) {
//            if (!isDirectChild(vmServiceModule)) {
//                addChildNode(vmServiceModule);
//            }
//
//            vmServiceModule.load();
//        }
//
//
//        if (!storageServiceModule.isLoading()) {
//            if (!isDirectChild(storageServiceModule)) {
//                addChildNode(storageServiceModule);
//            }
//
//            storageServiceModule.load();
//        }
    }

    @Override
    public Object getProject() {
        return project;
    }

    public void registerSubscriptionsChanged()
            throws AzureCmdException {
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
//
//                                        mobileServiceModule = new MobileServiceModule(AzureServiceModule.this);
//                                        vmServiceModule = new VMServiceModule(AzureServiceModule.this);
//                                        storageServiceModule = new StorageModule(AzureServiceModule.this);

                                        load();
                                    }
                                }
                            });
                        } catch (AzureCmdException ignored) {
                            break;
                        }
                    }
                }
            });
        }
    }
}