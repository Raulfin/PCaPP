package xml.lowLevel.weapon;

import xml.lowLevel.common.Bindable;
import enums.BaseWeaponTypes;
import enums.EffectTiers;
import enums.WeaponClasses;

public class WeaponType implements Bindable{

	private BaseWeaponTypes baseWeaponType;

	private EffectTiers debuffTier;
	private EffectTiers bleedTier;
	private EffectTiers staggerTier;

	private WeaponClasses weaponClass;

	private String identifier;

	private double speedBase;
	private double damageBase;
	private double reachBase;

	private int meltdownOutput = 1;
	private int meltdownInput = 1;
	
	private double critDamageFactor = 1.0;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getMeltdownOutput() {
		return meltdownOutput;
	}

	public void setMeltdownOutput(int meltdownOutput) {
		this.meltdownOutput = meltdownOutput;
	}

	public double getCritDamageFactor() {
		return critDamageFactor;
	}

	public void setCritDamageFactor(double critDamageFactor) {
		this.critDamageFactor = critDamageFactor;
	}

	public double getSpeedBase() {
		return speedBase;
	}

	public void setSpeedBase(double speedBase) {
		this.speedBase = speedBase;
	}

	public double getDamageBase() {
		return damageBase;
	}

	public void setDamageBase(double damageBase) {
		this.damageBase = damageBase;
	}

	public double getReachBase() {
		return reachBase;
	}

	public void setReachBase(double reachBase) {
		this.reachBase = reachBase;
	}

	public int getMeltdownInput() {
		return meltdownInput;
	}

	public void setMeltdownInput(int meltdownInput) {
		this.meltdownInput = meltdownInput;
	}

	/**
	 * @return the baseWeaponType
	 */
	public BaseWeaponTypes getBaseWeaponType() {
		return baseWeaponType;
	}

	/**
	 * @param baseWeaponType the baseWeaponType to set
	 */
	public void setBaseWeaponType(BaseWeaponTypes baseWeaponType) {
		this.baseWeaponType = baseWeaponType;
	}

	/**
	 * @return the weaponClass
	 */
	public WeaponClasses getWeaponClass() {
		return weaponClass;
	}

	/**
	 * @param weaponClass the weaponClass to set
	 */
	public void setWeaponClass(WeaponClasses weaponClass) {
		this.weaponClass = weaponClass;
	}

	/**
	 * @return the debuffTier
	 */
	public EffectTiers getDebuffTier() {
		return debuffTier;
	}

	/**
	 * @param debuffTier the debuffTier to set
	 */
	public void setDebuffTier(EffectTiers debuffTier) {
		this.debuffTier = debuffTier;
	}

	/**
	 * @return the bleedTier
	 */
	public EffectTiers getBleedTier() {
		return bleedTier;
	}

	/**
	 * @param bleedTier the bleedTier to set
	 */
	public void setBleedTier(EffectTiers bleedTier) {
		this.bleedTier = bleedTier;
	}

	/**
	 * @return the staggerTier
	 */
	public EffectTiers getStaggerTier() {
		return staggerTier;
	}

	/**
	 * @param staggerTier the staggerTier to set
	 */
	public void setStaggerTier(EffectTiers staggerTier) {
		this.staggerTier = staggerTier;
	}
}
