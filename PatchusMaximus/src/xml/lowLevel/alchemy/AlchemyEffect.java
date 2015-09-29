package xml.lowLevel.alchemy;

import xml.lowLevel.common.Bindable;

public class AlchemyEffect implements Bindable{
	private String identifier;
	private double baseMagnitude;
	private double baseDuration;
	private double baseCost;
	private boolean allowIngredientVariation = true;
	private boolean allowPotionMultiplier = true;

	public double getBaseMagnitude() {
		return baseMagnitude;
	}

	public void setBaseMagnitude(double baseMagnitude) {
		this.baseMagnitude = baseMagnitude;
	}

	public double getBaseDuration() {
		return baseDuration;
	}

	public void setBaseDuration(double baseDuration) {
		this.baseDuration = baseDuration;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isAllowIngredientVariation() {
		return allowIngredientVariation;
	}

	public void setAllowIngredientVariation(boolean allowIngredientVariation) {
		this.allowIngredientVariation = allowIngredientVariation;
	}

	public boolean isAllowPotionMultiplier() {
		return allowPotionMultiplier;
	}

	public void setAllowPotionMultiplier(boolean allowPotionMultiplier) {
		this.allowPotionMultiplier = allowPotionMultiplier;
	}

	/**
	 * @return the baseCost
	 */
	public double getBaseCost() {
		return baseCost;
	}

	/**
	 * @param baseCost the baseCost to set
	 */
	public void setBaseCost(double baseCost) {
		this.baseCost = baseCost;
	}
}
