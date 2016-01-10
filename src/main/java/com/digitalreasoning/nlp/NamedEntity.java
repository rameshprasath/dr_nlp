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
		Element eToken = new Element("SENTENCE");
		eToken.setAttribute(new Attribute("VALUE", sentence));
		
		Element eSpans = new Element("SPANS");
		for (Span ne : neSpans) {
			Element eSpan = new Element("SPAN");
			eSpan.setAttribute(new Attribute("START", Integer.toString(ne.getStart())));
			eSpan.setAttribute(new Attribute("END", Integer.toString(ne.getEnd())));
			eSpan.setAttribute(new Attribute("TYPE", ne.getType()));
			eSpans.addContent(eSpan);
		}
		
		eToken.addContent(eSpans);
		return eToken;
	}
}
