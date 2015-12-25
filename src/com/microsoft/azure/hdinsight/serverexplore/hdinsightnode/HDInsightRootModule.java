package com.microsoft.azure.hdinsight.serverexplore.hdinsightnode;

import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import com.microsoft.azure.hdinsight.common.CommonConst;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterManager;
import com.microsoft.azure.hdinsight.sdk.cluster.ClusterType;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.AggregatedException;
import com.microsoft.azure.hdinsight.sdk.common.AuthenticationErrorHandler;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.serverexplore.AzureManager;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.sdk.subscription.Subscription;
import com.microsoft.azure.hdinsight.serverexplore.AzureManagerImpl;
import com.microsoft.azure.hdinsight.serverexplore.node.EventHelper;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import com.microsoft.azure.hdinsight.serverexplore.node.RefreshableNode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by joezhang on 15-12-2.
 */
public class HDInsightRootModule extends RefreshableNode {
    private static final String HDInsight_SERVICE_MODULE_ID = HDInsightRootModule.class.getName();
    private static final String ICON_PATH = CommonConst.HDInsightIConPath;
    private static final String BASE_MODULE_NAME = "HDInsight";

    private Object project;
    private EventHelper.EventWaitHandle subscriptionsChanged;
    private boolean registeredSubscriptionsChanged;
    private final Object subscriptionsChangedSync = new Object();

    public HDInsightRootModule(Node parent, String iconPath, Object data) {
        super(HDInsight_SERVICE_MODULE_ID, BASE_MODULE_NAME, parent, iconPath);
    }

    public HDInsightRootModule(Object project) {
        this(null, ICON_PATH, null);
        this.project = project;
    }

 public void addHDInsightAdditionalCluster(HDInsightClusterDetail hdInsightClusterDetail) {

        hdinsightAdditionalList.add(hdInsightClusterDetail);
        writeToLocalJson();
        refreshWithoutAsync();
    }

public boolean IsHDInsightAdditionalClusterExist(String clusterName) {

        for(IClusterDetail clusterDetail : hdinsightAdditionalList)
        {
            if(clusterDetail.getName().equals(clusterName)) {
                return true;
            }
        }

        for (IClusterDetail clusterDetail : hdinsightAdditionalList) {
            if (clusterDetail.getName().equals(clusterName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void refreshItems() throws HDExploreException {
        removeAllChildNodes();
        List<IClusterDetail> clusterDetailList = HDInsightHelper.getInstance().getClusterDetails();

        if(clusterDetailList != null) {
            for (IClusterDetail clusterDetail : clusterDetailList) {
                addChildNode(new ClusterNode(this, clusterDetail));
            }
        }

        hdinsightAdditionalList = getFromLocalJson();
        for(IClusterDetail clusterDetail : hdinsightAdditionalList)
        {
            addChildNode(new ClusterNode(this, clusterDetail));
        }
    }

    @Override
    public Object getProject() {
        return project;
    }

    public void registerSubscriptionsChanged()
            throws HDExploreException {
        synchronized (subscriptionsChangedSync) {
            if (subscriptionsChanged == null) {
                subscriptionsChanged = AzureManagerImpl.getManager().registerSubscriptionsChanged();
            }

            registeredSubscriptionsChanged = true;

            DefaultLoader.getIdeHelper().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    while (registeredSubscriptionsChanged) {
                        try {
                            subscriptionsChanged.waitEvent(new Runnable() {
                                @Override
                                public void run() {
                                    if (registeredSubscriptionsChanged) {
                                        removeAllChildNodes();
                                        load();
                                    }
                                }
                            });
                        } catch (HDExploreException ignored) {
                            break;
                        }
                    }
                }
            });
        }
    }

    private void refreshWithoutAsync() {
        removeAllChildNodes();
        for (IClusterDetail clusterDetail : clusterDetailList) {
            addChildNode(new ClusterNode(this, clusterDetail));
        }

        for(IClusterDetail clusterDetail : hdinsightAdditionalList)
        {
            addChildNode(new ClusterNode(this, clusterDetail));
        }
    }

    private boolean dealWithAggregatedException(AggregatedException aggregateException) {
        boolean isReAuth = false;
        for (Exception exception : aggregateException.getExceptionList()) {
            if (exception instanceof HDIException) {
                if (((HDIException) exception).getErrorCode() == AuthenticationErrorHandler.AUTH_ERROR_CODE) {
                    try {
                        AzureManager apiManager = AzureManagerImpl.getManager();
                        apiManager.authenticate();
                        isReAuth = true;
                    } catch (HDExploreException e1) {
                        DefaultLoader.getUIHelper().showException(
                                "An error occurred while attempting to sign in to your account.", e1,
                                "HDInsight Explorer - Error Signing In", false, true);
                    } finally {
                        break;
                    }
                }
            }
        }

        return isReAuth;
    }

    private void writeToLocalJson()
    {

        Gson gson = new Gson();
        String json = gson.toJson(hdinsightAdditionalList);
        String filePath = String.join(PluginUtil.getCOnfigPath(), File.pathSeparator, PluginUtil.HDINSIGHT_ADDITIONAL_CLUSTER_RECODER);

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                        try{
                            FileWriter writer = new FileWriter(filePath);
                            writer.write(json);
                            writer.close();
                    }catch (IOException ex)
                    {
                        //do noting if we cannot write it to local file.
                    }
                }
            });
    }

    private List<IClusterDetail> getFromLocalJson()
    {
        Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();

        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = null;

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(String.join(PluginUtil.getCOnfigPath(), File.pathSeparator, "favourateCluster.json")));

                    while((s = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(s);
                    }

                } catch (IOException e) {
                    //we donoting if we cannot read it from local file
                }
            }
        }, ModalityState.NON_MODAL);

        List<IClusterDetail> hdiLocalClusters = new ArrayList<IClusterDetail>();
                try{
                    hdiLocalClusters = gson.fromJson(stringBuilder.toString(), new TypeToken<ArrayList<HDInsightClusterDetail>>() {}.getType());
                }
                catch (JsonSyntaxException e)
                {
                    //do nothing if we cannot get it from json
                }

        return hdiLocalClusters;
    }


}