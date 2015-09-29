package xml.lowLevel.ammunition;

import xml.lowLevel.common.Bindable;
import enums.BaseProjectileTypes;

public class AmmunitionType implements Bindable {
	private String identifier;
	private double rangeBase;
	private double speedBase;
	private double damageBase;
	private double gravityBase;
	private BaseProjectileTypes type;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public double getRangeBase() {
		return rangeBase;
	}

	public void setRangeBase(double rangeBase) {
		this.rangeBase = rangeBase;
	}

	public double getSpeedBase() {
		return speedBase;
	}

	public void setSpeedBase(double speedBase) {
		this.speedBase = speedBase;
	}

	public double getDamageBase() {
		return damageBase;
	}

	public void setDamageBase(double damageBase) {
		this.damageBase = damageBase;
	}

	public double getGravityBase() {
		return gravityBase;
	}

	public void setGravityBase(double gravityBase) {
		this.gravityBase = gravityBase;
	}

	/**
	 * @return the type
	 */
	public BaseProjectileTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BaseProjectileTypes type) {
		this.type = type;
	}
}
