package fr.matrixminer.engine.termsmining;

public class ContextWord {
	
	private Double weight;
	private Integer nbTerms;

	public ContextWord() {
		this.weight = 0.0;
		this.nbTerms = 0;
	}

	@Override
	public String toString() {
		return "ContextWord [weight=" + weight + ", nbTerms=" + nbTerms + "]";
	}

	public Integer getNbTerms() {
		return nbTerms;
	}

	public void setNbTerms(Integer nbTerms) {
		this.nbTerms = nbTerms;
	}

	public Double getWeight() {
		return weight;
	}
	
	public void setWeight(Double weight) {
		this.weight = weight;
	}
}
