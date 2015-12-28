package com.microsoft.azure.hdinsight;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.microsoft.azure.hdinsight.serverexplore.ServerExplorerToolWindowFactory;
import com.microsoft.azure.hdinsight.serverexplore.hdinsightnode.HDInsightRootModule;

/**
 * Created by ltian on 12/28/2015.
 */
public class ProjectManager {

    public static final String SERVEREXPLORER_TOOLWINDOW_ID = "HDInsight Explorer";
    private Project project;

    private static ProjectManager instance = null;

    private ProjectManager(final Project project)
    {
        this.project = project;
    }

    public static void initliza(Project project)
    {
        instance = new ProjectManager(project);
    }

    public static ProjectManager getInstance()
    {
        return instance;
    }

    public ToolWindow getServerExplorerToolWindow()
    {
       return ToolWindowManager.getInstance(project).getToolWindow(SERVEREXPLORER_TOOLWINDOW_ID);
    }

    public HDInsightRootModule getHDInsightRootModule()
    {
        return ((ServerExplorerToolWindowFactory)getServerExplorerToolWindow()).getAzureServiceModule();
    }
}
