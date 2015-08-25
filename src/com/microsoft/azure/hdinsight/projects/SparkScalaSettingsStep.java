package com.microsoft.azure.hdinsight.projects;

import com.intellij.facet.impl.ui.libraries.LibraryOptionsPanel;
import com.intellij.framework.library.FrameworkLibraryVersionFilter;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.plugins.scala.project.template.ScalaLibraryDescription$;

import javax.swing.*;

/**
 * Created by zhax on 8/21/2015.
 */
public class SparkScalaSettingsStep extends ModuleWizardStep {
    private HDInsightModuleBuilder builder;
    private ModuleWizardStep javaStep;
    private LibraryOptionsPanel libraryPanel;

    public SparkScalaSettingsStep(HDInsightModuleBuilder builder, SettingsStep settingsStep,
                                  LibrariesContainer librariesContainer) {
        this.builder = builder;
        this.javaStep = StdModuleTypes.JAVA.modifyProjectTypeStep(settingsStep, builder);
        this.libraryPanel = new LibraryOptionsPanel(ScalaLibraryDescription$.MODULE$, "",
                FrameworkLibraryVersionFilter.ALL, librariesContainer, false);
        settingsStep.addSettingsField("Scala S\u001BDK:", libraryPanel.getSimplePanel());
    }

    @Override
    public JComponent getComponent() {
        return libraryPanel.getMainPanel();
    }

    @Override
    public void updateDataModel() {
        this.builder.setLibraryCompositionSettings(libraryPanel.apply());
        javaStep.updateDataModel();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        return super.validate() && (javaStep == null || javaStep.validate());
    }

    @Override
    public void disposeUIResources() {
        super.disposeUIResources();
        javaStep.disposeUIResources();
        Disposer.dispose(libraryPanel);
    }
}
