package xml.lowLevel.common;

import enums.ExclusionTargets;
import enums.ExclusionTypes;

public class Exclusion {
	private String text;
	private ExclusionTargets target;
	private ExclusionTypes type;

	/**
	 * @return the target
	 */
	public ExclusionTargets getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(ExclusionTargets target) {
		this.target = target;
	}
	/**
	 * @return the type
	 */
	public ExclusionTypes getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ExclusionTypes type) {
		this.type = type;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
