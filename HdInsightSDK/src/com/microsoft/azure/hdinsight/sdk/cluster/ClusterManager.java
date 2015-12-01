package com.microsoft.azure.hdinsight.sdk.cluster;

import com.microsoft.azure.hdinsight.sdk.common.CommonRunnable;
import com.microsoft.azure.hdinsight.sdk.common.RequestCallBack;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by joezhang on 15-11-27.
 */
public class ClusterManager {

    private final int MAX_CONCURRENT = 5;
    // Singleton Instance
    private static ClusterManager instance = null;

    public static ClusterManager getInstance() {
        if(instance == null){
            synchronized (ClusterManager.class){
                if(instance == null){
                    instance = new ClusterManager();
                }
            }
        }

        return instance;
    }

    private ClusterManager() {
    }


    public synchronized List<IClusterDetail> getHDInsightClusers(List<Subscription>subscriptions, RequestCallBack<Exception> callBack) throws InterruptedException{
        return getClusterDetails(subscriptions, callBack);
    }

    public synchronized List<IClusterDetail> getHDInsightClusersWithSpecificType(List<Subscription>subscriptions, ClusterType type, RequestCallBack<Exception> callBack) throws InterruptedException{
        List<IClusterDetail> clusterDetailList = getClusterDetails(subscriptions, callBack);
        List<IClusterDetail> filterClusterDetailList = new ArrayList<>();
        for (IClusterDetail clusterDetail : clusterDetailList){
            if(clusterDetail.getType().equals(type)){
                filterClusterDetailList.add(clusterDetail);
            }
        }

        return filterClusterDetailList;
    }

    private List<IClusterDetail> getClusterDetails(List<Subscription> subscriptions, final RequestCallBack<Exception> callBack) throws InterruptedException {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(MAX_CONCURRENT);
        List<IClusterDetail> cachedClusterList = new ArrayList<>();

        for(Subscription subscription : subscriptions){
            taskExecutor.execute(new CommonRunnable<Subscription,IOException>(subscription) {
                @Override
                public void runSpecificParameter(Subscription parameter) throws IOException {
                    IClusterOperation clusterOperation = new ClusterOperationImpl();
                    List<ClusterRawInfo> clusterRawInfoList = clusterOperation.listCluster(parameter);
                    if (clusterRawInfoList != null) {
                        for (ClusterRawInfo item : clusterRawInfoList) {
                            IClusterDetail tempClusterDetail = new ClusterDetail(parameter, item);
                            synchronized (ClusterManager.class) {
                                cachedClusterList.add(tempClusterDetail);
                            }
                        }
                    }
                }

                @Override
                public void exceptionHandle(Exception e){
                    callBack.execute(e);
                }
            });
        }

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        return cachedClusterList;
    }
}
