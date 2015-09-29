package xml.lowLevel.armor;

import xml.lowLevel.common.Bindable;
import enums.BaseArmorTypes;
import enums.BaseMaterialsArmor;

public class ArmorMaterial implements Bindable{
	private String identifier;
	private int armorBase;
	private BaseMaterialsArmor materialMeltdown;
	private BaseMaterialsArmor materialTemper;
	private BaseArmorTypes type;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getArmorBase() {
		return armorBase;
	}

	public void setArmorBase(int armorBase) {
		this.armorBase = armorBase;
	}

	public BaseMaterialsArmor getMaterialMeltdown() {
		return materialMeltdown;
	}

	public void setMaterialMeltdown(BaseMaterialsArmor materialMeltdown) {
		this.materialMeltdown = materialMeltdown;
	}

	public enums.BaseMaterialsArmor getMaterialTemper() {
		return materialTemper;
	}

	public void setMaterialTemper(enums.BaseMaterialsArmor materialTemper) {
		this.materialTemper = materialTemper;
	}

	/**
	 * @return the type
	 */
	public BaseArmorTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BaseArmorTypes type) {
		this.type = type;
	}

}
