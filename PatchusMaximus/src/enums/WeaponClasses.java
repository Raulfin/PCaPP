package enums;

import skyproc.FormID;
import util.Statics;

public enum WeaponClasses {

	BLUNT(new FormID[]{Statics.kwWeaponClassBlunt}),
	BLADE(new FormID[]{Statics.kwWeaponClassBlade}), 
	PIERCING(new FormID[]{Statics.kwWeaponClassPiercing}), 
	NONE(null), 
	BLUNT_BLADE(new FormID[]{Statics.kwWeaponClassBlunt, Statics.kwWeaponClassBlade}), 
	BLUNT_PIERCING(new FormID[]{Statics.kwWeaponClassBlunt, Statics.kwWeaponClassPiercing}), 
	BLADE_PIERCING(new FormID[]{Statics.kwWeaponClassBlade, Statics.kwWeaponClassPiercing}), 
	ALL(new FormID[]{Statics.kwWeaponClassBlunt, Statics.kwWeaponClassPiercing, Statics.kwWeaponClassBlade});
	
	FormID[] relatedKeywords;
	
	private WeaponClasses(FormID[] relatedKeywords){
		this.relatedKeywords = relatedKeywords;
	}

	public FormID[] getRelatedKeywords() {
		return relatedKeywords;
	}

}
