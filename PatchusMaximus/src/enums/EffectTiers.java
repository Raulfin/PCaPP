package enums;

import skyproc.FormID;
import util.Statics;

public enum EffectTiers {
	ZERO(null, null, null), ONE(Statics.kwWeaponBleedTier1,
			Statics.kwWeaponDebuffTier1, Statics.kwWeaponStaggerTier1), TWO(
			Statics.kwWeaponBleedTier2, Statics.kwWeaponDebuffTier2,
			Statics.kwWeaponStaggerTier2), THREE(Statics.kwWeaponBleedTier3,
			Statics.kwWeaponDebuffTier3, Statics.kwWeaponStaggerTier3);

	private FormID bleedingKW;
	private FormID debuffKW;
	private FormID staggerKW;

	private EffectTiers(FormID bleedingKW, FormID debuffKW, FormID staggerKW) {
		this.bleedingKW = bleedingKW;
		this.debuffKW = debuffKW;
		this.staggerKW = staggerKW;
	}

	/**
	 * @return the bleedingKW
	 */
	public FormID getBleedingKW() {
		return bleedingKW;
	}

	/**
	 * @return the debuffKW
	 */
	public FormID getDebuffKW() {
		return debuffKW;
	}

	/**
	 * @return the staggerKW
	 */
	public FormID getStaggerKW() {
		return staggerKW;
	}
}
