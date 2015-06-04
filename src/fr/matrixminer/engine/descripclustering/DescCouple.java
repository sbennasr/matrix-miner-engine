package fr.matrixminer.engine.descripclustering;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DescCouple {
	
	private String descSource;
	private String descTarget;

	public DescCouple(String reqSource, String reqTarget) {
		this.descSource = reqSource;
		this.descTarget = reqTarget;
	}
	public String getDescSource() {
		return descSource;
	}
	public void setDescSource(String reqSource) {
		this.descSource = reqSource;
	}
	public String getDescTarget() {
		return descTarget;
	}
	public void setDescTarget(String reqTarget) {
		this.descTarget = reqTarget;
	}
	public  String toString (){
		return "("+descSource+","+descTarget+")";
	}

	public Boolean existKeyReverse (Map<DescCouple, Double> similarityMap){
		
		Set listKeys=similarityMap.keySet();
		Iterator iterator =listKeys.iterator();
		
		while(iterator.hasNext())
		{
			Object key= iterator.next();
		    if (key.toString().equals("("+descTarget+","+descSource+")"))
			return true;
		}
		
		return false;
		
	}
	}