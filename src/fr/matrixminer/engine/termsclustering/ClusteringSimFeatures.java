package fr.matrixminer.engine.termsclustering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.ext.JGraphModelAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import fr.matrixminer.engine.termsmining.CSV;
import fr.matrixminer.engine.termsmining.CustomStringList;

public class ClusteringSimFeatures extends JPanel {
	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
	private static List<Node> existingVertexs = new ArrayList<Node>();
	private JGraphModelAdapter m_jgAdapter;
	protected JGraph myGraph;
	private static int compteur = 1;
	private static List<Set<Node>> cliques;
	private static String valPath = "RResults\\ValFeatures\\";

	public ClusteringSimFeatures(Map<FeatureCouple, Double> similarityMap,
			CustomStringList valFeatures,
			Map<String, CustomStringList> featureTrans,
			Map<String, CustomStringList> lemmaFeatTrans,
			Map<String, String> lemmaValFeatures, File directory)
			throws IOException, ClassNotFoundException {
		super(new GridLayout(1, 0));

		valPath = directory.getAbsolutePath() + "\\ValFeatures\\";

		ListenableUndirectedWeightedGraph myAnalysisGraph = new ListenableUndirectedWeightedGraph(
				Edge.class);
		m_jgAdapter = new JGraphModelAdapter(myAnalysisGraph);
		JGraph myGraph = new JGraph(m_jgAdapter);
		Set listKeys = similarityMap.keySet();
		Iterator iterator = listKeys.iterator();
		double weight;
		FeatureCouple key;
		while (iterator.hasNext()) {
			key = (FeatureCouple) iterator.next();
			weight = similarityMap.get(key);
			Node source = seekVertex(key.getFeatureSource(), myAnalysisGraph);
			Node destination = seekVertex(key.getFeatureTarget(),
					myAnalysisGraph);
			if (weight >= 0.60) {
				myAnalysisGraph.addEdge(source, destination);
				myAnalysisGraph.setEdgeWeight(
						myAnalysisGraph.getEdge(source, destination), weight);
			}
		}
		BronKerboschCliqueFinder finder = new BronKerboschCliqueFinder(
				myAnalysisGraph);
		cliques = (List<Set<Node>>) finder.getAllMaximalCliques();
		cliques = removeRedundancy(cliques);

		updateCsvFile(valPath + "featureTrans.csv");
		CsvFileByCluster(featureTrans, cliques, lemmaFeatTrans,
				lemmaValFeatures);
		JScrollPane scrollPane = new JScrollPane(myGraph);
		add(scrollPane);
	}

	static List<Set<Node>> removeRedundancy(List<Set<Node>> cliques) {
		List<Set<Node>> tempCliques = new ArrayList<Set<Node>>();
		Set<Node> tempClique;

		for (Set<Node> clique : cliques) {
			tempClique = new HashSet<Node>();
			for (Node n : clique) {
				if (!tempClique.contains(n)) {
					tempClique.add(n);
				}
			}
			tempCliques.add(tempClique);
		}
		return tempCliques;
	}

	public static boolean isSubStringFeature(Set<Node> clique, Node feature) {
		boolean isSubStr = false;
		Set<Node> features = new HashSet<Node>();
		features.add(feature);
		Set<Node> remainSet = new HashSet<Node>(clique);
		remainSet.removeAll(features);

		String featureName = feature.getName().trim();
		for (Iterator<Node> iter = remainSet.iterator(); iter.hasNext();) {
			Node n = iter.next();
			String nodeName = n.getName().trim();
			if (nodeName.contains(featureName)) {
				isSubStr = true;
				break;
			}
		}
		return isSubStr;
	}

	public static boolean isClusterContainingSubString(Set<Node> clique) {
		boolean isSubStr = false;
		for (Node n : clique) {
			isSubStr = isSubStringFeature(clique, n);
			if (isSubStr == true) {
				return true;
			}
		}
		return isSubStr;
	}

	public static Set<Node> removeSubString(Set<Node> clique) {
		boolean isSubStr = false;
		List<Node> tempList = new ArrayList<Node>();
		for (Node n : clique) {
			isSubStr = ClusteringSimFeatures.isSubStringFeature(clique, n);
			if (isSubStr == true) {
				tempList.add(n);
			}
		}
		clique.removeAll(tempList);
		return clique;
	}

	private static void CsvFileByCluster(
			Map<String, CustomStringList> featureTrans,
			List<Set<Node>> cliques,
			Map<String, CustomStringList> lemmaFeatTrans,
			Map<String, String> lemmaValFeatures) throws FileNotFoundException,
			IOException {

		int i, j, k = 1;
		for (Set<Node> clique : cliques) {

			if (isClusterContainingSubString(clique)) {
				removeSubString(clique);
			}
			if (clique.size() > 1) {
				i = 1;
				j = 1;
				CustomStringList colNames = new CustomStringList();
				CSV csv = new CSV();
				File dir = new File(valPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				String fileName = "cluster2." + k;
				File tagFile = new File(dir, fileName + ".csv");
				if (!tagFile.exists()) {
					tagFile.createNewFile();
				}
				csv.open(tagFile, ';');
				csv.put(0, 0, "Features Trans");
				for (Node n : clique) {
					String feature = n.toString();
					String lemmaFeature = lemmaValFeatures.get(feature);
					CustomStringList itemsets = lemmaFeatTrans
							.get(lemmaFeature);

					for (String item : itemsets) {
						if (!colNames.contains(item)) {
							csv.put(j, 0, item);
							colNames.add(item);
							j++;
						}
					}
				}
				Set<String> tempClique = new HashSet<String>();
				for (Node n : clique) {
					String feature = n.toString();
					String lemmaFeature = lemmaValFeatures.get(feature);
					CustomStringList itemsets = (CustomStringList) lemmaFeatTrans
							.get(lemmaFeature);
					if (!tempClique.contains(lemmaFeature)) {
						csv.put(0, i, lemmaFeature);
						tempClique.add(lemmaFeature);
						for (String item : colNames) {
							if (itemsets.contains(item)) {
								csv.put(csv.getColum(item), i, "1");
							} else {
								csv.put(csv.getColum(item), i, "0");
							}
						}
						i++;
					}
				}

				csv.save(tagFile, ';');
				k++;

				i = 1;
				j = 1;
				String value = csv.get(1, i).trim();
				if ((csv.get(1, i) == null) || value.equals("")) {
					tagFile.delete();
				}

			}
		}
	}

	private static void updateCsvFile(String fileUrl) throws IOException {
		// TODO Auto-generated method stub
		CSV csv = new CSV();
		int i = 1;
		csv.open(new File(valPath + "featureTrans.csv"), ';');
		while (i < csv.rows()) {
			String featureID = csv.get(0, i);
			for (Set<Node> clique : cliques) {
				for (Node n : clique) {
					if (n.getName().equals(featureID.toString())) {
						String clusters = csv.get(2, i);

						if (!(clique.toString()).equals("")) {
							clusters += ", " + clique.toString();
							csv.put(2, i, clusters);

						}
					}
				}
			}
			i++;
		}
		csv.save(new File(valPath + "featureTrans.csv"), ';');
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
