package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.alchemy.AlchemyEffects;
import xml.lowLevel.alchemy.IngredientVariations;
import xml.lowLevel.alchemy.PotionMultipliers;
import xml.lowLevel.common.BindingListWrapper;
import xml.lowLevel.common.ExclusionListWrapper;

@XmlRootElement(namespace="PatchusMaximus.alchemyXML")
public class Alchemy {
	private AlchemyEffects alchemy_effects;
	private BindingListWrapper alchemy_effect_bindings;
	private IngredientVariations ingredient_variations;
	private BindingListWrapper ingredient_variation_bindings;
	private PotionMultipliers potion_multipliers;
	private BindingListWrapper potion_multiplier_bindings;
	private ExclusionListWrapper potion_exclusions;
	private ExclusionListWrapper ingredient_exclusions;

	public AlchemyEffects getAlchemy_effects() {
		return alchemy_effects;
	}

	public void setAlchemy_effects(AlchemyEffects alchemy_effects) {
		this.alchemy_effects = alchemy_effects;
	}

	public IngredientVariations getIngredient_variations() {
		return ingredient_variations;
	}

	public void setIngredient_variations(
			IngredientVariations ingredient_variations) {
		this.ingredient_variations = ingredient_variations;
	}

	public PotionMultipliers getPotion_multipliers() {
		return potion_multipliers;
	}

	public void setPotion_multipliers(PotionMultipliers potion_multipliers) {
		this.potion_multipliers = potion_multipliers;
	}

	/**
	 * @return the alchemy_effect_bindings
	 */
	public BindingListWrapper getAlchemy_effect_bindings() {
		return alchemy_effect_bindings;
	}

	/**
	 * @param alchemy_effect_bindings the alchemy_effect_bindings to set
	 */
	public void setAlchemy_effect_bindings(BindingListWrapper alchemy_effect_bindings) {
		this.alchemy_effect_bindings = alchemy_effect_bindings;
	}

	/**
	 * @return the ingredient_variation_bindings
	 */
	public BindingListWrapper getIngredient_variation_bindings() {
		return ingredient_variation_bindings;
	}

	/**
	 * @param ingredient_variation_bindings the ingredient_variation_bindings to set
	 */
	public void setIngredient_variation_bindings(
			BindingListWrapper ingredient_variation_bindings) {
		this.ingredient_variation_bindings = ingredient_variation_bindings;
	}

	/**
	 * @return the potion_multiplier_bindings
	 */
	public BindingListWrapper getPotion_multiplier_bindings() {
		return potion_multiplier_bindings;
	}

	/**
	 * @param potion_multiplier_bindings the potion_multiplier_bindings to set
	 */
	public void setPotion_multiplier_bindings(BindingListWrapper potion_multiplier_bindings) {
		this.potion_multiplier_bindings = potion_multiplier_bindings;
	}

	/**
	 * @return the potion_exclusions
	 */
	public ExclusionListWrapper getPotion_exclusions() {
		return potion_exclusions;
	}

	/**
	 * @param potion_exclusions the potion_exclusions to set
	 */
	public void setPotion_exclusions(ExclusionListWrapper potion_exclusions) {
		this.potion_exclusions = potion_exclusions;
	}

	/**
	 * @return the ingredient_exclusions
	 */
	public ExclusionListWrapper getIngredient_exclusions() {
		return ingredient_exclusions;
	}

	/**
	 * @param ingredient_exclusions the ingredient_exclusions to set
	 */
	public void setIngredient_exclusions(ExclusionListWrapper ingredient_exclusions) {
		this.ingredient_exclusions = ingredient_exclusions;
	}

}
