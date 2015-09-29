package xml.lowLevel.alchemy;

import xml.lowLevel.common.Bindable;

public class PotionMultiplier implements Bindable{
	private String identifier;
	private double multiplierMagnitude;
	private double multiplierDuration;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public double getMultiplierMagnitude() {
		return multiplierMagnitude;
	}

	public void setMultiplierMagnitude(double multiplierMagnitude) {
		this.multiplierMagnitude = multiplierMagnitude;
	}

	public double getMultiplierDuration() {
		return multiplierDuration;
	}

	public void setMultiplierDuration(double multiplierDuration) {
		this.multiplierDuration = multiplierDuration;
	}
}
