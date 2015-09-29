package xml.lowLevel.armor;

import enums.MasqueradeFactions;

public class ArmorMasqueradeBinding {
	private String substringArmor;
	private MasqueradeFactions masqueradeFaction;
	/**
	 * @return the substringArmor
	 */
	public String getSubstringArmor() {
		return substringArmor;
	}
	/**
	 * @param substringArmor the substringArmor to set
	 */
	public void setSubstringArmor(String substringArmor) {
		this.substringArmor = substringArmor;
	}
	/**
	 * @return the masqueradeFaction
	 */
	public MasqueradeFactions getMasqueradeFaction() {
		return masqueradeFaction;
	}
	/**
	 * @param masqueradeFaction the masqueradeFaction to set
	 */
	public void setMasqueradeFaction(MasqueradeFactions masqueradeFaction) {
		this.masqueradeFaction = masqueradeFaction;
	}

}
