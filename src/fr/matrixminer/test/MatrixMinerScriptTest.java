package fr.matrixminer.test;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.matrixminer.engine.termsclustering.FeatureBoolClustering;
import fr.matrixminer.engine.termsclustering.FeatureClustering;
import fr.matrixminer.engine.termsclustering.FeatureTextClustering;
import fr.matrixminer.engine.termsclustering.Node;
import fr.matrixminer.engine.termsclustering.TextValFeatureClustering;
import fr.matrixminer.engine.termsmining.CSV;
import fr.matrixminer.engine.termsmining.CustomStringList;
import fr.matrixminer.engine.termsmining.FilesManip;
import fr.matrixminer.engine.termsmining.NCValueTest;

public class MatrixMinerScriptTest {

	private static List<File> plFiles = new ArrayList<File>();
	private static Map<String, HashMap<String, Double>> topNCValMap = new HashMap<String, HashMap<String, Double>>();
	private static Map<String, List<String>> featuresMap = new HashMap<String, List<String>>();
	private static Map<String, List<String>> candidValMap = new HashMap<String, List<String>>();
	private static Map<String, List<String>> candidTextMap = new HashMap<String, List<String>>();
	private static Map<String, List<String>> candidTextValMap = new HashMap<String, List<String>>();
	private static Map<String, List<String>> candidBoolMap = new HashMap<String, List<String>>();
	private static CustomStringList candidBoolList = new CustomStringList();
	private static Map<List<String>, String> valFIS = new HashMap<List<String>, String>();
	private static Map<List<String>, String> textFIS = new HashMap<List<String>, String>();
	private static Map<List<String>, String> textValFIS = new HashMap<List<String>, String>();
	private static Map<List<String>, String> boolFIS = new HashMap<List<String>, String>();
	private static List<String> existCommonItems = new ArrayList<String>();
	private static Map<String, String> lemmaValFeatures = new HashMap<String, String>();
	private static Map<String, String> lemmaTextValFeatures = new HashMap<String, String>();
	private static Map<String, String> lemmaTextFeatures = new HashMap<String, String>();
	private static List<Set<Node>> boolCliques;
	private static int pcmLinesCount = 0;
	private static CSV csv = new CSV();
	private static File dir = new File("RResults\\");
	private static File pcmFile;

	private static int finalPcmLinesCount = 0;
	private static CSV finalCsv = new CSV();
	private static File finalPCM;
	private static int startingPoint = 0;
	private static int endBoolPoint = 0;

	private static int finalPcmLinesCount2 = 0;
	private static CSV finalCsv2 = new CSV();
	private static File finalPCM2;
	private static int startingPoint2 = 0;
	private static int endBoolPoint2 = 0;
	private static File directory;

	public static void main(String args[]) throws Exception {
		
		
		
		directory = new File(args[0]);
		boolean activateTextVal = Boolean.parseBoolean(args[1]);
		
		dir = directory;
		pcmFile = new File(directory, "pcm.csv");
		finalPCM = new File(directory, "finalPCM.csv");
		finalPCM2 = new File(directory, "finalPCM2.csv");
		 
	
		new File(directory.getAbsolutePath() + "\\TextFeatures").mkdirs();
		new File(directory.getAbsolutePath() + "\\ValFeatures").mkdirs();
		new File(directory.getAbsolutePath() + "\\TextValFeatures").mkdirs();
		
		seekFeaturesByFile(directory);
		generatePCM();
		featuresValDiff(directory);
		refactorValPCM();
		//refactorValPCM2();
		if (activateTextVal) {
			featuresTextValDiff(directory);
			refactorTextValPCM();	
		}
		
		featuresTextDiff(directory);
		refactorTextPCM();
		generateResultingPCM();
		refactorResultingPCM();
		// reducePCM();
	/*	featuresBoolDiff();
		refactorBoolPCM();
		generateBoolResultingPCM();
		refactorBoolResultingPCM();*/
		
		
	}

	private static void refactorBoolResultingPCM() throws IOException {
		Set<String> paths = featuresMap.keySet();

		while (startingPoint2 < finalPcmLinesCount) {
			if (!emptyLine2(startingPoint2)) {
				String feature = finalCsv.get(0, startingPoint2);
				finalCsv2.put(0, finalPcmLinesCount2, feature);
				for (String path : paths) {
					path = path.trim();
					String value = finalCsv.get(finalCsv.getColum(path),
							startingPoint2);
					finalCsv2.put(finalCsv2.getColum(path),
							finalPcmLinesCount2, value);
				}
				finalPcmLinesCount2++;
			}
			startingPoint2++;
		}
		finalCsv2.save(finalPCM2, ';');
	}

	private static void refactorResultingPCM() throws IOException {
		Set<String> paths = featuresMap.keySet();

		while (startingPoint < pcmLinesCount) {
			if (!emptyLine(startingPoint)) {
				String feature = csv.get(0, startingPoint);
				finalCsv.put(0, finalPcmLinesCount, feature);
				for (String path : paths) {
					path = path.trim();
					String value = csv.get(csv.getColum(path), startingPoint);
					finalCsv.put(finalCsv.getColum(path), finalPcmLinesCount,
							value);
				}
				finalPcmLinesCount++;
			}
			startingPoint++;
		}
		finalCsv.save(finalPCM, ';');
	}

	private static void generateBoolResultingPCM() throws IOException {
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!finalPCM2.exists()) {
			finalPCM2.createNewFile();
		}
		finalCsv2.open(finalPCM2);
		Set paths = featuresMap.keySet();
		Iterator it = paths.iterator();
		int i = 1, j = 1;
		CustomStringList lineNames = new CustomStringList();

		finalCsv2.put(0, 0, "Features");
		while (it.hasNext()) {
			String path = (String) it.next();
			List<String> features = featuresMap.get(path);
			for (String feature : features) {
				if (!lineNames.contains(feature)) {
					finalCsv2.put(0, i, feature);
					lineNames.add(feature);
					i++;
				}
			}
		}
		System.out.println("Line Names:" + lineNames + "\n");
		paths = featuresMap.keySet();
		it = paths.iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			path = path.trim();
			finalCsv2.put(j, 0, path);
			List<String> features = featuresMap.get(path);
			for (String feature : lineNames) {
				if (features.contains(feature)) {

					finalCsv2.put(j, finalCsv2.getLine(feature), "YES");
				} else {

					finalCsv2.put(j, finalCsv2.getLine(feature), "NO");
				}
			}
			j++;
		}
		finalPcmLinesCount2 = i;
		finalCsv2.save(finalPCM2, ';');

	}

	private static void reducePCM2() throws IOException {
		Set<String> paths = featuresMap.keySet();
		int lineNum = 1;
		while (lineNum < endBoolPoint) {
			if (!emptyLine(lineNum)) {
				String feature = finalCsv.get(0, lineNum);
				for (String path : paths) {
					path = path.trim();
					String value = finalCsv.get(csv.getColum(path), lineNum);
					if (value.equals(1)) {

					}
				}
			}
			lineNum++;
		}

	}

	private static boolean emptyLine(int startingPoint) {
		Set<String> paths = featuresMap.keySet();
		for (String path : paths) {
			path = path.trim();
			String value = csv.get(csv.getColum(path), startingPoint);
			if (!value.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static boolean emptyLine2(int startingPoint2) {
		Set<String> paths = featuresMap.keySet();
		for (String path : paths) {
			path = path.trim();
			String value = finalCsv
					.get(finalCsv.getColum(path), startingPoint2);
			if (!value.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static void generateResultingPCM() throws FileNotFoundException,
			IOException {
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!finalPCM.exists()) {
			finalPCM.createNewFile();
		}
		finalCsv.open(finalPCM);
		Set paths = featuresMap.keySet();
		Iterator it = paths.iterator();
		int i = 1, j = 1;
		CustomStringList lineNames = new CustomStringList();

		finalCsv.put(0, 0, "Features");
		while (it.hasNext()) {
			String path = (String) it.next();
			List<String> features = featuresMap.get(path);
			for (String feature : features) {
				if (!lineNames.contains(feature)) {
					finalCsv.put(0, i, feature);
					lineNames.add(feature);
					i++;
				}
			}
		}
		candidBoolList.addAll(lineNames);
		System.out.println("Line Names:" + lineNames + "\n");
		paths = featuresMap.keySet();
		it = paths.iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			path = path.trim();
			finalCsv.put(j, 0, path);
			List<String> features = featuresMap.get(path);
			List<String> boolFeatures = new ArrayList<String>();
			for (String feature : lineNames) {
				if (features.contains(feature)) {

					finalCsv.put(j, finalCsv.getLine(feature), "YES");
					boolFeatures.add(feature);
				} else {

					finalCsv.put(j, finalCsv.getLine(feature), "NO");
				}
			}
			
			
			List <String> valList = new ArrayList<String>();
			List <String> textValList = new ArrayList<String>();
			List <String> tempBoolList = new ArrayList<String>();
			valList = candidValMap.get(path);
			textValList = candidTextValMap.get(path);
			for(String bool : boolFeatures){
				if (valList.contains(bool)){
				String lemmaBool = lemmaValFeatures.get(bool);
				tempBoolList.add(lemmaBool);
				}
				if (textValList.contains(bool)){
					String lemmaBool = lemmaTextValFeatures.get(bool);
					tempBoolList.add(lemmaBool);
					}
				else{
					tempBoolList.add(bool);
				}
			}
			candidBoolMap.put(path, tempBoolList);
			j++;
		}
		finalPcmLinesCount = i;
		startingPoint2 = i;
		endBoolPoint2 = i;
		finalCsv.save(finalPCM, ';');

	}

	private static void refactorTextPCM() throws IOException {
		Set<List<String>> textClusters;
		Set<String> textPaths;
		Iterator<List<String>> textItCluster;
		Iterator<String> textItPath;
		List<String> textFeatureList = new ArrayList<String>();
		List<String> tempFeatures = new ArrayList<String>();

		textClusters = textFIS.keySet();
		textItCluster = textClusters.iterator();
		while (textItCluster.hasNext()) {
			List<String> cluster = textItCluster.next();
			String commonItem = textFIS.get(cluster);
			if (!existCommonItems.contains(commonItem) && (commonItem != null)) {
				existCommonItems.add(commonItem);
				csv.put(0, pcmLinesCount, commonItem);
				pcmLinesCount = pcmLinesCount + 1;

				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						textPaths = candidTextMap.keySet();
						textItPath = textPaths.iterator();
						while (textItPath.hasNext()) {
							String path = (String) textItPath.next();
							path = path.trim();
							textFeatureList = candidTextMap.get(path);
							if (textFeatureList.contains(feature)) {
								csv.put(csv.getColum(path),
										csv.getLine(commonItem), value);
								tempFeatures = featuresMap.get(path);
								for (Iterator<String> iterator = tempFeatures
										.iterator(); iterator.hasNext();) {
									String f = iterator.next();
									if (f.contains(commonItem)
											&& f.contains(value)) {
										iterator.remove();
									}
								}
								featuresMap.put(path, tempFeatures);
								List<String> remainingList = featuresMap
										.get(path);
								featuresMap.get(path).remove(feature);

							}

						}
					}
				}
			} else {
				Map<Point, String> tempMap = new HashMap<Point, String>();
				textPaths = candidTextMap.keySet();
				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						textItPath = textPaths.iterator();
						while (textItPath.hasNext()) {
							String path = (String) textItPath.next();
							path = path.trim();
							textFeatureList = candidTextMap.get(path);
							if (textFeatureList.contains(feature)) {
								Point point = new Point(csv.getColum(path),
										csv.getLine(commonItem));
								tempMap.put(point, value);
							}

						}
					}
				}
				if (!areDiffRows(tempMap)) {
					Set<Point> points = tempMap.keySet();
					for (Point point : points) {
						int column = (int) point.getX();
						int row = (int) point.getY();
						String cellVal = csv.get(column, row);
						String newCellVal = tempMap.get(point);
						if (cellVal.equals("") && !newCellVal.equals("")) {
							csv.put(column, row, newCellVal);
							String path = csv.get(column, 0);
							tempFeatures = featuresMap.get(path.trim());
							for (Iterator<String> iterator = tempFeatures
									.iterator(); iterator.hasNext();) {
								String f = iterator.next();
								if (f.contains(commonItem)
										&& f.contains(newCellVal)) {
									iterator.remove();
								}
							}
							featuresMap.put(path, tempFeatures);
							List<String> remainingList = featuresMap.get(path);
						}
					}

				}
			}
		}
		csv.save(pcmFile, ';');

	}

	private static boolean areDiffRows(Map<Point, String> tempMap) {
		Set<Point> points = tempMap.keySet();
		for (Point point : points) {
			int column = (int) point.getX();
			int row = (int) point.getY();
			String cellVal = csv.get(column, row);
			String newCellVal = tempMap.get(point);
			if (!cellVal.equals(newCellVal) && !cellVal.contains(newCellVal)
					&& !newCellVal.contains(cellVal) && !cellVal.equals("")
					&& !newCellVal.equals("")) {
				return true;
			}
		}
		return false;
	}
	private static boolean areDiffRows2(Map<Point, String> tempMap) {
		Set<Point> points = tempMap.keySet();
		for (Point point : points) {
			int column = (int) point.getX();
			int row = (int) point.getY();
			String cellVal = csv.get(column, row);
			String newCellVal = tempMap.get(point);
			if (!cellVal.equals(newCellVal) && !cellVal.equals("")
					&& !newCellVal.equals("")) {
				return true;
			}
		}
		return false;
	}

	private static boolean notNestedRows(Map<Point, String> tempMap) {
		Set<Point> points = tempMap.keySet();
		for (Point point : points) {
			int column = (int) point.getX();
			int row = (int) point.getY();
			String cellVal = csv.get(column, row);
			String newCellVal = tempMap.get(point);
			if (!newCellVal.contains(cellVal) && !cellVal.equals("")
					&& !newCellVal.equals("")) {
				return true;
			}
		}
		return false;
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	private static void refactorTextValPCM() throws IOException {
		Set<List<String>> textValClusters;
		Set<String> textValPaths;
		Iterator<List<String>> textValItCluster;
		Iterator<String> textValItPath;
		String lemmaFeat;
		List<String> textValFeatureList = new ArrayList<String>();
		List<String> tempFeatures = new ArrayList<String>();

		textValClusters = textValFIS.keySet();
		textValItCluster = textValClusters.iterator();
		while (textValItCluster.hasNext()) {
			List<String> cluster = textValItCluster.next();
			String commonItem = textValFIS.get(cluster);
			if (!existCommonItems.contains(commonItem) && (commonItem != null)) {
				existCommonItems.add(commonItem);
				csv.put(0, pcmLinesCount, commonItem);
				pcmLinesCount = pcmLinesCount + 1;
				int commonline = csv.getValLine(commonItem, endBoolPoint);
				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						textValPaths = candidTextValMap.keySet();
						textValItPath = textValPaths.iterator();
						while (textValItPath.hasNext()) {
							String path = (String) textValItPath.next();
							path = path.trim();
							textValFeatureList = candidTextValMap.get(path);
							List<String> textValLemmaFeatureList = new ArrayList<String>();
							for (String f : textValFeatureList) {
								lemmaFeat = lemmaTextValFeatures.get(f);
								if (!textValLemmaFeatureList
										.contains(lemmaFeat)) {
									textValLemmaFeatureList.add(lemmaFeat);
								}
							}
							if (textValLemmaFeatureList.contains(feature)) {
								csv.put(csv.getColum(path), commonline, value);
								tempFeatures = featuresMap.get(path);
								for (Iterator<String> iterator = tempFeatures
										.iterator(); iterator.hasNext();) {
									String f = iterator.next();
									if (f.contains(commonItem)
											&& f.contains(value)) {
										iterator.remove();
									}
								}
								featuresMap.put(path, tempFeatures);
								List<String> remainingList = featuresMap
										.get(path);

								String initialFeature = getKeyByValue(
										lemmaTextValFeatures, feature);
								List<String> featureList = featuresMap
										.get(path);
								if (featureList.contains(initialFeature)) {
									featuresMap.get(path)
											.remove(initialFeature);
								}
							}

						}
					}
				}
			} else {
				Map<Point, String> tempMap = new HashMap<Point, String>();
				textValPaths = candidTextValMap.keySet();
				int commonline = csv.getValLine(commonItem, endBoolPoint);
				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						textValItPath = textValPaths.iterator();
						while (textValItPath.hasNext()) {
							String path = (String) textValItPath.next();
							path = path.trim();
							textValFeatureList = candidTextValMap.get(path);
							if (textValFeatureList.contains(feature)) {
								Point point = new Point(csv.getColum(path),
										commonline);
								tempMap.put(point, value);
							}

						}
					}
				}
				if (!notNestedRows(tempMap)) {
					Set<Point> points = tempMap.keySet();
					for (Point point : points) {
						int column = (int) point.getX();
						int row = (int) point.getY();
						String cellTextVal = csv.get(column, row);
						String newCellTextVal = tempMap.get(point);
						if (!cellTextVal.equals("")
								&& !newCellTextVal.equals("")) {
							csv.put(column, row, newCellTextVal);
						}
					}

				}
			}
		}
		csv.save(pcmFile, ';');

	}

	private static void refactorValPCM() throws IOException {
		Set<List<String>> valClusters;
		Set<String> valPaths;
		Iterator<List<String>> valItCluster;
		Iterator<String> valItPath;
		String lemmaFeat;
		List<String> valFeatureList = new ArrayList<String>();
		List<String> tempFeatures = new ArrayList<String>();

		valClusters = valFIS.keySet();
		valItCluster = valClusters.iterator();
		while (valItCluster.hasNext()) {
			List<String> cluster = valItCluster.next();
			String commonItem = valFIS.get(cluster);
			if (!existCommonItems.contains(commonItem) && (commonItem != null)) {
				existCommonItems.add(commonItem);
				csv.put(0, pcmLinesCount, commonItem);
				pcmLinesCount = pcmLinesCount + 1;
				int commonline = csv.getValLine(commonItem, endBoolPoint);

				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						valPaths = candidValMap.keySet();
						valItPath = valPaths.iterator();
						while (valItPath.hasNext()) {
							String path = (String) valItPath.next();
							path = path.trim();
							valFeatureList = candidValMap.get(path);
							List<String> valLemmaFeatureList = new ArrayList<String>();
							for (String f : valFeatureList) {
								lemmaFeat = lemmaValFeatures.get(f);
								if (!valLemmaFeatureList.contains(lemmaFeat)) {
									valLemmaFeatureList.add(lemmaFeat);
								}
							}
							if (valLemmaFeatureList.contains(feature)) {
								csv.put(csv.getColum(path), commonline, value);
								tempFeatures = featuresMap.get(path);
								for (Iterator<String> iterator = tempFeatures
										.iterator(); iterator.hasNext();) {
									String f = iterator.next();
									if (f.contains(commonItem)
											&& f.contains(value)) {
										iterator.remove();
									}
								}
								featuresMap.put(path, tempFeatures);
								List<String> remainingList = featuresMap
										.get(path);
								String initialFeature = getKeyByValue(
										lemmaValFeatures, feature);
								List<String> featureList = featuresMap
										.get(path);
								if (featureList.contains(initialFeature)) {
									featuresMap.get(path)
											.remove(initialFeature);
								}
							}

						}
					}
				}
			} else {
				Map<Point, String> tempMap = new HashMap<Point, String>();
				valPaths = candidValMap.keySet();
				int commonline = csv.getValLine(commonItem, endBoolPoint);
				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						valItPath = valPaths.iterator();
						while (valItPath.hasNext()) {
							String path = (String) valItPath.next();
							path = path.trim();
							valFeatureList = candidValMap.get(path);
							if (valFeatureList.contains(feature)) {
								Point point = new Point(csv.getColum(path),
										commonline);
								tempMap.put(point, value);

							}

						}
					}
				}
				if (!areDiffRows2(tempMap)) {
					Set<Point> points = tempMap.keySet();
					for (Point point : points) {
						int column = (int) point.getX();
						int row = (int) point.getY();
						String cellVal = csv.get(column, row);
						String newCellVal = tempMap.get(point);
						if (cellVal.equals("") && !newCellVal.equals("")) {
							csv.put(column, row, newCellVal);
						}
					}

				}
			}
			csv.save(pcmFile, ';');
		}
		csv.save(pcmFile, ';');

	}

	private static void refactorValPCM2() throws IOException {
		Set<List<String>> valClusters;
		Set<String> valPaths;
		Iterator<List<String>> valItCluster;
		Iterator<String> valItPath;
		String lemmaFeat;
		List<String> valFeatureList = new ArrayList<String>();
		List<String> tempFeatures = new ArrayList<String>();

		valClusters = valFIS.keySet();
		valItCluster = valClusters.iterator();
		while (valItCluster.hasNext()) {
			List<String> cluster = valItCluster.next();
			String commonItem = valFIS.get(cluster);

			if (!existCommonItems.contains(commonItem) && (commonItem != null)) {

				List<String> redundantList = getSimilarCommon(commonItem);
				if (redundantList.size() > 0) {
					for (String redundant : redundantList) {
						if (!areCommonDiffRows(cluster, redundant, commonItem)) {
							if (redundant.contains(commonItem)) {
								updateBiggerRedundant(redundant, commonItem);
							} else if (commonItem.contains(redundant)) {
								updateSmallerRedundant(redundant, commonItem);
							}
						}
					}
				}

				else {
					existCommonItems.add(commonItem);
					csv.put(0, pcmLinesCount, commonItem);
					pcmLinesCount = pcmLinesCount + 1;
					int commonline = csv.getValLine(commonItem, endBoolPoint);

					for (String feature : cluster) {
						if (!feature.equals(commonItem)) {

							String value = diff(feature, commonItem);
							valPaths = candidValMap.keySet();
							valItPath = valPaths.iterator();
							while (valItPath.hasNext()) {
								String path = (String) valItPath.next();
								path = path.trim();
								valFeatureList = candidValMap.get(path);
								List<String> valLemmaFeatureList = new ArrayList<String>();
								for (String f : valFeatureList) {
									lemmaFeat = lemmaValFeatures.get(f);
									if (!valLemmaFeatureList
											.contains(lemmaFeat)) {
										valLemmaFeatureList.add(lemmaFeat);
									}
								}
								if (valLemmaFeatureList.contains(feature)) {
									csv.put(csv.getColum(path), commonline,
											value);
									tempFeatures = new ArrayList<String>();
									tempFeatures = featuresMap.get(path);
									for (Iterator<String> iterator = tempFeatures
											.iterator(); iterator.hasNext();) {
										String f = iterator.next();
										if (f.contains(commonItem)
												&& f.contains(value)) {
											iterator.remove();
										}
									}
									featuresMap.put(path, tempFeatures);
									List<String> remainingList = featuresMap
											.get(path);
									String initialFeature = getKeyByValue(
											lemmaValFeatures, feature);
									List<String> featureList = featuresMap
											.get(path);
									if (featureList.contains(initialFeature)) {
										featuresMap.get(path).remove(
												initialFeature);
									}
								}

							}
						}
					}
				}
			} else {
				Map<Point, String> tempMap = new HashMap<Point, String>();
				valPaths = candidValMap.keySet();
				int commonline = csv.getValLine(commonItem, endBoolPoint);
				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						valItPath = valPaths.iterator();
						while (valItPath.hasNext()) {
							String path = (String) valItPath.next();
							path = path.trim();
							valFeatureList = candidValMap.get(path);
							if (valFeatureList.contains(feature)) {
								Point point = new Point(csv.getColum(path),
										commonline);
								tempMap.put(point, value);

							}

						}
					}
				}
				if (!areDiffRows(tempMap)) {
					Set<Point> points = tempMap.keySet();
					for (Point point : points) {
						int column = (int) point.getX();
						int row = (int) point.getY();
						String cellVal = csv.get(column, row);
						String newCellVal = tempMap.get(point);
						if (cellVal.equals("") && !newCellVal.equals("")) {
							csv.put(column, row, newCellVal);
							String path = csv.get(column, 0);
							tempFeatures = new ArrayList<String>();
							tempFeatures = featuresMap.get(path.trim());
							for (Iterator<String> iterator = tempFeatures
									.iterator(); iterator.hasNext();) {
								String f = iterator.next();
								if (f.contains(commonItem)
										&& f.contains(newCellVal)) {
									iterator.remove();
								}
							}
							featuresMap.put(path, tempFeatures);
							List<String> remainingList = featuresMap.get(path);
						}
					}

				}
			}
			csv.save(pcmFile, ';');
		}
		csv.save(pcmFile, ';');

	}

	private static void updateSmallerRedundant(String redundant,
			String commonItem) {
		int redundantLine = csv.getValLine(redundant, endBoolPoint);
		if (commonItem.contains(" of ")) {
			csv.put(0, redundantLine, commonItem);
		} else {
			csv.put(0, redundantLine, redundant);
		}

	}

	private static void updateBiggerRedundant(String redundant,
			String commonItem) {
		int redundantLine = csv.getValLine(redundant, endBoolPoint);
		if (redundant.contains(" of ")) {
			csv.put(0, redundantLine, redundant);
		} else {
			csv.put(0, redundantLine, commonItem);
		}
	}

	private static boolean areCommonDiffRows(List<String> cluster,
			String redundant, String commonItem) {

		Set<String> valPaths;
		Iterator<String> valItPath;
		List<String> valFeatureList = new ArrayList<String>();
		Map<Point, String> tempMap = new HashMap<Point, String>();

		valPaths = candidValMap.keySet();
		int redundantLine = csv.getValLine(redundant, endBoolPoint);
		for (String feature : cluster) {
			if (!feature.equals(commonItem)) {

				String value = diff(feature, commonItem);
				valItPath = valPaths.iterator();
				while (valItPath.hasNext()) {
					String path = (String) valItPath.next();
					path = path.trim();
					valFeatureList = candidValMap.get(path);
					if (valFeatureList.contains(feature)) {
						Point point = new Point(csv.getColum(path),
								redundantLine);
						tempMap.put(point, value);

					}

				}
			}
		}
		if (!areDiffRows(tempMap)) {
			Set<Point> points = tempMap.keySet();
			for (Point point : points) {
				int column = (int) point.getX();
				int row = (int) point.getY();
				String cellVal = csv.get(column, row);
				String newCellVal = tempMap.get(point);
				if (cellVal.equals("") && !newCellVal.equals("")) {
					csv.put(column, row, newCellVal);
					String path = csv.get(column, 0);
					List<String> tempFeatures = featuresMap.get(path.trim());
					for (Iterator<String> iterator = tempFeatures.iterator(); iterator
							.hasNext();) {
						String f = iterator.next();
						if ((f.contains(redundant) || f.contains(commonItem))
								&& f.contains(newCellVal)) {
							iterator.remove();
						}
					}
					featuresMap.put(path, tempFeatures);
					List<String> remainingList = featuresMap.get(path);
				}
			}
			return false;
		}

		return true;
	}

	private static List<String> getSimilarCommon(String commonItem) {
		List<String> tempList = new ArrayList<String>();
		for (String common : existCommonItems) {
			if (commonItem.contains(common) || common.contains(commonItem)) {
				tempList.add(common);
			}
		}
		return tempList;
	}

	private static void featuresTextValDiff(File dir) throws ClassNotFoundException,
			IOException, InterruptedException {

		Set textValPaths = candidTextValMap.keySet();
		Iterator textValIt = textValPaths.iterator();
		CustomStringList textValFeatures = new CustomStringList();

		while (textValIt.hasNext()) {
			String textValPath = (String) textValIt.next();
			List<String> features1 = candidTextValMap.get(textValPath);
			for (String feature : features1) {
				if (!textValFeatures.contains(feature)) {
					textValFeatures.add(feature);
				}
			}
		}
		TextValFeatureClustering clusFeat1 = new TextValFeatureClustering(directory);
		textValFIS = clusFeat1.clusterFeatures(textValFeatures, dir);
		lemmaTextValFeatures = clusFeat1.getLemmaTextValFeatures();

	}

	private static void featuresValDiff(File dir) throws ClassNotFoundException,
			IOException, InterruptedException {

		Set valPaths = candidValMap.keySet();
		Iterator valIt = valPaths.iterator();
		CustomStringList valFeatures = new CustomStringList();

		while (valIt.hasNext()) {
			String valPath = (String) valIt.next();
			List<String> features1 = candidValMap.get(valPath);
			for (String feature : features1) {
				if (!valFeatures.contains(feature)) {
					valFeatures.add(feature);
				}
			}
		}
		FeatureClustering clusFeat1 = new FeatureClustering(directory);
		valFIS = clusFeat1.clusterFeatures(valFeatures, dir);
		lemmaValFeatures = clusFeat1.getLemmaValFeatures();

	}

	private static void featuresTextDiff(File dir) throws ClassNotFoundException,
			IOException, InterruptedException {

		Set textPaths = candidTextMap.keySet();
		Iterator textIt = textPaths.iterator();
		CustomStringList textFeatures = new CustomStringList();

		while (textIt.hasNext()) {
			String textPath = (String) textIt.next();
			List<String> features2 = candidTextMap.get(textPath);
			for (String feature : features2) {
				if (!textFeatures.contains(feature)) {
					textFeatures.add(feature);
				}
			}
		}
		FeatureTextClustering clusFeat2 = new FeatureTextClustering(directory);
		lemmaTextFeatures = clusFeat2.getLemmaTextFeatures();
		textFIS = clusFeat2.clusterTextFeatures(textFeatures, dir);
	}

	private static void featuresBoolDiff() throws ClassNotFoundException,
			IOException, InterruptedException {
		
		Set boolPaths = candidBoolMap.keySet();
		Iterator boolIt = boolPaths.iterator();
		CustomStringList boolFeatures = new CustomStringList();

		while (boolIt.hasNext()) {
			String boolPath = (String) boolIt.next();
			List<String> features3 = candidBoolMap.get(boolPath);
			for (String feature : features3) {
				if (!boolFeatures.contains(feature)) {
					boolFeatures.add(feature);
				}
			}
		}
		FeatureBoolClustering clusFeat3 = new FeatureBoolClustering();
		boolFIS = clusFeat3.clusterBoolFeatures(boolFeatures);
	}

	private static void refactorBoolPCM() throws IOException {
		Set<List<String>> boolClusters;
		Set<String> boolPaths;
		Iterator<List<String>> boolItCluster;
		Iterator<String> boolItPath;
		List<String> boolFeatureList = new ArrayList<String>();
		List<String> tempFeatures = new ArrayList<String>();

		boolClusters = boolFIS.keySet();
		boolItCluster = boolClusters.iterator();
		while (boolItCluster.hasNext()) {
			List<String> cluster = boolItCluster.next();
			String commonItem = boolFIS.get(cluster);
			if (!existCommonItems.contains(commonItem) && (commonItem != null)) {
				existCommonItems.add(commonItem);
				finalCsv.put(0, finalPcmLinesCount, commonItem);
				finalPcmLinesCount = finalPcmLinesCount + 1;

				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						boolPaths = candidBoolMap.keySet();
						boolItPath = boolPaths.iterator();
						while (boolItPath.hasNext()) {
							String path = (String) boolItPath.next();
							path = path.trim();
							boolFeatureList = candidBoolMap.get(path);
							if (boolFeatureList.contains(feature)) {
								System.out.println(path);
								finalCsv.put(finalCsv.getColum(path),
										finalCsv.getLine(commonItem), value);
								tempFeatures = featuresMap.get(path);
								for (Iterator<String> iterator = tempFeatures
										.iterator(); iterator.hasNext();) {
									String f = iterator.next();
									if (f.contains(commonItem)
											&& f.contains(value)) {
										iterator.remove();
									}
								}
								featuresMap.put(path, tempFeatures);
								List<String> remainingList = featuresMap
										.get(path);
								featuresMap.get(path).remove(feature);

							}

						}
					}
				}
			} else {
				Map<Point, String> tempMap = new HashMap<Point, String>();
				boolPaths = candidBoolMap.keySet();
				for (String feature : cluster) {
					if (!feature.equals(commonItem)) {

						String value = diff(feature, commonItem);
						boolItPath = boolPaths.iterator();
						while (boolItPath.hasNext()) {
							String path = (String) boolItPath.next();
							path = path.trim();
							boolFeatureList = candidBoolMap.get(path);
							if (boolFeatureList.contains(feature)) {
								Point point = new Point(
										finalCsv.getColum(path),
										finalCsv.getLine(commonItem));
								tempMap.put(point, value);
							}

						}
					}
				}
				if (!areDiffRows2(tempMap)) {
					Set<Point> points = tempMap.keySet();
					for (Point point : points) {
						int column = (int) point.getX();
						int row = (int) point.getY();
						String cellVal = finalCsv.get(column, row);
						String newCellVal = tempMap.get(point);
						if (cellVal.equals("") && !newCellVal.equals("")) {
							finalCsv.put(column, row, newCellVal);
							String path = finalCsv.get(column, 0);
							tempFeatures = new ArrayList<String>();
							tempFeatures = featuresMap.get(path.trim());
							for (Iterator<String> iterator = tempFeatures
									.iterator(); iterator.hasNext();) {
								String f = iterator.next();
								if (f.contains(commonItem)
										&& f.contains(newCellVal)) {
									iterator.remove();
								}
							}
							featuresMap.put(path, tempFeatures);
							List<String> remainingList = featuresMap.get(path);
						}
					}

				}
				else{
					Map<Point, String> tempMap2 = new HashMap<Point, String>();
					finalCsv.put(0, finalPcmLinesCount, commonItem);
					Set<Point> points = tempMap.keySet();
					for (Point point : points) {
						point.setLocation(point.getX(), finalPcmLinesCount);
						tempMap2.put(point, tempMap.get(point));
					}
					
					Set<Point> points2 = tempMap2.keySet();
					for (Point point : points2) {
						int column = (int) point.getX();
						int row = (int) point.getY();
						String newCellVal = tempMap2.get(point);
						finalCsv.put(column, row, newCellVal);
						String path = finalCsv.get(column, 0);
						tempFeatures = new ArrayList<String>();
						tempFeatures = featuresMap.get(path.trim());
						for (Iterator<String> iterator = tempFeatures
								.iterator(); iterator.hasNext();) {
							String f = iterator.next();
							if (f.contains(commonItem)
									&& f.contains(newCellVal)) {
								iterator.remove();
							}
						}
						featuresMap.put(path, tempFeatures);
						List<String> remainingList = featuresMap.get(path);
					}
					
					finalPcmLinesCount = finalPcmLinesCount + 1;
				}
			}
		}
		finalCsv.save(finalPCM, ';');

	}

	private static void generatePCM() throws FileNotFoundException, IOException {

		if (!dir.exists()) {
			dir.mkdir();
		}

		if (!pcmFile.exists()) {
			pcmFile.createNewFile();
		}
		csv.open(pcmFile);
		Set paths = featuresMap.keySet();
		Iterator it = paths.iterator();
		int i = 1, j = 1;
		CustomStringList lineNames = new CustomStringList();

		csv.put(0, 0, "Features");
		while (it.hasNext()) {
			String path = (String) it.next();
			List<String> features = featuresMap.get(path);
			for (String feature : features) {
				if (!lineNames.contains(feature)) {
					csv.put(0, i, feature);
					lineNames.add(feature);
					i++;
				}
			}
		}
		System.out.println("Line Names:" + lineNames + "\n");
		paths = featuresMap.keySet();
		it = paths.iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			path = path.trim();
			csv.put(j, 0, path);
			List<String> features = featuresMap.get(path);
			for (String feature : lineNames) {
				if (features.contains(feature)) {

					csv.put(j, csv.getLine(feature), "YES");
				} else {

					csv.put(j, csv.getLine(feature), "NO");
				}
			}
			j++;
		}
		pcmLinesCount = i;
		startingPoint = i;
		endBoolPoint = i;
		csv.save(pcmFile, ';');

	}

	private static void refactorPCM() throws IOException {
		Set<List<String>> valClusters;
		Set<String> valPaths;
		Iterator<List<String>> valItCluster;
		Iterator<String> valItPath;
		List<String> valFeatureList = new ArrayList<String>();

		valClusters = valFIS.keySet();
		valItCluster = valClusters.iterator();
		while (valItCluster.hasNext()) {
			List<String> cluster = valItCluster.next();
			String commonItem = valFIS.get(cluster);
			csv.put(0, pcmLinesCount, commonItem);
			pcmLinesCount = pcmLinesCount + 1;

			for (String feature : cluster) {
				if (!feature.equals(commonItem)) {

					String value = diff(feature, commonItem);
					valPaths = candidValMap.keySet();
					valItPath = valPaths.iterator();
					while (valItPath.hasNext()) {
						String path = (String) valItPath.next();
						path = path.trim();
						valFeatureList = candidValMap.get(path);
						if (valFeatureList.contains(feature)) {
							csv.put(csv.getColum(path),
									csv.getLine(commonItem), value);

						}

					}
				}
			}
		}

		Set<List<String>> textClusters;
		Set<String> textPaths;
		Iterator<List<String>> textItCluster;
		Iterator<String> textItPath;
		List<String> textFeatureList = new ArrayList<String>();

		textClusters = textFIS.keySet();
		textItCluster = textClusters.iterator();
		while (textItCluster.hasNext()) {
			List<String> cluster = textItCluster.next();
			String commonItem = textFIS.get(cluster);
			csv.put(0, pcmLinesCount, commonItem);
			pcmLinesCount = pcmLinesCount + 1;

			for (String feature : cluster) {
				if (!feature.equals(commonItem)) {

					String value = diff(feature, commonItem);
					textPaths = candidTextMap.keySet();
					textItPath = textPaths.iterator();
					while (textItPath.hasNext()) {
						String path = (String) textItPath.next();
						path = path.trim();
						textFeatureList = candidTextMap.get(path);
						if (textFeatureList.contains(feature)) {
							csv.put(csv.getColum(path),
									csv.getLine(commonItem), value);

						}

					}
				}
			}
		}
		csv.save(pcmFile, ';');
	}

	private static String diff(String feature, String commonItem) {
		String value = "NA";
		String commonPart;
		int begin = -1, end = -1;
		if (!feature.contains(commonItem)) {

			BreakIterator boundary = BreakIterator.getWordInstance();
			boundary.setText(commonItem);
			String firstword = printFirst(boundary, commonItem);
			String lastWord = commonItem
					.substring(commonItem.lastIndexOf(" ") + 1);
			begin = feature.indexOf(firstword);
			end = feature.indexOf(lastWord) + lastWord.length();
			if (begin != -1) {
				commonPart = feature.substring(begin, end);
			}
		}
		if (feature.contains(commonItem)) {
			commonPart = commonItem;
			begin = feature.indexOf(commonItem);
			end = begin + commonItem.length();
		}
		if (begin > 0 && begin <= end && end <= feature.length()) {
			value = feature.substring(0, begin - 1);
		}
		if ((begin == 0) && begin <= end && end <= feature.length()) {
			value = feature.substring(end + 1, feature.length());
		}
		value = value.trim();
		if (value.equals("")) {
			value = "NA";
		}
		return value;
	}

	public static String printFirst(BreakIterator boundary, String source) {
		int start = boundary.first();
		int end = boundary.next();
		return (source.substring(start, end));
	}

	public static String printLast(BreakIterator boundary, String source) {
		int end = boundary.last();
		int start = boundary.previous();
		return (source.substring(start, end));
	}

	public static void seekFeaturesByFile(File dir) throws Exception {

		File path;
		Map<String, Double> topNCValFeatures;
		FilesManip manip = new FilesManip();
		File[] plFilesArray = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt") && !name.equals("products.txt");
			}
		});
		
		plFiles = new ArrayList<File>();
		for (File file : plFilesArray) {
			plFiles.add(file);
		}
		
		CustomStringList totalFeatures = new CustomStringList();

		for (int i = 0; i < plFiles.size(); i++) {
			path = plFiles.get(i);
			NCValueTest ncValue = new NCValueTest();
			topNCValFeatures = ncValue.genFeatures(path.getPath());
			topNCValMap.put(path.getName(),
					(HashMap<String, Double>) topNCValFeatures);
			List<String> candidValList = ncValue.getCandidValList();
			List<String> candidTextList = ncValue.getCandidTextList();
			List<String> candidTextValList = ncValue.getCandidTextValList();
			candidValMap.put(path.getName(), candidValList);
			candidTextMap.put(path.getName(), candidTextList);
			candidTextValMap.put(path.getName(), candidTextValList);
		}

		Set files = topNCValMap.keySet();
		Iterator it = files.iterator();
		String file;
		while (it.hasNext()) {
			file = (String) it.next();
			List<String> topFeatures = new ArrayList<String>();
			topFeatures.addAll(topNCValMap.get(file).keySet());
			featuresMap.put(file, topFeatures);
			totalFeatures.addAll(topFeatures);
		}
	}

}
