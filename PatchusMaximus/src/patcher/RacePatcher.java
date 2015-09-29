package patcher;

import skyproc.Mod;
import skyproc.RACE;
import skyproc.SPGlobal;
import skyproc.RACE.RACEFlags;
import util.Statics;
import xml.access.XmlStorage;

/**
 * Distributes commonly used abilities and replaces certain spells on races.
 * 
 * @author T3nd0
 * 
 */
final class RacePatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	public RacePatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		boolean add = false;

		for (RACE r : this.merger.getRaces()) {
			try {
				if (this.s.isRaceExcluded(r)) {
					continue;
				}

				// general changes

				// warrior changes
				if (this.s.useWarrior()) {
					add = true;
					r.addSpell(Statics.spellWarriorModuleMain);
					r.addSpell(Statics.spellWarriorModuleStamine);
				}

				// mage changes
				if (this.s.useMage()) {

				}
				// thief changes
				if (this.s.useThief()) {
				}

				if (this.s.useThief() && this.s.useWarrior()) {
					if (r.get(RACEFlags.Playable)) {
						add = true;
						r.addSpell(Statics.spellThiefModuleCombatAbility);
						r.addSpell(Statics.spellWarriorThiefModulePenaltyHeavy);
						r.addSpell(Statics.spellWarriorThiefModulePenaltyLight);
					}
				}

				if (add) {
					add = false;
					patch.addRecord(r);
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Race Patcher: " + e.toString());
			}
		}
	}

	public String getInfo() {
		return "Adding abilities to races...";
	}
}
