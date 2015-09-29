package patcher;

import skyproc.ALCH;
import skyproc.GRUP_TYPE;
import skyproc.INGR;
import skyproc.MGEF;
import skyproc.MGEF.SpellEffectFlag;
import skyproc.Mod;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import skyproc.genenums.ActorValue;
import util.Statics;
import xml.access.XmlStorage;
import xml.lowLevel.alchemy.AlchemyEffect;
import xml.lowLevel.alchemy.IngredientVariation;
import xml.lowLevel.alchemy.PotionMultiplier;

final class AlchemyPatcher implements Patcher {
	private XmlStorage s;
	private Mod merger, patch;

	protected AlchemyPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		boolean shouldAdd = false;

		// first do potions
		for (ALCH a : this.merger.getAlchemy()) {
			try {
				// thief module changes

				if (this.s.useThief()) {
					if (!this.s.isAlchExcluded(a)
							&& this.makePotionWorkOverTime(a)) {
						shouldAdd = true;
					}
					this.disableAssociatedMagicSchools(a);
				}

				// if changed, add record
				if (shouldAdd) {
					this.patch.addRecord(a);
					shouldAdd = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Alchemy Patcher: " + e.toString());
			}
		}

		// next, ingredients

		for (INGR i : this.merger.getIngredients()) {
			// thief module changes
			try {
				if (this.s.useThief()) {
					if (!this.s.isIngrExcluded(i)
							&& this.makeIngredientWorkOverTime(i)) {
						shouldAdd = true;
					}

					this.disableAssociatedMagicSchools(i);
				}

				// if changed, add record
				if (shouldAdd) {
					this.patch.addRecord(i);
					shouldAdd = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Alchemy Patcher: " + e.toString());
			}

		}
	}

	/**
	 * Modifies potions and effects linked on potions to be duration based
	 * 
	 * @param a
	 * @return
	 */
	private boolean makePotionWorkOverTime(ALCH a) {
		boolean ret = false;
		AlchemyEffect ae = null;
		PotionMultiplier pm = null;
		double oldDur, oldMag, oldCost, newDur, newMag, newCost;
		int numEffects = a.getMagicEffects().size();

		// for each effect, search for a fitting xml definition

		for (int i = 0; i < numEffects; i++) {

			oldDur = a.getMagicEffects().get(i).getDuration();
			oldMag = a.getMagicEffects().get(i).getMagnitude();

			MGEF m = (MGEF) SPDatabase.getMajor(a.getMagicEffects().get(i)
					.getMagicRef(), GRUP_TYPE.MGEF);

			if (null == (ae = this.s.getAlchemyEffect(m))) {
				SPGlobal.log("ALCHEMY_PATCHER",
						a.getName() + " : " + m.getName()
								+ ": No alchemy_effect defined");
				continue;
			}

			oldCost = m.getBaseCost();

			// effect found; now check for potion multiplier
			pm = this.s.getPotionMultiplier(a);

			newDur = ae.getBaseDuration();
			newMag = ae.getBaseMagnitude();
			newCost = ae.getBaseCost();

			if (pm != null && ae.isAllowPotionMultiplier()) {
				newDur *= pm.getMultiplierDuration();
				newMag *= pm.getMultiplierMagnitude();
			} else {
				continue;
			}

			// if either changed for one effect at least, return true in the end
			if (oldDur != newDur && newDur >= 0) {

				// adjust description to include duration

				if (!(m.getDescription().contains(Statics.S_DUR_REPLACE))) {

					m.set(SpellEffectFlag.NoDuration, false);

					m.setDescription(m.getDescription() + " ["
							+ this.s.getOutputString(Statics.S_DURATION) + ": "
							+ Statics.S_DUR_REPLACE + " "
							+ this.s.getOutputString(Statics.S_SECONDS) + "]");

					this.patch.addRecord(m);

				}

				a.getMagicEffects().get(i).setDuration((int) newDur);
				ret = true;
			}

			if (oldMag != newMag && newMag >= 0) {
				a.getMagicEffects().get(i).setMagnitude((float) newMag);
				ret = true;
			}

			if (oldCost != newCost && newCost >= 0) {
				m.setBaseCost((float) newCost);
				ret = true;
			}
		}

		return ret;
	}

	private boolean makeIngredientWorkOverTime(INGR i) {

		boolean ret = false;
		AlchemyEffect ae = null;
		IngredientVariation iv = null;
		double oldDur, oldMag, newDur, newMag;

		int numEffects = i.getMagicEffects().size();

		// for each effect, search for a fitting xml definition

		for (int j = 0; j < numEffects; j++) {

			oldDur = i.getMagicEffects().get(j).getDuration();
			oldMag = i.getMagicEffects().get(j).getMagnitude();

			MGEF m = (MGEF) SPDatabase.getMajor(i.getMagicEffects().get(j)
					.getMagicRef(), GRUP_TYPE.MGEF);
			if (null == (ae = this.s.getAlchemyEffect(m))) {
				SPGlobal.log("ALCHEMY_PATCHER",
						i.getName() + " : " + m.getName()
								+ ": No alchemy_effect defined");
				continue;
			}
			// effect found; now check for potion multiplier
			iv = this.s.getIngredientVariation(i);

			newDur = ae.getBaseDuration();
			newMag = ae.getBaseMagnitude();

			if (iv != null && ae.isAllowIngredientVariation()) {
				newDur *= iv.getMultiplierDuration();
				newMag *= iv.getMultiplierMagnitude();
			}

			// if either changed at least for one effect, return true in the end
			if (oldDur != newDur && newDur >= 0) {
				i.getMagicEffects().get(j).setDuration((int) newDur);
				ret = true;
			}

			if (oldMag != newMag && newMag >= 0) {
				i.getMagicEffects().get(j).setMagnitude((float) newMag);
				ret = true;
			}
		}

		return ret;
	}

	private void disableAssociatedMagicSchools(ALCH a) {
		int numEffects = a.getMagicEffects().size();

		for (int i = 0; i < numEffects; i++) {

			MGEF m = (MGEF) SPDatabase.getMajor(a.getMagicEffects().get(i)
					.getMagicRef(), GRUP_TYPE.MGEF);

			if (!m.getSkillType().equals(ActorValue.UNKNOWN)) {
				m.setSkillType(ActorValue.UNKNOWN);
				this.patch.addRecord(m);
				SPGlobal.log("ALCHEMY_PATCHER", m.getName()
						+ ": Removed magic school assignment");
			}
		}
	}

	private void disableAssociatedMagicSchools(INGR i) {
		int numEffects = i.getMagicEffects().size();

		for (int j = 0; j < numEffects; j++) {

			MGEF m = (MGEF) SPDatabase.getMajor(i.getMagicEffects().get(j)
					.getMagicRef(), GRUP_TYPE.MGEF);

			if (!m.getSkillType().equals(ActorValue.UNKNOWN)) {
				m.setSkillType(ActorValue.UNKNOWN);
				this.patch.addRecord(m);
				SPGlobal.log("ALCHEMY_PATCHER", m.getName()
						+ ": Removed magic school assignment");
			}
		}
	}

	public String getInfo() {
		return "Ingredients and potions...";
	}
}
