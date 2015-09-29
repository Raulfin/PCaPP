package patcher;

import skyproc.ARMO;
import skyproc.COBJ;
import skyproc.Condition;
import skyproc.Condition.Operator;
import skyproc.Condition.RunOnType;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import skyproc.WEAP;
import util.Statics;
import xml.access.XmlStorage;
import xml.lowLevel.armor.ArmorMaterial;
import xml.lowLevel.weapon.WeaponMaterial;

/**
 * Disable certain crafting recipes
 * 
 * @author T3nd0
 * 
 */
final class CraftablePatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	protected CraftablePatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	/**
	 * Disables staff recipes if the mage module is used.
	 * 
	 */
	public void runChanges() {

		boolean shouldAdd = false;
		WEAP w;
		ARMO a;

		for (COBJ c : this.merger.getConstructibleObjects()) {
			try {
				if (this.s.useMage()
						&& c.getBenchKeywordFormID().equals(
								Statics.kwCraftingStaff)
						&& this.s.shouldDisableStaffRecipe(c)) {
					this.disableRecipe(c);
				}

				if (this.s.useWarrior()) {
					if (c.getBenchKeywordFormID().equals(
							Statics.kwCraftingSmithingSharpeningWheel)) {

						w = (WEAP) SPDatabase.getMajor(c.getResultFormID(),
								GRUP_TYPE.WEAP);

						if (null != w) {
							if (this.alterTemperingRecipe(c, w)) {
								shouldAdd = true;
							}
						}
					} else if (c.getBenchKeywordFormID().equals(
							Statics.kwCraftingSmithingArmorTable)) {

						a = (ARMO) SPDatabase.getMajor(c.getResultFormID(),
								GRUP_TYPE.ARMO);

						if (null != a) {
							if (this.alterTemperingRecipe(c, a)) {
								shouldAdd = true;
							}
						}
					}
				}

				if (shouldAdd) {
					patch.addRecord(c);
					shouldAdd = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Craftable Patcher: "
						+ e.toString());
			}
		}
	}

	/**
	 * Kills a recipe by messing with the bench keyword
	 * 
	 * @param c
	 */
	private void disableRecipe(COBJ c) {
		c.setBenchKeywordFormID(Statics.kwActorTypeNPC);
		SPGlobal.log("CRAFTABLE_PATCHER", c.getEDID() + ": Disabled");
	}

	/**
	 * Sets new requirements for tempering weapons
	 * 
	 * @param c
	 * @param w
	 */
	private boolean alterTemperingRecipe(COBJ c, WEAP w) {

		WeaponMaterial wm = this.s.getWeaponMaterial(w);

		if (wm == null) {
			SPGlobal.log(
					"CRAFTABLE_PATCHER",
					w.getName()
							+ ": No weapon material found. Excluded from further patching.");
			WeaponPatcher.weaponsWithNoMaterialOrType.add(w);
			return false;
		}

		FormID perk = this.s.getWeaponMaterial(w).getMaterialTemper()
				.getRelatedSmithingPerk();

		c.getConditions().clear();

		if (null != perk) {

			Condition c1 = new Condition(Condition.P_FormID.HasPerk, perk);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);

			c1.setRunOnType(RunOnType.Subject);

			c.getConditions().add(c1);
		}

		return true;
	}

	/**
	 * Sets new requirements for tempering armor
	 * 
	 * @param c
	 * @param w
	 */
	private boolean alterTemperingRecipe(COBJ c, ARMO a) {

		ArmorMaterial am = this.s.getArmorMaterial(a);

		if (am == null) {
			if (!(this.s.isClothing(a) && !(this.s.isJewelry(a)))) {
				SPGlobal.log(
						"CRAFTABLE_PATCHER",
						a.getName()
								+ ": No armor material found. Excluded from further patching.");
				ArmorPatcher.armorWithNoMaterialOrType.add(a);
			}
			return false;
		}

		FormID perk = this.s.getArmorMaterial(a).getMaterialTemper()
				.getRelatedSmithingPerk();

		c.getConditions().clear();

		if (null != perk) {

			Condition c1 = new Condition(Condition.P_FormID.HasPerk, perk);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);

			c1.setRunOnType(RunOnType.Subject);

			c.getConditions().add(c1);
		}
		return true;
	}

	public String getInfo() {
		return "Disabling and modifying crafting recipes...";
	}
}
