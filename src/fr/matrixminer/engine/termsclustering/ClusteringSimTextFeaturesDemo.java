package fr.matrixminer.engine.termsclustering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import fr.matrixminer.engine.termsmining.CustomStringList;

public class ClusteringSimTextFeaturesDemo extends JPanel implements
		ActionListener {
	private int newNodeSuffix = 1;
	private ClusteringSimTextFeatures graphPanel;
	List<Set<Node>> cliques;

	public ClusteringSimTextFeaturesDemo(
			Map<FeatureCouple, Double> similarityMap,
			CustomStringList textFeatures,
			Map<String, CustomStringList> featureTrans,
			Map<String, CustomStringList> lemmaFeatTrans,
			Map<String, String> lemmaTextFeatures, File directory)
			throws IOException, ClassNotFoundException {
		super(new BorderLayout());
		graphPanel = new ClusteringSimTextFeatures(similarityMap, textFeatures,
				featureTrans, lemmaFeatTrans, lemmaTextFeatures, directory);
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
