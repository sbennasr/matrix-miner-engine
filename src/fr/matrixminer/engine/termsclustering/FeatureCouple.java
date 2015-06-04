package fr.matrixminer.engine.termsclustering;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FeatureCouple {
	
	private String featureSource;
	private String featureTarget;

	public FeatureCouple(String fSource, String fTarget) {
		this.featureSource = fSource;
		this.featureTarget = fTarget;
	}
	public String getFeatureSource() {
		return featureSource;
	}
	public void setFeatureSource(String fSource) {
		this.featureSource = fSource;
	}
	public String getFeatureTarget() {
		return featureTarget;
	}
	public void setFeatureTarget(String fTarget) {
		this.featureTarget = fTarget;
	}
	public  String toString (){
		return "("+featureSource+","+featureTarget+")";
	}

	public Boolean existKeyReverse (Map<FeatureCouple, Double> similarityMap){
		
		Set listKeys=similarityMap.keySet();
		Iterator iterator =listKeys.iterator();
		
		while(iterator.hasNext())
		{
			Object key= iterator.next();
		    if (key.toString().equals("("+featureTarget+","+featureSource+")"))
			return true;
		}
		
		return false;
		
	}
	}