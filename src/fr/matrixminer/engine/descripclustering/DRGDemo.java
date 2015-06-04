package fr.matrixminer.engine.descripclustering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import fr.matrixminer.engine.termsclustering.Node;
import fr.matrixminer.engine.termsmining.CustomStringList;

public class DRGDemo extends JPanel implements ActionListener {
	private int newNodeSuffix = 1;
	private DRG graphPanel;
	List<Set<Node>> cliques;
	Map<String, CustomStringList> descTrans;

	public DRGDemo(Map<DescCouple, Double> similarityMap,
			Map<String, String> descMap) throws IOException,
			ClassNotFoundException {
		super(new BorderLayout());
		// Create the components.
		graphPanel = new DRG(similarityMap, descMap);
		cliques = graphPanel.getCliques();
		descTrans = graphPanel.getDescTrans();
		// Lay everything out.
		graphPanel.setPreferredSize(new Dimension(300, 150));
		add(graphPanel, BorderLayout.CENTER);

	}

	public Map<String, CustomStringList> getDescTrans() {
		return descTrans;
	}

	public List<Set<Node>> getCliques() {
		return cliques;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
