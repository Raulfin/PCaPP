package xml.lowLevel.ammunition;

import xml.lowLevel.common.Bindable;

public class AmmunitionModifier implements Bindable{
	private String identifier;
	private double rangeModifier;
	private double speedModifier;
	private double damageModifier;
	private double gravityModifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public double getRangeModifier() {
		return rangeModifier;
	}

	public void setRangeModifier(double rangeModifier) {
		this.rangeModifier = rangeModifier;
	}

	public double getSpeedModifier() {
		return speedModifier;
	}

	public void setSpeedModifier(double speedModifier) {
		this.speedModifier = speedModifier;
	}

	public double getDamageModifier() {
		return damageModifier;
	}

	public void setDamageModifier(double damageModifier) {
		this.damageModifier = damageModifier;
	}

	public double getGravityModifier() {
		return gravityModifier;
	}

	public void setGravityModifier(double gravityModifier) {
		this.gravityModifier = gravityModifier;
	}
}
