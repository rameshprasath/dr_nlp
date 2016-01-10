package com.digitalreasoning.nlp;

import java.io.IOException;

import opennlp.tools.util.Span;


public class NLPClient {

	public static void main(String[] args) {
//		FileProcessor fp = new FileProcessor();
//		NLPOperation nlp = new NLPOperation();
//
//		//Feature 1 testing code
//		String fc = fp.readFileContent("input/nlp_data.txt");	
//		String sentences[] = nlp.GetSentences(fc); 
//		
//		//		String strTokens[] = nlp.GetTokens(fc);
//		//		
//		//		//write the output into XML file
//		//		fp.StringToXMLFile(Arrays.asList(strTokens), "output/");
//
//		//Feature 2 testing code
//		fp.CreateNERTrainingFile("input/NER.txt");
//				
//		nlp.CreateNamedEntityModel("input/ner-entity.train");
//				
//		nlp.IdentifyNamedEntities(sentences);
		TestNER obj = new TestNER();
		try {
			obj.testModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
