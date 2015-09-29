package enums;

import skyproc.FormID;
import util.Statics;

public enum MasqueradeFactions {
	BANDIT(Statics.kwMasqueradeBandit), CULTIST(Statics.kwMasqueradeCultist), DAWNGUARD(
			Statics.kwMasqueradeDawnguard), FALMER(Statics.kwMasqueradeFalmer), FORSWORN(
			Statics.kwMasqueradeForsworn), IMPERIAL(
			Statics.kwMasqueradeImperial), STORMCLOAK(
			Statics.kwMasqueradeStormcloak), THALMOR(
			Statics.kwMasqueradeThalmor), VAMPIRE(Statics.kwMasqueradeVampire),
			NONE(null);

	private FormID factionKeyword = null;

	private MasqueradeFactions(FormID factionKeyword) {
		this.factionKeyword = factionKeyword;
	}

	/**
	 * @return the factionKeyword
	 */
	public FormID getFactionKeyword() {
		return factionKeyword;
	}
}
