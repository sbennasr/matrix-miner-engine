package fr.matrixminer.engine.termsmining;

public class CValueTriple {

	private Integer freq;
	private Integer freqNested;
	private Integer longerNb;
	
	public CValueTriple() {
		this.freq = 0;
		this.freqNested = 0;
		this.longerNb = 0;
	}

	@Override
	public String toString() {
		return "triple [freq=" + freq + ", freqNested=" + freqNested
				+ ", longerNb=" + longerNb + "]";
	}
	public void increFreq(){
		freq++;
	}
	public void increFreqNested(Integer freqLonger, Integer freqLongerNested){
		freqNested+=freqLonger-freqLongerNested;
	}
	public void increLongerNb(){
		longerNb++;
	}
	
	public Integer getFreq() {
		return freq;
	}
	
	public void setFreq(Integer freq) {
		this.freq = freq;
	}


	public Integer getFreqNested() {
		return freqNested;
	}


	public void setFreqNested(Integer freqNested) {
		this.freqNested = freqNested;
	}


	public Integer getLongerNb() {
		return longerNb;
	}


	public void setLongerNb(Integer longerNb) {
		this.longerNb = longerNb;
	}


	
	
	
}
