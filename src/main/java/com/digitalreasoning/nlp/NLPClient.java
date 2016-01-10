package com.digitalreasoning.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NLPClient {

	public static void main(String[] args) {
		Feature1Test();
		Feature2Test();
		Feature3Test();
	}


	/*
	 * Purpose: Method created to test the Feature 1 functionality. It is not a unit test method
	 * 
	 */

	public static void Feature1Test() {
		FileProcessor fp = new FileProcessor();
		NLPOperation nlp = new NLPOperation();

		//Feature 1 testing code
		String fc = fp.readFileContent("input/nlp_data.txt");	
		String sentences[] = nlp.GetSentences(fc); 

		//String strTokens[] = nlp.GetTokens(fc);
		List<Tokens> tokensList = nlp.ProcessSentences(sentences);

		//write the output into XML file
		fp.TokensToXMLFile(tokensList, "output/feature1.xml");
		
	}


	/*
	 * Purpose: Method created to test the Feature 2 functionality. It is not a unit test method
	 * 
	 */

	public static void Feature2Test() {
		FileProcessor fp = new FileProcessor();
		NLPOperation nlp = new NLPOperation();
		
		String fc = fp.readFileContent("input/nlp_data.txt");	
		String sentences[] = nlp.GetSentences(fc); 
		
		//Feature 2 testing code
		fp.CreateNERTrainingFile("input/NER.txt", "input/ner-entity.train");

		nlp.CreateNamedEntityModel("input/ner-entity.train");

		ArrayList<NamedEntity> neList = nlp.IdentifyEntities(sentences);
		
		if (neList != null) {
			System.out.println("Number of NE sentences identified = " + neList.size());
			fp.NEToXMLFile(neList, "output/feature2.xml");			
		}
		System.out.println("Feature 2 execution completed....");
	}



	/*
	 * Purpose: Method created to test the Feature 3 functionality. It is not a unit test method
	 * 
	 * Assumption: 
	 * 	1) Given input path for ProcessDirectory method is directory path
	 * 	2) Folder path is relative from resource folder
	 * 
	 */

	public static void Feature3Test() {
		FileProcessor fp = new FileProcessor();
		NLPOperation nlp = new NLPOperation();

		List<String[]> list = fp.ProcessDirectory("input/nlp_data/nlp_data");

		ArrayList<NamedEntity> neList = nlp.ProcessSentenceCollection(list);
		System.out.println("Number of NE sentences identified = " + neList.size());
		fp.NEToXMLFile(neList, "output/feature3.xml");
		System.out.println("Feature 3 execution completed....");
	}


	/*
	 * Purpose: Method created to test how NER model behave with less training data
	 * Findings: Model unable to predict anything due to lack of training data
	 */

	public static void SampleNERTest() {
		TestNER obj = new TestNER();
		try {
			obj.testModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
