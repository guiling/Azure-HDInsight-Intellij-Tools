package com.microsoft.azure.hdinsight.common;

import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

/**
 * Created by guizha on 8/21/2015.
 */
public class PluginUtil {

    public static final Icon Product = IconLoader.getIcon("/icons/Product.png");
    public static final String RefreshIConPath = "/icons/Refresh.png";
    public static final String HDInsightIConPath = "/icons/HdInsight.png";
    public static final String ClusterIConPath = "/icons/Cluster.png";
    public static final String StorageAccountIConPath = "/icons/StorageAccount.png";
    public static final String StorageAccountFoldIConPath = "/icons/StorageAccountFolder.png";
    public static final String BlobContainerIConPath = "/icons/Container.png";

    public static final String PLUGIN_NAME = "Microsoft Azure HDInsight Tools for IntelliJ";
    public static final String PLUGIN_VERSION = "1.0";

    public static final String CURRENT_PLUGIN_VERSION = "com.microsoft.azure.hdinsight.intellij.PluginVersion";
    public static final String AAD_AUTHENTICATION_RESULTS = "com.microsoft.azure.hdinsight.intellij.AADAuthenticationResults";
    public static final String AZURE_SUBSCRIPTIONS = "com.microsoft.azure.hdinsight.intellij.AzureSubscriptions";
    public static final String AZURE_USER_INFO = "com.microsoft.azure.hdinsight.intellij.AzureUserInfo";
    public static final String AZURE_USER_SUBSCRIPTIONS = "com.microsoft.azure.hdinsight.intellij.AzureUserSubscriptions";

    public static boolean isModuleRoot(VirtualFile moduleFolder, Module module) {
        return moduleFolder != null && ProjectRootsUtil.isModuleContentRoot(moduleFolder, module.getProject());
    }

    public static final Icon getIcon(String iconPath){
        return IconLoader.getIcon(iconPath);
    }
}