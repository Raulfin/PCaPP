package patcher;

import skyproc.ENCH;
import skyproc.GRUP_TYPE;
import skyproc.MGEF;
import skyproc.MagicEffectRef;
import skyproc.Mod;
import skyproc.SPDatabase;
import skyproc.SPEL;
import skyproc.SPGlobal;
import skyproc.genenums.ActorValue;
import skyproc.genenums.CastType;
import xml.access.XmlStorage;

/**
 * Removes spell school assignments from constant-effect type enchantments and
 * spells to prevent bugs that originate from spell fortification effects
 * 
 * @author T3nd0
 * 
 */
final class SpellPatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	public SpellPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		// first spells
		if (this.s.useMage()) {
			try {
				for (SPEL s : this.merger.getSpells()) {
					this.disableAssociatedMagicSchools(s);
				}
			} catch (Exception e) {
				System.out.println("ERROR in Spell Patcher: " + e.toString());
			}
		}

		// then enchantments
		if (this.s.useMage()) {
			try {
				for (ENCH e : this.merger.getEnchantments()) {
					this.disableAssociatedMagicSchools(e);
				}
			} catch (Exception e) {
				System.out.println("ERROR in Spell Patcher: " + e.toString());
			}
		}
	}

	private void disableAssociatedMagicSchools(SPEL s) {

		if (!(s.getType().equals(SPEL.SPELType.Ability) || s.getCastType()
				.equals(CastType.ConstantEffect))) {
			return;
		}

		int numEffects = s.getMagicEffects().size();

		for (int i = 0; i < numEffects; i++) {

			MGEF m = (MGEF) SPDatabase.getMajor(s.getMagicEffects().get(i)
					.getMagicRef(), GRUP_TYPE.MGEF);

			if (!m.getSkillType().equals(ActorValue.UNKNOWN)) {
				m.setSkillType(ActorValue.UNKNOWN);
				this.patch.addRecord(m);
				SPGlobal.log("LOG", m.getName()
						+ ": Removed magic school assignment");
			}
		}
	}

	private void disableAssociatedMagicSchools(ENCH e) {

		if (!(e.getType().equals(SPEL.SPELType.Ability) || e.getCastType()
				.equals(CastType.ConstantEffect))) {
			return;
		}

		int numEffects = e.getMagicEffects().size();

		for (int i = 0; i < numEffects; i++) {

			MGEF m = (MGEF) SPDatabase.getMajor(e.getMagicEffects().get(i)
					.getMagicRef(), GRUP_TYPE.MGEF);

			if (!m.getSkillType().equals(ActorValue.UNKNOWN)) {
				m.setSkillType(ActorValue.UNKNOWN);
				this.patch.addRecord(m);
				SPGlobal.log("LOG", m.getName()
						+ ": Removed magic school assignment");
			}
		}
	}

	/**
	 * Gets a spell's assigned spell school
	 * 
	 * @param s
	 * @return
	 */
	public static ActorValue getSchool(SPEL s) {
		ActorValue av = null;

		for (MagicEffectRef mer : s.getMagicEffects()) {
			av = ((MGEF) SPDatabase.getMajor(mer.getMagicRef(), GRUP_TYPE.MGEF))
					.getSkillType();
			if (null == av) {
				continue;
			}
			if (av.equals(ActorValue.Alteration)
					|| av.equals(ActorValue.Conjuration)
					|| av.equals(ActorValue.Destruction)
					|| av.equals(ActorValue.Illusion)
					|| av.equals(ActorValue.Restoration)) {
				return av;
			}
		}

		return null;
	}

	/**
	 * Determine whether a spell has AoE
	 * 
	 * @param s
	 * @return
	 */
	public static boolean doesSpellHaveAoEEffect(SPEL s) {

		int numEffects = s.getMagicEffects().size();

		for (int i = 0; i < numEffects; i++) {
			if (0 != s.getMagicEffects().get(i).getAreaOfEffect()) {
				return true;
			}
		}

		return false;
	}

	public String getInfo() {
		return "Disabling spell schools on certain spells and enchantments...";
	}
}
