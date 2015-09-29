package patcher;

import java.util.ArrayList;

import enums.SpellSkillTiers;
import skyproc.BOOK;
import skyproc.FLST;
import skyproc.LVLI;
import skyproc.LeveledEntry;
import skyproc.SCRL;
import skyproc.COBJ;
import skyproc.Condition;
import skyproc.Condition.Operator;
import skyproc.Condition.RunOnType;
import skyproc.ENCH;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.MGEF;
import skyproc.MagicEffectRef;
import skyproc.Mod;
import skyproc.SPDatabase;
import skyproc.SPEL;
import skyproc.SPGlobal;
import skyproc.WEAP;
import skyproc.genenums.ActorValue;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;
import util.Statics;
import xml.access.XmlStorage;

/**
 * Iterates over books to find fitting spells. Creates staves and scrolls for
 * these spells.
 * 
 * @author T3nd0
 * 
 */

// disable vanilla staves from leveled list(?), distribute on
// leveled lists (?), add ench xp and keyword to scrolls

final class BookPatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	private FLST formListTwoHandedSpells = (FLST) SPDatabase.getMajor(
			Statics.formListTwoHandedSpells, GRUP_TYPE.FLST);
	private FLST formListAoEDestructionSpells = (FLST) SPDatabase.getMajor(
			Statics.formListAoEDestructionSpells, GRUP_TYPE.FLST);
	private FLST formListSpellsDestruction = (FLST) SPDatabase.getMajor(
			Statics.formListSpellsDestruction, GRUP_TYPE.FLST);
	private FLST formListSpellsAlteration = (FLST) SPDatabase.getMajor(
			Statics.formListSpellsAlteration, GRUP_TYPE.FLST);
	private FLST formListSpellsConjuration = (FLST) SPDatabase.getMajor(
			Statics.formListSpellsConjuration, GRUP_TYPE.FLST);
	private FLST formListSpellsIllusion = (FLST) SPDatabase.getMajor(
			Statics.formListSpellsIllusion, GRUP_TYPE.FLST);
	private FLST formListSpellsRestoration = (FLST) SPDatabase.getMajor(
			Statics.formListSpellsRestoration, GRUP_TYPE.FLST);
	private FLST formListSpellsConcentration = (FLST) SPDatabase.getMajor(
			Statics.formListConcentrationSpells, GRUP_TYPE.FLST);
	private FLST formListSpellbinderExcludedSpells = (FLST) SPDatabase
			.getMajor(Statics.formListSpellbinderExcludedSpells, GRUP_TYPE.FLST);

	public BookPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		SPEL sp = null;
		WEAP st = null;
		SCRL sc = null;
		COBJ o = null;

		boolean createStaff = true, createScroll = true, distribute = true;

		for (BOOK b : this.merger.getBooks()) {

			try {
				if (null == (sp = (SPEL) SPDatabase.getMajor(
						b.getTeachesSpell(), GRUP_TYPE.SPEL))) {
					continue;
				}

				SPGlobal.log("BOOK_PATCHER", b.getName() + ": Teaches spell "
						+ sp.getName());

				createStaff = !(this.s.isSpellExcludedStaff(b) || this.s
						.isSpellExcludedStaff(sp));
				createScroll = !(this.s.isSpellExcludedScroll(b) || this.s
						.isSpellExcludedScroll(sp));
				distribute = !(this.s.isSpellExcludedDistribution(b) || this.s
						.isSpellExcludedDistribution(sp));

				// staff creation

				if (createStaff) {

					if (null == (st = this.generateStaff(sp, sp.getName()))) {
						SPGlobal.log("BOOK_PATCHER", sp.getName()
								+ ": No staff generated");

					} else if (null == (o = this.generateStaffCraftingRecipe(
							st, sp, b))) {
						SPGlobal.log("BOOK_PATCHER", sp.getName()
								+ ": No staff crafting recipe generated");
					} else {
						this.patch.addRecord(o);
						SPGlobal.log("BOOK_PATCHER", st.getName()
								+ ": Crafting recipe generated");
					}

				}

				// scroll creation

				if (createScroll) {
					if (null == (sc = this.generateScroll(sp))) {
						SPGlobal.log("BOOK_PATCHER", sp.getName()
								+ ": No scroll generated");
					} else if (null == (o = this.generateScrollCraftingRecipe(
							sp, sc))) {
						SPGlobal.log("BOOK_PATCHER", sp.getName()
								+ ": No scroll crafting recipe generated");
					} else {

						this.patch.addRecord(o);
						SPGlobal.log("BOOK_PATCHER", sc.getName()
								+ ": Crafting recipe generated");
						this.patch.addRecord(this.generateBookCraftingRecipe(
								sc, b, sp));
						SPGlobal.log("BOOK_PATCHER", b.getName()
								+ ": Crafting recipe generated");
					}
				}

				// list distribution

				if (distribute) {
					// this.distributeBookOnFormLists(b, sp);
					this.distributeBookOnLeveledLists(b, sp);
				}

				this.addSpellToFormLists(sp);

				distribute = true;
				createStaff = true;
				createScroll = true;
			} catch (Exception e) {
				SPGlobal.log("ERROR in Armor Patcher: " + e.toString());
				distribute = true;
				createStaff = true;
				createScroll = true;
			}
		}

		this.patch.addRecord(this.formListAoEDestructionSpells);
		this.patch.addRecord(this.formListSpellsAlteration);
		this.patch.addRecord(this.formListSpellsConjuration);
		this.patch.addRecord(this.formListSpellsDestruction);
		this.patch.addRecord(this.formListSpellsIllusion);
		this.patch.addRecord(this.formListSpellsRestoration);
		this.patch.addRecord(this.formListTwoHandedSpells);
	}

	/**
	 * Add books to form lists that contain similar books
	 * 
	 * @param b
	 * @param s_b
	 */
	@SuppressWarnings("unused")
	private void distributeBookOnFormLists(BOOK b, SPEL s_b) {

		SPEL s = null;
		boolean add = false;

		for (FLST f : this.merger.getFormLists()) {

			// already in?
			if (f.getFormIDEntries().contains(b.getForm())) {
				SPGlobal.log("BOOK_PATCHER", b.getName()
						+ ": Already found in form list " + f.getEDID());
				continue;
			}

			for (FormID id : f.getFormIDEntries()) {

				// is there a book?
				if (null == (BOOK) SPDatabase.getMajor(id, GRUP_TYPE.BOOK)) {
					continue;
				}
				// is the book a spell book?
				if (null == (s = (SPEL) SPDatabase.getMajor(
						b.getTeachesSpell(), GRUP_TYPE.SPEL))) {
					continue;
				}
				// is the spell similar to our original book's spell?
				if (this.areSpellsSimilar(s, s_b)) {
					add = true;
					break;
				}
			}

			if (add) {
				add = false;
				f.addFormEntry(s_b.getForm());
				patch.addRecord(f);
			}

			s = null;
		}
	}

	/**
	 * Add books to leveled lists that contain similar books
	 */

	private void distributeBookOnLeveledLists(BOOK b, SPEL s) {

		SPEL ls = null, firstSimilarMatch = null;
		BOOK lb = null;
		boolean similarSet = false, added = false, alreadyOnLists = false;

		SPGlobal.log("BOOK_PATCHER",
				s.getName() + ": Started adding book" + b.getName()
						+ "to leveled lists");

		ArrayList<LeveledEntry> newEntries = new ArrayList<LeveledEntry>();

		for (LVLI i : this.merger.getLeveledItems()) {

			// excluded?
			if (this.s.isListExcludedBook(i)) {
				continue;
			}

			// already in?
			if (i.getEntryForms().contains(b.getForm())) {
				alreadyOnLists = true;
				continue;
			}

			for (LeveledEntry li : i.getEntries()) {

				// is li a book?
				if (null == (lb = (BOOK) SPDatabase.getMajor(li.getForm(),
						GRUP_TYPE.BOOK))) {
					continue;
				}

				// is the book a spell book?
				if (null == (ls = (SPEL) SPDatabase.getMajor(
						lb.getTeachesSpell(), GRUP_TYPE.SPEL))) {
					continue;
				}

				if (!similarSet) {
					// similar?
					if (!(this.areSpellsSimilar(s, ls))) {
						continue;
					}
					// first similar match - use in next iterations
					similarSet = true;
					firstSimilarMatch = ls;
				} else {
					// after similar match is set, check for equality
					if (!ls.equals(firstSimilarMatch)) {
						continue;
					}
				}

				SPGlobal.log("BOOK_PATCHER", b.getName()
						+ ": Should add to leveled list " + i.getEDID());

				newEntries.add(new LeveledEntry(b.getForm(), li.getLevel(), li
						.getCount()));

			}

			// add entries if matches were found

			if (newEntries.size() > 0) {
				added = true;
				for (LeveledEntry le : newEntries) {
					i.addEntry(le);
				}

				SPGlobal.log("BOOK_PATCHER",
						b.getName() + ": Added " + newEntries.size()
								+ " entries to leveled list " + i.getEDID());

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
			SPGlobal.log("BOOK_PATCHER", s.getName() + ": Book" + b.getName()
					+ " not added anywhere. Better check it out.");
		} else if (!added && alreadyOnLists) {
			SPGlobal.log("BOOK_PATCHER", s.getName() + ": Book" + b.getName()
					+ " not added anywhere, but already on at least one list.");
		}

	}

	/**
	 * Determines whether spells are similar, based on spell school and skill
	 * level
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean areSpellsSimilar(SPEL s1, SPEL s2) {

		// same AV?

		if (null == s1 || null == s2) {
			SPGlobal.log("BOOK_PATCHER",
					"areSpellsSimilar: Called with null shouldn't happen.");
			return false;
		}

		ActorValue a1 = SpellPatcher.getSchool(s1);
		ActorValue a2 = SpellPatcher.getSchool(s2);

		if (null == a1 || null == a2 || !(a1.equals(a2))) {
			return false;
		}

		// same skill levels?

		if (!(this.getSpellTier(s1).equals(this.getSpellTier(s2)))) {
			return false;
		}

		SPGlobal.log("BOOK_PATCHER",
				s1.getName() + ": Similar to " + s2.getName());

		return true;
	}

	/**
	 * Generate an ENCH to be placed on a staff
	 * 
	 * @param s
	 * @return
	 */
	private ENCH generateStaffEnchantment(SPEL s) {

		if (s.getCastType().equals(CastType.ConstantEffect)) {
			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Has unsupported cast type");
			return null;
		}

		if (s.getDeliveryType().equals(DeliveryType.Self)) {
			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Has unsupported delivery type");
			return null;
		}

		if (s.getEquipSlot().equals(Statics.equipSlotBothHands)) {
			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Has unsupported equip slot");
			return null;
		}

		if (null == (SpellPatcher.getSchool(s))) {
			SPGlobal.log("BOOK_PATCHER", s.getName() + ": Has no spell school");
			return null;
		}

		SPGlobal.log("BOOK_PATCHER", s.getName() + ": Started creating ENCH");

		ENCH newEnch = (ENCH) patch.makeCopy(
				(ENCH) SPDatabase.getMajor(Statics.enchStaffEmpty,
						GRUP_TYPE.ENCH),
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_ENCHANTMENT
						+ s.getName() + s.getFormStr());

		SPGlobal.log("BOOK_PATCHER", s.getName() + ": ENCH created");

		newEnch.setDeliveryType(s.getDeliveryType());
		newEnch.setCastType(s.getCastType());
		newEnch.setName(Statics.S_PREFIX_ENCHANTMENT + s.getName());

		newEnch.setBaseCost(Math.min(100, Math.max(s.getBaseCost(), 50)));

		SPGlobal.log("BOOK_PATCHER", s.getName() + ": About to copy effects");

		int numEffects = s.getMagicEffects().size();

		newEnch.clearMagicEffects();

		for (int i = 0; i < numEffects; i++) {
			newEnch.addMagicEffect(s.getMagicEffects().get(i));
		}

		for (int i = 0; i < numEffects; i++) {
			newEnch.getMagicEffects()
					.get(i)
					.setAreaOfEffect(
							s.getMagicEffects().get(i).getAreaOfEffect());
			newEnch.getMagicEffects().get(i)
					.setDuration(s.getMagicEffects().get(i).getDuration());
			newEnch.getMagicEffects().get(i)
					.setMagnitude(s.getMagicEffects().get(i).getMagnitude());
		}

		// for (int i = 0; i < numEffects; i++) {
		//
		// ArrayList<Condition> alce = newEnch.getMagicEffects().get(i)
		// .getConditions();
		// ArrayList<Condition> alcs = s.getMagicEffects().get(i)
		// .getConditions();
		//
		// for (Condition c : alcs) {
		// }
		// }
		//
		// SPGlobal.log("BOOK_PATCHER", s.getName() +
		// ": Stopped copying conditions");

		return newEnch;
	}

	private WEAP generateStaff(SPEL s, String name) {

		ENCH staffEnch = this.generateStaffEnchantment(s);
		if (staffEnch == null) {
			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Failed to create staff ENCH");
			return null;
		}

		// determine school
		ActorValue av = SpellPatcher.getSchool(s);

		if (av == null) {
			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Couldn't determine spell school ");
			return null;
		}

		SPGlobal.log("BOOK_PATCHER", s.getName() + ": Spell school " + av);

		WEAP newStaff = null;

		if (av.equals(ActorValue.Destruction)) {
			newStaff = (WEAP) patch.makeCopy(
					SPDatabase.getMajor(Statics.emptyStaffDestruction,
							GRUP_TYPE.WEAP),
					Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_STAFF
							+ s.getName() + s.getFormStr());
		} else if (av.equals(ActorValue.Alteration)) {
			newStaff = (WEAP) patch.makeCopy(
					SPDatabase.getMajor(Statics.emptyStaffAlteration,
							GRUP_TYPE.WEAP),
					Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_STAFF
							+ s.getName() + s.getFormStr());
		} else if (av.equals(ActorValue.Conjuration)) {
			newStaff = (WEAP) patch.makeCopy(
					SPDatabase.getMajor(Statics.emptyStaffConjuration,
							GRUP_TYPE.WEAP),
					Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_STAFF
							+ s.getName() + s.getFormStr());
		} else if (av.equals(ActorValue.Illusion)) {
			newStaff = (WEAP) patch.makeCopy(
					SPDatabase.getMajor(Statics.emptyStaffIllusion,
							GRUP_TYPE.WEAP),
					Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_STAFF
							+ s.getName() + s.getFormStr());
		} else if (av.equals(ActorValue.Restoration)) {
			newStaff = (WEAP) patch.makeCopy(
					SPDatabase.getMajor(Statics.emptyStaffRestoration,
							GRUP_TYPE.WEAP),
					Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_STAFF
							+ s.getName() + s.getFormStr());
		}

		if (newStaff == null) {

			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Couldn't create staff");

			return null;
		}

		newStaff.setEnchantment(staffEnch.getForm());
		newStaff.setEnchantmentCharge(2500);
		newStaff.setName(this.s.getOutputString(Statics.S_STAFF) + " [" + name
				+ "]");
		return newStaff;
	}

	/**
	 * Creates crafting recipe for a spell staff
	 * 
	 * @param w
	 * @param s
	 * @param b
	 * @return
	 */

	private COBJ generateStaffCraftingRecipe(WEAP w, SPEL s, BOOK b) {

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_CRAFTING + Statics.S_PREFIX_STAFF
				+ s.getName());

		newRecipe.setBenchKeywordFormID(Statics.kwCraftingStaff);
		newRecipe.setResultFormID(w.getForm());
		newRecipe.setOutputQuantity(1);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk,
				Statics.perkEnchantingStafffaire);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);
		c1.setRunOnType(RunOnType.Subject);

		Condition c2 = new Condition(Condition.P_FormID.HasSpell, s.getForm());
		c2.setOperator(Operator.EqualTo);
		c2.setValue(1.0f);
		c2.setRunOnType(RunOnType.Subject);

		newRecipe.getConditions().add(c1);
		newRecipe.getConditions().add(c2);

		ActorValue av = SpellPatcher.getSchool(s);

		if (av.equals(ActorValue.Alteration)) {
			newRecipe.addIngredient(Statics.emptyStaffAlteration, 1);
		} else if (av.equals(ActorValue.Conjuration)) {
			newRecipe.addIngredient(Statics.emptyStaffConjuration, 1);
		} else if (av.equals(ActorValue.Destruction)) {
			newRecipe.addIngredient(Statics.emptyStaffDestruction, 1);
		} else if (av.equals(ActorValue.Illusion)) {
			newRecipe.addIngredient(Statics.emptyStaffIllusion, 1);
		} else if (av.equals(ActorValue.Restoration)) {
			newRecipe.addIngredient(Statics.emptyStaffRestoration, 1);
		}

		newRecipe.addIngredient(b.getForm(), 1);

		return newRecipe;
	}

	/**
	 * Determine the skill tier of a spell
	 * 
	 * @param s
	 * @return
	 */
	private SpellSkillTiers getSpellTier(SPEL s) {
		MGEF m = null;
		int maxSkillLevel = 0;
		int currSkillLevel = 0;

		for (MagicEffectRef mer : s.getMagicEffects()) {
			m = (MGEF) SPDatabase.getMajor(mer.getMagicRef(), GRUP_TYPE.MGEF);
			currSkillLevel = m.getSkillLevel();
			if (currSkillLevel > maxSkillLevel) {
				maxSkillLevel = currSkillLevel;
			}
		}

		return SpellSkillTiers.getTierFromLevel(maxSkillLevel);
	}

	/**
	 * Determines the perk needed to craft a scroll
	 * 
	 * @param s
	 * @return
	 */

	private FormID getRequiredScrollCraftingPerk(SPEL s) {

		SpellSkillTiers t = this.getSpellTier(s);

		if (t.equals(SpellSkillTiers.NOVICE)
				|| t.equals(SpellSkillTiers.APPRENTICE)) {
			return Statics.perkEnchantingBasicScripture;
		} else if (t.equals(SpellSkillTiers.ADEPT)) {
			return Statics.perkEnchantingAdvancedScripture;
		} else if (t.equals(SpellSkillTiers.EXPERT)) {
			return Statics.perkEnchantingElaborateScripture;
		} else if (t.equals(SpellSkillTiers.MASTER)) {
			return Statics.perkEnchantingSagesScripture;
		}

		SPGlobal.log("BOOK_PATCHER", s.getName()
				+ ": Couldn't determine scroll craftig perk");
		return null;
	}

	/**
	 * Generate scroll from a spell
	 * 
	 * @param s
	 * @return
	 */
	private SCRL generateScroll(SPEL s) {

		// concentration spells don't work as scrolls
		if (s.getCastType().equals(CastType.Concentration)) {
			SPGlobal.log("BOOK_PATCHER", s.getName()
					+ ": Concentration spell - no scroll creation");
			return null;
		}

		SPGlobal.log("BOOK_PATCHER", s.getName() + ": Started creating scroll");

		SCRL newScroll = (SCRL) patch
				.makeCopy(
						(SCRL) SPDatabase.getMajor(Statics.emptyScroll,
								GRUP_TYPE.SCRL),
						Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_SCROLL
								+ s.getName() + s.getFormStr());

		newScroll.setCastDuration(s.getCastDuration());
		newScroll.setCastType(s.getCastType());
		newScroll.setChargeTime(s.getChargeTime());
		newScroll.setDeliveryType(s.getDeliveryType());
		newScroll.setEquipmentType(s.getEquipSlot());
		newScroll.setSpellType(s.getSpellType());
		newScroll.setName(s.getName() + " ["
				+ this.s.getOutputString(Statics.S_SCROLL) + "]");

		int numEffects = s.getMagicEffects().size();

		for (int i = 0; i < numEffects; i++) {
			MGEF currentEffect = (MGEF) (SPDatabase.getMajor(s
					.getMagicEffects().get(i).getMagicRef()));
			MGEF newScrollEffect = (MGEF) patch.makeCopy(
					currentEffect,
					Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_SCROLL
							+ Statics.S_PREFIX_MAGICEFFECT
							+ currentEffect.getFormStr());
			newScrollEffect.getKeywordSet().getKeywordRefs()
					.add(Statics.kwScrollSpell);
			newScroll.addMagicEffect(newScrollEffect);
			newScroll
					.getMagicEffects()
					.get(i)
					.setAreaOfEffect(
							s.getMagicEffects().get(i).getAreaOfEffect());
			newScroll.getMagicEffects().get(i)
					.setDuration(s.getMagicEffects().get(i).getDuration());
			newScroll.getMagicEffects().get(i)
					.setMagnitude(s.getMagicEffects().get(i).getMagnitude());
		}

		SPGlobal.log("BOOK_PATCHER", s.getName() + ": Done creating scroll");

		return newScroll;

	}

	/**
	 * Create a book crafting recipe for a given book
	 * 
	 * @param s
	 * @param b
	 * @return
	 */

	private COBJ generateBookCraftingRecipe(SCRL s, BOOK b, SPEL sp) {

		FormID requiredPerk = this.getRequiredScrollCraftingPerk(sp);

		if (requiredPerk == null) {
			return null;
		}

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_CRAFTING + Statics.S_PREFIX_BOOK
				+ b.getName());

		newRecipe.setBenchKeywordFormID(Statics.kwCraftingScroll);
		newRecipe.setResultFormID(b.getForm());
		newRecipe.setOutputQuantity(1);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk, requiredPerk);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);
		c1.setRunOnType(RunOnType.Subject);

		Condition c2 = new Condition(Condition.P_FormID.GetItemCount,
				s.getForm());
		c2.setOperator(Operator.GreaterThan);
		c2.setValue(1.0f);
		c2.setRunOnType(RunOnType.Subject);

		newRecipe.addCondition(c1);
		newRecipe.addCondition(c2);

		newRecipe.addIngredient(Statics.inkwell, 2);
		newRecipe.addIngredient(Statics.paperroll, 2);
		newRecipe.addIngredient(s.getForm(), 3);

		return newRecipe;

	}

	/**
	 * Create a new crafting recipe for a scroll
	 * 
	 * @param sp
	 * @param sc
	 * @return
	 */
	private COBJ generateScrollCraftingRecipe(SPEL sp, SCRL sc) {

		FormID requiredPerk = this.getRequiredScrollCraftingPerk(sp);

		if (requiredPerk == null) {
			return null;
		}

		COBJ newRecipe = new COBJ(Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_CRAFTING + Statics.S_PREFIX_SCROLL
				+ sc.getName());

		newRecipe.setBenchKeywordFormID(Statics.kwCraftingScroll);
		newRecipe.setResultFormID(sc.getForm());
		newRecipe.setOutputQuantity(1);

		Condition c1 = new Condition(Condition.P_FormID.HasPerk, requiredPerk);
		c1.setOperator(Operator.EqualTo);
		c1.setValue(1.0f);
		c1.setRunOnType(RunOnType.Subject);

		Condition c2 = new Condition(Condition.P_FormID.HasSpell, sp.getForm());
		c2.setOperator(Operator.EqualTo);
		c2.setValue(1.0f);
		c2.setRunOnType(RunOnType.Subject);

		newRecipe.addCondition(c1);
		newRecipe.addCondition(c2);

		newRecipe.addIngredient(Statics.inkwell, 1);
		newRecipe.addIngredient(Statics.paperroll, 1);

		return newRecipe;
	}

	private void addSpellToFormLists(SPEL s) {

		if (s.getEquipSlot().equals(Statics.equipSlotBothHands)) {
			formListTwoHandedSpells.addFormEntry(s.getForm());
			SPGlobal.log("SPELL_PATCHER", s.getName()
					+ ": Added to Studies: Master Alteration FLST");
			this.patch.addRecord(formListTwoHandedSpells);
		}

		if (s.getCastType().equals(CastType.Concentration)) {
			this.formListSpellbinderExcludedSpells.addFormEntry(s.getForm());
			this.formListSpellsConcentration.addFormEntry(s.getForm());
		}

		ActorValue av = SpellPatcher.getSchool(s);

		if (null == av) {
			return;
		} else if (av.equals(ActorValue.Alteration)) {
			this.formListSpellsAlteration.addFormEntry(s.getForm());
		} else if (av.equals(ActorValue.Conjuration)) {
			this.formListSpellsConjuration.addFormEntry(s.getForm());
		} else if (av.equals(ActorValue.Destruction)) {
			this.formListSpellsDestruction.addFormEntry(s.getForm());
			if (SpellPatcher.doesSpellHaveAoEEffect(s)) {
				formListAoEDestructionSpells.addFormEntry(s.getForm());
			}
		} else if (av.equals(ActorValue.Illusion)) {
			this.formListSpellsIllusion.addFormEntry(s.getForm());
		} else if (av.equals(ActorValue.Restoration)) {
			this.formListSpellsRestoration.addFormEntry(s.getForm());
		}
	}

	public String getInfo() {
		return "Scroll and staff creation...";
	}
}
