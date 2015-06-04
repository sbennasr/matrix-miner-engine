package fr.matrixminer.engine.termsmining;

import java.text.DecimalFormat;


public class Candid {

	private String nameSW;
	private Integer len;
	private CValueTriple triple;
	private Double cVal;
	private Double nVal;
	private Double ncVal; 
	
	

	public Candid(String name, Integer len) {
		this.nameSW = name;
		this.len = len;
		this.triple = new CValueTriple();
		this.cVal  = 0.0;
		this.nVal  = 0.0;
		this.ncVal = 0.0;
		
	}

	public String getNameSW() {
		return nameSW;
	}

	public void setNameSW(String nameSW) {
		this.nameSW = nameSW;
	}

	static double log2(Integer len) {
		return (double) (Math.log(len) / Math.log(2));
	}

	@Override
	public String toString() {
		return "Candid [nameSW=" + nameSW + ", len=" + len + ", triple="
				+ triple + ", cVal=" + cVal + ", nVal=" + nVal + ", ncVal="
				+ ncVal + "]";
	}

	public void setCValue() {
		cVal = (double) roundFourDecimals(log2(len) * ((double) triple.getFreq() - ((double) triple.getFreqNested() / (double) triple.getLongerNb())));
	}

	public void setCValueMaxLen() {
		cVal = (double) roundFourDecimals(log2(len) * triple.getFreq());
	}
	
	public void increNVal(double freqContext, double weight){
		nVal+=(double) freqContext * (double) weight;
	}

	public double roundFourDecimals(double d) {
	    DecimalFormat twoDForm = new DecimalFormat("#.####");
	    return Double.valueOf(twoDForm.format(d).replaceAll(",", "."));
	}
	
	public String getName() {
		return nameSW;
	}

	public void setName(String name) {
		this.nameSW = name;
	}

	public CValueTriple getTriple() {
		return triple;
	}

	public void setTriple(CValueTriple triple) {
		this.triple = triple;
	}

	public Double getcVal() {
		return cVal;
	}

	public void setcVal(Double cVal) {
		this.cVal = cVal;
	}
	
	public Double getnVal() {
		return nVal;
	}

	public void setnVal(Double nVal) {
		this.nVal = nVal;
	}

	public Double getNcVal() {
		return ncVal;
	}

	public void setNcVal() {
		this.ncVal = 0.8 * cVal + 0.2 * nVal;
	}
	public void setNcVal2() {
		this.ncVal = 0.5 * cVal + 0.5 * nVal;
	}
	public Integer getLen() {
		return len;
	}

	public void setLen(Integer len) {
		this.len = len;
	}

}
