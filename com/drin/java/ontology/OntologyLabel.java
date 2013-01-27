package com.drin.java.ontology;

import java.util.Map;
import java.util.HashMap;

public class OntologyLabel implements Labelable {
   private Map<String, Boolean> mLabelMap;

   public OntologyLabel() {
      mLabelMap = new HashMap<String, Boolean>();
   }

   public Map<String, Boolean> getLabelMap() { return mLabelMap; }

   public void addLabel(String labelName) {
      mLabelMap.put(labelName, new Boolean(true));
   }

   public boolean hasLabel(String labelName) {
      return mLabelMap.containsKey(labelName);
   }

   public void addAll(OntologyLabel oldLabels) {
      mLabelMap.putAll(oldLabels.getLabelMap());
   }

}
