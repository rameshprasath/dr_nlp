package com.digitalreasoning.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/* Name: Ramesh
 * Created on: 01/08/2016
 * Purpose: Interact with OpenNLP API
 * 			Exception are handled using try..catch block and displayed on screen. Not logged in a file
 * 
 * Reference: Online help references
 * https://opennlp.apache.org/documentation/manual/opennlp.html 
 * 
 */

public class NLPOperation {
	/*
	 * Purpose: Method to identify sentence boundary and return collection of sentences as string array
	 * 
	 * Assumption: 
	 * 		1) Files are placed under resource folder inside src/main structure
	 * 
	 * Potential problem: Identifying sentence boundary from large file
	 * 
	 * Solution: Passing a small chunks at a time
	 */
	public String[] GetSentences(String fileContent) {
		InputStream sentdetIS = null;
		String sentences[] = null;
		ClassLoader clsLoader = getClass().getClassLoader();
		
		try {
			//Build the Sentence Detector model using OpenNLP API
			sentdetIS = new FileInputStream(clsLoader.getResource("nlpbin/en-sent.bin").getFile());
			SentenceModel sentModel = new SentenceModel(sentdetIS);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
			
			//below method identifies sentence boundary
			sentences = sentenceDetector.sentDetect(fileContent);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (InvalidFormatException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (sentdetIS != null) {
			    try {
			    	sentdetIS.close();
			    }
			    catch (Exception ex) {
			    	
			    }
			}
		}
		return sentences;
	}
	
	/*
	 * Purpose: Method to get tokens from the given sentence
	 * 
	 * Potential problem: Performance may deteriorate as number of sentences increases
	 * Alternative: Passing a chunk of content instead of one sentence at a time
	 */
	public List<String> GetTokens(String fileContent) {
		InputStream tokenIS = null;
		String tokens[] = null;
		ClassLoader clsLoader = getClass().getClassLoader();
				
		try {
			//Build the Tokenizer model using OpenNLP API
			tokenIS = new FileInputStream(clsLoader.getResource("nlpbin/en-token.bin").getFile());
			TokenizerModel tokenModel = new TokenizerModel(tokenIS);
			
			Tokenizer tokenizer = new TokenizerME(tokenModel);
			 
			tokens = tokenizer.tokenize(fileContent);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (InvalidFormatException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (tokenIS != null) {
			    try {
			    	tokenIS.close();
			    }
			    catch (Exception ex) {
			    	
			    }
			}
		}
		
		return Arrays.asList(tokens);
	}
}
