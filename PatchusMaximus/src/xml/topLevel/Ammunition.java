package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.ammunition.AmmunitionMaterials;
import xml.lowLevel.ammunition.AmmunitionModifiers;
import xml.lowLevel.ammunition.AmmunitionTypes;
import xml.lowLevel.common.BindingListWrapper;
import xml.lowLevel.common.ExclusionListWrapper;

@XmlRootElement(namespace = "PatchusMaximus.ammunitionXML")
public class Ammunition {
	private AmmunitionTypes ammunition_types;
	private AmmunitionMaterials ammunition_materials;
	private AmmunitionModifiers ammunition_modifiers;
	private BindingListWrapper ammunition_type_bindings;
	private BindingListWrapper ammunition_material_bindings;
	private BindingListWrapper ammunition_modifier_bindings;
	private ExclusionListWrapper ammunition_exclusions_multiplication;


	public AmmunitionMaterials getAmmunition_materials() {
		return ammunition_materials;
	}

	public void setAmmunition_materials(AmmunitionMaterials ammunition_materials) {
		this.ammunition_materials = ammunition_materials;
	}

	public AmmunitionModifiers getAmmunition_modifiers() {
		return ammunition_modifiers;
	}

	public void setAmmunition_modifiers(AmmunitionModifiers ammunition_modifiers) {
		this.ammunition_modifiers = ammunition_modifiers;
	}

	public AmmunitionTypes getAmmunition_types() {
		return ammunition_types;
	}

	public void setAmmunition_types(AmmunitionTypes ammunition_types) {
		this.ammunition_types = ammunition_types;
	}

	/**
	 * @return the ammunition_exclusions_multiplication
	 */
	public ExclusionListWrapper getAmmunition_exclusions_multiplication() {
		return ammunition_exclusions_multiplication;
	}

	/**
	 * @param ammunition_exclusions_multiplication the ammunition_exclusions_multiplication to set
	 */
	public void setAmmunition_exclusions_multiplication(
			ExclusionListWrapper ammunition_exclusions_multiplication) {
		this.ammunition_exclusions_multiplication = ammunition_exclusions_multiplication;
	}

	/**
	 * @return the ammunition_material_bindings
	 */
	public BindingListWrapper getAmmunition_material_bindings() {
		return ammunition_material_bindings;
	}

	/**
	 * @param ammunition_material_bindings the ammunition_material_bindings to set
	 */
	public void setAmmunition_material_bindings(
			BindingListWrapper ammunition_material_bindings) {
		this.ammunition_material_bindings = ammunition_material_bindings;
	}

	/**
	 * @return the ammunition_type_bindings
	 */
	public BindingListWrapper getAmmunition_type_bindings() {
		return ammunition_type_bindings;
	}

	/**
	 * @param ammunition_type_bindings the ammunition_type_bindings to set
	 */
	public void setAmmunition_type_bindings(BindingListWrapper ammunition_type_bindings) {
		this.ammunition_type_bindings = ammunition_type_bindings;
	}

	/**
	 * @return the ammunition_modifier_bindings
	 */
	public BindingListWrapper getAmmunition_modifier_bindings() {
		return ammunition_modifier_bindings;
	}

	/**
	 * @param ammunition_modifier_bindings the ammunition_modifier_bindings to set
	 */
	public void setAmmunition_modifier_bindings(
			BindingListWrapper ammunition_modifier_bindings) {
		this.ammunition_modifier_bindings = ammunition_modifier_bindings;
	}


}
