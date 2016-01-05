package com.microsoft.azure.hdinsight.spark.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.common.StringHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.storage.BlobContainer;
import com.microsoft.azure.hdinsight.sdk.storage.CallableSingleArg;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.sdk.storage.StorageClientImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joezhang on 16-1-5.
 */
public class SparkSubmitHelper {
    private static SparkSubmitHelper ourInstance = new SparkSubmitHelper();

    public static SparkSubmitHelper getInstance() {
        return ourInstance;
    }

    private SparkSubmitHelper() {
    }

    public void printRunningLogStreamingly(int id, IClusterDetail clusterDetail) throws IOException {
        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("======================Begin printing out spark job log.=======================");
        try {
            boolean isFailedJob = false;
            while (true) {
                printoutJobLog(id, clusterDetail);
                HttpResponse statusHttpResponse = SparkBatchSubmission.getInstance().getBatchSparkJobStatus(clusterDetail.getConnectionUrl() + "/livy/batches", id);
                SparkSubmitResponse status = new Gson().fromJson(statusHttpResponse.getMessage(), new TypeToken<SparkSubmitResponse>() {}.getType());

                if(status.getState().toLowerCase().equals("error") || status.getState().toLowerCase().equals("success")){
                    if(status.getState().toLowerCase().equals("error")){
                        isFailedJob = true;
                    }
                    printoutJobLog(id, clusterDetail);
                    HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("======================Finish printing out spark job log.=======================");
                    break;
                }
            }

            if(isFailedJob){
                HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setError("Error : Your submitted job run failed");
            }

        } catch (Exception e) {
            HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setError("Error : Failed to getting running log. Exception : " + e.toString());
        }
    }


    public String uploadFileToAzureBlob(String localFile, StorageAccount storageAccount, String defaultContainerName, String uniqueFolderId)
            throws HDIException, IOException {
        File file = new File(localFile);
        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo(String.format("Info : Begin upload file %s to azure blob ...", localFile));

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
                final CallableSingleArg<Void, Long> callable = new CallableSingleArg<Void, Long>() {
                    @Override
                    public Void call(Long uploadedBytes) throws Exception {
                        double progress = ((double) uploadedBytes) / file.length();
                        return null;
                    }
                };

                BlobContainer defaultContainer = getSparkClusterDefaultContainer(storageAccount, defaultContainerName);
                String path = String.format("SparkSubmission/%s/%s", uniqueFolderId, file.getName());
                StorageClientImpl.getInstance().uploadBlobFileContent(
                        storageAccount,
                        defaultContainer,
                        path,
                        bufferedInputStream,
                        callable,
                        1024 * 1024,
                        file.length());

                String uploadedPath = String.format("wasb://%s@%s/%s", defaultContainerName, storageAccount.getFullStoragBlobName(), path);
                HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo(String.format("Info : Submit file to azure blob '%s' successfully.", uploadedPath));
                return uploadedPath;
            }
        }
    }

    public List<String> uploadFileListToAzureBlob(String pathListString,StorageAccount storageAccount, String defaultContainerName, String uniqueFolderId)
            throws HDIException, IOException{

        List<String> referencedFileList = new ArrayList<>();
        for (String singleReferencedFile : pathListString.split(";")) {
            if (!StringHelper.isNullOrWhiteSpace(singleReferencedFile)) {
                referencedFileList.add(singleReferencedFile);
            }
        }

        List<String> uploadedFilePathList = new ArrayList<>();
        for(String path : referencedFileList){
            String uploadedFilePath = uploadFileToAzureBlob(path, storageAccount, defaultContainerName,uniqueFolderId);
            uploadedFilePathList.add(uploadedFilePath);
        }

        return uploadedFilePathList;
    }

    private void printoutJobLog(int id, IClusterDetail clusterDetail) throws IOException {
        HttpResponse sparkLog = SparkBatchSubmission.getInstance().getBatchJobFullLog(clusterDetail.getConnectionUrl() + "/livy/batches", id);

        SparkJobLog sparkJobLog = new Gson().fromJson(sparkLog.getMessage(), new TypeToken<SparkJobLog>() {}.getType());

        if (sparkJobLog.getLog().size() > 0) {
            StringBuilder tempLogBuilder = new StringBuilder();
            for (String partLog : sparkJobLog.getLog()) {
                tempLogBuilder.append(partLog);
            }

            HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setDuplicatedInfo(tempLogBuilder.toString());
        }
    }

    private BlobContainer getSparkClusterDefaultContainer(StorageAccount storageAccount, String defalutContainerName) throws HDIException {
        List<BlobContainer> containerList = StorageClientImpl.getInstance().getBlobContainers(storageAccount);
        for (BlobContainer container : containerList) {
            if (container.getName().toLowerCase().equals(defalutContainerName.toLowerCase())) {
                return container;
            }
        }

        return null;
    }
}
