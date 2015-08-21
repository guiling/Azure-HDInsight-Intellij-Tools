package com.microsoft.azure.hdinsight.projects;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.microsoft.azure.hdinsight.common.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by zhax on 8/20/2015.
 */
public class HDInsightModuleBuilder extends ModuleBuilder implements ModuleBuilderListener {
    private HDInsightTemplateItem selectedTemplate;

    public HDInsightModuleBuilder() {
        this.addListener(this);
    }

    public void setSelectedTemplate(HDInsightTemplateItem selectedTemplate)
    {
        this.selectedTemplate = selectedTemplate;
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
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        this.doAddContentEntry(modifiableRootModel);
    }

    @Override
    public ModuleType getModuleType() {
        return HDInsightModuleType.getInstance();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new HDInsightModuleWizardStep(this);
    }

    @Override
    public void moduleCreated(Module module) {
        JOptionPane.showMessageDialog(null, this.selectedTemplate.getDisplayText(), "HDInsight selectedTemplate", JOptionPane.OK_OPTION);
    }
}
