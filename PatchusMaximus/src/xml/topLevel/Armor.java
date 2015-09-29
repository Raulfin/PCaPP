package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.armor.ArmorMasqueradeBindings;
import xml.lowLevel.armor.ArmorMaterials;
import xml.lowLevel.armor.ArmorModifiers;
import xml.lowLevel.armor.GeneralArmorSettings;
import xml.lowLevel.common.BindingListWrapper;
import xml.lowLevel.common.ExclusionListWrapper;

@XmlRootElement(namespace = "PatchusMaximus.armorXML")
public class Armor {
	private GeneralArmorSettings armor_settings;
	private ArmorMaterials armor_materials;
	private BindingListWrapper armor_material_bindings;
	private BindingListWrapper armor_modifier_bindings;
	private ArmorModifiers armor_modifiers;
	private ArmorMasqueradeBindings armor_masquerade_bindings;
	private ExclusionListWrapper reforge_exclusions;

	public GeneralArmorSettings getArmor_settings() {
		return armor_settings;
	}

	public void setArmor_settings(GeneralArmorSettings armor_settings) {
		this.armor_settings = armor_settings;
	}

	public ArmorMaterials getArmor_materials() {
		return armor_materials;
	}

	public void setArmor_materials(ArmorMaterials armor_materials) {
		this.armor_materials = armor_materials;
	}


	/**
	 * @return the armor_masquerade_bindings
	 */
	public ArmorMasqueradeBindings getArmor_masquerade_bindings() {
		return armor_masquerade_bindings;
	}

	/**
	 * @param armor_masquerade_bindings the armor_masquerade_bindings to set
	 */
	public void setArmor_masquerade_bindings(ArmorMasqueradeBindings armor_masquerade_bindings) {
		this.armor_masquerade_bindings = armor_masquerade_bindings;
	}



	/**
	 * @return the armor_modifiers
	 */
	public ArmorModifiers getArmor_modifiers() {
		return armor_modifiers;
	}

	/**
	 * @param armor_modifiers the armor_modifiers to set
	 */
	public void setArmor_modifiers(ArmorModifiers armor_modifiers) {
		this.armor_modifiers = armor_modifiers;
	}

	/**
	 * @return the armor_material_bindings
	 */
	public BindingListWrapper getArmor_material_bindings() {
		return armor_material_bindings;
	}

	/**
	 * @param armor_material_bindings the armor_material_bindings to set
	 */
	public void setArmor_material_bindings(BindingListWrapper armor_material_bindings) {
		this.armor_material_bindings = armor_material_bindings;
	}

	/**
	 * @return the armor_modifier_bindings
	 */
	public BindingListWrapper getArmor_modifier_bindings() {
		return armor_modifier_bindings;
	}

	/**
	 * @param armor_modifier_bindings the armor_modifier_bindings to set
	 */
	public void setArmor_modifier_bindings(BindingListWrapper armor_modifier_bindings) {
		this.armor_modifier_bindings = armor_modifier_bindings;
	}

	public ExclusionListWrapper getReforge_exclusions() {
		return reforge_exclusions;
	}

	public void setReforge_exclusions(ExclusionListWrapper reforge_exclusions) {
		this.reforge_exclusions = reforge_exclusions;
	}
}
