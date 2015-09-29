package xml.lowLevel.armor;

public class GeneralArmorSettings {
	private double armorFactorHands = 1.0;
	private double armorFactorFeet = 1.0;
	private double armorFactorHead = 1.5;
	private double armorFactorBody = 3.0;
	private double armorFactorShield = 1.5;
	private int meltdownOutputHands = 1;
	private int meltdownOutputFeet = 1;
	private int meltdownOutputHead = 1;
	private int meltdownOutputBody = 2;
	private int meltdownOutputShield = 2;
	
	private double protectionPerArmor = 0.1;
	private double maxProtection = 90.0;
	private double armorRatingPCMax = 1.4;
	private double armorRatingMax = 2.5;

	public double getArmorFactorHands() {
		return armorFactorHands;
	}

	public void setArmorFactorHands(double armorFactorHands) {
		this.armorFactorHands = armorFactorHands;
	}

	public double getArmorFactorFeet() {
		return armorFactorFeet;
	}

	public void setArmorFactorFeet(double armorFactorFeet) {
		this.armorFactorFeet = armorFactorFeet;
	}

	public double getArmorFactorHead() {
		return armorFactorHead;
	}

	public void setArmorFactorHead(double armorFactorHead) {
		this.armorFactorHead = armorFactorHead;
	}

	public double getArmorFactorBody() {
		return armorFactorBody;
	}

	public void setArmorFactorBody(double armorFactorBody) {
		this.armorFactorBody = armorFactorBody;
	}

	public double getProtectionPerArmor() {
		return protectionPerArmor;
	}

	public void setProtectionPerArmor(double protectionPerArmor) {
		this.protectionPerArmor = protectionPerArmor;
	}

	public double getMaxProtection() {
		return maxProtection;
	}

	public void setMaxProtection(double maxProtection) {
		this.maxProtection = maxProtection;
	}

	public double getArmorFactorShield() {
		return armorFactorShield;
	}

	public void setArmorFactorShield(double armorFactorShield) {
		this.armorFactorShield = armorFactorShield;
	}

	public int getMeltdownOutputHands() {
		return meltdownOutputHands;
	}

	public void setMeltdownOutputHands(int meltdownOutputHands) {
		this.meltdownOutputHands = meltdownOutputHands;
	}

	public int getMeltdownOutputFeet() {
		return meltdownOutputFeet;
	}

	public void setMeltdownOutputFeet(int meltdownOutputFeet) {
		this.meltdownOutputFeet = meltdownOutputFeet;
	}

	public int getMeltdownOutputHead() {
		return meltdownOutputHead;
	}

	public void setMeltdownOutputHead(int meltdownOutputHead) {
		this.meltdownOutputHead = meltdownOutputHead;
	}

	public int getMeltdownOutputBody() {
		return meltdownOutputBody;
	}

	public void setMeltdownOutputBody(int meltdownOutputBody) {
		this.meltdownOutputBody = meltdownOutputBody;
	}

	public int getMeltdownOutputShield() {
		return meltdownOutputShield;
	}

	public void setMeltdownOutputShield(int meltdownOutputShield) {
		this.meltdownOutputShield = meltdownOutputShield;
	}

	/**
	 * @return the armorRatingPCMax
	 */
	public double getArmorRatingPCMax() {
		return armorRatingPCMax;
	}

	/**
	 * @param armorRatingPCMax the armorRatingPCMax to set
	 */
	public void setArmorRatingPCMax(double armorRatingPCMax) {
		this.armorRatingPCMax = armorRatingPCMax;
	}

	/**
	 * @return the armorRatingMax
	 */
	public double getArmorRatingMax() {
		return armorRatingMax;
	}

	/**
	 * @param armorRatingMax the armorRatingMax to set
	 */
	public void setArmorRatingMax(double armorRatingMax) {
		this.armorRatingMax = armorRatingMax;
	}
}
