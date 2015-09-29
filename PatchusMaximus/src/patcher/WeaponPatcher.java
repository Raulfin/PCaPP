package patcher;

import java.util.ArrayList;

import skyproc.COBJ;
import skyproc.Condition;
import skyproc.Condition.Operator;
import skyproc.Condition.RunOnType;
import skyproc.ENCH;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.LVLI;
import skyproc.LeveledEntry;
import skyproc.LeveledRecord.LVLFlag;
import skyproc.Mod;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import skyproc.ScriptPackage;
import skyproc.WEAP;
import skyproc.WEAP.WeaponFlag;
import skyproc.genenums.SoundVolume;
import skyproc.gui.SPProgressBarPlug;
import util.Bucket;
import util.Statics;
import util.Tuple;
import xml.access.XmlStorage;
import xml.lowLevel.weapon.WeaponMaterial;
import xml.lowLevel.weapon.WeaponModifier;
import xml.lowLevel.weapon.WeaponOverride;
import xml.lowLevel.weapon.WeaponType;
import enums.BaseMaterialsWeapon;
import enums.BaseWeaponTypes;

final class WeaponPatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	public static ArrayList<WEAP> weaponsWithNoMaterialOrType = new ArrayList<>();

	private ArrayList<Bucket<WEAP, WEAP>> similarWeaponBuckets = new ArrayList<>();
	private ArrayList<WEAP> weaponsWithoutSimilars = new ArrayList<>();

	public WeaponPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		boolean addRecord = false;
		WeaponMaterial wm;
		WeaponType wt;
		WEAP reforgedWeapon = null, warforgedWeapon = null;

		for (WEAP w : merger.getWeapons()) {

			// first, check if override exists. if so, apply override and finish
			try {
				if (this.s.useWarrior()) {
					WeaponOverride wo = this.s.getWeaponOverride(w);

					if (null != wo) {
						this.applyWeaponOverride(w, wo);
						patch.addRecord(w);
						continue;
					}
				}

				// if no override exists, start normal procedure
				if (!(this.shouldPatch(w))) {
					// SPGlobal.log("WEAPON_PATCHER", w.getName() +
					// ": Ignored");
					continue;
				}

				if (null == (wm = this.s.getWeaponMaterial(w))) {
					SPGlobal.log("WEAPON_PATCHER", w.getName()
							+ ": Failed to patch (no weapon material).");
					WeaponPatcher.weaponsWithNoMaterialOrType.add(w);
					continue;
				}
				if (null == (wt = this.s.getWeaponType(w))) {
					SPGlobal.log("WEAPON_PATCHER", w.getName()
							+ ": Failed to patch (no weapon type).");
					WeaponPatcher.weaponsWithNoMaterialOrType.add(w);
					continue;
				}

				// SPGlobal.log("WEAPON_PATCHER", w.getName() +
				// ": Started patching");

				// run general changes

				addRecord = this.addSpecificKeyword(w, wt)
						| this.addGenericKeyword(w, wt);

				// if ranged weapon. make npcs use ammo

				if (wt.getBaseWeaponType().getRelatedWeaponSchool()
						.equals(Statics.kwWeaponSchoolRangedWeaponry)) {
					w.set(WeaponFlag.NPCsUseAmmo, true);
				}

				// run changes needed when warrior module is used
				if (this.s.useWarrior()) {

					if (this.s.shouldAppendWeaponType()) {
						if (this.appendTypeToName(w, wt)) {
							addRecord = true;
						}
					}

					addRecord = this.addCombatLogicKeywords(w, wt)
							| this.modStats(w, wt, wm);

					if (!w.get(WeaponFlag.CantDrop)) {
						this.addMeltdownRecipe(w, wt, wm);

						if (!this.s.isWeaponExcludedReforged(w)) {
							this.addMeltdownRecipe(w, wt, wm);

							this.createRefinedSilverWeapon(w);

							reforgedWeapon = createReforgedWeapon(w, wt, wm);
							this.applyModifiers(reforgedWeapon);
							this.addReforgedCraftingRecipe(reforgedWeapon, w,
									wm);
							this.addMeltdownRecipe(reforgedWeapon, wt, wm);
							this.addTemperingRecipe(reforgedWeapon, wm);

							warforgedWeapon = createWarforgedWeapon(w, wt, wm);
							this.addWarforgedCraftingRecipe(warforgedWeapon,
									reforgedWeapon, wm);
							this.addMeltdownRecipe(warforgedWeapon, wt, wm);
							this.addTemperingRecipe(warforgedWeapon, wm);
							this.applyModifiers(warforgedWeapon);

							this.createCrossbowVariants(w, wm, wt);
						}
						this.doCopycat(w, wm, wt);
						this.distributeWeaponOnLeveledList(w, wm, wt);
					}

					this.applyModifiers(w);
				}

				// even the thief module needs to distribute fist weapons

				if (this.s.useThief()) {
					if (this.s.getWeaponType(w).equals(BaseWeaponTypes.FIST)) {
						this.distributeWeaponOnLeveledList(w, wm, wt);
					}
				}

				// if changes were made, add record

				if (addRecord) {
					patch.addRecord(w);
					addRecord = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Weapon Patcher: " + e.toString());
			}
		}

		// SPGlobal.log("WEAPON_PATCHER",
		// "Starting to process list enchantment bindings");
		SPProgressBarPlug.setStatus("Task: Weapon enchantments(1/2)");
		this.processListEnchantmentBindings();
		// SPGlobal.log("WEAPON_PATCHER",
		// "Done processing list enchantment bindings");

		// SPGlobal.log("WEAPON_PATCHER",
		// "Starting to process direct enchantment bindings");
		SPProgressBarPlug.setStatus("Task: Weapon enchantments(2/2)");
		this.processDirectEnchantmentBindings();
		// SPGlobal.log("WEAPON_PATCHER",
		// "Done processing direct enchantment bindings");

	}

	/**
	 * Distributes w on any leveled lists that contains 'similar' weapons.
	 * 
	 * @param w
	 * @param wm
	 * @param wt
	 */
	private void distributeWeaponOnLeveledList(WEAP w, WeaponMaterial wm,
			WeaponType wt) {
		// undroppable or bound weapon?
		if (w.get(WeaponFlag.CantDrop) || w.get(WeaponFlag.BoundWeapon)) {
			return;

		}

		// weapon excluded?
		if (this.s.isWeaponExcludedDistribution(w)) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName() +
			// ": Not adding weapon"
			// + w.getName() + "to leveled lists - excluded");
			return;
		}

		WEAP lw = null, firstSimilarMatch = null;

		boolean similarSet = false, added = false, alreadyOnLists = false;

		// SPGlobal.log("WEAPON_PATCHER", w.getName() +
		// ": Started adding weapon "
		// + w.getName() + "to leveled lists");

		ArrayList<LeveledEntry> newEntries = new ArrayList<LeveledEntry>();

		for (LVLI i : this.merger.getLeveledItems()) {

			// excluded?
			if (this.s.isListExcludedWeaponRegular(i)) {
				continue;
			}

			// already in?
			if (i.getEntryForms().contains(w.getForm())) {
				alreadyOnLists = true;
				continue;
			}

			for (LeveledEntry li : i.getEntries()) {

				// is li a weapon?
				if (null == (lw = (WEAP) SPDatabase.getMajor(li.getForm(),
						GRUP_TYPE.WEAP))) {
					continue;
				}

				if (!similarSet) {
					// similar?
					if (!(this.areWeaponsSimilar(w, wm, wt, lw))) {
						continue;
					}
					// first similar match - use in next iterations
					similarSet = true;
					firstSimilarMatch = lw;
					// SPGlobal.log("WEAPON_PATCHER", w.getName()
					// + ": Found similar weapon " + lw.getName());
				} else {
					// after similar match is set, check for equality
					if (!lw.equals(firstSimilarMatch)) {
						continue;
					}
				}

				newEntries.add(new LeveledEntry(w.getForm(), li.getLevel(), li
						.getCount()));

			}

			// add entries if matches were found

			if (newEntries.size() > 0) {
				added = true;
				for (LeveledEntry le : newEntries) {
					i.addEntry(le);
				}

				// SPGlobal.log("WEAPON_PATCHER",
				// w.getName() + ": Added " + newEntries.size()
				// + " entries to leveled list " + i.getEDID());

				if (!this.patch.contains(i.getForm())) {
					this.patch.addRecord(i);
				}

				// reset
				newEntries.clear();
			}

			similarSet = false;
			firstSimilarMatch = null;
		}

		if (!added && !alreadyOnLists) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName()
			// + ": Not added anywhere. Better check it out.");
		} else if (!added && alreadyOnLists) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName()
			// + ": Not added anywhere, but already on at least one list.");
		}

	}

	/**
	 * Determines whether two weapons are similar. Definition of similar: Same
	 * tempering material, same weapon school, at least one matching "class"
	 * keyword (blunt, blade, piercing), same enchantment
	 * 
	 * @param w1
	 * @param wm1
	 * @param wt1
	 * @param w2
	 * @return
	 */
	private boolean areWeaponsSimilar(WEAP w1, WeaponMaterial wm1,
			WeaponType wt1, WEAP w2) {

		if (WeaponPatcher.weaponsWithNoMaterialOrType.contains(w2)) {
			return false;
		}

		WeaponMaterial wm2 = this.s.getWeaponMaterial(w2);
		WeaponType wt2 = this.s.getWeaponType(w2);

		if (null == wm2 || null == wt2) {
			WeaponPatcher.weaponsWithNoMaterialOrType.add(w2);
			return false;
		}

		return wm1.getMaterialTemper() == wm2.getMaterialTemper()
				&& wt1.getBaseWeaponType().getRelatedWeaponSchool() == wt2
						.getBaseWeaponType().getRelatedWeaponSchool()
				&& this.doWeaponsContainClasses(w1, w2)
				&& w1.getEnchantment().equals(w2.getEnchantment());
	}

	/**
	 * Determines whether two weapons are similar. Definition of similar: Same
	 * tempering material, same weapon school, at least one matching "class"
	 * keyword (blunt, blade, piercing).
	 * 
	 * Both should be unenchanted.
	 * 
	 * @param w1
	 * @param w2
	 * @return
	 */
	private boolean areWeaponsSimilar(WEAP w1, WEAP w2) {

		WeaponMaterial wm1 = this.s.getWeaponMaterial(w1);
		WeaponType wt1 = this.s.getWeaponType(w1);
		WeaponMaterial wm2 = this.s.getWeaponMaterial(w2);
		WeaponType wt2 = this.s.getWeaponType(w2);

		if (!(w1.getEnchantment() == null || w1.getEnchantment().equals(
				FormID.NULL))) {
			return false;
		}
		if (!(w2.getEnchantment() == null || w2.getEnchantment().equals(
				FormID.NULL))) {
			return false;
		}

		if (WeaponPatcher.weaponsWithNoMaterialOrType.contains(w1)
				|| WeaponPatcher.weaponsWithNoMaterialOrType.contains(w2)) {
			return false;
		}

		if (null == wm1 || null == wt1) {
			WeaponPatcher.weaponsWithNoMaterialOrType.add(w1);
			return false;
		}

		if (null == wm2 || null == wt2) {
			WeaponPatcher.weaponsWithNoMaterialOrType.add(w2);
			// SPGlobal.log("WEAPON_PATCHER", w2.getName()+
			// ": Material or type null. Check out.");
			return false;
		}

		return wm1.getMaterialTemper() == wm2.getMaterialTemper()
				&& wt1.getBaseWeaponType().getRelatedWeaponSchool() == wt2
						.getBaseWeaponType().getRelatedWeaponSchool()
				&& this.doWeaponsContainClasses(w1, w2);
	}

	/**
	 * Checks whether one weapon's classes are completely contained in another's
	 * 
	 * @param w1
	 * @param w2
	 * @return
	 */
	private boolean doWeaponsContainClasses(WEAP w1, WEAP w2) {

		ArrayList<FormID> k1 = w1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> k2 = w2.getKeywordSet().getKeywordRefs();

		if (k1.contains(Statics.kwWeaponClassBlade)) {
			if (!(k2.contains(Statics.kwWeaponClassBlade))) {
				return false;
			}
		}

		if (k1.contains(Statics.kwWeaponClassBlunt)) {
			if (!(k2.contains(Statics.kwWeaponClassBlunt))) {
				return false;
			}
		}

		if (k1.contains(Statics.kwWeaponClassPiercing)) {
			if (!(k2.contains(Statics.kwWeaponClassPiercing))) {
				return false;
			}
		}

		return true;

	}

	/**
	 * Patches reach, damage, critical damage and speed of a weapon
	 * 
	 * @param w
	 * @param wt
	 * @param wm
	 * @return
	 */
	private boolean modStats(WEAP w, WeaponType wt, WeaponMaterial wm) {
		return this.setReach(w, wm, wt) | this.setDamage(w, wm, wt)
				| this.setCritDamage(w, wt) | this.setSpeed(w, wm, wt);
	}

	/**
	 * Adds keywords responsible for bleeding, stagger and defense debuffs
	 * 
	 * @param w
	 * @param wt
	 * @return
	 */

	private boolean addCombatLogicKeywords(WEAP w, WeaponType wt) {

		FormID bleedKW = wt.getBleedTier().getBleedingKW();
		FormID staggerKW = wt.getStaggerTier().getStaggerKW();
		FormID debuffKW = wt.getDebuffTier().getDebuffKW();

		boolean ret = false;

		if (null != bleedKW) {
			w.getKeywordSet().addKeywordRef(bleedKW);
			ret = true;
		}
		if (null != staggerKW) {
			w.getKeywordSet().addKeywordRef(staggerKW);
			ret = true;
		}
		if (null != debuffKW) {
			w.getKeywordSet().addKeywordRef(debuffKW);
			ret = true;
		}

		return ret;

	}

	/**
	 * Applies some early filters to patched weapons
	 * 
	 * @param w
	 * @return
	 */
	private boolean shouldPatch(WEAP w) {
		if (!(w.getTemplate().isNull())) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName() + ": Has template.");
			return false;
		} else if (w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwWeapTypeStaff)) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName() + ": Is mage staff.");
			return false;
		} else if (null == w.getName()) {
			SPGlobal.log("WEAPON_PATCHER", "null name");
			return false;
		} else if (WeaponPatcher.weaponsWithNoMaterialOrType.contains(w)) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName() +
			// "previously excluded");
			return false;
		}

		return true;
	}

	private boolean setDamage(WEAP w, WeaponMaterial wm, WeaponType wt) {

		int oldDamage = w.getDamage();

		double skillBase = this.s.getWeaponSkillDamageBase(wt
				.getBaseWeaponType());
		double typeMod = wt.getDamageBase();
		double matMod = wm.getDamageModifier();
		double typeMult = this.s.getWeaponSkillDamageMultiplier(wt
				.getBaseWeaponType());

		// SPGlobal.log("WEAPON_PATCHER", w.getName() +
		// " setDamage: Skill base: "
		// + skillBase + " Type mod: " + typeMod + " Mat mod: " + matMod
		// + " Type mult: " + typeMult);

		if (skillBase == -1 || typeMult == -1) {
			SPGlobal.log("WEAPON_PATCHER", w.getName()
					+ ": Failed patching damage.");
			return false;
		}

		// int newDamage = (int) ((skillBase + typeMod + matMod) * typeMult);

		int newDamage = (int) (skillBase + typeMod * matMod);

		if (newDamage != oldDamage) {
			w.setDamage(newDamage);
			// SPGlobal.log("WEAPON_PATCHER", w.getName()
			// + " setDamage: newDamage: " + w.getDamage());
			return true;
		}

		return false;
	}

	private boolean setReach(WEAP w, WeaponMaterial wm, WeaponType wt) {
		double originalReach = w.getReach();
		double newReach = wt.getReachBase() + wm.getReachModifier();

		if (originalReach != newReach) {
			w.setReach((float) newReach);
			return true;
		}

		return false;
	}

	private boolean setSpeed(WEAP w, WeaponMaterial wm, WeaponType wt) {
		double originalSpeed = w.getSpeed();
		double newSpeed = wt.getSpeedBase() + wm.getSpeedModifier();

		if (originalSpeed != newSpeed) {
			w.setSpeed((float) newSpeed);
			return true;
		}

		return false;
	}

	private boolean setCritDamage(WEAP w, WeaponType wt) {
		int oldCritDamage = w.getCritDamage();
		int newCritDamage = (int) (w.getDamage() * wt.getCritDamageFactor());

		if (oldCritDamage != newCritDamage) {
			w.setCritDamage(newCritDamage);
			// SPGlobal.log("WEAPON_PATCHER", w.getName()
			// + " setDamage: new crit damage: " + w.getCritDamage());
			return true;
		}

		return false;
	}

	/**
	 * Adds basic weapon classification keywords - blunt, piercing, bladed
	 * 
	 * @param w
	 * @param wt
	 * @return
	 */
	private boolean addGenericKeyword(WEAP w, WeaponType wt) {

		int added = 0;

		if (w.get(WeaponFlag.BoundWeapon)) {
			w.getKeywordSet().addKeywordRef(Statics.kwBoundWeapon);
			added++;
		}

		for (FormID kw : wt.getWeaponClass().getRelatedKeywords()) {
			w.getKeywordSet().addKeywordRef(kw);
			added++;
		}

		if (added == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Adds specific weapon type keyword, and generic skill-related keyword
	 * 
	 * @param w
	 * @param wt
	 * @return
	 */

	private boolean addSpecificKeyword(WEAP w, WeaponType wt) {

		if (null == wt.getBaseWeaponType()) {
			SPGlobal.log("WEAPON_PATCHER", w.getName()
					+ ": BaseWeaponType is null");
			return false;
		}

		FormID sType = wt.getBaseWeaponType().getRelatedSpecificType();
		FormID school = wt.getBaseWeaponType().getRelatedWeaponSchool();

		if (null == sType) {
			SPGlobal.log("WEAPON_PATCHER", w.getName()
					+ ": related spec. type is null");
			return false;
		}

		if (null == school) {
			SPGlobal.log("WEAPON_PATCHER", w.getName()
					+ ": related school is null");
			return false;
		}

		w.getKeywordSet().addKeywordRef(sType);
		w.getKeywordSet().addKeywordRef(school);

		return true;
	}

	// TODO finish

	private void applyWeaponOverride(WEAP w, WeaponOverride wo) {
		SPGlobal.log("WEAPON_PATCHER", w.getName() + ": Applying override");
		w.setDamage(wo.getDamage());
		w.setReach((float) wo.getReach());
		w.setSpeed((float) wo.getSpeed());
		w.setCritDamage(wo.getCritDamage());
		w.setName(w.getName() + " " + wo.getStringToAppend());
		this.alterTemperingRecipe(w, wo.getMaterialTempering());
		this.addMeltdownRecipe(w, wo.getMaterialMeltdown(),
				wo.getMeltdownInput(), wo.getMeltdownOutput());
	}

	/**
	 * Appends a weapon's type to its name
	 * 
	 * @param w
	 * @param wt
	 * @return
	 */
	private boolean appendTypeToName(WEAP w, WeaponType wt) {

		String typeID = this.s.getOutputString(wt.getIdentifier());
		if (null == typeID) {
			typeID = wt.getIdentifier();
		}
		String lowtypeID = typeID.toLowerCase();

		if (!(w.getName().contains(typeID))
				&& !(w.getName().contains(lowtypeID))) {
			w.setName(w.getName() + " [" + typeID + "]");
			return true;
		}

		return false;
	}

	/**
	 * Adds perk condition to tempering recipes. Only used in override
	 * 
	 * @param w
	 * @param wm
	 */
	private void alterTemperingRecipe(WEAP w, BaseMaterialsWeapon bmw) {
		for (COBJ c : this.merger.getConstructibleObjects()) {
			if (c.getResultFormID().equals(w.getForm())
					&& c.getBenchKeywordFormID().equals(
							Statics.kwCraftingSmithingSharpeningWheel)) {
				c.getConditions().clear();
				FormID perk = bmw.getRelatedSmithingPerk();
				if (null != perk) {
					Condition c1 = new Condition(Condition.P_FormID.HasPerk,
							perk);
					c1.setOperator(Operator.EqualTo);
					c1.setValue(1.0f);

					c1.setRunOnType(RunOnType.Subject);

					c.getConditions().add(c1);
				}

				this.patch.addRecord(c);
			}
		}
	}

	/**
	 * Creates meltdown recipe for a given weapon
	 * 
	 * @param w
	 * @param wt
	 * @param wm
	 */
	private void addMeltdownRecipe(WEAP w, WeaponType wt, WeaponMaterial wm) {

		FormID requiredPerk = wm.getMaterialMeltdown().getRelatedSmithingPerk();
		FormID output = wm.getMaterialMeltdown().getRelatedMeltdownProduct();
		FormID benchKW = wm.getMaterialMeltdown()
				.getRelatedMeltdownCraftingStation();

		int inputNum = wt.getMeltdownInput();
		int outputNum = wt.getMeltdownOutput();

		if ((null == output) || inputNum <= 0 || outputNum <= 0) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName()
			// + ": No meltdown recipe generated.");
			return;
		}

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + Statics.S_PREFIX_MELTDOWN
				+ w.getName() + w.getFormStr());

		newRecipe.addIngredient(w.getForm(), inputNum);
		newRecipe.setResultFormID(output);
		newRecipe.setOutputQuantity(outputNum);
		newRecipe.setBenchKeywordFormID(benchKW);

		if (requiredPerk != null) {
			Condition c1 = new Condition(Condition.P_FormID.HasPerk,
					requiredPerk);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);
			c1.setRunOnType(RunOnType.Subject);
			newRecipe.addCondition(c1);
		}

		Condition c2 = new Condition(Condition.P_FormID.GetItemCount,
				w.getForm());
		c2.setOperator(Operator.GreaterThanOrEqual);
		c2.setValue((float) inputNum);
		c2.setRunOnType(RunOnType.Subject);

		newRecipe.addCondition(c2);

		Condition c3 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingMeltdown);
		c3.setOperator(Operator.EqualTo);
		c3.setValue(1.0f);
		c3.setRunOnType(RunOnType.Subject);

		newRecipe.addCondition(c3);

		this.patch.addRecord(newRecipe);
	}

	/**
	 * Creates meltdown recipe for a given weapon
	 * 
	 * @param w
	 * @param wt
	 * @param wm
	 */
	private void addMeltdownRecipe(WEAP w, BaseMaterialsWeapon mw,
			int meltdownIn, int meltdownOut) {
		FormID requiredPerk = mw.getRelatedSmithingPerk();
		FormID output = mw.getRelatedMeltdownProduct();
		FormID benchKW = mw.getRelatedMeltdownCraftingStation();

		if ((null == output) || meltdownIn <= 0 || meltdownOut <= 0) {
			// SPGlobal.log("WEAPON_PATCHER", w.getName()
			// + ": No meltdown recipe generated.");
			return;
		}

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + Statics.S_PREFIX_MELTDOWN
				+ w.getName() + w.getFormStr());

		newRecipe.addIngredient(w.getForm(), meltdownIn);
		newRecipe.setResultFormID(output);
		newRecipe.setOutputQuantity(meltdownOut);
		newRecipe.setBenchKeywordFormID(benchKW);

		if (requiredPerk != null) {
			Condition c1 = new Condition(Condition.P_FormID.HasPerk,
					requiredPerk);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);
			c1.setRunOnType(RunOnType.Subject);
			newRecipe.addCondition(c1);
		}

		Condition c2 = new Condition(Condition.P_FormID.GetItemCount,
				w.getForm());
		c2.setOperator(Operator.GreaterThanOrEqual);
		c2.setValue((float) meltdownIn);
		c2.setRunOnType(RunOnType.Subject);

		newRecipe.addCondition(c2);

		Condition c3 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingMeltdown);
		c3.setOperator(Operator.EqualTo);
		c3.setValue(1.0f);
		c3.setRunOnType(RunOnType.Subject);

		newRecipe.addCondition(c3);

		this.patch.addRecord(newRecipe);
	}

	/**
	 * Creates reforged variant of an existing weapon
	 * 
	 * @param w
	 */
	private WEAP createReforgedWeapon(WEAP w, WeaponType wt, WeaponMaterial wm) {

		// SPGlobal.log("WEAPON_PATCHER", w.getName() +
		// ": Adding reforged weapon");

		String newName = this.s.getOutputString("Reforged") + " " + w.getName();

		WEAP newReforgedWeapon = (WEAP) patch.makeCopy(w,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON + newName
						+ w.getFormStr());

		newReforgedWeapon.setName(newName);

		// this.patch.addRecord(newReforgedWeapon);
		return newReforgedWeapon;

	}

	/**
	 * Creates warforged variant of an existing weapon
	 * 
	 * @param w
	 */
	private WEAP createWarforgedWeapon(WEAP w, WeaponType wt, WeaponMaterial wm) {

		// SPGlobal.log("WEAPON_PATCHER", w.getName()
		// + ": Adding warforged weapon");

		String newName = this.s.getOutputString("Warforged") + " "
				+ w.getName();

		WEAP newWarforgedWeapon = (WEAP) patch.makeCopy(w,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON + newName
						+ w.getFormStr());

		newWarforgedWeapon.setName(newName);
		newWarforgedWeapon.setEnchantment(Statics.enchSmithingWarforgedWeapon);
		newWarforgedWeapon.setEnchantmentCharge(10);
		newWarforgedWeapon.getKeywordSet().addKeywordRef(
				Statics.kwSmithingWarforgedWeapon);
		newWarforgedWeapon.getKeywordSet().addKeywordRef(
				Statics.kwMagicDisallowEnchanting);
		// this.patch.addRecord(newWarforgedWeapon);

		return newWarforgedWeapon;

	}

	private void applyModifiers(WEAP w) {

		ArrayList<WeaponModifier> mods = this.s.getWeaponModifiers(w);

		if (null == mods) {
			return;
		}

		SPGlobal.log("WEAPON_PATCHER", w.getName() + ": Has modifiers "
				+ mods.toArray().toString());

		for (WeaponModifier wm : mods) {
			SPGlobal.log("WEAPON_PATCHER", w.getName() + ": Applying modifier "
					+ wm.getIdentifier());
			w.setDamage((int) (w.getDamage() * wm.getFactorDamage()));
			w.setCritDamage((int) (w.getCritDamage() * wm.getFactorCritDamage()));
			w.setSpeed((float) (w.getSpeed() * wm.getFactorAttackSpeed()));
			w.setWeight((float) (w.getWeight() * wm.getFactorWeight()));
			w.setReach((float) (w.getReach() * wm.getFactorReach()));
			w.setValue((int) (w.getValue() * wm.getFactorValue()));
		}

	}

	private void addReforgedCraftingRecipe(WEAP newWeapon, WEAP oldWeapon,
			WeaponMaterial wm) {

		// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName()
		// + ": Adding reforged crafting recipe");

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON
				+ Statics.S_PREFIX_CRAFTING + newWeapon.getName()
				+ newWeapon.getFormStr());
		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newWeapon.getForm());

		FormID ing1 = oldWeapon.getForm();
		FormID ing2 = wm.getMaterialTemper().getRelatedTemperingInput();

		if (ing2 == null) {
			ing2 = wm.getMaterialMeltdown().getRelatedMeltdownProduct();
		}

		if (ing1 != null) {
			c.addIngredient(ing1, 1);
		}

		if (ing2 != null) {
			c.addIngredient(ing2, 2);
		}

		FormID perk = wm.getMaterialTemper().getRelatedSmithingPerk();

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingWeaponsmith);
		c1.setRunOnType(RunOnType.Subject);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);

		if (perk != null) {
			Condition c2 = new Condition(Condition.P_FormID.HasPerk, perk);
			c2.setRunOnType(RunOnType.Subject);
			c2.setOperator(Operator.EqualTo);
			c2.setValue(1.0f);
			c.addCondition(c2);
		}

		Condition c3 = new Condition(Condition.P_FormID.GetItemCount, ing1);
		c3.setRunOnType(RunOnType.Subject);
		c3.setOperator(Operator.GreaterThanOrEqual);
		c3.setValue(1.0f);

		c.addCondition(c1);
		c.addCondition(c3);

		this.patch.addRecord(c);

	}

	private void addWarforgedCraftingRecipe(WEAP newWeapon, WEAP oldWeapon,
			WeaponMaterial wm) {

		// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName()
		// + ": Adding warforged crafting recipe");

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON
				+ Statics.S_PREFIX_CRAFTING + newWeapon.getName()
				+ newWeapon.getFormStr());
		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newWeapon.getForm());

		FormID ing1 = oldWeapon.getForm();
		FormID ing2 = wm.getMaterialTemper().getRelatedTemperingInput();

		if (ing2 == null) {
			ing2 = wm.getMaterialMeltdown().getRelatedMeltdownProduct();
		}

		if (ing1 != null) {
			c.addIngredient(ing1, 1);
		}

		if (ing2 != null) {
			c.addIngredient(ing2, 5);
		}

		FormID perk = wm.getMaterialTemper().getRelatedSmithingPerk();

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingMasteryWarforged);
		c1.setRunOnType(RunOnType.Subject);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);

		if (perk != null) {
			Condition c2 = new Condition(Condition.P_FormID.HasPerk, perk);
			c2.setRunOnType(RunOnType.Subject);
			c2.setOperator(Operator.EqualTo);
			c2.setValue(1.0f);
			c.addCondition(c2);
		}

		Condition c3 = new Condition(Condition.P_FormID.GetItemCount, ing1);
		c3.setRunOnType(RunOnType.Subject);
		c3.setOperator(Operator.GreaterThanOrEqual);
		c3.setValue(1.0f);

		c.addCondition(c1);
		c.addCondition(c3);

		this.patch.addRecord(c);

	}

	/**
	 * Create tempering recipe for any weapon
	 * 
	 * @param w
	 * @param wm
	 */
	private void addTemperingRecipe(WEAP w, WeaponMaterial wm) {

		// SPGlobal.log("WEAPON_PATCHER", w.getName()
		// + ": Started adding tempering recipe");

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON
				+ Statics.S_PREFIX_TEMPER + w.getName() + w.getFormStr());

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingSharpeningWheel);
		c.setResultFormID(w.getForm());
		c.setOutputQuantity(1);

		FormID perk = wm.getMaterialTemper().getRelatedSmithingPerk();
		FormID ing = wm.getMaterialTemper().getRelatedTemperingInput();

		if (ing == null) {
			SPGlobal.log(
					"WEAPON_PATCHER",
					w.getName()
							+ ": No proper tempering input. Will try meltdown output instead");
			ing = wm.getMaterialTemper().getRelatedMeltdownProduct();
		}

		if (ing != null) {
			c.addIngredient(ing, 1);
		} else {
			SPGlobal.log("WEAPON_PATCHER", w.getName()
					+ ": Found no suitable intput for tempering recipe.");
		}

		if (perk != null) {
			Condition c1 = new Condition(Condition.P_FormID.HasPerk, perk);
			c1.setRunOnType(RunOnType.Subject);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);

			c.addCondition(c1);
		}

		this.patch.addRecord(c);

	}

	/**
	 * Creates refined variant of an existing silver weapon and adds all
	 * relevant recipes.
	 * 
	 * @param w
	 * @return
	 */
	private WEAP createRefinedSilverWeapon(WEAP w) {
		if (!w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwWeapMaterialSilver)) {
			return null;
		}

		// SPGlobal.log("WEAPON_PATCHER", w.getName()
		// + ": Started creating refined silver variant");

		String newName = this.s.getOutputString("Refined") + " " + w.getName();

		WEAP newRefinedSilverWeapon = (WEAP) patch.makeCopy(w,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON + newName
						+ w.getFormStr());

		newRefinedSilverWeapon.setDescription(this.s
				.getOutputString(Statics.S_WEAPON_REFINED_DESC));

		newRefinedSilverWeapon.setName(newName);

		newRefinedSilverWeapon.getKeywordSet().addKeywordRef(
				Statics.kwWeapMaterialSilverRefined);
		newRefinedSilverWeapon.getKeywordSet().removeKeywordRef(
				Statics.kwWeapMaterialSilver);

		WeaponMaterial wm = this.s.getWeaponMaterial(newRefinedSilverWeapon);
		WeaponType wt = this.s.getWeaponType(newRefinedSilverWeapon);

		this.modStats(newRefinedSilverWeapon, wt, wm);
		this.applyModifiers(newRefinedSilverWeapon);
		;

		// swap properties on silver sword script

		ScriptPackage silverScriptPackage = newRefinedSilverWeapon
				.getScriptPackage();
		if (!silverScriptPackage.hasScript(Statics.S_SCRIPT_SILVERSWORD)) {
			silverScriptPackage.addScript(Statics.S_SCRIPT_SILVERSWORD);
		}
		silverScriptPackage.getScript(Statics.S_SCRIPT_SILVERSWORD)
				.setProperty(Statics.S_SCRIPT_SILVERSWORD_PROPERTY,
						Statics.perkWeaponSilverRefined);

		this.addRefinedSilverCraftingRecipe(newRefinedSilverWeapon, w);
		this.addMeltdownRecipe(newRefinedSilverWeapon, wt, wm);
		this.addTemperingRecipe(newRefinedSilverWeapon, wm);

		if (!this.s.isWeaponExcludedReforged(w)) {
			WEAP reforgedWeapon = createReforgedWeapon(newRefinedSilverWeapon,
					wt, wm);
			this.applyModifiers(reforgedWeapon);
			this.addReforgedCraftingRecipe(reforgedWeapon,
					newRefinedSilverWeapon, wm);
			this.addMeltdownRecipe(reforgedWeapon, wt, wm);
			this.addTemperingRecipe(reforgedWeapon, wm);

			WEAP warforgedWeapon = createWarforgedWeapon(
					newRefinedSilverWeapon, wt, wm);
			this.addWarforgedCraftingRecipe(warforgedWeapon, reforgedWeapon, wm);
			this.addMeltdownRecipe(warforgedWeapon, wt, wm);
			this.addTemperingRecipe(warforgedWeapon, wm);
			this.applyModifiers(warforgedWeapon);
		}

		this.doCopycat(w, wm, wt);
		this.distributeWeaponOnLeveledList(w, wm, wt);

		return newRefinedSilverWeapon;
	}

	/**
	 * Adds a crafting recipe specifically geared towards refined silver weapons
	 * 
	 * @param newWeapon
	 * @param oldWeapon
	 */
	private void addRefinedSilverCraftingRecipe(WEAP newWeapon, WEAP oldWeapon) {

		// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName()
		// + ": Started adding crafting recipe");

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON
				+ Statics.S_PREFIX_CRAFTING + newWeapon.getName()
				+ newWeapon.getFormStr());
		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newWeapon.getForm());

		c.addIngredient(oldWeapon.getForm(), 1);
		c.addIngredient(Statics.ingotGold, 1);
		c.addIngredient(Statics.ingotSilver, 2);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingSilverRefined);
		c1.setRunOnType(RunOnType.Subject);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);

		c.addCondition(c1);

		this.patch.addRecord(c);
	}

	/**
	 * Create all records related to the Copycat perk
	 * 
	 * @param w
	 * @param wm
	 * @return
	 */

	private boolean doCopycat(WEAP w, WeaponMaterial wm, WeaponType wt) {
		WEAP newWeapon;

		if (!(w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwDaedricArtifact) || w.getKeywordSet()
				.getKeywordRefs().contains(Statics.kwWeapTypeStaff))) {
			return false;
		}

		// SPGlobal.log("WEAPON_PATCHER", w.getName()
		// + ": Starting to create copycat artifact");

		if (null == (newWeapon = this.createCopycatWeapon(w))) {
			return false;
		} else if (null == (this.createCopycatCraftingRecipe(newWeapon, w, wm))) {
			return false;
		}

		this.addMeltdownRecipe(newWeapon, wt, wm);
		this.addTemperingRecipe(newWeapon, wm);

		this.createRefinedSilverWeapon(newWeapon);
		this.createReforgedWeapon(newWeapon, wt, wm);

		this.applyModifiers(newWeapon);

		return true;
	}

	/**
	 * Creates the copycat weapon
	 * 
	 * @param w
	 * @return
	 */
	private WEAP createCopycatWeapon(WEAP w) {

		String newName = w.getName() + " ["
				+ this.s.getOutputString(Statics.S_REPLICA) + "]";

		WEAP newWeapon = (WEAP) patch.makeCopy(w, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + newName + w.getFormStr());

		newWeapon.setName(newName);
		newWeapon.setEnchantment(FormID.NULL);
		newWeapon.setEnchantmentCharge(0);

		return newWeapon;
	}

	/**
	 * Creates the copycat weapon's crafting recipe
	 * 
	 * @param w
	 * @param wm
	 * @return
	 */
	private COBJ createCopycatCraftingRecipe(WEAP newWeapon, WEAP oldWeapon,
			WeaponMaterial wm) {

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON
				+ Statics.S_PREFIX_CRAFTING + newWeapon.getName()
				+ newWeapon.getFormStr());

		FormID materialPerk = wm.getMaterialTemper().getRelatedSmithingPerk();
		FormID input = wm.getMaterialTemper().getRelatedTemperingInput();

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newWeapon.getForm());

		if (input != null) {
			c.addIngredient(input, 3);
		}

		c.addIngredient(Statics.artifactEssence, 1);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingCopycat);
		c1.setRunOnType(RunOnType.Subject);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);

		c.addCondition(c1);

		if (materialPerk != null) {
			Condition c2 = new Condition(Condition.P_FormID.HasPerk,
					materialPerk);
			c2.setRunOnType(RunOnType.Subject);
			c2.setOperator(Operator.EqualTo);
			c2.setValue(1.0f);

			c.addCondition(c2);

		}

		Condition c3 = new Condition(Condition.P_FormID.GetItemCount,
				oldWeapon.getForm());
		c3.setRunOnType(RunOnType.Subject);
		c3.setOperator(Operator.GreaterThanOrEqual);
		c3.setValue(1.0f);

		c.addCondition(c3);

		// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName()
		// + ": Done adding copycat crafting recipe");

		this.patch.addRecord(c);

		return c;
	}

	/**
	 * Create all records related to the various crossbow enhancements
	 * 
	 * @param w
	 * @param wm
	 * @param wt
	 * @return
	 */

	private boolean createCrossbowVariants(WEAP w, WeaponMaterial wm,
			WeaponType wt) {

		if (!(w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwWeapTypeCrossbow))) {
			return false;
		}

		ArrayList<WEAP> newCrossbows = new ArrayList<>();

		// create all that jizz
		// single CB enhancements

		WEAP newRecurveCrossbow = this.applyRecurveCrossbowModifications(w);
		this.createEnhancedCrossbowCraftingRecipe(w, newRecurveCrossbow, wm, 1,
				new FormID[] { Statics.perkRangedWeaponryAspiringEngineer0 });

		WEAP newLightweightCrossbow = this
				.applyLightweightCrossbowModifications(w);
		this.createEnhancedCrossbowCraftingRecipe(w, newLightweightCrossbow,
				wm, 1,
				new FormID[] { Statics.perkRangedWeaponryAspiringEngineer1 });

		WEAP newArbalestCrossbow = this.applyArbalestCrossbowModifications(w);
		this.createEnhancedCrossbowCraftingRecipe(w, newArbalestCrossbow, wm,
				1,
				new FormID[] { Statics.perkRangedWeaponryProficientEngineer0 });

		WEAP newSilencedCrossbow = this.applySilencedCrossbowModifications(w);
		this.createEnhancedCrossbowCraftingRecipe(w, newSilencedCrossbow, wm,
				1,
				new FormID[] { Statics.perkRangedWeaponryProficientEngineer0 });

		// dual enhancements with 2 recipes each

		WEAP newRecurveArbalestCrossbow = this
				.applyArbalestCrossbowModifications(newRecurveCrossbow);
		this.createEnhancedCrossbowCraftingRecipe(newRecurveCrossbow,
				newRecurveArbalestCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer0,
						Statics.perkRangedWeaponryAspiringEngineer0,
						Statics.perkRangedWeaponryCrossbowTechnician });
		this.createEnhancedCrossbowCraftingRecipe(newArbalestCrossbow,
				newRecurveArbalestCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer0,
						Statics.perkRangedWeaponryAspiringEngineer0,
						Statics.perkRangedWeaponryCrossbowTechnician });

		WEAP newRecurveSilencedCrossbow = this
				.applySilencedCrossbowModifications(newRecurveCrossbow);
		this.createEnhancedCrossbowCraftingRecipe(newRecurveCrossbow,
				newRecurveSilencedCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer1,
						Statics.perkRangedWeaponryAspiringEngineer0,
						Statics.perkRangedWeaponryCrossbowTechnician });
		this.createEnhancedCrossbowCraftingRecipe(newSilencedCrossbow,
				newRecurveSilencedCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer1,
						Statics.perkRangedWeaponryAspiringEngineer0,
						Statics.perkRangedWeaponryCrossbowTechnician });

		WEAP newRecurveLightweightCrossbow = this
				.applyLightweightCrossbowModifications(newRecurveCrossbow);
		this.createEnhancedCrossbowCraftingRecipe(newRecurveCrossbow,
				newRecurveLightweightCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryAspiringEngineer1,
						Statics.perkRangedWeaponryAspiringEngineer0,
						Statics.perkRangedWeaponryCrossbowTechnician });
		this.createEnhancedCrossbowCraftingRecipe(newLightweightCrossbow,
				newRecurveLightweightCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryAspiringEngineer1,
						Statics.perkRangedWeaponryAspiringEngineer0,
						Statics.perkRangedWeaponryCrossbowTechnician });

		WEAP newSilencedLightweightCrossbow = this
				.applyLightweightCrossbowModifications(newSilencedCrossbow);
		this.createEnhancedCrossbowCraftingRecipe(newSilencedCrossbow,
				newSilencedLightweightCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer1,
						Statics.perkRangedWeaponryAspiringEngineer1,
						Statics.perkRangedWeaponryCrossbowTechnician });
		this.createEnhancedCrossbowCraftingRecipe(newLightweightCrossbow,
				newSilencedLightweightCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer1,
						Statics.perkRangedWeaponryAspiringEngineer1,
						Statics.perkRangedWeaponryCrossbowTechnician });

		WEAP newSilencedArbalestCrossbow = this
				.applyArbalestCrossbowModifications(newSilencedCrossbow);
		this.createEnhancedCrossbowCraftingRecipe(newArbalestCrossbow,
				newSilencedArbalestCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer0,
						Statics.perkRangedWeaponryProficientEngineer1,
						Statics.perkRangedWeaponryCrossbowTechnician });
		this.createEnhancedCrossbowCraftingRecipe(newSilencedCrossbow,
				newSilencedArbalestCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer0,
						Statics.perkRangedWeaponryProficientEngineer1,
						Statics.perkRangedWeaponryCrossbowTechnician });

		WEAP newLightweightArbalestCrossbow = this
				.applyArbalestCrossbowModifications(newLightweightCrossbow);
		this.createEnhancedCrossbowCraftingRecipe(newLightweightCrossbow,
				newLightweightArbalestCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer0,
						Statics.perkRangedWeaponryAspiringEngineer1,
						Statics.perkRangedWeaponryCrossbowTechnician });
		this.createEnhancedCrossbowCraftingRecipe(newArbalestCrossbow,
				newLightweightArbalestCrossbow, wm, 2, new FormID[] {
						Statics.perkRangedWeaponryProficientEngineer0,
						Statics.perkRangedWeaponryAspiringEngineer1,
						Statics.perkRangedWeaponryCrossbowTechnician });

		newCrossbows.add(newRecurveCrossbow);
		newCrossbows.add(newLightweightCrossbow);
		newCrossbows.add(newArbalestCrossbow);
		newCrossbows.add(newSilencedCrossbow);
		newCrossbows.add(newLightweightArbalestCrossbow);
		newCrossbows.add(newSilencedArbalestCrossbow);
		newCrossbows.add(newSilencedLightweightCrossbow);
		newCrossbows.add(newRecurveLightweightCrossbow);
		newCrossbows.add(newRecurveSilencedCrossbow);
		newCrossbows.add(newRecurveArbalestCrossbow);

		// add tempering and meltdown recipes for everything, and
		// reforged/warforged variants
		for (WEAP c : newCrossbows) {
			this.addMeltdownRecipe(c, wt, wm);
			this.addTemperingRecipe(c, wm);
			this.createReforgedWeapon(c, wt, wm);

			WEAP reforgedWeapon = createReforgedWeapon(c, wt, wm);
			this.applyModifiers(reforgedWeapon);
			this.addReforgedCraftingRecipe(reforgedWeapon, c, wm);
			this.addMeltdownRecipe(reforgedWeapon, wt, wm);
			this.addTemperingRecipe(reforgedWeapon, wm);

			WEAP warforgedWeapon = createWarforgedWeapon(c, wt, wm);
			this.addWarforgedCraftingRecipe(warforgedWeapon, reforgedWeapon, wm);
			this.addMeltdownRecipe(warforgedWeapon, wt, wm);
			this.addTemperingRecipe(warforgedWeapon, wm);
			this.applyModifiers(warforgedWeapon);

			this.applyModifiers(c);
		}

		return true;
	}

	/**
	 * Create silenced crossbow tweaks
	 * 
	 * @return
	 */

	private WEAP applySilencedCrossbowModifications(WEAP w) {

		if (w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwCrossbowSilenced)) {
			return null;
		}

		String newName = w.getName() + " ["
				+ this.s.getOutputString(Statics.S_CROSSBOW_SILENCED) + "]";

		WEAP newCrossbow = (WEAP) patch.makeCopy(w, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + newName + w.getFormStr());
		newCrossbow.setName(newName);
		newCrossbow.getKeywordSet().getKeywordRefs()
				.add(Statics.kwCrossbowSilenced);
		newCrossbow.setDetectionSoundLevel(SoundVolume.Silent);

		return newCrossbow;
	}

	/**
	 * Add recurve crossbow tweaks
	 * 
	 * @return
	 */

	private WEAP applyRecurveCrossbowModifications(WEAP w) {

		if (w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwCrossbowRecurve)) {
			return null;
		}

		String newName = w.getName() + " ["
				+ this.s.getOutputString(Statics.S_CROSSBOW_RECURVE) + "]";

		WEAP newCrossbow = (WEAP) patch.makeCopy(w, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + newName + w.getFormStr());

		newCrossbow.setName(newName);
		newCrossbow.getKeywordSet().getKeywordRefs()
				.add(Statics.kwCrossbowRecurve);

		return newCrossbow;
	}

	/**
	 * Add lightweight crossbow tweaks
	 * 
	 * @return
	 */
	private WEAP applyLightweightCrossbowModifications(WEAP w) {

		if (w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwCrossbowLightweight)) {
			return null;
		}

		String newName = w.getName() + " ["
				+ this.s.getOutputString(Statics.S_CROSSBOW_LIGHTWEIGHT) + "]";

		WEAP newCrossbow = (WEAP) patch.makeCopy(w, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + newName + w.getFormStr());

		newCrossbow.setName(newName);
		newCrossbow.getKeywordSet().getKeywordRefs()
				.add(Statics.kwCrossbowLightweight);

		return newCrossbow;
	}

	/**
	 * Add arbalest crossbow tweaks
	 */
	private WEAP applyArbalestCrossbowModifications(WEAP w) {

		if (w.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwCrossbowArbalest)) {
			return null;
		}

		String newName = w.getName() + " ["
				+ this.s.getOutputString(Statics.S_CROSSBOW_ARBALEST) + "]";

		WEAP newCrossbow = (WEAP) patch.makeCopy(w, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_WEAPON + newName + w.getFormStr());

		newCrossbow.setName(newName);
		newCrossbow.setWeight((float) (w.getWeight() * 1.2));
		newCrossbow.getKeywordSet().getKeywordRefs()
				.add(Statics.kwCrossbowArbalest);

		return newCrossbow;
	}

	/**
	 * Creates the crafting recipe for an enhanced crossbow
	 * 
	 * @param oldWeapon
	 * @param newWeapon
	 */
	private COBJ createEnhancedCrossbowCraftingRecipe(WEAP oldWeapon,
			WEAP newWeapon, WeaponMaterial wm, int numKits, FormID[] perks) {

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON
				+ Statics.S_PREFIX_CRAFTING + newWeapon.getName()
				+ newWeapon.getFormStr());

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newWeapon.getForm());

		c.addIngredient(Statics.crossbowModificationKit, numKits);
		c.addIngredient(oldWeapon.getForm(), 1);

		for (FormID perk : perks) {

			Condition c1 = new Condition(Condition.P_FormID.HasPerk, perk);
			c1.setRunOnType(RunOnType.Subject);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);

			c.addCondition(c1);
		}

		Condition c2 = new Condition(Condition.P_FormID.GetItemCount,
				oldWeapon.getForm());
		c2.setRunOnType(RunOnType.Subject);
		c2.setOperator(Operator.GreaterThanOrEqual);
		c2.setValue(1.0f);

		c.addCondition(c2);

		// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName()
		// + ": Done adding crafting  recipe");

		this.patch.addRecord(c);

		return c;

	}

	private WEAP createEnchantedWeaponFromTemplate(WEAP template, WEAP like,
			FormID ench) {
		String newName = this.s.getLocalizedEnchantmentNameWeapon(template,
				(ENCH) SPDatabase.getMajor(ench, GRUP_TYPE.ENCH));
		WEAP newWeapon = (WEAP) patch.makeCopy(template,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_WEAPON + newName
						+ ench.getFormStr());
		newWeapon.setTemplate(template.getForm());
		newWeapon.setEnchantment(ench);
		newWeapon.setEnchantmentCharge(like.getEnchantmentCharge());
		newWeapon.setValue(like.getValue());
		newWeapon.setName(newName);
		// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName() + ": created.");

		return newWeapon;
	}

	/**
	 * Checks whether a weapon already exists with a given enchantment. Make
	 * sure to only call this on "similar" weapons.
	 * 
	 * @param w
	 *            enchanted weapon
	 * @param id
	 *            enchantment form id
	 * @return
	 */
	private WEAP doesWeaponExistWithEnchantment(WEAP w, FormID id) {
		for (WEAP we : this.merger.getWeapons()) {
			if (we.getTemplate().equals(w.getForm())
					&& we.getEnchantment().equals(id)) {
				return we;
			}
		}

		for (WEAP we : this.patch.getWeapons()) {
			if (we.getTemplate().equals(w.getForm())
					&& we.getEnchantment().equals(id)) {
				return we;
			}
		}

		return null;
	}

	/**
	 * Get all weapons similar to w. Uses a slightly different similarity
	 * definition than leveled lists
	 * 
	 * @param w
	 * @return
	 */
	private ArrayList<WEAP> getSimilarWeapons(WEAP w) {

		ArrayList<WEAP> ret = new ArrayList<>();

		if (WeaponPatcher.weaponsWithNoMaterialOrType.contains(w)) {
			return ret;
		}

		for (WEAP we : this.merger.getWeapons()) {
			if (WeaponPatcher.weaponsWithNoMaterialOrType.contains(we)) {
				continue;
			}
			if (this.areWeaponsSimilar(w, we)) {

				if (this.s.canWeaponsNotBeSimilar(w, we)) {
					SPGlobal.log("WEAPON_PATCHER", w.getName()
							+ ": weapon similar to " + we.getName()
							+ ", but excluded with complex exclusion");
					continue;
				}

				// SPGlobal.log("WEAPON_PATCHER", w.getName()
				// + ": weapon similar to " + we.getName());
				ret.add(we);
			}
		}
		return ret;
	}

	private void processDirectEnchantmentBindings() {
		ArrayList<Bucket<String, String>> directBindings = this.s
				.getDirectEnchantmentBindingsAsBucketList();

//		for (Bucket<String, String> b : directBindings) {
//			SPGlobal.log("WEAP-DEBUG-EB:", b.toString());
//		}

		// TODO fix last parameter
		ArrayList<Bucket<WEAP, WEAP>> enchantedWeapons = this
				.generateEnchantedWeaponsFromBindingList(directBindings, false);

//		for (Bucket<WEAP, WEAP> b : enchantedWeapons) {
//			SPGlobal.log("WEAP-DEBUG-EnWe:", b.toString());
//		}

		this.distributeEnchantedWeaponsFromBuckets(enchantedWeapons);

		// this.distributeEnchantedWeaponsOnLeveledListsByDirectEnchantmentBinding(this
		// .createEnchantedWeaponVariantsByDirectEnchantmentBinding());
	}

	private void distributeEnchantedWeaponsFromBuckets(
			ArrayList<Bucket<WEAP, WEAP>> weaponBuckets) {

		ArrayList<LeveledEntry> newEntries;
		ArrayList<WEAP> newWeapons;
		WEAP currentWeapon;
		ArrayList<Mod> toProcess = new ArrayList<>();
		toProcess.add(this.patch);
		toProcess.add(this.merger);

		for (Mod m : toProcess) {
			for (LVLI li : m.getLeveledItems()) {
				if (this.s.isListExcludedEnchantmentWeapon(li)) {
					continue;
				}
				if (li.get(LVLFlag.UseAll)) {
					continue;
				}

				newEntries = new ArrayList<>();

				for (LeveledEntry le : li.getEntries()) {
					if (null == (currentWeapon = (WEAP) SPDatabase.getMajor(
							le.getForm(), GRUP_TYPE.WEAP))) {
						continue;
					}

					if (null == (newWeapons = Bucket.getBindingsFromListByKey(
							weaponBuckets, currentWeapon))) {
						continue;
					}
					// insert all new weapons
					for (WEAP w : newWeapons) {
						newEntries.add(new LeveledEntry(w.getForm(), le
								.getLevel(), le.getCount()));
					}
				}

				if (newEntries.size() > 0) {
					for (LeveledEntry newLE : newEntries) {
						SPGlobal.log("WEAP_DEBUG", "Adding " + newLE.getForm()
								+ " to " + li.getForm());
						li.addEntry(newLE);
					}
					this.patch.addRecord(li);
				}
			}
		}
	}

	/**
	 * Take buckets full of ENCH EDIDs, and create enchanted weapons
	 * 
	 * @param directBindings
	 * @return
	 */
	// TODO finish?
	private ArrayList<Bucket<WEAP, WEAP>> generateEnchantedWeaponsFromBindingList(
			ArrayList<Bucket<String, String>> directBindings,
			boolean fillWithSimilar) {

		ArrayList<Bucket<WEAP, WEAP>> ret = new ArrayList<>();
		ENCH e;
		Bucket<String, String> ENCHBucket;
		ArrayList<WEAP> variants;
		WEAP wBase;
		// saves weapons we already created
		ArrayList<Tuple<WEAP, String>> generatedWeapons = new ArrayList<Tuple<WEAP, String>>();
		ArrayList<Tuple<WEAP, String>> generatedSimilarWeapons = new ArrayList<Tuple<WEAP, String>>();
		Tuple<WEAP, String> currentTuple;
		FormID newEnchFormID;

		for (WEAP w : this.merger.getWeapons()) {
			if (WeaponPatcher.weaponsWithNoMaterialOrType.contains(w)) {
				continue;
			}

			if (w.getEnchantment().isNull()) {
				continue;
			}

			if (this.s.isWeaponExcludedEnchantment(w)) {
				SPGlobal.log("WEAPON_PATCHER", w.getName()
						+ ": Weapon excluded from enchantment");
				continue;

			}
			// get the template weapon
			if (null == (wBase = (WEAP) SPDatabase.getMajor(w.getTemplate(),
					GRUP_TYPE.WEAP))) {

				SPGlobal.log("WEAPON_PATCHER", w.getName()
						+ ": Didn't find template");
				continue;
			}

			// template excluded?
			if (this.s.isWeaponExcludedEnchantment(wBase)) {
				SPGlobal.log("WEAPON_PATCHER", w.getName()
						+ ": Weapon template " + wBase.getName()
						+ " excluded from enchantment");
				continue;

			}

			e = (ENCH) SPDatabase.getMajor(w.getEnchantment(), GRUP_TYPE.ENCH);

			if (null == (ENCHBucket = Bucket.getBucketWithKeyFromList(
					directBindings, e.getEDID()))) {
				continue;
			}

			variants = new ArrayList<>();
			// create new bucket with base weapon and all variants
			for (String newEnchEDID : ENCHBucket.getBindings()) {

				currentTuple = new Tuple<WEAP, String>(w, newEnchEDID);
				if (generatedWeapons.contains(currentTuple)) {
					continue;
				}

				generatedWeapons.add(currentTuple);

				if (null == (newEnchFormID = Statics.getEnchFormIDFromEDID(
						newEnchEDID, merger))) {
					continue;
				}

				variants.add(this
						.createEnchantedWeaponFromTemplate(wBase, w,
								Statics.getEnchFormIDFromEDID(newEnchEDID,
										this.merger)));
			}
			// if told to do so, also detect similar weapons and create
			// enchanted variants
			if (fillWithSimilar) {
				Bucket<WEAP, WEAP> b;

				if (null == (b = Bucket.getBucketWithKeyFromList(
						similarWeaponBuckets, wBase))) {
					b = new Bucket<WEAP, WEAP>(wBase,
							this.getSimilarWeapons(wBase));
					similarWeaponBuckets.add(b);
				}

				for (String newEnchEDID : ENCHBucket.getBindings()) {
					for (WEAP ws : b.getBindings()) {

						Tuple<WEAP, String> t = new Tuple<>(ws, newEnchEDID);

						if (generatedSimilarWeapons.contains(t)) {
							continue;
						}
						// TODO finish

						generatedSimilarWeapons.add(t);

						variants.add(this.createEnchantedWeaponFromTemplate(ws,
								w, Statics.getEnchFormIDFromEDID(newEnchEDID,
										this.merger)));
					}
				}
			}
			ret.add(new Bucket<WEAP, WEAP>(w, variants));
		}

		return ret;

	}

	/**
	 * Create an distribute enchanted armor based on list bindings
	 */

	private void processListEnchantmentBindings() {

		int failedLookups = 0, successfullLookups = 0;

		ArrayList<LVLI> boundLists = new ArrayList<>();
		ArrayList<ArrayList<String>> newENCHEdids = new ArrayList<>();
		ArrayList<ArrayList<String>> baseENCHEdids = new ArrayList<>();
		ArrayList<LeveledEntry> newEntries;
		LVLI currentLVLI;
		WEAP currentWeapon, currentWeaponBase, newWeapon;
		FormID baseEnchFormID, newEnchFormID;

		for (String edid : this.s.getListsOnListEnchantmentBindings()) {
			if (null != (currentLVLI = Statics.getLVLIFromEDID(edid,
					this.merger, this.patch))) {
				boundLists.add(currentLVLI);
				// SPGlobal.log("WEAPON_PATCHER", edid
				// + ": Has list enchantment binding");
			}
		}

		for (ArrayList<String> baseENCHEdidSublists : baseENCHEdids) {

			int index0 = baseENCHEdids.indexOf(baseENCHEdidSublists);
			if (index0 < 0) {
				SPGlobal.log("WEAPON_PATCHER", baseENCHEdids.toString()
						+ ": failed to recieve index of "
						+ baseENCHEdidSublists.toString());
				continue;
			}

			for (LVLI l : boundLists) {
				// for each list, get the replacement instructions
				if (0 == (newENCHEdids = this.s
						.getNewENCHEdidsOnListEnchantmentBinding(l)).size()) {
					SPGlobal.log(
							"WEAPON_PATCHER",
							l.getEDID()
									+ ": Has list enchantment binding, but newENCHEdids has length 0");
					continue;
				}

				if (0 == (baseENCHEdids = this.s
						.getBaseENCHEdidsOnListEnchantmentBinding(l)).size()) {
					SPGlobal.log(
							"WEAPON_PATCHER",
							l.getEDID()
									+ ": Has list enchantment binding, but baseENCHEdids has length 0");
					continue;
				}

				// SPGlobal.log("WEAPON_PATCHER",
				// "newENCHEdids: " + newENCHEdids.toString());
				// SPGlobal.log("WEAPON_PATCHER", "baseENCHEdids: "
				// + baseENCHEdids.toString());

				if (newENCHEdids.size() != baseENCHEdids.size()) {

					SPGlobal.log("WEAPON_PATCHER", "Size not equal; aborting.");
					continue;
				}

				// if configured to do so, gather similar weapons to each piece
				// on
				// l,
				// and expand l with it
				if (this.s.shouldFillUpListOnListEnchantmentBinding(l)) {

					ArrayList<WEAP> similarWeapons;
					newEntries = new ArrayList<>();

					for (LeveledEntry le : l.getEntries()) {

						currentWeapon = (WEAP) SPDatabase.getMajor(
								le.getForm(), GRUP_TYPE.WEAP);

						if (currentWeapon == null
								|| currentWeapon.getEnchantment() == null
								|| currentWeapon.getEnchantment().equals(
										FormID.NULL)) {
							continue;
						}

						if (null == (currentWeaponBase = (WEAP) SPDatabase
								.getMajor(currentWeapon.getTemplate(),
										GRUP_TYPE.WEAP))) {
							continue;
						}

						if (weaponsWithoutSimilars.contains(currentWeaponBase)) {
							continue;
						}

						// TODO check if works

						if (Bucket.doesBucketListContainKey(
								similarWeaponBuckets, currentWeaponBase)) {
							similarWeapons = Bucket.getBindingsFromListByKey(
									similarWeaponBuckets, currentWeaponBase);
							successfullLookups++;
						} else {
							failedLookups++;
							if (0 == (similarWeapons = this
									.getSimilarWeapons(currentWeaponBase))
									.size()) {
								weaponsWithoutSimilars.add(currentWeaponBase);
								continue;
							}

							similarWeaponBuckets.add(new Bucket<WEAP, WEAP>(
									currentWeaponBase, similarWeapons));
						}

						for (WEAP w : similarWeapons) {
							if (null == this.doesWeaponExistWithEnchantment(w,
									currentWeapon.getEnchantment())) {

								newWeapon = this
										.createEnchantedWeaponFromTemplate(w,
												currentWeapon,
												currentWeapon.getEnchantment());

								newEntries.add(new LeveledEntry(newWeapon
										.getForm(), le.getLevel(), le
										.getCount()));

								// SPGlobal.log(
								// "WEAPON_PATCHER",
								// newWeapon.getName()
								// + ": adding to list "
								// + l.getEDID());
							}
						}
					}

					if (newEntries.size() > 0) {
						for (LeveledEntry le : newEntries) {
							l.addEntry(le);
						}
						newEntries.clear();
						this.patch.addRecord(l);
					}

				}

				newEntries = new ArrayList<>();

				for (LeveledEntry le : l.getEntries()) {
					currentWeapon = (WEAP) SPDatabase.getMajor(le.getForm(),
							GRUP_TYPE.WEAP);

					if (currentWeapon == null
							|| currentWeapon.getEnchantment() == null
							|| currentWeapon.getEnchantment().equals(
									FormID.NULL)) {
						continue;
					}

					if (null == (currentWeaponBase = (WEAP) SPDatabase
							.getMajor(currentWeapon.getTemplate(),
									GRUP_TYPE.WEAP))) {
						continue;
					}

					// search for fitting oldEnch

					newEnchFormID = null;
					int index1;

					for (String baseENCH : baseENCHEdidSublists) {
						if (null == (baseEnchFormID = Statics
								.getEnchFormIDFromEDID(baseENCH, this.merger))) {
							SPGlobal.log(
									"WEAPON_PATCHER",
									"createEnchantedWeaponVariantsByDirectEnchantmentBinding: received null FormID for EDID "
											+ baseENCH);
							continue;
						}

						index1 = baseENCHEdidSublists.indexOf(baseENCH);

						if (index1 < 0) {
							SPGlobal.log("ARMOR_PATCHER",
									baseENCHEdidSublists.toString()
											+ ": failed to recieve index of "
											+ baseENCH);
							continue;
						}

						if (baseEnchFormID.equals(currentWeapon
								.getEnchantment())) {
							if (null == (newEnchFormID = Statics
									.getEnchFormIDFromEDID(
											newENCHEdids.get(index0)
													.get(index1), this.merger))) {
								SPGlobal.log(
										"WEAPON_PATCHER",
										l.getEDID()
												+ baseENCH
												+ ": Has list enchantment binding, but failed to retrieve new ENCH from EDID "
												+ newENCHEdids.get(index0).get(
														index1));
								continue;
							}
							break;
						}
					}

					if (newEnchFormID == null) {
						continue;
					}

					// create new ARMO with new ENCH , if it doesn't exist
					if (null == (newWeapon = this
							.doesWeaponExistWithEnchantment(currentWeaponBase,
									newEnchFormID))) {
						newWeapon = this
								.createEnchantedWeaponFromTemplate(
										currentWeaponBase, currentWeapon,
										newEnchFormID);
					}

					newEntries.add(new LeveledEntry(newWeapon.getForm(), le
							.getLevel(), le.getCount()));

					// SPGlobal.log("WEAPON_PATCHER", newWeapon.getName()
					// + ": adding to list " + l.getEDID());
				}

				if (newEntries.size() <= 0) {
					continue;
				}

				// copy LVLI

				currentLVLI = (LVLI) patch.makeCopy(l, Statics.S_PREFIX_PATCHER
						+ Statics.S_PREFIX_LVLI + Statics.S_PREFIX_WEAPON
						+ Statics.getFormCount());
				currentLVLI.clearEntries();
				for (LeveledEntry le : newEntries) {
					currentLVLI.addEntry(le);
				}

				// now, place the new list wherever the old one is referenced

				ArrayList<String> processed = new ArrayList<>();

				for (LVLI li : this.patch.getLeveledItems()) {
					processed.add(li.getEDID());

					if (li.get(LVLFlag.UseAll)) {
						continue;
					}

					newEntries = new ArrayList<>();

					for (LeveledEntry lile : li.getEntries()) {
						if (lile.getForm().equals(l.getForm())) {
							newEntries.add(new LeveledEntry(currentLVLI
									.getForm(), lile.getLevel(), lile
									.getCount()));

							// SPGlobal.log("WEAPON_PATCHER", l.getEDID()
							// + ": adding to list " + li.getEDID());
							;
						}
					}

					if (newEntries.size() > 0) {
						for (LeveledEntry newLE : newEntries) {
							li.addEntry(newLE);
						}
					}
				}

				for (LVLI li : this.merger.getLeveledItems()) {

					if (processed.contains(li.getEDID())) {
						continue;
					}
					if (li.get(LVLFlag.UseAll)) {
						continue;
					}

					newEntries = new ArrayList<>();

					for (LeveledEntry lile : li.getEntries()) {
						if (lile.getForm().equals(l.getForm())) {
							newEntries.add(new LeveledEntry(currentLVLI
									.getForm(), lile.getLevel(), lile
									.getCount()));
						}
					}

					if (newEntries.size() > 0) {
						for (LeveledEntry newLE : newEntries) {
							li.addEntry(newLE);
						}
						this.patch.addRecord(li);
					}
				}
			}
		}

		SPGlobal.log("WEAPON_PATCHER",
				"processListEnchantmentBindings(): Successfull lookups: "
						+ successfullLookups + ", failed lookups: "
						+ failedLookups);
	}

	public String getInfo() {
		return "Weapon stats, variants, keywords...";
	}
}
