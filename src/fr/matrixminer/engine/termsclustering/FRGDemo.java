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

public class FRGDemo extends JPanel implements ActionListener{
	 private int newNodeSuffix = 1;
	 private FRG graphPanel;
	 List<Set<Node>> cliques;
	 Map<String,CustomStringList> featureTrans,lemmaFeatTrans; 
	 Map<String, String> lemmaValFeatures;
	
	public FRGDemo(Map<FeatureCouple, Double> similarityMap, CustomStringList valFeatures, File directory) throws IOException, ClassNotFoundException{
		 super(new BorderLayout());
		 // Create the components.
		 
		    graphPanel = new FRG(similarityMap, valFeatures, directory);
		    cliques = graphPanel.getCliques();
		    featureTrans=graphPanel.getFeatureTrans();
		    lemmaFeatTrans = graphPanel.getLemmaFeatTrans();
		    lemmaValFeatures = graphPanel.getLemmaValFeatures();
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
	public Map<String, String> getLemmaValFeatures() {
		return lemmaValFeatures;
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
