package com.drin.java.clustering;

import com.drin.java.clustering.Cluster;
import com.drin.java.clustering.Clusterable;

import java.util.List;
import java.util.Map;

public class ClusterResults {
   private Map<Double, List<Cluster>> mClusters;

   public ClusterResults(Map<Double, List<Cluster>> finalClusters) {
      mClusters = finalClusters;
   }

   public String toString() {
      String clustInfo = "", clustContents = "", bigClustName = "";
      int biggestClust = 0;

      for (Map.Entry<Double, List<Cluster>> clusterData : mClusters.entrySet()) {
         clustInfo += String.format("\nThreshold: %.04f\nNumber of Result Clusters, %d\n",
                                     clusterData.getKey(), clusterData.getValue().size());

         for (Cluster cluster : clusterData.getValue()) {
            if (cluster.size() > biggestClust) {
               biggestClust = cluster.size();
               bigClustName = String.format("\"Cluster %s\"", cluster.getName());
            }

            for (Clusterable<?> elem : cluster.getElements()) {
               clustContents += String.format("Cluster %s, %s\n", cluster.getName(), elem.getName());
            }
         }

         clustInfo += String.format("Largest Cluster, %s\nLargest Cluster Size, %d\n\n%s",
                                    bigClustName, biggestClust, clustContents);
      }

      return String.format("%s", "******\n\nClusters:\n" + clustInfo);
   }
}
