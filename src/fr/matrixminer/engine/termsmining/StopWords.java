package fr.matrixminer.engine.termsmining;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;


public class StopWords {
	public static Set<String> stopWords;
	
	public static String removeStopWords(String textFile) throws Exception {
	    
		setStopWords("stoplists/en.txt");
		CharArraySet stopSet = new CharArraySet(Version.LUCENE_4_10_2, stopWords, true);
	    TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_4_10_2, new StringReader(textFile.trim()));

	    tokenStream = new StopFilter(Version.LUCENE_4_10_2, tokenStream, stopSet);
	    StringBuilder sb = new StringBuilder();
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	    tokenStream.reset();
	    while (tokenStream.incrementToken()) {
	        String term = charTermAttribute.toString();
	        sb.append(term + " ");
	    }
	    return sb.toString();
	}
	public static void setStopWords(String file) throws IOException {
		stopWords = new HashSet<String>();
		
			InputStream ips = new FileInputStream(file);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String s = br.readLine();
			while ((s = br.readLine()) != null) {
				stopWords.add(s);
				
			}
			br.close();
		
	}

	public Set<String> getStopWords() {
		return stopWords;
	}
	static String readFile(String fileName) throws IOException {
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

/*	public static void main(String args[]) throws Exception {
		String file = "cnxUseCase\\France.txt";
		String text = readFile(file);
		System.out.println(text);

		StopWords stopwords = new StopWords();
		System.out.println(stopwords.removeStopWords(text));

	}*/

}
