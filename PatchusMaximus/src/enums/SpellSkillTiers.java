package enums;

public enum SpellSkillTiers {
	NOVICE(0), APPRENTICE(25), ADEPT(50), EXPERT(75), MASTER(100), INVALID(1);
	
	private int skillLevel;
	
	private SpellSkillTiers(int minSkillLevel){
		this.skillLevel = minSkillLevel;
	}
	
	public int getSkillLevel(){
		return this.skillLevel;
	}
	
	public static SpellSkillTiers getTierFromLevel(int level){
		for (SpellSkillTiers t: SpellSkillTiers.values()){
			if(level == t.getSkillLevel()){
				return t;
			}
		}
		
		return INVALID;
	}
}
