package xml.lowLevel.weapon;

import xml.lowLevel.common.Bindable;
import enums.BaseMaterialsWeapon;

public class WeaponMaterial implements Bindable {

	private String identifier;
	private BaseMaterialsWeapon materialTemper;
	private BaseMaterialsWeapon materialMeltdown;

	private double damageModifier;
	private double speedModifier;
	private double reachModifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public double getReachModifier() {
		return reachModifier;
	}

	public void setReachModifier(double reachModifier) {
		this.reachModifier = reachModifier;
	}

	public double getSpeedModifier() {
		return speedModifier;
	}

	public void setSpeedModifier(double speedModifier) {
		this.speedModifier = speedModifier;
	}

	public double getDamageModifier() {
		return damageModifier;
	}

	public void setDamageModifier(double damageModifier) {
		this.damageModifier = damageModifier;
	}

	public BaseMaterialsWeapon getMaterialTemper() {
		return materialTemper;
	}

	public void setMaterialTemper(BaseMaterialsWeapon materialTemper) {
		this.materialTemper = materialTemper;
	}

	public BaseMaterialsWeapon getMaterialMeltdown() {
		return materialMeltdown;
	}

	public void setMaterialMeltdown(BaseMaterialsWeapon materialMeltdown) {
		this.materialMeltdown = materialMeltdown;
	}
}
