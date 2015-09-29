package xml.access;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import skyproc.ALCH;
import skyproc.AMMO;
import skyproc.ARMO;
import skyproc.BOOK;
import skyproc.COBJ;
import skyproc.ENCH;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.INGR;
import skyproc.LVLI;
import skyproc.MGEF;
import skyproc.MajorRecord;
import skyproc.NPC_;
import skyproc.RACE;
import skyproc.SPDatabase;
import skyproc.SPEL;
import skyproc.SPGlobal;
import skyproc.WEAP;
import util.Bucket;
import util.Statics;
import xml.lowLevel.alchemy.AlchemyEffect;
import xml.lowLevel.alchemy.IngredientVariation;
import xml.lowLevel.alchemy.PotionMultiplier;
import xml.lowLevel.ammunition.AmmunitionMaterial;
import xml.lowLevel.ammunition.AmmunitionModifier;
import xml.lowLevel.ammunition.AmmunitionType;
import xml.lowLevel.armor.ArmorMasqueradeBinding;
import xml.lowLevel.armor.ArmorMaterial;
import xml.lowLevel.armor.ArmorModifier;
import xml.lowLevel.common.Bindable;
import xml.lowLevel.common.Binding;
import xml.lowLevel.common.ComplexExclusion;
import xml.lowLevel.common.Exclusion;
import xml.lowLevel.enchanting.DirectEnchantmentBinding;
import xml.lowLevel.enchanting.EnchantmentNameBinding;
import xml.lowLevel.enchanting.EnchantmentReplacer;
import xml.lowLevel.enchanting.ListEnchantmentBinding;
import xml.lowLevel.language.Language;
import xml.lowLevel.language.StringBinding;
import xml.lowLevel.weapon.WeaponMaterial;
import xml.lowLevel.weapon.WeaponModifier;
import xml.lowLevel.weapon.WeaponOverride;
import xml.lowLevel.weapon.WeaponType;
import xml.topLevel.Alchemy;
import xml.topLevel.Ammunition;
import xml.topLevel.Armor;
import xml.topLevel.Enchanting;
import xml.topLevel.GeneralSettings;
import xml.topLevel.Languages;
import xml.topLevel.LeveledLists;
import xml.topLevel.NPC;
import xml.topLevel.Weapons;
import enums.BaseWeaponTypes;
import enums.EnchantmentNameTypes;
import enums.ExclusionTargets;
import enums.ExclusionTypes;

/**
 * Singleton class that offers methods to access the parsed XML files
 * indirectly. The XML should only be accessed through this class.
 * 
 */

public class XmlStorage {
	private GeneralSettings generalSettings;
	private Weapons weapons;
	private Armor armor;
	private Languages languages;
	private Enchanting enchanting;
	private Alchemy alchemy;
	private Ammunition ammunition;
	private NPC npc;
	private LeveledLists leveledLists;

	private static XmlStorage storage = null;

	private XmlStorage() {
		try {
			this.weapons = XmlImportExport.readWeaponsXML();
			this.armor = XmlImportExport.readArmorXML();
			this.generalSettings = XmlImportExport.readGeneralSettingsXML();
			this.languages = XmlImportExport.readLanguagesXML();
			this.enchanting = XmlImportExport.readEnchantingXML();
			this.alchemy = XmlImportExport.readAlchemyXML();
			this.ammunition = XmlImportExport.readAmmunitionXML();
			this.npc = XmlImportExport.readNPCXML();
			this.leveledLists = XmlImportExport.readLeveledListsXML();
		} catch (FileNotFoundException e) {
			SPGlobal.log("XML_STORAGE", e.toString());

		} catch (JAXBException e) {
			SPGlobal.log("XML_STORAGE", e.toString());
		}
	}

	public static XmlStorage getStorage() {
		if (null == storage) {
			storage = new XmlStorage();
		}

		if (storage.weapons == null || storage.armor == null
				|| storage.generalSettings == null || storage.languages == null
				|| storage.enchanting == null || storage.alchemy == null
				|| storage.ammunition == null || storage.npc == null
				|| storage.leveledLists == null) {
			return null;
		}

		return storage;
	}

	public ArmorMaterial getArmorMaterial(ARMO a) {
		return (ArmorMaterial) this.querySingleBindingInBindables(a.getName(),
				this.armor.getArmor_material_bindings().getBinding(),
				this.armor.getArmor_materials().getArmor_material());
	}

	public double getArmorSlotMultiplier(ARMO a) {

		ArrayList<FormID> keywords = a.getKeywordSet().getKeywordRefs();

		if (keywords.contains(util.Statics.kwArmorSlotBoots)) {
			return this.armor.getArmor_settings().getArmorFactorFeet();
		} else if (keywords.contains(util.Statics.kwArmorSlotCuirass)) {
			return this.armor.getArmor_settings().getArmorFactorBody();
		} else if (keywords.contains(util.Statics.kwArmorSlotHelmet)) {
			return this.armor.getArmor_settings().getArmorFactorHead();
		} else if (keywords.contains(util.Statics.kwArmorSlotGauntlets)) {
			return this.armor.getArmor_settings().getArmorFactorHands();
		} else if (keywords.contains(util.Statics.kwArmorSlotShield)) {
			return this.armor.getArmor_settings().getArmorFactorShield();
		}

		SPGlobal.log("XML_STORAGE", a.getName() + ": no armor slot keyword");

		return -1;
	}

	public WeaponMaterial getWeaponMaterial(WEAP w) {
		return (WeaponMaterial) this.querySingleBindingInBindables(w.getName(),
				this.weapons.getWeapon_material_bindings().getBinding(),
				this.weapons.getWeapon_materials().getWeapon_material());
	}

	public WeaponType getWeaponType(WEAP w) {
		return (WeaponType) this.querySingleBindingInBindables(w.getName(),
				this.weapons.getWeapon_type_bindings().getBinding(),
				this.weapons.getWeapon_types().getWeapon_type());
	}

	/**
	 * Get weapon skill damage multiplier from base weapon type
	 * 
	 * @param wt
	 * @return
	 */
	public double getWeaponSkillDamageMultiplier(BaseWeaponTypes wt) {

		if (wt.getRelatedWeaponSchool().equals(
				Statics.kwWeaponSchoolHeavyWeaponry)) {
			return this.weapons.getWeapon_settings()
					.getDamageFactorHeavyWeaponry();
		}

		if (wt.getRelatedWeaponSchool().equals(
				Statics.kwWeaponSchoolLightWeaponry)) {
			return this.weapons.getWeapon_settings()
					.getDamageFactorLightWeaponry();
		}

		if (wt.getRelatedWeaponSchool().equals(
				Statics.kwWeaponSchoolRangedWeaponry)) {
			return this.weapons.getWeapon_settings()
					.getDamageFactorRangedWeaponry();
		}

		SPGlobal.log("XML_STORAGE", wt.toString()
				+ ": No weapon skill damage multiplier");
		return -1;
	}

	public double getWeaponSkillDamageBase(BaseWeaponTypes wt) {

		if (wt.getRelatedWeaponSchool().equals(
				Statics.kwWeaponSchoolHeavyWeaponry)) {
			return this.weapons.getWeapon_settings()
					.getBaseDamageHeavyWeaponry();
		}

		if (wt.getRelatedWeaponSchool().equals(
				Statics.kwWeaponSchoolLightWeaponry)) {
			return this.weapons.getWeapon_settings()
					.getBaseDamageLightWeaponry();
		}

		if (wt.getRelatedWeaponSchool().equals(
				Statics.kwWeaponSchoolRangedWeaponry)) {
			return this.weapons.getWeapon_settings()
					.getBaseDamageRangedWeaponry();
		}

		SPGlobal.log("XML_STORAGE", wt.toString()
				+ ": No weapon skill damage multiplier");
		return -1;
	}

	public boolean useWarrior() {
		return this.generalSettings.isUseWarrior();
	}

	public boolean useMage() {
		return this.generalSettings.isUseMage();
	}

	public boolean useThief() {
		return this.generalSettings.isUseThief();
	}

	public boolean appendWeaponTypeToName() {
		return this.weapons.getWeapon_settings().isAppendTypeToName();
	}

	public WeaponOverride getWeaponOverride(WEAP w) {
		String name = w.getName();

		for (WeaponOverride wo : this.weapons.getWeapon_overrides()
				.getWeapon_override()) {
			if (wo.getFullName().equals(name)) {
				return wo;
			}
		}

		return null;
	}

	public String getOutputString(String searchString) {

		String preferredLanguage = this.generalSettings.getOutputLanguage();
		String defaultLanguage = this.languages.getDefault_language();

		// try preferred language

		for (Language l : this.languages.getLanguage()) {
			if (l.getLanguageID().equals(preferredLanguage)) {
				for (StringBinding sb : l.getStringBinding()) {
					if (sb.getIdentifier().equals(searchString)) {
						return sb.getOutputString();
					}
				}
			}
		}

		// try default language
		if (!(defaultLanguage.equals(preferredLanguage))) {
			for (Language l : this.languages.getLanguage()) {
				if (l.getLanguageID().equals(defaultLanguage)) {
					for (StringBinding sb : l.getStringBinding()) {
						if (sb.getIdentifier().equals(searchString)) {
							SPGlobal.log("XML_STORAGE", searchString
									+ ": Found in default language");
							return sb.getOutputString();
						}
					}
				}
			}
		}

		// nothing found
		SPGlobal.log("XML_STORAGE", searchString
				+ ": Not found in language definitions");
		return null;
	}

	public boolean shouldAppendWeaponType() {
		return this.weapons.getWeapon_settings().isAppendTypeToName();
	}

	public int getArmorMeltdownOutput(ARMO a) {

		if (a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwArmorSlotBoots)
				|| a.getKeywordSet().getKeywordRefs()
						.contains(Statics.kwClothingFeet)) {
			return this.armor.getArmor_settings().getMeltdownOutputFeet();
		} else if (a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwArmorSlotHelmet)
				|| a.getKeywordSet().getKeywordRefs()
						.contains(Statics.kwClothingHead)) {
			return this.armor.getArmor_settings().getMeltdownOutputHead();
		}
		if (a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwArmorSlotGauntlets)
				|| a.getKeywordSet().getKeywordRefs()
						.contains(Statics.kwClothingHands)) {
			return this.armor.getArmor_settings().getMeltdownOutputHands();
		}
		if (a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwArmorSlotCuirass)
				|| a.getKeywordSet().getKeywordRefs()
						.contains(Statics.kwClothingBody)) {
			return this.armor.getArmor_settings().getMeltdownOutputBody();
		}
		if (a.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwArmorSlotShield)) {
			return this.armor.getArmor_settings().getMeltdownOutputShield();
		}
		return 0;
	}

	public double getArmorRatingPerDR() {
		return this.armor.getArmor_settings().getProtectionPerArmor();
	}

	public double getmaxProtection() {
		return this.armor.getArmor_settings().getMaxProtection();
	}
	public double getfArmorRatingPCMax() {
		return this.armor.getArmor_settings().getArmorRatingPCMax();
	}
	public double getfArmorRatingMax() {
		return this.armor.getArmor_settings().getArmorRatingMax();
	}

	/**
	 * Checks whether a book's spell should be excluded from patching (staff
	 * creation).
	 * 
	 * @param b
	 * @return
	 */
	public boolean isSpellExcludedStaff(SPEL s) {

		// shouldn't happen, but better safe than sorry
		if (null == s) {
			SPGlobal.log("XML_STORAGE", "isSpellExcludedStaff called with null");
		}

		for (Exclusion e : this.enchanting.getStaff_crafting_exclusions()
				.getExclusion()) {
			if (this.checkExclusionSPEL(e, s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a book's spell should be excluded from patching (staff
	 * creation).
	 * 
	 * @param b
	 * @return
	 */
	public boolean isSpellExcludedStaff(BOOK b) {
		for (Exclusion e : this.enchanting.getStaff_crafting_exclusions()
				.getExclusion()) {
			if (this.checkExclusionBOOK(e, b)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a book's spell should be excluded from patching (scroll
	 * creation).
	 * 
	 * @param b
	 * @return
	 */
	public boolean isSpellExcludedScroll(SPEL s) {
		for (Exclusion e : this.enchanting.getScroll_crafting_exclusions()
				.getExclusion()) {
			if (this.checkExclusionSPEL(e, s)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks whether a book's spell should be excluded from patching (scroll
	 * creation). Uses CONTAINS on book name and exclusion
	 * 
	 * @param b
	 * @return
	 */
	public boolean isSpellExcludedScroll(BOOK b) {
		for (Exclusion e : this.enchanting.getScroll_crafting_exclusions()
				.getExclusion()) {
			if (this.checkExclusionBOOK(e, b)) {
				return true;
			}
		}
		return false;
	}

	public boolean shouldRemoveUnspecificSpells() {
		return this.generalSettings.isRemoveUnspecificStartingSpells();
	}

	public AmmunitionType getAmmunitionType(AMMO p) {
		return (AmmunitionType) this.querySingleBindingInBindables(p.getName(),
				this.ammunition.getAmmunition_type_bindings().getBinding(),
				this.ammunition.getAmmunition_types().getAmmunition_type());
	}

	public AmmunitionMaterial getAmmunitionMaterial(AMMO p) {

		return (AmmunitionMaterial) this.querySingleBindingInBindables(p
				.getName(), this.ammunition.getAmmunition_material_bindings()
				.getBinding(), this.ammunition.getAmmunition_materials()
				.getAmmunition_material());
	}

	/**
	 * Unified operation to query a set of bindings for a best match, and then
	 * look up the match in a set of bindables
	 * 
	 * @param toMatch
	 * @param bindings
	 * @param bindables
	 * @return
	 */
	private Bindable querySingleBindingInBindables(String toMatch,
			ArrayList<Binding> bindings, ArrayList<? extends Bindable> bindables) {

		String bestHit = this.getBestBindingMatch(toMatch, bindings);

		if (null == bestHit) {
			return null;
		}

		return this.getBindableFromIdentifier(bestHit, bindables);
	}

	/**
	 * Unified operation to query a set of bindings for all matches, and then
	 * look up the matches in a set of bindables. All bindables that got
	 * successfully looked up are returned
	 * 
	 * @param toMatch
	 * @param bindings
	 * @param bindables
	 * @return
	 */
	private ArrayList<? extends Bindable> queryAllBindingsInBindables(
			String toMatch, ArrayList<Binding> bindings,
			ArrayList<? extends Bindable> bindables) {

		ArrayList<String> hits = this.getAllBindingMatches(toMatch, bindings);
		Bindable b;

		if (hits.size() == 0) {
			return null;
		}

		ArrayList<Bindable> ret = new ArrayList<>();

		for (String s : hits) {
			if ((b = this.getBindableFromIdentifier(s, bindables)) != null) {
				ret.add(b);
			}
		}

		return ret;
	}

	/**
	 * Query a list of bindings for a string, and return the best (longest)
	 * match, based on "contains" operation
	 * 
	 * @param toMatch
	 * @param bindings
	 * @return
	 */
	private String getBestBindingMatch(String toMatch,
			ArrayList<Binding> bindings) {
		int maxHitSize = 0;
		int currHitSize = 0;
		String bestHit = null;
		String currHit = null;

		if (toMatch == null) {
			SPGlobal.log("XML_STORAGE", "getBestBindingMatch: null input");
			return null;
		}

		for (Binding b : bindings) {
			if (toMatch.contains(b.getSubstring())) {
				currHit = b.getIdentifier();
				currHitSize = b.getSubstring().length();

				if (currHitSize > maxHitSize) {
					maxHitSize = currHitSize;
					bestHit = currHit;
				}
			}
		}

		return bestHit;
	}

	/**
	 * Get all matching bindings for a given String, based on "contains"
	 * operation
	 * 
	 * @param toMatch
	 * @param bindings
	 * @return
	 */
	private ArrayList<String> getAllBindingMatches(String toMatch,
			ArrayList<Binding> bindings) {

		ArrayList<String> ret = new ArrayList<>();

		if (toMatch == null) {
			SPGlobal.log("XML_STORAGE", "getAllBindingMatches: null input");
			return ret;
		}

		for (Binding b : bindings) {
			if (toMatch.contains(b.getSubstring())) {
				ret.add(b.getIdentifier());
			}
		}

		return ret;
	}

	private Bindable getBindableFromIdentifier(String identifier,
			ArrayList<? extends Bindable> list) {
		for (Bindable b : list) {
			if (b.getIdentifier().equals(identifier)) {
				return b;
			}
		}

		return null;
	}

	/**
	 * Get all modifiers fitting a specific ammunition
	 * 
	 * @param a
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AmmunitionModifier> getAmmunitionModifiers(AMMO p) {
		return (ArrayList<AmmunitionModifier>) this
				.queryAllBindingsInBindables(p.getName(), this.ammunition
						.getAmmunition_modifier_bindings().getBinding(),
						this.ammunition.getAmmunition_modifiers()
								.getAmmunition_modifier());
	}

	/**
	 * Get all modifiers fitting a specific armor
	 * 
	 * @param a
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArmorModifier> getArmorModifiers(ARMO a) {
		return (ArrayList<ArmorModifier>) this.queryAllBindingsInBindables(a
				.getName(), this.armor.getArmor_modifier_bindings()
				.getBinding(), this.armor.getArmor_modifiers()
				.getArmor_modifier());
	}

	/**
	 * Get all modifiers fitting a specific weapon
	 * 
	 * @param w
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<WeaponModifier> getWeaponModifiers(WEAP w) {
		return (ArrayList<WeaponModifier>) this.queryAllBindingsInBindables(w
				.getName(), this.weapons.getWeapon_modifier_bindings()
				.getBinding(), this.weapons.getWeapon_modifiers()
				.getWeapon_modifier());
	}

	public AlchemyEffect getAlchemyEffect(MGEF e) {

		return (AlchemyEffect) this.querySingleBindingInBindables(e.getName(),
				this.alchemy.getAlchemy_effect_bindings().getBinding(),
				this.alchemy.getAlchemy_effects().getAlchemy_effect());
	}

	public IngredientVariation getIngredientVariation(INGR i) {
		return (IngredientVariation) this.querySingleBindingInBindables(i
				.getName(), this.alchemy.getIngredient_variation_bindings()
				.getBinding(), this.alchemy.getIngredient_variations()
				.getIngredient_variation());
	}

	public PotionMultiplier getPotionMultiplier(ALCH a) {
		return (PotionMultiplier) this.querySingleBindingInBindables(a
				.getName(), this.alchemy.getPotion_multiplier_bindings()
				.getBinding(), this.alchemy.getPotion_multipliers()
				.getPotion_multiplier());
	}

	public boolean isClothing(ARMO a) {
		for (FormID kw : Statics.keywordListClothingKeywords) {
			if (a.getKeywordSet().getKeywordRefs().contains(kw)) {
				return true;
			}
		}
		return false;
	}

	public boolean isJewelry(ARMO a) {
		for (FormID kw : Statics.keywordListJewelryKeywords) {
			if (a.getKeywordSet().getKeywordRefs().contains(kw)) {
				return true;
			}
		}
		return false;
	}

	public boolean shouldDisableStaffRecipe(COBJ o) {
		WEAP result = ((WEAP) SPDatabase.getMajor(o.getResultFormID(),
				GRUP_TYPE.WEAP));

		if (null == result) {
			return false;
		}

		for (Exclusion e : this.enchanting
				.getStaff_crafting_disable_crafting_exclusions().getExclusion()) {
			if (this.checkExclusionWEAP(e, result)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNPCExcluded(NPC_ n) {
		for (Exclusion e : this.npc.getNpc_exclusions().getExclusion()) {
			if (this.checkExclusionNPC_(e, n)) {
				return true;
			}
		}
		return false;
	}

	public boolean isRaceExcluded(RACE r) {
		for (Exclusion e : this.npc.getRace_exclusions().getExclusion()) {
			if (this.checkExclusionRACE(e, r)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<FormID> getArmorMasqueradeKeywords(ARMO a) {

		ArrayList<FormID> kws = new ArrayList<FormID>();

		for (ArmorMasqueradeBinding amb : this.armor
				.getArmor_masquerade_bindings().getArmor_masquerade_binding()) {
			if (a.getName().contains(amb.getSubstringArmor())) {
				kws.add(amb.getMasqueradeFaction().getFactionKeyword());
			}
		}

		return kws;
	}

	/**
	 * Checks (equals) whether a spell tome should not be distributed, based on
	 * its spell
	 * 
	 * @param s
	 * @return
	 */
	public boolean isSpellExcludedDistribution(SPEL s) {
		for (Exclusion e : this.leveledLists
				.getDistribution_exclusions_spell_tome()
				.getDistribution_exclusions_spell().getExclusion()) {
			if (this.checkExclusionSPEL(e, s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks (contains) whether a spell tome should not be distributed, based
	 * on the book
	 * 
	 * @param b
	 * @return
	 */
	public boolean isSpellExcludedDistribution(BOOK b) {

		for (Exclusion e : this.leveledLists
				.getDistribution_exclusions_spell_tome()
				.getDistribution_exclusions_book().getExclusion()) {
			if (this.checkExclusionBOOK(e, b)) {
				return true;
			}
		}

		return false;
	}

	public boolean isAlchExcluded(ALCH a) {

		for (Exclusion e : this.alchemy.getPotion_exclusions().getExclusion()) {
			if (this.checkExclusionALCH(e, a)) {
				return true;
			}
		}

		return false;
	}
	
	public boolean isIngrExcluded(INGR a) {

		for (Exclusion e : this.alchemy.getIngredient_exclusions().getExclusion()) {
			if (this.checkExclusionINGR(e, a)) {
				return true;
			}
		}

		return false;
	}

	public boolean isWeaponExcludedReforged(WEAP w) {

		for (Exclusion e : this.weapons.getReforge_exclusions().getExclusion()) {
			if (this.checkExclusionWEAP(e, w)) {
				return true;
			}
		}

		return false;
	}

	public boolean isArmorExcludedReforged(ARMO a) {

		for (Exclusion e : this.armor.getReforge_exclusions().getExclusion()) {
			if (this.checkExclusionARMO(e, a)) {
				return true;
			}
		}

		return false;
	}

	public boolean canArmorNotBeSimilar(ARMO a1, ARMO a2) {
		for (ComplexExclusion ce : this.enchanting
				.getSimilarity_exclusions_armor().getComplex_exclusion()) {
			if (this.checkComplexExclusionARMO(ce, a1, a2)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWeaponsNotBeSimilar(WEAP w1, WEAP w2) {
		for (ComplexExclusion ce : this.enchanting
				.getSimilarity_exclusions_weapon().getComplex_exclusion()) {
			if (this.checkComplexExclusionWEAP(ce, w1, w2)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkComplexExclusionWEAP(ComplexExclusion ce, WEAP w1,
			WEAP w2) {
		return (checkExclusionWEAP(ce.getExclusion_0(), w1) && checkExclusionWEAP(
				ce.getExclusion_1(), w2))
				|| (checkExclusionWEAP(ce.getExclusion_0(), w2) && checkExclusionWEAP(
						ce.getExclusion_1(), w1));
	}

	private boolean checkComplexExclusionARMO(ComplexExclusion ce, ARMO a1,
			ARMO a2) {
		return (checkExclusionARMO(ce.getExclusion_0(), a1) && checkExclusionARMO(
				ce.getExclusion_1(), a2))
				|| (checkExclusionARMO(ce.getExclusion_0(), a2) && checkExclusionARMO(
						ce.getExclusion_1(), a1));
	}

	private boolean checkExclusionAMMO(Exclusion e, AMMO a) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, a.getName());
		} else {
			return this.checkExclusionMajorRecord(e, a);
		}
	}

	private boolean checkExclusionALCH(Exclusion e, ALCH a) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, a.getName());
		} else {
			return this.checkExclusionMajorRecord(e, a);
		}
	}
	
	private boolean checkExclusionINGR(Exclusion e, INGR i) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, i.getName());
		} else {
			return this.checkExclusionMajorRecord(e, i);
		}
	}

	private boolean checkExclusionNPC_(Exclusion e, NPC_ n) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, n.getName());
		} else {
			return this.checkExclusionMajorRecord(e, n);
		}
	}

	private boolean checkExclusionRACE(Exclusion e, RACE r) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, r.getName());
		} else {
			return this.checkExclusionMajorRecord(e, r);
		}
	}

	private boolean checkExclusionSPEL(Exclusion e, SPEL s) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, s.getName());
		} else {
			return this.checkExclusionMajorRecord(e, s);
		}
	}

	private boolean checkExclusionBOOK(Exclusion e, BOOK b) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, b.getName());
		} else {
			return this.checkExclusionMajorRecord(e, b);
		}
	}

	private boolean checkExclusionWEAP(Exclusion e, WEAP w) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, w.getName());
		} else {
			return this.checkExclusionMajorRecord(e, w);
		}
	}

	private boolean checkExclusionARMO(Exclusion e, ARMO a) {
		if (e.getTarget().equals(ExclusionTargets.NAME)) {
			return this.checkExclusionName(e, a.getName());
		} else {
			return this.checkExclusionMajorRecord(e, a);
		}
	}

	private boolean checkExclusionName(Exclusion e, String name) {

		if (null == name) {
			SPGlobal.log("XML_STORAGE",
					"checkExclusionName: Encountered null name");
			return true;
		}

		if (e.getType().equals(ExclusionTypes.CONTAINS)) {
			return name.contains(e.getText());
		} else if (e.getType().equals(ExclusionTypes.EQUALS)) {
			return name.equals(e.getText());
		} else if (e.getType().equals(ExclusionTypes.EQUALS_IGNORECASE)) {
			return name.equalsIgnoreCase(e.getText());
		} else if (e.getType().equals(ExclusionTypes.STARTSWITH)) {
			return name.indexOf(e.getText()) == 0;
		} else {
			throw new IllegalArgumentException("Exclusion has invalid type: "
					+ e.getType());
		}
	}

	private boolean checkExclusionMajorRecord(Exclusion e, MajorRecord m) {

		String toCheck;

		if (e.getTarget().equals(ExclusionTargets.EDID)) {
			toCheck = m.getEDID();
		} else if (e.getTarget().equals(ExclusionTargets.FORMID)) {
			toCheck = m.getFormStr();
		} else {
			throw new IllegalArgumentException("Exclusion has invalid target: "
					+ e.getTarget());
		}

		if (e.getType().equals(ExclusionTypes.CONTAINS)) {
			return toCheck.contains(e.getText());
		} else if (e.getType().equals(ExclusionTypes.EQUALS)) {
			return toCheck.equals(e.getText());
		} else if (e.getType().equals(ExclusionTypes.EQUALS_IGNORECASE)) {
			return toCheck.equalsIgnoreCase(e.getText());
		} else if (e.getType().equals(ExclusionTypes.STARTSWITH)) {
			return toCheck.indexOf(e.getText()) == 0;
		} else {
			throw new IllegalArgumentException("Exclusion has invalid type: "
					+ e.getType());
		}
	}

	/**
	 * Checks (equals) whether a weapon should not be distributed
	 * 
	 * @param b
	 * @return
	 */

	public boolean isWeaponExcludedDistribution(WEAP w) {
		for (Exclusion e : this.leveledLists
				.getDistribution_exclusions_weapon()
				.getDistribution_exclusions_weapon_regular().getExclusion()) {
			if (this.checkExclusionWEAP(e, w)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether a list should be ignored when distributing spell tomes
	 * 
	 * @param l
	 * @return
	 */
	public boolean isListExcludedBook(LVLI l) {
		for (Exclusion e : this.leveledLists
				.getDistribution_exclusions_spell_tome()
				.getDistribution_exclusions_list().getExclusion()) {
			if (this.checkExclusionMajorRecord(e, l)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check whether a list should be ignored when distributing weapons
	 * 
	 * @param l
	 * @return
	 */
	public boolean isListExcludedWeaponRegular(LVLI l) {
		for (Exclusion e : this.leveledLists
				.getDistribution_exclusions_weapon()
				.getDistribution_exclusions_list_regular().getExclusion()) {
			if (this.checkExclusionMajorRecord(e, l)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get all direct bindings for a given ENCH as FormID
	 * 
	 * @param e
	 *            FormID
	 * @return
	 */
	public ArrayList<String> getDirectEnchantmentBindings(FormID e) {
		ArrayList<String> ret = new ArrayList<>();
		MajorRecord oldEnch = SPDatabase.getMajor(e, GRUP_TYPE.ENCH);
		if (null == oldEnch) {
			SPGlobal.log("XML_STORAGE", e + ": null MajorRecord.");
			return ret;
		}
		String myEDID = oldEnch.getEDID();

		if (null == myEDID) {
			SPGlobal.log("XML_STORAGE", e + ": null EDID.");
			return ret;
		}

		for (DirectEnchantmentBinding eb : this.enchanting
				.getDirect_enchantment_bindings()
				.getDirect_enchantment_binding()) {
			if (eb.getEdidEnchantmentBase().equalsIgnoreCase(myEDID)) {
				ret.add(eb.getEdidEnchantmentNew());
				SPGlobal.log("XML_STORAGE", eb.getEdidEnchantmentBase()
						+ ": direct binding with " + eb.getEdidEnchantmentNew());
			}
		}

		return ret;
	}

	public boolean isWeaponExcludedEnchantment(WEAP w) {
		for (Exclusion e : this.enchanting.getEnchantment_weapon_exclusions()
				.getExclusion()) {
			if (this.checkExclusionWEAP(e, w)) {
				return true;
			}
		}
		return false;
	}

	public boolean isArmorExcludedEnchantment(ARMO a) {
		for (Exclusion e : this.enchanting.getEnchantment_armor_exclusions()
				.getExclusion()) {
			if (this.checkExclusionARMO(e, a)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether two enchantments, defined by their FormIDs, are linked via
	 * at least one direct binding.
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */

	public boolean doesDirectEnchantmentBindingExist(FormID f1, FormID f2) {

		ENCH e1 = (ENCH) SPDatabase.getMajor(f1, GRUP_TYPE.ENCH);
		ENCH e2 = (ENCH) SPDatabase.getMajor(f2, GRUP_TYPE.ENCH);
		if (null == e1 || null == e2) {
			return false;
		}
		String edid1 = e1.getEDID();
		String edid2 = e2.getEDID();
		if (null == edid1 || null == edid2) {
			return false;
		}

		for (DirectEnchantmentBinding eb : this.enchanting
				.getDirect_enchantment_bindings()
				.getDirect_enchantment_binding()) {
			if ((eb.getEdidEnchantmentBase().equalsIgnoreCase(edid1) && eb
					.getEdidEnchantmentNew().equalsIgnoreCase(edid2))
					|| eb.getEdidEnchantmentNew().equalsIgnoreCase(edid1)
					&& eb.getEdidEnchantmentBase().equalsIgnoreCase(edid2)) {
				return true;
			}
		}

		return false;
	}

	public boolean isListExcludedEnchantmentArmor(LVLI l) {
		for (Exclusion e : this.leveledLists.getDistribution_exclusions_armor()
				.getDistribution_exclusions_list_enchanted().getExclusion()) {
			if (this.checkExclusionMajorRecord(e, l)) {
				return true;
			}
		}
		return false;
	}

	public boolean isListExcludedEnchantmentWeapon(LVLI l) {
		for (Exclusion e : this.leveledLists
				.getDistribution_exclusions_weapon()
				.getDistribution_exclusions_list_enchanted().getExclusion()) {
			if (this.checkExclusionMajorRecord(e, l)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tries to retrieve an adjusted name for the weapon with enchantment. If
	 * none is available, use the enchantment's name
	 * 
	 * @param e
	 * @return
	 */
	public String getLocalizedEnchantmentNameWeapon(WEAP w, ENCH e) {
		
		if (e == null || e.equals(FormID.NULL)){
			SPGlobal.log("STATICS", "getLocalizedEnchantmentNameWeapon() called with null");
			return null;
		}

		for (EnchantmentNameBinding enb : this.enchanting
				.getEnchantment_name_bindings().getEnchantment_name_binding()) {
			if (enb.getEdidEnchantment().equals(e.getEDID())) {

				if (enb.getType().equals(EnchantmentNameTypes.PREFIX)) {
					return this.getOutputString(enb.getName()) + " "
							+ w.getName();
				} else {
					return w.getName()
							+ " "
							+ this.getOutputString(Statics.S_ENCHANTMENT_DELIMITER)
							+ " " + this.getOutputString(enb.getName());
				}

			}
		}

		return w.getName() + " "
				+ this.getOutputString(Statics.S_ENCHANTMENT_DELIMITER) + " "
				+ e.getName();
	}

	public String getLocalizedEnchantmentNameArmor(ARMO a, ENCH e) {

		for (EnchantmentNameBinding enb : this.enchanting
				.getEnchantment_name_bindings().getEnchantment_name_binding()) {
			if (enb.getEdidEnchantment().equals(e.getEDID())) {

				if (enb.getType().equals(EnchantmentNameTypes.PREFIX)) {
					return this.getOutputString(enb.getName()) + " "
							+ a.getName();
				} else {
					return a.getName()
							+ " "
							+ this.getOutputString(Statics.S_ENCHANTMENT_DELIMITER)
							+ " " + this.getOutputString(enb.getName());
				}

			}
		}

		return a.getName() + " "
				+ this.getOutputString(Statics.S_ENCHANTMENT_DELIMITER) + " "
				+ e.getName();
	}

	public ArrayList<String> getListsOnListEnchantmentBindings() {
		ArrayList<String> ret = new ArrayList<>();
		for (ListEnchantmentBinding leb : this.enchanting
				.getList_enchantment_bindings().getList_enchantment_binding()) {
			if (!ret.contains(leb.getEdidList())) {
				ret.add(leb.getEdidList());
			}
		}
		return ret;
	}

	public ArrayList<ArrayList<String>> getBaseENCHEdidsOnListEnchantmentBinding(
			LVLI l) {
		ArrayList<ArrayList<String>> ret = new ArrayList<>();

		for (ListEnchantmentBinding leb : this.enchanting
				.getList_enchantment_bindings().getList_enchantment_binding()) {
			if (leb.getEdidList().equals(l.getEDID())) {

				ArrayList<String> temp = new ArrayList<String>();

				for (EnchantmentReplacer er : leb.getEnchantment_replacers()
						.getEnchantment_replacer()) {
					temp.add(er.getEdidEnchantmentBase());
				}

				ret.add(temp);
			}
		}

		return ret;
	}

	public ArrayList<ArrayList<String>> getNewENCHEdidsOnListEnchantmentBinding(
			LVLI l) {
		ArrayList<ArrayList<String>> ret = new ArrayList<>();

		for (ListEnchantmentBinding leb : this.enchanting
				.getList_enchantment_bindings().getList_enchantment_binding()) {
			if (leb.getEdidList().equals(l.getEDID())) {

				ArrayList<String> temp = new ArrayList<String>();

				for (EnchantmentReplacer er : leb.getEnchantment_replacers()
						.getEnchantment_replacer()) {
					temp.add(er.getEdidEnchantmentNew());
				}

				ret.add(temp);
			}
		}

		return ret;
	}

	public boolean shouldFillUpListOnListEnchantmentBinding(LVLI l) {
		for (ListEnchantmentBinding leb : this.enchanting
				.getList_enchantment_bindings().getList_enchantment_binding()) {
			if (leb.getEdidList().equals(l.getEDID())) {
				return leb.isFillListWithSimilars();
			}
		}
		return false;
	}
	
	public ArrayList<Bucket<String,String>> getDirectEnchantmentBindingsAsBucketList(){
		ArrayList<Bucket<String,String>> dbs = new ArrayList<>();
		Bucket<String,String> currBucket;
		
		for(DirectEnchantmentBinding db : this.enchanting.getDirect_enchantment_bindings().getDirect_enchantment_binding()){
			if(null == (currBucket = Bucket.getBucketWithKeyFromList(dbs, db.getEdidEnchantmentBase()))){
				Bucket<String,String> b = new Bucket<String,String>(db.getEdidEnchantmentBase(),new ArrayList<String>());
				b.getBindings().add(db.getEdidEnchantmentNew());
				dbs.add(b);
			}else{
				currBucket.getBindings().add(db.getEdidEnchantmentNew());
			}
		}
		return dbs;
	}
}
