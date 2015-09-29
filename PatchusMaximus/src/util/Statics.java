package util;

import java.util.ArrayList;

import skyproc.ENCH;
import skyproc.FormID;
import skyproc.LVLI;
import skyproc.Mod;
import skyproc.SPGlobal;

/**
 * Container full of loaded FormIDs that are used across multiple patchers
 * 
 * @author T3nd0
 * 
 */
public class Statics {

	// /////////////
	// FILE PATHS
	// /////////////

	public static final String S_MASTER = "PerkusMaximus_Master.esp";
	public static final String S_SKYRIM = "Skyrim.esm";
	public static final String S_DAWNGUARD = "Dawnguard.esm";
	public static final String S_DRAGONBORN = "Dragonborn.esm";

	// /////////////
	// FORM LISTS
	// /////////////

	public static FormID formListTwoHandedSpells = new FormID("5f5dde",
			S_MASTER);
	public static FormID formListConcentrationSpells = new FormID("5faee9",
			S_MASTER);
	public static FormID formListSpellbinderExcludedSpells = new FormID(
			"06129b", S_MASTER);
	public static FormID formListAoEDestructionSpells = new FormID("5f5de0",
			S_MASTER);

	public static FormID formListSpellsAlteration = new FormID("5e19c6",
			S_MASTER);
	public static FormID formListSpellsConjuration = new FormID("5e19ca",
			S_MASTER);
	public static FormID formListSpellsDestruction = new FormID("5e19cb",
			S_MASTER);
	public static FormID formListSpellsIllusion = new FormID("5e19cc", S_MASTER);
	public static FormID formListSpellsRestoration = new FormID("5e19cd",
			S_MASTER);

	// /////////////
	// PERKS
	// /////////////

	// PERKUS MAXIMUS MASTER
	public static FormID perkLIASecureGrip = new FormID("3960f9", S_MASTER);

	public static FormID perkSmithingMasteryWarforged = new FormID("619535",
			S_MASTER);
	public static FormID perkSmithingLeather = new FormID("1d8be6", S_MASTER);
	public static FormID perkSmithingSilver = new FormID("0a82a6", S_MASTER);
	public static FormID perkSmithingMeltdown = new FormID("0a82a5", S_MASTER);
	public static FormID perkSmithingSilverRefined = new FormID("054ff5",
			S_MASTER);

	public static FormID perkSmithingArmorer = new FormID("36d874", S_MASTER);
	public static FormID perkSmithingWeaponsmith = new FormID("36d873",
			S_MASTER);
	public static FormID perkSmithingCopycat = new FormID("36d875", S_MASTER);

	public static FormID perkEnchantingStafffaire = new FormID("1bef1e",
			S_MASTER);
	public static FormID perkEnchantingBasicScripture = new FormID("2888a7",
			S_MASTER);
	public static FormID perkEnchantingAdvancedScripture = new FormID("28b045",
			S_MASTER);
	public static FormID perkEnchantingElaborateScripture = new FormID(
			"28b046", S_MASTER);
	public static FormID perkEnchantingSagesScripture = new FormID("28d80f",
			S_MASTER);

	public static FormID perkWeaponSilverRefined = new FormID("289911",
			S_MASTER);

	public static FormID perkRangedWeaponryAspiringEngineer0 = new FormID(
			"3aa555", S_MASTER);
	public static FormID perkRangedWeaponryAspiringEngineer1 = new FormID(
			"3aa556", S_MASTER);
	public static FormID perkRangedWeaponryProficientEngineer0 = new FormID(
			"3aa557", S_MASTER);
	public static FormID perkRangedWeaponryProficientEngineer1 = new FormID(
			"3aa558", S_MASTER);
	public static FormID perkRangedWeaponryCrossbowTechnician = new FormID(
			"3aa559", S_MASTER);
	public static FormID perkRangedWeaponryAdvancedMissilecraft0 = new FormID(
			"28ea45", S_MASTER);
	public static FormID perkRangedWeaponryAdvancedMissilecraft1 = new FormID(
			"28ea48", S_MASTER);

	public static FormID perkAlchemyAdvancedExplosives = new FormID("00fed9",
			S_MASTER);
	public static FormID perkAlchemyFuse = new FormID("1088fd", S_MASTER);
	public static FormID perkAlchemyElementalBombard = new FormID("1088fa",
			S_MASTER);
	public static FormID perkAlchemySkilledEnhancer0 = new FormID("1088ef",
			S_MASTER);
	public static FormID perkAlchemySkilledEnhancer1 = new FormID("1088f0",
			S_MASTER);
	public static FormID perkAlchemyPoisonBurst = new FormID("1a2404", S_MASTER);

	public static FormID perkSneakThiefsToolbox0 = new FormID("289914",
			S_MASTER);
	public static FormID perkSneakThiefsToolbox1 = new FormID("289915",
			S_MASTER);

	public static FormID kwBoundWeapon = new FormID("5e19c2", S_MASTER);

	// module specific perks

	public static FormID perkWarriorModuleScalingCritDamage = new FormID(
			"05c161", S_MASTER);
	public static FormID perkWarriorModuleTrapAwareness = new FormID("0fe6d2",
			S_MASTER);
	public static FormID perkMageModuleScalingSpells = new FormID("05c15f",
			S_MASTER);
	public static FormID perkWarriorModuleDualWieldMalus = new FormID("386dc1",
			S_MASTER);
	public static FormID perkWarriorModuleScarredPassive = new FormID("36876d",
			S_MASTER);
	public static FormID perkWarriorModuleFistScaling = new FormID("386dd1",
			S_MASTER);
	public static FormID perkWarriorModulePassiveCrossbow = new FormID(
			"405888", S_MASTER);

	public static FormID perkMageModuleScalingScrolls = new FormID("117c28",
			S_MASTER);
	public static FormID perkMageModulePassives = new FormID("641d7c", S_MASTER);

	public static FormID perkThiefModuleFingersmithXP = new FormID("112b24",
			S_MASTER);
	public static FormID perkThiefModuleSpellSneak = new FormID("037d38",
			S_MASTER);
	public static FormID perkThiefModulePassiveArmorSneakPenalty = new FormID(
			"29ddd7", S_MASTER);
	public static FormID perkThiefModulePassiveShoutScaling = new FormID(
			"2a8006", S_MASTER);
	public static FormID perkThiefModuleWeaponSneakScaling = new FormID(
			"29ddd5", S_MASTER);

	public static FormID perkAlchemySkillBoosts = new FormID("0a725c", S_SKYRIM);

	// Skyrim.esm smithing perks

	public static FormID perkSmithingArcaneBlacksmith = new FormID("05218e",
			S_SKYRIM);
	public static FormID perkSmithingSteel = new FormID("0cb40d", S_SKYRIM);
	public static FormID perkSmithingDwarven = new FormID("0cb40e", S_SKYRIM);
	public static FormID perkSmithingOrcish = new FormID("0cb410", S_SKYRIM);
	public static FormID perkSmithingEbony = new FormID("0cb412", S_SKYRIM);
	public static FormID perkSmithingDaedric = new FormID("0cb413", S_SKYRIM);

	public static FormID perkSmithingElven = new FormID("0cb40f", S_SKYRIM);
	public static FormID perkSmithingAdvanced = new FormID("0cb414", S_SKYRIM);
	public static FormID perkSmithingGlass = new FormID("0cb411", S_SKYRIM);
	public static FormID perkSmithingDragon = new FormID("052190", S_SKYRIM);

	// ingots

	public static FormID ingotDwarven = new FormID("0db8a2", S_SKYRIM);
	public static FormID ingotMalachite = new FormID("05ada1", S_SKYRIM);
	public static FormID ingotQuicksilver = new FormID("05ada0", S_SKYRIM);
	public static FormID ingotMoonstone = new FormID("05ad9f", S_SKYRIM);
	public static FormID ingotGold = new FormID("05ad9e", S_SKYRIM);
	public static FormID ingotEbony = new FormID("05ad9d", S_SKYRIM);
	public static FormID ingotOrichalcum = new FormID("05ad99", S_SKYRIM);
	public static FormID ingotCorundum = new FormID("05ad93", S_SKYRIM);
	public static FormID ingotSteel = new FormID("05ace5", S_SKYRIM);
	public static FormID ingotIron = new FormID("05ace4", S_SKYRIM);
	public static FormID ingotSilver = new FormID("05ace3", S_SKYRIM);

	// other crafting stuff

	public static FormID charcoal = new FormID("033760", S_SKYRIM);
	public static FormID leatherStrips = new FormID("0800e4", S_SKYRIM);
	public static FormID chaurusChitin = new FormID("03ad57", S_SKYRIM);
	public static FormID dragonbone = new FormID("03ada4", S_SKYRIM);
	public static FormID dragonscale = new FormID("03ada3", S_SKYRIM);
	public static FormID firewood = new FormID("06f993", S_SKYRIM);
	public static FormID fireSalt = new FormID("03ad5e", S_SKYRIM);
	public static FormID frostSalt = new FormID("03ad5f", S_SKYRIM);
	public static FormID voidSalt = new FormID("03ad60", S_SKYRIM);
	public static FormID pettySoulGem = new FormID("02e4e2", S_SKYRIM);
	public static FormID torchbugThorax = new FormID("04da73", S_SKYRIM);
	public static FormID ale = new FormID("034c5e", S_SKYRIM);
	public static FormID deathBell = new FormID("0516c8", S_SKYRIM);
	public static FormID boneMeal = new FormID("034cdd", S_SKYRIM);
	public static FormID leather = new FormID("0db5d2", S_SKYRIM);
	public static FormID inkwell = new FormID("04c3c6", S_SKYRIM);
	public static FormID paperroll = new FormID("033761", S_SKYRIM);

	public static FormID netchLeather = new FormID("01cd7c", S_DRAGONBORN);
	public static FormID oreStalhrim = new FormID("02b06b", S_DRAGONBORN);
	public static FormID heartStone = new FormID("017749", S_DRAGONBORN);

	public static FormID emptyStaffDestruction = new FormID("0be11f", S_SKYRIM);
	public static FormID emptyStaffConjuration = new FormID("07e647", S_SKYRIM);
	public static FormID emptyStaffAlteration = new FormID("07e646", S_SKYRIM);
	public static FormID emptyStaffIllusion = new FormID("07a91b", S_SKYRIM);
	public static FormID emptyStaffRestoration = new FormID("051b0c", S_SKYRIM);

	public static FormID emptyScroll = new FormID("428fe2", S_MASTER);

	public static FormID quill = new FormID("04c3c8", S_SKYRIM);

	public static FormID artifactEssence = new FormID("4331e5", S_MASTER);

	public static FormID crossbowModificationKit = new FormID("43d417",
			S_MASTER);

	public static FormID qualityLeather = new FormID("44761d", S_MASTER);
	public static FormID qualityLeatherStrips = new FormID("44761e", S_MASTER);

	// /////////////
	// KEYWORDS
	// /////////////

	public static FormID kwMagicDisallowEnchanting = new FormID("0c27bd",
			S_SKYRIM);

	public static FormID kwArmorJewelry = new FormID("06bbe9", S_SKYRIM);
	public static FormID kwJewelryExpensive = new FormID("0a8664", S_SKYRIM);
	public static FormID kwVendorItemJewelry = new FormID("08F95A", S_SKYRIM);
	public static FormID kwClothingRing = new FormID("10cd09", S_SKYRIM);
	public static FormID kwClothingNecklace = new FormID("10cd0a", S_SKYRIM);

	public static FormID kwWeapTypeBow = new FormID("01e715", S_SKYRIM);
	public static FormID kwWeapTypeDagger = new FormID("01e713", S_SKYRIM);
	public static FormID kwWeapTypeSword = new FormID("01e711", S_SKYRIM);
	public static FormID kwWeapTypeMace = new FormID("01e714", S_SKYRIM);
	public static FormID kwWeapTypeWaraxe = new FormID("01e712", S_SKYRIM);
	public static FormID kwWeapTypeWarhammer = new FormID("06d930", S_SKYRIM);
	public static FormID kwWeapTypeBattleaxe = new FormID("06d932", S_SKYRIM);
	public static FormID kwWeapTypeGreatsword = new FormID("06d931", S_SKYRIM);
	public static FormID kwWeapTypeStaff = new FormID("01e716", S_SKYRIM);

	public static FormID kwArmorMaterialDaedric = new FormID("06bbd4", S_SKYRIM);
	public static FormID kwArmorMaterialDragonplate = new FormID("06bbd5",
			S_SKYRIM);
	public static FormID kwArmorMaterialDragonscale = new FormID("06bbd6",
			S_SKYRIM);
	public static FormID kwArmorMaterialDwarven = new FormID("06bbd7", S_SKYRIM);
	public static FormID kwArmorMaterialEbony = new FormID("06bbd8", S_SKYRIM);
	public static FormID kwArmorMaterialElven = new FormID("06bbd9", S_SKYRIM);
	public static FormID kwArmorMaterialElvenGilded = new FormID("06bbda",
			S_SKYRIM);
	public static FormID kwArmorMaterialGlass = new FormID("06bbdc", S_SKYRIM);
	public static FormID kwArmorMaterialHide = new FormID("06bbdd", S_SKYRIM);
	public static FormID kwArmorMaterialImperialHeavy = new FormID("06bbe2",
			S_SKYRIM);
	public static FormID kwArmorMaterialImperialLight = new FormID("06bbe0",
			S_SKYRIM);
	public static FormID kwArmorMaterialImperialStudded = new FormID("06bbe1",
			S_SKYRIM);
	public static FormID kwArmorMaterialIron = new FormID("06bbe3", S_SKYRIM);
	public static FormID kwArmorMaterialIronBanded = new FormID("06bbe4",
			S_SKYRIM);
	public static FormID kwArmorMaterialLeather = new FormID("06bbdb", S_SKYRIM);
	public static FormID kwArmorMaterialOrcish = new FormID("06bbe5", S_SKYRIM);
	public static FormID kwArmorMaterialScaled = new FormID("06bbde", S_SKYRIM);
	public static FormID kwArmorMaterialSteel = new FormID("06bbe6", S_SKYRIM);
	public static FormID kwArmorMaterialSteelPlate = new FormID("06bbe7",
			S_SKYRIM);
	public static FormID kwArmorMaterialStormcloak = new FormID("0ac13a",
			S_SKYRIM);
	public static FormID kwArmorMaterialStudded = new FormID("06bbdf", S_SKYRIM);
	public static FormID kwArmorMaterialDarkBrotherhood = new FormID("10fd62",
			S_SKYRIM);
	public static FormID kwArmorMaterialNightingale = new FormID("10fd61",
			S_SKYRIM);

	public static FormID kwDaedricArtifact = new FormID("0a8668", S_SKYRIM);
	public static FormID kwSmithingWarforgedArmor = new FormID("619547",
			S_MASTER);
	public static FormID kwSmithingWarforgedWeapon = new FormID("619546",
			S_MASTER);

	// weapon materials

	public static FormID kwWeapMaterialSilver = new FormID("10aa1a", S_SKYRIM);
	public static FormID kwWeapMaterialFalmerHoned = new FormID("0c5c04",
			S_SKYRIM);
	public static FormID kwWeapMaterialFalmer = new FormID("0c5c03", S_SKYRIM);
	public static FormID kwWeapMaterialDraugrHoned = new FormID("0c5c02",
			S_SKYRIM);
	public static FormID kwWeapMaterialDraugr = new FormID("0c5c01", S_SKYRIM);
	public static FormID kwWeapMaterialImperial = new FormID("0c5c00", S_SKYRIM);
	public static FormID kwWeapMaterialDaedric = new FormID("01e71f", S_SKYRIM);
	public static FormID kwWeapMaterialEbony = new FormID("01e71e", S_SKYRIM);
	public static FormID kwWeapMaterialGlass = new FormID("01e71d", S_SKYRIM);
	public static FormID kwWeapMaterialOrcish = new FormID("01e71c", S_SKYRIM);
	public static FormID kwWeapMaterialElven = new FormID("01e71b", S_SKYRIM);
	public static FormID kwWeapMaterialDwarven = new FormID("01e71a", S_SKYRIM);
	public static FormID kwWeapMaterialSteel = new FormID("01e719", S_SKYRIM);
	public static FormID kwWeapMaterialIron = new FormID("01e718", S_SKYRIM);
	public static FormID kwWeapMaterialWood = new FormID("01e717", S_SKYRIM);

	// crafting stations

	public static FormID kwCraftingTanningRack = new FormID("07866a", S_SKYRIM);
	public static FormID kwCraftingSmithingSharpeningWheel = new FormID(
			"088108", S_SKYRIM);
	public static FormID kwCraftingSmithingForge = new FormID("088105",
			S_SKYRIM);
	public static FormID kwCraftingSmithingArmorTable = new FormID("0adb78",
			S_SKYRIM);
	public static FormID kwCraftingSmelter = new FormID("00a5cce", S_SKYRIM);
	public static FormID kwCraftingCookpot = new FormID("0a5cb3", S_SKYRIM);

	// vanilla armor slot keywords

	public static FormID kwArmorSlotGauntlets = new FormID("06c0ef", S_SKYRIM);
	public static FormID kwArmorSlotHelmet = new FormID("06c0ee", S_SKYRIM);
	public static FormID kwArmorSlotBoots = new FormID("06c0ed", S_SKYRIM);
	public static FormID kwArmorSlotCuirass = new FormID("06c0ec", S_SKYRIM);
	public static FormID kwArmorSlotShield = new FormID("0965b2", S_SKYRIM);

	public static FormID kwArmorLight = new FormID("06bbd3", S_SKYRIM);
	public static FormID kwArmorHeavy = new FormID("06bbd2", S_SKYRIM);

	// clothing

	public static FormID kwVendorItemClothing = new FormID("08f95b", S_SKYRIM);
	public static FormID kwArmorClothing = new FormID("06bbe8", S_SKYRIM);
	public static FormID kwClothingHands = new FormID("10cd13", S_SKYRIM);
	public static FormID kwClothingHead = new FormID("10cd11", S_SKYRIM);
	public static FormID kwClothingFeet = new FormID("10cd12", S_SKYRIM);
	public static FormID kwClothingBody = new FormID("0a8657", S_SKYRIM);
	public static FormID kwClothingCirclet = new FormID("10CD08", S_SKYRIM);
	public static FormID kwClothingPoor = new FormID("0a865c", S_SKYRIM);
	public static FormID kwClothingRich = new FormID("10f95b", S_SKYRIM);

	public static FormID kwArmorMaterialDawnguard = new FormID("012ccd",
			S_DAWNGUARD);
	public static FormID kwArmorMaterialFalmerHardened = new FormID("021cce",
			S_DAWNGUARD);
	public static FormID kwArmorMaterialFalmerHeavy = new FormID("021ccf",
			S_DAWNGUARD);
	public static FormID kwArmorMaterialFalmerHeavyOriginal = new FormID(
			"012cd0", S_DAWNGUARD);
	public static FormID kwArmorMaterialHunter = new FormID("0050c4",
			S_DAWNGUARD);
	public static FormID kwArmorMaterialVampire = new FormID("01463e",
			S_DAWNGUARD);

	public static FormID kwWeapMaterialDragonbone = new FormID("019822",
			S_DAWNGUARD);

	public static FormID kwArmorMaterialBonemoldHeavy = new FormID("024101",
			S_DRAGONBORN);
	public static FormID kwArmorMaterialStalhrimHeavy = new FormID("024106",
			S_DRAGONBORN);
	public static FormID kwArmorMaterialStalhrimLight = new FormID("024107",
			S_DRAGONBORN);
	public static FormID kwArmorMaterialNordicHeavy = new FormID("024105",
			S_DRAGONBORN);

	public static FormID kwWeapMaterialStalhrim = new FormID("02622f",
			S_DRAGONBORN);
	public static FormID kwWeapMaterialNordic = new FormID("026230",
			S_DRAGONBORN);
	public static FormID kwCraftingStaff = new FormID("017738", S_DRAGONBORN);

	public static FormID kwWeapMaterialSilverRefined = new FormID("289912",
			S_MASTER);

	public static FormID kwCraftingScroll = new FormID("10da22", S_MASTER);

	public static FormID kwWeapTypeArmingSword = new FormID("098eef", S_MASTER);
	public static FormID kwWeapTypeBastardSword = new FormID("01006f", S_MASTER);
	public static FormID kwWeapTypeBattleaxePerMa = new FormID("0d5e61",
			S_MASTER);
	public static FormID kwWeapTypeBattlestaff = new FormID("010071", S_MASTER);
	public static FormID kwWeapTypeClub = new FormID("010079", S_MASTER);
	public static FormID kwWeapTypeDaggerPerMa = new FormID("27a600", S_MASTER);
	public static FormID kwWeapTypePartisan = new FormID("01006c", S_MASTER);
	public static FormID kwWeapTypeGreatswordPerMa = new FormID("0d5e63",
			S_MASTER);
	public static FormID kwWeapTypeHalberd = new FormID("01006d", S_MASTER);
	public static FormID kwWeapTypeKatana = new FormID("010076", S_MASTER);
	public static FormID kwWeapTypeLongmace = new FormID("010070", S_MASTER);
	public static FormID kwWeapTypeLongsword = new FormID("010075", S_MASTER);
	public static FormID kwWeapTypeNodachi = new FormID("01006e", S_MASTER);
	public static FormID kwWeapTypeScimitar = new FormID("2703f4", S_MASTER);
	public static FormID kwWeapTypeSaber = new FormID("2703f5", S_MASTER);
	public static FormID kwWeapTypeShortspear = new FormID("010078", S_MASTER);
	public static FormID kwWeapTypeShortsword = new FormID("010074", S_MASTER);
	public static FormID kwWeapTypeTanto = new FormID("01007a", S_MASTER);
	public static FormID kwWeapTypeWakizashi = new FormID("010077", S_MASTER);
	public static FormID kwWeapTypeMacePerMa = new FormID("0d5e62", S_MASTER);
	public static FormID kwWeapTypeMaul = new FormID("2703f6", S_MASTER);
	public static FormID kwWeapTypeHatchet = new FormID("2703f7", S_MASTER);
	public static FormID kwWeapTypeWarhammerPerMa = new FormID("0d5e60",
			S_MASTER);
	public static FormID kwWeapTypeWaraxePerMa = new FormID("0d5e5f", S_MASTER);
	// TODO remove
	public static FormID kwWeapTypeFist = new FormID("368770", S_MASTER);
	public static FormID kwWeapTypeClaw = new FormID("558d87", S_MASTER);
	public static FormID kwWeapTypeKatar = new FormID("558d89", S_MASTER);
	public static FormID kwWeapTypeKnuckles = new FormID("558d88", S_MASTER);

	public static FormID kwWeapTypeCrossbow = new FormID("010073", S_MASTER);
	public static FormID kwWeapTypeLongbow = new FormID("010072", S_MASTER);
	public static FormID kwWeapTypeShortbow = new FormID("098ef0", S_MASTER);

	// crossbow variants

	public static FormID kwCrossbowArbalest = new FormID("40588a", S_MASTER);
	public static FormID kwCrossbowRecurve = new FormID("40588b", S_MASTER);
	public static FormID kwCrossbowSilenced = new FormID("405889", S_MASTER);
	public static FormID kwCrossbowLightweight = new FormID("438308", S_MASTER);

	// armor slots

	public static FormID kwArmorLightShield = new FormID("2703f2", S_MASTER);
	public static FormID kwArmorLightHead = new FormID("0a3180", S_MASTER);
	public static FormID kwArmorLightArms = new FormID("0a317a", S_MASTER);
	public static FormID kwArmorLightChest = new FormID("0a317c", S_MASTER);
	public static FormID kwArmorLightLegs = new FormID("0a317d", S_MASTER);

	public static FormID kwArmorHeavyShield = new FormID("0a317e", S_MASTER);
	public static FormID kwArmorHeavyHead = new FormID("0a317f", S_MASTER);
	public static FormID kwArmorHeavyArms = new FormID("0a3179", S_MASTER);
	public static FormID kwArmorHeavyChest = new FormID("0a317b", S_MASTER);
	public static FormID kwArmorHeavyLegs = new FormID("2703f3", S_MASTER);

	// weapon class

	public static FormID kwWeaponClassBlade = new FormID("0d5e65", S_MASTER);
	public static FormID kwWeaponClassBlunt = new FormID("0d5e64", S_MASTER);
	public static FormID kwWeaponClassPiercing = new FormID("1e7769", S_MASTER);

	// weapon school

	public static FormID kwWeaponSchoolLightWeaponry = new FormID("2b222c",
			S_MASTER);
	public static FormID kwWeaponSchoolHeavyWeaponry = new FormID("2b222d",
			S_MASTER);
	public static FormID kwWeaponSchoolRangedWeaponry = new FormID("2b222e",
			S_MASTER);

	// stagger, bleed, debuff

	public static FormID kwWeaponStaggerTier1 = new FormID("1599f1", S_MASTER);
	public static FormID kwWeaponStaggerTier2 = new FormID("1599f2", S_MASTER);
	public static FormID kwWeaponStaggerTier3 = new FormID("1599f3", S_MASTER);

	public static FormID kwWeaponDebuffTier1 = new FormID("1599f5", S_MASTER);
	public static FormID kwWeaponDebuffTier2 = new FormID("1599f6", S_MASTER);
	public static FormID kwWeaponDebuffTier3 = new FormID("1599f7", S_MASTER);

	public static FormID kwWeaponBleedTier1 = new FormID("1599eb", S_MASTER);
	public static FormID kwWeaponBleedTier2 = new FormID("1599ec", S_MASTER);
	public static FormID kwWeaponBleedTier3 = new FormID("1599ed", S_MASTER);

	// masquerade

	public static FormID kwMasqueradeForsworn = new FormID("3125ae", S_MASTER);
	public static FormID kwMasqueradeThalmor = new FormID("3125b0", S_MASTER);
	public static FormID kwMasqueradeBandit = new FormID("3f143a", S_MASTER);
	public static FormID kwMasqueradeImperial = new FormID("3125af", S_MASTER);
	public static FormID kwMasqueradeStormcloak = new FormID("3125b2", S_MASTER);

	public static FormID kwMasqueradeCultist = new FormID("3125b5", S_MASTER);
	public static FormID kwMasqueradeDawnguard = new FormID("3125b4", S_MASTER);
	public static FormID kwMasqueradeFalmer = new FormID("3125b1", S_MASTER);
	public static FormID kwMasqueradeVampire = new FormID("3125b3", S_MASTER);

	// random
	public static FormID kwActorTypeNPC = new FormID("013794", S_SKYRIM);
	public static FormID kwScrollSpell = new FormID("28b047", S_MASTER);

	public static FormID kwShoutEffect = new FormID("046b99", S_SKYRIM);
	public static FormID kwShoutHarmful = new FormID("2a8008", S_MASTER);
	public static FormID kwShoutNonHarmful = new FormID("3125ad", S_MASTER);
	public static FormID kwShoutSummoning = new FormID("2a800b", S_MASTER);

	public static FormID kwMagicDisarm = new FormID("3960f8", S_MASTER);

	// /////////////
	// SPELLS AND ABILITIES
	// /////////////

	public static FormID spellFlamesOld = new FormID("012fcd", S_SKYRIM);
	public static FormID spellHealingOld = new FormID("012fcc", S_SKYRIM);
	public static FormID spellFuryOld = new FormID("04deeb", S_SKYRIM);
	public static FormID spellSparksOld = new FormID("02dd2a", S_SKYRIM);
	public static FormID spellConjureFamiliarOld = new FormID("0640b6",
			S_SKYRIM);

	public static FormID spellFlamesNew = new FormID("08ecb5", S_MASTER);
	public static FormID spellRecoveryNew = new FormID("0338a5", S_MASTER);
	public static FormID spellSparksNew = new FormID("08ecc0", S_MASTER);
	public static FormID spellFearNew = new FormID("09e071", S_MASTER);
	public static FormID spellConjureWeakFlameAtronachNew = new FormID(
			"205e17", S_MASTER);

	public static FormID spellWarriorModuleMain = new FormID("29ddce", S_MASTER);
	public static FormID spellWarriorModuleStamine = new FormID("052f8d",
			S_MASTER);
	public static FormID spellWarriorModuleTimedBlocking = new FormID("35431c",
			S_MASTER);
	public static FormID spellWarriorModuleShieldDetector = new FormID(
			"354329", S_MASTER);

	public static FormID spellThiefModuleCombatAbility = new FormID("386dd0",
			S_MASTER);
	public static FormID spellWarriorThiefModulePenaltyLight = new FormID(
			"36d877", S_MASTER);
	public static FormID spellWarriorThiefModulePenaltyHeavy = new FormID(
			"36d879", S_MASTER);
	public static FormID spellThiefModuleMain = new FormID("4bbd95", S_MASTER);
	public static FormID spellThiefModuleInitSneakTools = new FormID("4ee7e4",
			S_MASTER);

	public static FormID spellMageModuleMain = new FormID("549a7d", S_MASTER);

	public static FormID spellSpeedFix = new FormID("48e461", S_MASTER);

	// /////////////
	// EXPLOSIONS
	// /////////////

	public static FormID expElementalFrost = new FormID("405886", S_MASTER);
	public static FormID expElementalFire = new FormID("405887", S_MASTER);
	public static FormID expElementalShock = new FormID("405885", S_MASTER);
	public static FormID expExploding = new FormID("40587e", S_MASTER);
	public static FormID expTimebomb = new FormID("40587f", S_MASTER);
	public static FormID expBarbed = new FormID("405881", S_MASTER);
	public static FormID expNoisemaker = new FormID("08d24a", S_MASTER);
	public static FormID expPoison = new FormID("73f060", S_MASTER);

	// /////////////
	// LIGHTS
	// /////////////

	public static FormID lightLightsource = new FormID("28ea50", S_MASTER);

	// /////////////
	// ENCHANTMENTS
	// /////////////

	public static FormID enchStaffEmpty = new FormID("27f702", S_MASTER);
	public static FormID enchSmithingWarforgedWeapon = new FormID("61e64b",
			S_MASTER);
	public static FormID enchSmithingWarforgedArmor = new FormID("61e64c",
			S_MASTER);

	// /////////////
	// EQUIP SLOTS
	// /////////////

	public static FormID equipSlotBothHands = new FormID("013f45", S_SKYRIM);

	// /////////////
	// GAME SETTING EDIDs
	// /////////////

	public static String gmstfArmorScalingFactor = "fArmorScalingFactor";
	public static String gmstfMaxArmorRating = "fMaxArmorRating";
	public static String gmstfArmorRatingPCMax = "fArmorRatingPCMax";
	public static String gmstfArmorRatingMax = "fArmorRatingMax";

	// /////////////
	// GLOBAL VARIABLES
	// /////////////

	public static FormID globUseMage = new FormID("3176ca", S_MASTER);
	public static FormID globUseWarrior = new FormID("3176c9", S_MASTER);
	public static FormID globUseThief = new FormID("3176cb", S_MASTER);
	public static FormID globShoutExpBase = new FormID("44251b", S_MASTER);

	// /////////////
	// QUESTS
	// /////////////

	public static FormID questMain = new FormID("03372b", Statics.S_SKYRIM);

	// /////////////
	// ACTORS
	// /////////////

	public static FormID player = new FormID("000007", Statics.S_SKYRIM);
	public static FormID playerref = new FormID("000014", Statics.S_SKYRIM);

	// Some strings for consistency

	public static final String S_PREFIX_PATCHER = "PaMa_";
	public static final String S_PREFIX_MELTDOWN = "MELTDOWN_";
	public static final String S_PREFIX_CRAFTING = "CRAFT_";
	public static final String S_PREFIX_TEMPER = "TEMPER_";
	public static final String S_PREFIX_WEAPON = "WEAP_";
	public static final String S_PREFIX_ARMOR = "ARMO_";
	public static final String S_PREFIX_AMMUNITION = "AMMO_";
	public static final String S_PREFIX_CLOTHING = "CLOTH_";
	public static final String S_PREFIX_PROJECTILE = "PROJ_";
	public static final String S_PREFIX_ENCHANTMENT = "ENCH_";
	public static final String S_PREFIX_MAGICEFFECT = "MGEF_";
	public static final String S_PREFIX_STAFF = "STAFF_";
	public static final String S_PREFIX_SCROLL = "SCRO_";
	public static final String S_PREFIX_BOOK = "BOOK_";
	public static final String S_PREFIX_LVLI = "LVLI_";

	public static final String S_CROSSBOW_RECURVE = "Recurve";
	public static final String S_CROSSBOW_ARBALEST = "Arbalest";
	public static final String S_CROSSBOW_SILENCED = "Silenced";
	public static final String S_CROSSBOW_LIGHTWEIGHT = "Lightweight";

	public static final String S_AMMO_STRONG = "Strong";
	public static final String S_AMMO_STRONGEST = "Strongest";
	public static final String S_AMMO_EXPLOSIVE = "Explosive";
	public static final String S_AMMO_TIMEBOMB = "Timebomb";
	public static final String S_AMMO_FROST = "Frost";
	public static final String S_AMMO_FIRE = "Fire";
	public static final String S_AMMO_SHOCK = "Shock";
	public static final String S_AMMO_BARBED = "Barbed";
	public static final String S_AMMO_LIGHTSOURCE = "Lightsource";
	public static final String S_AMMO_NOISEMAKER = "Noisemaker";
	public static final String S_AMMO_POISON = "Poisoned";

	public static final String S_WEAPON_CROSSBOW_DESC = "Ignores 50% armor.";
	public static final String S_WEAPON_CROSSBOW_ARBALEST_DESC = "Deals double damage against blocking enemies, but fires slower.";
	public static final String S_WEAPON_CROSSBOW_SILENCED_DESC = "Deals increased sneak attack damage.";
	public static final String S_WEAPON_CROSSBOW_LIGHTWEIGHT_DESC = "Has increased attack speed.";
	public static final String S_WEAPON_CROSSBOW_RECURVE_DESC = "Deals additional damage.";

	public static final String S_WEAPON_REFINED_DESC = "Deals more bonus damage to undead, and is easier to handle than regular silver weapons.";

	public static final String S_SCRIPT_APPLYPERK = "xMAAddPerkWhileEquipped";
	public static final String S_SCRIPT_APPLYPERK_PROPERTY = "p";

	public static final String S_SCRIPT_SILVERSWORD = "SilverSwordScript";
	public static final String S_SCRIPT_SILVERSWORD_PROPERTY = "SilverPerk";

	public static final String S_SCRIPT_SHOUTEXP = "xMATHIShoutExpScript";
	public static final String S_SCRIPT_SHOUTEXP_PROPERTY_0 = "xMATHIShoutExpBase";
	public static final String S_SCRIPT_SHOUTEXP_PROPERTY_1 = "playerref";
	public static final String S_SCRIPT_SHOUTEXP_PROPERTY_2 = "expFactor";

	public static final String S_AMMO_EXPLOSIVE_DESC = "Explodes upon impact, dealing 60 points of non-elemental damage.";
	public static final String S_AMMO_TIMEBOMB_DESC = "Explodes 3 seconds after being fired into a surface, dealing 150 points of non-elemental damage.";
	public static final String S_AMMO_BARBED_DESC = "Deals 6 points of bleeding damag per second over 8 seconds, and slows the target down by 20%.";
	public static final String S_AMMO_FROST_DESC = "Explodes upon impact, dealing 30 points of frost damage.";
	public static final String S_AMMO_FIRE_DESC = "Explodes upon impact, dealing 30 points of fire damage.";
	public static final String S_AMMO_SHOCK_DESC = "Explodes upon impact, dealing 30 points of shock damage.";
	public static final String S_AMMO_LIGHTSOURCE_DESC = "Emits light after being fired.";
	public static final String S_AMMO_NOISEMAKER_DESC = "Emits sound upon impact, distracting enemies.";
	public static final String S_AMMO_HEAVYWEIGHT_DESC = "Has a 50% increased chance to stagger, and a 25% chance to strike the target down.";
	public static final String S_AMMO_POISON_DESC = "Explodes upon impact, dealing 3 points of poison damage per second for 20 seconds.";

	public static final String S_SCROLL = "Scroll";
	public static final String S_STAFF = "Staff";
	public static final String S_REPLICA = "Replica";
	public static final String S_QUALITY = "Quality";
	public static final String S_DURATION = "Duration";
	public static final String S_SECONDS = "seconds";
	public static final String S_DUR_REPLACE = "<dur>";
	public static final String S_REFORGED = "Reforged";
	public static final String S_WARFORGED = "Warforged";
	public static final String S_SHORTBOW = "Shortbow";
	public static final String S_LONGBOW = "Longbow";
	public static final String S_ENCHANTMENT_DELIMITER = "of";
	public static final int ExpensiveClothingThreshold = 50;

	// array lists for easy checks

	public static ArrayList<FormID> keywordListClothingKeywords = new ArrayList<>();
	public static ArrayList<FormID> keywordListJewelryKeywords = new ArrayList<>();

	// missile crafting stuff
	// TODO move to xml
	public static float timebombTimer = 4.0f;

	public static int enhancementIn = 20;
	public static int enhancementOut = 10;
	public static int enhancementOutSE0 = (int) (enhancementOut * 1.2);
	public static int enhancementOutSE1 = (int) (enhancementOut * 1.4);

	// generated form counter

	private static int formCount = 0;

	/**
	 * Fill array lists
	 */

	public static void initializeAll() {
		initializeArrayLists();
	}

	private static void initializeArrayLists() {
		keywordListClothingKeywords.add(kwClothingBody);
		keywordListClothingKeywords.add(kwClothingHands);
		keywordListClothingKeywords.add(kwClothingFeet);
		keywordListClothingKeywords.add(kwClothingHead);
		keywordListClothingKeywords.add(kwVendorItemClothing);
		keywordListClothingKeywords.add(kwArmorClothing);
		keywordListClothingKeywords.add(kwClothingPoor);
		keywordListClothingKeywords.add(kwClothingRich);

		keywordListJewelryKeywords.add(kwVendorItemJewelry);
		keywordListJewelryKeywords.add(kwJewelryExpensive);
		keywordListJewelryKeywords.add(kwClothingRing);
		keywordListJewelryKeywords.add(kwClothingNecklace);
		keywordListJewelryKeywords.add(kwClothingCirclet);
	}

	// just used on leveled lists now

	public static String getFormCount() {
		return ("_" + formCount++);
	}

	/**
	 * Get a LVLI from a given EDID
	 * 
	 * @param edid
	 * @param merger
	 * @return
	 */
	public static LVLI getLVLIFromEDID(String edid, Mod merger, Mod patch) {

		// first try patch

		for (LVLI l : patch.getLeveledItems()) {
			if (l.getEDID().equalsIgnoreCase(edid)) {
				return l;
			}
		}

		for (LVLI l : merger.getLeveledItems()) {
			if (l.getEDID().equalsIgnoreCase(edid)) {
				return l;
			}
		}

		SPGlobal.log("STATICS", edid + ": getLVLIFromEDID() didn't find LVLI");
		return null;
	}

	/**
	 * Get an ENCH FormID from a given EDID
	 * 
	 * @param edid
	 * @param merger
	 * @return
	 */
	public static FormID getEnchFormIDFromEDID(String edid, Mod merger) {
		for (ENCH e : merger.getEnchantments()) {
			if (e.getEDID().equalsIgnoreCase(edid)) {
				return e.getForm();
			}
		}

		SPGlobal.log("STATICS", edid
				+ ": getEnchFormIDFromEDID() didn't find FormID");
		return null;
	}
}
