package com.digitalreasoning.nlp;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class TestNER {
	ClassLoader clsLoader = getClass().getClassLoader();

	public void createModel() throws IOException {

		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream =
				new PlainTextByLineStream(new FileInputStream(
						clsLoader.getResource("input/sample.txt").getFile()), charset);
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);

		TokenNameFinderModel model = null;

		try {
			model = NameFinderME.train("en", "person", sampleStream, Collections.<String, Object>emptyMap());
		}
		finally {
			sampleStream.close();
		}
		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream(
					clsLoader.getResource("nlpbin/").getPath() + "en-test.bin"));
			model.serialize(modelOut);
		} 
		finally {
			if (modelOut != null) 
				modelOut.close();      
		}
	}

	public void testModel() throws IOException {
		createModel();
		
		InputStream modelIn = new FileInputStream(
				clsLoader.getResource("nlpbin/en-test.bin").getFile());

		try {
			TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			NameFinderME nameFinder = new NameFinderME(model);
			String sentence[] = new String[]{
					"Pierre",
					"Vinken",
					"is",
					"61",
					"years",
					"old",
					"."
			};

			Span nameSpans[] = nameFinder.find(sentence);
			System.out.println("Number of spans - " + nameSpans.length);
			for(Span s: nameSpans) {					
				System.out.println(s.toString());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
}
