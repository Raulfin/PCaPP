package patcher;

import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.NPC_;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import util.Statics;
import xml.access.XmlStorage;

/**
 * Edits player actor and main quest to swap a few spells. Distributes general
 * perks to all actors.
 * 
 * @author T3nd0
 * 
 */
final class NPCPatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	public NPCPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {
		this.patchPlayer();

		boolean shouldAdd = false;

		for (NPC_ npc : this.merger.getNPCs()) {
			try {
				if (!(this.shouldPatch(npc))) {
					continue;
				}

				SPGlobal.log("NPC_PATCHER", "Started patching " + npc.getName());

				if (this.s.useMage()) {
					shouldAdd = true;
					npc.addPerk(Statics.perkMageModuleScalingSpells, 1);
					npc.addPerk(Statics.perkMageModulePassives, 1);
					npc.addPerk(Statics.perkAlchemySkillBoosts, 1);
				}

				if (this.s.useThief()) {
					npc.addSpell(Statics.spellThiefModuleCombatAbility);
				}

				if (this.s.useWarrior()) {
					shouldAdd = true;
					npc.addPerk(Statics.perkWarriorModuleScarredPassive, 1);
					npc.addPerk(Statics.perkWarriorModuleFistScaling, 1);
					npc.addSpell(Statics.spellWarriorModuleShieldDetector);
					npc.addPerk(Statics.perkWarriorModuleScalingCritDamage, 1);
					npc.addPerk(Statics.perkWarriorModulePassiveCrossbow, 1);

				}

				if (shouldAdd) {
					this.patch.addRecord(npc);
					shouldAdd = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in NPC Patcher: " + e.toString());
			}

		}
	}

	private boolean shouldPatch(NPC_ npc) {

		if (this.s.isNPCExcluded(npc)) {
			SPGlobal.log("NPC_PATCHER", "Excluded " + npc.getName());
			return false;
		}

		return true;
	}

	/**
	 * Do player-specific patching
	 */
	private void patchPlayer() {
		SPGlobal.log("NPC_PATCHER", "Started patching player");

		NPC_ player = (NPC_) SPDatabase
				.getMajor(Statics.player, GRUP_TYPE.NPC_);

		player.addSpell(Statics.spellSpeedFix);

		if (this.s.useMage()) {
			player.removeSpell(Statics.spellFlamesOld);
			player.removeSpell(Statics.spellHealingOld);
			player.addSpell(Statics.spellMageModuleMain);
			player.addPerk(Statics.perkMageModuleScalingScrolls, 1);

			// if the user wants generic spells, he gets the new ones

			if (!(this.s.shouldRemoveUnspecificSpells())) {
				player.addSpell(Statics.spellFlamesNew);
				player.addSpell(Statics.spellRecoveryNew);
			}
		}

		if (this.s.useThief()) {
			player.addPerk(Statics.perkThiefModuleFingersmithXP, 1);
			player.addPerk(Statics.perkThiefModuleSpellSneak, 1);
			player.addPerk(Statics.perkThiefModulePassiveArmorSneakPenalty, 1);
			player.addPerk(Statics.perkThiefModuleWeaponSneakScaling, 1);
			player.addSpell(Statics.spellThiefModuleMain);
			player.addSpell(Statics.spellThiefModuleInitSneakTools);
			player.addPerk(Statics.perkThiefModulePassiveShoutScaling, 1);
		}

		if (this.s.useWarrior()) {
			player.addSpell(Statics.spellWarriorModuleTimedBlocking);
			player.addPerk(Statics.perkSmithingArcaneBlacksmith, 1);
			player.addPerk(Statics.perkWarriorModuleDualWieldMalus, 1);
		}

		this.patch.addRecord(player);
		SPGlobal.log("NPC_PATCHER", "Done patching player");
	}

	public String getInfo() {
		return "Distributing perks and abilities to NPCs...";
	}
}
