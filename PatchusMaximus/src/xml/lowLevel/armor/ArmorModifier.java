package xml.lowLevel.armor;

import xml.lowLevel.common.Bindable;

public class ArmorModifier implements Bindable{
	private String identifier;
	private double factorWeight;
	private double factorValue;
	private double factorArmor;
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
	/**
	 * @return the factorArmor
	 */
	public double getFactorArmor() {
		return factorArmor;
	}
	/**
	 * @param factorArmor the factorArmor to set
	 */
	public void setFactorArmor(double factorArmor) {
		this.factorArmor = factorArmor;
	}
}
