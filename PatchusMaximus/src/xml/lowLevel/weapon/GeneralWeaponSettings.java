package xml.lowLevel.weapon;

public class GeneralWeaponSettings {
	private double baseDamageLightWeaponry;
	private double baseDamageHeavyWeaponry;
	private double baseDamageRangedWeaponry;
	
	private double damageFactorLightWeaponry;
	private double damageFactorHeavyWeaponry;
	private double damageFactorRangedWeaponry;

	private boolean appendTypeToName = true;

	public double getDamageFactorLightWeaponry() {
		return damageFactorLightWeaponry;
	}

	public void setDamageFactorLightWeaponry(double damageFactorLightWeaponry) {
		this.damageFactorLightWeaponry = damageFactorLightWeaponry;
	}

	public double getDamageFactorHeavyWeaponry() {
		return damageFactorHeavyWeaponry;
	}

	public void setDamageFactorHeavyWeaponry(double damageFactorHeavyWeaponry) {
		this.damageFactorHeavyWeaponry = damageFactorHeavyWeaponry;
	}

	public boolean isAppendTypeToName() {
		return appendTypeToName;
	}

	public void setAppendTypeToName(boolean appendTypeToName) {
		this.appendTypeToName = appendTypeToName;
	}

	public double getDamageFactorRangedWeaponry() {
		return damageFactorRangedWeaponry;
	}

	public void setDamageFactorRangedWeaponry(double damageFactorRangedWeaponry) {
		this.damageFactorRangedWeaponry = damageFactorRangedWeaponry;
	}


	public void setBaseDamageRangedWeaponry(int baseDamageRangedWeaponry) {
		this.baseDamageRangedWeaponry = baseDamageRangedWeaponry;
	}

	public double getBaseDamageLightWeaponry() {
		return baseDamageLightWeaponry;
	}

	public void setBaseDamageLightWeaponry(double baseDamageLightWeaponry) {
		this.baseDamageLightWeaponry = baseDamageLightWeaponry;
	}

	public double getBaseDamageHeavyWeaponry() {
		return baseDamageHeavyWeaponry;
	}

	public void setBaseDamageHeavyWeaponry(double baseDamageHeavyWeaponry) {
		this.baseDamageHeavyWeaponry = baseDamageHeavyWeaponry;
	}

	public double getBaseDamageRangedWeaponry() {
		return baseDamageRangedWeaponry;
	}

	public void setBaseDamageRangedWeaponry(double baseDamageRangedWeaponry) {
		this.baseDamageRangedWeaponry = baseDamageRangedWeaponry;
	}

}
