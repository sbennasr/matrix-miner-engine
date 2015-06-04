package fr.matrixminer.engine.termsmining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.common.Similarity;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import edu.ucla.sspace.matrix.NoTransform;
import edu.ucla.sspace.matrix.factorization.SingularValueDecompositionLibC;
import fr.matrixminer.engine.termsclustering.FeatureCouple;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;

public class NCValueTest {
	protected Levenshtein levenshtein;
	protected SmithWaterman smithWaterman;
	LatentSemanticAnalysis lsa;
	final String FRANCE_REQ = "cnxUseCase//France";
	final Integer freqThreshold = 1;
	final Integer maxLength = 20;
	final double cValThreshold = 1.5;
	List<Pattern> patternsList = new ArrayList<Pattern>();
	List<Integer> lengthList = new ArrayList<Integer>();
	List<String> badStrings = new ArrayList<String>();
	Map<String, Candid> candidMap = new HashMap<String, Candid>();
	Map<String, ContextWord> contextMap = new HashMap<String, ContextWord>();
	Map<String, Double> topCValMap = new HashMap<String, Double>();
	Map<String, Double> topNCValMap = new HashMap<String, Double>();
	Map<String, Integer> candidFreqMap = new HashMap<String, Integer>();
	Map<Integer, HashMap<String, Integer>> mapByLen = new HashMap<Integer, HashMap<String, Integer>>();
	Map<FeatureCouple, Double> similarityMap = new HashMap<FeatureCouple, Double>();
	List<String> candidValList = new ArrayList<String>();
	List<String> candidTextList = new ArrayList<String>();
	List<String> candidTextValList = new ArrayList<String>();

	// (Noun)+ Noun
	Pattern pattern0 = Pattern
			.compile("(([a-zA-Z0-9-]+)(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)");

	// (Adj|Noun)+ Noun
	Pattern pattern1 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)");
	Pattern pattern = Pattern
			.compile("([a-zA-Z0-9-.]+(\\\\/)[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)");

	// (Adj|Noun)+ prepo (of|between|within|in)(Adj|Noun)+
	Pattern pattern2 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");
	// Nb+ (adj|Noun)+
	Pattern pattern3 = Pattern
			.compile("[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS))");

	Pattern pattern4 = Pattern
			.compile("[a-zA-Z0-9,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((\\s+((%|°)/NN))|(\\s+(x/SYM)|\\s+(''/'')))(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS))");

	// (Adj|Noun)+ prepo (of|between|within|in) (Adj|Noun)+
	Pattern pattern5 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");

	// Nb (Adj|Noun)+ prepo (of|between|within|in) Nb ? (Adj|Noun)+
	Pattern pattern51 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");

	// Nb ?(Adj|Noun)+ prepo (of|between|within|in) Nb (Adj|Noun)+
	Pattern pattern52 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");

	// Nb (Adj|Noun)+ prepo (of|between|within|in) Nb (Adj|Noun)+
	Pattern pattern53 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");

	// (adj|Noun)+ "at?(up|down)" to Nb Noun+
	Pattern pattern6 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)((\\s+(at/IN))?\\s+((up|down)/IN)\\s+(to/TO)\\s+)[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS))");

	// (between Nb and Nb )?(Adj|Noun)+ prepo (of|between|within|in) Nb ?
	// (Adj|Noun)+
	Pattern pattern13 = Pattern
			.compile("((between/IN)\\s+[a-zA-Z0-9,.-]+(/CD)\\s+(and/CC)\\s+[a-zA-Z0-9,.-]+(/CD))\\s+([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+)((of/IN))?\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");

	// Noun+ Nb
	Pattern pattern7 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))* ");
	// ( Adj( ,|and))* adj Noun
	Pattern pattern8 = Pattern
			.compile("(([a-zA-Z0-9-]+(JJ|/JJR|/RB|/VBN|/VBG)\\s+)((,/,)|(and/CC))\\s+)*([a-zA-Z0-9-]+(JJ|/JJR|/RB|/VBN|/VBG)\\s+)([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/CD)((%|'')/SYM)?\\s+)+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)");

	// Noun "include" Noun+
	Pattern pattern9 = Pattern
			.compile("([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)(include/VB)((([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)((%|'')/SYM)?\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+))((,/,)|(and/CC))\\s+)*(([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)((%|'')/SYM)?\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+))");
	// Foreign words
	Pattern pattern10 = Pattern
			.compile("[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD|/FW)\\s+(([a-zA-Z0-9-.]+(\\\\)(\\/)[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD|/FW)|[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD|/FW)|(--/:))\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)");

	// (Adj|Noun)+ Noun Nb+ (adj|Noun)+ Noun
	Pattern pattern11 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS))");
	Pattern pattern12 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+[a-zA-Z0-9,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((\\s+((%|°)/NN))|(\\s+(x/SYM)|\\s+(''/'')))(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS))");

	String[] specialHtml_Space = {"&#160;", "&#8206;", "&#8207;", "&#58;",
			"&#8224;", "&#8225;", "&#167;", "&#182;", "&#169;", "&#174;",
			"&#8482;", "&#38;", "&#64;", "&#9674;", "&#9824;", "&#9827;",
			"&#9829;", "&#9830;", "&#8592;", "&#8593;", "&#8594;", "&#8595;",
			"&#8596;", "&#34;", "&#171;", "&#187;", "&#8249;", "&#8250;",
			"&#8220;", "&#8221;", "&#8222;", "&#39;", "&#8216;", "&#8217;",
			"&#8218;", "&#191;", "&#161;", "&#40;", "&#41;", "&#91;", "&#93;",
			"&#123;", "&#125;", "&#168;", "&#180;", "&#96;", "&#94;", "&#710;",
			"&#732;", "&#184;", "&#35;", "&#42;", "&#8226;"};
	
	String[] specialHtml_tiret = {"&#173;","&#126;","&#175;","&#8254;","&#45;",
			"&#8211;","&#8212;","&#95;","&#124;","&#166;","&#8204;","&#8205;"};
	
	String[] specialHtml_point = {"&#46;","&#8230;","&#33;","&#63;","&#183;"};
	
	public void filter(String fileName, String text) throws Exception {

		String candidName, candidTag, candidSW;
		Candid candid;
		Integer length = 0, freq;
		int frequency = 0;
		StopWords stopwords = new StopWords();
		String[] patterns = { "/NNPS", "/NNP", "/NNS", "/NN", "/JJR", "/JJ",
				"/DT", "/IN", "/VBN", "/VBG", "/VBP", "/CD", "/RB", "/SYM",
				"/''", "/TO", "/FW", "/CC", "\\\\", "/,", "/\\.", "-/:", "--/" };

		patternsList.add(pattern0);
		patternsList.add(pattern1);
		patternsList.add(pattern2);
		patternsList.add(pattern3);
		patternsList.add(pattern4);
		patternsList.add(pattern5);
		patternsList.add(pattern51);
		patternsList.add(pattern52);
		patternsList.add(pattern53);
		patternsList.add(pattern13);
		patternsList.add(pattern6);
		patternsList.add(pattern7);
		patternsList.add(pattern11);
		patternsList.add(pattern12);
		patternsList.add(pattern);
		MaxentTagger tagger = new MaxentTagger(
				"taggers/bidirectional-distsim-wsj-0-18.tagger");
		String taggedText = tagger.tagString(text);
		System.out.println(taggedText + "\n");
		List<String> candidList = new ArrayList<String>();
		for (Pattern p : patternsList) {
			Matcher matcher = p.matcher(taggedText);
			while (matcher.find()) {
				candidName = matcher.group();
				candidTag = candidName;
				candidName = candidTag;
				for (int i = 0; i < patterns.length; i++) {
					candidName = candidName.replaceAll(patterns[i], "");
				}
				candidName = candidName.replaceAll("(\\s+)?--(\\s+)?", "-");
				candidName = candidName.toLowerCase().trim();
				frequency = computeCandidFrequency(candidName,
						text.toLowerCase());
				candidName = candidName.replace(",", ".");
				candidName = candidName.replaceAll("(\\s+)?--(\\s+)?", "-");
				candidName = candidName.replace("°", "");
				candidName = candidName.replace("'", "");
				if (!candidList.contains(candidName)) {
					candidList.add(candidName);
					if (!candidFreqMap.containsKey(candidName)) {
						candidFreqMap.put(candidName, frequency);
					}
					if (!p.equals(pattern0) && !p.equals(pattern1)
							&& !p.equals(pattern2) && !p.equals(pattern5)
							&& !p.equals(pattern11) && !p.equals(pattern12)) {
						if (!candidValList.contains(candidName)) {
							candidValList.add(candidName);
						}
					}
					if (p.equals(pattern0) || p.equals(pattern1)) {
						if (!candidTextList.contains(candidName)) {
							candidTextList.add(candidName);
						}
					}
					if (p.equals(pattern11) || p.equals(pattern12)) {
						if (!candidTextValList.contains(candidName)) {
							candidTextValList.add(candidName);
						}
					}
				}
			}
		}
		for (String str : candidList) {
			if (!candidMap.containsKey(str)) {
				candidSW = stopwords.removeStopWords(str);
				length = candidSW.split(" ").length;
				if (length <= maxLength) {
					candid = new Candid(candidSW, length);
					candid.getTriple().setFreq(candidFreqMap.get(str));
					candidMap.put(str, candid);
				}

			}

		}
		int len;
		Set keys = candidMap.keySet();
		Iterator it = keys.iterator();
		String key;
		while (it.hasNext()) {
			key = (String) it.next();
			if (candidMap.get(key).getTriple().getFreq() < freqThreshold) {
				badStrings.add(key);

			} else {
				len = candidMap.get(key).getLen();
				if (!lengthList.contains(len)) {
					lengthList.add(len);
				}
			}
		}
		for (String s : badStrings) {
			candidMap.remove(s);
		}
		Set keys2 = candidMap.keySet();

	}

	private int computeCandidFrequency(String candidName, String text) {

		Pattern pattern = Pattern.compile(candidName, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		if (!candidFreqMap.containsKey(candidName)) {
			candidFreqMap.put(candidName, count);
		}
		return count;
	}

	public String convertToSingular(String str) {
		if (str.length() > 0 && str.charAt(str.length() - 1) == 's') {
			str = str.substring(0, str.length() - 1);
		}
		return str + "/NN";
	}

	public void filterByLenght() {
		HashMap<String, Integer> candidLenMap;
		Set keys = candidMap.keySet();
		String key;
		for (Integer l : lengthList) {
			candidLenMap = new HashMap<String, Integer>();
			Iterator it = keys.iterator();
			while (it.hasNext()) {
				key = (String) it.next();
				if (candidMap.get(key).getLen() == l) {
					candidLenMap.put(key, candidMap.get(key).getTriple()
							.getFreq());
				}

			}
			mapByLen.put(l, candidLenMap);

		}

	}

	public void computeMaxLenCVal() {
		int max = Collections.max(lengthList);
		HashMap<String, Integer> maxStrings = mapByLen.get(max);
		Integer freqLonger, freqLongerNested;
		Set keys = maxStrings.keySet();
		Iterator it = keys.iterator();
		String key;
		while (it.hasNext()) {
			key = (String) it.next();
			candidMap.get(key).setCValueMaxLen();
			freqLonger = candidMap.get(key).getTriple().getFreq();
			freqLongerNested = candidMap.get(key).getTriple().getFreqNested();
			reviseAllSubStr(key, freqLonger, freqLongerNested, max - 1);
			computeCVal(max - 1);
		}

	}

	public void reviseAllSubStr(String longerStr, Integer freqLonger,
			Integer freqLongerNested, int max) {
		while (max >= 1) {
			if (mapByLen.containsKey(max)) {
				HashMap<String, Integer> subStrByLen = mapByLen.get(max);
				Set keys = subStrByLen.keySet();
				Iterator it = keys.iterator();
				String key;

				while (it.hasNext()) {
					key = (String) it.next();
					if (longerStr.toLowerCase().contains(key.toLowerCase())) {

						candidMap.get(key).getTriple()
								.increFreqNested(freqLonger, freqLongerNested);
						candidMap.get(key).getTriple().increLongerNb();
					}
				}
				max--;
			} else {
				max--;
			}
		}

	}

	public void computeCVal(int max) {
		while (max >= 1) {
			if (mapByLen.containsKey(max)) {
				HashMap<String, Integer> subStrByLen = mapByLen.get(max);
				Set keys = subStrByLen.keySet();
				Iterator it = keys.iterator();
				String key;
				Integer freqLonger, freqLongerNested;
				while (it.hasNext()) {
					key = (String) it.next();
					if (candidMap.get(key).getTriple().getLongerNb() == 0) {
						candidMap.get(key).setCValueMaxLen();
					} else {
						candidMap.get(key).setCValue();
					}
					freqLonger = candidMap.get(key).getTriple().getFreq();
					freqLongerNested = candidMap.get(key).getTriple()
							.getFreqNested();
					reviseAllSubStr(key, freqLonger, freqLongerNested, max - 1);

				}
				max--;
			} else {
				max--;
			}
		}
	}

	public void filterTopCval() {

		Set keys = candidMap.keySet();
		Iterator it = keys.iterator();
		String key;
		Double cval;
		while (it.hasNext()) {
			key = (String) it.next();
			cval = candidMap.get(key).getcVal();
			if (cval >= cValThreshold) {
				topCValMap.put(key, cval);
			}
		}
		ValueComparator bvc = new ValueComparator(topCValMap);
		TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
		sorted_map.putAll(topCValMap);
	}

	public void filterTopNCval() {

		Set keys = candidMap.keySet();
		Iterator it = keys.iterator();
		String key;
		Double cval;
		while (it.hasNext()) {
			key = (String) it.next();
			topNCValMap.put(key, candidMap.get(key).getNcVal());
		}

		ValueComparator bvc = new ValueComparator(topNCValMap);
		TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
		sorted_map.putAll(topNCValMap);
	}

	public void extractContextWord() {
		Set keys = topCValMap.keySet();
		Iterator it = keys.iterator();
		String t, w;
		while (it.hasNext()) {
			t = (String) it.next();
			String[] subWords = t.split(" ");
			for (int i = 0; i < subWords.length; i++) {
				w = subWords[i];
				ContextWord contextWord = new ContextWord();
				if (!contextMap.containsKey(w)) {
					contextMap.put(w, contextWord);
				}

			}
		}
	}

	public void computeWeights() {

		Set<String> words = contextMap.keySet();
		Set<String> terms = topCValMap.keySet();
		double weight;

		int i = 0;
		for (String w : words) {
			for (String t : terms) {
				if (t.toLowerCase().contains(w.toLowerCase())) {
					i++;
				}
			}
			weight = (double) i / (topCValMap.size());
			contextMap.get(w).setWeight(weight);
			contextMap.get(w).setNbTerms(i);
			i = 0;
		}
	}

	public void computeNCVal() {

		Set<String> words = contextMap.keySet();
		Set<String> terms = topCValMap.keySet();
		double weight;
		double freqContext;
		Candid candid;

		for (String t : terms) {
			candid = candidMap.get(t);
			for (String w : words) {
				if (t.toLowerCase().contains(w.toLowerCase())) {
					freqContext = (double) StringUtils.countMatches(t, w)
							/ (double) candid.getLen();
					weight = contextMap.get(w).getWeight();
					candid.increNVal(freqContext, weight);
				}
			}
			candidMap.get(t).setNcVal();
		}

		for (Iterator<Map.Entry<String, Candid>> it = candidMap.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, Candid> x = it.next();
			String str = x.getKey();
			if (!topCValMap.containsKey(str)) {
				it.remove();
			}
		}

	}

	private Map sortMap(Map aMap) {
		Map myMap = new HashMap();
		TreeSet set = new TreeSet(new Comparator() {
			public int compare(Object obj, Object obj1) {
				Double val1 = (Double) ((Map.Entry) obj).getValue();
				Double val2 = (Double) ((Map.Entry) obj1).getValue();
				return val1.compareTo(val2);
			}
		});

		set.addAll(aMap.entrySet());

		for (Iterator it = set.iterator(); it.hasNext();) {
			Map.Entry myMapEntry = (Map.Entry) it.next();
			myMap.put(myMapEntry.getKey(), myMapEntry.getValue());
		}

		return myMap;
	}
	public String readFile(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public void clusterFeaturesLSA() throws IOException {
		Set<String> featureKeys = topNCValMap.keySet();
		Iterator<String> it = featureKeys.iterator();
		String featureKey;
		lsa = new LatentSemanticAnalysis(true, 115, new NoTransform(),
				new SingularValueDecompositionLibC(), false,
				new StringBasisMapping());
		while (it.hasNext()) {
			featureKey = it.next();
			lsa.processDocument(new BufferedReader(new StringReader(featureKey
					.toLowerCase())));
		}
		lsa.processSpace(System.getProperties());
		Object[] array = featureKeys.toArray();

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				String source = array[i].toString();
				String target = array[j].toString();
				if (i != j) {
					FeatureCouple coupleKey = new FeatureCouple(source, target);
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
		FeatureCouple key;
		while (iterator.hasNext())

		{
			key = (FeatureCouple) iterator.next();
			sim = similarityMap.get(key);
			if (sim > 0.30) {
				source = key.getFeatureSource();
				target = key.getFeatureTarget();
			}

		}

	}

	public void clusterFeaturesSmithWaterman() throws IOException {
		smithWaterman = new SmithWaterman();
		Set<String> featureKeys = topNCValMap.keySet();
		Object[] array = featureKeys.toArray();

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				String source = array[i].toString();
				String target = array[j].toString();
				if (i != j) {
					FeatureCouple coupleKey = new FeatureCouple(source, target);
					if ((!similarityMap.containsKey(coupleKey))
							&& (coupleKey.existKeyReverse(similarityMap) == false)) {
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
			if (sim > 0.30) {
				source = key.getFeatureSource();
				target = key.getFeatureTarget();
			}

		}

	}

	public void clusterFeaturesLevenshtein() throws IOException {
		System.out.println("\n ***********Levenshtein*******\n");
		levenshtein = new Levenshtein();
		Set<String> featureKeys = topNCValMap.keySet();
		Object[] array = featureKeys.toArray();

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
			if (sim > 0.30) {
				source = key.getFeatureSource();
				target = key.getFeatureTarget();
			}

		}

	}

	public void tagFile(String fileName) throws IOException,
			ClassNotFoundException {
		String[] patterns = { "/NNPS", "/NNP", "/NNS", "/NN", "/JJR", "/JJ",
				"/DT", "/IN", "/VBN", "/VBG", "/VBP", "/CD" };
		MaxentTagger tagger = new MaxentTagger(
				"taggers\\bidirectional-distsim-wsj-0-18.tagger");

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				String taggedLine = tagger.tagString(line);
				for (Pattern p : patternsList) {
					Matcher matcher = p.matcher(taggedLine);
					while (matcher.find()) {
						String candidName = matcher.group();
					}
				}
				line = br.readLine();
			}

		} finally {
			br.close();
		}
	}

	public Map<String, Double> genFeatures(String file) throws Exception {
		mapByLen.clear();
		candidMap.clear();
		contextMap.clear();
		topNCValMap.clear();
		String text = readFile(file);
		System.out.println(text);
		text = preProcessingText(text);
		System.out.println("Preporoccessed text:" + text);
		filter(file, text);
		filterByLenght();
		computeMaxLenCVal();
		filterTopCval();
		extractContextWord();
		computeWeights();
		computeNCVal();
		Set keys = candidMap.keySet();
		filterTopNCval();
		return topNCValMap;

	}

	private String preProcessingText(String text) {
		for (int i=0;i<specialHtml_Space.length;i++){
			text = text.replace(specialHtml_Space[i], " ");	
		}
		for (int i=0;i<specialHtml_tiret.length;i++){
			text = text.replace(specialHtml_tiret[i], "-");	
		}
		for (int i=0;i<specialHtml_point.length;i++){
			text = text.replace(specialHtml_point[i], ".");	
		}
		text = text.replace("&#44;", ",");	
		text = text.replace("&#59;", ";");
		text = text.replace("&#47;", "/");
		text = text.replace("&#92;", "\\");
		text = text.replace("–", "-");
		text = text.replace("--","-");
		text = text.replaceAll("[*()°\"]", "");
		text = text.replace("''","");
		return text;
	}

	private void filterTopTextFeatures() {

		List<String> tempTextList = new ArrayList<String>();
		Set<String> featureKeys = topNCValMap.keySet();

		for (String feature : candidTextList) {
			if (featureKeys.contains(feature)) {
				tempTextList.add(feature);
			}
		}
		candidTextList.clear();
		candidTextList.addAll(tempTextList);

	}

	private void filterTopValFeatures() {

		List<String> tempValList = new ArrayList<String>();
		Set<String> featureKeys = topNCValMap.keySet();

		for (String feature : candidValList) {
			if (featureKeys.contains(feature)) {
				tempValList.add(feature);
			}
		}
		candidValList.clear();
		candidValList.addAll(tempValList);
	}

	public List<String> getCandidValList() {
		return candidValList;
	}

	public List<String> getCandidTextList() {
		return candidTextList;
	}

	public List<String> getCandidTextValList() {
		return candidTextValList;
	}
}
