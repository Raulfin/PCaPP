package xml.access;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import skyproc.SPGlobal;
import xml.topLevel.Alchemy;
import xml.topLevel.Ammunition;
import xml.topLevel.Armor;
import xml.topLevel.Enchanting;
import xml.topLevel.GeneralSettings;
import xml.topLevel.Languages;
import xml.topLevel.LeveledLists;
import xml.topLevel.NPC;
import xml.topLevel.Weapons;

/**
 * Handles import and export of various xml files
 * 
 * @author T3nd0
 * 
 */
public class XmlImportExport {
	public static void writeXmlLanguages(Languages l) throws JAXBException,
			IOException {
		JAXBContext context = JAXBContext.newInstance(Languages.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_LANGUAGES);
			m.marshal(l, w);
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	public static void writeXmlAlchemy(Alchemy a) throws JAXBException,
			IOException {
		JAXBContext context = JAXBContext.newInstance(Alchemy.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_ALCHEMY);
			m.marshal(a, w);
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	public static void writeXmlAmmunition(Ammunition p) throws JAXBException,
			IOException {
		JAXBContext context = JAXBContext.newInstance(Ammunition.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_AMMUNITION);
			m.marshal(p, w);
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	public static void writeXmlEnchanting(Enchanting e) throws JAXBException,
			IOException {
		JAXBContext context = JAXBContext.newInstance(Enchanting.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_ENCHANTING);
			m.marshal(e, w);
		} finally {
			try {
				w.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void writeXmlArmor(Armor a) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(Armor.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_ARMOR);
			m.marshal(a, w);
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	public static void writeXmlWeapons(Weapons we) throws JAXBException,
			IOException {
		JAXBContext context = JAXBContext.newInstance(Weapons.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_WEAPON);
			m.marshal(we, w);
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	public static void writeXmlGeneralSettings(GeneralSettings g)
			throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(GeneralSettings.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		Writer w = null;
		try {
			w = new FileWriter(XmlStatics.XML_PATH_GENERAL);
			m.marshal(g, w);
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	public static Armor readArmorXML() {

		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Armor.class);

			Unmarshaller um = context.createUnmarshaller();
			Armor a = (Armor) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_ARMOR));

			return a;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read Armor.xml: " + e.toString());
		}
		return null;

	}

	public static Alchemy readAlchemyXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(Alchemy.class);
			Unmarshaller um = context.createUnmarshaller();
			Alchemy a = (Alchemy) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_ALCHEMY));

			return a;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read Alchemy.xml: " + e.toString());
		}
		return null;

	}

	public static Ammunition readAmmunitionXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(Ammunition.class);
			Unmarshaller um = context.createUnmarshaller();
			Ammunition p = (Ammunition) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_AMMUNITION));

			return p;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read Ammunition.xml: " + e.toString());
		}
		return null;

	}

	public static Enchanting readEnchantingXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(Enchanting.class);
			Unmarshaller um = context.createUnmarshaller();
			Enchanting e = (Enchanting) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_ENCHANTING));

			return e;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read Enchanting.xml: " + e.toString());
		}
		return null;

	}

	public static Weapons readWeaponsXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(Weapons.class);
			Unmarshaller um = context.createUnmarshaller();
			Weapons w = (Weapons) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_WEAPON));

			return w;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read Weapons.xml: " + e.toString());
		}
		return null;

	}

	public static Languages readLanguagesXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(Languages.class);
			Unmarshaller um = context.createUnmarshaller();
			Languages l = (Languages) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_LANGUAGES));

			return l;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read Languages.xml: " + e.toString());
		}
		return null;

	}

	public static GeneralSettings readGeneralSettingsXML() {
		try {
			JAXBContext context = JAXBContext
					.newInstance(GeneralSettings.class);
			Unmarshaller um = context.createUnmarshaller();
			GeneralSettings g = (GeneralSettings) um.unmarshal(new FileReader(
					XmlStatics.XML_PATH_GENERAL));

			return g;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read GeneralSettings.xml: " + e.toString());
		}
		return null;

	}

	public static NPC readNPCXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(NPC.class);
			Unmarshaller um = context.createUnmarshaller();
			NPC n = (NPC) um.unmarshal(new FileReader(XmlStatics.XML_PATH_NPC));

			return n;
		} catch (JAXBException | FileNotFoundException e) {
			SPGlobal.log("XmlImportExport",
					"CRITICAL: Couldn't read NPC.xml: " + e.toString());
		}
		return null;

	}

	public static LeveledLists readLeveledListsXML() throws JAXBException,
			FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(LeveledLists.class);
		Unmarshaller um = context.createUnmarshaller();
		LeveledLists l = (LeveledLists) um.unmarshal(new FileReader(
				XmlStatics.XML_PATH_LEVELEDLISTS));

		return l;

	}

	public static void generateDefaultXml() {
		// nothing to do atm
	}
}
