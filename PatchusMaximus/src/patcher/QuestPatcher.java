package patcher;

import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.QUST;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import util.Statics;
import xml.access.XmlStorage;

// TODO unused

public class QuestPatcher implements Patcher {

	private Mod patch;
	private XmlStorage s;

	protected QuestPatcher(Mod patch, XmlStorage s) {
		this.patch = patch;
		this.s = s;
	}

	@Override
	public void runChanges() {
		this.swapMQSpells();
	}

	/**
	 * Swap spells on main quest. To avoid conflicts, I let SkyProc handle this.
	 * 
	 */
	private void swapMQSpells() {
		SPGlobal.log("QUEST_PATCHER", "Started swapping main quest properties");

		if (this.s.useMage()) {
			SPGlobal.log("QUEST_PATCHER", "Started swapping main quest properties");
			QUST mq = (QUST) SPDatabase.getMajor(Statics.questMain, GRUP_TYPE.QUST);
			
			mq.getScriptPackage().getScript("MQ101QuestScript")
					.setProperty("Fury", Statics.spellFearNew);
			mq.getScriptPackage()
					.getScript("MQ101QuestScript")
					.setProperty("ConjureFamiliar",
							Statics.spellConjureWeakFlameAtronachNew);
			mq.getScriptPackage().getScript("MQ101QuestScript")
					.setProperty("Sparks", Statics.spellSparksNew);

			this.patch.addRecord(mq);
			SPGlobal.log("QUEST_PATCHER", "Done swapping main quest properties");
		}
	}
	
	public String getInfo() {
		return "Swapping properties on quest scripts...";
	}

}
