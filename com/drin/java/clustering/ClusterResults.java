package com.drin.java.clustering;

import com.drin.java.clustering.Cluster;
import com.drin.java.clustering.Clusterable;

import java.util.List;

public class ClusterResults {
   private List<Cluster> mClusters;

   public ClusterResults(List<Cluster> finalClusters) {
      mClusters = finalClusters;
   }

   public String toString() {
      String clustInfo = "", dendInfo = "<Clusters>\n";

      for (Cluster cluster : mClusters) {
         dendInfo += cluster.getDendogram().toString();
         clustInfo += String.format("Cluster %s:\n", cluster.getName());

         for (Clusterable<?> elem : cluster.getElements()) {
            clustInfo += String.format("\t,%s\n", elem.getName());
         }
      }

      dendInfo += "</Clusters>\n";

      return String.format("%s%s", ("******\n\nDendogram:\n" + dendInfo),
                          ("******\n\nClusters:\n" + clustInfo));
   }
}
