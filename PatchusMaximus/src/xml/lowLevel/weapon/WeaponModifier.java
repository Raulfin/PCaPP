package xml.lowLevel.weapon;

import xml.lowLevel.common.Bindable;

public class WeaponModifier implements Bindable{
	private String identifier;
	private double factorDamage;
	private double factorCritDamage;
	private double factorWeight;
	private double factorReach;
	private double factorAttackSpeed;
	private double factorValue;
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return the factorDamage
	 */
	public double getFactorDamage() {
		return factorDamage;
	}
	/**
	 * @param factorDamage the factorDamage to set
	 */
	public void setFactorDamage(double factorDamage) {
		this.factorDamage = factorDamage;
	}
	/**
	 * @return the factorCritDamage
	 */
	public double getFactorCritDamage() {
		return factorCritDamage;
	}
	/**
	 * @param factorCritDamage the factorCritDamage to set
	 */
	public void setFactorCritDamage(double factorCritDamage) {
		this.factorCritDamage = factorCritDamage;
	}
	/**
	 * @return the factorWeight
	 */
	public double getFactorWeight() {
		return factorWeight;
	}
	/**
	 * @param factorWeight the factorWeight to set
	 */
	public void setFactorWeight(double factorWeight) {
		this.factorWeight = factorWeight;
	}
	/**
	 * @return the factorReach
	 */
	public double getFactorReach() {
		return factorReach;
	}
	/**
	 * @param factorReach the factorReach to set
	 */
	public void setFactorReach(double factorReach) {
		this.factorReach = factorReach;
	}
	/**
	 * @return the factorAttackSpeed
	 */
	public double getFactorAttackSpeed() {
		return factorAttackSpeed;
	}
	/**
	 * @param factorAttackSpeed the factorAttackSpeed to set
	 */
	public void setFactorAttackSpeed(double factorAttackSpeed) {
		this.factorAttackSpeed = factorAttackSpeed;
	}
	/**
	 * @return the factorValue
	 */
	public double getFactorValue() {
		return factorValue;
	}
	/**
	 * @param factorValue the factorValue to set
	 */
	public void setFactorValue(double factorValue) {
		this.factorValue = factorValue;
	}
}
