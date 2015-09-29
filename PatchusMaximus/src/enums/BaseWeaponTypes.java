package enums;

import skyproc.FormID;
import util.Statics;

public enum BaseWeaponTypes {
	DAGGER(Statics.kwWeapTypeDaggerPerMa, Statics.kwWeaponSchoolLightWeaponry), 
	CROSSBOW(Statics.kwWeapTypeCrossbow, Statics.kwWeaponSchoolRangedWeaponry), 
	LONGBOW(Statics.kwWeapTypeLongbow, Statics.kwWeaponSchoolRangedWeaponry), 
	SHORTBOW(Statics.kwWeapTypeShortbow, Statics.kwWeaponSchoolRangedWeaponry), 
	KATANA(Statics.kwWeapTypeKatana, Statics.kwWeaponSchoolLightWeaponry), 
	NODACHI(Statics.kwWeapTypeNodachi, Statics.kwWeaponSchoolHeavyWeaponry), 
	TANTO(Statics.kwWeapTypeTanto, Statics.kwWeaponSchoolLightWeaponry), 
	WAKIZASHI(Statics.kwWeapTypeWakizashi, Statics.kwWeaponSchoolLightWeaponry), 
	SHORTSWORD(Statics.kwWeapTypeShortsword, Statics.kwWeaponSchoolLightWeaponry), 
	SCIMITAR(Statics.kwWeapTypeScimitar, Statics.kwWeaponSchoolLightWeaponry), 
	SABRE(Statics.kwWeapTypeSaber, Statics.kwWeaponSchoolLightWeaponry), 
	BATTLESTAFF(Statics.kwWeapTypeBattlestaff, Statics.kwWeaponSchoolHeavyWeaponry), 
	SHORTSPEAR(Statics.kwWeapTypeShortspear, Statics.kwWeaponSchoolLightWeaponry), 
	BASTARDSWORD(Statics.kwWeapTypeBastardSword, Statics.kwWeaponSchoolHeavyWeaponry), 
	CLUB(Statics.kwWeapTypeClub, Statics.kwWeaponSchoolLightWeaponry), 
	LONGMACE(Statics.kwWeapTypeLongmace, Statics.kwWeaponSchoolHeavyWeaponry), 
	LONGSWORD(Statics.kwWeapTypeLongsword, Statics.kwWeaponSchoolLightWeaponry), 
	HALBERD(Statics.kwWeapTypeHalberd, Statics.kwWeaponSchoolHeavyWeaponry), 
	PARTISAN(Statics.kwWeapTypePartisan, Statics.kwWeaponSchoolHeavyWeaponry), 
	ARMINGSWORD(Statics.kwWeapTypeArmingSword, Statics.kwWeaponSchoolLightWeaponry), 
	HATCHET(Statics.kwWeapTypeHatchet, Statics.kwWeaponSchoolLightWeaponry), 
	MAUL(Statics.kwWeapTypeMaul, Statics.kwWeaponSchoolLightWeaponry), 
	MACE(Statics.kwWeapTypeMacePerMa, Statics.kwWeaponSchoolLightWeaponry), 
	WARAXE(Statics.kwWeapTypeWaraxePerMa, Statics.kwWeaponSchoolLightWeaponry), 
	WARHAMMER(Statics.kwWeapTypeWarhammerPerMa, Statics.kwWeaponSchoolHeavyWeaponry), 
	GREATSWORD(Statics.kwWeapTypeGreatswordPerMa, Statics.kwWeaponSchoolHeavyWeaponry), 
	FIST(Statics.kwWeapTypeFist, Statics.kwWeaponSchoolLightWeaponry), 
	BATTLEAXE(Statics.kwWeapTypeBattleaxePerMa, Statics.kwWeaponSchoolHeavyWeaponry);
	
	private FormID relatedSpecificType;
	private FormID relatedWeaponSchool;
	
	private BaseWeaponTypes(FormID relatedSpecificType, FormID relatedWeaponSchool){
		this.relatedSpecificType = relatedSpecificType;
		this.relatedWeaponSchool = relatedWeaponSchool;
	}

	public FormID getRelatedSpecificType() {
		return relatedSpecificType;
	}

	public FormID getRelatedWeaponSchool() {
		return relatedWeaponSchool;
	}

}
