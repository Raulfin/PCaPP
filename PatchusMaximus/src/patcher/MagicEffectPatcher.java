package patcher;

import skyproc.Condition;
import skyproc.Condition.CondFlag;
import skyproc.MGEF;
import skyproc.Mod;
import skyproc.SPGlobal;
import skyproc.ScriptRef;
import skyproc.Condition.Operator;
import skyproc.Condition.RunOnType;
import skyproc.MGEF.SpellEffectFlag;
import util.Statics;
import xml.access.XmlStorage;

//TODO add xml exclusion

public class MagicEffectPatcher implements Patcher {

	// private XmlStorage s;
	private Mod merger, patch;

	public MagicEffectPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {
		for (MGEF m : merger.getMagicEffects()) {
			try {
				if (this.markDisarm(m) | this.markShout(m)) {
					patch.addRecord(m);
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in MagicEffect Patcher: "
						+ e.toString());
			}
		}
	}

	private boolean markShout(MGEF m) {

		if (!(m.getKeywordSet().getKeywordRefs()
				.contains(Statics.kwShoutEffect))) {
			return false;
		}

		// 0.0: absorb, 5:dual value modifier, 34.0: value modfier;
		if ((m.getEffectType() == 34.0f || m.getEffectType() == 0.0f || m
				.getEffectType() == 5.0f) && m.get(SpellEffectFlag.Detrimental)) {
			m.getKeywordSet().addKeywordRef(Statics.kwShoutHarmful);
			SPGlobal.log("MAGIC_EFFECT_PATCHER", m.getName()
					+ ": Marked as harmful shout");
			// 18: summon creature
		} else if (m.getEffectType() == 18.0f) {
			m.getKeywordSet().addKeywordRef(Statics.kwShoutSummoning);
			SPGlobal.log("MAGIC_EFFECT_PATCHER", m.getName()
					+ ": Marked as summoning shout");
		} else {
			m.getKeywordSet().addKeywordRef(Statics.kwShoutNonHarmful);
			SPGlobal.log("MAGIC_EFFECT_PATCHER", m.getName()
					+ ": Marked as non harmful shout");
		}

		m.getScriptPackage().addScript(Statics.S_SCRIPT_SHOUTEXP);
		ScriptRef s = m.getScriptPackage().getScript(Statics.S_SCRIPT_SHOUTEXP);
		s.setProperty(Statics.S_SCRIPT_SHOUTEXP_PROPERTY_0,
				Statics.globShoutExpBase);
		s.setProperty(Statics.S_SCRIPT_SHOUTEXP_PROPERTY_1, Statics.playerref);
		s.setProperty(Statics.S_SCRIPT_SHOUTEXP_PROPERTY_2,
				this.getShoutExpFactor(m));

		return true;
	}

	private boolean markDisarm(MGEF m) {

		// type 9 -> disarm
		if (m.getEffectType() == 9.0f) {
			m.getKeywordSet().addKeywordRef(Statics.kwMagicDisarm);

			Condition c1 = new Condition(Condition.P_FormID.WornHasKeyword,
					Statics.kwWeaponSchoolLightWeaponry);
			c1.setOperator(Operator.EqualTo);
			c1.setValue(0.0f);
			c1.setRunOnType(RunOnType.Subject);
			c1.set(CondFlag.OR, true);

			Condition c2 = new Condition(Condition.P_FormID.HasPerk,
					Statics.perkLIASecureGrip);
			c2.setOperator(Operator.EqualTo);
			c2.setValue(0.0f);
			c2.setRunOnType(RunOnType.Subject);
			c2.set(CondFlag.OR, true);

			m.addCondition(c1);
			m.addCondition(c2);

			SPGlobal.log("MAGIC_EFFECT_PATCHER", m.getName()
					+ ": Marked as disarm effect");
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	// TODO finish
	private float getShoutExpFactor(MGEF m) {
		return 1.0f;
	}

	public String getInfo() {
		return "Marking shouts and certain magic effects...";
	}
}
