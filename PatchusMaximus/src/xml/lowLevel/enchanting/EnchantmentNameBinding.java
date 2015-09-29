package xml.lowLevel.enchanting;

import enums.EnchantmentNameTypes;

public class EnchantmentNameBinding {
	private String name;
	private String edidEnchantment;
	private EnchantmentNameTypes type;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the edidEnchantment
	 */
	public String getEdidEnchantment() {
		return edidEnchantment;
	}
	/**
	 * @param edidEnchantment the edidEnchantment to set
	 */
	public void setEdidEnchantment(String edidEnchantment) {
		this.edidEnchantment = edidEnchantment;
	}
	/**
	 * @return the type
	 */
	public EnchantmentNameTypes getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(EnchantmentNameTypes type) {
		this.type = type;
	}
}
