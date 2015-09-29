package xml.lowLevel.weapon;

import enums.BaseMaterialsWeapon;
import enums.BaseWeaponTypes;

public class WeaponOverride {
	private String fullName;
	private String stringToAppend;
	private int damage;
	private double reach;
	private double speed;
	private int critDamage;
	private BaseWeaponTypes baseWeaponType;
	private BaseMaterialsWeapon materialTempering;
	private BaseMaterialsWeapon materialMeltdown;
	private int meltdownOutput;
	private int meltdownInput;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getStringToAppend() {
		return stringToAppend;
	}

	public void setStringToAppend(String stringToAppend) {
		this.stringToAppend = stringToAppend;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getMeltdownOutput() {
		return meltdownOutput;
	}

	public void setMeltdownOutput(int meltdownOutput) {
		this.meltdownOutput = meltdownOutput;
	}

	public double getReach() {
		return reach;
	}

	public void setReach(double reach) {
		this.reach = reach;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getCritDamage() {
		return critDamage;
	}

	public void setCritDamage(int critDamage) {
		this.critDamage = critDamage;
	}

	public int getMeltdownInput() {
		return meltdownInput;
	}

	public void setMeltdownInput(int meltdownInput) {
		this.meltdownInput = meltdownInput;
	}

	public BaseMaterialsWeapon getMaterialTempering() {
		return materialTempering;
	}

	public void setMaterialTempering(BaseMaterialsWeapon materialTempering) {
		this.materialTempering = materialTempering;
	}

	/**
	 * @return the materialMeltdown
	 */
	public BaseMaterialsWeapon getMaterialMeltdown() {
		return materialMeltdown;
	}

	/**
	 * @param materialMeltdown
	 *            the materialMeltdown to set
	 */
	public void setMaterialMeltdown(BaseMaterialsWeapon materialMeltdown) {
		this.materialMeltdown = materialMeltdown;
	}

	/**
	 * @return the baseWeaponType
	 */
	public BaseWeaponTypes getBaseWeaponType() {
		return baseWeaponType;
	}

	/**
	 * @param baseWeaponType
	 *            the baseWeaponType to set
	 */
	public void setBaseWeaponType(BaseWeaponTypes baseWeaponType) {
		this.baseWeaponType = baseWeaponType;
	}
}
