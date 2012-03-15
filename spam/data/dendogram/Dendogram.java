package spam.dendogram;

import spam.dataStructures.IsolateSimilarityMatrix;

import spam.dataTypes.Cluster;

import java.util.List;

public interface Dendogram {
   public double getCorrelation();
   public Dendogram getLeft();
   public Dendogram getRight();
   public String getXML();
   public String toXML(String spacing);
   public String defaultStyle(String spacing);
   public String toClusterGraph(String spacing);
}