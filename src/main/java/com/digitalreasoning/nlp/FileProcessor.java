package com.digitalreasoning.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/* Name: Ramesh
 * Created on: 01/08/2016
 * Purpose: Process input files
 * 			Exception are handled using try..catch block and displayed on screen. Not logged in a file
 * 
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
	 * Purpose: This method creates the XML document from list of strings
	 * 
	 * Assumption: This method is very specific to this requirement and not for generic  
	 */

	public void StringToXMLFile(List<String> content, String filePath) {
		ClassLoader clsLoader = getClass().getClassLoader();

		Element root = new Element("TOKENS");
		Document xmlDoc = new Document(root); 

		for(String token : content) {
			Element eToken = new Element("TOKEN");
			eToken.addContent(token);
			root.addContent(eToken);
		}

		xmlDoc.setRootElement(root);

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());

		try {
			//xmlOutput.output(xmlDoc, System.out);
			xmlOutput.output(xmlDoc,new FileWriter(clsLoader.getResource(filePath).getPath() + "tokens.xml"));
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}
		//release the resources
		xmlOutput = null;
		clsLoader = null;
		xmlDoc = null;
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
	public void CreateNERTrainingFile(String inputFilePath) {
		ClassLoader clsLoader = getClass().getClassLoader();
		BufferedReader bReader = null;
		BufferedWriter bWriter = null;
		String line = null;
		try {
			//read the unformatted input sample NER data file
			bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(clsLoader.getResource(inputFilePath).getFile()), "UTF-8"));

			//write the formatted NER sample data file for model training
			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					clsLoader.getResource("input/").getPath() + "ner-entity.train"), "UTF8"));
			while ((line = bReader.readLine()) != null) {
				//To avoid writing empty lines
				if(line.length() > 0) {
					bWriter.write(" <START> " + line + " <END> ");
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
}
