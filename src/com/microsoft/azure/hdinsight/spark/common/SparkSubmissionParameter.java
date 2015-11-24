package com.microsoft.azure.hdinsight.spark.common;

/**
 For batches spark job:

        proxyUser	        The user to impersonate that will execute the job	            string
        file	            Archive holding the file	                                    path (required)
        args	            Command line arguments	                                        list of strings
        className	        Application's java/spark main class	                            string
        jars	            Files to be placed on the java classpath	                    list of paths
        pyFiles	            Files to be placed on the PYTHONPATH	                        list of paths
        files	            Files to be placed in executor working directory	            list of paths
        driverMemory	    Memory for driver (e.g. 1000M, 2G)	                            string
        driverCores	        Number of cores used by driver	                                int
        executorMemory	    Memory for executor (e.g. 1000M, 2G)	                        string
        executorCores	    Number of cores used by executor	                            int
        numExecutors	    Number of executor	                                            int
        archives	        Archives to be uncompressed (YARN mode only)	                list of paths
        queue	            The YARN queue to submit too (YARN mode only)	                string
        name	            Name of the application	string
*/

import java.util.ArrayList;
import java.util.List;

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
    private String className="";

    private List<String> files;

    public SparkSubmissionParameter(String file, String className, List<String>files){
        this.file = file;
        this.className = className;
        this.files = files;
    }

    public String getFile() {
        return file;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getFiles(){
        return files;
    }
}
