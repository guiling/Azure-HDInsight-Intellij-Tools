package com.microsoft.azure.hdinsight.projects;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.microsoft.azure.hdinsight.common.Resources;

import javax.swing.*;

/**
 * Created by zhax on 8/20/2015.
 */
public class HDInsightModuleBuilder extends JavaModuleBuilder implements ModuleBuilderListener {

    public HDInsightModuleBuilder() {
        addListener(this);
    }

    @Override
    public String getBuilderId() {
        return "HDInsight";
    }

    @Override
    public Icon getBigIcon() {
        return null;
    }

    @Override
    public Icon getNodeIcon() {
        return Resources.Product;
    }

    @Override
    public String getPresentableName() {
        return "HDInsight";
    }

    @Override
    public String getGroupName() {
        return "HDInsight Tools";
    }

    @Override
    public void moduleCreated(Module module) {

    }
}
