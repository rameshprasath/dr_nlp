package com.digitalreasoning.nlp;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class Tokens {
	String sentence;
	List<String> tokens;

	public Tokens() {

	}

	public Tokens(String sentence, List<String> tokens) {
		this.sentence = sentence;
		this.tokens = tokens;
	}
	
	//Convert the object to XML Element which represents complete details
	
	public Element GetXMLElement() {
		Element eSentence = new Element("SENTENCE");
		eSentence.setAttribute(new Attribute("VALUE", sentence));
		
		Element eTokens = new Element("TOKENS");
		for (String str : tokens) {
			Element eToken = new Element("TOKEN");
			eToken.addContent(str);
			eTokens.addContent(eToken);
		}
		
		eSentence.addContent(eTokens);
		return eSentence;
	}
}
