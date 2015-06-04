package fr.matrixminer.engine.termsclustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import fr.matrixminer.engine.termsmining.CustomStringList;
import fr.matrixminer.test.MatrixMinerTest;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;

public class TextValFeatureClustering {

	private LatentSemanticAnalysis lsa;
	protected Levenshtein levenshtein;
	protected SmithWaterman smithWaterman;
	private Map<FeatureCouple, Double> similarityMap = new HashMap<FeatureCouple, Double>();
	CustomStringList textValFeatures = new CustomStringList();
	JFrame frame;
	private  Map<String, String> lemmaTextValFeatures = new HashMap<String, String>();
	private static String RCode = "RCode\\";
	private static String textValPath = "RResults\\TextValFeatures\\";
	
	
	// Nb+ (adj|Noun)+
		Pattern patternNb = Pattern
				.compile("[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*");
		private File directory;
	
	public TextValFeatureClustering(File directory) {
		this.directory = directory;
		textValPath = directory.getAbsolutePath() + "\\TextValFeatures\\";
	}
		
	public Map<List<String>, String> clusterFeatures(CustomStringList textValFeat, File dir)
			throws IOException, InterruptedException, ClassNotFoundException {
		textValFeatures = textValFeat;
		SimFeatures();
		JTabbedPane onglets = new JTabbedPane();

		TextValFRGDemo FRGContentPane = new TextValFRGDemo(similarityMap, textValFeatures, directory);
		FRGContentPane.setOpaque(true); // content panes must be opaque
		onglets.addTab("FRG", null, FRGContentPane,
				"Features relationship graph");

		ClusteringSimTextValFeaturesDemo clusterContentPane = new ClusteringSimTextValFeaturesDemo(
				similarityMap, textValFeatures, FRGContentPane.getFeaturesTrans(),FRGContentPane.getLemmaFeatTrans(),FRGContentPane.getLemmaTextValFeatures(), directory);
		lemmaTextValFeatures=FRGContentPane.getLemmaTextValFeatures();
		clusterContentPane.setOpaque(true); // content panes must be opaque
		onglets.addTab("Cliques with threshold 0.7", null, clusterContentPane,
				null);
		System.out.println("\nRscript C:\\Users\\sbennasr\\FinalSpace\\VMiner\\RCode\\textValClustersFIS.R");
		ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\R\\R-3.1.2\\bin\\Rscript", "C:\\Users\\sbennasr\\FinalSpace\\VMiner\\RCode\\textValClustersFIS.R", dir.getAbsolutePath());
		Process p = pb.start();
		
		String s = null;
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

		// read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}
		// read any errors from the attempted command
		System.out
				.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}
		p.waitFor();
		Map<List<String>, String> FIS = getCommonPartByCluster(textValFeatures,
				textValPath+"clustersFIS.csv");
		return FIS;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	private Map<List<String>, String> getCommonPartByCluster(
			CustomStringList textValFeatures, String csvFile)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		String itemSet;
		Map<List<String>, String> FIS = new HashMap<List<String>, String>();
		
		try {

			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();

			while ((line = br.readLine()) != null) {
				List<String> filteredFIS = new ArrayList<String>();
				List<String> filteredCluster = new ArrayList<String>();
				String[] itemSplit;
				String[] itemSplit2;
				List<String> commonItemList = new ArrayList<String>();
				Pattern p1 = Pattern.compile("\\{[a-zA-Z0-9-x,.\\s]+\\}");
				String[] clusterFIS = line.split(cvsSplitBy);
				String value = clusterFIS[2].replace("list", "")
						.replace(".", " ").replaceAll("[\"()]", "");
				Matcher matcher = p1.matcher(value);
				while (matcher.find()) {
					itemSet = matcher.group();
					itemSplit = itemSet.replaceAll("[{}]", "").split(",");
					Set<String> stringSet = new HashSet<String>(
							Arrays.asList(itemSplit));
					filteredFIS.addAll(stringSet);
					for (Iterator<String> iterator = filteredFIS.iterator(); iterator
							.hasNext();) {
						String s = iterator.next();
						s = s.trim();
						if (isSubString(filteredFIS, s)) {
							iterator.remove();
						}
					}

				}
				
				Pattern p2 = Pattern.compile("\"\"[a-zA-Z0-9-x,.%°\'\\s]+\"\"");
				String valueCluster = clusterFIS[1].replaceAll("\"list\\(", "").replaceAll("\\)\"", "");
				Matcher matcher2 = p2.matcher(valueCluster);
				while (matcher2.find()) {
					itemSet = matcher2.group();
					itemSet = itemSet.replace("\"", "");
					filteredCluster.add(itemSet);
				}
				for (String f : filteredCluster) {
					f = f.trim();
				}

				filteredCluster = trim(filteredCluster);		
				commonItemList = getCommonPartItem(filteredFIS, filteredCluster);	
				if(commonItemList.size()==1){
				String	commonItem = commonItemList.get(0);
				FIS.put(filteredCluster, commonItem);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return FIS;
	}
	
	private List<String> getCommonPartItem2(List<String> filteredFIS,
			List<String> filteredCluster) {
		String commonPart;
		List<String> commonList = new ArrayList<String>();
		int begin = -1, end = -1;
		
		for(String commonItem : filteredFIS){
			String feature = filteredCluster.get(0);
			
			if (!feature.contains(commonItem)) {

				BreakIterator boundary = BreakIterator.getWordInstance();
				boundary.setText(commonItem);
				String firstword = MatrixMinerTest.printFirst(boundary, commonItem);
				String lastWord = commonItem
						.substring(commonItem.lastIndexOf(" ") + 1);
				 begin = feature.indexOf(firstword);
				 end = feature.indexOf(lastWord) + lastWord.length();
				commonPart = feature.substring(begin, end);
			}
			if (feature.contains(commonItem)) {
				commonPart = commonItem;
				 begin = feature.indexOf(commonItem);
				 end = begin + commonItem.length();
			}
			if (begin > 0 && begin <= end && end <= feature.length()) {
				commonList.add(commonItem);
			}
		
			
		}
		return commonList;
	}

	private List<String> getCommonPartItem(List<String> filteredFIS, List<String> filteredCluster) throws ClassNotFoundException, IOException {
		String[] patterns = { "/NNPS", "/NNP", "/NNS", "/NN", "/JJR", "/JJ",
				"/DT", "/IN", "/VBN", "/VBG", "/VBP", "/CD", "/RB", "/SYM",
				"/''", "/TO", "/FW", "/,", "/.", "-/:", "--/", "\\\\" };
		String nbName, nbTag;
		
		
		MaxentTagger tagger = new MaxentTagger(
				"taggers/bidirectional-distsim-wsj-0-18.tagger");
		
		String feature =filteredCluster.get(0);
		String taggedText = tagger.tagString(feature);

				List<String> nbList = new ArrayList<String>();
				List<String> commonList = new ArrayList<String>();
			
					Matcher matcher = patternNb.matcher(taggedText);
					while (matcher.find()) {
						nbName = matcher.group();
						nbTag = nbName;
						nbName = nbTag;
						// delete tag from candidate name
						for (int i = 0; i < patterns.length; i++) {
							nbName = nbName.replaceAll(patterns[i], "");
						}
						nbName = nbName.replaceAll("(\\s+)?--(\\s+)?",
								"-");
						nbName = nbName.toLowerCase().trim();
						if (!nbList.contains(nbName)) {
							nbList.add(nbName);
						}
					}
					int nbIndex = -1;
					if(nbList.size()==1){
				    nbIndex=feature.indexOf(nbList.get(0));
					}
					String commonPart = "N/A";
					for (String commonCandid :filteredFIS){
					if(feature.indexOf(commonCandid)>nbIndex){
					commonList.add(commonCandid);
					}
					}
				
		return commonList;

	}

	private String diff(String feature, String valuePart) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getMax(List<String> candidList) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> trim(List<String> filteredCluster) {
		List<String> tempList = new ArrayList<String>();
		for (String f : filteredCluster) {
			f = f.trim();
			tempList.add(f);
		}
		return tempList;
	}

	private List<String> getMaxItem(List<String> filteredFIS) {
		int max = seekmax(filteredFIS);
		List<String> maxItem = new ArrayList<String>();
		for (String item : filteredFIS) {
			if (max == item.length()) {
				maxItem.add(item);
			}
		}
		return maxItem;
	}

	public int seekmax(List<String> filteredFIS) {
		int max = 0;
		for (String item : filteredFIS) {
			int size = item.length();
			if (size > max) {
				max = size;
			}
		}
		return max;
	}

	private boolean isSubString(List<String> filteredFIS, String s) {

		boolean isSubStr = false;
		for (String item : filteredFIS) {
			if (!item.equals(s) && item.contains(s)) {
				isSubStr = true;
			}
		}
		return isSubStr;
	}

	public void SimFeatures() throws IOException {
		//System.out.println("\n ***********Levenshtein*******\n");
		levenshtein = new Levenshtein();
		Object[] array = textValFeatures.toArray();

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				String source = array[i].toString();
				String target = array[j].toString();
				if (i != j) {
					FeatureCouple coupleKey = new FeatureCouple(source, target);
					if ((!similarityMap.containsKey(coupleKey))
							&& (coupleKey.existKeyReverse(similarityMap) == false)) {
						similarityMap.put(coupleKey, (double) levenshtein
								.getSimilarity(source, target));

					}
				}
			}
		}

		Set listKeys = similarityMap.keySet();
		Iterator iterator = listKeys.iterator();
		double sim;
		String source;
		String target;
		FeatureCouple key;
		while (iterator.hasNext())

		{
			key = (FeatureCouple) iterator.next();
			sim = similarityMap.get(key);
			if (sim >= 0.40) {
				source = key.getFeatureSource();
				target = key.getFeatureTarget();
			}

		}

	}

	public Map<String, String> getLemmaTextValFeatures() {
		return lemmaTextValFeatures;
	}
	
}
