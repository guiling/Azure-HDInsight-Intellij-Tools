package com.microsoft.azure.hdinsight.spark.common;

/**
 * For batches spark job:
 * <p>
 * proxyUser	        The user to impersonate that will execute the job	            string
 * file	            Archive holding the file	                                    path (required)
 * args	            Command line arguments	                                        list of strings
 * className	        Application's java/spark main class	                            string
 * jars	            Files to be placed on the java classpath	                    list of paths
 * pyFiles	            Files to be placed on the PYTHONPATH	                        list of paths
 * files	            Files to be placed in executor working directory	            list of paths
 * driverMemory	    Memory for driver (e.g. 1000M, 2G)	                            string
 * driverCores	        Number of cores used by driver	                                int
 * executorMemory	    Memory for executor (e.g. 1000M, 2G)	                        string
 * executorCores	    Number of cores used by executor	                            int
 * numExecutors	    Number of executor	                                            int
 * archives	        Archives to be uncompressed (YARN mode only)	                list of paths
 * queue	            The YARN queue to submit too (YARN mode only)	                string
 * name	            Name of the application	string
 */

import com.microsoft.azure.hdinsight.common.StringHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *For interactive spark job:
 *
 kind	            The session kind (required)	session kind
 proxyUser	        The user to impersonate that will run this session (e.g. bob)	                string
 jars	            Files to be placed on the java classpath	                                    list of paths
 pyFiles	            Files to be placed on the PYTHONPATH	                                        list of paths
 files	            Files to be placed in executor working directory	                            list of paths
 driverMemory	    Memory for driver (e.g. 1000M, 2G)	                                            string
 driverCores	        Number of cores used by driver (YARN mode only)	                                int
 executorMemory	    Memory for executor (e.g. 1000M, 2G)	                                        string
 executorCores	    Number of cores used by executor	                                            int
 numExecutors	    Number of executors (YARN mode only)	                                        int
 archives	        Archives to be uncompressed in the executor working directory (YARN mode only)	list of paths
 queue	            The YARN queue to submit too (YARN mode only)	string
 name	            Name of the application	string
 */


public class SparkSubmissionParameter {

    private String file = "";
    private String className = "";

    private List<String> files;
    private List<String> jars;
    private List<String> args;
    private Map<String, Object> jobConfig;

    private static final String driverMemory = "driverMemory";
    private static final String driverCores = "driverCores";
    private static final String executorMemory = "executorMemory";
    private static final String numExecutors = "numExecutors";
    private static final String executorCores = "executorCores";
    private static final String sparkJobName = "name";

    public SparkSubmissionParameter(String file,
                                    String className,
                                    List<String> referencedFiles,
                                    List<String> referencedJars,
                                    List<String> args,
                                    Map<String, Object> jobConfig) {
        this.file = file;
        this.className = className;
        this.files = referencedFiles;
        this.jars = referencedJars;
        this.jobConfig = jobConfig;
        this.args = args;
    }


    public static List<String> checkJobConfigMap(Map<String, Object> jobConfigMap){

        List<String> errorMessageList = new ArrayList<>();
        for(Map.Entry<String, Object> entry : jobConfigMap.entrySet()) {
            if(entry.getKey().equals(driverCores)
                    || entry.getKey().equals(numExecutors)
                    || entry.getKey().equals(executorCores)){
                if(StringHelper.isNullOrWhiteSpace(entry.getValue().toString())){
                    errorMessageList.add(String.format("Error : Value of \"%s\" should not be empty",entry.getKey()));
                    continue;
                }

                try {
                    Integer.parseInt(entry.getValue().toString());
                } catch (NumberFormatException e){
                    errorMessageList.add(String.format("Error : Failed to parse \"%s\", it should be an integer", entry.getValue()));
                }
            } else if(entry.getKey().equals(driverMemory)
                    ||entry.getKey().equals(executorMemory)
                    ||entry.getKey().equals(sparkJobName)){
                if(StringHelper.isNullOrWhiteSpace(entry.getValue().toString())){
                    errorMessageList.add(String.format("Error : Value of \"%s\" should not be empty",entry.getKey()));
                }
            }else
            {
                errorMessageList.add(String.format("Error : Key \"%s\" is invalid. It should be one of \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"",
                        entry.getKey(),driverMemory,driverCores,executorMemory,numExecutors,executorCores,sparkJobName));
            }
        }

        return errorMessageList;
    }

    public String serializeToJson() {

        JSONObject jsonObject = new JSONObject(getSparkSubmissionParameterMap());
        try {
            Object driverCoresValue = jsonObject.get(driverCores);
            jsonObject.put(driverCores, Integer.parseInt(driverCoresValue.toString()));
        } catch (JSONException e) {
        }

        try {
            Object driverCoresValue = jsonObject.get(executorCores);
            jsonObject.put(executorCores, Integer.parseInt(driverCoresValue.toString()));
        } catch (JSONException e) {
        }

        try {
            Object driverCoresValue = jsonObject.get(numExecutors);
            jsonObject.put(numExecutors, Integer.parseInt(driverCoresValue.toString()));
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }

    private Map<String, Object> getSparkSubmissionParameterMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("file", file);
        map.put("className", className);
        if (files != null && files.size() > 0) {
            map.put("files", files);
        }

        if (jars != null && jars.size() > 0) {
            map.put("jars", jars);
        }

        if (args != null && args.size() > 0) {
            map.put("args", args);
        }

        if (jobConfig != null) {
            if (jobConfig.containsKey(driverMemory)) {
                map.put(driverMemory, jobConfig.get(driverMemory));
            }

            if (jobConfig.containsKey(driverCores)) {
                map.put(driverCores, jobConfig.get(driverCores));
            }

            if (jobConfig.containsKey(executorMemory)) {
                map.put(executorMemory, jobConfig.get(executorMemory));
            }

            if (jobConfig.containsKey(executorCores)) {
                map.put(executorCores, jobConfig.get(executorCores));
            }

            if (jobConfig.containsKey(numExecutors)) {
                map.put(numExecutors, jobConfig.get(numExecutors));
            }

            if (jobConfig.containsKey(sparkJobName)) {
                map.put(sparkJobName, jobConfig.get(sparkJobName));
            }
        }

        return map;
    }
}
