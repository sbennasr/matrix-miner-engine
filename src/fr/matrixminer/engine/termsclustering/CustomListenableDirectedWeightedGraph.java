package fr.matrixminer.engine.termsclustering;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;

public class CustomListenableDirectedWeightedGraph extends
		ListenableUndirectedWeightedGraph {

	public CustomListenableDirectedWeightedGraph(Class arg0) {
		super(arg0);
	}

	public CustomListenableDirectedWeightedGraph(WeightedGraph arg0) {
		super(arg0);
	}

	public void setEdgeWeight(Edge e, double weight) {
		super.setEdgeWeight(e, weight);
		((Edge) e).setWeight(weight);
	}

}
