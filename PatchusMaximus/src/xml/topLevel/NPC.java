package xml.topLevel;

import javax.xml.bind.annotation.XmlRootElement;

import xml.lowLevel.common.ExclusionListWrapper;

@XmlRootElement(namespace="PatchusMaximus.npcXML")
public class NPC {
	private ExclusionListWrapper npc_exclusions;
	private ExclusionListWrapper race_exclusions;

	/**
	 * @return the npc_exclusions
	 */
	public ExclusionListWrapper getNpc_exclusions() {
		return npc_exclusions;
	}

	/**
	 * @param npc_exclusions the npc_exclusions to set
	 */
	public void setNpc_exclusions(ExclusionListWrapper npc_exclusions) {
		this.npc_exclusions = npc_exclusions;
	}

	/**
	 * @return the race_exclusions
	 */
	public ExclusionListWrapper getRace_exclusions() {
		return race_exclusions;
	}

	/**
	 * @param race_exclusions the race_exclusions to set
	 */
	public void setRace_exclusions(ExclusionListWrapper race_exclusions) {
		this.race_exclusions = race_exclusions;
	}

}
