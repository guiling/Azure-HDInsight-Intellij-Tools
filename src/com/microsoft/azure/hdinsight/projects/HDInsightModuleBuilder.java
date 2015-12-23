package com.microsoft.azure.hdinsight.projects;

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.PluginsAdvertiser;
import com.microsoft.azure.hdinsight.common.CommonConst;
import com.microsoft.azure.hdinsight.common.PluginUtil;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by zhax on 8/20/2015.
 */
public class HDInsightModuleBuilder extends JavaModuleBuilder implements ModuleBuilderListener {
    private HDInsightTemplateItem selectedTemplate;
    private LibrariesContainer librariesContainer;
    private LibraryCompositionSettings libraryCompositionSettings;

    public HDInsightModuleBuilder() {
        this.addListener(this);
        this.addModuleConfigurationUpdater(new ModuleConfigurationUpdater() {
            @Override
            public void update(Module module, ModifiableRootModel modifiableRootModel) {
                if (libraryCompositionSettings != null) {
                    libraryCompositionSettings.addLibraries(modifiableRootModel, new ArrayList<Library>(), librariesContainer);
                }
            }
        });
    }

    public void setSelectedTemplate(HDInsightTemplateItem selectedTemplate)
    {
        this.selectedTemplate = selectedTemplate;
    }

    public void setLibraryCompositionSettings(LibraryCompositionSettings libraryCompositionSettings)
    {
        this.libraryCompositionSettings = libraryCompositionSettings;
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
        return PluginUtil.getIcon(CommonConst.ProductIConPath);
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
    public ModuleType getModuleType() {
        return HDInsightModuleType.getInstance();
    }

    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new HDInsightModuleWizardStep(this);
    }

    @Override
    public ModuleWizardStep modifySettingsStep(SettingsStep settingsStep) {
        if (this.selectedTemplate.getType() == HDInsightTemplatesType.SparkScala ||
            this.selectedTemplate.getType() == HDInsightTemplatesType.SparkSamplesScala) {
            this.librariesContainer = LibrariesContainerFactory.createContainer(settingsStep.getContext().getProject());
            return new SparkScalaSettingsStep(this, settingsStep, this.librariesContainer);
        }

        return new SparkJavaSettingsStep(this, settingsStep);
    }

    public void moduleCreated(Module module) {

    }
}
