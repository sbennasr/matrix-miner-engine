package fr.matrixminer.engine.termsclustering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import fr.matrixminer.engine.termsmining.CustomStringList;

public class ClusteringSimBoolFeaturesDemo extends JPanel implements
		ActionListener {
	private int newNodeSuffix = 1;
	private ClusteringSimBoolFeatures graphPanel;
	List<Set<Node>> cliques;

	public ClusteringSimBoolFeaturesDemo(
			Map<FeatureCouple, Double> similarityMap,
			CustomStringList boolFeatures,
			Map<String, CustomStringList> featureTrans,
			Map<String, CustomStringList> lemmaFeatTrans,
			Map<String, String> lemmaBoolFeatures) throws IOException,
			ClassNotFoundException {
		super(new BorderLayout());
		graphPanel = new ClusteringSimBoolFeatures(similarityMap, boolFeatures,
				featureTrans, lemmaFeatTrans, lemmaBoolFeatures);
		cliques = graphPanel.getCliques();
		graphPanel.setPreferredSize(new Dimension(300, 150));
		add(graphPanel, BorderLayout.CENTER);
	}

	public List<Set<Node>> getCliques() {
		return cliques;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
