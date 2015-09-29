package patcher;

import skyproc.GMST;
import skyproc.Mod;
import skyproc.SPGlobal;
import util.Statics;
import xml.access.XmlStorage;

final class GameSettingPatcher implements Patcher {
	private Mod merger;
	private Mod patch;
	private XmlStorage s;

	protected GameSettingPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.merger = merger;
		this.patch = patch;
		this.s = s;
	}

	public void runChanges() {

		boolean shouldAdd = false;

		for (GMST g : this.merger.getGameSettings()) {
			try {

				SPGlobal.log("GAME_SETTING_PATCHER DEBUG", g.getEDID());
				if (this.s.useWarrior()) {
					if (g.getEDID().equals(Statics.gmstfArmorScalingFactor)) {
						g.setData((float) this.s.getArmorRatingPerDR());
						shouldAdd = true;
					} else if (g.getEDID().equals(Statics.gmstfMaxArmorRating)) {
						g.setData((float) this.s.getmaxProtection());
						shouldAdd = true;
					} else if (g.getEDID().equals(Statics.gmstfArmorRatingMax)) {
						g.setData((float) this.s.getfArmorRatingMax());
						shouldAdd = true;
					} else if (g.getEDID().equals(Statics.gmstfArmorRatingPCMax)) {
						g.setData((float) this.s.getfArmorRatingPCMax());
						shouldAdd = true;
					}
				}

				if (this.s.useThief()) {

				}

				if (this.s.useMage()) {

				}

				if (shouldAdd) {
					SPGlobal.log("GAME_SETTING_PATCHER", g.getEDID()
							+ ": Altered.");
					this.patch.addRecord(g);
					shouldAdd = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Game Settings Patcher: " + e.toString());
			}
		}
	}

	public String getInfo() {
		return "Game settings...";
	}
}
