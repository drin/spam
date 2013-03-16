package com.drin.java.test;

import com.drin.java.database.CPLOPConnection;

import com.drin.java.biology.Isolate;
import com.drin.java.biology.ITSRegion;
import com.drin.java.biology.Pyroprint;

import com.drin.java.metrics.DataMetric;

import com.drin.java.ontology.Ontology;

import com.drin.java.clustering.Clusterable;
import com.drin.java.clustering.Cluster;
import com.drin.java.clustering.HCluster;
import com.drin.java.clustering.ClusterResults;

import com.drin.java.analysis.clustering.Clusterer;
import com.drin.java.analysis.clustering.AgglomerativeClusterer;
import com.drin.java.analysis.clustering.OHClusterer;

import com.drin.java.util.Configuration;
import com.drin.java.util.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * SPAMEvaluation serves as a test harness for SPAM and various results
 * reported by any particular analysis method. SPAMEvaluation, being a test
 * harness, will handle instantiation of an analysis method and the execution
 * thereof. I think the best approach would be to allow the test harness to
 * automatically do parameter tuning, or execute its test with a single
 * parameter configuration. Whether parameter tuning occurs or not will be a
 * parameter to the test harness. Another parameter to the test harness will be
 * the data itself. However, the test harness will have to transform the data
 * into the appropriate representation given the choice of analysis method.
 * Finally, an important parameter will be what analysis method to use. This
 * will be selected from a list, and by default will try everything in the
 * list. I believe that the best choice would be for all analysis methods,
 * with automatic parameter tuning, to be tested on a given dataset and their
 * results automatically compared and reported. If otherwise specified, then
 * the test harness can execute a particular analysis method or instantiate
 * a particular configuration.
 */
public class SPAMEvaluation {
   private Configuration mConfiguration;
   private Clusterer mClusterer;

   public SPAMEvaluation(Configuration configuration, Clusterer clusterer,
                         List<Clusterable<?>> dataList) {
      DataMetric<Cluster> clustMetric = null;

      if (configuration == null) {
         System.err.println("Invalid Evaluation Configuration");
         System.exit(1);
      }

      try {
         clustMetric = (DataMetric) Class.forName(configuration.getClusterMetric()).newInstance();
      }
      catch (ClassNotFoundException classErr) {
         System.err.println("fail!");
         System.exit(1);
      }
      catch (InstantiationException instErr) {
         System.err.println("fail!");
         System.exit(1);
      }
      catch (IllegalAccessException accErr) {
         System.err.println("fail!");
         System.exit(1);
      }

      if (clusterer == null) {
         List<Cluster> clusterList = new ArrayList<Cluster>();
         List<Double> threshList = null;

         for (Clusterable<?> data : dataList) {
            clusterList.add(new HCluster(clustMetric, data));
         }

         clusterer = new AgglomerativeClusterer(clusterList, threshList);
      }

      mConfiguration = configuration;
      mClusterer = clusterer;
   }

   public static void main(String[] args) {
      String dataSet = "'Sw-020','Sw-021','Sw-022','Sw-023','Sw-024', " +
                       "'Sw-025','Sw-026','Sw-027','Sw-028','Sw-029', " +
                       "'Sw-030','Sw-031','Sw-032','Sw-033','Sw-034', " +
                       "'Sw-035','Sw-036','Sw-037','Sw-038','Sw-039', " +
                       "'Sw-040','Sw-041','Sw-042'";

      Configuration config = Configuration.loadConfig();
      List<Clusterable<?>> dataList = constructIsolates(config, dataSet);

      SPAMEvaluation evaluator = new SPAMEvaluation(config, null, dataList);
      System.out.println("success");
   }

   private static List<Clusterable<?>> constructIsolates(Configuration config, String dataIds) {
      CPLOPConnection conn = CPLOPConnection.getConnection();
      List<Map<String, Object>> rawDataList = null;
      List<Clusterable<?>> dataList = null;


      DataMetric<Isolate> isoMetric = null;
      DataMetric<ITSRegion> regionMetric = null;
      DataMetric<Pyroprint> pyroMetric = null;

      /*
       * query CPLOP for data
       */
      try { rawDataList = conn.getDataByIsoID(dataIds); }
      catch (java.sql.SQLException sqlErr) {
         sqlErr.printStackTrace();
         System.exit(1);
      }

      /*
       * construct object representations
       */
      try {
         isoMetric = (DataMetric) Class.forName(config.getIsoMetric()).newInstance();
         regionMetric = (DataMetric) Class.forName(config.getRegionMetric()).newInstance();
         pyroMetric = (DataMetric) Class.forName(config.getPyroMetric()).newInstance();
      }
      catch(Exception err) {
         System.err.println("fail!");
         System.exit(1);
      }

      Isolate tmpIso = null;
      Pyroprint tmpPyro = null;
      for (Map<String, Object> dataMap : rawDataList) {
         String wellID     = String.valueOf(dataMap.get("well"));
         String isoID      = String.valueOf(dataMap.get("isolate"));
         String regName    = String.valueOf(dataMap.get("region"));
         String nucleotide = String.valueOf(dataMap.get("nucleotide"));
         Integer pyroID    = new Integer(String.valueOf(dataMap.get("pyroprint")));
         double peakHeight = Double.parseDouble(String.valueOf(dataMap.get("pHeight")));

         String pyroName = String.format("%d (%s)", pyroID.intValue(), wellID);

         //Retrieve Isolate
         if (tmpIso == null || !tmpIso.getName().equals(isoID)) {
            tmpIso = new Isolate(isoID, isoMetric);
            dataList.add(tmpIso);
         }

         if (tmpIso != null) {
            tmpIso.getData().add(new ITSRegion(regName, regionMetric));
         }

         if (tmpPyro == null || !tmpPyro.getName().equals(pyroName)) {
            tmpPyro = new Pyroprint(pyroID.intValue(), wellID, pyroMetric);
            tmpIso.getRegion(regName).add(tmpPyro);
         }
         else if (tmpPyro.getName().equals(pyroName)) {
            tmpPyro.addDispensation(nucleotide, peakHeight);
         }
      }

      return dataList;
   }
}