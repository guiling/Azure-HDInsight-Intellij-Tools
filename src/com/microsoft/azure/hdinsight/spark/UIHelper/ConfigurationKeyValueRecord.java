package com.microsoft.azure.hdinsight.spark.UIHelper;

/**
 * Created by guizha on 8/25/2015.
 */
public class ConfigurationKeyValueRecord {
    private String key = "";
    private Object value = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
