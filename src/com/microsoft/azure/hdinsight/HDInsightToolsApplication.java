package com.microsoft.azure.hdinsight;

/**
 * Created by joezhang on 15-12-2.
 */

import com.intellij.openapi.components.ApplicationComponent;
import com.microsoft.azure.hdinsight.common.*;
import com.microsoft.azure.hdinsight.serverexplore.NodeActionsMap;
import org.jetbrains.annotations.NotNull;

public class HDInsightToolsApplication extends ApplicationComponent.Adapter{

    private static HDInsightToolsApplication current = null;

    private HDInsightToolsApplication() {
    }

    public static HDInsightToolsApplication getCurrent() {
        return current;
    }

    @Override
    @NotNull
    public String getComponentName() {
        return PluginUtil.PLUGIN_NAME;
    }

    @Override
    public void initComponent() {
        // save the object instance
        current = this;
        DefaultLoader.setUiHelper(new UIHelperImpl());
        DefaultLoader.setIdeHelper(new IDEHelperImpl());
        DefaultLoader.setNode2Actions(NodeActionsMap.node2Actions);
        cleanTempData(DefaultLoader.getIdeHelper());
    }

    private void cleanTempData(IDEHelper ideHelper) {
        // check the plugin version stored in the properties; if it
        // doesn't match with the current plugin version then we clear
        // all stored options
        // TODO: The authentication tokens are stored with the subscription id appended as a
        // suffix to AZURE_AUTHENTICATION_TOKEN. So clearing that requires that we enumerate the
        // current subscriptions and iterate over that list to clear the auth tokens for those
        // subscriptions.

        String currentPluginVersion = ideHelper.getProperty(PluginUtil.CURRENT_PLUGIN_VERSION);

        if (StringHelper.isNullOrWhiteSpace(currentPluginVersion)|| !PluginUtil.PLUGIN_VERSION.equals(currentPluginVersion)){

            String[] settings = new String[]{
                    PluginUtil.AAD_AUTHENTICATION_RESULTS,
                    PluginUtil.AZURE_SUBSCRIPTIONS,
                    PluginUtil.AZURE_USER_INFO,
                    PluginUtil.AZURE_USER_SUBSCRIPTIONS
            };

            for (String setting : settings) {
                ideHelper.unsetProperty(setting);
            }
        }

        // save the current plugin version
        ideHelper.setProperty(PluginUtil.CURRENT_PLUGIN_VERSION, PluginUtil.PLUGIN_VERSION);
    }
}