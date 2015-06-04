package fr.matrixminer.engine.descripclustering;

import java.awt.Container;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.common.Similarity;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import edu.ucla.sspace.matrix.NoTransform;
import edu.ucla.sspace.matrix.factorization.SingularValueDecompositionLibC;
import fr.matrixminer.engine.termsmining.FilesManip;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;

public class DescripClusteringTest {
	protected static Levenshtein levenshtein;
	protected static SmithWaterman smithWaterman;
	private static LatentSemanticAnalysis lsa;
	
	private static Map<DescCouple, Double> similarityMap = new HashMap<DescCouple, Double>();
	private static Map<File, HashMap<String, String>> descripMap = new HashMap<File, HashMap<String, String>>();
	private static HashMap<String, String> tempDescrips = new HashMap<String, String>();
	private static List<File> plFiles = new ArrayList<File>();
	private static String RCode = "RCode\\";
	private static String RResults = "RResults\\";

	static public void main(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException {
		JFrame frame = new JFrame("Clustering Descriptors");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		scanDir("Camera");
		seekDescriptors();
	
		JTabbedPane onglets = new JTabbedPane();
		Set<File> filePaths = descripMap.keySet();
		for (Iterator<File> it = filePaths.iterator(); it.hasNext();) {
			File path = it.next();
			DRGDemo RRGContentPane = new DRGDemo(similarityMap,
					descripMap.get(path));
			RRGContentPane.setOpaque(true); 
			onglets.addTab("RRG", null, RRGContentPane,
					"Requirements relationship graph");

			ClusteringSimDescDemo clusterContentPane = new ClusteringSimDescDemo(
					similarityMap, descripMap.get("Camera\\test"),
					RRGContentPane.getDescTrans());
			clusterContentPane.setOpaque(true);
			onglets.addTab("Cliques with threshold 0.7", null,
					clusterContentPane, null);

			Process p = Runtime.getRuntime().exec("Rscript "+RCode+"renamingClusters.R");
			String s = null;
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			p.waitFor();
			renameFeatures(tempDescrips,RResults+"clustersFIS.csv");
			Container contentPane = frame.getContentPane();
			contentPane.add(onglets);
			frame.pack();
			frame.setVisible(true);
		}

	}

	private static void SimDescripSmithWaterman() {
		Set<File> filePaths = descripMap.keySet();
		for (Iterator<File> it = filePaths.iterator(); it.hasNext();) {
			// File test
			File path = it.next();
			Set<String> descKeys = descripMap.get(path).keySet();
			Iterator<String> iter = descKeys.iterator();
			String descKey, descValue;
			// cluster tempDescrips
			System.out.println(descKeys.size());
			Object[] array = descKeys.toArray();
			System.out.println(array.toString());
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array.length; j++) {
					String source = array[i].toString();
					String target = array[j].toString();
					if (i != j) {
						DescCouple coupleKey = new DescCouple(source, target);
						if ((!similarityMap.containsKey(coupleKey))
								&& (coupleKey.existKeyReverse(similarityMap) == false)) {
							/*similarityMap.put(coupleKey,
									smithWaterman.similarity(source, target));*/

						}
					}
				}
			}

			Set listKeys = similarityMap.keySet();
			Iterator iterator = listKeys.iterator();
			double sim;
			String source;
			String target;
			DescCouple key;
			System.out
					.println("\n *****************************Sim(Ri,Rj) >= 0.40********************************** \n");
			while (iterator.hasNext())

			{
				key = (DescCouple) iterator.next();
				sim = similarityMap.get(key);
				source = key.getDescSource();
				target = key.getDescTarget();
				System.out.println(key.getDescSource() + ":"
						+ descripMap.get(path).get(source));
				System.out.println(key.getDescTarget() + ":"
						+ descripMap.get(path).get(target));
				System.out.println("COSINE" + key.toString() + " = "
						+ similarityMap.get(key) + "\n");

			}
		}

	}

	private static void SimDescripLevenshtein() {

		Set<File> filePaths = descripMap.keySet();
		for (Iterator<File> it = filePaths.iterator(); it.hasNext();) {
			// File test
			File path = it.next();
			Set<String> descKeys = descripMap.get(path).keySet();
			Iterator<String> iter = descKeys.iterator();
			String descKey, descValue;
			// cluster tempDescrips
			System.out.println(descKeys.size());
			Object[] array = descKeys.toArray();
			System.out.println(array.toString());
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array.length; j++) {
					String source = array[i].toString();
					String target = array[j].toString();
					if (i != j) {
						DescCouple coupleKey = new DescCouple(source, target);
						if ((!similarityMap.containsKey(coupleKey))
								&& (coupleKey.existKeyReverse(similarityMap) == false)) {
						/*	similarityMap.put(coupleKey, similarityMap.put(
									coupleKey,
									levenshtein.similarity(source, target)));
*/
						}
					}
				}
			}

			Set listKeys = similarityMap.keySet();
			Iterator iterator = listKeys.iterator();
			double sim;
			String source;
			String target;
			DescCouple key;
			System.out
					.println("\n *****************************Sim(Ri,Rj) >= 0.40********************************** \n");
			while (iterator.hasNext())

			{
				key = (DescCouple) iterator.next();
				sim = similarityMap.get(key);
				source = key.getDescSource();
				target = key.getDescTarget();
				System.out.println(key.getDescSource() + ":"
						+ descripMap.get(path).get(source));
				System.out.println(key.getDescTarget() + ":"
						+ descripMap.get(path).get(target));
				System.out.println("COSINE" + key.toString() + " = "
						+ similarityMap.get(key) + "\n");

			}
		}

	}

	public static void scanDir(String string) {
		FilesManip manip = new FilesManip();
		manip.addTree(new File(string), plFiles);
		System.out.println(plFiles);
		System.out.println(plFiles.size());
	}

	public static void seekDescriptors() throws IOException {
		int lineCount = 0;
		String line;
		File path;

		for (int i = 0; i < plFiles.size(); i++) {
			path = plFiles.get(i);
			HashMap<String, String> descriptors = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(new FileReader(path));
			try {
				while ((line = br.readLine()) != null) {
					System.out.println("D" + i + "." + lineCount + ":" + line);
					descriptors.put("D" + i + "." + lineCount, line);
					lineCount++;
				}
			} finally {
				br.close();
			}
			descripMap.put(path, descriptors);
			lineCount = 0;
		}

		System.out.println(descripMap + "\n");

	}

	public static void descripClustering() throws IOException {

		Set<File> filePaths = descripMap.keySet();

		Set<File> remainingPaths = filePaths;
		System.out.println(remainingPaths + "\n");

		for (Iterator<File> i = remainingPaths.iterator(); i.hasNext();) {
			File path = i.next();
			Set<String> descripKey = descripMap.get(path).keySet();
			Iterator<String> it = descripKey.iterator();
			if (it.hasNext()) {
				String descrip = it.next();
				tempDescrips.put(descrip, descripMap.get(path).get(descrip));
			}
			remainingDescrips(path);
			clusterDescrip();
			System.out.println(tempDescrips);
			i.remove();
			System.out.println(remainingPaths + "\n");
			break;

		}
	}

	public static void clusterDescrip() throws IOException {

		Set<String> descKeys = tempDescrips.keySet();
		Iterator<String> it = descKeys.iterator();
		String descKey, descValue;
		System.out.println(tempDescrips.size());
		lsa = new LatentSemanticAnalysis(true, tempDescrips.size(),
				new NoTransform(), new SingularValueDecompositionLibC(), false,
				new StringBasisMapping());
		while (it.hasNext()) {
			descKey = it.next();
			descValue = tempDescrips.get(descKey);
			lsa.processDocument(new BufferedReader(new StringReader(descValue
					.toLowerCase())));
		}
		lsa.processSpace(System.getProperties());
		Object[] array = descKeys.toArray();

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				String source = array[i].toString();
				String target = array[j].toString();
				if ((i != j) && (source.charAt(1) != target.charAt(1))) {
					DescCouple coupleKey = new DescCouple(source, target);
					if ((!similarityMap.containsKey(coupleKey))
							&& (coupleKey.existKeyReverse(similarityMap) == false)) {
						similarityMap.put(coupleKey, Similarity.getSimilarity(
								Similarity.SimType.COSINE,
								lsa.getDocumentVector(i),
								lsa.getDocumentVector(j)));

					}
				}
			}
		}

		Set listKeys = similarityMap.keySet();
		Iterator iterator = listKeys.iterator();
		double sim;
		String source;
		String target;
		DescCouple key;
		System.out
				.println("\n *****************************Sim(Ri,Rj) >= 0.40********************************** \n");
		while (iterator.hasNext())

		{
			key = (DescCouple) iterator.next();
			sim = similarityMap.get(key);
			if (sim > 0.40) {
				source = key.getDescSource();
				target = key.getDescTarget();

				System.out.println(key.getDescSource() + ":"
						+ tempDescrips.get(source));
				System.out.println(key.getDescTarget() + ":"
						+ tempDescrips.get(target));
				System.out.println("COSINE" + key.toString() + " = "
						+ similarityMap.get(key) + "\n");
			}

		}

	}

	public static void SimDescripLSA() throws IOException {
		Set<File> filePaths = descripMap.keySet();
		for (Iterator<File> it = filePaths.iterator(); it.hasNext();) {
			File path = it.next();
			Set<String> descKeys = descripMap.get(path).keySet();
			Iterator<String> iter = descKeys.iterator();
			String descKey, descValue;
			System.out.println(descKeys.size());
			lsa = new LatentSemanticAnalysis(true, descKeys.size(),
					new NoTransform(), new SingularValueDecompositionLibC(),
					false, new StringBasisMapping());
			while (iter.hasNext()) {
				descKey = iter.next();
				descValue = descripMap.get(path).get(descKey);
				lsa.processDocument(new BufferedReader(new StringReader(
						descValue.toLowerCase())));
			}
			lsa.processSpace(System.getProperties());
			Object[] array = descKeys.toArray();
			System.out.println(array.toString());
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array.length; j++) {
					String source = array[i].toString();
					String target = array[j].toString();
					if (i != j) {
						DescCouple coupleKey = new DescCouple(source, target);
						if ((!similarityMap.containsKey(coupleKey))
								&& (coupleKey.existKeyReverse(similarityMap) == false)) {
							similarityMap.put(coupleKey, Similarity
									.getSimilarity(Similarity.SimType.COSINE,
											lsa.getDocumentVector(i),
											lsa.getDocumentVector(j)));

						}
					}
				}
			}

			Set listKeys = similarityMap.keySet();
			Iterator iterator = listKeys.iterator();
			double sim;
			String source;
			String target;
			DescCouple key;
			System.out
					.println("\n *****************************Sim(Ri,Rj) >= 0.40********************************** \n");
			while (iterator.hasNext())

			{
				key = (DescCouple) iterator.next();
				sim = similarityMap.get(key);
				if (sim > 0.30) {
					source = key.getDescSource();
					target = key.getDescTarget();

					System.out.println(key.getDescSource() + ":"
							+ descripMap.get(path).get(source));
					System.out.println(key.getDescTarget() + ":"
							+ descripMap.get(path).get(target));
					System.out.println("COSINE" + key.toString() + " = "
							+ similarityMap.get(key) + "\n");
				}

			}
		}

	}

	public static void remainingDescrips(File filePath) {
		Set<File> keys = descripMap.keySet();
		Iterator<File> it = keys.iterator();
		File path;
		while (it.hasNext()) {
			path = it.next();
			if (!path.equals(filePath)) {
				tempDescrips.putAll(descripMap.get(path));
			}
		}
	}

	private static void renameFeatures(Map<String, String> reqMap,
			String csvFile) throws FileNotFoundException, IOException {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		String req;
		String itemSet;

	

		try {

			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			Pattern p = Pattern.compile("\\{[a-zA-Z0-9-,]*[a-zA-Z0-9-,]\\}");
			while ((line = br.readLine()) != null) {
				List<String[]> FIS = new ArrayList<String[]>();
				List<String[]> REQ = new ArrayList<String[]>();
				String[] itemSplit;
				String[] reqSplit;
				System.out
						.println("****************************************Next cluster*******************************************");
		
				String[] clusterFIS = line.split(cvsSplitBy);
				System.out.println("ClusterFIS [cluster= " + clusterFIS[1]
						+ " , FIS=" + clusterFIS[2] + "]\n");
				
				String value = clusterFIS[2].replace("list", "")
						.replace(".", ",").replaceAll("[\"()]", "");
			
				Matcher matcher = p.matcher(value);
				while (matcher.find()) {
					itemSet = matcher.group();
					
					itemSplit = itemSet.replaceAll("[{}]", "").split(",");
					Set<String> stringSet = new HashSet<String>(
							Arrays.asList(itemSplit));
					String[] filteredFIS = stringSet.toArray(new String[0]);
					
					for (String s : filteredFIS) {
						s = s.trim();
					}
					FIS.add(filteredFIS);
				}

				String[] cluster = clusterFIS[1].replace("list", "")
						.replaceAll("[\"()]", "").split(",");
				for (String reqID : cluster) {
					req = reqMap.get(reqID.trim());
					
					reqSplit = req.replaceAll("[().,;]", "").split(" ");
		
					for (String split : reqSplit) {
						// System.out.println(split + ", ");
					}
					REQ.add(reqSplit);

				}
				for (String[] fis : FIS) {
					if (fis != null) {
					
						for (String s : fis) {
							System.out.print(s + ", ");
						}
						for (String[] rplit : REQ) {

						
							for (String r : rplit) {
								System.out.print(r + ", ");
							}

							
						}
					}
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

		System.out.println("Done");
	}

}
