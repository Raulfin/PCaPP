package patcher;

import java.util.ArrayList;

import skyproc.ARMO;
import skyproc.BodyTemplate.BodyTemplateType;
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
import skyproc.SubFormInt;
import skyproc.genenums.ArmorType;
import skyproc.gui.SPProgressBarPlug;
import util.Bucket;
import util.Statics;
import util.Tuple;
import xml.access.XmlStorage;
import xml.lowLevel.armor.ArmorMaterial;
import xml.lowLevel.armor.ArmorModifier;

public class ArmorPatcher implements Patcher {

	private XmlStorage s;
	private Mod merger, patch;
	private ArrayList<Bucket<ARMO, ARMO>> similarArmorBuckets = new ArrayList<>();
	private ArrayList<ARMO> armorWithoutSimilars = new ArrayList<>();

	public static ArrayList<ARMO> armorWithNoMaterialOrType = new ArrayList<>();

	public ArmorPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		boolean addRecord = false;
		ARMO reforgedArmor = null, warforgedArmor = null;
		ArmorMaterial am = null;

		SPGlobal.log("ARMOR_PATCHER", "num armors: " + this.merger.getMasters());
		SPGlobal.log("ARMOR_PATCHER", "num armors: " + this.merger.getArmors().size());

		
		for (ARMO a : this.merger.getArmors()) {
			SPGlobal.log("ARMOR_PATCHER", a.getName() + ": started patching");
			try {
				if (!(this.shouldPatch(a))) {
					SPGlobal.log("ARMOR_PATCHER", a.getName() + ": Ignored.");
					continue;
				}

				// do clothing specific stuff, then skip to next armor
				if (this.s.isClothing(a)) {
					if (this.s.useWarrior()) {
						this.addClothingMeltdownRecipe(a);
						continue;
					}
					if (this.s.useThief()) {
						if (this.makeClothingExpensive(a)) {
							this.patch.addRecord(a);
							continue;
						}
					}
				}

				// check whether we have a material linked for a; if not, skip
				if (null == (am = this.s.getArmorMaterial(a))) {
					if (!(this.s.isJewelry(a))) {
						ArmorPatcher.armorWithNoMaterialOrType.add(a);
						SPGlobal.log("ARMOR_PATCHER", a.getName()
								+ ": No material.");
					}
					continue;
				}

				// general changes

				addRecord = addRecord | this.addSpecificKeyword(a, am);

				// changes only used when running the warrior module
				if (s.useWarrior()) {
					addRecord = addRecord | this.setArmorValue(a, am);

					if (!this.s.isArmorExcludedReforged(a)) {
						this.addMeltdownRecipe(a, am);
					}

					if (!this.s.isArmorExcludedReforged(a)
							&& !this.s.isClothing(a) && !this.s.isJewelry(a)) {

						reforgedArmor = this.createReforgedArmor(a, am);
						this.applyArmorModifiers(reforgedArmor);
						this.addTemperingRecipe(reforgedArmor, am);
						this.createReforgedCraftingRecipe(reforgedArmor, a, am);
						this.addMeltdownRecipe(reforgedArmor, am);

						warforgedArmor = this.createWarforgedArmor(a, am);
						this.applyArmorModifiers(warforgedArmor);
						this.addTemperingRecipe(warforgedArmor, am);
						this.createWarforgedCraftingRecipe(warforgedArmor,
								reforgedArmor, am);
						this.addMeltdownRecipe(warforgedArmor, am);
					}

					this.doCopycat(a, am);
				}

				if (this.s.useThief()) {
					addRecord = addRecord | this.addMasqueradeKeyword(a);
					this.doQualityLeather(a, am);
				}

				if (this.s.useWarrior()) {
					this.applyArmorModifiers(a);
				}

				// if changes were made, add record
				if (addRecord) {
					patch.addRecord(a);
					addRecord = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Armor Patcher: " + e.toString());
			}
		}

		// create and distribute enchanted armor - list bindings

		// SPGlobal.log("ARMOR_PATCHER",
		// "Starting to process list enchantment bindings");
		SPProgressBarPlug.setStatus("Task: Armor enchantments(1/2)");
		this.processListEnchantmentBindings();
		// SPGlobal.log("ARMOR_PATCHER",
		// "Done processing list enchantment bindings");

		// create and distribute enchanted armor - direct bindings

		// SPGlobal.log("ARMOR_PATCHER",
		// "Starting to process direct enchantment bindings");
		SPProgressBarPlug.setStatus("Task: Armor enchantments(2/2)");
		this.processDirectEnchantmentBindings();
		// SPGlobal.log("ARMOR_PATCHER",
		// "Done processing direct enchantment bindings");

	}

	private boolean makeClothingExpensive(ARMO a) {
		if (a.getValue() >= Statics.ExpensiveClothingThreshold
				&& a.getKeywordSet().getKeywordRefs()
						.contains(Statics.kwClothingBody)
				&& !a.getKeywordSet().getKeywordRefs()
						.contains(Statics.kwClothingRich)) {
			a.getKeywordSet().addKeywordRef(Statics.kwClothingRich);
			return true;
		}
		return false;
	}

	/**
	 * Uses some generic conditions to filter out unwanted armor.
	 * 
	 * @param a
	 * @return
	 */
	private boolean shouldPatch(ARMO a) {

		if (!(a.getTemplate().isNull())) {
			// SPGlobal.log("ARMOR_PATCHER", a.getName() + ": Has template");
			return false;
		} else if (this.s.isJewelry(a)) {
			// SPGlobal.log("ARMOR_PATCHER", a.getName() + ": Is jewelry");
			return false;
		} else if (ArmorPatcher.armorWithNoMaterialOrType.contains(a)) {
			// SPGlobal.log("ARMOR_PATCHER", a.getName() +
			// "previously excluded");
			return false;
		}

		return true;
	}

	/**
	 * Tries to add the armor to a masquerade faction
	 * 
	 * @param a
	 * @return
	 */
	private boolean addMasqueradeKeyword(ARMO a) {
		ArrayList<FormID> newKWs = this.s.getArmorMasqueradeKeywords(a);

		if (newKWs.size() == 0) {
			return false;
		}

		for (FormID kw : newKWs) {
			a.getKeywordSet().addKeywordRef(kw);
		}

		// SPGlobal.log("ARMOR_PATCHER", a.getName()
		// + ": Found masquerade faction(s)");
		return true;
	}

	/**
	 * Adds some more specific keywords to armor pieces to be picked up by perks
	 * and script logic.
	 * 
	 * @param a
	 * @return useless right now, always true
	 */

	// TODO make return value not suck
	private boolean addSpecificKeyword(ARMO a, ArmorMaterial am) {
		ArrayList<FormID> keywords = a.getKeywordSet().getKeywordRefs();

		// override basic keywords

		if (keywords.contains(Statics.kwArmorHeavy)) {
			keywords.remove(Statics.kwArmorHeavy);
		}
		if (keywords.contains(Statics.kwArmorLight)) {
			keywords.remove(Statics.kwArmorLight);
		}

		switch (am.getType()) {
		case LIGHT:
			keywords.add(Statics.kwArmorLight);
			a.getBodyTemplate().setArmorType(BodyTemplateType.Normal,
					ArmorType.LIGHT);
			break;
		case HEAVY:
			keywords.add(Statics.kwArmorHeavy);
			a.getBodyTemplate().setArmorType(BodyTemplateType.Normal,
					ArmorType.HEAVY);
			break;
		case BOTH:
			keywords.add(Statics.kwArmorHeavy);
			keywords.add(Statics.kwArmorLight);
			break;
		case UNDEFINED:
			return true;
		default:
			return true;
		}

		// add specific stuff

		if (keywords.contains(Statics.kwArmorSlotBoots)) {
			if (keywords.contains(Statics.kwArmorHeavy)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorHeavyLegs);
			}

			if (keywords.contains(Statics.kwArmorLight)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorLightLegs);
			}
		} else if (keywords.contains(Statics.kwArmorSlotCuirass)) {
			if (keywords.contains(Statics.kwArmorHeavy)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorHeavyChest);
			}

			if (keywords.contains(Statics.kwArmorLight)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorLightChest);
			}

		} else if (keywords.contains(Statics.kwArmorSlotGauntlets)) {
			if (keywords.contains(Statics.kwArmorHeavy)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorHeavyArms);
			}
			if (keywords.contains(Statics.kwArmorLight)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorLightArms);
			}
		} else if (keywords.contains(Statics.kwArmorSlotHelmet)) {
			if (keywords.contains(Statics.kwArmorHeavy)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorHeavyHead);
			}
			if (keywords.contains(Statics.kwArmorLight)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorLightHead);
			}
		} else if (keywords.contains(Statics.kwArmorSlotShield)) {

			if (keywords.contains(Statics.kwArmorHeavy)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorHeavyShield);
			}
			if (keywords.contains(Statics.kwArmorLight)) {
				a.getKeywordSet().addKeywordRef(Statics.kwArmorLightShield);
			}
		}
		return true;
	}

	/**
	 * Calculates a new armor values from the provided XML stats and sets it if
	 * appropriate.
	 * 
	 * @param a
	 * @param am
	 * @return true if value was changed
	 */
	private boolean setArmorValue(ARMO a, ArmorMaterial am) {
		int originalArmorValue = a.getArmorRating();
		int newArmorValue = (int) (am.getArmorBase()
				* this.s.getArmorSlotMultiplier(a) * 100);

		if (originalArmorValue != newArmorValue && newArmorValue > 0) {
			a.setArmorRating(newArmorValue);
			return true;
		} else if (newArmorValue < 0) {
			SPGlobal.log("ARMOR_PATCHER", a.getName()
					+ ": Failed to patch armor rating.");
		}
		return false;
	}

	/**
	 * Generates a meltdown recipe for a given piece of armor and its
	 * XML-defined armor material.
	 * 
	 * @param a
	 * @param am
	 */
	private void addMeltdownRecipe(ARMO a, ArmorMaterial am) {
		FormID requiredPerk = am.getMaterialMeltdown().getRelatedSmithingPerk();
		FormID output = am.getMaterialMeltdown().getRelatedMeltdownProduct();
		FormID benchKW = am.getMaterialMeltdown()
				.getRelatedMeltdownCraftingStation();

		int inputNum = 1;
		int outputNum = this.s.getArmorMeltdownOutput(a);

		if ((null == output) || outputNum <= 0 || null == benchKW) {
			// SPGlobal.log("ARMOR_PATCHER", a.getName()
			// + ": No meltdown recipe generated.");
			return;
		}

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_ARMOR + Statics.S_PREFIX_MELTDOWN
				+ a.getName() + a.getFormStr());

		newRecipe.addIngredient(a.getForm(), inputNum);
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
				a.getForm());
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
	 * Creates a meltdown recipe for a piece of clothing.
	 * 
	 * @param a
	 */
	private void addClothingMeltdownRecipe(ARMO a) {

		FormID output = Statics.leatherStrips;
		FormID benchKW = Statics.kwCraftingTanningRack;

		int inputNum = 1;
		int outputNum = this.s.getArmorMeltdownOutput(a);

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_CLOTHING + Statics.S_PREFIX_MELTDOWN
				+ a.getName() + a.getFormStr());

		newRecipe.addIngredient(a.getForm(), inputNum);
		newRecipe.setResultFormID(output);
		newRecipe.setOutputQuantity(outputNum);
		newRecipe.setBenchKeywordFormID(benchKW);

		Condition c2 = new Condition(Condition.P_FormID.GetItemCount,
				a.getForm());
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
		// SPGlobal.log("ARMOR_PATCHER", a.getName()
		// + ": Finished adding meltdown recipe");
	}

	/**
	 * Adds tempering recipe for given ARMOR
	 * 
	 * @param a
	 * @param am
	 */
	private void addTemperingRecipe(ARMO a, ArmorMaterial am) {

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ARMOR
				+ Statics.S_PREFIX_TEMPER + a.getName() + a.getFormStr());

		FormID temperInput = am.getMaterialTemper().getRelatedTemperingInput();
		FormID perk = am.getMaterialTemper().getRelatedSmithingPerk();

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingArmorTable);
		c.setResultFormID(a.getForm());
		c.setOutputQuantity(1);

		if (null != temperInput) {
			c.addIngredient(am.getMaterialTemper().getRelatedTemperingInput(),
					1);
		}

		if (null != perk) {
			Condition c1 = new Condition(Condition.P_FormID.HasPerk, am
					.getMaterialTemper().getRelatedSmithingPerk());
			c1.setRunOnType(RunOnType.Subject);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);

			c.addCondition(c1);
		}

		this.patch.addRecord(c);

	}

	private ARMO createReforgedArmor(ARMO a, ArmorMaterial am) {

		// SPGlobal.log("ARMOR_PATCHER", a.getName() +
		// ": Adding reforged armor");

		String newName = this.s.getOutputString(Statics.S_REFORGED) + " "
				+ a.getName();

		ARMO newReforgedArmor = (ARMO) patch.makeCopy(
				a,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ARMOR + newName
						+ a.getFormStr());

		newReforgedArmor.setName(newName);

		return newReforgedArmor;

	}

	private ARMO createWarforgedArmor(ARMO a, ArmorMaterial am) {

		// SPGlobal.log("ARMOR_PATCHER", a.getName() +
		// ": Adding warforged armor");

		String newName = this.s.getOutputString(Statics.S_WARFORGED) + " "
				+ a.getName();

		ARMO newWarforgedArmor = (ARMO) patch.makeCopy(
				a,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ARMOR + newName
						+ a.getFormStr());
		newWarforgedArmor.setName(newName);
		newWarforgedArmor.getKeywordSet().addKeywordRef(
				Statics.kwSmithingWarforgedArmor);
		newWarforgedArmor.getKeywordSet().addKeywordRef(
				Statics.kwMagicDisallowEnchanting);
		newWarforgedArmor.setEnchantment(Statics.enchSmithingWarforgedArmor);

		return newWarforgedArmor;

	}

	/**
	 * Creates the copycat armor
	 * 
	 * @param a
	 * @return
	 */
	private ARMO createCopycatArmor(ARMO a) {

		String newName = a.getName() + " ["
				+ this.s.getOutputString(Statics.S_REPLICA) + "]";

		ARMO newArmor = (ARMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_ARMOR + newName + a.getFormStr());

		newArmor.setName(newName);
		newArmor.setEnchantment(FormID.NULL);
		this.applyArmorModifiers(newArmor);
		return newArmor;
	}

	/**
	 * Creates the reforged armor's crafting recipe
	 * 
	 * @param w
	 * @param wm
	 * @return
	 */
	private COBJ createReforgedCraftingRecipe(ARMO newArmor, ARMO oldArmor,
			ArmorMaterial am) {

		// SPGlobal.log("ARMOR_PATCHER", newArmor.getName()
		// + ": Adding reforged crafting recipe");

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_CRAFTING
				+ Statics.S_PREFIX_ARMOR + newArmor.getName()
				+ oldArmor.getFormStr());

		FormID materialPerk = am.getMaterialTemper().getRelatedSmithingPerk();
		FormID input = am.getMaterialTemper().getRelatedTemperingInput();

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newArmor.getForm());

		if (input != null) {
			c.addIngredient(input, 2);
		}

		c.addIngredient(oldArmor.getForm(), 1);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingArmorer);
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
				oldArmor.getForm());
		c3.setRunOnType(RunOnType.Subject);
		c3.setOperator(Operator.GreaterThanOrEqual);
		c3.setValue(1.0f);

		c.addCondition(c3);

		this.patch.addRecord(c);

		return c;
	}

	/**
	 * Creates the reforged armor's crafting recipe
	 * 
	 * @param w
	 * @param wm
	 * @return
	 */
	private COBJ createWarforgedCraftingRecipe(ARMO newArmor, ARMO oldArmor,
			ArmorMaterial am) {

		// SPGlobal.log("ARMOR_PATCHER", newArmor.getName()
		// + ": Adding warforged crafting recipe");

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_CRAFTING
				+ Statics.S_PREFIX_ARMOR + newArmor.getName()
				+ newArmor.getFormStr());

		FormID materialPerk = am.getMaterialTemper().getRelatedSmithingPerk();
		FormID input = am.getMaterialTemper().getRelatedTemperingInput();

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newArmor.getForm());

		if (input != null) {
			c.addIngredient(input, 5);
		}

		c.addIngredient(oldArmor.getForm(), 1);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkSmithingMasteryWarforged);
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

		Condition c4 = new Condition(Condition.P_FormID.GetItemCount,
				oldArmor.getForm());
		c4.setRunOnType(RunOnType.Subject);
		c4.setOperator(Operator.GreaterThanOrEqual);
		c4.setValue(1.0f);

		c.addCondition(c4);

		this.patch.addRecord(c);

		return c;
	}

	/**
	 * Creates the copycat armor's crafting recipe
	 * 
	 * @param w
	 * @param wm
	 * @return
	 */
	private COBJ createCopycatCraftingRecipe(ARMO newArmor, ARMO oldArmor,
			ArmorMaterial am) {

		COBJ c = new COBJ(Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ARMOR
				+ Statics.S_PREFIX_CRAFTING + newArmor.getName()
				+ newArmor.getFormStr());

		FormID materialPerk = am.getMaterialTemper().getRelatedSmithingPerk();
		FormID input = am.getMaterialTemper().getRelatedTemperingInput();

		c.setBenchKeywordFormID(Statics.kwCraftingSmithingForge);
		c.setResultFormID(newArmor.getForm());

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
				oldArmor.getForm());
		c3.setRunOnType(RunOnType.Subject);
		c3.setOperator(Operator.GreaterThanOrEqual);
		c3.setValue(1.0f);

		c.addCondition(c3);

		// SPGlobal.log("ARMOR_PATCHER", newArmor.getName()
		// + ": Done adding copycat crafting recipe");

		this.patch.addRecord(c);

		return c;
	}

	/**
	 * Create all records related to the Copycat perk
	 * 
	 * @param w
	 * @param wm
	 * @return
	 */

	private boolean doCopycat(ARMO a, ArmorMaterial am) {
		ARMO newArmor;

		if (!(a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwDaedricArtifact))) {
			return false;
		}

		// SPGlobal.log("ARMOR_PATCHER", a.getName()
		// + ": Starting to create copycat artifact");

		if (null == (newArmor = this.createCopycatArmor(a))) {
			return false;
		} else if (null == (this.createCopycatCraftingRecipe(newArmor, a, am))) {
			return false;
		}

		if (this.s.useWarrior() && !this.s.isJewelry(a)
				&& !this.s.isClothing(a)) {
			this.addMeltdownRecipe(newArmor, am);

			ARMO ar = this.createReforgedArmor(newArmor, am);
			this.createReforgedCraftingRecipe(ar, a, am);
			this.addMeltdownRecipe(ar, am);

			ARMO aw = this.createWarforgedArmor(ar, am);
			this.createWarforgedCraftingRecipe(aw, ar, am);
			this.addMeltdownRecipe(aw, am);
		}

		return true;
	}

	/**
	 * Do all changes needed to create a quality leather variant of a given
	 * armor, if it makes sense to do so
	 * 
	 * @return
	 */

	private boolean doQualityLeather(ARMO a, ArmorMaterial am) {

		if (!(a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwArmorMaterialLeather))) {
			return false;
		}

		ArrayList<COBJ> craftingRecipes = this.getCraftingRecipes(a);

		if (craftingRecipes.size() == 0) {
			SPGlobal.log(
					"ARMOR_PATCHER",
					a.getName()
							+ ": Leather material, but no crafting recipe. No quality leather variant created");
			return false;
		}

		ArrayList<COBJ> temperingRecipes = this.getTemperingRecipes(a);

		ARMO qa = this.createQualityLeatherVariant(a);
		this.createQualityLeatherRecipe(craftingRecipes, qa);
		this.createQualityLeatherRecipe(temperingRecipes, qa);

		this.addTemperingRecipe(qa, am);
		this.addMeltdownRecipe(qa, am);

		if (s.useWarrior()) {

			this.doCopycat(qa, am);

			ARMO qr = this.createReforgedArmor(qa, am);
			this.createReforgedCraftingRecipe(qr, a, am);
			this.addTemperingRecipe(qr, am);
			this.addMeltdownRecipe(qr, am);

			ARMO qw = this.createWarforgedArmor(qa, am);
			this.createWarforgedCraftingRecipe(qw, qr, am);
			this.addTemperingRecipe(qw, am);
			this.addMeltdownRecipe(qw, am);

		}

		return true;
	}

	private void createQualityLeatherRecipe(ArrayList<COBJ> recipes,
			ARMO newArmor) {
		for (COBJ c : recipes) {

			COBJ newRecipe = (COBJ) patch.makeCopy(c, Statics.S_PREFIX_PATCHER
					+ newArmor.getName() + newArmor.getFormStr());
			newRecipe.setResultFormID(newArmor.getForm());

			boolean needsLeatherStrips = false, needsLeather = false;

			for (SubFormInt i : newRecipe.getIngredients()) {
				if (i.getForm().equals(Statics.leather)) {
					i.setForm(Statics.qualityLeather);
					needsLeather = true;
				} else if (i.getForm().equals(Statics.leatherStrips)) {
					i.setForm(Statics.qualityLeatherStrips);
					needsLeatherStrips = true;
				}
			}

			newRecipe.getConditions().clear();

			Condition c1 = new Condition(Condition.P_FormID.HasPerk,
					Statics.perkSmithingLeather);
			c1.setRunOnType(RunOnType.Subject);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(1.0f);

			newRecipe.addCondition(c1);

			if (needsLeather) {
				Condition c2 = new Condition(Condition.P_FormID.GetItemCount,
						Statics.qualityLeather);
				c2.setRunOnType(RunOnType.Subject);
				c2.setOperator(Operator.GreaterThanOrEqual);
				c2.setValue(1.0f);

				newRecipe.addCondition(c2);

			}

			if (needsLeatherStrips) {
				Condition c3 = new Condition(Condition.P_FormID.GetItemCount,
						Statics.qualityLeatherStrips);
				c3.setRunOnType(RunOnType.Subject);
				c3.setOperator(Operator.GreaterThanOrEqual);
				c3.setValue(1.0f);

				newRecipe.addCondition(c3);

			}
		}
	}

	/**
	 * Get all crafting recipes for a given piece of armor
	 * 
	 * @param a
	 * @return
	 */
	private ArrayList<COBJ> getCraftingRecipes(ARMO a) {

		ArrayList<COBJ> craftingRecipes = new ArrayList<>();

		for (COBJ c : this.merger.getConstructibleObjects()) {
			if (c.getResultFormID().equals(a.getForm())
					&& c.getBenchKeywordFormID().equals(
							Statics.kwCraftingSmithingForge)) {
				craftingRecipes.add(c);
			}
		}

		return craftingRecipes;
	}

	/**
	 * Get all tempering recpes for a given pieve of armor
	 * 
	 * @param a
	 * @return
	 */
	private ArrayList<COBJ> getTemperingRecipes(ARMO a) {

		ArrayList<COBJ> temperingRecipes = new ArrayList<>();

		for (COBJ c : this.merger.getConstructibleObjects()) {
			if (c.getResultFormID().equals(a.getForm())
					&& c.getBenchKeywordFormID().equals(
							Statics.kwCraftingSmithingForge)) {
				temperingRecipes.add(c);
			}
		}

		return temperingRecipes;
	}

	/**
	 * Creates quality leather ARMO record
	 * 
	 * @param a
	 * @return
	 */
	private ARMO createQualityLeatherVariant(ARMO a) {

		String newName = a.getName() + " ["
				+ this.s.getOutputString(Statics.S_QUALITY) + "]";

		ARMO newQualityLeatherArmor = (ARMO) patch.makeCopy(
				a,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ARMOR + newName
						+ a.getFormStr());

		newQualityLeatherArmor.setName(newName);
		this.applyArmorModifiers(newQualityLeatherArmor);
		return newQualityLeatherArmor;
	}

	private void applyArmorModifiers(ARMO a) {

		ArrayList<ArmorModifier> ams = this.s.getArmorModifiers(a);

		if (null == ams) {
			return;
		}

		for (ArmorModifier am : ams) {
			SPGlobal.log("ARMOR_PATCHER", a.getName() + ": Apply modifier: "
					+ am.getIdentifier());
			a.setWeight((float) (a.getWeight() * am.getFactorWeight()));
			a.setValue((int) (a.getValue() * am.getFactorValue()));
			a.setArmorRating((int) (a.getArmorRating() * am.getFactorArmor()));
		}

	}

	/**
	 * Find out whether two pieces of armor are similar. Similar: Same slot,
	 * same type, same tempering and meltdown material.
	 * 
	 * Does not cover enchantments
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	private boolean areArmorPiecesSimilar(ARMO a1, ARMO a2) {

		if (ArmorPatcher.armorWithNoMaterialOrType.contains(a1)
				|| ArmorPatcher.armorWithNoMaterialOrType.contains(a2)) {
			return false;
		}

		ArmorMaterial am1 = this.s.getArmorMaterial(a1);
		ArmorMaterial am2 = this.s.getArmorMaterial(a2);

		if (null == am1) {
			if (!this.s.isClothing(a1) && !this.s.isJewelry(a1)) {
				SPGlobal.log("ARMOR_PATCHER", a1.getName()
						+ ": No material. Excluded from further patching.");
				ArmorPatcher.armorWithNoMaterialOrType.add(a1);
			}
			return false;
		}
		if (null == am2) {
			if (!this.s.isClothing(a2) && !this.s.isJewelry(a2)) {
				SPGlobal.log("ARMOR_PATCHER", a2.getName()
						+ ": no material Excluded from further patching.");
				ArmorPatcher.armorWithNoMaterialOrType.add(a2);
			}
			return false;
		}

		return this.doArmorPiecesHaveSameSlot(a1, a2)
				&& this.doArmorPiecesHaveSameType(a1, a2)
				&& (am1.getMaterialMeltdown().equals(am2.getMaterialMeltdown()))
				&& (am2.getMaterialTemper().equals(am1.getMaterialTemper()));
	}

	private boolean doArmorPiecesHaveSameSlot(ARMO a1, ARMO a2) {

		ArrayList<FormID> kw1 = a1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> kw2 = a2.getKeywordSet().getKeywordRefs();

		return (kw1.contains(util.Statics.kwArmorSlotBoots) && kw2
				.contains(util.Statics.kwArmorSlotBoots))
				|| (kw1.contains(util.Statics.kwArmorSlotCuirass) && kw2
						.contains(util.Statics.kwArmorSlotCuirass))
				|| (kw1.contains(util.Statics.kwArmorSlotHelmet) && kw2
						.contains(util.Statics.kwArmorSlotHelmet))
				|| (kw1.contains(util.Statics.kwArmorSlotShield) && kw2
						.contains(util.Statics.kwArmorSlotShield))
				|| (kw1.contains(util.Statics.kwArmorSlotGauntlets) && kw2
						.contains(util.Statics.kwArmorSlotGauntlets));
	}

	private boolean doArmorPiecesHaveSameType(ARMO a1, ARMO a2) {
		ArrayList<FormID> kw1 = a1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> kw2 = a2.getKeywordSet().getKeywordRefs();

		return (kw1.contains(Statics.kwArmorHeavy) && kw2
				.contains(Statics.kwArmorHeavy))
				|| (kw1.contains(Statics.kwArmorLight) && kw2
						.contains(Statics.kwArmorLight))
				|| (!kw1.contains(Statics.kwArmorLight)
						&& !kw1.contains(Statics.kwArmorHeavy)
						&& !kw2.contains(Statics.kwArmorLight) && !kw2
							.contains(Statics.kwArmorHeavy));
	}

	/**
	 * Decide whether two pieces of clothing are similar.
	 * 
	 * Similar == same slot and same price keyword (or both no price keyword)
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */

	private boolean areClothingPiecesSimilar(ARMO a1, ARMO a2) {

		return this.doClothingPiecesHaveSameSlot(a1, a2)
				&& this.doClothingPiecesHaveSimilarPriceCategory(a1, a2);
	}

	private boolean doClothingPiecesHaveSameSlot(ARMO a1, ARMO a2) {
		ArrayList<FormID> kw1 = a1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> kw2 = a2.getKeywordSet().getKeywordRefs();

		return (kw1.contains(Statics.kwClothingBody) && kw2
				.contains(Statics.kwClothingBody))
				|| (kw1.contains(Statics.kwClothingCirclet) && kw2
						.contains(Statics.kwClothingCirclet))
				|| (kw1.contains(Statics.kwClothingFeet) && kw2
						.contains(Statics.kwClothingFeet))
				|| (kw1.contains(Statics.kwClothingHands) && kw2
						.contains(Statics.kwClothingHands))
				|| (kw1.contains(Statics.kwClothingHead) && kw2
						.contains(Statics.kwClothingHead));

	}

	private boolean doClothingPiecesHaveSimilarPriceCategory(ARMO a1, ARMO a2) {
		ArrayList<FormID> kw1 = a1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> kw2 = a2.getKeywordSet().getKeywordRefs();

		return (kw1.contains(Statics.kwClothingPoor) && kw2
				.contains(Statics.kwClothingPoor))
				|| (kw1.contains(Statics.kwClothingRich) && kw2
						.contains(Statics.kwClothingRich))
				|| (!kw1.contains(Statics.kwClothingPoor)
						&& !kw1.contains(Statics.kwClothingRich)
						&& !kw2.contains(Statics.kwClothingPoor) && !kw2
							.contains(Statics.kwClothingRich));

	}

	private boolean areEnchantedClothingPiecesSimilar(ARMO a1, ARMO a2) {
		return (null != a1.getEnchantment())
				&& (null != a2.getEnchantment())
				&& this.areClothingPiecesSimilar(a1, a2)
				&& this.s.doesDirectEnchantmentBindingExist(
						a1.getEnchantment(), a2.getEnchantment());
	}

	private boolean areEnchantedJewelryPiecesSimilar(ARMO a1, ARMO a2) {
		return (null != a1.getEnchantment())
				&& (null != a2.getEnchantment())
				&& this.areJewelryPiecesSimilar(a1, a2)
				&& this.s.doesDirectEnchantmentBindingExist(
						a1.getEnchantment(), a2.getEnchantment());
	}

	/**
	 * Check if two jewelry pieces are similar.
	 * 
	 * Similar := same slot and same price category
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	private boolean areJewelryPiecesSimilar(ARMO a1, ARMO a2) {
		return this.doJewelryPiecesHaveSameSlot(a1, a2)
				&& this.doJewelryPiecesHaveSimilarPriceCategory(a1, a2);
	}

	private boolean doJewelryPiecesHaveSameSlot(ARMO a1, ARMO a2) {
		ArrayList<FormID> kw1 = a1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> kw2 = a2.getKeywordSet().getKeywordRefs();

		return (kw1.contains(Statics.kwClothingNecklace) && kw2
				.contains(Statics.kwClothingNecklace))
				|| (kw1.contains(Statics.kwClothingRing) && kw2
						.contains(Statics.kwClothingRing))
				|| (kw1.contains(Statics.kwClothingCirclet) && kw2
						.contains(Statics.kwClothingCirclet));

	}

	private boolean doJewelryPiecesHaveSimilarPriceCategory(ARMO a1, ARMO a2) {
		ArrayList<FormID> kw1 = a1.getKeywordSet().getKeywordRefs();
		ArrayList<FormID> kw2 = a2.getKeywordSet().getKeywordRefs();

		return (kw1.contains(Statics.kwJewelryExpensive) && kw2
				.contains(Statics.kwJewelryExpensive))
				|| (!kw1.contains(Statics.kwJewelryExpensive) && !kw2
						.contains(Statics.kwJewelryExpensive));
	}

	// TODO fix if ENCH lookup fails

	private ArrayList<ARMO> createEnchantedArmorVariantsByDirectEnchantmentBinding() {

		ArrayList<ARMO> ret = new ArrayList<>();

		ArrayList<String> newEnchEDIDs = new ArrayList<>();
		// ArrayList<ARMO> similarArmor = new ArrayList<>();
		FormID enchFormID;
		ARMO baseArmor;
		ENCH newEnchantment;

		for (ARMO a : this.merger.getArmors()) {

			if (ArmorPatcher.armorWithNoMaterialOrType.contains(a)) {
				// SPGlobal.log("ARMOR_PATCHER", a.getName()
				// + ": createEnchantedArmorVariants previously excluded");
				continue;
			}

			// check if enchanted
			if (a.getEnchantment() == null
					|| a.getEnchantment().equals(FormID.NULL)) {
				continue;
			}

			// check if enchantment has bindings
			if (!((newEnchEDIDs = this.s.getDirectEnchantmentBindings(a
					.getEnchantment())).size() > 0)) {
				continue;

			}

			// check if armor excluded
			if (this.s.isArmorExcludedEnchantment(a)) {
				SPGlobal.log("ARMOR_PATCHER", a.getName()
						+ ": Armor excluded from enchantment");
				continue;

			}

			// get the template armo
			if (null == (baseArmor = (ARMO) SPDatabase.getMajor(
					a.getTemplate(), GRUP_TYPE.ARMO))) {
				continue;
			}

			// check if template excluded
			if (this.s.isArmorExcludedEnchantment(baseArmor)) {
				SPGlobal.log("ARMOR_PATCHER", a.getName() + ": Armor template "
						+ baseArmor.getName() + " excluded from enchantment");
				continue;

			}

			// Get material
			if (null == (this.s.getArmorMaterial(baseArmor))
					&& !(this.s.isClothing(baseArmor))
					&& !(this.s.isJewelry(baseArmor))) {
				SPGlobal.log("ARMOR_PATCHER", a.getName()
						+ ": No clothing or jewelry and no material");
				ArmorPatcher.armorWithNoMaterialOrType.add(a);
				continue;
			}

			// get similar armor
			// if ((similarArmor = this.getSimilarArmor(baseArmor)).isEmpty()) {
			// SPGlobal.log("ARMOR_PATCHER", baseArmor.getName()
			// + ": No similar armor");
			// continue;
			// }

			// for each binding, get the related ENCH and create an armor
			for (String edid : newEnchEDIDs) {
				// first we need the ENCH FormID
				if (null == (enchFormID = Statics.getEnchFormIDFromEDID(edid,
						this.merger))) {

					SPGlobal.log("ARMOR_PATCHER", edid
							+ ": Received null FormID");
					continue;
				}

				newEnchantment = (ENCH) SPDatabase.getMajor(enchFormID,
						GRUP_TYPE.ENCH);

				// then check if a already exists with that enchantment
				if (null != this.doesArmorExistWithEnchantment(baseArmor,
						enchFormID)) {
					continue;
				}

				// if not, create it

				ret.add(this.createEnchantedArmorFromTemplate(baseArmor, a,
						newEnchantment));
			}
		}
		return ret;
	}

	private ARMO createEnchantedArmorFromTemplate(ARMO template, ARMO like,
			ENCH e) {
		ARMO newArmor = (ARMO) patch.makeCopy(
				template,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ARMOR
						+ template.getName() + e.getName() + e.getFormStr());
		newArmor.setTemplate(template.getForm());
		newArmor.setEnchantment(e.getForm());
		newArmor.setValue(like.getValue());
		newArmor.setName(this.s.getLocalizedEnchantmentNameArmor(template, e));
		// SPGlobal.log("ARMOR_PATCHER", newArmor.getName() + ": created.");
		return newArmor;
	}

	/**
	 * Get all armors similar to a. Similar armor tuples should not be
	 * enchanted.
	 * 
	 * @param a
	 * @return
	 */
	private ArrayList<ARMO> getSimilarArmor(ARMO a) {
		ArrayList<ARMO> ret = new ArrayList<>();

		if (this.s.isClothing(a)) {
			for (ARMO ae : this.merger.getArmors()) {
				if (this.s.isClothing(ae)
						&& (ae.getEnchantment() == null || ae.getEnchantment()
								.equals(FormID.NULL))
						&& this.areClothingPiecesSimilar(a, ae)) {

					if (this.s.canArmorNotBeSimilar(a, ae)) {
						SPGlobal.log("ARMOR_PATCHER", a.getName()
								+ ": clothing similar to " + ae.getName()
								+ ", but excluded with complex exclusion");
						continue;
					}

					// SPGlobal.log("ARMOR_PATCHER", a.getName()
					// + ": clothing similar to " + ae.getName());
					ret.add(ae);
				}
			}
		} else if (this.s.isJewelry(a)) {
			for (ARMO ae : this.merger.getArmors()) {
				if (this.s.isJewelry(ae)
						&& (ae.getEnchantment() == null || ae.getEnchantment()
								.equals(FormID.NULL))
						&& this.areJewelryPiecesSimilar(a, ae)) {

					if (this.s.canArmorNotBeSimilar(a, ae)) {
						SPGlobal.log("ARMOR_PATCHER", a.getName()
								+ ": jewelry similar to " + ae.getName()
								+ ", but excluded with complex exclusion");
						continue;
					}

					// SPGlobal.log("ARMOR_PATCHER", a.getName()
					// + ": jewelry similar to " + ae.getName());
					ret.add(ae);
				}
			}
		} else {
			for (ARMO ae : this.merger.getArmors()) {
				if (!this.s.isClothing(ae)
						&& !this.s.isJewelry(ae)
						&& (ae.getEnchantment() == null || ae.getEnchantment()
								.equals(FormID.NULL)) && !this.s.isJewelry(ae)
						&& this.areArmorPiecesSimilar(a, ae)) {

					if (this.s.canArmorNotBeSimilar(a, ae)) {
						SPGlobal.log("ARMOR_PATCHER", a.getName()
								+ ": armor similar to " + ae.getName()
								+ ", but excluded with complex exclusion");
						continue;
					}

					// SPGlobal.log("ARMOR_PATCHER", a.getName()
					// + ": armor similar to " + ae.getName());
					ret.add(ae);
				}
			}
		}
		return ret;
	}

	/**
	 * Checks whether an armor already exists with a given enchantment.
	 * 
	 * @param a
	 *            enchanted armor
	 * @param id
	 *            enchantment form id
	 * @return
	 */
	private ARMO doesArmorExistWithEnchantment(ARMO a, FormID id) {
		for (ARMO ae : this.merger.getArmors()) {
			if (ae.getTemplate().equals(a.getForm())
					&& ae.getEnchantment().equals(id)) {
				// SPGlobal.log("ARMOR_PATCHER", a.getName()
				// + ": exists with ENCH " + id + " as " + ae.getName()
				// + " in merger");
				return ae;
			}
		}
		for (ARMO ae : this.patch.getArmors()) {
			if (ae.getTemplate().equals(a.getForm())
					&& ae.getEnchantment().equals(id)) {
				// SPGlobal.log("ARMOR_PATCHER", a.getName()
				// + ": exists with ENCH " + id + " as " + ae.getName()
				// + " in patch");
				return ae;
			}
		}
		return null;
	}

	/**
	 * Distribute enchanted armors on leveled lists
	 * 
	 * @param enchantedArmor
	 */

	private void distributeEnchantedArmorOnLeveledListsByDirectEnchantmentBinding(
			ArrayList<ARMO> enchantedArmor) {

		ArrayList<LeveledEntry> newEntries;
		ARMO currentArmor;

		for (LVLI li : this.merger.getLeveledItems()) {

			if (this.s.isListExcludedEnchantmentArmor(li)) {
				SPGlobal.log("ARMOR_PATCHER", li.getEDID() + ": excluded.");
				continue;
			}

			if (li.get(LVLFlag.UseAll)) {
				continue;
			}

			newEntries = new ArrayList<>();

			for (LeveledEntry le : li.getEntries()) {
				currentArmor = (ARMO) SPDatabase.getMajor(le.getForm(),
						GRUP_TYPE.ARMO);

				if (currentArmor == null
						|| currentArmor.getEnchantment() == null
						|| currentArmor.getEnchantment().equals(FormID.NULL)) {
					continue;
				}

				for (ARMO a : enchantedArmor) {

					if (this.s.isClothing(a) && this.s.isClothing(currentArmor)) {
						if (this.areEnchantedClothingPiecesSimilar(
								currentArmor, a)) {
							newEntries.add(new LeveledEntry(a.getForm(), le
									.getLevel(), le.getCount()));
						}
					} else if (this.s.isJewelry(a)
							&& this.s.isJewelry(currentArmor)) {
						if (this.areEnchantedJewelryPiecesSimilar(currentArmor,
								a)) {
							newEntries.add(new LeveledEntry(a.getForm(), le
									.getLevel(), le.getCount()));
						}
					} else {
						if (this.areEnchantedArmorPiecesSimilar(currentArmor, a)) {
							newEntries.add(new LeveledEntry(a.getForm(), le
									.getLevel(), le.getCount()));
						}
					}

				}
			}

			// add new entries to list
			if (newEntries.size() > 0) {
				for (LeveledEntry e : newEntries) {
					li.addEntry(e);
				}
				this.patch.addRecord(li);
			}
		}
	}

	/**
	 * Determines whether two enchanted armor piecess are similar. Definition of
	 * similar: Same tempering material, same meltdown material, same slot and
	 * type. Enchantments should be linked via at least one binding in xml.
	 * 
	 * @param w1
	 * @param w2
	 * @return
	 */
	private boolean areEnchantedArmorPiecesSimilar(ARMO a1, ARMO a2) {
		return this.areArmorPiecesSimilar(a1, a2)
				&& this.s.doesDirectEnchantmentBindingExist(
						a1.getEnchantment(), a2.getEnchantment());
	}

	private void processDirectEnchantmentBindings() {
		ArrayList<Bucket<String, String>> directBindings = this.s
				.getDirectEnchantmentBindingsAsBucketList();

		ArrayList<Bucket<ARMO, ARMO>> enchantedArmor = this
				.generateEnchantedArmorFromBindingList(directBindings);

		this.distributeEnchantedArmorFromBuckets(enchantedArmor);

	}

	private void distributeEnchantedArmorFromBuckets(
			ArrayList<Bucket<ARMO, ARMO>> ArmorBuckets) {

		ArrayList<LeveledEntry> newEntries;
		ArrayList<ARMO> newArmor;
		ARMO currentArmor;
		ArrayList<Mod> toProcess = new ArrayList<>();
		toProcess.add(this.patch);
		toProcess.add(this.merger);

		for (Mod m : toProcess) {
			for (LVLI li : m.getLeveledItems()) {
				if (this.s.isListExcludedEnchantmentArmor(li)) {
					continue;
				}
				if (li.get(LVLFlag.UseAll)) {
					continue;
				}

				newEntries = new ArrayList<>();

				for (LeveledEntry le : li.getEntries()) {
					if (null == (currentArmor = (ARMO) SPDatabase.getMajor(
							le.getForm(), GRUP_TYPE.ARMO))) {
						continue;
					}

					if (null == (newArmor = Bucket.getBindingsFromListByKey(
							ArmorBuckets, currentArmor))) {
						continue;
					}
					// insert all new Armor
					for (ARMO a : newArmor) {
						newEntries.add(new LeveledEntry(a.getForm(), le
								.getLevel(), le.getCount()));
					}
				}

				if (newEntries.size() > 0) {
					for (LeveledEntry newLE : newEntries) {
						// SPGlobal.log("ARMOR_DEBUG", "Adding " +
						// newLE.getForm()
						// + " to " + li.getForm());
						li.addEntry(newLE);
					}
					this.patch.addRecord(li);
				}
			}
		}
	}

	/**
	 * Take buckets full of ENCH EDIDs, and create enchanted Armor
	 * 
	 * @param directBindings
	 * @return
	 */
	// TODO finish?
	private ArrayList<Bucket<ARMO, ARMO>> generateEnchantedArmorFromBindingList(
			ArrayList<Bucket<String, String>> directBindings) {

		ArrayList<Bucket<ARMO, ARMO>> ret = new ArrayList<>();
		ENCH e;
		Bucket<String, String> ENCHBucket;
		ArrayList<ARMO> variants;
		ARMO aBase;
		// saves Armor we already created
		ArrayList<Tuple<ARMO, String>> generatedArmor = new ArrayList<Tuple<ARMO, String>>();
		Tuple<ARMO, String> currentTuple;

		for (ARMO a : this.merger.getArmors()) {
			if (ArmorPatcher.armorWithNoMaterialOrType.contains(a)) {
				continue;
			}

			if (a.getEnchantment().isNull()) {
				continue;
			}

			if (this.s.isArmorExcludedEnchantment(a)) {
				SPGlobal.log("Armor_PATCHER", a.getName()
						+ ": Armor excluded from enchantment");
				continue;

			}
			// get the template Armor
			if (null == (aBase = (ARMO) SPDatabase.getMajor(a.getTemplate(),
					GRUP_TYPE.ARMO))) {

				SPGlobal.log("ARMOR_PATCHER", a.getName()
						+ ": Didn't find template");
				continue;
			}

			// template excluded?
			if (this.s.isArmorExcludedEnchantment(aBase)) {
				SPGlobal.log("Armor_PATCHER", a.getName() + ": Armor template "
						+ aBase.getName() + " excluded from enchantment");
				continue;

			}

			e = (ENCH) SPDatabase.getMajor(a.getEnchantment(), GRUP_TYPE.ENCH);

			if (null == (ENCHBucket = Bucket.getBucketWithKeyFromList(
					directBindings, e.getEDID()))) {
				continue;
			}

			variants = new ArrayList<>();
			// create new bucket with base Armor and all variants
			for (String newEnchEDID : ENCHBucket.getBindings()) {

				currentTuple = new Tuple<ARMO, String>(a, newEnchEDID);
				if (generatedArmor.contains(currentTuple)) {
					continue;
				}

				generatedArmor.add(currentTuple);

				if (null == Statics.getEnchFormIDFromEDID(newEnchEDID, merger)) {
					continue;
				}

				variants.add(this.createEnchantedArmorFromTemplate(aBase, a,
						(ENCH) SPDatabase.getMajor(
								Statics.getEnchFormIDFromEDID(newEnchEDID,
										this.merger), GRUP_TYPE.ENCH)));
			}
			ret.add(new Bucket<ARMO, ARMO>(a, variants));

		}

		return ret;

	}

	/**
	 * Create an distribute enchanted armor based on list bindings
	 */

	private void processListEnchantmentBindings() {
		ArrayList<LVLI> boundLists = new ArrayList<>();
		ArrayList<ArrayList<String>> newENCHEdids = new ArrayList<>();
		ArrayList<ArrayList<String>> baseENCHEdids = new ArrayList<>();
		ArrayList<LeveledEntry> newEntries;
		LVLI currentLVLI;
		ARMO currentArmor, currentArmorBase, newArmor;
		FormID baseEnchFormID, newEnchFormID;
		int failedLookups = 0, successfullLookups = 0;

		for (String edid : this.s.getListsOnListEnchantmentBindings()) {
			if (!(null == (currentLVLI = Statics.getLVLIFromEDID(edid,
					this.merger, this.patch)))) {
				boundLists.add(currentLVLI);
				// SPGlobal.log("ARMOR_PATCHER", edid
				// + ": Has list enchantment binding");
			}
		}

		for (LVLI l : boundLists) {

			// for each list, get the replacement instructions
			if (0 == (newENCHEdids = this.s
					.getNewENCHEdidsOnListEnchantmentBinding(l)).size()) {
				SPGlobal.log(
						"ARMOR_PATCHER",
						l.getEDID()
								+ ": Has list enchantment binding, but newENCHEdids has length 0");
				continue;
			}

			if (0 == (baseENCHEdids = this.s
					.getBaseENCHEdidsOnListEnchantmentBinding(l)).size()) {
				SPGlobal.log(
						"ARMOR_PATCHER",
						l.getEDID()
								+ ": Has list enchantment binding, but baseENCHEdids has length 0");
				continue;
			}

			// SPGlobal.log("ARMOR_PATCHER",
			// "newENCHEdids: " + newENCHEdids.toString());
			// SPGlobal.log("ARMOR_PATCHER",
			// "baseENCHEdids: " + baseENCHEdids.toString());

			if (newENCHEdids.size() != baseENCHEdids.size()) {

				SPGlobal.log("ARMOR_PATCHER", "Size not equal; aborting.");
				continue;
			}

			// if configured to do so, gather similar armor to each piece on l,
			// and expand l with it
			if (this.s.shouldFillUpListOnListEnchantmentBinding(l)) {

				// SPGlobal.log("ARMOR_PATCHER", l.getEDID()
				// + ": Filling up with similars");

				ArrayList<ARMO> similarArmor;
				newEntries = new ArrayList<>();

				for (LeveledEntry le : l.getEntries()) {

					currentArmor = (ARMO) SPDatabase.getMajor(le.getForm(),
							GRUP_TYPE.ARMO);

					if (currentArmor == null
							|| currentArmor.getEnchantment() == null
							|| currentArmor.getEnchantment()
									.equals(FormID.NULL)
							|| this.s.isArmorExcludedEnchantment(currentArmor)) {
						continue;
					}

					if (null == (currentArmorBase = (ARMO) SPDatabase.getMajor(
							currentArmor.getTemplate(), GRUP_TYPE.ARMO))) {
						continue;
					}

					if (Bucket.doesBucketListContainKey(similarArmorBuckets,
							currentArmorBase)) {
						similarArmor = Bucket.getBindingsFromListByKey(
								similarArmorBuckets, currentArmorBase);
						successfullLookups++;
					} else {
						failedLookups++;
						if (0 == (similarArmor = this
								.getSimilarArmor(currentArmorBase)).size()) {
							armorWithoutSimilars.add(currentArmorBase);
							continue;
						}

						similarArmorBuckets.add(new Bucket<ARMO, ARMO>(
								currentArmorBase, similarArmor));
					}

					for (ARMO a : similarArmor) {
						if (null == this.doesArmorExistWithEnchantment(a,
								currentArmor.getEnchantment())) {

							// SPGlobal.log("DEBUG", a.getEDID() + " "
							// + currentArmor.getEnchantment()
							// + ": Will be created");
							newArmor = this.createEnchantedArmorFromTemplate(a,
									currentArmor, (ENCH) SPDatabase.getMajor(
											currentArmor.getEnchantment(),
											GRUP_TYPE.ENCH));

							newEntries.add(new LeveledEntry(newArmor.getForm(),
									le.getLevel(), le.getCount()));

							// SPGlobal.log("ARMOR_PATCHER", newArmor.getName()
							// + ": adding to list " + l.getEDID());
							;
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

			// now get started creating the copy

			newEntries = new ArrayList<>();

			for (ArrayList<String> baseENCHEdidSublists : baseENCHEdids) {

				int index0 = baseENCHEdids.indexOf(baseENCHEdidSublists);
				if (index0 < 0) {
					SPGlobal.log("ARMOR_PATCHER", baseENCHEdids.toString()
							+ ": failed to recieve index of "
							+ baseENCHEdidSublists.toString());
					continue;
				}

				for (LeveledEntry le : l.getEntries()) {
					currentArmor = (ARMO) SPDatabase.getMajor(le.getForm(),
							GRUP_TYPE.ARMO);

					// is armor, and is enchanted?
					if (currentArmor == null
							|| currentArmor.getEnchantment() == null
							|| currentArmor.getEnchantment()
									.equals(FormID.NULL)) {
						continue;
					}

					// has it valid template?

					if (null == currentArmor.getTemplate()
							|| FormID.NULL.equals(currentArmor.getTemplate())) {
						continue;
					}

					if (null == (currentArmorBase = (ARMO) SPDatabase.getMajor(
							currentArmor.getTemplate(), GRUP_TYPE.ARMO))) {
						continue;
					}

					// search for fitting old ENCH
					newEnchFormID = null;
					baseEnchFormID = null;

					int index1;

					for (String baseENCH : baseENCHEdidSublists) {
						if (null == (baseEnchFormID = Statics
								.getEnchFormIDFromEDID(baseENCH, this.merger))) {
							SPGlobal.log(
									"ARMOR_PATCHER",
									l.getEDID()
											+ baseENCH
											+ ": Has list enchantment binding, but failed to retrieve base ENCH from EDID");
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

						if (baseEnchFormID
								.equals(currentArmor.getEnchantment())) {
							if (null == (newEnchFormID = Statics
									.getEnchFormIDFromEDID(
											newENCHEdids.get(index0)
													.get(index1), this.merger))) {
								SPGlobal.log(
										"ARMOR_PATCHER",
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

					if (null == baseEnchFormID || null == newEnchFormID) {
						continue;
					}

					// create new ARMO with new ENCH , if it doesn't exist
					if (null == (newArmor = this.doesArmorExistWithEnchantment(
							currentArmorBase, newEnchFormID))) {

						newArmor = this.createEnchantedArmorFromTemplate(
								currentArmorBase, currentArmor,
								(ENCH) SPDatabase.getMajor(newEnchFormID,
										GRUP_TYPE.ENCH));
					}

					newEntries.add(new LeveledEntry(newArmor.getForm(), le
							.getLevel(), le.getCount()));

					// SPGlobal.log("ARMOR_PATCHER", newArmor.getName()
					// + ": adding to list " + l.getEDID());
					;
				}

				if (newEntries.size() <= 0) {
					continue;
				}

				// copy LVLI

				currentLVLI = (LVLI) patch.makeCopy(l, Statics.S_PREFIX_PATCHER
						+ Statics.S_PREFIX_LVLI + Statics.S_PREFIX_ARMOR
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

							// SPGlobal.log("ARMOR_PATCHER", l.getEDID()
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

							// SPGlobal.log("ARMOR_PATCHER", l.getEDID()
							// + ": adding to list " + li.getEDID());
							;
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

		SPGlobal.log("ARMOR_PATCHER",
				"processListEnchantmentBindings(): Successfull lookups: "
						+ successfullLookups + ", failed lookups: "
						+ failedLookups);
	}

	public String getInfo() {
		return "Armor stats, variants, keywords...";
	}
}
