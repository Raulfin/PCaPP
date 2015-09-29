package xml.lowLevel.language;

import java.util.ArrayList;

public class Language {
	private String languageID;
	private ArrayList<StringBinding> stringBinding;

	public String getLanguageID() {
		return languageID;
	}

	public void setLanguageID(String languageID) {
		this.languageID = languageID;
	}

	public ArrayList<StringBinding> getStringBinding() {
		return stringBinding;
	}

	public void setStringBinding(ArrayList<StringBinding> stringBinding) {
		this.stringBinding = stringBinding;
	}
}
