package xml.lowLevel.enchanting;


public class ListEnchantmentBinding {
	private String edidList;
	private EnchantmentReplacers enchantment_replacers;
	private boolean fillListWithSimilars;

	/**
	 * @return the edidList
	 */
	public String getEdidList() {
		return edidList;
	}

	/**
	 * @param edidList
	 *            the edidList to set
	 */
	public void setEdidList(String edidList) {
		this.edidList = edidList;
	}

	/**
	 * @return the fillListWithSimilars
	 */
	public boolean isFillListWithSimilars() {
		return fillListWithSimilars;
	}

	/**
	 * @param fillListWithSimilars
	 *            the fillListWithSimilars to set
	 */
	public void setFillListWithSimilars(boolean fillListWithSimilars) {
		this.fillListWithSimilars = fillListWithSimilars;
	}

	/**
	 * @return the enchantment_replacers
	 */
	public EnchantmentReplacers getEnchantment_replacers() {
		return enchantment_replacers;
	}

	/**
	 * @param enchantment_replacers the enchantment_replacers to set
	 */
	public void setEnchantment_replacers(EnchantmentReplacers enchantment_replacers) {
		this.enchantment_replacers = enchantment_replacers;
	}
}
