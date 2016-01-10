package com.digitalreasoning.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/* Name: Ramesh
 * Created on: 01/08/2016
 * Purpose: Process input files
 * 			Exception are handled using try..catch block and displayed on screen. Not logged in a file
 * 
 * Alternative approach:
 * 	Checked-in files under resource folder which may be handled better by keeping away from source control
 *  (or) it can be zipped and maintained in source control.
 *  Files can be kept under network share. But project code needs to be modified.
 *  Method call should pass complete file path and respective methods needs to handle absolute path
 *  instead of relative path(i.e. current approach).
 */

public class FileProcessor {

	/*
	 * Purpose: Method to read entire file content and return the content as String
	 * 
	 * Assumption: 
	 * 		1) Text file with UTF-8 encoding
	 * 		2) Empty lines are ignored from further processing
	 * 		3) Files are placed under resource folder inside src/main structure
	 * 
	 * Limitation: If the input file is too large, it is not a efficient way
	 * 
	 * Alternatives: Read the file content into small chunks instead of transforming entire file content 
	 * as string	
	 */	
	public String readFileContent(String filePath) {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		ClassLoader clsLoader = getClass().getClassLoader();

		try {
			//Open the text file with UTF-8 encoding
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(clsLoader.getResource(filePath).getFile()), "UTF-8"));
			String line = null;

			while ((line = reader.readLine()) != null) {
				//Eliminate empty lines
				if (line.length() > 0)
					sb.append(line).append("\n");
			}
		} 
		catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		} 
		catch (FileNotFoundException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}
		finally {
			if (reader != null) {
				try {
					reader.close();	
					clsLoader = null;
				} 
				catch (IOException e) {					
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	
	
	/*
	 * Purpose: This method creates the XML document from list of named entities
	 * 
	 * Assumption: 
	 * 	1) This method is very specific to this requirement
	 * 	2) outputFolderPath is relative from resource folder
	 */

	public void TokensToXMLFile(List<Tokens> tokenList, String outputFilePath) {
		ClassLoader clsLoader = getClass().getClassLoader();
		File outFile = new File(outputFilePath);
		
		Element root = new Element("SENTENCES");
		Document xmlDoc = new Document(root); 

		for(Tokens sTokens : tokenList) {
			Element eToken = sTokens.GetXMLElement();
			root.addContent(eToken);
		}

		xmlDoc.setRootElement(root);

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		
		try {
			//xmlOutput.output(xmlDoc, System.out);
			xmlOutput.output(xmlDoc,new FileWriter(
				Paths.get(clsLoader.getResource(outFile.getParent()).getPath(), outFile.getName()).toString()));
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}
		//release the resources
		xmlOutput = null;
		clsLoader = null;
		xmlDoc = null;
		outFile = null;
		System.out.println("Feature 1 output file is created....");
	}
	
	
	
	/*
	 * Purpose: This method creates the XML document from list of named entities
	 * 
	 * Assumption: 
	 * 	1) This method is very specific to this requirement
	 * 	2) outputFolderPath is relative from resource folder
	 */

	public void NEToXMLFile(ArrayList<NamedEntity> neList, String outputFilePath) {
		ClassLoader clsLoader = getClass().getClassLoader();
		File outFile = new File(outputFilePath);
		
		Element root = new Element("SENTENCES");
		Document xmlDoc = new Document(root); 

		for(NamedEntity ne : neList) {
			Element eToken = ne.GetXMLElement();
			root.addContent(eToken);
		}

		xmlDoc.setRootElement(root);

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());

		try {
			//xmlOutput.output(xmlDoc, System.out);
			xmlOutput.output(xmlDoc,new FileWriter(
				Paths.get(clsLoader.getResource(outFile.getParent()).getPath(), outFile.getName()).toString()));
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}
		//release the resources
		xmlOutput = null;
		clsLoader = null;
		xmlDoc = null;
		outFile = null;
		System.out.println("Creation of sentences.xml is completed");
	}
	
	
	
	
	/*
	 * Purpose: This method creates the named entity training data file
	 * 
	 * Assumption: 
	 * 		1) This method is very specific to this requirement and not designed for generic purpose
	 * 		2) Input path relative to resource folder structure
	 * 		3) Output file path is hard coded
	 *  
	 */
	public void CreateNERTrainingFile(String inputFilePath, String outputFilePath) {
		ClassLoader clsLoader = getClass().getClassLoader();
		File outFile = new File(outputFilePath);
		//"ner-entity.train"
		
		BufferedReader bReader = null;
		BufferedWriter bWriter = null;
		String line = null;
		try {
			//read the unformatted input sample NER data file
			bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(clsLoader.getResource(inputFilePath).getFile()), "UTF-8"));

			//write the formatted NER sample data file for model training
			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					Paths.get(clsLoader.getResource(outFile.getParent()).getPath(), 
							outFile.getName()).toString()), "UTF8"));
			
			while ((line = bReader.readLine()) != null) {
				//To avoid writing empty lines
				if(line.length() > 0) {
					bWriter.write(" <START:entity> " + line + " <END> ");
					bWriter.newLine();
				}
			}
			bWriter.flush();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {

			try {
				if (bReader != null) 
					bReader.close();
				if (bWriter != null)
					bWriter.close();

				clsLoader = null;
			} 
			catch (IOException e) {					
				e.printStackTrace();
			}

		}
	}
	
	
	/*
	 * Purpose: 
	 * 		This method process data files under the given directory
	 * 		Output contains sentences and its corresponding NE spans
	 * Assumption: 
	 * 		1) Filename is not tracked
	 * 		2) Sentence is ignored if no NE spans identified 
	 * 
	 */
	
	public List<String[]> ProcessDirectory(String inputDirPath) {		
		
		List<String> fileList = GetFileNames(inputDirPath);
		
		//creates thread pool
		ExecutorService es = Executors.newFixedThreadPool(10);
		
		//List to hold the Future object associated with Callable
        List<Future<String[]>> taskList = new ArrayList<Future<String[]>>();
		
		//process each file & get the sentences
		for(String filePath : fileList) {
			//submit the thread job to process the file
			Future<String[]> task = es.submit(new ProcessFile(filePath));
			
			//add the task to the list to retrieve output later
			taskList.add(task);
		}
		
		//capture sentences from each file as string array. 
		List<String[]> sentencesList = new ArrayList<String[]>();
		
		for(Future<String[]> task : taskList) {
			try {
				sentencesList.add(task.get());
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			} 
			catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		//shutdown the execution service 
		es.shutdown();
		
		//If all tasks completed return the sentences collection
		if (es.isTerminated()) {
			
		}
		return sentencesList;
	}
	
	
	/*
	 * Purpose: Method process only files under this folder. Won't process files under sub-folders recursively
	 * 
	 * Assumption: 
	 *  	1) Directory path is relative from resource folder
	 *   
	 */
	
	public List<String> GetFileNames(String inputDirPath) {
		ClassLoader clsLoader = getClass().getClassLoader();
		List<String> fileList = new ArrayList<String>();
		
		final File folder = new File(clsLoader.getResource(inputDirPath).getPath());
		
		for (File fileEntry : folder.listFiles()) {
	        if (! fileEntry.isDirectory()) {
	            fileList.add(fileEntry.getAbsolutePath());
	        }
	    }
		
		return fileList;
	}
}
