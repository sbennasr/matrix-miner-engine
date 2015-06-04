package fr.matrixminer.test;

import java.text.DecimalFormat;
import java.util.regex.Pattern;
import org.junit.Test;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
 
import au.com.bytecode.opencsv.CSVReader;

public class PatternFeaturesTest {
	// Noun
	   Pattern patt = Pattern.compile("[a-zA-Z0-9-.]+(/NN|/NNS|/FW)");
	   
	// (Noun)+ Noun
	Pattern pattern0 = Pattern
			.compile("(([a-zA-Z0-9-]+)(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)", Pattern.CASE_INSENSITIVE);

	// (Adj|Noun)+ Noun
	Pattern pattern1 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)", Pattern.CASE_INSENSITIVE);
	Pattern pattern = Pattern
	.compile("([a-zA-Z0-9-.]+(\\\\/)[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)", Pattern.CASE_INSENSITIVE);

	// (Adj|Noun)+ prepo (of|between|within|in)(Adj|Noun)+
	Pattern pattern2 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW))", Pattern.CASE_INSENSITIVE);
	// Nb+ (adj|Noun)+
	Pattern pattern3 = Pattern
			.compile("[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW))", Pattern.CASE_INSENSITIVE);

	Pattern pattern4 = Pattern
			.compile("[a-zA-Z0-9,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((\\s+((%|°)/NN))|(\\s+(x/SYM)|\\s+(''/'')))(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW))", Pattern.CASE_INSENSITIVE);

	/*// Nb ?(Adj|Noun)+ prepo (of|between|within|in) Nb ? (Adj|Noun)+
	Pattern pattern5 = Pattern
			.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG))");
	*/
	// (Adj|Noun)+ prepo (of|between|within|in) (Adj|Noun)+
		Pattern pattern5 = Pattern
				.compile("(([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW))", Pattern.CASE_INSENSITIVE);
		
	// Nb (Adj|Noun)+ prepo (of|between|within|in) Nb ? (Adj|Noun)+
		Pattern pattern51 = Pattern
				.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW))", Pattern.CASE_INSENSITIVE);

	// Nb ?(Adj|Noun)+ prepo (of|between|within|in) Nb  (Adj|Noun)+
		Pattern pattern52 = Pattern
						.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW))", Pattern.CASE_INSENSITIVE);
				
	// Nb (Adj|Noun)+ prepo (of|between|within|in) Nb  (Adj|Noun)+
		Pattern pattern53 = Pattern
						.compile("(([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+))((of|between|within|in)(/IN))\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((%|'')/SYM)?\\s+)([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW))", Pattern.CASE_INSENSITIVE);

	// (adj|Noun)+ "at?(up|down)" to Nb Noun+
	    Pattern pattern6 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)((\\s+(at/IN))?\\s+((up|down)/IN)\\s+(to/TO)\\s+)[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW))", Pattern.CASE_INSENSITIVE);
	
	// (between Nb and Nb )?(Adj|Noun)+ prepo (of|between|within|in) Nb ? (Adj|Noun)+
		Pattern pattern13 = Pattern
				.compile("((between/IN)\\s+[a-zA-Z0-9,.-]+(/CD)\\s+(and/CC)\\s+[a-zA-Z0-9,.-]+(/CD))\\s+([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW)\\s+)((of/IN))?\\s+([a-zA-Z]+/DT\\s+)?([a-zA-Z0-9,.-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/VBG|/FW))", Pattern.CASE_INSENSITIVE);
		
	// Noun+ Nb
	Pattern pattern7 = Pattern
			.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)\\s+[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*", Pattern.CASE_INSENSITIVE);
	// ( Adj( ,|and))* adj Noun
	Pattern pattern8 = Pattern
			.compile("(([a-zA-Z0-9-]+(JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)((,/,)|(and/CC))\\s+)*([a-zA-Z0-9-]+(JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/CD)((%|'')/SYM)?\\s+)+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)", Pattern.CASE_INSENSITIVE);

	// Noun "include" Noun+
	Pattern pattern9 = Pattern
			.compile("([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD)\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)(include/VB)((([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)((%|'')/SYM)?\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+))((,/,)|(and/CC))\\s+)*(([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD)((%|'')/SYM)?\\s+)*([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)\\s+))", Pattern.CASE_INSENSITIVE);
	// Foreign words
	Pattern pattern10 = Pattern
			.compile("[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD|/FW)\\s+(([a-zA-Z0-9-.]+(\\\\)(\\/)[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD|/FW)|[a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/CD|/FW)|(--/:))\\s+)+[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS)", Pattern.CASE_INSENSITIVE);

	// (Adj|Noun)+ Noun Nb+ (adj|Noun)+ Noun
	Pattern pattern11 = Pattern
					.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)\\s+[a-zA-Z0-9,.-]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*(\\s+(x/SYM)\\s+[a-zA-Z0-9-,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*)*(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW))", Pattern.CASE_INSENSITIVE);
	Pattern pattern12 = Pattern
					.compile("([a-zA-Z0-9-.]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW)\\s+[a-zA-Z0-9,.]+(/CD)(\\s+((--/:|,/,|./.)\\s+)?[a-zA-Z0-9,.-]+(/CD))*((\\s+((%|°)/NN))|(\\s+(x/SYM)|\\s+(''/'')))(\\s+([a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/JJ|/JJR|/RB|/VBN|/VBG|/VBD|/FW)\\s+)*[a-zA-Z0-9-]+(/NN|/NNS|/NNP|/NNPS|/FW))", Pattern.CASE_INSENSITIVE);
			
	List<Pattern> patternsList = new ArrayList<Pattern>();
	Map<String,Double> filtersCount = new HashMap<String, Double>();

	  
	
	@Test
	public void featuresByPatterns () throws FileNotFoundException, IOException, ClassNotFoundException{
		String finalPcmPath = "vminer-dataset-diff\\Laptops\\Filter-Brand-Category\\Dell-PC-Laptops\\Dell3\\finalPCM.csv";
		
		  String feature;
		  List<String> cellValues;
		  List<String> features = new ArrayList<String>();
		 
		  
	            //Get the CSVReader instance with specifying the delimiter to be used
	        	 CSVReader reader = new CSVReader(new FileReader(finalPcmPath), ';', '"', 1);
	             
	        	//Read all rows at once
	        	    List<String[]> allRows = reader.readAll();
  
	        	    //Read CSV line by line and use the string array as you want
	        	   for(String[] row : allRows){
	        	      System.out.println(Arrays.toString(row));
	               
	            	feature = row[0];
	            	features.add(feature);
	                System.out.println(feature);
	                
	            	cellValues = new ArrayList<String>();
	                for(int i=1; i<row.length;i++)
	                {
	                	cellValues.add(row[i]);
	                }
	               
	            }
	    
		System.out.println(features);
		filtersCount.put("F0", 0.);
		filtersCount.put("F1", 0.);
		filtersCount.put("F2", 0.);
		filtersCount.put("F3", 0.);
		filtersCount.put("F4", 0.);
		filtersCount.put("F5", 0.);
		filtersCount.put("F6", 0.);
		
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
		//patternsList.add(pattern10);
		patternsList.add(pattern);
		patternsList.add(patt);
		
		MaxentTagger tagger = new MaxentTagger(
				"taggers/bidirectional-distsim-wsj-0-18.tagger");
		double nb;
		//String exp = "ghz/FW wireless/FW lan/FW 802.11/CD";
		//System.out.println(java.util.regex.Pattern.matches(pattern7.pattern(), exp));
		for(String f : features){
			String taggedFeature = tagger.tagString(f);
			taggedFeature = taggedFeature.trim();
			System.out.println(taggedFeature + "\n");
			if(Pattern.matches(pattern3.pattern(), taggedFeature)||Pattern.matches(pattern4.pattern(), taggedFeature)||Pattern.matches(pattern.pattern(), taggedFeature)){
				nb = filtersCount.get("F4")+1;
				filtersCount.put("F4", nb);
			}
			else if(Pattern.matches(pattern6.pattern(), taggedFeature)||Pattern.matches(pattern7.pattern(), taggedFeature)){
				nb = filtersCount.get("F5")+1;
				filtersCount.put("F5", nb);
			}
			else if(Pattern.matches(pattern11.pattern(), taggedFeature)||Pattern.matches(pattern12.pattern(), taggedFeature)){
				nb = filtersCount.get("F6")+1;
				filtersCount.put("F6", nb);
			}
			else if(Pattern.matches(patt.pattern(), taggedFeature)){
				nb = filtersCount.get("F0")+1;
				filtersCount.put("F0", nb);
			}
			else if(Pattern.matches(pattern0.pattern(), taggedFeature)){
				nb = filtersCount.get("F1")+1;
				filtersCount.put("F1", nb);
			}
			else if(Pattern.matches(pattern1.pattern(), taggedFeature)){
				nb = filtersCount.get("F2")+1;
				filtersCount.put("F2", nb);
			}
			else if(Pattern.matches(pattern2.pattern(), taggedFeature)||Pattern.matches(pattern5.pattern(), taggedFeature)){
				nb = filtersCount.get("F3")+1;
				filtersCount.put("F3", nb);
			}
			
		
		}
		System.out.println(filtersCount);
		//double total = features.size();
		double total =0.;
		double pourcent;
		for (int i=0; i<=6; i++){
			total += filtersCount.get("F"+i);
		}
		System.out.println("total features:" + total);
		for (int i=0; i<=6; i++){
		pourcent = roundTwoDecimals((filtersCount.get("F"+i)/total)*100);
		filtersCount.put("F"+i, pourcent);
		}
		System.out.println(filtersCount);
		
	}
	public double roundTwoDecimals(double d) {
	    DecimalFormat twoDForm = new DecimalFormat("#.##");
	    return Double.valueOf(twoDForm.format(d).replaceAll(",", "."));
	}
}
