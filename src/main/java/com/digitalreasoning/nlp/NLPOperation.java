package com.digitalreasoning.nlp;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

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
	 * Purpose: Method to get tokens for a given document sentences
	 * 
	 */
	
	public List<Tokens> ProcessSentences(String[] sentences) {
		List<Tokens> tokenList = new ArrayList<Tokens>();
		
		for (String sentence : sentences) {
			tokenList.add(new Tokens(sentence, Arrays.asList(GetTokens(sentence))));
		}
		
		return tokenList;
	}
	
	
	
	/*
	 * Purpose: Method to get tokens from the given sentence
	 * 
	 * Potential problem: Performance may deteriorate as number of sentences increases
	 * Alternative: Passing a chunk of content instead of one sentence at a time
	 */
	public String[] GetTokens(String fileContent) {
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

		return tokens;
	}


	/*
	 * Purpose: Method to build and train the custom Named Entity Recognition model 
	 * 
	 */
	public void CreateNamedEntityModel(String trainingDataFilePath) {
		BufferedOutputStream modelOS = null;
		ClassLoader clsLoader = getClass().getClassLoader();

		try {
			Charset charset = Charset.forName("UTF-8");
			ObjectStream<String> trainIS = new PlainTextByLineStream(new FileInputStream(
					clsLoader.getResource(trainingDataFilePath).getFile()), charset);

			ObjectStream<NameSample> sampleDS = new NameSampleDataStream(trainIS);

//			@SuppressWarnings("deprecation")
			TokenNameFinderModel nerModel = NameFinderME.train
					("en", "entity", sampleDS, Collections.<String, Object>emptyMap());

			 modelOS = new BufferedOutputStream(new FileOutputStream(
					 clsLoader.getResource("nlpbin/").getPath() + "en-ner-entity.bin"));
			 nerModel.serialize(modelOS);
			 
			 nerModel = null;
			 trainIS.close();
			 sampleDS.close();
			 
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {

			try {
				//Close the model output stream
				if (modelOS != null) 
					modelOS.close(); 
				clsLoader = null;
			}
			catch (Exception ex) { //digest the exception

			}
		}
	}
	
	
	/*
	 * Purpose: Method to identify the Named Entity based on the custom model
	 * 
	 */
	public ArrayList<NamedEntity> IdentifyEntities(String[] sentences) {
		NameFinderME nameFinder = BuildNFMEModel();
		ArrayList<NamedEntity> neList = IdentifyNamedEntities(nameFinder, sentences);
		return neList;
	}

	
	
	/*
	 * Purpose: Method to load the custom NER model
	 * 
	 */
	public NameFinderME BuildNFMEModel() {
		InputStream nerIS = null;
		TokenNameFinderModel tokenNFM = null;
		NameFinderME nameFinder = null;
		
		ClassLoader clsLoader = getClass().getClassLoader();
		
		try {
			nerIS = new FileInputStream(clsLoader.getResource("nlpbin/en-ner-entity.bin").getFile());
			
			tokenNFM = new TokenNameFinderModel(nerIS);
			nameFinder = new NameFinderME(tokenNFM);
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

			try {
				//Close the model output stream
				if (nerIS != null) 
					nerIS.close(); 
				
				tokenNFM = null; 
				clsLoader = null;
			}
			catch (Exception ex) { //digest the exception

			}
		}
		return nameFinder;
	}
	
	
	/*
	 * Purpose: Method to identify the Named Entity based on the custom model
	 * Input: NameFinderME, sentence tokens
	 * Output: NE - Sentence and Spans
	 * 
	 */
	private ArrayList<NamedEntity> IdentifyNamedEntities(NameFinderME nameFinder,String[] sentences) {
		Span entitySpans[] = null;
		ArrayList<NamedEntity> neList = new ArrayList<NamedEntity>();
		
		for(String sentence : sentences) {
			//find the Named Entities from the sentence tokens
			entitySpans = nameFinder.find(GetTokens(sentence));		
			
			//If sentence has spans, add it to result list
			if (entitySpans.length > 0) {
				neList.add(new NamedEntity(sentence, entitySpans));
			}
			else
				System.out.println("Not found NE for sentence - " + sentence);
		}	
		
		nameFinder.clearAdaptiveData();
		
		if (neList.isEmpty())
			return null;
		else
			return neList;
	}
	
	
	/*
	 * Purpose: Method to process multiple documents & identify Named Entities based on the custom model
	 * 		Invokes BuildNFMEModel() - to build the NER model
	 * 		Invoked IdentifyNamedEntities() - to identify the NEs
	 * Input: Sentence list from a document
	 * Output: Collection of NamedEntity objects
	 * 
	 * Assumption:
	 * 		1) Won't ignore if identical sentence appear in the same or different file 
	 * 
	 */
	
	public ArrayList<NamedEntity> ProcessSentenceCollection(List<String[]> sentencesList) {
		ArrayList<NamedEntity> outputNEList = new ArrayList<NamedEntity>();
		
		NameFinderME nameFinder = BuildNFMEModel();
		
		//process each document sentence
		for (String[] sentences : sentencesList) {
			//send the model and document sentences
			ArrayList<NamedEntity> neList = IdentifyNamedEntities(nameFinder, sentences);
			
			//append to NE collection to final output if it is not empty
			if (neList != null) {
				for (NamedEntity ne : neList) {
					outputNEList.add(ne);
				}
			}
		}
		
		nameFinder = null;
		if (outputNEList.isEmpty())
			return null;
		else
			return outputNEList;
	}
}
