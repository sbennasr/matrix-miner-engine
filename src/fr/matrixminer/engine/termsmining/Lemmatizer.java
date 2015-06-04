package fr.matrixminer.engine.termsmining;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer {
	
	 private Properties props;
	 private StanfordCoreNLP pipeline;
	 
	public Lemmatizer(){
		 	props = new Properties(); 
	        props.put("annotators", "tokenize, ssplit, pos, lemma"); 
	        pipeline = new StanfordCoreNLP(props, false);
	}
	
	public String lemmatizeText(String text){
		
		  	Annotation document = pipeline.process(text);  
		  	String lemmaSentence = "";
	        for(CoreMap sentence: document.get(SentencesAnnotation.class))
	        {    
	            for(CoreLabel token: sentence.get(TokensAnnotation.class))
	            {       
	                String word = token.get(TextAnnotation.class);      
	                String lemma = token.get(LemmaAnnotation.class); 
	                lemmaSentence = lemmaSentence + lemma + " ";
	               // System.out.println("lemmatized word :" + lemma);
	            }
	            
	            lemmaSentence=lemmaSentence.trim();
	           // System.out.println("lemmatized sentence:" + lemmaSentence);
	        }
			return lemmaSentence;
	}
}
