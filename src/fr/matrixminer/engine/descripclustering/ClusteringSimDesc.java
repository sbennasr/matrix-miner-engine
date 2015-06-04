package fr.matrixminer.engine.descripclustering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.csvreader.CsvReader;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import fr.matrixminer.engine.termsclustering.CustomListenableDirectedWeightedGraph;
import fr.matrixminer.engine.termsclustering.Edge;
import fr.matrixminer.engine.termsclustering.Node;
import fr.matrixminer.engine.termsmining.CSV;
import fr.matrixminer.engine.termsmining.CustomStringList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClusteringSimDesc extends JPanel {
	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
	private static List<Node> existingVertexs = new ArrayList<Node>();
	private JGraphModelAdapter m_jgAdapter;
	protected JGraph myGraph;
	private static int compteur = 1;
	private static List<Set<Node>> cliques;

	public ClusteringSimDesc(Map<DescCouple, Double> similarityMap,
			Map<String, String> descMap, Map<String, CustomStringList> descTrans)
			throws IOException, ClassNotFoundException {
		super(new GridLayout(1, 0));
		// create a JGraphT graph
		CustomListenableDirectedWeightedGraph myAnalysisGraph = new CustomListenableDirectedWeightedGraph(
				Edge.class);
		// create a visualization using JGraph, via an adapter
		m_jgAdapter = new JGraphModelAdapter(myAnalysisGraph);
		JGraph myGraph = new JGraph(m_jgAdapter);

		// add some sample data (graph manipulated via JGraphT)
		Set listKeys = similarityMap.keySet();
		Iterator iterator = listKeys.iterator();
		double weight;
		DescCouple key;
		while (iterator.hasNext()) {

			key = (DescCouple) iterator.next();
			weight = similarityMap.get(key);
			System.out.println(weight);
			Node source = seekVertex(key.getDescSource(), myAnalysisGraph);
			Node destination = seekVertex(key.getDescTarget(), myAnalysisGraph);
			if (weight >= 0.40) {
				myAnalysisGraph.addEdge(source, destination);
				myAnalysisGraph.setEdgeWeight(
						myAnalysisGraph.getEdge(source, destination),
						similarityMap.get(key));
			}
		}
		BronKerboschCliqueFinder finder = new BronKerboschCliqueFinder(
				myAnalysisGraph);
		cliques = (List<Set<Node>>) finder.getAllMaximalCliques();
		System.out.println("clique3:" + cliques);
		updateCsvFile("RResults\\descTrans.csv", descMap);
		CsvFileByCluster(descTrans, cliques);
		JScrollPane scrollPane = new JScrollPane(myGraph);
		add(scrollPane);

	}

	private static void CsvFileByCluster(
			Map<String, CustomStringList> descTrans, List<Set<Node>> cliques)
			throws FileNotFoundException, IOException {

		int i, j, k = 1;
		for (Set<Node> clique : cliques) {
			if (clique.size() != 1) {
				i = 1;
				j = 1;
				CustomStringList colNames = new CustomStringList();
				CSV csv = new CSV();
				File dir = new File("RResults\\");
				if (!dir.exists()) {
					dir.mkdir();
				}
				String fileName = "cluster3." + k;
				File tagFile = new File(dir, fileName + ".csv");
				if (!tagFile.exists()) {
					tagFile.createNewFile();
				}
				csv.open(tagFile, ';');
				csv.put(0, 0, "Desc Trans");
				for (Node n : clique) {
					CustomStringList itemsets = descTrans.get(n.toString());
					for (String item : itemsets) {
						if (!colNames.contains(item)) {
							csv.put(j, 0, item);
							colNames.add(item);
							j++;
						}
					}
				}

				for (Node n : clique) {
					CustomStringList itemsets = (CustomStringList) descTrans
							.get(n.toString());
					csv.put(0, i, n.toString());
					for (String item : colNames) {
						if (itemsets.contains(item)) {
							csv.put(csv.getColum(item), i, "1");
						} else {
							csv.put(csv.getColum(item), i, "0");
						}
					}
					i++;
				}
				csv.save(tagFile, ';');
				k++;
			}

		}
	}

	private static void updateCsvFile(String fileUrl,
			Map<String, String> descMap) throws IOException {

		CSV csv = new CSV();
		int i = 1;
		csv.open(new File(fileUrl), ';');
		while (i < csv.rows()) {
			String descID = csv.get(0, i);
			for (Set<Node> clique : cliques) {
				for (Node n : clique) {
					if (n.getName().equals(descID.toString())) {
						String clusters = csv.get(2, i);
						System.out.print(clusters);
						if (!(clique.toString()).equals("")) {
							clusters += ", " + clique.toString();
							csv.put(2, i, clusters);

						}
					}
				}
			}
			i++;
		}
		csv.save(new File("RResults\\descTrans.csv"), ';');
	}

	static Node seekVertex(String nom,
			ListenableUndirectedWeightedGraph myAnalysisGraph) {

		boolean existe = false;
		Node newNode = null;
		for (Node n : existingVertexs) {
			if (n.getName().equals(nom) == true) {
				existe = true;
				newNode = n;
				break;
			}
		}
		if (existe == false) {
			Node v = new Node(nom);
			myAnalysisGraph.addVertex(v);
			existingVertexs.add(v);
			newNode = v;
		}
		return newNode;
	}

	private void adjustDisplaySettings(JGraph jg) {
		jg.setPreferredSize(DEFAULT_SIZE);

		Color c = DEFAULT_BG_COLOR;
		String colorStr = null;
		try {

		} catch (Exception e) {
		}
		if (colorStr != null) {
			c = Color.decode(colorStr);
		}
		jg.setBackground(c);
	}

	private void positionVertexAt(Object vertex, int x, int y) {
		DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
		Map attr = cell.getAttributes();
		Rectangle b = (Rectangle) GraphConstants.getBounds(attr);
		GraphConstants.setBounds(attr, new Rectangle(x, y, b.width, b.height));
		Map cellAttr = new HashMap();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);
	}

	public List<Set<Node>> getCliques() {
		return cliques;
	}
}
