package com.microsoft.azure.hdinsight.projects;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.PluginsAdvertiser;
import com.microsoft.azure.hdinsight.common.CommonConst;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by zhax on 8/20/2015.
 */
public class HDInsightModuleType extends ModuleType<HDInsightModuleBuilder> {
    private static final HDInsightModuleType INSTANCE = new HDInsightModuleType();

    public HDInsightModuleType() {
        super("HDINSIGHT_MODULE");
    }

    public static HDInsightModuleType getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public HDInsightModuleBuilder createModuleBuilder() {
        return new HDInsightModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "HDInsight Projects";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Support HDInsight products.";
    }

    @Override
    public Icon getBigIcon() {
        return null;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return PluginUtil.getIcon(CommonConst.ProductIConPath);
    }
}
