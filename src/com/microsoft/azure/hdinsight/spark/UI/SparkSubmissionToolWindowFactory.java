package com.microsoft.azure.hdinsight.spark.UI;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

/**
 * Created by joezhang on 15-12-23.
 */
public class SparkSubmissionToolWindowFactory implements ToolWindowFactory {

    public static final String SPARK_SUBMISSION_WINDOW = "Spark Submission";

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {

    }
}
