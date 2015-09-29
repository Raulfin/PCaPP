package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.common.BindingListWrapper;
import xml.lowLevel.common.ExclusionListWrapper;
import xml.lowLevel.weapon.GeneralWeaponSettings;
import xml.lowLevel.weapon.WeaponMaterials;
import xml.lowLevel.weapon.WeaponModifiers;
import xml.lowLevel.weapon.WeaponOverrides;
import xml.lowLevel.weapon.WeaponTypes;

@XmlRootElement(namespace = "PatchusMaximus.weaponsXML")
public class Weapons {
	private GeneralWeaponSettings weapon_settings;
	private WeaponMaterials weapon_materials;
	private WeaponTypes weapon_types;
	private WeaponModifiers weapon_modifiers;
	private BindingListWrapper weapon_type_bindings;
	private BindingListWrapper weapon_material_bindings;
	private BindingListWrapper weapon_modifier_bindings;
	private WeaponOverrides weapon_overrides;
	private ExclusionListWrapper reforge_exclusions;
	

	public WeaponMaterials getWeapon_materials() {
		return weapon_materials;
	}

	public void setWeapon_materials(WeaponMaterials weapon_materials) {
		this.weapon_materials = weapon_materials;
	}

	public GeneralWeaponSettings getWeapon_settings() {
		return weapon_settings;
	}

	public void setWeapon_settings(GeneralWeaponSettings weapon_settings) {
		this.weapon_settings = weapon_settings;
	}

	public WeaponTypes getWeapon_types() {
		return weapon_types;
	}

	public void setWeapon_types(WeaponTypes weapon_types) {
		this.weapon_types = weapon_types;
	}


	public WeaponOverrides getWeapon_overrides() {
		return weapon_overrides;
	}

	public void setWeapon_overrides(WeaponOverrides weapon_overrides) {
		this.weapon_overrides = weapon_overrides;
	}

	/**
	 * @return the weapon_modifiers
	 */
	public WeaponModifiers getWeapon_modifiers() {
		return weapon_modifiers;
	}

	/**
	 * @param weapon_modifiers the weapon_modifiers to set
	 */
	public void setWeapon_modifiers(WeaponModifiers weapon_modifiers) {
		this.weapon_modifiers = weapon_modifiers;
	}

	/**
	 * @return the weapon_material_bindings
	 */
	public BindingListWrapper getWeapon_material_bindings() {
		return weapon_material_bindings;
	}

	/**
	 * @param weapon_material_bindings the weapon_material_bindings to set
	 */
	public void setWeapon_material_bindings(BindingListWrapper weapon_material_bindings) {
		this.weapon_material_bindings = weapon_material_bindings;
	}

	/**
	 * @return the weapon_type_bindings
	 */
	public BindingListWrapper getWeapon_type_bindings() {
		return weapon_type_bindings;
	}

	/**
	 * @param weapon_type_bindings the weapon_type_bindings to set
	 */
	public void setWeapon_type_bindings(BindingListWrapper weapon_type_bindings) {
		this.weapon_type_bindings = weapon_type_bindings;
	}

	/**
	 * @return the weapon_modifier_bindings
	 */
	public BindingListWrapper getWeapon_modifier_bindings() {
		return weapon_modifier_bindings;
	}

	/**
	 * @param weapon_modifier_bindings the weapon_modifier_bindings to set
	 */
	public void setWeapon_modifier_bindings(BindingListWrapper weapon_modifier_bindings) {
		this.weapon_modifier_bindings = weapon_modifier_bindings;
	}

	public ExclusionListWrapper getReforge_exclusions() {
		return reforge_exclusions;
	}

	public void setReforge_exclusions(ExclusionListWrapper reforge_exclusions) {
		this.reforge_exclusions = reforge_exclusions;
	}
}
