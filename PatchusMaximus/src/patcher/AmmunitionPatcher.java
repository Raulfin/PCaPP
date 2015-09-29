package patcher;

import java.util.ArrayList;

import skyproc.AMMO;
import skyproc.AMMO.AMMOFlag;
import skyproc.COBJ;
import skyproc.Condition;
import skyproc.Condition.Operator;
import skyproc.Condition.RunOnType;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.PROJ;
import skyproc.PROJ.ProjectileFlag;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import util.Statics;
import xml.access.XmlStorage;
import xml.lowLevel.ammunition.AmmunitionMaterial;
import xml.lowLevel.ammunition.AmmunitionModifier;
import xml.lowLevel.ammunition.AmmunitionType;
import enums.BaseProjectileTypes;

final class AmmunitionPatcher implements Patcher {

	private XmlStorage s;
	private Mod merger, patch;

	public AmmunitionPatcher(Mod merger, Mod patch, XmlStorage s) {
		this.s = s;
		this.merger = merger;
		this.patch = patch;
	}

	public void runChanges() {

		AmmunitionMaterial am = null;
		AmmunitionType at = null;
		ArrayList<AmmunitionModifier> amod = null;

		boolean shouldPatch = false;

		for (AMMO a : this.merger.getAmmo()) {
			try {
				if (!(this.shouldPatch(a))) {
					continue;
				}

				if (null == (at = this.s.getAmmunitionType(a))) {
					SPGlobal.log("AMMUNITION_PATCHER", a.getName()
							+ ": Failed to patch - no ammunition type");
					continue;
				}

				if (null == (am = this.s.getAmmunitionMaterial(a))) {
					SPGlobal.log("AMMUNITION_PATCHER", a.getName()
							+ ": Failed to patch - no ammunition material");
					continue;
				}

				if (am.isMultiply()) {

					if (at.getType().equals(BaseProjectileTypes.ARROW)) {
						this.createArrowVariants(a, am, at);
					} else if (at.getType().equals(BaseProjectileTypes.BOLT)) {
						this.createBoltVariants(a, am, at);
					} else {
						SPGlobal.log("AMMUNITION_PATCHER", a.getName()
								+ ": Failed to create variants - type missing");
					}
				} else {
					SPGlobal.log("AMMUNITION_PATCHER", a.getName()
							+ ": AMMO multiplication excluded");
				}

				// thief module changes
				if (this.s.useThief()) {

				}
				// warrior module changes
				if (this.s.useWarrior()) {

					if (this.setDamage(a, am, at, amod)) {
						shouldPatch = true;
					}

					this.patchProjectile(a, am, at, amod);
				}

				if (shouldPatch) {
					this.patch.addRecord(a);
					shouldPatch = false;
				}
			} catch (Exception e) {
				SPGlobal.log("ERROR in Ammunition Patcher: "
						+ e.toString());
			}
		}
	}

	private boolean shouldPatch(AMMO a) {
		if (null == a.getName()) {
			SPGlobal.log("AMMUNITION_PATCHER", "null name");
			return false;
		}

		if (a.get(AMMOFlag.NonPlayable)) {
			SPGlobal.log("AMMUNITION_PATCHER", a.getName() + "not playable");
			return false;
		}

		return true;
	}

	private boolean setDamage(AMMO a, AmmunitionMaterial am, AmmunitionType at,
			ArrayList<AmmunitionModifier> amod) {

		double oldDamage = a.getDamage();
		double newDamage = at.getDamageBase() + am.getDamageModifier();

		if (null != amod) {
			for (AmmunitionModifier amo : amod) {
				newDamage += amo.getDamageModifier();
			}
		}

		if (oldDamage != newDamage) {
			a.setDamage((float) newDamage);
			SPGlobal.log("AMMUNITION_PATCHER", a.getName() + ": Set damage to "
					+ newDamage);
			return true;
		}

		return false;
	}

	private void patchProjectile(AMMO a, AmmunitionMaterial am,
			AmmunitionType at, ArrayList<AmmunitionModifier> amod) {

		PROJ p = (PROJ) SPDatabase.getMajor(a.getProjectile(), GRUP_TYPE.PROJ);

		if (null == p) {
			SPGlobal.log("AMMUNITION_PATCHER", a.getName()
					+ ": Projectile is null");
			return;
		}

		if (this.setSpeed(p, am, at, amod) | this.setGravity(p, am, at, amod)
				| this.setRange(p, am, at, amod)) {
			this.patch.addRecord(p);
		}

		SPGlobal.log("AMMUNITION_PATCHER", a.getName()
				+ ": Done patching projectile");

	}

	private boolean setSpeed(PROJ p, AmmunitionMaterial am, AmmunitionType at,
			ArrayList<AmmunitionModifier> amod) {

		double oldSpeed = p.getSpeed();
		double newSpeed = at.getSpeedBase() + am.getSpeedModifier();

		if (null != amod) {
			for (AmmunitionModifier amo : amod) {
				newSpeed += amo.getDamageModifier();
			}
		}

		if (oldSpeed != newSpeed) {
			p.setSpeed((float) newSpeed);
			SPGlobal.log("AMMUNITION_PATCHER", p.getName() + ": Set speed to "
					+ newSpeed);
			return true;
		}
		return false;
	}

	private boolean setGravity(PROJ p, AmmunitionMaterial am,
			AmmunitionType at, ArrayList<AmmunitionModifier> amod) {

		double oldGravity = p.getGravity();
		double newGravity = at.getGravityBase() + am.getGravityModifier();

		if (null != amod) {
			for (AmmunitionModifier amo : amod) {
				newGravity += amo.getGravityModifier();
			}
		}

		if (oldGravity != newGravity) {
			p.setGravity((float) newGravity);
			SPGlobal.log("AMMUNITION_PATCHER", p.getName()
					+ ": Set gravity to " + newGravity);
			return true;
		}

		return false;
	}

	private boolean setRange(PROJ p, AmmunitionMaterial am, AmmunitionType at,
			ArrayList<AmmunitionModifier> amod) {

		double oldRange = p.getRange();
		double newRange = at.getRangeBase() + am.getRangeModifier();

		if (null != amod) {
			for (AmmunitionModifier amo : amod) {
				newRange += amo.getRangeModifier();
			}
		}

		if (oldRange != newRange) {
			p.setRange((float) newRange);
			SPGlobal.log("AMMUNITION_PATCHER", p.getName() + ": Set range to "
					+ newRange);
			return true;
		}
		return false;
	}

	/**
	 * Creates strong variant of given AMMO. AMMO returned already carries
	 * patched stats.
	 * 
	 * @param a
	 * @return the AMMO created
	 */
	private AMMO createStrongAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO strongAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName() + " - "
				+ Statics.S_AMMO_STRONG + a.getFormStr());
		strongAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_STRONG));

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(strongAMMO);

		this.setDamage(strongAMMO, am, at, amod);
		this.patchProjectile(strongAMMO, am, at, amod);

		return strongAMMO;

	}

	/**
	 * Creates strongest variant of given AMMO. AMMO returned already carries
	 * patched stats.
	 * 
	 * @param a
	 * @return the AMMO created
	 */
	private AMMO createStrongestAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO strongestAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_STRONGEST + a.getFormStr());
		strongestAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_STRONGEST));

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(strongestAMMO);

		this.setDamage(strongestAMMO, am, at, amod);
		this.patchProjectile(strongestAMMO, am, at, amod);

		return strongestAMMO;

	}

	/**
	 * Creates poison explosion variant of given AMMO. AMMO returned already
	 * carries patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createPoisonAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO poisonAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_POISON + a.getFormStr());
		poisonAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_POISON));

		poisonAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_POISON_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(poisonAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ poisonAMMO.getName() + a.getFormStr());

		poisonAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, false);
		newProjectile.setExplosionType(Statics.expPoison);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(poisonAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(poisonAMMO, am, at, amod);
			this.patchProjectile(poisonAMMO, am, at, amod);
		}

		return poisonAMMO;
	}

	/**
	 * Creates fire explosion variant of given AMMO. AMMO returned already
	 * carries patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createFireAMMO(AMMO a, AmmunitionMaterial am, AmmunitionType at) {
		AMMO fireAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_FIRE + a.getFormStr());
		fireAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_FIRE));

		fireAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_FIRE_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(fireAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ fireAMMO.getName() + a.getFormStr());

		fireAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, false);
		newProjectile.setExplosionType(Statics.expElementalFire);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(fireAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(fireAMMO, am, at, amod);
			this.patchProjectile(fireAMMO, am, at, amod);
		}

		return fireAMMO;
	}

	/**
	 * Creates frost explosion variant of given AMMO. AMMO returned already
	 * carries patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createFrostAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO frostAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_FROST + a.getFormStr());
		frostAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_FROST));

		frostAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_FROST_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(frostAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ frostAMMO.getName() + a.getFormStr());

		frostAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, false);
		newProjectile.setExplosionType(Statics.expElementalFrost);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(frostAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(frostAMMO, am, at, amod);
			this.patchProjectile(frostAMMO, am, at, amod);
		}

		return frostAMMO;
	}

	/**
	 * Creates shock explosion variant of given AMMO. AMMO returned already
	 * carries patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createShockAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO shockAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_SHOCK + a.getFormStr());
		shockAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_SHOCK));

		shockAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_SHOCK_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(shockAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ shockAMMO.getName() + a.getFormStr());

		shockAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, false);
		newProjectile.setExplosionType(Statics.expElementalShock);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(shockAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(shockAMMO, am, at, amod);
			this.patchProjectile(shockAMMO, am, at, amod);
		}

		return shockAMMO;
	}

	/**
	 * Creates non-elemental explosion variant of given AMMO. AMMO returned
	 * already carries patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createExplosiveAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO explosiveAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_EXPLOSIVE + a.getFormStr());
		explosiveAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_EXPLOSIVE));

		explosiveAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_EXPLOSIVE_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(explosiveAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ explosiveAMMO.getName() + a.getFormStr());

		explosiveAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, false);
		newProjectile.setExplosionType(Statics.expExploding);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(explosiveAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(explosiveAMMO, am, at, amod);
			this.patchProjectile(explosiveAMMO, am, at, amod);
		}

		return explosiveAMMO;
	}

	/**
	 * Creates barbed variant of given AMMO. AMMO returned already carries
	 * patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createBarbedAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO barbedAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_BARBED + a.getFormStr());
		barbedAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_BARBED));

		barbedAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_BARBED_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(barbedAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ barbedAMMO.getName() + a.getFormStr());

		barbedAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, false);
		newProjectile.setExplosionType(Statics.expBarbed);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(barbedAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(barbedAMMO, am, at, amod);
			this.patchProjectile(barbedAMMO, am, at, amod);
		}

		return barbedAMMO;
	}

	/**
	 * Creates timebomb variant of given AMMO. AMMO returned already carries
	 * patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createTimebombAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO timebombAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_TIMEBOMB + a.getFormStr());
		timebombAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_TIMEBOMB));

		timebombAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_TIMEBOMB_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(timebombAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ timebombAMMO.getName() + a.getFormStr());

		timebombAMMO.setProjectile(newProjectile.getForm());

		newProjectile.set(ProjectileFlag.Explosion, true);
		newProjectile.set(ProjectileFlag.AltTrigger, true);
		// TODO test
		newProjectile.setTimer(Statics.timebombTimer);
		newProjectile.setExplosionType(Statics.expTimebomb);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(timebombAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(timebombAMMO, am, at, amod);
			this.patchProjectile(timebombAMMO, am, at, amod);
		}

		return timebombAMMO;
	}

	/**
	 * Creates lightsource variant of given AMMO. AMMO returned already carries
	 * patched stats.
	 * 
	 * @param patch
	 * @param a
	 * @return the AMMO created
	 */

	private AMMO createLightsourceAMMO(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {
		AMMO lsAMMO = (AMMO) patch.makeCopy(a, Statics.S_PREFIX_PATCHER
				+ Statics.S_PREFIX_AMMUNITION + a.getName()
				+ Statics.S_AMMO_LIGHTSOURCE + a.getFormStr());
		lsAMMO.setName(a.getName() + " - "
				+ this.s.getOutputString(Statics.S_AMMO_LIGHTSOURCE));

		lsAMMO.setDescription(this.s
				.getOutputString(Statics.S_AMMO_LIGHTSOURCE_DESC));
		PROJ p = (PROJ) SPDatabase.getMajor(lsAMMO.getProjectile(),
				GRUP_TYPE.PROJ);

		PROJ newProjectile = (PROJ) patch.makeCopy(
				p,
				Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_PROJECTILE
						+ lsAMMO.getName() + a.getFormStr());

		lsAMMO.setProjectile(newProjectile.getForm());

		newProjectile.setLight(Statics.lightLightsource);

		ArrayList<AmmunitionModifier> amod = this.s
				.getAmmunitionModifiers(lsAMMO);

		if (this.s.useWarrior()) {
			this.setDamage(lsAMMO, am, at, amod);
			this.patchProjectile(lsAMMO, am, at, amod);
		}

		return lsAMMO;
	}

	/**
	 * Creates and adds all bolt variants. Stat patching is delegated to called
	 * methods.
	 * 
	 * @param a
	 * @param am
	 * @param at
	 */

	private void createBoltVariants(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {

		ArrayList<FormID> perks = new ArrayList<>();
		ArrayList<FormID> ingredients = new ArrayList<>();

		AMMO newStrongAMMO = this.createStrongAMMO(a, am, at);
		ingredients.add(Statics.ingotIron);
		perks.add(Statics.perkRangedWeaponryAdvancedMissilecraft0);
		this.createAmmoCraftingRecipeVariants(a, newStrongAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		perks.clear();
		ingredients.clear();

		AMMO newStrongestAMMO = this.createStrongestAMMO(a, am, at);
		ingredients.add(Statics.ingotSteel);
		perks.add(Statics.perkRangedWeaponryAdvancedMissilecraft0);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO, newStrongestAMMO,
				ingredients, perks, Statics.kwCraftingSmithingForge);
		perks.clear();
		ingredients.clear();

		// poison

		AMMO newPoisonAMMO = this.createPoisonAMMO(a, am, at);
		AMMO newStrongPoisonAMMO = this.createPoisonAMMO(newStrongAMMO, am, at);
		AMMO newStrongestPoisonAMMO = this.createPoisonAMMO(newStrongestAMMO,
				am, at);

		ingredients.add(Statics.deathBell);
		perks.add(Statics.perkAlchemyPoisonBurst);

		this.createAmmoCraftingRecipeVariants(a, newPoisonAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongPoisonAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestPoisonAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		perks.clear();
		ingredients.clear();

		// shock

		AMMO newShockAMMO = this.createShockAMMO(a, am, at);
		AMMO newStrongShockAMMO = this.createShockAMMO(newStrongAMMO, am, at);
		AMMO newStrongestShockAMMO = this.createShockAMMO(newStrongestAMMO, am,
				at);

		ingredients.add(Statics.voidSalt);
		perks.add(Statics.perkAlchemyElementalBombard);

		this.createAmmoCraftingRecipeVariants(a, newShockAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongShockAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestShockAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// frost

		AMMO newFrostAMMO = this.createFrostAMMO(a, am, at);
		AMMO newStrongFrostAMMO = this.createFrostAMMO(newStrongAMMO, am, at);
		AMMO newStrongestFrostAMMO = this.createFrostAMMO(newStrongestAMMO, am,
				at);

		ingredients.add(Statics.frostSalt);
		perks.add(Statics.perkAlchemyElementalBombard);

		this.createAmmoCraftingRecipeVariants(a, newFrostAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongFrostAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestFrostAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// fire

		AMMO newFireAMMO = this.createFireAMMO(a, am, at);
		AMMO newStrongFireAMMO = this.createFireAMMO(newStrongAMMO, am, at);
		AMMO newStrongestFireAMMO = this.createFireAMMO(newStrongestAMMO, am,
				at);

		ingredients.add(Statics.fireSalt);
		perks.add(Statics.perkAlchemyElementalBombard);

		this.createAmmoCraftingRecipeVariants(a, newFireAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO, newStrongFireAMMO,
				ingredients, perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestFireAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// barbed

		AMMO newBarbedAMMO = this.createBarbedAMMO(a, am, at);
		AMMO newStrongBarbedAMMO = this.createBarbedAMMO(newStrongAMMO, am, at);
		AMMO newStrongestBarbedAMMO = this.createBarbedAMMO(newStrongestAMMO,
				am, at);

		ingredients.add(Statics.ingotSteel);
		ingredients.add(Statics.ingotIron);
		perks.add(Statics.perkRangedWeaponryAdvancedMissilecraft1);

		this.createAmmoCraftingRecipeVariants(a, newBarbedAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongBarbedAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestBarbedAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// explosive

		AMMO newExplosiveAMMO = this.createExplosiveAMMO(a, am, at);
		AMMO newStrongExplosiveAMMO = this.createExplosiveAMMO(newStrongAMMO,
				am, at);
		AMMO newStrongestExplosiveAMMO = this.createExplosiveAMMO(
				newStrongestAMMO, am, at);

		ingredients.add(Statics.ale);
		ingredients.add(Statics.torchbugThorax);
		perks.add(Statics.perkAlchemyFuse);

		this.createAmmoCraftingRecipeVariants(a, newExplosiveAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongExplosiveAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestExplosiveAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// timebomb

		AMMO newTimebombAMMO = this.createTimebombAMMO(a, am, at);
		AMMO newStrongTimebombAMMO = this.createTimebombAMMO(newStrongAMMO, am,
				at);
		AMMO newStrongestTimebombAMMO = this.createTimebombAMMO(
				newStrongestAMMO, am, at);

		ingredients.add(Statics.ale);
		ingredients.add(Statics.torchbugThorax);
		ingredients.add(Statics.charcoal);
		perks.add(Statics.perkAlchemyAdvancedExplosives);

		this.createAmmoCraftingRecipeVariants(a, newTimebombAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongTimebombAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestTimebombAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// lightsource

		AMMO newLightsourceAMMO = this.createLightsourceAMMO(a, am, at);
		AMMO newStrongLightsourceAMMO = this.createLightsourceAMMO(
				newStrongAMMO, am, at);
		AMMO newStrongestLightsourceAMMO = this.createLightsourceAMMO(
				newStrongestAMMO, am, at);

		ingredients.add(Statics.leatherStrips);
		ingredients.add(Statics.torchbugThorax);
		perks.add(Statics.perkSneakThiefsToolbox0);

		this.createAmmoCraftingRecipeVariants(a, newLightsourceAMMO,
				ingredients, perks, Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongAMMO,
				newStrongLightsourceAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);
		this.createAmmoCraftingRecipeVariants(newStrongestAMMO,
				newStrongestLightsourceAMMO, ingredients, perks,
				Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

	}

	/**
	 * Creates and adds all arrow variants. Stat patching is delegated to called
	 * methods. fire, frost, shock, poison
	 * 
	 * @param a
	 * @param am
	 * @param at
	 */

	private void createArrowVariants(AMMO a, AmmunitionMaterial am,
			AmmunitionType at) {

		ArrayList<FormID> perks = new ArrayList<>();
		ArrayList<FormID> ingredients = new ArrayList<>();

		// poison

		AMMO newPoisonAMMO = this.createPoisonAMMO(a, am, at);

		perks.add(Statics.perkAlchemyPoisonBurst);
		ingredients.add(Statics.deathBell);
		this.createAmmoCraftingRecipeVariants(a, newPoisonAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		perks.clear();
		ingredients.clear();

		// fire

		AMMO newFireAMMO = this.createFireAMMO(a, am, at);

		perks.add(Statics.perkAlchemyElementalBombard);
		ingredients.add(Statics.fireSalt);
		this.createAmmoCraftingRecipeVariants(a, newFireAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		perks.clear();
		ingredients.clear();

		// frost

		AMMO newFrostAMMO = this.createFrostAMMO(a, am, at);

		perks.add(Statics.perkAlchemyElementalBombard);
		ingredients.add(Statics.frostSalt);
		this.createAmmoCraftingRecipeVariants(a, newFrostAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		perks.clear();
		ingredients.clear();

		// shock

		AMMO newShockAMMO = this.createShockAMMO(a, am, at);

		perks.add(Statics.perkAlchemyElementalBombard);
		ingredients.add(Statics.voidSalt);
		this.createAmmoCraftingRecipeVariants(a, newShockAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);
		perks.clear();
		ingredients.clear();

		// lightsource

		AMMO newLightsourceAMMO = this.createLightsourceAMMO(a, am, at);

		ingredients.add(Statics.leatherStrips);
		ingredients.add(Statics.torchbugThorax);
		perks.add(Statics.perkSneakThiefsToolbox0);

		this.createAmmoCraftingRecipeVariants(a, newLightsourceAMMO,
				ingredients, perks, Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// explosive

		AMMO newExplosiveAMMO = this.createExplosiveAMMO(a, am, at);

		ingredients.add(Statics.ale);
		ingredients.add(Statics.torchbugThorax);
		perks.add(Statics.perkAlchemyFuse);

		this.createAmmoCraftingRecipeVariants(a, newExplosiveAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

		// timebomb

		AMMO newTimebombAMMO = this.createTimebombAMMO(a, am, at);

		ingredients.add(Statics.ale);
		ingredients.add(Statics.torchbugThorax);
		ingredients.add(Statics.charcoal);
		perks.add(Statics.perkAlchemyAdvancedExplosives);

		this.createAmmoCraftingRecipeVariants(a, newTimebombAMMO, ingredients,
				perks, Statics.kwCraftingSmithingForge);

		ingredients.clear();
		perks.clear();

	}

	/**
	 * Creates COBJ for ammunition
	 * 
	 * @param a
	 * @param inputNum
	 * @param ingredients
	 * @param requiredPerk
	 * @param craftingBenchKeyword
	 */
	private void createAmmoCraftingRecipe(AMMO baseAmmo, AMMO resultAmmo,
			int inputNum, int outputNum, ArrayList<FormID> ingredients,
			ArrayList<FormID> requiredPerks, FormID blockerPerk,
			FormID craftingBenchKeyword) {

		String edid = null;

		if (blockerPerk != null) {
			edid = Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_AMMUNITION
					+ Statics.S_PREFIX_CRAFTING + resultAmmo.getName()
					+ baseAmmo.getFormStr() + blockerPerk.getFormStr();
		} else {

			edid = Statics.S_PREFIX_PATCHER + Statics.S_PREFIX_AMMUNITION
					+ Statics.S_PREFIX_CRAFTING + resultAmmo.getName()
					+ baseAmmo.getFormStr();
		}

		COBJ newRecipe = new COBJ(edid);

		newRecipe.setBenchKeywordFormID(craftingBenchKeyword);
		newRecipe.setResultFormID(resultAmmo.getForm());
		newRecipe.setOutputQuantity(outputNum);

		newRecipe.addIngredient(baseAmmo.getForm(), inputNum);

		for (FormID ing : ingredients) {
			newRecipe.addIngredient(ing, 1);
		}

		for (FormID perk : requiredPerks) {
			Condition c = new Condition(Condition.P_FormID.HasPerk, perk);
			c.setOperator(Operator.EqualTo);
			c.setRunOnType(RunOnType.Subject);
			c.setValue(1.0f);
			newRecipe.addCondition(c);
		}

		if (null != blockerPerk) {
			Condition c = new Condition(Condition.P_FormID.HasPerk, blockerPerk);
			c.setOperator(Operator.EqualTo);
			c.setRunOnType(RunOnType.Subject);
			c.setValue(0.0f);
			newRecipe.addCondition(c);
		}

		Condition c = new Condition(Condition.P_FormID.GetItemCount,
				baseAmmo.getForm());
		c.setOperator(Operator.GreaterThanOrEqual);
		c.setRunOnType(RunOnType.Subject);
		c.setValue(1.0f);
		newRecipe.addCondition(c);

		this.patch.addRecord(newRecipe);
	}

	/**
	 * Creates recipe variants, factoring in Skilled Enhancer
	 * 
	 * @param baseAmmo
	 * @param resultAmmo
	 * @param inputNum
	 * @param outputNum
	 * @param ingredients
	 * @param requiredPerks
	 * @param craftingBenchKeyword
	 */
	private void createAmmoCraftingRecipeVariants(AMMO baseAmmo,
			AMMO resultAmmo, ArrayList<FormID> ingredients,
			ArrayList<FormID> requiredPerks, FormID craftingBenchKeyword) {

		@SuppressWarnings("unchecked")
		ArrayList<FormID> allPerks = (ArrayList<FormID>) requiredPerks.clone();

		this.createAmmoCraftingRecipe(baseAmmo, resultAmmo,
				Statics.enhancementIn, Statics.enhancementOut, ingredients,
				allPerks, Statics.perkAlchemySkilledEnhancer0,
				Statics.kwCraftingSmithingForge);

		allPerks.add(Statics.perkAlchemySkilledEnhancer0);

		this.createAmmoCraftingRecipe(baseAmmo, resultAmmo,
				Statics.enhancementIn, Statics.enhancementOutSE0, ingredients,
				allPerks, Statics.perkAlchemySkilledEnhancer1,
				Statics.kwCraftingSmithingForge);

		allPerks.add(Statics.perkAlchemySkilledEnhancer1);

		this.createAmmoCraftingRecipe(baseAmmo, resultAmmo,
				Statics.enhancementIn, Statics.enhancementOutSE1, ingredients,
				allPerks, null, Statics.kwCraftingSmithingForge);

	}

	public String getInfo() {
		return "Bolt and arrow stats and variants...";
	}
}
