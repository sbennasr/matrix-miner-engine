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

public class TextValFRGDemo extends JPanel implements ActionListener{
	 private int newNodeSuffix = 1;
	 private TextValFRG graphPanel;
	 List<Set<Node>> cliques;
	 Map<String,CustomStringList> featureTrans,lemmaFeatTrans; 
	 Map<String, String> lemmaTextValFeatures;
	
	public TextValFRGDemo(Map<FeatureCouple, Double> similarityMap, CustomStringList textValFeatures, File directory) throws IOException, ClassNotFoundException{
		 super(new BorderLayout());
		 // Create the components.
		 
		    graphPanel = new TextValFRG(similarityMap, textValFeatures, directory);
		    cliques = graphPanel.getCliques();
		    featureTrans=graphPanel.getFeatureTrans();
		    lemmaFeatTrans = graphPanel.getLemmaFeatTrans();
		    lemmaTextValFeatures = graphPanel.getLemmaTextValFeatures();
		    // Lay everything out.
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
	public Map<String, String> getLemmaTextValFeatures() {
		return lemmaTextValFeatures;
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
