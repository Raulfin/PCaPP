/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyprocstarter;

import lev.gui.LCheckBox;
import lev.gui.LComboBox;
import skyproc.SPGlobal;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;
import skyproc.gui.SUMGUI;

/**
 *
 * @author Justin Swanson
 */
public class OtherSettingsPanel extends SPSettingPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LCheckBox importOnStartup;
    LComboBox<Enum<?>> language;

    public OtherSettingsPanel(SPMainMenuPanel parent_) {
	super(parent_, "Other Settings", SkyProcStarter.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	importOnStartup = new LCheckBox("Import Mods on Startup", SkyProcStarter.settingsFont, SkyProcStarter.settingsColor);
	importOnStartup.tie(YourSaveFile.Settings.IMPORT_AT_START, SkyProcStarter.save, SUMGUI.helpPanel, true);
	importOnStartup.setOffset(2);
	importOnStartup.addShadow();
	setPlacement(importOnStartup);
	AddSetting(importOnStartup);

	language = new LComboBox<Enum<?>>("Language", SkyProcStarter.settingsFont, SkyProcStarter.settingsColor);
	language.setSize(260, 60);
	for (Enum<?> e : SPGlobal.Language.values()) {
	    language.addItem(e);
	}
	language.tie(YourSaveFile.Settings.LANGUAGE, SkyProcStarter.save, SUMGUI.helpPanel, true);
	setPlacement(language);
	AddSetting(language);

	alignRight();

    }
}
