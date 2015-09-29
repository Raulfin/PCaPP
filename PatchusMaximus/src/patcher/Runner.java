package patcher;

import java.util.ArrayList;

import skyproc.Mod;
import skyproc.gui.SPProgressBarPlug;
import xml.access.XmlStorage;

/**
 * Instantiates all patchers and runs them. Singleton access.
 * 
 * @author T3nd0
 * 
 */
public class Runner {

	private static Runner r = null;

	private ArrayList<Patcher> patchers;

	private Runner(Mod merger, Mod patch, XmlStorage s) {
		this.patchers = new ArrayList<>();
		this.patchers.add(new CraftablePatcher(merger, patch, s));
		this.patchers.add(new WeaponPatcher(merger, patch, s));
		this.patchers.add(new ArmorPatcher(merger, patch, s));
		this.patchers.add(new BookPatcher(merger, patch, s));
		this.patchers.add(new RacePatcher(merger, patch, s));
		this.patchers.add(new AlchemyPatcher(merger, patch, s));
		this.patchers.add(new SpellPatcher(merger, patch, s));
		this.patchers.add(new AmmunitionPatcher(merger, patch, s));
		this.patchers.add(new GameSettingPatcher(merger, patch, s));
		this.patchers.add(new GlobalVariablePatcher(patch, s));
		this.patchers.add(new NPCPatcher(merger, patch, s));
		this.patchers.add(new MagicEffectPatcher(merger,patch,s));
		
		// TODO enable once done
//		 this.patchers.add(new QuestPatcher(patch, s));
	}

	public static Runner getInstance(Mod merger, Mod patch, XmlStorage s) {
		if (r == null) {
			r = new Runner(merger, patch, s);
		}

		return r;
	}

	public void patch() {
		for (Patcher p : this.patchers) {
			SPProgressBarPlug.setStatus("Task:" + p.getInfo());
			p.runChanges();
		}
	}
}
