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

public class TextFRGDemo extends JPanel implements ActionListener{
	 private int newNodeSuffix = 1;
	 private TextFRG graphPanel;
	 List<Set<Node>> cliques;
	 Map<String,CustomStringList> featureTrans,lemmaFeatTrans; 
	 Map<String, String> lemmaTextFeatures;
	public TextFRGDemo(Map<FeatureCouple, Double> similarityMap, CustomStringList textFeatures, File directory) throws IOException, ClassNotFoundException{
		 super(new BorderLayout());
		 // Create the components.
		    graphPanel = new TextFRG(similarityMap, textFeatures, directory);
		    cliques = graphPanel.getCliques();
		    featureTrans=graphPanel.getFeatureTrans();
		    lemmaFeatTrans = graphPanel.getLemmaFeatTrans();
		    lemmaTextFeatures = graphPanel.getLemmaTextFeatures();
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
	 
	 public Map<String, String> getLemmaTextFeatures(){
		return lemmaTextFeatures;
		 
	 }
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
