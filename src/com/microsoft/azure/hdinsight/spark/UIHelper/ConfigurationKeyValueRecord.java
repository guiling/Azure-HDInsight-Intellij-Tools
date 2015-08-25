package com.microsoft.azure.hdinsight.spark.UIHelper;

/**
 * Created by guizha on 8/25/2015.
 */
public class ConfigurationKeyValueRecord {
    private String key = "";
    private String value = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
