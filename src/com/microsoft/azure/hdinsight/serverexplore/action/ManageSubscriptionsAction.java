package com.microsoft.azure.hdinsight.serverexplore.action;

import com.intellij.openapi.project.Project;
import com.microsoft.azure.hdinsight.serverexplore.UI.ManageSubscriptionForm;
import com.microsoft.azure.hdinsight.serverexplore.azurenode.AzureServiceModule;
import com.microsoft.azure.hdinsight.serverexplore.node.Name;
import com.microsoft.azure.hdinsight.serverexplore.node.NodeActionEvent;
import com.microsoft.azure.hdinsight.serverexplore.node.NodeActionListener;

/**
 * Created by joezhang on 15-12-3.
 */
@Name("Manage Subscriptions")
public class ManageSubscriptionsAction extends NodeActionListener {
    private AzureServiceModule azureServiceModule;

    public ManageSubscriptionsAction(AzureServiceModule azureServiceModule) {
        this.azureServiceModule = azureServiceModule;
    }

    @Override
    public void actionPerformed(NodeActionEvent e) {
        ManageSubscriptionForm form = new ManageSubscriptionForm((Project) azureServiceModule.getProject());
        form.show();
    }
}