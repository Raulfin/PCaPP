package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.common.ComplexExclusionListWrapper;
import xml.lowLevel.common.ExclusionListWrapper;
import xml.lowLevel.enchanting.DirectEnchantmentBindings;
import xml.lowLevel.enchanting.EnchantmentNameBindings;
import xml.lowLevel.enchanting.ListEnchantmentBindings;

@XmlRootElement(namespace = "PatchusMaximus.enchantingXML")
public class Enchanting {
	private DirectEnchantmentBindings direct_enchantment_bindings;
	private ListEnchantmentBindings list_enchantment_bindings;
	private ExclusionListWrapper enchantment_weapon_exclusions;
	private ExclusionListWrapper enchantment_armor_exclusions;
	private ExclusionListWrapper scroll_crafting_exclusions;
	private ExclusionListWrapper staff_crafting_exclusions;
	private ExclusionListWrapper staff_crafting_disable_crafting_exclusions;
	private EnchantmentNameBindings enchantment_name_bindings;
	private ComplexExclusionListWrapper similarity_exclusions_armor;
	private ComplexExclusionListWrapper similarity_exclusions_weapon;


	/**
	 * @return the enchantment_name_bindings
	 */
	public EnchantmentNameBindings getEnchantment_name_bindings() {
		return enchantment_name_bindings;
	}

	/**
	 * @param enchantment_name_bindings the enchantment_name_bindings to set
	 */
	public void setEnchantment_name_bindings(EnchantmentNameBindings enchantment_name_bindings) {
		this.enchantment_name_bindings = enchantment_name_bindings;
	}

	/**
	 * @return the enchantment_weapon_exclusions
	 */
	public ExclusionListWrapper getEnchantment_weapon_exclusions() {
		return enchantment_weapon_exclusions;
	}

	/**
	 * @param enchantment_weapon_exclusions the enchantment_weapon_exclusions to set
	 */
	public void setEnchantment_weapon_exclusions(
			ExclusionListWrapper enchantment_weapon_exclusions) {
		this.enchantment_weapon_exclusions = enchantment_weapon_exclusions;
	}

	/**
	 * @return the scroll_crafting_exclusions
	 */
	public ExclusionListWrapper getScroll_crafting_exclusions() {
		return scroll_crafting_exclusions;
	}

	/**
	 * @param scroll_crafting_exclusions the scroll_crafting_exclusions to set
	 */
	public void setScroll_crafting_exclusions(ExclusionListWrapper scroll_crafting_exclusions) {
		this.scroll_crafting_exclusions = scroll_crafting_exclusions;
	}

	/**
	 * @return the staff_crafting_disable_crafting_exclusions
	 */
	public ExclusionListWrapper getStaff_crafting_disable_crafting_exclusions() {
		return staff_crafting_disable_crafting_exclusions;
	}

	/**
	 * @param staff_crafting_disable_crafting_exclusions the staff_crafting_disable_crafting_exclusions to set
	 */
	public void setStaff_crafting_disable_crafting_exclusions(
			ExclusionListWrapper staff_crafting_disable_crafting_exclusions) {
		this.staff_crafting_disable_crafting_exclusions = staff_crafting_disable_crafting_exclusions;
	}

	/**
	 * @return the enchantment_armor_exclusions
	 */
	public ExclusionListWrapper getEnchantment_armor_exclusions() {
		return enchantment_armor_exclusions;
	}

	/**
	 * @param enchantment_armor_exclusions the enchantment_armor_exclusions to set
	 */
	public void setEnchantment_armor_exclusions(
			ExclusionListWrapper enchantment_armor_exclusions) {
		this.enchantment_armor_exclusions = enchantment_armor_exclusions;
	}

	/**
	 * @return the staff_crafting_exclusions
	 */
	public ExclusionListWrapper getStaff_crafting_exclusions() {
		return staff_crafting_exclusions;
	}

	/**
	 * @param staff_crafting_exclusions the staff_crafting_exclusions to set
	 */
	public void setStaff_crafting_exclusions(ExclusionListWrapper staff_crafting_exclusions) {
		this.staff_crafting_exclusions = staff_crafting_exclusions;
	}

	/**
	 * @return the direct_enchantment_bindings
	 */
	public DirectEnchantmentBindings getDirect_enchantment_bindings() {
		return direct_enchantment_bindings;
	}

	/**
	 * @param direct_enchantment_bindings the direct_enchantment_bindings to set
	 */
	public void setDirect_enchantment_bindings(
			DirectEnchantmentBindings direct_enchantment_bindings) {
		this.direct_enchantment_bindings = direct_enchantment_bindings;
	}

	/**
	 * @return the list_enchantment_bindings
	 */
	public ListEnchantmentBindings getList_enchantment_bindings() {
		return list_enchantment_bindings;
	}

	/**
	 * @param list_enchantment_bindings the list_enchantment_bindings to set
	 */
	public void setList_enchantment_bindings(ListEnchantmentBindings list_enchantment_bindings) {
		this.list_enchantment_bindings = list_enchantment_bindings;
	}

	/**
	 * @return the similarity_exclusions_armor
	 */
	public ComplexExclusionListWrapper getSimilarity_exclusions_armor() {
		return similarity_exclusions_armor;
	}

	/**
	 * @param similarity_exclusions_armor the similarity_exclusions_armor to set
	 */
	public void setSimilarity_exclusions_armor(
			ComplexExclusionListWrapper similarity_exclusions_armor) {
		this.similarity_exclusions_armor = similarity_exclusions_armor;
	}

	/**
	 * @return the similarity_exclusions_weapon
	 */
	public ComplexExclusionListWrapper getSimilarity_exclusions_weapon() {
		return similarity_exclusions_weapon;
	}

	/**
	 * @param similarity_exclusions_weapon the similarity_exclusions_weapon to set
	 */
	public void setSimilarity_exclusions_weapon(
			ComplexExclusionListWrapper similarity_exclusions_weapon) {
		this.similarity_exclusions_weapon = similarity_exclusions_weapon;
	}

}
