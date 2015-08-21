package com.microsoft.azure.hdinsight.projects;

import com.microsoft.azure.hdinsight.projects.HDInsightTemplateItem;
import com.microsoft.azure.hdinsight.projects.HDInsightTemplatesType;

import java.util.ArrayList;

/**
 * Created by zhax on 8/21/2015.
 */
public class HDInsightTemplates {
    private static ArrayList<HDInsightTemplateItem> templates = new ArrayList<HDInsightTemplateItem>();

    static {
        templates.add(new HDInsightTemplateItem("Spark on HDInsight (Scala)", HDInsightTemplatesType.SparkScala));
        templates.add(new HDInsightTemplateItem("Spark on HDInsight (Java)", HDInsightTemplatesType.SparkJava));
        templates.add(new HDInsightTemplateItem("Spark on HDInsight Samples (Scala)", HDInsightTemplatesType.SparkSamplesScala));
        templates.add(new HDInsightTemplateItem("Spark on HDInsight Samples (Java)", HDInsightTemplatesType.SparkSamplesJava));
    }

    public static ArrayList<HDInsightTemplateItem> getTemplates() {
        return templates;
    }
}

