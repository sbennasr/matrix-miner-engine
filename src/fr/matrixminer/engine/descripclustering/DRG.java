package fr.matrixminer.engine.descripclustering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.ext.JGraphModelAdapter;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import fr.matrixminer.engine.termsclustering.CustomListenableDirectedWeightedGraph;
import fr.matrixminer.engine.termsclustering.Edge;
import fr.matrixminer.engine.termsclustering.Node;
import fr.matrixminer.engine.termsmining.CSV;
import fr.matrixminer.engine.termsmining.CustomStringList;
import fr.matrixminer.engine.termsmining.StopWords;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DRG extends JPanel {
	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
	private static List<Node> existingVertexs = new ArrayList<Node>();
	private JGraphModelAdapter m_jgAdapter;
	protected JGraph myGraph;
	private static int compteur = 1;
	private static List<Set<Node>> cliques;
	private static Map<String, List<String>> descTags = new HashMap<String, List<String>>();
	private static Map<String, CustomStringList> descTrans = new HashMap<String, CustomStringList>();
	List<Pattern> patternsList = new ArrayList<Pattern>();
	private static String RCode = "RCode\\";
	private static String RResults = "RResults\\";
	
	// Noun
	static Pattern pattern0 = Pattern.compile("[a-zA-Z0-9-]+(/NN |/NNS|/NNP )");
	// Noun+ Noun
	static Pattern pattern1 = Pattern
			.compile("([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/CD)\\s+)+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS))");

	// (Adj|Noun)+ Noun
	static Pattern pattern2 = Pattern
			.compile("([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)");

	// (Adj|Noun)+ prep (Adj|Noun)+
	static Pattern pattern3 = Pattern
			.compile("(([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?(([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)))");

	public DRG(Map<DescCouple, Double> similarityMap,
			Map<String, String> descMap) throws IOException,
			ClassNotFoundException {
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
			Node source = seekVertex(key.getDescSource(), myAnalysisGraph);
			Node destination = seekVertex(key.getDescTarget(), myAnalysisGraph);
			
				myAnalysisGraph.addEdge(source, destination);
				myAnalysisGraph.setEdgeWeight(
						myAnalysisGraph.getEdge(source, destination), weight);
			

		}
		BronKerboschCliqueFinder finder = new BronKerboschCliqueFinder(
				myAnalysisGraph);
		cliques = (List<Set<Node>>) finder.getAllMaximalCliques();
		System.out.println("clique1:" + cliques);
		System.out.println("clique1 size:" + cliques.size());
		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger(
				"taggers\\bidirectional-distsim-wsj-0-18.tagger");

		String[] patterns = { "/NNPS", "/NNP", "/NNS", "/NN", "/JJR", "/JJ",
				"/DT", "/IN", "/VBN", "/VBG", "/VBP", "/CD", "/RB" };

		patternsList.add(pattern0);
		patternsList.add(pattern1);
		

		StopWords stopwords = new StopWords();
		Set cles = descMap.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()) {
			List<String> newDescTags = new ArrayList<String>();
			CustomStringList newDescTrans = new CustomStringList();
			String cle = (String) it.next();
			String taggedDesc = tagger.tagString(descMap.get(cle));
			for (Pattern p : patternsList) {
				Matcher matcher = p.matcher(taggedDesc);

				String nounGroupsTag;
				String nounGroupsTrans = null;
				int numGrp = 0;
				while (matcher.find()) {
					nounGroupsTag = matcher.group();
					newDescTags.add(nounGroupsTag);
					for (int i = 0; i < patterns.length; i++) {
						nounGroupsTrans = nounGroupsTag
								.replaceAll(patterns[i], "");
						nounGroupsTag=nounGroupsTrans;
						System.out.println(nounGroupsTrans);
						
					}
					nounGroupsTrans = nounGroupsTrans.toLowerCase();
					if (p.equals(pattern0)) {
						nounGroupsTag = nounGroupsTag.trim();
						nounGroupsTrans = nounGroupsTrans.trim();
						if (nounGroupsTag.contains("/NNS")) {
							nounGroupsTrans = convertToSingular(nounGroupsTrans);
						}
						stopwords.setStopWords("stoplists\\en.txt");
						Set<String> stopList = stopwords.getStopWords();
						if (!stopList.contains(nounGroupsTrans)) {
							if (!newDescTrans.contains(nounGroupsTrans)) {
								newDescTrans.add(nounGroupsTrans);
							}
						}
					} else {
						if (!newDescTrans.contains(nounGroupsTrans)) {
							newDescTrans.add(nounGroupsTrans);
						}
					}
				

				}
			}

			descTags.put(cle.toString(), newDescTags);
			descTrans.put(cle.toString(), newDescTrans);

		}

		System.out.println("cliqTags" + descTags);
		System.out.println("cliqTrans" + descTrans + "\n");
		File dir = new File(RResults);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String fileName = "descTrans";
		File tagFile = new File(dir, fileName + ".csv");
		if (!tagFile.exists()) {
			tagFile.createNewFile();
		}

		

		JScrollPane scrollPane = new JScrollPane(myGraph);
		add(scrollPane);

	}

	public static Map<String, CustomStringList> getDescTrans() {
		return descTrans;
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

				File dir = new File(RResults);
				if (!dir.exists()) {
					dir.mkdir();
				}
				String fileName = "cluster1." + k;
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
	
	public String convertToSingular(String str) {
		if (str.length() > 0 && str.charAt(str.length() - 1) == 's') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

}
