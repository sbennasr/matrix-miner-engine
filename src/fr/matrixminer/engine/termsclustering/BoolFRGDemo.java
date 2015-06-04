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

public class BoolFRGDemo extends JPanel implements ActionListener {
	private int newNodeSuffix = 1;
	private BoolFRG graphPanel;
	List<Set<Node>> cliques;
	Map<String, CustomStringList> featureTrans, lemmaFeatTrans;
	Map<String, String> lemmaBoolFeatures;

	public BoolFRGDemo(Map<FeatureCouple, Double> similarityMap,
			CustomStringList boolFeatures) throws IOException,
			ClassNotFoundException {
		super(new BorderLayout());

		graphPanel = new BoolFRG(similarityMap, boolFeatures);
		cliques = graphPanel.getCliques();
		featureTrans = graphPanel.getFeatureTrans();
		lemmaFeatTrans = graphPanel.getLemmaFeatTrans();
		lemmaBoolFeatures = graphPanel.getLemmaBoolFeatures();

		graphPanel.setPreferredSize(new Dimension(300, 150));
		add(graphPanel, BorderLayout.CENTER);

	}

	public Map<String, CustomStringList> getFeaturesTrans() {
		return featureTrans;
	}

	public List<Set<Node>> getCliques() {
		return cliques;
	}

	public Map<String, CustomStringList> getLemmaFeatTrans() {
		return lemmaFeatTrans;
	}

	public Map<String, String> getLemmaBoolFeatures() {
		return lemmaBoolFeatures;

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
