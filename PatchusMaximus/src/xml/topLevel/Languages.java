package xml.topLevel;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.language.Language;

@XmlRootElement(namespace="PatchusMaximus.languageXML")
public class Languages {
	private String default_language;
	private ArrayList<Language> language;

	public String getDefault_language() {
		return default_language;
	}

	public void setDefault_language(String default_language) {
		this.default_language = default_language;
	}

	public ArrayList<Language> getLanguage() {
		return language;
	}

	public void setLanguage(ArrayList<Language> language) {
		this.language = language;
	}
}
