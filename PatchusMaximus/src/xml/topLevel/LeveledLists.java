package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.leveledLists.DistributionExclusionsArmor;
import xml.lowLevel.leveledLists.DistributionExclusionsSpellTome;
import xml.lowLevel.leveledLists.DistributionExclusionsWeapon;

@XmlRootElement(namespace = "PatchusMaximus.leveledListsXML")
public class LeveledLists {
	private DistributionExclusionsSpellTome distribution_exclusions_spell_tome;
	private DistributionExclusionsWeapon distribution_exclusions_weapon;
	private DistributionExclusionsArmor distribution_exclusions_armor;
	/**
	 * @return the distribution_exclusions_spell_tome
	 */
	public DistributionExclusionsSpellTome getDistribution_exclusions_spell_tome() {
		return distribution_exclusions_spell_tome;
	}
	/**
	 * @param distribution_exclusions_spell_tome the distribution_exclusions_spell_tome to set
	 */
	public void setDistribution_exclusions_spell_tome(
			DistributionExclusionsSpellTome distribution_exclusions_spell_tome) {
		this.distribution_exclusions_spell_tome = distribution_exclusions_spell_tome;
	}
	/**
	 * @return the distribution_exclusions_weapon
	 */
	public DistributionExclusionsWeapon getDistribution_exclusions_weapon() {
		return distribution_exclusions_weapon;
	}
	/**
	 * @param distribution_exclusions_weapon the distribution_exclusions_weapon to set
	 */
	public void setDistribution_exclusions_weapon(
			DistributionExclusionsWeapon distribution_exclusions_weapon) {
		this.distribution_exclusions_weapon = distribution_exclusions_weapon;
	}
	/**
	 * @return the distribution_exclusions_armor
	 */
	public DistributionExclusionsArmor getDistribution_exclusions_armor() {
		return distribution_exclusions_armor;
	}
	/**
	 * @param distribution_exclusions_armor the distribution_exclusions_armor to set
	 */
	public void setDistribution_exclusions_armor(
			DistributionExclusionsArmor distribution_exclusions_armor) {
		this.distribution_exclusions_armor = distribution_exclusions_armor;
	}

}
