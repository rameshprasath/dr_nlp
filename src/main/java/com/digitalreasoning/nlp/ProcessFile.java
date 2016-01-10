package com.digitalreasoning.nlp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

/*
 * Purpose: Read the file content and return the sentences as string array
 * 
 * Assumption: Given file path is a absolute path
 * 
 */

public class ProcessFile implements Callable<String[]> {

	private final String filePath;
	 
	ProcessFile(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String[] call() throws Exception {
		BufferedReader reader = null;
		String sentences[] = null;
		
		NLPOperation nlp = new NLPOperation();
		StringBuilder sb = new StringBuilder();
				
		try {
			//Open the text file with UTF-8 encoding
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "UTF-8"));
			String line = null;

			//Collect the file content in string builder and append newline character at the end of each line 
			while ((line = reader.readLine()) != null) {
				//Eliminate empty lines
				if (line.length() > 0)
					sb.append(line).append("\n");
			}
			
			//Call the NLP function to identify sentences
			sentences = nlp.GetSentences(sb.toString());
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
					nlp = null;
					sb = null;
				} 
				catch (IOException e) {					
					e.printStackTrace();
				}
			}
		}
		
		return sentences;
	}

}
