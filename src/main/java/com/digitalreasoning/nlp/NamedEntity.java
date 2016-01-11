package com.digitalreasoning.nlp;

import org.jdom.Attribute;
import org.jdom.Element;

import opennlp.tools.util.Span;

public class NamedEntity {
	String sentence;
	Span[] neSpans;
		
	public NamedEntity(String sentence, Span[] spans) {
		this.sentence = sentence;
		this.neSpans = spans;
	}
	
	public NamedEntity() {
		
	}

	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public Span[] getNESpans() {
		return neSpans;
	}
	public void setNESpans(Span[] neSpans) {
		this.neSpans = neSpans;
	}

	//Convert the object to XML Element which represents complete details
	
	public Element GetXMLElement() {
		NLPOperation nlp = new NLPOperation();
		String[] tokens = nlp.GetTokens(sentence);
				
		Element eToken = new Element("SENTENCE");
		eToken.setAttribute(new Attribute("VALUE", sentence));
		
		String hlToken;
		Element eSpans = new Element("SPANS");
		for (Span ne : neSpans) {
			hlToken = "";
			Element eSpan = new Element("SPAN");
			eSpan.setAttribute(new Attribute("START", Integer.toString(ne.getStart())));
			eSpan.setAttribute(new Attribute("END", Integer.toString(ne.getEnd())));
			eSpan.setAttribute(new Attribute("TYPE", ne.getType()));
			
			for(int i = ne.getStart(); i < ne.getEnd() && i < tokens.length; i++) {
				hlToken = hlToken + " " + tokens[i];
			}
			eSpan.setAttribute(new Attribute("ENTITY", hlToken.trim())); 
			eSpans.addContent(eSpan);
		}
		
		eToken.addContent(eSpans);
		return eToken;
	}
}
