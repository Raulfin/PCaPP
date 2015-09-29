package enums;

import skyproc.FormID;
import util.Statics;

public enum BaseMaterialsWeapon {
	NONE(null,null,null, null),
	IRON(null, Statics.ingotIron, Statics.kwCraftingSmelter, Statics.ingotIron),
	STEEL(Statics.perkSmithingSteel, Statics.ingotSteel, Statics.kwCraftingSmelter,Statics.ingotSteel),
	DWARVEN(Statics.perkSmithingDwarven, Statics.ingotDwarven,Statics.kwCraftingSmelter, Statics.ingotDwarven),
	FALMER(Statics.perkSmithingAdvanced, Statics.chaurusChitin, Statics.kwCraftingSmelter, Statics.chaurusChitin),
	ORCISH(Statics.perkSmithingOrcish, Statics.ingotOrichalcum, Statics.kwCraftingSmelter, Statics.ingotOrichalcum),
	EBONY(Statics.perkSmithingEbony, Statics.ingotEbony, Statics.kwCraftingSmelter, Statics.ingotEbony),
	DRAGONPLATE(Statics.perkSmithingDragon, Statics.dragonbone, Statics.kwCraftingSmelter, Statics.dragonbone),
	DAEDRIC(Statics.perkSmithingDaedric, Statics.ingotEbony, Statics.kwCraftingSmelter, Statics.ingotEbony),
	ELVEN(Statics.perkSmithingElven, Statics.ingotMoonstone,Statics.kwCraftingSmelter, Statics.ingotMoonstone),
	GLASS(Statics.perkSmithingGlass, Statics.ingotMalachite, Statics.kwCraftingSmelter, Statics.ingotMalachite),
	DRAGONSCALE(Statics.perkSmithingDragon, Statics.dragonscale, Statics.kwCraftingSmelter, Statics.dragonscale),
	STALHRIM(Statics.perkSmithingEbony, Statics.oreStalhrim,Statics.kwCraftingSmelter, Statics.oreStalhrim),
	WOOD(null, Statics.charcoal, Statics.kwCraftingSmelter, Statics.firewood),
	ADVANCED(Statics.perkSmithingAdvanced, Statics.ingotCorundum, Statics.kwCraftingSmelter, Statics.ingotCorundum),
	SILVER(Statics.perkSmithingSilver, Statics.ingotSilver, Statics.kwCraftingSmelter, Statics.ingotSilver),
	REFINED_SILVER(Statics.perkSmithingSilverRefined, Statics.ingotSilver, Statics.kwCraftingSmelter, Statics.ingotSilver),
	DRAUGR(Statics.perkSmithingSteel, Statics.ingotSteel, Statics.kwCraftingSmelter, Statics.ingotSteel),
	CHITIN(Statics.perkSmithingAdvanced, Statics.ingotCorundum, Statics.kwCraftingSmelter, Statics.ingotCorundum), 
	GOLD(Statics.perkSmithingSilver, Statics.ingotGold, Statics.kwCraftingSmelter, Statics.ingotGold),
	BONEMOLD_HEAVY(Statics.perkSmithingAdvanced, Statics.ingotIron, Statics.kwCraftingSmelter, Statics.ingotIron);

	private FormID relatedSmithingPerk;
	private FormID relatedTemperingInput;
	private FormID relatedMeltdownProduct;
	private FormID relatedMeltdownCraftingStation;
	
	private BaseMaterialsWeapon(FormID relatedSmithingPerk,FormID relatedMeltdownProduct, FormID relatedMeltdownCraftingStation, FormID relatedTemperingInput){
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
	
}
