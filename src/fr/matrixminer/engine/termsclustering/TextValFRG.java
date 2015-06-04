package fr.matrixminer.engine.termsclustering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.text.BreakIterator;
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
import fr.matrixminer.engine.termsmining.CSV;
import fr.matrixminer.engine.termsmining.CustomStringList;
import fr.matrixminer.engine.termsmining.Lemmatizer;
import fr.matrixminer.engine.termsmining.StopWords;
import fr.matrixminer.test.MatrixMinerTest;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TextValFRG extends JPanel {
	private final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private final Dimension DEFAULT_SIZE = new Dimension(530, 320);
	private List<Node> existingVertexs = new ArrayList<Node>();
	private JGraphModelAdapter m_jgAdapter;
	protected JGraph myGraph;
	private int compteur = 1;
	private List<Set<Node>> cliques;
	private Map<String, List<String>> featureTags = new HashMap<String, List<String>>();
	private Map<String, CustomStringList> featureTrans = new HashMap<String, CustomStringList>();
	private  Map<String, CustomStringList> lemmaFeatTrans = new HashMap<String, CustomStringList>();
	private  Map<String, String> lemmaTextValFeatures = new HashMap<String, String>();
	List<Pattern> patternsList = new ArrayList<Pattern>();
	List<Pattern> patternsLemma = new ArrayList<Pattern>();
	private static String textValPath = "RResults\\TextValFeatures\\";
	// Noun
	   Pattern pattern0 = Pattern.compile("[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
	
	// (Noun)+ Noun
		Pattern pattern1 = Pattern
					.compile("(([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+)+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		Pattern pattern11 = Pattern
				.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		Pattern pattern12 = Pattern
				.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		Pattern pattern13 = Pattern
				.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		Pattern pattern14 = Pattern
				.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
	// (Adj|Noun)+ Noun
		  Pattern pattern2 = Pattern
								.compile("(([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		  
		  Pattern pattern21 = Pattern
					.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		  Pattern pattern22 = Pattern
					.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
		  Pattern pattern23 = Pattern
					.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW|/NNP|/NNPS)");
	// Noun
		   Pattern patt0 = Pattern.compile("[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
		
	// (Noun)+ Noun
				Pattern patt1 = Pattern
						.compile("(([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+)+[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
				Pattern patt11 = Pattern
						.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
				Pattern patt12 = Pattern
						.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
				Pattern patt13 = Pattern
						.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
				Pattern patt14 = Pattern
						.compile("([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+([a-zA-Z0-9-.]+)(/NN|/NNS|/FW)\\s+[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
	
	public TextValFRG(Map<FeatureCouple, Double> similarityMap,
			CustomStringList textValFeatures, File directory) throws IOException,
			ClassNotFoundException {
		super(new GridLayout(1, 0));
		textValPath = directory.getAbsolutePath() + "\\TextValFeatures\\";
		ListenableUndirectedWeightedGraph myAnalysisGraph = new ListenableUndirectedWeightedGraph(
				Edge.class);
		m_jgAdapter = new JGraphModelAdapter(myAnalysisGraph);
		JGraph myGraph = new JGraph(m_jgAdapter);
		Set listKeys = similarityMap.keySet();
		Iterator iterator = listKeys.iterator();

		FeatureCouple key;
		while (iterator.hasNext()) {

			key = (FeatureCouple) iterator.next();
			Node source = seekVertex(key.getFeatureSource(), myAnalysisGraph);
			Node destination = seekVertex(key.getFeatureTarget(),
					myAnalysisGraph);
			myAnalysisGraph.addEdge(source, destination);
			myAnalysisGraph.setEdgeWeight(
					myAnalysisGraph.getEdge(source, destination),
					similarityMap.get(key));
			Edge edge = (Edge) myAnalysisGraph.getEdge(source, destination);
			edge.setWeight(similarityMap.get(key));

		}

		BronKerboschCliqueFinder finder = new BronKerboschCliqueFinder(
				myAnalysisGraph);
		cliques = (List<Set<Node>>) finder.getAllMaximalCliques();
		MaxentTagger tagger = new MaxentTagger(
				"taggers\\bidirectional-distsim-wsj-0-18.tagger");

		String[] patterns = { "/NNPS", "/NNP", "/NNS", "/NN", "/JJR", "/JJ",
				"/DT", "/IN", "/VBN", "/VBG", "/VBP", "/CD", "/RB", "/SYM",
				"/''", "/TO" ,"/FW"};

		patternsList.add(pattern0);
		patternsList.add(pattern1);
		patternsList.add(pattern11);
		patternsList.add(pattern12);
		patternsList.add(pattern13);
		patternsList.add(pattern14);
		patternsList.add(pattern2);
		patternsList.add(pattern21);
		patternsList.add(pattern22);
		patternsList.add(pattern23);
		patternsLemma.add(patt0);
		patternsLemma.add(patt1);
		patternsLemma.add(patt11);
		patternsLemma.add(patt12);
		patternsLemma.add(patt13);
		patternsLemma.add(patt14);
		
		StopWords stopwords = new StopWords();
		Lemmatizer lemmatizer = new Lemmatizer();
		String nounGroupsTag, nounGroupTempTag, nounPartTag;
		String nounGroupsTrans = null, nounPartTrans = null;
		String lemmaTrans = null,lemmaPartTrans,lemmaFeature = null;
		int beginIndex, endIndex ;
		String trans, partTrans;
		for (String feature : textValFeatures) {

			List<String> newFeatureTags = new ArrayList<String>();
			CustomStringList newFeatureTrans = new CustomStringList();
			CustomStringList lemmaFeatureTrans = new CustomStringList(); 
			String taggedFeature = tagger.tagString(feature);
			
			for (Pattern p : patternsList) {
				Matcher matcher = p.matcher(taggedFeature);
				while (matcher.find()) {
					nounGroupsTag = matcher.group();
					nounGroupsTag = nounGroupsTag.trim();
					nounGroupTempTag =nounGroupsTag;
					newFeatureTags.add(nounGroupsTag);
					
					for (int i = 0; i < patterns.length; i++) {
						nounGroupsTrans = nounGroupTempTag.replaceAll(patterns[i],"");
						nounGroupTempTag=nounGroupsTrans;
					}

					nounGroupsTrans = nounGroupsTrans.toLowerCase().trim();
					
					stopwords.setStopWords("stoplists\\en.txt");
					Set<String> stopList = stopwords.getStopWords();
					
					if (p.equals(pattern0)){
						if (!stopList.contains(nounGroupsTrans)) {
							if (!newFeatureTrans.contains(nounGroupsTrans)) { 
								newFeatureTrans.add(nounGroupsTrans);
							}
						}
						
					} 
					else {
						if (!newFeatureTrans.contains(nounGroupsTrans)) {
							newFeatureTrans.add(nounGroupsTrans);
						}
						
					}
					for (Pattern patt : patternsLemma) {
						Matcher mat = patt.matcher(nounGroupsTag);
						while (mat.find()) {
							 nounPartTag = matcher.group();
							nounPartTag = nounPartTag.trim();
							
							for (int i = 0; i < patterns.length; i++) {
								nounPartTrans = nounPartTag.replaceAll(patterns[i],"");
								nounPartTag=nounPartTrans;
							}
							nounPartTrans = nounPartTrans.toLowerCase().trim();
							lemmaPartTrans = lemmatizer.lemmatizeText(nounPartTrans).trim();
							
							if(nounGroupsTrans.contains(nounPartTrans)){
								partTrans = nounPartTrans;	
								}
								else{
								BreakIterator boundary = BreakIterator.getWordInstance();
								boundary.setText(nounPartTrans);
								String firstWord = MatrixMinerTest.printFirst(boundary, nounPartTrans);
								String lastWord = nounPartTrans.substring(nounPartTrans.lastIndexOf(" ") + 1);
								beginIndex = nounGroupsTrans.indexOf(firstWord);
								endIndex = nounGroupsTrans.indexOf(lastWord) + lastWord.length();
								partTrans = nounGroupsTrans.substring(beginIndex, endIndex); 
								}
								String secondTag = tagger.tagString(lemmaPartTrans);
								if(lemmaPartTrans!=null && partTrans!=null && !secondTag.contains("/VB")&&!secondTag.contains("/VBP")){
								lemmaTrans = nounGroupsTrans.replace(partTrans, lemmaPartTrans); 
								}
								else
								{
								lemmaTrans =nounGroupsTrans;
								lemmaPartTrans = nounPartTrans;
								}
						}
					}
							
					
					
					if(feature.contains(nounGroupsTrans)){
					trans = nounGroupsTrans;	
					}
					else{
					BreakIterator boundary = BreakIterator.getWordInstance();
					boundary.setText(nounGroupsTrans);
					String firstWord = MatrixMinerTest.printFirst(boundary, nounGroupsTrans);
					String lastWord = nounGroupsTrans.substring(nounGroupsTrans.lastIndexOf(" ") + 1);
					beginIndex = feature.indexOf(firstWord);
					endIndex = feature.indexOf(lastWord) + lastWord.length();
					trans = feature.substring(beginIndex, endIndex); 
					}
					
					if(lemmaTrans!=null && trans!=null){
					lemmaFeature = feature.replace(trans, lemmaTrans); 
					}
					else
					{
					lemmaFeature =feature;
					lemmaTrans = nounGroupsTrans;
					}
					if (p.equals(pattern0)){
						if (!stopList.contains(lemmaTrans)) {
							if (!lemmaFeatureTrans.contains(lemmaTrans)) {
								lemmaFeatureTrans.add(lemmaTrans);	
							}
						}
						
					}
					else{
						if (!lemmaFeatureTrans.contains(lemmaTrans)) {
							lemmaFeatureTrans.add(lemmaTrans);
						}
					}
					
					
					

				}
			}
			
			lemmaTextValFeatures.put(feature, lemmaFeature);
			featureTags.put(feature, newFeatureTags);
			if(newFeatureTrans!=null){
			featureTrans.put(feature, newFeatureTrans);
			}
			if(lemmaFeatureTrans!=null){
			lemmaFeatTrans.put(lemmaFeature, lemmaFeatureTrans);
			}
		}
		File dir = new File(textValPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String fileName = "featureTrans";
		File tagFile = new File(dir, fileName + ".csv");
		if (!tagFile.exists()) {
			tagFile.createNewFile();
		}
		generateCsvFile(textValPath+"featureTrans.csv");
		fileName = "cluster1";
		tagFile = new File(dir, fileName + ".csv");
		if (!tagFile.exists()) {
			tagFile.createNewFile();
		}
		generateItemsetsCsvFile(textValPath+"cluster1.csv",featureTrans);
		JScrollPane scrollPane = new JScrollPane(myGraph);
		add(scrollPane);

	}

	private String convertToSingular(String nounGroupsTrans) {
		// TODO Auto-generated method stub
		return null;
	}

	private void generateItemsetsCsvFile(String fileUrl,
			Map<String, CustomStringList> featureTrans)
			throws FileNotFoundException, IOException {

		CSV csv = new CSV();
		csv.open(new File(fileUrl));
		Set keys = featureTrans.keySet();
		Iterator it = keys.iterator();
		int i = 1, j = 1;
		CustomStringList colNames = new CustomStringList();

		csv.put(0, 0, "Features Trans");

		while (it.hasNext()) {
			String key = (String) it.next();
			List<String> itemsets = featureTrans.get(key);
			for (String item : itemsets) {
				if (!colNames.contains(item)) {
					csv.put(j, 0, item);
					colNames.add(item);
					j++;
				}
			}
		}
		keys = featureTrans.keySet();
		it = keys.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			csv.put(0, i, key);
			CustomStringList itemsets = (CustomStringList) featureTrans
					.get(key);

			for (String item : colNames) {
				if (itemsets.contains(item)) {

					csv.put(csv.getColum(item), i, "1");
				} else {

					csv.put(csv.getColum(item), i, "0");
				}
			}
			i++;
		}
		csv.save(new File(fileUrl), ';');
	}

	public Map<String, CustomStringList> getFeatureTrans() {
		return featureTrans;
	}

	private void generateCsvFile(String fileUrl) throws FileNotFoundException,
			IOException {

		CSV csv = new CSV();

		csv.open(new File(textValPath+"featureTrans.csv"));

		csv.put(0, 0, "Features");
		csv.put(1, 0, "ItemSets");
		csv.put(2, 0, "Clusters");
		Set keys = featureTrans.keySet();
		Iterator it = keys.iterator();
		int i = 1;

		while (it.hasNext()) {
			String key = (String) it.next();
			csv.put(0, i, key);
			csv.put(1, i, featureTrans.get(key).toString());
			for (Set<Node> clique : cliques) {
				for (Node n : clique) {
					if (n.toString().equals(key.toString())) {
						csv.put(2, i, clique.toString());
						i++;
					}

				}
			}
		}
		csv.save(new File(textValPath+"featureTrans.csv"),';');
	}

	Node seekVertex(String nom,
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

	public Map<String, CustomStringList> getLemmaFeatTrans() {
		return lemmaFeatTrans;
	}

	public Map<String, String> getLemmaTextValFeatures() {
		return lemmaTextValFeatures;
	}

}
