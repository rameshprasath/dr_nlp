package com.digitalreasoning.nlp;

import java.util.List;

public class NLPClient {
		
	public static void main(String[] args) {
		FileProcessor fp = new FileProcessor();
				
		String fc = fp.readFileContent("input/nlp_data.txt");	
		System.out.println(fc);
		
		NLPOperation nlp = new NLPOperation();
		
		/*
		 * The below commented code identifies sentence boundary first and then tokenizes
		 * each sentence
		 */
//		for(String str : nlp.GetSentences(fc)) {
//			for(String token : nlp.GetTokens(str)) {
//				System.out.println(token);
//			}
//		}
		
		/*
		 * The below code takes the file content and do tokenize without identifying sentence boundary 
		 */
		List<String> lstTokens = nlp.GetTokens(fc);
		
		//write the output into XML file
		fp.StringToXMLFile(lstTokens, "output/");
	}

}
