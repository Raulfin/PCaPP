package enums;

import skyproc.FormID;
import util.Statics;

public enum BaseMaterialsArmor {
	ADVANCED(Statics.perkSmithingAdvanced, Statics.ingotCorundum,
			Statics.kwCraftingSmelter, Statics.ingotCorundum), NONE(null, null,
			null, null), IRON(null, Statics.ingotIron,
			Statics.kwCraftingSmelter, Statics.ingotIron), STEEL(
			Statics.perkSmithingSteel, Statics.ingotSteel,
			Statics.kwCraftingSmelter, Statics.ingotSteel), DWARVEN(
			Statics.perkSmithingDwarven, Statics.ingotDwarven,
			Statics.kwCraftingSmelter, Statics.ingotDwarven), FALMER(
			Statics.perkSmithingAdvanced, Statics.chaurusChitin,
			Statics.kwCraftingSmelter, Statics.chaurusChitin), ORCISH(
			Statics.perkSmithingOrcish, Statics.ingotOrichalcum,
			Statics.kwCraftingSmelter, Statics.ingotOrichalcum), STEELPLATE(
			Statics.perkSmithingAdvanced, Statics.ingotSteel,
			Statics.kwCraftingSmelter, Statics.ingotSteel), EBONY(
			Statics.perkSmithingEbony, Statics.ingotEbony,
			Statics.kwCraftingSmelter, Statics.ingotEbony), DRAGONPLATE(
			Statics.perkSmithingDragon, Statics.dragonbone,
			Statics.kwCraftingSmelter, Statics.dragonbone), DAEDRIC(
			Statics.perkSmithingDaedric, Statics.ingotEbony,
			Statics.kwCraftingSmelter, Statics.ingotEbony), FUR(null,
			Statics.leatherStrips, Statics.kwCraftingTanningRack,
			Statics.leatherStrips), HIDE(null, Statics.leatherStrips,
			Statics.kwCraftingTanningRack, Statics.leatherStrips), LEATHER(
			Statics.perkSmithingLeather, Statics.leatherStrips,
			Statics.kwCraftingTanningRack, Statics.leatherStrips), ELVEN(
			Statics.perkSmithingElven, Statics.ingotMoonstone,
			Statics.kwCraftingSmelter, Statics.ingotMoonstone), SCALED(
			Statics.perkSmithingAdvanced, Statics.ingotCorundum,
			Statics.kwCraftingSmelter, Statics.ingotCorundum), GLASS(
			Statics.perkSmithingGlass, Statics.ingotMalachite,
			Statics.kwCraftingSmelter, Statics.ingotMalachite), DRAGONSCALE(
			Statics.perkSmithingDragon, Statics.dragonscale,
			Statics.kwCraftingSmelter, Statics.dragonscale), STALHRIM_HEAVY(
			Statics.perkSmithingEbony, Statics.oreStalhrim,
			Statics.kwCraftingSmelter, Statics.oreStalhrim), STALHRIM_LIGHT(
			Statics.perkSmithingEbony, Statics.oreStalhrim,
			Statics.kwCraftingSmelter, Statics.oreStalhrim), NORDIC_HEAVY(
			Statics.perkSmithingAdvanced, Statics.ingotCorundum,
			Statics.kwCraftingSmelter, Statics.ingotSteel), BONEMOLD_HEAVY(
			Statics.perkSmithingAdvanced, Statics.ingotIron,
			Statics.kwCraftingSmelter, Statics.ingotIron), CHITIN(
			Statics.perkSmithingAdvanced, Statics.ingotCorundum,
			Statics.kwCraftingSmelter, Statics.ingotCorundum), SILVER(
			Statics.perkSmithingSilver, Statics.ingotSilver,
			Statics.kwCraftingSmelter, Statics.ingotSilver), GOLD(
			Statics.perkSmithingSilver, Statics.ingotGold,
			Statics.kwCraftingSmelter, Statics.ingotGold),
			WOOD(null, Statics.charcoal, Statics.kwCraftingSmelter, Statics.firewood);
	;

	private FormID relatedSmithingPerk;
	private FormID relatedMeltdownProduct;
	private FormID relatedMeltdownCraftingStation;
	private FormID relatedTemperingInput;

	private BaseMaterialsArmor(FormID relatedSmithingPerk,
			FormID relatedMeltdownProduct,
			FormID relatedMeltdownCraftingStation, FormID relatedTemperingInput) {
		this.relatedMeltdownCraftingStation = relatedMeltdownCraftingStation;
		this.relatedMeltdownProduct = relatedMeltdownProduct;
		this.relatedSmithingPerk = relatedSmithingPerk;
		this.relatedTemperingInput = relatedTemperingInput;
	}

	public FormID getRelatedSmithingPerk() {
		return relatedSmithingPerk;
	}

	public FormID getRelatedMeltdownProduct() {
		return relatedMeltdownProduct;
	}

	public FormID getRelatedMeltdownCraftingStation() {
		return relatedMeltdownCraftingStation;
	}

	/**
	 * @return the relatedTemperingInput
	 */
	public FormID getRelatedTemperingInput() {
		return relatedTemperingInput;
	}

	public static boolean isMaterialHeavy(BaseMaterialsArmor a) {

		if ((a == ADVANCED) || (a == FUR) || (a == HIDE) || (a == ELVEN)
				|| (a == SCALED) || (a == GLASS) || (a == DRAGONSCALE)
				|| (a == STALHRIM_LIGHT)) {
			return false;
		}

		return true;
	}
}
