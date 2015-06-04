package fr.matrixminer.engine.termsclustering;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge implements Comparable<Edge> {

	Node from, to;
	private double weight;

	public Edge() {
		super();
	}

	public Edge(final Node argFrom, final Node argTo, final int argWeight) {
		from = argFrom;
		to = argTo;
		weight = argWeight;
	}

	public int compareTo(final Edge argEdge) {
		return (int) (weight - argEdge.weight);
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		weight = w;
	}

	@Override
	public String toString() {
		return String.format("%.2f", getWeight());
	}
}