package fr.matrixminer.engine.termsclustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import fr.matrixminer.engine.termsmining.CustomStringList;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;

public class FeatureTextClustering {

	private LatentSemanticAnalysis lsa;
	protected Levenshtein levenshtein;
	protected SmithWaterman smithWaterman;
	private Map<FeatureCouple, Double> similarityTextMap = new HashMap<FeatureCouple, Double>();
	CustomStringList textFeatures = new CustomStringList();
	private static String RCode = "RCode\\";
	private static String textPath = "RResults\\TextFeatures\\";
	private Map<String, String> lemmaTextFeatures = new HashMap<String, String>();
	private File directory;

	public FeatureTextClustering(File directory) {
		this.directory = directory;
		textPath = directory.getAbsolutePath() + "\\TextFeatures\\";
	}

	public Map<List<String>, String> clusterTextFeatures(
			CustomStringList textFeat, File dir) throws IOException,
			InterruptedException, ClassNotFoundException {
		textFeatures = textFeat;
		SimFeatures();
		JTabbedPane onglets = new JTabbedPane();

		TextFRGDemo FRGContentPane = new TextFRGDemo(similarityTextMap,
				textFeatures, directory);
		FRGContentPane.setOpaque(true); // content panes must be opaque
		onglets.addTab("FRG", null, FRGContentPane,
				"Features relationship graph");

		ClusteringSimTextFeaturesDemo clusterContentPane = new ClusteringSimTextFeaturesDemo(
				similarityTextMap, textFeatures,
				FRGContentPane.getFeaturesTrans(),
				FRGContentPane.getLemmaFeatTrans(),
				FRGContentPane.getLemmaTextFeatures(), directory);
		clusterContentPane.setOpaque(true); // content panes must be opaque
		onglets.addTab("Cliques with threshold 0.7", null, clusterContentPane,
				null);
		ProcessBuilder pb = new ProcessBuilder(
				"C:\\Program Files\\R\\R-3.1.2\\bin\\Rscript",
				"C:\\Users\\sbennasr\\FinalSpace\\VMiner\\RCode\\textClustersFIS.R",
				dir.getAbsolutePath());
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
		Map<List<String>, String> FIS = getCommonPartByCluster(textFeatures,
				textPath + "clustersFIS.csv");
		lemmaTextFeatures = FRGContentPane.getLemmaTextFeatures();
		return FIS;
	}

	private Map<List<String>, String> getCommonPartByCluster(
			CustomStringList valFeatures, String csvFile)
			throws FileNotFoundException, IOException {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		String itemSet;
		Map<List<String>, String> FIS = new HashMap<List<String>, String>();

		try {

			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();

			while ((line = br.readLine()) != null) {

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
					List<String> filteredFIS = new ArrayList<String>();
					filteredFIS.addAll(stringSet);

					for (Iterator<String> iterator = filteredFIS.iterator(); iterator
							.hasNext();) {
						String s = iterator.next();
						s = s.trim();
						if (isSubString(filteredFIS, s)) {
							iterator.remove();
						}
					}
					commonItemList = getMaxItem(filteredFIS);

				}
				List<String> filteredCluster = new ArrayList<String>();
				String valueCluster = clusterFIS[1].replace("list", "")
						.replaceAll("[\"()]", "");
				itemSplit2 = valueCluster.split(",");
				Set<String> clusterSet = new HashSet<String>(
						Arrays.asList(itemSplit2));

				filteredCluster.addAll(clusterSet);
				for (String f : filteredCluster) {
					f = f.trim();
				}

				filteredCluster = trim(filteredCluster);
				if (commonItemList.size() == 1) {
					String commonItem = commonItemList.get(0);
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

		levenshtein = new Levenshtein();
		Object[] array = textFeatures.toArray();

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				String source = array[i].toString();
				String target = array[j].toString();
				if (i != j) {
					FeatureCouple coupleKey = new FeatureCouple(source, target);
					if ((!similarityTextMap.containsKey(coupleKey))
							&& (coupleKey.existKeyReverse(similarityTextMap) == false)) {
						similarityTextMap.put(coupleKey, (double) levenshtein
								.getSimilarity(source, target));

					}
				}
			}
		}

		Set listKeys = similarityTextMap.keySet();
		Iterator iterator = listKeys.iterator();
		double sim;
		String source;
		String target;
		FeatureCouple key;
		while (iterator.hasNext()) {
			key = (FeatureCouple) iterator.next();
			sim = similarityTextMap.get(key);
			if (sim >= 0.60) {
				source = key.getFeatureSource();
				target = key.getFeatureTarget();
			}

		}

	}

	public Map<String, String> getLemmaTextFeatures() {
		return lemmaTextFeatures;
	}

}
