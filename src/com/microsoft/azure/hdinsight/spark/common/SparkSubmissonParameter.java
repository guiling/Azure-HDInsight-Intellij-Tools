package com.microsoft.azure.hdinsight.spark.common;

/**
 * Created by guizha on 8/27/2015.

 file	            archive holding the file                            path (required)
 args	            command line arguments	                            list of strings
 className	        application's java/spark main class	                string
 jars	            files to be placed on the java classpath	        list of paths
 pyFiles	        files to be placed on the PYTHONPATH	            list of paths
 files	            files to be placed in executor working directory	list of paths
 driverMemory	    memory for driver	                                string
 driverCores	    number of cores used by driver	                    int
 executorMemory	    memory for executor	                                string
 executorCores	    number of cores used by executor	                int
 numExecutors	    number of executor	                                int
 archives	 	                                                        list of paths

 */
public class SparkSubmissonParameter {

    private String file = "";
    private String className="";

    public SparkSubmissonParameter(){

    }

    public SparkSubmissonParameter(String file, String className){
        this.file = file;
        this.className = className;
    }

    public String getFile() {
        return file;
    }

    public String getClassName() {
        return className;
    }
}
