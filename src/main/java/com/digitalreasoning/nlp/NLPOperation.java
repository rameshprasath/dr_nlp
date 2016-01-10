package com.digitalreasoning.nlp;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;

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
					("en", "person", sampleDS, Collections.<String, Object>emptyMap());

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
	public Span[] IdentifyNamedEntities(String[] sentences) {
		InputStream nerIS = null;
		Span entitySpans[] = null;
		TokenNameFinderModel tokenNFM = null;
		
		ClassLoader clsLoader = getClass().getClassLoader();
		
		try {
			nerIS = new FileInputStream(clsLoader.getResource("nlpbin/en-ner-entity.bin").getFile());
			
			tokenNFM = new TokenNameFinderModel(nerIS);
			NameFinderME nameFinder = new NameFinderME(tokenNFM);
			for(String sentence : sentences) {
				entitySpans = nameFinder.find(GetTokens(sentence));
				
				System.out.println(sentence);
				for(Span s: entitySpans) {					
					System.out.println(s.toString());
				}
			}			
			
			nameFinder.clearAdaptiveData();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		return entitySpans;
	}
}
