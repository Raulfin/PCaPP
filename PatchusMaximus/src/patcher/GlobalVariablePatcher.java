package patcher;

import skyproc.GLOB;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.SPDatabase;
import util.Statics;
import xml.access.XmlStorage;

public class GlobalVariablePatcher implements Patcher {
	private XmlStorage s;
	private Mod patch;

	public GlobalVariablePatcher(Mod patch, XmlStorage s) {
		this.s = s;
		this.patch = patch;
	}

	@Override
	public void runChanges() {
		GLOB isMage = (GLOB) (SPDatabase.getMajor(Statics.globUseMage,
				 GRUP_TYPE.GLOB));
		GLOB isWarrior = (GLOB) (SPDatabase.getMajor(Statics.globUseWarrior,
				 GRUP_TYPE.GLOB));
		GLOB isThief = (GLOB) (SPDatabase.getMajor(Statics.globUseThief,
				 GRUP_TYPE.GLOB));
		
		if(this.s.useMage()){
			isMage.setValue(1.0f);
			patch.addRecord(isMage);
		}
		
		if(this.s.useThief()){
			isThief.setValue(1.0f);
			patch.addRecord(isThief);
		}
		
		if(this.s.useWarrior()){
			isWarrior.setValue(1.0f);
			patch.addRecord(isWarrior);
		}
	}
	
	public String getInfo() {
		return "Global variable adjustments...";
	}
}
