package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "PatchusMaximus.generalSettingsXML")
public class GeneralSettings {

	private boolean useMage;
	private boolean useWarrior;
	private boolean useThief;
	private String outputLanguage;
	private boolean removeUnspecificStartingSpells;
	
	public boolean isUseMage() {
		return useMage;
	}
	public void setUseMage(boolean useMage) {
		this.useMage = useMage;
	}
	public boolean isUseWarrior() {
		return useWarrior;
	}
	public void setUseWarrior(boolean useWarrior) {
		this.useWarrior = useWarrior;
	}
	public boolean isUseThief() {
		return useThief;
	}
	public void setUseThief(boolean useThief) {
		this.useThief = useThief;
	}
	public String getOutputLanguage() {
		return outputLanguage;
	}
	public void setOutputLanguage(String outputLanguage) {
		this.outputLanguage = outputLanguage;
	}
	public boolean isRemoveUnspecificStartingSpells() {
		return removeUnspecificStartingSpells;
	}
	public void setRemoveUnspecificStartingSpells(
			boolean removeUnspecificStartingSpells) {
		this.removeUnspecificStartingSpells = removeUnspecificStartingSpells;
	}
	
	
}
