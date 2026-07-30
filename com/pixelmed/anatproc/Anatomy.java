/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.anatproc;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodedSequenceItem;
import com.pixelmed.dicom.TagFromName;

import com.pixelmed.utils.StringUtilities;

import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

/**
 * <p>This class encapsulates information pertaining to anatomy of CT images.</p>
 * 
 * <p>Utility methods provide for the detection of anatomy from various header attributes regardless
 * of whether these are formal codes, code strings or free text comments.</p>
 * 
 * @author	dclunie
 */
public class Anatomy {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/anatproc/Anatomy.java,v 1.1 2026/05/19 14:38:44 dclunie Exp $";

	private static final Logger slf4jlogger = LoggerFactory.getLogger(Anatomy.class);
	
	protected static String[] newStringArray(String... values) { return values; }		// use 1.5 varargs feature; seems like a lot of trouble to work around lack of string array curly braces outside declarations

	protected static String[] badLateralityOrViewOrAnatomyPhraseTriggers = {
		"History",
		"Hx of"
	};
	
	protected static String[] badAnatomyWords = {
		"research",		// contains "ear"
		"and",			// expedient way to remove conjunction
		"head first",	// sometimes occurs in protocols
		"feet first",
		"entra di piedi",
		"axials",		// don't want LS to be confused as lumbar spine
		"sagittals",
		"coronals",
		"locator",		// else TOR matches chest
		"tracker"
	};

	protected static DisplayableAnatomicConcept[] anatomicConceptEntries = {
		// combined entries ...
	
		// CP 1962 - was 416949008 R-FAB57
		new DisplayableAnatomicConcept("C5231291","818982008",	false/*unpaired*/,	"SCT",	null,	null,	null,	"818982008",	null,	"Abdomen and Pelvis",				"ABDOMENPELVIS",
			newStringArray(
				"Abdomen Pelvis",	// without conjunctions
				"Abdo Pelvis",		// various abbreviations
				"Abd Pelvis",
				"Abd Pelv",
				"Abd Pel",
				"AbdoPelv",
				"brzuch miednica"/*PL*/
			),
			newStringArray("Abdomen and Pelvis"),		newStringArray("Abdomen and Pelvis")),

		new DisplayableAnatomicConcept("C1442171","416550000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"416550000",	"R-FAB55",	"Chest and Abdomen",				"CHESTABDOMEN",
			newStringArray(
				"Chest Abdomen",	// without conjunctions
				"Chest Abdo",		// various abbreviations
				"Chest Abd",
				"Thorax Abdomen",
				"Thorax Abdo",
				"Thorax Abd",
				"Chest Liver",		// not ideal match, but sometimes seem in protocols
				"Thorax Liver",
				"torace addome",
				"Klatka brzuch"/*PL*/
			),
			newStringArray("Chest and Abdomen"),		newStringArray("Chest and Abdomen")),

		new DisplayableAnatomicConcept("C1562547","416775004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"416775004",	"R-FAB56",	"Chest, Abdomen and Pelvis",		"CHESTABDPELVIS",
			newStringArray(
				"Chest Abdomen Pelvis",	// without conjunctions
				"Chest Abdo Pelvis",	// various abbreviations
				"Chest Abdo Pelv",
				"Chest Abdo Pel",
				"Chest Abd Pelvis",
				"Chest Abd Pelv",
				"Chest Abd Pel",
				"Chest AbdoPelv",
				"Chest Abdomen Pelv",
				"Chest Abdomen Pel",
				"Thorax Abdomen Pelvis",
				"Thorax Abdo Pelvis",
				"Thorax Abdo Pelv",
				"Thorax Abdo Pel",
				"Thorax Abd Pelvis",
				"Thorax Abd Pelv",
				"Thorax Abd Pel",
				"Thorax AbdoPelv",
				"Thorax Abdomen Pelv",
				"Thorax Abdomen Pel",
				"Thoraco Abdomino Pelvien",
				"Torax Abdomen Pelvis",
				"Th Abd Pel",
				"C A P",
				"CAP",
				"T A P",
				"TAP",
				"Klatka brzuch miednica"/*PL*/,
				""
			),
			newStringArray("Chest, Abdomen and Pelvis"),		newStringArray("Chest, Abdomen and Pelvis")),

		new DisplayableAnatomicConcept("C0460004","774007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"774007",	"T-D1000",	"Head and Neck",					"HEADNECK",
			newStringArray(
				"Head Neck"	// without conjunctions
			),
			newStringArray("Head and Neck"),		newStringArray("Head and Neck")),

		new DisplayableAnatomicConcept("C1562459","417437006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"417437006",	"R-FAB52",	"Neck and Chest",					"NECKCHEST",
			newStringArray(
				"Neck Chest",	// without conjunctions
				"Neck Thorax",
				"Collo Tor"
			),
			newStringArray("Neck and Chest"),		newStringArray("Neck and Chest")),

		new DisplayableAnatomicConcept("C1562378","416152001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"416152001",	"R-FAB53",	"Neck, Chest and Abdomen",			"NECKCHESTABDOMEN",
			newStringArray(
				"Neck Chest Abdomen",	// without conjunctions
				"Neck Chest Abdo",	// various abbreviations
				"Neck Chest Abd",
				"Neck Thorax Abdomen",
				"Neck Thorax Abdo",
				"Neck Thorax Abd",
				"Collo Tor Addo"
			),
			newStringArray("Neck, Chest and Abdomen"),		newStringArray("Neck, Chest and Abdomen")),

		new DisplayableAnatomicConcept("C1562776","416319003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"416319003",	"R-FAB54",	"Neck, Chest, Abdomen and Pelvis",	"NECKCHESTABDPELV",
			newStringArray(
				"Neck Chest Abdomen Pelvis",	// without conjunctions
				"Neck Chest Abdo Pelvis",		// various abbreviations
				"Neck Chest Abd Pelvis",
				"Neck Chest Abdo Pelv",
				"Neck Chest Abdo Pel",
				"Neck Chest Abd Pelv",
				"Neck Chest Abd Pel",
				"Neck Thorax Abdomen Pelvis",
				"Neck Thorax Abdo Pelvis",
				"Neck Thorax Abd Pelvis",
				"Neck Thorax Abdo Pelv",
				"Neck Thorax Abdo Pel",
				"Neck Thorax Abd Pelv",
				"Neck Thorax Abd Pel"
			),
			newStringArray("Neck, Chest, Abdomen and Pelvis"),		newStringArray("Neck, Chest, Abdomen and Pelvis")),
	
		new DisplayableAnatomicConcept("C1508520","LP33902-5",	false/*unpaired*/,	"LN",	null,	null,	null,	"LP33902-5",	null,	"Aortic Arch and Carotid Artery",				null,
			newStringArray(
				"Aortic Arch Carotid Artery",	// without conjunctions
				"Aortic Arch and Carotid Arteries",
				"Aortic Arch Carotid Arteries"
			),
			newStringArray("Aortic Arch and Carotid Artery"),		newStringArray("Aortic Arch and Carotid Artery")),


		// single part entries ...
		// CP 1962 - was 113345001 T-D4000
		new DisplayableAnatomicConcept("C5231290","818981001",	false/*unpaired*/,	"SCT",	null,	null,	null,	"818981001",	null,	"Abdomen",			"ABDOMEN",
			newStringArray(
				"Abdominal",
				"BØICHO"/*CZ*/,
				"bruco",/*CZ*/
				"Buik"/*NL*/,
				"Vatsa"/*FI*/,
				"Ventre"/*FR*/,
				"Addome"/*IT*/,
				"Abdome"/*PT*/,
				"はら"/*JP*/,
				"心窩部"/*JP*/,
				"胴"/*JP*/,
				"腹"/*JP*/,
				"腹部"/*JP*/,
				"ЖИВОТ"/*RU*/,
				"Buk"/*NL*/,
				"Pilvo"/*LT*/,
				"Addo",
				"brzuch"/*PL*/
			),
			newStringArray("Abdomen"),
			newStringArray("Abdomen")),
		new DisplayableAnatomicConcept("C0003484","7832008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"7832008",		"T-42500",	"Abdominal aorta",					"ABDOMINALAORTA",	null,	newStringArray("Abdominal aorta"),				newStringArray("Abdominal aorta")),
		new DisplayableAnatomicConcept("C0506230","818987002",	false/*unpaired*/,	"SCT",	null,	null,											null,	"818987002",	null,		"Abdominopelvic cavity",			"",					null,	newStringArray("Abdominopelvic cavity"),		newStringArray("Abdominopelvic cavity","Intra-abdominopelvic","Intraabdominopelvic")),
		new DisplayableAnatomicConcept("C0001208","85856004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"85856004",		"T-15420",	"Acromioclavicular joint",			"ACJOINT",			null,	newStringArray("Acromioclavicular joint"),		newStringArray("Acromioclavicular joint")),
		new DisplayableAnatomicConcept("C0001625","23451007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"23451007",		"T-B3000",	"Adrenal gland",					"ADRENAL",
				newStringArray("Adrenal"),
				newStringArray("Adrenal gland"),
				newStringArray("Adrenal gland")),
		new DisplayableAnatomicConcept("C0042425","67109009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"67109009",		"T-64700",	"Ampulla of Vater",					null,				null,	newStringArray("Ampulla of Vater"),				newStringArray("Ampulla of Vater")),
		new DisplayableAnatomicConcept("C0003087","70258002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"70258002",		"T-15750",	"Ankle joint",						"ANKLE",
			newStringArray(
				"Ankle",
				"Tobillo"/*ES*/,
				"Knöchel"/*DE*/,
				"Enkel"/*NL*/,
				"Cheville"/*FR*/,
				"Tornozelo"/*PT*/,
				"αστράγαλος"/*GR*/,
				"足首"/*JP*/,
				"발목"/*KR*/,
				"лодыжка"/*RU*/
			),
			newStringArray("Ankle joint"),
			newStringArray("Ankle joint")),
		new DisplayableAnatomicConcept("C0265914","128585006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128585006",	"T-48503",	"Anomalous pulmonary vein",			"",					null,	newStringArray("Anomalous pulmonary vein"),		newStringArray("Anomalous pulmonary vein")),
		new DisplayableAnatomicConcept("C1276271","128553008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128553008",	"T-49215",	"Antecubital vein",					"ANTECUBITALV",		null,	newStringArray("Antecubital vein"),				newStringArray("Antecubital vein")),
		new DisplayableAnatomicConcept("C0226662","194996006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"194996006",	"T-48403",	"Anterior cardiac vein",			"ANTCARDIACV",		null,	newStringArray("Anterior cardiac vein"),		newStringArray("Anterior cardiac vein")),
		new DisplayableAnatomicConcept("C0149561","60176003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"60176003",		"T-45540",	"Anterior cerebral artery",			"ACA",				null,	newStringArray("Anterior cerebral artery"),		newStringArray("Anterior cerebral artery")),
		new DisplayableAnatomicConcept("C0149562","8012006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8012006",		"T-45530",	"Anterior communicating artery",	"ANTCOMMA",			null,	newStringArray("Anterior communicating artery"),newStringArray("Anterior communicating artery")),
		new DisplayableAnatomicConcept("C0149603","17388009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"17388009",		"T-45730",	"Anterior spinal artery",			"ANTSPINALA",		null,	newStringArray("Anterior spinal artery"),		newStringArray("Anterior spinal artery")),
		new DisplayableAnatomicConcept("C0085816","68053000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"68053000",		"T-47700",	"Anterior tibial artery",			"ANTTIBIALA",		null,	newStringArray("Anterior tibial artery"),		newStringArray("Anterior tibial artery")),
		new DisplayableAnatomicConcept("C0003461","53505006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53505006",		"T-59900",	"Anus",								"ANUS",				null,	newStringArray("Anus"),							newStringArray("Anus")),
		new DisplayableAnatomicConcept("C1267595","110612005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110612005",	"T-59490",	"Anus, rectum and sigmoid colon",	"ANUSRECTUMSIGMD",	null,	newStringArray("Anus, rectum and sigmoid colon"),	newStringArray("Anus, rectum and sigmoid colon")),
		new DisplayableAnatomicConcept("C0003483","15825003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"15825003",		"T-42000",	"Aorta",							"AORTA",			null,	newStringArray("Aorta"),						newStringArray("Aorta")),
		new DisplayableAnatomicConcept("C0003489","57034009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"57034009",		"T-42300",	"Aortic arch",						"AORTICARCH",		null,	newStringArray("Aortic arch"),					newStringArray("Aortic arch")),
		new DisplayableAnatomicConcept("C1508529","LP33903-3",	false/*unpaired*/,	"LN",	null,	null,											null,	"LP33903-3",	null,		"Aortic arch and subclavian artery",null,				null,	newStringArray("Aortic arch and subclavian artery"),	newStringArray("Aortic arch and subclavian artery")),
		new DisplayableAnatomicConcept("C1290392","128551005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128551005",	"D3-81922",	"Aortic fistula",					"",					null,	newStringArray("Aortic fistula"),				newStringArray("Aortic fistula")),
		new DisplayableAnatomicConcept("C0545736","LP33868-8",	false/*unpaired*/,	"LN",	null,	null,											null,	"LP33868-8",	null,		"Aorta and femoral artery",			null,				null,	newStringArray("Aorta and femoral artery"),		newStringArray("Aorta and femoral artery")),
		new DisplayableAnatomicConcept("C0225703","86598002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"86598002",		"T-280A0",	"Apex of Lung",						"",					null,	newStringArray("Apex of Lung"),					newStringArray("Apex of Lung")),
		new DisplayableAnatomicConcept("C0580781","128564006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128564006",	"T-32602",	"Apex of left ventricle",			"",					null,	newStringArray("Apex of left ventricle"),		newStringArray("Apex of left ventricle")),
		new DisplayableAnatomicConcept("C0445242","128565007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128565007",	"T-32502",	"Apex of right ventricle",			"",					null,	newStringArray("Apex of right ventricle"),		newStringArray("Apex of right ventricle")),
		new DisplayableAnatomicConcept("C0003617","66754008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"66754008",		"T-59200",	"Appendix",							"APPENDIX",			null,	newStringArray("Appendix"),						newStringArray("Appendix")),
		new DisplayableAnatomicConcept("C0446516","40983000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"40983000",		"T-D8200",	"Arm",								"ARM",				null,	newStringArray("Arm"),							newStringArray("Arm")),
		// D1-50666 "Arteriovenous fistula" is in the SNOMED US extension
		new DisplayableAnatomicConcept("C0003855","439470001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"439470001",	"D1-50666",	"Arteriovenous fistula",			null,				null,	newStringArray("Arteriovenous fistula"),		newStringArray("Arteriovenous fistula")),
		// Does not handle BPE synonym "ENDOARTERIAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0003842","51114001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"51114001",		"T-41000",	"Artery",							"ARTERY",			null,	newStringArray("Artery"),						newStringArray("Artery","Endo-arterial")),
		new DisplayableAnatomicConcept("C0003956","54247002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"54247002",		"T-42100",	"Ascending aorta",					"ASCAORTA",			null,	newStringArray("Ascending aorta"),				newStringArray("Ascending aorta")),

		new DisplayableAnatomicConcept("C0227375","9040008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"9040008",		"T-59420",	"Ascending colon",					"ASCENDINGCOLON",	null,	newStringArray("Ascending colon"),				newStringArray("Ascending colon")),
		new DisplayableAnatomicConcept("C0224585","62555009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"62555009",		"T-15317",	"Atlantal-axial joint",				"ATLANTOAXIAL",		null,	newStringArray("Atlantal-axial joint"),			newStringArray("Atlantal-axial joint")),
		new DisplayableAnatomicConcept("C0004169","20292002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"20292002",		"T-15311",	"Atlanto-occipital joint",			"ATLANTOOCCIPITAL",	null,	newStringArray("Atlanto-occipital joint"),		newStringArray("Atlanto-occipital joint")),
		new DisplayableAnatomicConcept("C0018792","59652004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"59652004",		"T-32100",	"Atrium",							"",					null,	newStringArray("Atrium"),						newStringArray("Atrium")),
		// Axilla was 34797008 T-D8100
		new DisplayableAnatomicConcept("C0004454","91470000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91470000",		"T-D8104",	"Axilla",							"AXILLA",			null,	newStringArray("Axilla"),						newStringArray("Axilla")),
		new DisplayableAnatomicConcept("C0004455","67937003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"67937003",		"T-47100",	"Axillary Artery",					"AXILLARYA",		null,	newStringArray("Axillary Artery"),				newStringArray("Axillary Artery")),
		new DisplayableAnatomicConcept("C0004456","68705008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"68705008",		"T-49110",	"Axillary vein",					"AXILLARYV",		null,	newStringArray("Axillary vein"),				newStringArray("Axillary vein")),
		new DisplayableAnatomicConcept("C0004526","72107004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"72107004",		"T-48340",	"Azygos vein",						"AZYGOSVEIN",		null,	newStringArray("Azygos vein"),					newStringArray("Azygos vein")),
		new DisplayableAnatomicConcept("C0004600","77568009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"77568009",		"T-D2100",	"Back",								"BACK",				null,	newStringArray("Back"),							newStringArray("Back")),
		new DisplayableAnatomicConcept("C1289790","128981007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128981007",	"A-00203",	"Baffle",							"",					null,	newStringArray("Baffle"),						newStringArray("Baffle")),
		new DisplayableAnatomicConcept("C0004811","59011009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"59011009",		"T-45800",	"Basilar artery",					"BASILARA",			null,	newStringArray("Basilar artery"),				newStringArray("Basilar artery")),
		new DisplayableAnatomicConcept("C0005400","28273000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"28273000",		"T-60610",	"Bile duct",						"BILEDUCT",			null,	newStringArray("Bile duct"),					newStringArray("Bile duct")),
		new DisplayableAnatomicConcept("C0005423","34707002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"34707002",		"T-60600",	"Biliary tract",					"BILIARYTRACT",		null,	newStringArray("Biliary tract"),				newStringArray("Biliary tract")),
		new DisplayableAnatomicConcept("C1268386","110837003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110837003",	"T-DD123",	"Bladder and urethra",				"BLADDERURETHRA",	null,	newStringArray("Bladder and urethra"),			newStringArray("Bladder and urethra")),
		// Does not handle BPE synonym "ENDOVASCULAR" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0005847","59820001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"59820001",		"T-40000",	"Blood vessel",						"ENDOVASCULAR",		null,	newStringArray("Blood vessel"),					newStringArray("Blood vessel","Endo-vascular","Endovascular")),
		new DisplayableAnatomicConcept("C0005682","89837001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"89837001",		"T-74000",	"Bladder",							"BLADDER",			null,	newStringArray("Bladder"),						newStringArray("Bladder")),
		// Does not handle BPE synonym "ENDOVESICAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0227710","48367006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"48367006",		"T-74250",	"Bladder cavity",					"",					null,	newStringArray("Bladder cavity"),				newStringArray("Bladder cavity","Endo-vesical","Endovesical")),
		new DisplayableAnatomicConcept("C1735317","91830000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91830000",		"T-D00AB",	"Body conduit",						"",					null,	newStringArray("Body conduit"),					newStringArray("Body conduit")),
		new DisplayableAnatomicConcept("C0448188","72001000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"72001000",		"T-12700",	"Bone of lower limb",				"",					null,	newStringArray("Bone of lower limb"),			newStringArray("Bone of lower limb")),
		new DisplayableAnatomicConcept("C0003793","371195002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"371195002",	"T-D0821",	"Bone of upper limb",				"",					null,	newStringArray("Bone of upper limb"),			newStringArray("Bone of upper limb")),
		new DisplayableAnatomicConcept("C1267522","128548003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128548003",	"T-49424",	"Boyd's perforating vein",			"",					null,	newStringArray("Boyd's perforating vein"),		newStringArray("Boyd's perforating vein")),
		new DisplayableAnatomicConcept("C0006087","17137000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"17137000",		"T-47160",	"Brachial artery",					"BRACHIALA",		null,	newStringArray("Brachial artery"),				newStringArray("Brachial artery")),
		new DisplayableAnatomicConcept("C0226812","20115005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"20115005",		"T-49350",	"Brachial vein",					"BRACHIALV",		null,	newStringArray("Brachial vein"),				newStringArray("Brachial vein")),
		new DisplayableAnatomicConcept("C0006104","12738006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"12738006",		"T-A0100",	"Brain",							"BRAIN",			null,	newStringArray("Brain"),						newStringArray("Brain")),
		// Not C0929301 as in L-3 previously
		new DisplayableAnatomicConcept("C0006141","76752008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"76752008",		"T-04000",	"Breast",							"BREAST",			null,	newStringArray("Breast"),						newStringArray("Breast")),
		new DisplayableAnatomicConcept("C0006205","34411009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"34411009",		"T-D6500",	"Broad ligament",					"",					null,	newStringArray("Broad ligament"),				newStringArray("Broad ligament")),
		new DisplayableAnatomicConcept("C0006255","955009",		true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"955009",		"T-26000",	"Bronchus",							"BRONCHUS",			null,	newStringArray("Bronchus"),						newStringArray("Bronchus")),
		new DisplayableAnatomicConcept("C0007966","60819002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"60819002",		"T-D1206",	"Buccal region of face",			"CHEEK",			null,	newStringArray("Buccal region of face"),		newStringArray("Buccal region of face")),
		new DisplayableAnatomicConcept("C0006497","46862004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"46862004",		"T-D2600",	"Buttock",							"BUTTOCK",			null,	newStringArray("Buttock"),						newStringArray("Buttock")),
		new DisplayableAnatomicConcept("C0223921","82474009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"82474009",		"T-12771",	"Calcaneal tubercle",				"",					null,	newStringArray("Calcaneal tubercle"),			newStringArray("Calcaneal tubercle")),
		new DisplayableAnatomicConcept("C0006655","80144004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"80144004",		"T-12770",	"Calcaneus",						"CALCANEUS",		null,	newStringArray("Calcaneus"),					newStringArray("Calcaneus")),
		new DisplayableAnatomicConcept("C0230445","53840002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53840002",		"T-D9440",	"Calf of leg",						"CALF",
			newStringArray("Calf"),
			newStringArray("Calf of leg"),
			newStringArray("Calf of leg")),
		new DisplayableAnatomicConcept("C0022651","2334006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"2334006",		"T-72100",	"Calyx",							"",					null,	newStringArray("Calyx"),						newStringArray("Calyx")),
		new DisplayableAnatomicConcept("C0007226","113257007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113257007",	"T-30000",	"Cardiovascular system",			"CARDIOVASCSYS",	null,	newStringArray("Cardiovascular system"),		newStringArray("Cardiovascular system")),
		new DisplayableAnatomicConcept("C0007272","69105007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69105007",		"T-45010",	"Carotid Artery",					"CAROTID",
			newStringArray("Carotid"),
			newStringArray("Carotid Artery"),
			newStringArray("Carotid Artery")),
		new DisplayableAnatomicConcept("C0007281","21479005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"21479005",		"T-45170",	"Carotid bulb",						"BULB",				null,	newStringArray("Carotid bulb"),					newStringArray("Carotid bulb")),
		new DisplayableAnatomicConcept("C0043262","8205005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8205005",		"T-D8600",	"Carpus",							"CARPUS",			null,	newStringArray("Carpus"),						newStringArray("Carpus")),
		new DisplayableAnatomicConcept("C0007569","57850000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"57850000",		"T-46400",	"Celiac artery",					"CELIACA",			null,	newStringArray("Celiac artery"),				newStringArray("Celiac artery")),
		new DisplayableAnatomicConcept("C0226802","20699002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"20699002",		"T-49240",	"Cephalic vein",					"CEPHALICV",		null,	newStringArray("Cephalic vein"),				newStringArray("Cephalic vein")),
		// was C1268981 180924008 T-A600A
		new DisplayableAnatomicConcept("C0007765","113305005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113305005",	"T-A6000",	"Cerebellum",						"CEREBELLUM",		null,	newStringArray("Cerebellum"),					newStringArray("Cerebellum")),
		new DisplayableAnatomicConcept("C0007770","88556005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"88556005",		"T-45510",	"Cerebral artery",					"CEREBRALA",		null,	newStringArray("Cerebral artery"),				newStringArray("Cerebral artery")),
		new DisplayableAnatomicConcept("C0228174","372073000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"372073000",	"T-A010F",	"Cerebral hemisphere",				"CEREBHEMISPHERE",	null,	newStringArray("Cerebral hemisphere"),			newStringArray("Cerebral hemisphere")),
		new DisplayableAnatomicConcept("C0728985","122494005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"122494005",	"T-11501",	"Cervical spine",					"CSPINE",
			newStringArray(
				"CS",
				"CWK"/*NL*/,
				"CWZ"/*NL*/,
				"HWS"/*DE*/,
				"H Rygg"/*SE*/,
				"Cspine",
				"C spine",
				"Spine Cervical",
				"Cervical",
				"Cervic"/*abbrev*/,
				"Kaelalülid"/*EE*/,
				"KRÈNÍ OBRATLE"/*CZ*/,
				"Halswervels"/*NL*/,
				"Vertebrae cervicalis"/*NL*/,
				"Wervel hals"/*NL*/,
				"Kaulanikamat"/*FI*/,
				"Rachis cervical"/*FR*/,
				"Vertèbre cervicale"/*FR*/,
				"Vertèbres cervicales"/*FR*/,
				"COLONNE CERVICALE"/*FR*/,
				"CERVICALE"/*FR*/,
				"Halswirbel"/*DE*/,
				"Vertebrae cervicales"/*DE*/,
				"Vertebre cervicali"/*IT*/,
				"頚椎"/*JP*/,
				"頸椎"/*JP*/,
				"Vértebras Cervicais"/*PT*/,
				"ШЕЙНЫЕ ПОЗВОНКИ"/*RU*/,
				"columna cervical"/*ES*/,
				"columna cerv"/*ES abbrev*/,
				"columna espinal cervical"/*ES*/,
				"columna vertebral cervical"/*ES*/,
				"vértebras cervicales"/*ES*/,
				"Cervikalkotor"/*SE*/,
				"Halskotor"/*SE*/,
				"Halsrygg"/*SE*/,
				"Cervicale wervelzuil"/*BE*/,
				"C chrbtica"/*SK*/
			),
			newStringArray("Cervical spine"),
			newStringArray("Cervical spine")),
		// was C0729373 297171002 T-D00F7
		new DisplayableAnatomicConcept("C5687879","1217257000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"1217257000",	"",			"Cervico-thoracic spine",			"CTSPINE",
			newStringArray(
				"CTSPINE",
				"Cervico-thoracic",
				"Cervicothoracic"
			),
			newStringArray("Cervico-thoracic spine"),
			newStringArray("Cervico-thoracic spine")),
		new DisplayableAnatomicConcept("C0007874","71252005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"71252005",		"T-83200",	"Cervix",							"CERVIX",			null,	newStringArray("Cervix"),						newStringArray("Cervix")),
		new DisplayableAnatomicConcept("C0007966","60819002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"60819002",		"T-D1206",	"Cheek",							"CHEEK",			null,	newStringArray("Cheek"),						newStringArray("Cheek")),
		// does not handle BPE synonym "THORAX" except for case insensitive matches w. synonyms :(
		// was C0817096 51185008 T-D3000
		new DisplayableAnatomicConcept("C5230958","816094009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"816094009",	"",			"Chest",							"CHEST",
			newStringArray(
				"Thorax",
				"Rindkere"/*EE*/,
				"HRUDNÍK"/*CZ*/,
				"hrudník"/*CZ*/,
				"Borst"/*NL*/,
				"Rintakehä"/*FI*/,
				"Poitrine"/*FR*/,
				"Potter"/*FR ?? - seen in examples*/,
				"Torse"/*FR*/,
				"Brustkorb"/*DE*/,
				"Torace"/*IT*/,
				"Peito"/*PT*/,
				"ГРУДНАЯ КЛЕТКА"/*RU*/,
				"ГРУДЬ"/*RU*/,
				"pecho"/*ES*/,
				"torácico"/*ES*/,
				"Bröstkorg"/*SE*/,
				"Torax"/*SE,PT,ES*/,
				"hrudnнk"/*SK*/,
				"hrudn"/*SK abbrev*/,
				"mellkas"/*HU*/,
				"Krūtinės ląsta"/*LT*/,
				"Tor",
				"Klatka"/*PL*/
			),
			newStringArray("Chest"),
			newStringArray("Chest")),
		new DisplayableAnatomicConcept("C0008524","80621003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"80621003",		"T-A1900",	"Choroid plexus",					"CHOROIDPLEXUS",	null,	newStringArray("Choroid plexus"),				newStringArray("Choroid plexus")),
		// was C1284333 362047009 T-45526
		new DisplayableAnatomicConcept("C0008812","11279006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"11279006",		"T-45520",	"Circle of Willis",					"CIRCLEOFWILLIS",	null,	newStringArray("Circle of Willis"),				newStringArray("Circle of Willis")),
		new DisplayableAnatomicConcept("C0008913","51299004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"51299004",		"T-12310",	"Clavicle",							"CLAVICLE",			null,	newStringArray("Clavicle"),						newStringArray("Clavicle")),
		new DisplayableAnatomicConcept("C0223616","18149002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"18149002",		"T-11B00",	"Coccygeal vertrebrae",				"TAIL",				null,	newStringArray("Coccygeal vertrebrae"),			newStringArray("Coccygeal vertrebrae")),
		new DisplayableAnatomicConcept("C0009194","64688005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"64688005",		"T-11BF0",	"Coccyx",							"COCCYX",			null,	newStringArray("Coccyx"),						newStringArray("Coccyx")),
		new DisplayableAnatomicConcept("C0009368","71854001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"71854001",		"T-59300",	"Colon",							"COLON",			null,	newStringArray("Colon"),						newStringArray("Colon")),
		// not in PS3.16
		new DisplayableAnatomicConcept("C1268346","110797007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110797007",	"T-DD080",	"Colon and rectum",					null,				null,	newStringArray("Colon and rectum"),				newStringArray("Colon and rectum")),
		new DisplayableAnatomicConcept("C0392482","253276007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"253276007",	"D4-31005",	"Common atrium",					"",					null,	newStringArray("Common atrium"),				newStringArray("Common atrium")),
		new DisplayableAnatomicConcept("C0009437","79741001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"79741001",		"T-64500",	"Common bile duct",					"COMMONBILEDUCT",	null,	newStringArray("Common bile duct"),				newStringArray("Common bile duct")),
		new DisplayableAnatomicConcept("C0162859","32062004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"32062004",		"T-45100",	"Common carotid artery",			"CCA",				null,	newStringArray("Common carotid artery"),		newStringArray("Common carotid artery")),
		new DisplayableAnatomicConcept("C0447105","181347005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"181347005",	"T-47402",	"Common femoral artery",			"CFA",				null,	newStringArray("Common femoral artery"),		newStringArray("Common femoral artery")),
		new DisplayableAnatomicConcept("C1275667","397363009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"397363009",	"G-035B",	"Common femoral vein",				"CFV",				null,	newStringArray("Common femoral vein"),			newStringArray("Common femoral vein")),
		new DisplayableAnatomicConcept("C1261084","73634005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"73634005",		"T-46710",	"Common iliac artery",				"COMILIACA",		null,	newStringArray("Common iliac artery"),			newStringArray("Common iliac artery")),
		new DisplayableAnatomicConcept("C0226758","46027005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"46027005",		"T-48920",	"Common iliac vein",				"COMILIACV",		null,	newStringArray("Common iliac vein"),			newStringArray("Common iliac vein")),
		new DisplayableAnatomicConcept("C0152424","45503006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"45503006",		"D4-31120",	"Common ventricle",					"",					null,	newStringArray("Common ventricle"),				newStringArray("Common ventricle")),
		new DisplayableAnatomicConcept("C1290487","128555001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128555001",	"D4-32504",	"Congenital coronary artery fistula to left atrium",			"",		null,	newStringArray("Congenital coronary artery fistula to left atrium"),	newStringArray("Congenital coronary artery fistula to left atrium")),
		new DisplayableAnatomicConcept("C1290488","128556000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128556000",	"D4-32506",	"Congenital coronary artery fistula to left ventricle",			"",		null,	newStringArray("Congenital coronary artery fistula to left ventricle"),	newStringArray("Congenital coronary artery fistula to left ventricle")),
		new DisplayableAnatomicConcept("C1290489","128557009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128557009",	"D4-32509",	"Congenital coronary artery fistula to right atrium",			"",		null,	newStringArray("Congenital coronary artery fistula to right atrium"),	newStringArray("Congenital coronary artery fistula to right atrium")),
		new DisplayableAnatomicConcept("C1290490","128558004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128558004",	"D4-32510",	"Congenital coronary artery fistula to right ventricle",		"",		null,	newStringArray("Congenital coronary artery fistula to right ventricle"),newStringArray("Congenital coronary artery fistula to right ventricle")),
		new DisplayableAnatomicConcept("C0010031","28726007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"28726007",		"T-AA200",	"Cornea",							"CORNEA",			null,	newStringArray("Cornea"),						newStringArray("Cornea")),
		new DisplayableAnatomicConcept("C0205042","41801008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"41801008",		"T-43000",	"Coronary artery",					"CORONARYARTERY",
			newStringArray("Coronary"),
			newStringArray("Coronary artery"),	newStringArray("Coronary artery")),
		new DisplayableAnatomicConcept("C0456944","90219004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"90219004",		"T-48410",	"Coronary sinus",					"CORONARYSINUS",	null,	newStringArray("Coronary sinus"),				newStringArray("Coronary sinus")),
		new DisplayableAnatomicConcept("C0230041","1101003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"1101003",		"T-D1400",	"Cranial cavity",					"INTRACRANIAL",		null,	newStringArray("Cranial cavity"),				newStringArray("Cranial cavity","Intra-cranial","Intracranial")),
		new DisplayableAnatomicConcept("C0447118","128320002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128320002",	"T-A0191",	"Cranial venous system",			"",					null,	newStringArray("Cranial venous system"),		newStringArray("Cranial venous system")),
		new DisplayableAnatomicConcept("C0011666","281130003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"281130003",	"T-D0765",	"Descending aorta",					"DESCAORTA",		null,	newStringArray("Descending aorta"),				newStringArray("Descending aorta")),
		new DisplayableAnatomicConcept("C0227389","32622004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"32622004",		"T-59460",	"Descending colon",					"DESCENDINGCOLON",	null,	newStringArray("Descending colon"),				newStringArray("Descending colon")),
		// not in PS3.16 Annex L
		new DisplayableAnatomicConcept("C0011980","5798000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"5798000",		"T-D3400",	"Diaphragm",						null,				null,	newStringArray("Diaphragm"),					newStringArray("Diaphragm")),
		new DisplayableAnatomicConcept("C0582802","82680008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"82680008",		"T-D0310",	"Digit",							"DIGIT",			null,	newStringArray("Digit"),						newStringArray("Digit")),
		new DisplayableAnatomicConcept("C3669027","C3669027",	true   /*paired*/,	"UMLS",	null,	null,											null,	"C3669027",		null,		"Distal phalanx",					"DISTALPHALANX",	null,	newStringArray("Distal phalanx"),				newStringArray("Distal phalanx")),
		new DisplayableAnatomicConcept("C1267525","128554002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128554002",	"T-49429",	"Dodd's perforating vein",			"",					null,	newStringArray("Dodd's perforating vein"),		newStringArray("Dodd's perforating vein")),
		new DisplayableAnatomicConcept("C0013303","38848004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"38848004",		"T-58200",	"Duodenum",							"DUODENUM",			null,	newStringArray("Duodenum"),						newStringArray("Duodenum")),
		// was C0521421 1910005 T-AB000
		new DisplayableAnatomicConcept("C0013443","117590005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"117590005",	"T-AB001",	"Ear",								"EAR",				null,	newStringArray("Ear"),							newStringArray("Ear")),
		// was C1305417 76248009 T-D8300
		new DisplayableAnatomicConcept("C0013770","16953009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"16953009",		"T-15430",	"Elbow joint",						"ELBOW",
			newStringArray(
				"Ellbogen"/*DE*/,
				"Coude"/*FR*/,
				"Küünar"/*EE*/,
				"Armbåge"/*SE*/,
				"Codo"/*ES*/,
				"Cotovelo"/*PT*/
			),
			newStringArray("Elbow joint"),
			newStringArray("Elbow joint","Elbow")),
		// one of the few that is "endo" without corresponding non-endo BPE ...
		new DisplayableAnatomicConcept("C0225425","53342003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53342003",	"T-21300",	"Endo-nasal",							"ENDONASAL",		null,	newStringArray("Endo-nasal"),					newStringArray("Endo-nasal","Endonasal","Internal nose")),
		new DisplayableAnatomicConcept("C0014180","2739003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"2739003",	"T-83400",	"Endometrium",							"ENDOMETRIUM",		null,	newStringArray("Endometrium"),					newStringArray("Endometrium")),
		new DisplayableAnatomicConcept("C0229960","38266002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"38266002",	"T-D0010",	"Entire body",							"WHOLEBODY",
			newStringArray(
				"Entire body",
				"Whole body",
				"Mid body"	/* not quite right, but nothing better */
			),
			newStringArray("Entire body"),
			newStringArray("Entire body")),
		new DisplayableAnatomicConcept("C0014533","87644002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"87644002",	"T-95000",	"Epididymis",							"EPIDIDYMIS",		null,	newStringArray("Epididymis"),					newStringArray("Epididymis")),
		new DisplayableAnatomicConcept("C0230185","27947004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"27947004",	"T-D4200",	"Epigastric region",					"EPIGASTRIC",		null,	newStringArray("Epigastric region"),			newStringArray("Epigastric region","Epigastric")),
		// Does not handle BPE synonym "ENDOESOPHAGEAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0014876","32849002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"32849002",	"T-56000",	"Esophagus",							"ESOPHAGUS",
				newStringArray(
				"Oesophagus"/*GB*/
				),
				newStringArray("Esophagus"),
				newStringArray("Esophagus","Endoesophageal","Endo-esophageal","Intra-esophageal","Intraesophageal")),
		new DisplayableAnatomicConcept("C1268410","110861005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110861005","T-DD163",	"Esophagus, stomach and duodenum",		"",					null,	newStringArray("Esophagus, stomach and duodenum"),	newStringArray("Esophagus, stomach and duodenum")),
		new DisplayableAnatomicConcept("C0013444","84301002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"84301002",	"T-AB200",	"External auditory canal",				"EAC",				null,	newStringArray("External auditory canal"),		newStringArray("External auditory canal")),
		new DisplayableAnatomicConcept("C0007275","22286001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"22286001",	"T-45200",	"External carotid artery",				"ECA",				null,	newStringArray("External carotid artery"),		newStringArray("External carotid artery")),
		new DisplayableAnatomicConcept("C0226398","113269004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113269004","T-46910",	"External iliac artery",				"EXTILIACA",		null,	newStringArray("External iliac artery"),		newStringArray("External iliac artery")),
		new DisplayableAnatomicConcept("C0226761","63507001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"63507001",	"T-48930",	"External iliac vein",					"EXTILIACV",		null,	newStringArray("External iliac vein"),			newStringArray("External iliac vein")),
		new DisplayableAnatomicConcept("C0226543","71585003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"71585003",	"T-48160",	"External jugular vein",				"EXTJUGV",			null,	newStringArray("External jugular vein"),		newStringArray("External jugular vein")),
		new DisplayableAnatomicConcept("C0015385","66019005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"66019005",	"T-D0300",	"Extremity",							"EXTREMITY",
			newStringArray(
				"Extremety"/*Agfa CR spelling mistake*/,
				"Extremidad"/*ES*/
				),
			newStringArray("Extremity"),
			newStringArray("Extremity")),
		new DisplayableAnatomicConcept("C0015392","81745001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"81745001",	"T-AA000",	"Eye",									"EYE",			null,	newStringArray("Eye"),				newStringArray("Eye")),
		new DisplayableAnatomicConcept("C0015426","80243003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"80243003",	"T-AA810",	"Eyelid",								"EYELID",		null,	newStringArray("Eyelid"),			newStringArray("Eyelid")),
		// not face ... gets confused with frontal view (FR,NL) ...
		//new DisplayableAnatomicConcept("C0015450","89545001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"89545001",	"T-D1200",	"Face",									"FACE",			null,	newStringArray("Face"),				newStringArray("Face")),
		new DisplayableAnatomicConcept("C0226109","23074001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"23074001",	"T-45240",	"Facial artery",						"FACIALA",		null,	newStringArray("Facial artery"),	newStringArray("Facial artery")),
		new DisplayableAnatomicConcept("C0015455","91397008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91397008",	"T-11196",	"Facial bones",							"",				null,	newStringArray("Facial bones"),		newStringArray("Facial bones")),
		new DisplayableAnatomicConcept("C0015801","7657000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"7657000",	"T-47400",	"Femoral artery",						"FEMORALA",		null,	newStringArray("Femoral artery"),	newStringArray("Femoral artery")),
		new DisplayableAnatomicConcept("C0015809","83419000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"83419000",	"T-49410",	"Femoral vein",							"FEMORALV",		null,	newStringArray("Femoral vein"),		newStringArray("Femoral vein")),
		new DisplayableAnatomicConcept("C0015811","71341001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"71341001",	"T-12710",	"Femur",								"FEMUR",		null,	newStringArray("Femur"),			newStringArray("Femur")),
		new DisplayableAnatomicConcept("C0521445","13190002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"13190002",	"T-D8640",	"Fetlock of forelimb",					"FOREFETLOCK",	null,	newStringArray("Fetlock of forelimb"),	newStringArray("Fetlock of forelimb")),
		new DisplayableAnatomicConcept("C0521446","113351006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113351006","T-D9540",	"Fetlock of hindlimb",					"HINDFETLOCK",	null,	newStringArray("Fetlock of hindlimb"),	newStringArray("Fetlock of hindlimb")),
		new DisplayableAnatomicConcept("C0524584","55460000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"55460000",	"T-F5201",	"Fetus",								null,			null,	newStringArray("Fetus"),			newStringArray("Fetus")),
		new DisplayableAnatomicConcept("C0016068","87342007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"87342007",	"T-12750",	"Fibula",								"FIBULA",		null,	newStringArray("Fibula"),			newStringArray("Fibula")),
		new DisplayableAnatomicConcept("C0016129","7569003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"7569003",	"T-D8800",	"Finger",								"FINGER",		null,	newStringArray("Finger"),			newStringArray("Finger")),
		new DisplayableAnatomicConcept("C0230171","58602004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"58602004",	"T-D2310",	"Flank",								"FLANK",		null,	newStringArray("Flank"),			newStringArray("Flank")),
		new DisplayableAnatomicConcept("C0224548","79361005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"79361005",	"T-15200",	"Fontanel of skull",					"FONTANEL",		null,	newStringArray("Fontanel of skull"),newStringArray("Fontanel of skull")),
		new DisplayableAnatomicConcept("C0016504","56459004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"56459004",	"T-D9700",	"Foot",									"FOOT",
			newStringArray(
				"Pied"/*FR*/,
				"Pie"/*ES*/,
				"Voet"/*NL*/,
				"Fuß"/*DE*/,
				"πόδι"/*GR*/,
				"Piede"/*IT*/,
				/*"pé"*//*PT*//*,*//* Cannot use this one ... matches PET and calls all PET scans as foot ! */
				"нога"/*RU*/
			),
			newStringArray("Foot"),
			newStringArray("Foot")),
		new DisplayableAnatomicConcept("C0016536","14975008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"14975008",	"T-D8500",	"Forearm",								"FOREARM",
			newStringArray(
				"Forearm",
				"U ARM"/*DE*/,
				"Unterarm"/*DE*/,
				"Avambraccio"/*IT*/,
				"PØEDLOKTÍ"/*CZ*/,
				"Onderarm"/*NL*/,
				"Kyynärvarsi"/*FI*/,
				"Avant-bras"/*FR*/,
				"まえうで"/*JP*/,
				"前腕"/*JP*/,
				"Antebraço"/*PT*/,
				"ПРЕДПЛЕЧЬЕ"/*RU*/,
				"antebrazo"/*ES*/,
				"Underarm"/*SE*/,
				"predlaktie"/*SK*/
			),
			newStringArray("Forearm"),		newStringArray("Forearm")),

		new DisplayableAnatomicConcept("C1630649","419176008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"419176008","T-D04F2",	"Forefoot",								"FOREFOOT",		null,	newStringArray("Forefoot"),			newStringArray("Forefoot")),
		new DisplayableAnatomicConcept("C0149556","35918002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"35918002",	"T-A1820",	"Fourth ventricle",						"4THVENTRICLE",	null,	newStringArray("Fourth ventricle"),	newStringArray("Fourth ventricle")),
		new DisplayableAnatomicConcept("C0016734","55060009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"55060009",	"T-22200",	"Frontal sinus",						"FRONTALSINUS",	null,	newStringArray("Frontal sinus"),	newStringArray("Frontal sinus")),
		new DisplayableAnatomicConcept("C0016976","28231008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"28231008",	"T-63000",	"Gallbladder",							"GALLBLADDER",	null,	newStringArray("Gallbladder"),		newStringArray("Gallbladder")),
		new DisplayableAnatomicConcept("C0750610","110568007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110568007","T-48820",	"Gastric vein",							"GASTRICV",		null,	newStringArray("Gastric vein"),		newStringArray("Gastric vein")),
		new DisplayableAnatomicConcept("C0447108","128559007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128559007","T-47490",	"Genicular artery",						"GENICULARA",	null,	newStringArray("Genicular artery"),	newStringArray("Genicular artery")),
		new DisplayableAnatomicConcept("C0577295","300571009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"300571009","F-03FC9",	"Gestational sac",						"GESTSAC",		null,	newStringArray("Gestational sac"),	newStringArray("Gestational sac")),
		new DisplayableAnatomicConcept("C0006497","46862004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"46862004",	"T-D2600",	"Gluteal region",						"GLUTEAL",		null,	newStringArray("Gluteal region"),	newStringArray("Gluteal region")),
		new DisplayableAnatomicConcept("C0226659","5928000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"5928000",	"T-48420",	"Great cardiac vein",					"",				null,	newStringArray("Great cardiac vein"),	newStringArray("Great cardiac vein")),
		new DisplayableAnatomicConcept("C0392907","60734001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"60734001",	"T-49530",	"Great saphenous vein",					"GSV",			null,	newStringArray("Great saphenous vein"),	newStringArray("Great saphenous vein")),
		new DisplayableAnatomicConcept("C0018563","85562004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"85562004",	"T-D8700",	"Hand",									"HAND",			null,	newStringArray("Hand"),				newStringArray("Hand")),
		new DisplayableAnatomicConcept("C0018670","69536005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69536005",	"T-D1100",	"Head",									"HEAD",
			newStringArray(
				"Kopf"/*DE*/,
				"Schaedel"/*DE*/,
				"Schædel"/*DE*/,
				"Sch?del"/*DE encoded incorrectly*/,
				"Tete"/*FR*/
			),
			newStringArray("Head"),				newStringArray("Head")),
		new DisplayableAnatomicConcept("C0460004","774007",		false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"774007",	"T-D1000",	"Head and Neck",						"HEADNECK",
			newStringArray("Head Neck"),
			newStringArray("Head and Neck"),	newStringArray("Head and Neck")),
			
		// Does not handle BPE synonym "ENDOCARDIAC" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0018787","80891009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"80891009",	"T-32000",	"Heart",								"HEART",		null,	newStringArray("Heart"),			newStringArray("Heart","Cardiac","Endocardiac","Endo-cardiac")),
		new DisplayableAnatomicConcept("C0019145","76015000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"76015000",	"T-46420",	"Hepatic artery",						"HEPATICA",		null,	newStringArray("Hepatic artery"),	newStringArray("Hepatic artery")),
		new DisplayableAnatomicConcept("C0019155","8993003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8993003",	"T-48720",	"Hepatic vein",							"HEPATICV",		null,	newStringArray("Hepatic vein"),		newStringArray("Hepatic vein")),
		new DisplayableAnatomicConcept("C0230459","416804009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"416804009","T-D9713",	"Hindfoot",								"HINDFOOT",		null,	newStringArray("Hindfoot"),			newStringArray("Hindfoot")),
		new DisplayableAnatomicConcept("C0019552","24136001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"24136001",	"T-15710",	"Hip joint",							"HIP",
			newStringArray(
				"Hip",
				"Heup"/*NL*/,
				"Hanche"/*FR*/,
				"Hüfte"/*DE*/,
				"Puus"/*EE*/,
				"HÖFT"/*SE*/,
				"Cadera"/*ES*/,
				"ισχίο"/*GR*/,
				"anca"/*IT*/,
				"ヒップ"/*JP*/,
				"엉덩이"/*KR*/,
				"вальма"/*RU*/
			),
			newStringArray("Hip"),		newStringArray("Hip")),
		new DisplayableAnatomicConcept("C0020164","85050009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"85050009",	"T-12410",	"Humerus",								"HUMERUS",
			newStringArray(
				"UP_EXM"/*Fuji CR BPE*/,
				"O ARM"/*DE,SE*/,
				"Oberarm"/*DE*/,
				"Õlavars"/*EE*/,
				"Bovenarm"/*NL*/,
				"húmero"/*ES*/
			),
			newStringArray("Humerus"),			newStringArray("Humerus")),
		new DisplayableAnatomicConcept("C1267526","128560002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128560002","T-4942A",	"Hunterian perforating vein",			"",				null,	newStringArray("Hunterian perforating vein"),newStringArray("Hunterian perforating vein")),
		new DisplayableAnatomicConcept("C0230189","11708003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"11708003",	"T-D4240",	"Hypogastric region",					"HYPOGASTRIC",	null,	newStringArray("Hypogastric region"),newStringArray("Hypogastric region")),
		new DisplayableAnatomicConcept("C0020629","81502006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"81502006",	"T-55300",	"Hypopharynx",							"HYPOPHARYNX",	null,	newStringArray("Hypopharynx"),		newStringArray("Hypopharynx")),
		new DisplayableAnatomicConcept("C0020885","34516001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"34516001",	"T-58600",	"Ileum",								"ILEUM",		null,	newStringArray("Ileum"),			newStringArray("Ileum")),
		new DisplayableAnatomicConcept("C0576469","299716001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"299716001","T-41068",	"Iliac and/or femoral artery",			null,			null,	newStringArray("Iliac and femoral artery"),	newStringArray("Iliac and/or femoral artery","Iliac and femoral artery")),
		new DisplayableAnatomicConcept("C0020887","10293006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"10293006",	"T-46700",	"Iliac artery",							"ILIACA",		null,	newStringArray("Iliac artery"),		newStringArray("Iliac artery")),
		new DisplayableAnatomicConcept("C0020888","244411005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"244411005","T-4940E",	"Iliac vein",							"ILIACV",		null,	newStringArray("Iliac vein"),		newStringArray("Iliac vein")),
		new DisplayableAnatomicConcept("C0020889","22356005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"22356005",	"T-12340",	"Ilium",								"ILIUM",		null,	newStringArray("Ilium"),			newStringArray("Ilium")),
		new DisplayableAnatomicConcept("C0226664","195416006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"195416006","T-484A4",	"Inferior cardiac vein",				"",				null,	newStringArray("Inferior cardiac vein"),		newStringArray("Inferior cardiac vein")),
		new DisplayableAnatomicConcept("C0226686","51249003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"51249003",	"T-48540",	"Inferior left pulmonary vein",			"",				null,	newStringArray("Inferior left pulmonary vein"),	newStringArray("Inferior left pulmonary vein")),
		new DisplayableAnatomicConcept("C0162860","33795007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"33795007",	"T-46520",	"Inferior mesenteric artery",			"INFMESA",		null,	newStringArray("Inferior mesenteric artery"),	newStringArray("Inferior mesenteric artery")),
		new DisplayableAnatomicConcept("C0226676","113273001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113273001","T-48520",	"Inferior right pulmonary vein",		"",				null,	newStringArray("Inferior right pulmonary vein"),newStringArray("Inferior right pulmonary vein")),
		new DisplayableAnatomicConcept("C0042458","64131007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"64131007",	"T-48710",	"Inferior vena cava",					"INFVENACAVA",	null,	newStringArray("Inferior vena cava"),newStringArray("Inferior vena cava")),
		new DisplayableAnatomicConcept("C0018246","26893007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"26893007",	"T-D7000",	"Inguinal region",						"INGUINAL",		null,	newStringArray("Inguinal region"),	newStringArray("Inguinal region")),

		new DisplayableAnatomicConcept("C0006094","12691009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"12691009",	"T-46010",	"Innominate artery",					"INNOMINATEA",	null,	newStringArray("Innominate artery"),newStringArray("Innominate artery")),
		new DisplayableAnatomicConcept("C0006095","8887007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8887007",	"T-48620",	"Innominate vein",						"INNOMINATEV",	null,	newStringArray("Innominate vein"),	newStringArray("Innominate vein")),
		//new DisplayableAnatomicConcept("C1283773","361078006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"361078006",	"T-AB959",	"Internal Auditory Canal",	"IAC",
		//	newStringArray("IAC"),
		//	newStringArray("Internal Auditory Canal"),	newStringArray("Internal Auditory Canal")),
		new DisplayableAnatomicConcept("C0007276","86117002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"86117002",	"T-45300",	"Internal carotid artery",				"ICA",			null,	newStringArray("Internal carotid artery"),	newStringArray("Internal carotid artery")),
		new DisplayableAnatomicConcept("C0226364","90024005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"90024005",	"T-46740",	"Internal iliac artery",				"INTILIACA",	null,	newStringArray("Internal iliac artery"),	newStringArray("Internal iliac artery")),
		new DisplayableAnatomicConcept("C0226550","12123001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"12123001",	"T-48170",	"Internal jugular vein",				"INTJUGULARV",	null,	newStringArray("Internal jugular vein"),	newStringArray("Internal jugular vein")),
		new DisplayableAnatomicConcept("C0226276","69327007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69327007",	"T-46200",	"Internal mammary artery",				"INTMAMMARYA",	null,	newStringArray("Internal mammary artery"),	newStringArray("Internal mammary artery")),
		new DisplayableAnatomicConcept("C0442108","131183008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"131183008","G-A15A",	"Intra-articular",						"",				null,	newStringArray("Intra-articular"),			newStringArray("Intra-articular")),
		new DisplayableAnatomicConcept("C3887617","661005",		true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"661005",	"T-D1213",	"Jaw region",							"JAW",			null,	newStringArray("Jaw region"),				newStringArray("Jaw region","Jaw","Maxilla and mandible")),
		new DisplayableAnatomicConcept("C0022378","21306003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"21306003",	"T-58400",	"Jejunum",								"JEJUNUM",		null,	newStringArray("Jejunum"),			newStringArray("Jejunum")),
		new DisplayableAnatomicConcept("C0022417","39352004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"39352004",	"T-15001",	"Joint",								"JOINT",		null,	newStringArray("Joint"),			newStringArray("Joint")),
		new DisplayableAnatomicConcept("C1290478","128563000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128563000","D4-31052",	"Juxtaposed atrial appendage",			"",				null,	newStringArray("Juxtaposed atrial appendage"),	newStringArray("Juxtaposed atrial appendage")),
		// Does not handle BPE synonym "ENDORENAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0022646","64033007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"64033007",	"T-71000",	"Kidney",								"KIDNEY",		null,	newStringArray("Kidney"),			newStringArray("Kidney","Renal","Endo-renal","Endorenal")),
		new DisplayableAnatomicConcept("C0022742","72696002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"72696002",	"T-D9200",	"Knee",									"KNEE",
			newStringArray(
				"Knie"/*DE,NL*/,
				"Genou"/*FR*/,
				"Põlv"/*EE*/,
				"Pölv"/*EE ?wrong accent*/,
				"Knä"/*SE*/,
				"Rodilla"/*ES*/
			),
			newStringArray("Knee"),				newStringArray("Knee")),
		new DisplayableAnatomicConcept("C0226171","59749000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"59749000",	"T-45410",	"Lacrimal artery",						"LACRIMALA",	null,	newStringArray("Lacrimal artery"),	newStringArray("Lacrimal artery")),
		new DisplayableAnatomicConcept("C0021851","14742008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"14742008",	"T-59000",	"Large intestine",						"LARGEINTESTINE",null,	newStringArray("Large intestine"),	newStringArray("Large intestine")),
		new DisplayableAnatomicConcept("C0023078","4596009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"4596009",	"T-24100",	"Larynx",								"LARYNX",
			newStringArray(
				"Laringe"/*ES,IT*/,
				"Kehlkopf"/*DE*/,
				"Strottenhoofd"/*NL*/
				/*FR is same as english*/
			),
				newStringArray("Larynx"),
				newStringArray("Larynx")),
		new DisplayableAnatomicConcept("C0152279","66720007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"66720007",	"T-A1650",	"Lateral Ventricle",					"LATVENTRICLE",	null,	newStringArray("Lateral Ventricle"),newStringArray("Lateral Ventricle")),
		new DisplayableAnatomicConcept("C0225860","82471001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"82471001",	"T-32300",	"Left atrium",							"LATRIUM",		null,	newStringArray("Left atrium"),		newStringArray("Left atrium")),
		new DisplayableAnatomicConcept("C0225861","33626005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"33626005",	"T-32310",	"Left auricular appendage",				"",				null,	newStringArray("Left auricular appendage"),	newStringArray("Left auricular appendage")),
		new DisplayableAnatomicConcept("C0226448","113270003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113270003","T-47420",	"Left femoral artery",					"LFEMORALA",	null,	newStringArray("Left femoral artery"),newStringArray("Left femoral artery")),
		new DisplayableAnatomicConcept("C0226708","273202007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"273202007","T-48727",	"Left hepatic vein",					"LHEPATICV",	null,	newStringArray("Left hepatic vein"),newStringArray("Left hepatic vein")),
		new DisplayableAnatomicConcept("C0738591","133945003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"133945003","T-D4211",	"Left hypochondriac region",			"LHYPOCHONDRIAC",null,	newStringArray("Left hypochondriac region"),	newStringArray("Left hypochondriac region")),
		new DisplayableAnatomicConcept("C0230321","85119005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"85119005",	"T-D7020",	"Left inguinal region",					"LINGUINAL",	null,	newStringArray("Left inguinal region"),	newStringArray("Left inguinal region")),
		new DisplayableAnatomicConcept("C0230180","68505006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"68505006",	"T-D4140",	"Left lower quadrant of abdomen",		"LLQ",			null,	newStringArray("Left lower quadrant of abdomen"),	newStringArray("Left lower quadrant of abdomen")),
		new DisplayableAnatomicConcept("C5439491","1017210004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"1017210004","",		"Left lumbar region",					"LLUMBAR",		null,	newStringArray("Left lumbar region"),newStringArray("Left lumbar region")),
		new DisplayableAnatomicConcept("C0933785","70253006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"70253006",	"T-48814",	"Left portal vein",						"LPORTALV",		null,	newStringArray("Left portal vein"),	newStringArray("Left portal vein")),
		new DisplayableAnatomicConcept("C0226069","50408007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"50408007",	"T-44400",	"Left pulmonary artery",				"LPULMONARYA",	null,	newStringArray("Left pulmonary artery"),newStringArray("Left pulmonary artery")),
		new DisplayableAnatomicConcept("C0230179","86367003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"86367003",	"T-D4130",	"Left upper quadrant of abdomen",		"LUQ",			null,	newStringArray("Left upper quadrant of abdomen"),	newStringArray("Left upper quadrant of abdomen")),
		new DisplayableAnatomicConcept("C0225911","70238003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"70238003",	"T-32640",	"Left ventricle inflow tract",			"",				null,	newStringArray("Left ventricle inflow tract"),	newStringArray("Left ventricle inflow tract")),
		new DisplayableAnatomicConcept("C0225912","13418002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"13418002",	"T-32650",	"Left ventricle outflow tract",			"",				null,	newStringArray("Left ventricle outflow tract"),	newStringArray("Left ventricle outflow tract")),
		new DisplayableAnatomicConcept("C0225897","87878005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"87878005",	"T-32600",	"Left ventricle",						"LVENTRICLE",	null,	newStringArray("Left ventricle"),	newStringArray("Left ventricle")),
		new DisplayableAnatomicConcept("C0226104","113264009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113264009","T-45230",	"Lingual artery",						"LINGUALA",		null,	newStringArray("Lingual artery"),	newStringArray("Lingual artery")),
		new DisplayableAnatomicConcept("C0023884","10200004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"10200004",	"T-62000",	"Liver",								"LIVER",
			newStringArray(
				"foie"/*FR*/,
				"Kepenys"/*LT*/
			),
			newStringArray("Liver"),
			newStringArray("Liver")),
		new DisplayableAnatomicConcept("C0222597","19100000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"19100000",	"T-04003",	"Lower inner quadrant of breast",		"",				null,	newStringArray("Lower inner quadrant of breast"),	newStringArray("Lower inner quadrant of breast")),
		new DisplayableAnatomicConcept("C1140621","30021000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"30021000",	"T-D9400",	"Lower leg",							"LOWERLEG",
			newStringArray(
				"LOW_EXM"/*Fuji CR BPE*/,
				"LOWEXM"/*Siemens CR BPE*/,
				"TIB FIB ANKLE",
				"Jambe"/*FR*/
			),
			newStringArray("Lower leg"),
			newStringArray("Lower leg","Leg")),
		new DisplayableAnatomicConcept("C0230331","42694008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"42694008",	"T-D8030",	"All legs",								"LEGS",
				null,
				newStringArray("All legs"),
				newStringArray(
					"All legs",
					"Legs"
				)),
		new DisplayableAnatomicConcept("C0023216","61685007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"61685007",	"T-D9000",	"Lower limb",							"LOWERLIMB",	null,	newStringArray("Lower limb"),		newStringArray("Lower limb")),
		new DisplayableAnatomicConcept("C0222599","33564002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"33564002",	"T-04005",	"Lower outer quadrant of breast",		"",				null,	newStringArray("Lower outer quadrant of breast"),	newStringArray("Lower outer quadrant of breast")),
		new DisplayableAnatomicConcept("C0230094","63337009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"63337009",	"T-D2020",	"Lower trunk",							"LOWERTRUNK",	null,	newStringArray("Lower trunk"),		newStringArray("Lower trunk")),
		new DisplayableAnatomicConcept("C0226408","34635009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"34635009",	"T-46960",	"Lumbar artery",						"LUMBARA",		null,	newStringArray("Lumbar artery"),	newStringArray("Lumbar artery")),
		// do not include lumbar region, since will conflict with matches for lumbar spine
		new DisplayableAnatomicConcept("C0024090","52612000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"52612000",	"T-D2300",	"Lumbar region",						"LUMBAR",		null,	newStringArray("Lumbar region"),	newStringArray("Lumbar region")),
		new DisplayableAnatomicConcept("C0024091","122496007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"122496007","T-11503",	"Lumbar spine",							"LSPINE",
			newStringArray(
				"LS",
				"LWK"/*NL*/,
				"LWZ"/*NL*/,
				"LWS"/*DE*/,
				"L Rygg"/*SE*/,
				"Lspine",
				"L spine",
				"Spine Lumbar",
				"Lumbar",
				"Rachis lombaire"/*FR*/,
				"COLONNE LOMBAIRE"/*FR*/,
				"Rach.Lomb"/*FR abbrev*/,
				"lombaire"/*FR*/,
				"Nimmelülid"/*EE*/,
				"Columna lumbar"/*ES*/,
				"LÄNDRYGG"/*SE*/,
				"L chrbtica"/*SK*/,
				"COL LOMBARE"
			),
			newStringArray("Lumbar spine"),		newStringArray("Lumbar spine")),
		// CP 2166 - was 297173004 T-D00F9
		new DisplayableAnatomicConcept("C5687876","1217253001",	false/*unpaired*/,	"SCT",	null,	null,	null,	"1217253001",	null,	"Lumbo-sacral spine",	"LSSPINE",
			newStringArray(
				"LSSPINE",
				"Lumbosacral spine",
				"Lumbo-sacrale wervelzuil"/*BE*/,
				"columna vertebral lumbosacra"/*ES*/,
				"vértebras lumbosacras"/*ES*/,
				"Colonna Lombosacrale"
			),
			newStringArray("Lumbo-sacral spine"),	newStringArray("Lumbo-sacral spine")),
		new DisplayableAnatomicConcept("C0524424","91747007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91747007",	"T-40230",	"Lumen of blood vessel",			"LUMEN",		null,	newStringArray("Lumen of blood vessel"),	newStringArray("Lumen of blood vessel")),
		new DisplayableAnatomicConcept("C0024109","39607008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"39607008",	"T-28000",	"Lung",								"LUNG",
			newStringArray(
				"pluco"/*PL*/,
				"pluca"/*PL*/
			),
			newStringArray("Lung"),				newStringArray("Lung")),
		new DisplayableAnatomicConcept("C0024687","91609006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91609006",	"T-11180",	"Mandible",							"MANDIBLE",		null,	newStringArray("Mandible"),					newStringArray("Mandible")),
		new DisplayableAnatomicConcept("C0227027","88176008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"88176008",	"T-54170",	"Mandibular dental arch",			"",				null,	newStringArray("Mandibular dental arch"),	newStringArray("Mandibular dental arch")),
		new DisplayableAnatomicConcept("C2711599","442274007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"442274007","T-540EE",	"Mandibular incisor teeth",			"",				null,	newStringArray("Mandibular incisor teeth"),	newStringArray("Mandibular incisor teeth")),
		new DisplayableAnatomicConcept("C0446908","59066005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"59066005",	"T-11133",	"Mastoid bone",						"MASTOID",		null,	newStringArray("Mastoid bone"),				newStringArray("Mastoid bone")),
		new DisplayableAnatomicConcept("C0024947","70925003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"70925003",	"T-11170",	"Maxilla",							"MAXILLA",		null,	newStringArray("Maxilla"),					newStringArray("Maxilla")),
		new DisplayableAnatomicConcept("C0227026","39481002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"39481002",	"T-54160",	"Maxillary dental arch",			"",				null,	newStringArray("Maxillary dental arch"),	newStringArray("Maxillary dental arch")),
		new DisplayableAnatomicConcept("C2711204","442100006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"442100006","T-540ED",	"Maxillary incisor teeth",			"",				null,	newStringArray("Maxillary incisor teeth"),	newStringArray("Maxillary incisor teeth")),
		// See "Jaw region"
//		new DisplayableAnatomicConcept("C0178738","LP30124-9",	true   /*paired*/,	"LN",	null,	null,											null,	"LP30124-9",	null,	"Maxilla and Mandible",				null,			null,	newStringArray("Maxilla and Mandible"),		newStringArray("Maxilla and Mandible")),
		new DisplayableAnatomicConcept("C0025066","72410000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"72410000",	"T-D3300",	"Mediastinum",						"MEDIASTINUM",	null,	newStringArray("Mediastinum"),				newStringArray("Mediastinum")),
		new DisplayableAnatomicConcept("C0025465","86570000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"86570000",	"T-46500",	"Mesenteric artery",				"MESENTRICA",	null,	newStringArray("Mesenteric artery"),		newStringArray("Mesenteric artery")),
		new DisplayableAnatomicConcept("C0025473","128583004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128583004","T-4884A",	"Mesenteric vein",					"MESENTRICV",	null,	newStringArray("Mesenteric vein"),			newStringArray("Mesenteric vein")),
		new DisplayableAnatomicConcept("C0025526","36455000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"36455000",	"T-12540",	"Metacarpus",						"METACARPUS",	null,	newStringArray("Metacarpus"),				newStringArray("Metacarpus")),
		new DisplayableAnatomicConcept("C0025590","280711000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"280711000","T-12847",	"Metatarsus",						"METATARSUS",	null,	newStringArray("Metatarsus"),				newStringArray("Metatarsus")),
		new DisplayableAnatomicConcept("C0149566","17232002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"17232002",	"T-45600",	"Middle cerebral artery",			"MCA",			null,	newStringArray("Middle cerebral artery"),	newStringArray("Middle cerebral artery")),
		new DisplayableAnatomicConcept("C0226707","273099000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"273099000","T-48726",	"Middle hepatic vein",				"MIDHEPATICV",	null,	newStringArray("Middle hepatic vein"),		newStringArray("Middle hepatic vein")),
		new DisplayableAnatomicConcept("C0446609","243977002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"243977002","T-D4434",	"Morisons pouch",					"MORISONSPOUCH",null,	newStringArray("Morisons pouch"),			newStringArray("Morisons pouch")),
		// was C1267547 21082005 T-51000
		new DisplayableAnatomicConcept("C0230028","123851003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"123851003","T-D0662",	"Mouth",							"MOUTH",		null,	newStringArray("Mouth"),					newStringArray("Mouth")),
		new DisplayableAnatomicConcept("C0584890","102292000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"102292000","T-14668",	"Muscle of lower limb",				"",				null,	newStringArray("Muscle of lower limb"),		newStringArray("Muscle of lower limb")),
		new DisplayableAnatomicConcept("C0559498","30608006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"30608006",	"T-13600",	"Muscle of upper limb",				"",				null,	newStringArray("Muscle of upper limb"),		newStringArray("Muscle of upper limb")),
		new DisplayableAnatomicConcept("C0027422","74386004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"74386004",	"T-11149",	"Nasal bone",						"",				null,	newStringArray("Nasal bone"),				newStringArray("Nasal bone")),
		// Does not handle BPE synonym "ENDONASOPHARYNYX" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C1283682","360955006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"360955006","T-2300C",	"Nasopharynx",						"NASOPHARYNX",	null,	newStringArray("Nasopharynx"),	newStringArray("Nasopharynx","Endo-nasopharyngeal","Endonasopharyngeal","Endonasopharynx")),
		new DisplayableAnatomicConcept("C0223724","30518006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"30518006",	"T-12450",	"Navicular of forefoot",			"FORENAVICULAR",null,	newStringArray("Navicular of forefoot"),	newStringArray("Navicular of forefoot")),
		new DisplayableAnatomicConcept("C0223947","75772009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"75772009",	"T-12800",	"Navicular of hindfoot",			"HINDNAVICULAR",null,	newStringArray("Navicular of hindfoot"),	newStringArray("Navicular of hindfoot")),
		new DisplayableAnatomicConcept("C0027530","45048000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"45048000",	"T-D1600",	"Neck",								"NECK",
			newStringArray(
				"Kael"/*EE*/,
				"Collo"/*IT*/,
				"Cuello"/*ES*/,
				"Hals"/*DE*/,
				"Nek"/*NL*/,
				"Nacke"/*SE*/
			),
			newStringArray("Neck"),				newStringArray("Neck")),
		new DisplayableAnatomicConcept("C0028429","45206002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"45206002",	"T-21000",	"Nose",								"NOSE",			null,	newStringArray("Nose"),						newStringArray("Nose")),
		new DisplayableAnatomicConcept("C0226117","31145008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"31145008",	"T-45250",	"Occipital artery",					"OCCPITALA",	null,	newStringArray("Occipital artery"),			newStringArray("Occipital artery")),
		new DisplayableAnatomicConcept("C0226579","32114007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"32114007",	"T-48214",	"Occipital vein",					"OCCIPTALV",	null,	newStringArray("Occipital vein"),			newStringArray("Occipital vein")),
		new DisplayableAnatomicConcept("C0230212","113346000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113346000","T-D4450",	"Omental bursa",					"",				null,	newStringArray("Omental bursa"),			newStringArray("Omental bursa")),
		new DisplayableAnatomicConcept("C0028977","27398004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"27398004",	"T-D4600",	"Omentum",							"",				null,	newStringArray("Omentum"),					newStringArray("Omentum")),
		new DisplayableAnatomicConcept("C0029078","53549008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53549008",	"T-45400",	"Ophthalmic artery",				"OPHTHALMICA",	null,	newStringArray("Ophthalmic artery"),		newStringArray("Ophthalmic artery")),
		new DisplayableAnatomicConcept("C0450102","55024004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"55024004",	"T-11102",	"Optic canal",						"OPTICCANAL",	null,	newStringArray("Optic canal"),				newStringArray("Optic canal")),
		// ORBIT was "Orbital region" C0015392 371398005 T-D0801
		new DisplayableAnatomicConcept("C0029180","363654007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"363654007","T-D14AE",	"Orbital structure",				"ORBIT",
			newStringArray(
				"Orbit"
			),
			newStringArray("Orbital structure"),	newStringArray("Orbital structure","Orbital region","Orbit")),
		new DisplayableAnatomicConcept("C0029939","15497006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"15497006",	"T-87000",	"Ovary",							"OVARY",		null,	newStringArray("Ovary"),					newStringArray("Ovary")),
		new DisplayableAnatomicConcept("C0030274","15776009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"15776009",	"T-65000",	"Pancreas",							"PANCREAS",		null,	newStringArray("Pancreas"),					newStringArray("Pancreas")),
		new DisplayableAnatomicConcept("C0030288","69930009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69930009",	"T-65010",	"Pancreatic duct",					null,			null,	newStringArray("Pancreatic duct"),			newStringArray("Pancreatic duct")),
		new DisplayableAnatomicConcept("C1267614","110621006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110621006","T-65600",	"Pancreatic duct and bile duct systems",	"PANCBILEDUCT",
			newStringArray(
				"Pancreatic duct and bile duct systems",
				"Pancreatic duct and bile ducts",
				"Pancreatic duct and bile duct",
				"Pancreatic and bile ducts"
			),
			newStringArray("Pancreatic duct and bile duct systems"),
			newStringArray("Pancreatic duct and bile duct systems")),
		new DisplayableAnatomicConcept("C0030471","2095001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"2095001",	"T-22000",	"Paranasal sinus",					"",				null,	newStringArray("Paranasal sinus"),			newStringArray("Paranasal sinus","Nasal sinus")),
		new DisplayableAnatomicConcept("C0458345","91691001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91691001",	"T-D3136",	"Parasternal",						"PARASTERNAL",	null,	newStringArray("Parasternal"),				newStringArray("Parasternal")),
		new DisplayableAnatomicConcept("C0030518","111002",		true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"111002",	"T-B7000",	"Parathyroid",						"PARATHYROID",	null,	newStringArray("Parathyroid"),				newStringArray("Parathyroid")),
		new DisplayableAnatomicConcept("C0030580","45289007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"45289007",	"T-61100",	"Parotid gland",					"PAROTID",
			newStringArray("Parotid"),
			newStringArray("Parotid gland"),	newStringArray("Parotid gland")),
		new DisplayableAnatomicConcept("C0230368","31329001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"31329001",	"T-D8650",	"Pastern of forefoot",				"FOREPASTERN",	null,	newStringArray("Pastern of forefoot"),		newStringArray("Pastern of forefoot")),
		new DisplayableAnatomicConcept("C0230455","18525008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"18525008",	"T-D9550",	"Pastern of hindfoot",				"HINDPASTERN",	null,	newStringArray("Pastern of hindfoot"),		newStringArray("Pastern of hindfoot")),
		new DisplayableAnatomicConcept("C0030647","64234005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"64234005",	"T-12730",	"Patella",							"PATELLA",		null,	newStringArray("Patella"),					newStringArray("Patella")),
		new DisplayableAnatomicConcept("C0013274","83330001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"83330001",	"D4-32012",	"Patent ductus arteriosus",			"",				null,	newStringArray("Patent ductus arteriosus"),	newStringArray("Patent ductus arteriosus")),
		new DisplayableAnatomicConcept("C5230966","816989007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"816989007","",			"Pelvic cavity",					"",				null,	newStringArray("Pelvic cavity"),			newStringArray("Pelvic cavity, false and/or true","Pelvic cavity","Intra-pelvic","Intrapelvic")),
		// was C0030797 12921003 T-D6000
		new DisplayableAnatomicConcept("C5230955","816092008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"816092008","",			"Pelvis",							"PELVIS",
			newStringArray(
				"PV"/*abbreviations*/,
				"Pelv",
				"Pel",
				"Bekken"/*NL*/,
				"Becken"/*DE*/,
				"Bassin"/*FR*/,
				"Vaagen"/*EE*/,
				"BÄCKEN"/*SE*/,
				"λεκάνη"/*GR*/,
				"Bacino"/*IT*/,
				"骨盤"/*JP*/,
				"골반"/*KR*/,
				"miednica"/*PL*/
			),
			newStringArray("Pelvis"),			newStringArray("Pelvis")),
		new DisplayableAnatomicConcept("C5688969","1231522001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"1231522001","",		"Pelvis and lower extremities",		"PELVISLOWEXTREMT",	null,newStringArray("Pelvis and lower extremities"),	newStringArray("Pelvis and lower extremities")),
		new DisplayableAnatomicConcept("C0559907","282044005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"282044005","T-46807",	"Penile artery",					"PENILEA",		null,	newStringArray("Penile artery"),			newStringArray("Penile artery")),
		new DisplayableAnatomicConcept("C0030851","18911002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"18911002",	"T-91000",	"Penis",							"PENIS",		null,	newStringArray("Penis"),					newStringArray("Penis")),
		new DisplayableAnatomicConcept("C0031066","38864007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"38864007",	"T-D2700",	"Perineum",							"PERINEUM",		null,	newStringArray("Perineum"),					newStringArray("Perineum")),
		new DisplayableAnatomicConcept("C0226476","8821006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8821006",	"T-47630",	"Peroneal artery",					"PERONEALA",	null,	newStringArray("Peroneal artery"),			newStringArray("Peroneal artery")),
		new DisplayableAnatomicConcept("C0225972","25489000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"25489000",	"T-39050",	"Pericardial cavity",				null,			null,	newStringArray("Pericardial cavity"),		newStringArray("Pericardial cavity")),
		new DisplayableAnatomicConcept("C0282611","706342009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"706342009","R-FE0C7",	"Phantom",							"PHANTOM",		null,	newStringArray("Phantom"),					newStringArray("Phantom")),
		new DisplayableAnatomicConcept("C0729889","312535008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"312535008","T-20101",	"Pharynx and larynx",				"PHARYNXLARYNX",null,	newStringArray("Pharynx and larynx"),		newStringArray("Pharynx and larynx")),
		// was C1278903 181211006 T-55002
		new DisplayableAnatomicConcept("C0031354","54066008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"54066008",	"T-55000",	"Pharynx",							"PHARYNX",		null,	newStringArray("Pharynx"),					newStringArray("Pharynx")),
		new DisplayableAnatomicConcept("C0032043","78067005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"78067005",	"T-F1100",	"Placenta",							"PLACENTA",		null,	newStringArray("Placenta"),					newStringArray("Placenta")),
		new DisplayableAnatomicConcept("C0032649","43899006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"43899006",	"T-47500",	"Popliteal artery",					"POPLITEALA",	null,	newStringArray("Popliteal artery"),			newStringArray("Popliteal artery")),
		new DisplayableAnatomicConcept("C0230436","32361000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"32361000",	"T-D9310",	"Popliteal fossa",					"POPLITEALFOSSA",null,	newStringArray("Popliteal fossa"),			newStringArray("Popliteal fossa")),
		new DisplayableAnatomicConcept("C0032652","56849005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"56849005",	"T-49650",	"Popliteal vein",					"POPLITEALV",	null,	newStringArray("Popliteal vein"),			newStringArray("Popliteal vein")),
		new DisplayableAnatomicConcept("C0032718","32764006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"32764006",	"T-48810",	"Portal vein",						"PORTALV",		null,	newStringArray("Portal vein"),				newStringArray("Portal vein")),
		new DisplayableAnatomicConcept("C0149576","70382005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"70382005",	"T-45900",	"Posterior cerebral artery",		"PCA",			null,	newStringArray("Posterior cerebral artery"),newStringArray("Posterior cerebral artery")),
		new DisplayableAnatomicConcept("C0149559","43119007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"43119007",	"T-45320",	"Posterior communicating artery",	"POSCOMMA",		null,	newStringArray("Posterior communicating artery"),newStringArray("Posterior communicating artery")),
		new DisplayableAnatomicConcept("C1267527","128569001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128569001","T-49535",	"Posterior medial tributary",		"",				null,	newStringArray("Posterior medial tributary"),newStringArray("Posterior medial tributary")),
		new DisplayableAnatomicConcept("C0086835","13363002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"13363002",	"T-47600",	"Posterior tibial artery",			"POSTIBIALA",	null,	newStringArray("Posterior tibial artery"),	newStringArray("Posterior tibial artery")),
		new DisplayableAnatomicConcept("C0231136","14944004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"14944004",	"T-F7001",	"Primitive aorta",					"",				null,	newStringArray("Primitive aorta"),			newStringArray("Primitive aorta")),
		new DisplayableAnatomicConcept("C0231157","91707000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"91707000",	"T-F7040",	"Primitive pulmonary artery",		"",				null,	newStringArray("Primitive pulmonary artery"),newStringArray("Primitive pulmonary artery")),
		new DisplayableAnatomicConcept("C0226455","31677005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"31677005",	"T-47440",	"Profunda femoris artery",			"PROFFEMA",		null,	newStringArray("Profunda femoris artery"),	newStringArray("Profunda femoris artery")),
		new DisplayableAnatomicConcept("C0226841","23438002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"23438002",	"T-49660",	"Profunda femoris vein",			"PROFFEMV",		null,	newStringArray("Profunda femoris vein"),	newStringArray("Profunda femoris vein")),
		new DisplayableAnatomicConcept("C0033572","41216001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"41216001",	"T-92000",	"Prostate",							"PROSTATE",		null,	newStringArray("Prostate"),					newStringArray("Prostate")),
		new DisplayableAnatomicConcept("C0155675","111289009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"111289009","D3-40208",	"Pulmonary arteriovenous fistula",	"",				null,	newStringArray("Pulmonary arteriovenous fistula"),	newStringArray("Pulmonary arteriovenous fistula")),
		new DisplayableAnatomicConcept("C1290491","128584005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128584005","D4-33142",	"Pulmonary artery conduit",			"",				null,	newStringArray("Pulmonary artery conduit"),	newStringArray("Pulmonary artery conduit")),
		new DisplayableAnatomicConcept("C0034052","81040000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"81040000",	"T-44000",	"Pulmonary artery",					"PULMONARYA",	null,	newStringArray("Pulmonary artery"),			newStringArray("Pulmonary artery")),
		new DisplayableAnatomicConcept("C1267246","128586007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128586007","T-32190",	"Pulmonary chamber of cor triatriatum","",			null,	newStringArray("Pulmonary chamber of cor triatriatum"),	newStringArray("Pulmonary chamber of cor triatriatum")),
		new DisplayableAnatomicConcept("C1290492","128566008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128566008","D4-33512",	"Pulmonary vein confluence",		"",				null,	newStringArray("Pulmonary vein confluence"),newStringArray("Pulmonary vein confluence")),
		new DisplayableAnatomicConcept("C0034090","122972007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"122972007","T-48581",	"Pulmonary vein",					"PULMONARYV",	null,	newStringArray("Pulmonary vein"),			newStringArray("Pulmonary vein")),
		new DisplayableAnatomicConcept("C1290493","128567004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128567004","D4-33514",	"Pulmonary venous atrium",			"",				null,	newStringArray("Pulmonary venous atrium"),	newStringArray("Pulmonary venous atrium")),
		new DisplayableAnatomicConcept("C0162857","45631007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"45631007",	"T-47300",	"Radial artery",					"RADIALA",		null,	newStringArray("Radial artery"),			newStringArray("Radial artery")),
		new DisplayableAnatomicConcept("C1267080","110535000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110535000","T-12403",	"Radius and ulna",					"RADIUSULNA",	null,	newStringArray("Radius and ulna"),			newStringArray("Radius and ulna")),
		new DisplayableAnatomicConcept("C0034627","62413002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"62413002",	"T-12420",	"Radius",							"RADIUS",		null,	newStringArray("Radius"),					newStringArray("Radius")),
		new DisplayableAnatomicConcept("C0013075","53843000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53843000",	"T-D6407",	"Rectouterine pouch",				"CULDESAC",		null,	newStringArray("Rectouterine pouch"),		newStringArray("Rectouterine pouch")),
		// Does not handle BPE synonym "ENDORECTAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0034896","34402009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"34402009",	"T-59600",	"Rectum",							"RECTUM",		null,	newStringArray("Rectum"),					newStringArray("Rectum","Endo-rectal","Endorectal")),
		new DisplayableAnatomicConcept("C0035065","2841007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"2841007",	"T-46600",	"Renal artery",						"RENALA",		null,	newStringArray("Renal artery"),				newStringArray("Renal artery")),
		new DisplayableAnatomicConcept("C0227666","25990002",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"25990002",	"T-72000",	"Renal pelvis",						"",				null,	newStringArray("Renal pelvis"),				newStringArray("Renal pelvis")),
		new DisplayableAnatomicConcept("C0035092","56400007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"56400007",	"T-48740",	"Renal vein",						"RENALV",		null,	newStringArray("Renal vein"),				newStringArray("Renal vein")),
		new DisplayableAnatomicConcept("C0035359","82849001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"82849001",	"T-D4900",	"Retroperitoneum",					"RETROPERITONEUM",null,	newStringArray("Retroperitoneum"),			newStringArray("Retroperitoneum")),
		new DisplayableAnatomicConcept("C0035561","113197003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113197003","T-11300",	"Rib",								"RIB",
			newStringArray(
				"Gril costal"/*FR*/,
				"Gril cost"/*FR abbrev*/
			),
			newStringArray("Rib"),
			newStringArray("Rib")),
		new DisplayableAnatomicConcept("C0225844","73829009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"73829009",	"T-32200",	"Right atrium",						"RATRIUM",		null,	newStringArray("Right atrium"),				newStringArray("Right atrium")),
		new DisplayableAnatomicConcept("C0225845","68300000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"68300000",	"T-32210",	"Right auricular appendage",		"",				null,	newStringArray("Right auricular appendage"),newStringArray("Right auricular appendage")),
		new DisplayableAnatomicConcept("C0226447","69833005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69833005",	"T-47410",	"Right femoral artery",				"RFEMORALA",	null,	newStringArray("Right femoral artery"),		newStringArray("Right femoral artery")),
		new DisplayableAnatomicConcept("C0226706","272998002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"272998002","T-48725",	"Right hepatic vein",				"RHEPATICV",	null,	newStringArray("Right hepatic vein"),		newStringArray("Right hepatic vein")),
		new DisplayableAnatomicConcept("C0738590","133946002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"133946002","T-D4212",	"Right hypochondriac region",		"RHYPOCHONDRIAC",null,	newStringArray("Right hypochondriac region"),newStringArray("Right hypochondriac region")),
		new DisplayableAnatomicConcept("C0230318","37117007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"37117007",	"T-D7010",	"Right inguinal region",			"RINGUINAL",	null,	newStringArray("Right inguinal region"),	newStringArray("Right inguinal region")),
		new DisplayableAnatomicConcept("C0230178","48544008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"48544008",	"T-D4120",	"Right lower quadrant of abdomen",	"RLQ",			null,	newStringArray("Right lower quadrant of abdomen"),newStringArray("Right lower quadrant of abdomen")),
		new DisplayableAnatomicConcept("C5439490","1017211000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"1017211000","",		"Right lumbar region",				"RLUMBAR",		null,	newStringArray("Right lumbar region"),		newStringArray("Right lumbar region")),
		new DisplayableAnatomicConcept("C0226730","73931004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"73931004",	"T-48813",	"Right portal vein",				"RPORTALV",		null,	newStringArray("Right portal vein"),		newStringArray("Right portal vein")),
		new DisplayableAnatomicConcept("C0226054","78480002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"78480002",	"T-44200",	"Right pulmonary artery",			"RPULMONARYA",	null,	newStringArray("Right pulmonary artery"),	newStringArray("Right pulmonary artery")),
		new DisplayableAnatomicConcept("C0230177","50519007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"50519007",	"T-D4110",	"Right upper quadrant of abdomen",	"RUQ",			null,	newStringArray("Right upper quadrant of abdomen"),newStringArray("Right upper quadrant of abdomen")),
		new DisplayableAnatomicConcept("C0225891","8017000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8017000",	"T-32540",	"Right ventricle inflow",			"",				null,	newStringArray("Right ventricle inflow"),	newStringArray("Right ventricle inflow")),
		new DisplayableAnatomicConcept("C0225892","44627009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"44627009",	"T-32550",	"Right ventricle outflow tract",	"",				null,	newStringArray("Right ventricle outflow tract"),newStringArray("Right ventricle outflow tract")),
		new DisplayableAnatomicConcept("C0225883","53085002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53085002",	"T-32500",	"Right ventricle",					"RVENTRICLE",	null,	newStringArray("Right ventricle"),			newStringArray("Right ventricle")),
		new DisplayableAnatomicConcept("C0036036","39723000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"39723000",	"T-15680",	"Sacroiliac joint",					"SIJOINT",		null,	newStringArray("Sacroiliac joint"),			newStringArray("Sacroiliac joint")),
		new DisplayableAnatomicConcept("C0036037","54735007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"54735007",	"T-11AD0",	"Sacrum",							"SSPINE",
			newStringArray("SSPINE"),
			newStringArray("Sacrum"),
			newStringArray("Sacrum","Sacral spine")),
		new DisplayableAnatomicConcept("C0447132","128587003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128587003","T-D930A",	"Saphenofemoral junction",			"SFJ",			null,	newStringArray("Saphenofemoral junction"),	newStringArray("Saphenofemoral junction")),
		new DisplayableAnatomicConcept("C0036186","362072009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"362072009","T-4940B",	"Saphenous vein",					"SAPHENOUSV",	null,	newStringArray("Saphenous vein"),			newStringArray("Saphenous vein")),
		new DisplayableAnatomicConcept("C0036270","41695006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"41695006",	"T-D1160",	"Scalp",							"SCALP",		null,	newStringArray("Scalp"),					newStringArray("Scalp")),
		new DisplayableAnatomicConcept("C0036277","79601000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"79601000",	"T-12280",	"Scapula",							"SCAPULA",		null,	newStringArray("Scapula"),					newStringArray("Scapula")),
		new DisplayableAnatomicConcept("C0036410","18619003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"18619003",	"T-AA110",	"Sclera",							"SCLERA",		null,	newStringArray("Sclera"),					newStringArray("Sclera")),
		new DisplayableAnatomicConcept("C0036471","20233005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"20233005",	"T-98000",	"Scrotum",							"SCROTUM",		null,	newStringArray("Scrotum"),					newStringArray("Scrotum")),
		new DisplayableAnatomicConcept("C0036609","42575006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"42575006",	"T-D1460",	"Sella turcica",					"SELLA",		null,	newStringArray("Sella turcica"),			newStringArray("Sella turcica")),
		new DisplayableAnatomicConcept("C0036628","64739004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"64739004",	"T-93000",	"Seminal vesicle",					"SEMVESICLE",	null,	newStringArray("Seminal vesicle"),			newStringArray("Seminal vesicle")),
		new DisplayableAnatomicConcept("C0278418","58742003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"58742003",	"T-12980",	"Sesamoid bones of foot",			"SESAMOID",		null,	newStringArray("Sesamoid bones of foot"),	newStringArray("Sesamoid bones of foot")),
		new DisplayableAnatomicConcept("C0037004","16982005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"16982005",	"T-D2220",	"Shoulder",							"SHOULDER",
			newStringArray(
				"Schouder"/*NL*/,
				"Schulter"/*DE*/,
				"Epaule"/*FR*/,
				"épaule"/*FR*/,
				"õlg"/*EE*/,
				"Ölg"/*EE ?wrong accent*/,
				"Hombro"/*ES*/,
				"Ombro"/*PT*/,
				"Rameno"/*SK*/,
				"Rippe"/*DE*/
			),
			newStringArray("Shoulder"),
			newStringArray("Shoulder")),
		new DisplayableAnatomicConcept("C0227391","60184004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"60184004",	"T-59470",	"Sigmoid colon",					"SIGMOID",		null,	newStringArray("Sigmoid colon"),			newStringArray("Sigmoid colon")),
		new DisplayableAnatomicConcept("C0037303","89546000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"89546000",	"T-11100",	"Skull",							"SKULL",
			newStringArray(
				"Kolju"/*EE*/,
				"LEBKA"/*CZ*/,
				"Schedel"/*NL*/,
				"Kallo"/*FI*/,
				"Crâne"/*FR*/,
				"Cranium"/*DE*/,
				"Schädel"/*DE*/,
				"Cranio"/*IT*/,
				"Calota Craniana"/*PT*/,
				"Crânio"/*PT*/,
				"ЧЕРЕП"/*RU*/,
				"Calota Craneal"/*ES*/,
				"Cráneo"/*ES*/,
				"Kalvarium"/*SE*/,
				"Kranium"/*SE*/,
				"Skalle"/*SE*/,
				"Lebka"/*SK*/
			),
			newStringArray("Skull"),			newStringArray("Skull")),
		new DisplayableAnatomicConcept("C0021852","30315005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"30315005",	"T-58000",	"Small intestine",				"SMALLINTESTINE",	null,	newStringArray("Small intestine"),			newStringArray("Small intestine")),
		new DisplayableAnatomicConcept("C0037925","2748008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"2748008",	"T-A7010",	"Spinal cord",					"SPINALCORD",		null,	newStringArray("Spinal cord"),				newStringArray("Spinal cord")),
		// was 280717001 T-D0146
		new DisplayableAnatomicConcept("C0037949","421060004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"421060004","T-D04FF",	"Spine",						"SPINE",
			newStringArray(
				"Rachis"/*FR*/,
				"Rygg"/*SE*/,
				"chrbtica"/*SK*/
			),
			newStringArray("Spine"),			newStringArray("Spine")),
			
		new DisplayableAnatomicConcept("C0028872","51807001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"51807001",	"T-64710",	"Sphincter of Oddi",			null,				null,	newStringArray("Sphincter of Oddi"),		newStringArray("Sphincter of Oddi")),
		new DisplayableAnatomicConcept("C0278443","56101001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"56101001",	"T-65016",	"Sphincter pancreaticus",		null,				null,	newStringArray("Sphincter pancreaticus"),	newStringArray("Sphincter pancreaticus")),
		new DisplayableAnatomicConcept("C0037993","78961009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"78961009",	"T-C3000",	"Spleen",						"SPLEEN",			null,	newStringArray("Spleen"),					newStringArray("Spleen")),
		new DisplayableAnatomicConcept("C0037996","22083002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"22083002",	"T-46460",	"Splenic artery",				"SPLENICA",			null,	newStringArray("Splenic artery"),			newStringArray("Splenic artery")),
		new DisplayableAnatomicConcept("C0038001","35819009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"35819009",	"T-48890",	"Splenic vein",					"SPLENICV",			null,	newStringArray("Splenic vein"),				newStringArray("Splenic vein")),
		new DisplayableAnatomicConcept("C0038291","7844006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"7844006",	"T-15610",	"Sternoclavicular joint",		"SCJOINT",			null,	newStringArray("Sternoclavicular joint"),	newStringArray("Sternoclavicular joint")),
		new DisplayableAnatomicConcept("C0038293","56873002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"56873002",	"T-11210",	"Sternum",						"STERNUM",			null,	newStringArray("Sternum"),					newStringArray("Sternum")),
		new DisplayableAnatomicConcept("C1456798","116010006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"116010006","T-15728",	"Stiffle",						"STIFLE",			null,	newStringArray("Stiffle"),					newStringArray("Stiffle")),
		new DisplayableAnatomicConcept("C0038351","69695003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69695003",	"T-57000",	"Stomach",						"STOMACH",			null,	newStringArray("Stomach"),					newStringArray("Stomach")),
		new DisplayableAnatomicConcept("C0038530","36765005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"36765005",	"T-46100",	"Subclavian artery",			"SUBCLAVIANA",		null,	newStringArray("Subclavian artery"),		newStringArray("Subclavian artery")),
		new DisplayableAnatomicConcept("C0038532","9454009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"9454009",	"T-48330",	"Subclavian vein",				"SUBCLAVIANV",		null,	newStringArray("Subclavian vein"),			newStringArray("Subclavian vein")),
		new DisplayableAnatomicConcept("C0442184","19695001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"19695001",	"T-D4210",	"Subcostal",					"SUBCOSTAL",		null,	newStringArray("Subcostal"),				newStringArray("Subcostal")),
		new DisplayableAnatomicConcept("C0230070","5713008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"5713008",	"T-D1603",	"Submandibular area",			"",					null,	newStringArray("Submandibular area"),		newStringArray("Submandibular area","Submandibular triangle")),
		new DisplayableAnatomicConcept("C0038556","54019009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"54019009",	"T-61300",	"Submandibular gland",			"SUBMANDIBULAR",
			newStringArray("Submandibular"),
			newStringArray("Submandibular gland"),
			newStringArray("Submandibular gland")),
		new DisplayableAnatomicConcept("C0931905","170887008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"170887008","T-D161E",	"Submental",					"",					null,	newStringArray("Submental"),				newStringArray("Submental")),
		new DisplayableAnatomicConcept("C0230144","5076001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"5076001",	"T-D3213",	"Subxiphoid",					"",					null,	newStringArray("Subxiphoid"),				newStringArray("Subxiphoid")),
		new DisplayableAnatomicConcept("C0447106","181349008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"181349008","T-47403",	"Superficial femoral artery",	"SFA",				null,	newStringArray("Superficial femoral artery"),newStringArray("Superficial femoral artery")),
		new DisplayableAnatomicConcept("C1301369","397364003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"397364003","G-035A",	"Superficial femoral vein",		"SFV",				null,	newStringArray("Superficial femoral vein"),	newStringArray("Superficial femoral vein")),
		new DisplayableAnatomicConcept("C0226130","15672000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"15672000",	"T-45270",	"Superficial temporal artery",	"",					null,	newStringArray("Superficial temporal artery"),newStringArray("Superficial temporal artery")),
		new DisplayableAnatomicConcept("C0226682","43863001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"43863001",	"T-48530",	"Superior left pulmonary vein",	"LSUPPULMONARYV",	null,	newStringArray("Superior left pulmonary vein"),newStringArray("Superior left pulmonary vein")),
		new DisplayableAnatomicConcept("C0162861","42258001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"42258001",	"T-46510",	"Superior mesenteric artery",	"SMA",				null,	newStringArray("Superior mesenteric artery"),newStringArray("Superior mesenteric artery")),
		new DisplayableAnatomicConcept("C0226671","8629005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"8629005",	"T-48510",	"Superior right pulmonary vein","RSUPPULMONARYV",	null,	newStringArray("Superior right pulmonary vein"),newStringArray("Superior right pulmonary vein")),
		new DisplayableAnatomicConcept("C0226093","72021004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"72021004",	"T-45210",	"Superior thyroid artery",		"SUPTHYROIDA",		null,	newStringArray("Superior thyroid artery"),	newStringArray("Superior thyroid artery")),
		new DisplayableAnatomicConcept("C0042459","48345005",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"48345005",	"T-48610",	"Superior vena cava",			"SVC",				null,	newStringArray("Superior vena cava"),		newStringArray("Superior vena cava")),
		new DisplayableAnatomicConcept("C0230078","77621008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"77621008",	"T-D1620",	"Supraclavicular region of neck","SUPRACLAVICULAR",	null,	newStringArray("Supraclavicular region of neck"),newStringArray("Supraclavicular region of neck")),
		new DisplayableAnatomicConcept("C0230189","11708003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"11708003",	"T-D4240",	"Suprapubic region",			"SUPRAPUBIC",		null,	newStringArray("Suprapubic region"),		newStringArray("Suprapubic region")),
		new DisplayableAnatomicConcept("C0222769","26493002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"26493002",	"T-11218",	"Suprasternal notch",			"",					null,	newStringArray("Suprasternal notch"),		newStringArray("Suprasternal notch")),
		new DisplayableAnatomicConcept("C0345096","128589000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128589000","T-44007",	"Systemic collateral artery to lung","",			null,	newStringArray("Systemic collateral artery to lung"),newStringArray("Systemic collateral artery to lung")),
		new DisplayableAnatomicConcept("C1290494","128568009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"128568009","D4-33516",	"Systemic venous atrium",		"",					null,	newStringArray("Systemic venous atrium"),	newStringArray("Systemic venous atrium")),
		new DisplayableAnatomicConcept("C0039318","27949001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"27949001",	"T-15770",	"Tarsal joint",					"",					null,	newStringArray("Tarsal joint"),				newStringArray("Tarsal joint")),
		new DisplayableAnatomicConcept("C0039316","108371006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"108371006","T-12761",	"Tarsus",						"TARSUS",			null,	newStringArray("Tarsus"),					newStringArray("Tarsus")),
		new DisplayableAnatomicConcept("C0039493","53620006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53620006",	"T-15290",	"Temporomandibular joint",		"TMJ",
			newStringArray(
				"Temporomandibular",
				"TMJ"
			),
			newStringArray("Temporomandibular joint"),	newStringArray("Temporomandibular joint")),
		new DisplayableAnatomicConcept("C0039597","40689003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"40689003",	"T-94000",	"Testis",						"TESTIS",			null,	newStringArray("Testis"),					newStringArray("Testis")),
		new DisplayableAnatomicConcept("C0039729","42695009",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"42695009",	"T-A4000",	"Thalamus",						"THALAMUS",			null,	newStringArray("Thalamus"),					newStringArray("Thalamus")),
		new DisplayableAnatomicConcept("C0039866","68367000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"68367000",	"T-D9100",	"Thigh",						"THIGH",
			newStringArray(
				"Oberschenkel"/*DE*/,
				"Bovenbeen"/*NL*/,
				"Reis"/*EE*/
			),
			newStringArray("Thigh"),
			newStringArray("Thigh")),
		new DisplayableAnatomicConcept("C0149555","49841001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"49841001",	"T-A1740",	"Third ventricle",				"3RDVENTRICLE",		null,	newStringArray("Third ventricle"),			newStringArray("Third ventricle")),
		new DisplayableAnatomicConcept("C1522460","113262008",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"113262008","T-42070",	"Thoracic aorta",				"THORACICAORTA",	null,	newStringArray("Thoracic aorta"),			newStringArray("Thoracic aorta")),
		new DisplayableAnatomicConcept("C0581269","122495006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"122495006","T-11502",	"Thoracic spine",				"TSPINE",
			newStringArray(
				"TSPINE",
				"TS",
				"THWK"/*NL*/,
				"DWZ"/*NL*/,
				"BWS"/*DE*/,
				"B Rygg"/*SE*/,
				"T spine",
				"Spine Thoracic",
				"Thoracic",
				"Dorsal",
				"Dorsal spine",
				"Spine Dorsal",
				"Rachis dorsal"/*FR*/,
				"COLONNE THORACIQUE"/*FR*/,
				"THORACIQUE"/*FR*/,
				"Rinnaosa"/*EE??*/,
				"Rinnalülid"/*EE*/,
				"Columna dorsal"/*ES*/,
				"Columna vertebral dorsal"/*ES*/,
				"Thoracale wervelzuil"/*BE*/,
				"BRÖSTRYGG"/*SE*/,
				"Th chrbtica"/*SK*/
			),
			newStringArray("Thoracic spine"),
			newStringArray("Thoracic spine")),
		// was C0729374 297172009 T-D00F8
		new DisplayableAnatomicConcept("C5687878","1217256009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"1217256009","",		"Thoraco-lumbar spine",			"TLSPINE",
			newStringArray(
				"TLSPINE",
				"Thoraco-lumbar",
				"Thoracolumbar",
				"Col.Dors.Lomb"/*FR abbrev*/,
				"THORACOLUMBALE"
			),
			newStringArray("Thoraco-lumbar spine"),
			newStringArray("Thoraco-lumbar spine")),
		new DisplayableAnatomicConcept("C0040067","76505004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"76505004",	"T-D8810",	"Thumb",						"THUMB",			null,	newStringArray("Thumb"),					newStringArray("Thumb")),
		// was C1306748 118507000 T-C8001
		new DisplayableAnatomicConcept("C0040113","9875009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"9875009",	"T-C8000",	"Thymus",						"THYMUS",			null,	newStringArray("Thymus"),					newStringArray("Thymus")),
		new DisplayableAnatomicConcept("C0040132","69748006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"69748006",	"T-B6000",	"Thyroid",						"THYROID",			null,	newStringArray("Thyroid"),					newStringArray("Thyroid")),
		new DisplayableAnatomicConcept("C0224692","110536004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110536004","T-12701",	"Tibia and fibula",				"TIBIAFIBULA",		null,	newStringArray("Tibia and fibula"),			newStringArray("Tibia and fibula")),
		new DisplayableAnatomicConcept("C0040184","12611008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"12611008",	"T-12740",	"Tibia",						"TIBIA",			null,	newStringArray("Tibia"),					newStringArray("Tibia")),
		new DisplayableAnatomicConcept("C0040357","29707007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"29707007",	"T-D9800",	"Toe",							"TOE",				null,	newStringArray("Toe"),						newStringArray("Toe")),
		new DisplayableAnatomicConcept("C0040408","21974007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"21974007",	"T-53000",	"Tongue",						"TONGUE",			null,	newStringArray("Tongue"),					newStringArray("Tongue")),
		new DisplayableAnatomicConcept("C1268276","110726009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110726009","T-DD006",	"Trachea and bronchus",			"TRACHEABRONCHUS",	null,	newStringArray("Trachea and bronchus"),		newStringArray("Trachea and bronchus")),
		new DisplayableAnatomicConcept("C0040578","44567001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"44567001",	"T-25000",	"Trachea",						"TRACHEA",			null,	newStringArray("Trachea"),					newStringArray("Trachea")),
		new DisplayableAnatomicConcept("C0227386","485005",		false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"485005",	"T-59440",	"Transverse colon",				"TRANSVERSECOLON",	null,	newStringArray("Transverse colon"),			newStringArray("Transverse colon")),
		new DisplayableAnatomicConcept("C0041207","61959006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"61959006",	"D4-31400",	"Truncus arteriosus communis",	"",					null,	newStringArray("Truncus arteriosus communis"),newStringArray("Truncus arteriosus communis")),
		new DisplayableAnatomicConcept("C0007569","57850000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"57850000",	"T-46400",	"Truncus coeliacus",			"",					null,	newStringArray("Truncus coeliacus"),		newStringArray("Truncus coeliacus")),
		new DisplayableAnatomicConcept("C0460005","22943007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"22943007",	"T-D2000",	"Trunk",						"TRUNK",			null,	newStringArray("Trunk"),					newStringArray("Trunk")),
		new DisplayableAnatomicConcept("C0041600","23416004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"23416004",	"T-12430",	"Ulna",							"ULNA",				null,	newStringArray("Ulna"),						newStringArray("Ulna")),
		new DisplayableAnatomicConcept("C0162858","44984001",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"44984001",	"T-47200",	"Ulnar artery",					"ULNARA",			null,	newStringArray("Ulnar artery"),				newStringArray("Ulnar artery")),
		new DisplayableAnatomicConcept("C0041632","50536004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"50536004",	"T-F1810",	"Umbilical artery",				"UMBILICALA",		null,	newStringArray("Umbilical artery"),			newStringArray("Umbilical artery")),
		new DisplayableAnatomicConcept("C0041638","90290004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"90290004",	"T-D4230",	"Umbilical region",				"UMBILICAL",		null,	newStringArray("Umbilical region"),			newStringArray("Umbilical region")),
		new DisplayableAnatomicConcept("C0226734","284639000",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"284639000","T-48832",	"Umbilical vein",				"UMBILICALV",		null,	newStringArray("Umbilical vein"),			newStringArray("Umbilical vein")),
		new DisplayableAnatomicConcept("C0446516","40983000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"40983000",	"T-D8200",	"Upper arm",					"UPPERARM",			null,	newStringArray("Upper arm"),				newStringArray("Upper arm")),
		new DisplayableAnatomicConcept("C3203348","62834003",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"62834003",	"T-50110",	"Upper gastro-intestinal tract","UGITRACT",			null,	newStringArray("Upper gastro-intestinal tract"),newStringArray("Upper gastro-intestinal tract")),
		new DisplayableAnatomicConcept("C0222596","77831004",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"77831004",	"T-04002",	"Upper inner quadrant of breast","",				null,	newStringArray("Upper inner quadrant of breast"),newStringArray("Upper inner quadrant of breast")),
		new DisplayableAnatomicConcept("C1140618","53120007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53120007",	"T-D8000",	"Upper limb",					"UPPERLIMB",		null,	newStringArray("Upper limb"),				newStringArray("Upper limb")),
		new DisplayableAnatomicConcept("C0222598","76365002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"76365002",	"T-04004",	"Upper outer quadrant of breast","",				null,	newStringArray("Upper outer quadrant of breast"),newStringArray("Upper outer quadrant of breast")),
		new DisplayableAnatomicConcept("C0230093","67734004",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"67734004",	"T-D2010",	"Upper trunk",					"UPPERTRUNK",		null,	newStringArray("Upper trunk"),				newStringArray("Upper trunk")),
		new DisplayableAnatomicConcept("C2317509","431491007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"431491007","T-7000B",	"Upper urinary tract",			"UPRURINARYTRACT",	null,	newStringArray("Upper urinary tract"),		newStringArray("Upper urinary tract")),
		// was C0227690 65364008 T-73800
		// Does not handle BPE synonym "ENDOURETERIC" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0041951","87953007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"87953007",	"T-73000",	"Ureter",						"URETER",			null,	newStringArray("Ureter"),					newStringArray("Ureter","Endo-ureteric","Endoureteric")),
		// Does not handle BPE synonym "ENDOURETHRAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0041967","13648007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"13648007",	"T-75000",	"Urethra",						"URETHRA",			null,	newStringArray("Urethra"),					newStringArray("Urethra","Endo-urethral","Endourethral")),
		new DisplayableAnatomicConcept("C2316969","431938005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"431938005","T-7000C",	"Urinary tract",				"URINARYTRACT",		null,	newStringArray("Urinary tract"),			newStringArray("Urinary tract")),
		new DisplayableAnatomicConcept("C1267676","110639002",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110639002","T-88920",	"Uterus and fallopian tubes",	"",					null,	newStringArray("Uterus and fallopian tubes"),newStringArray("Uterus and fallopian tubes")),
		new DisplayableAnatomicConcept("C0042149","35039007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"35039007",	"T-83000",	"Uterus",						"UTERUS",			null,	newStringArray("Uterus"),					newStringArray("Uterus")),
		// Does not handle BPE synonym "ENDOVAGINAL" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C0042232","76784001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"76784001",	"T-82000",	"Vagina",						"VAGINA",			null,	newStringArray("Vagina"),					newStringArray("Vagina","Endo-vaginal","Endovaginal")),
		// Does not handle BPE synonym "ENDOVENOUS" except for case insensitive matches w. synonyms :(
		new DisplayableAnatomicConcept("C1289794","312288001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"312288001","T-D000F",	"Vascular graft",				"",					null,	newStringArray("Vascular graft"),			newStringArray("Vascular graft")),
		new DisplayableAnatomicConcept("C0042449","29092000",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"29092000",	"T-48000",	"Vein",							"VEIN",				null,	newStringArray("Vein"),						newStringArray("Vein")),
		new DisplayableAnatomicConcept("C0226503","34340008",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"34340008",	"T-48003",	"Venous network",				"",					null,	newStringArray("Venous network"),			newStringArray("Venous network")),
		new DisplayableAnatomicConcept("C0729900","312548007",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"312548007","T-46006",	"Ventral branch of abdominal aorta",null,			null,	newStringArray("Ventral branch of abdominal aorta"),newStringArray("Ventral branch of abdominal aorta")),
		new DisplayableAnatomicConcept("C0018827","21814001",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"21814001",	"T-32400",	"Ventricle",					"",					null,	newStringArray("Ventricle"),				newStringArray("Ventricle")),
		new DisplayableAnatomicConcept("C0042559","85234005",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"85234005",	"T-45700",	"Vertebral artery",				"VERTEBRALA",		null,	newStringArray("Vertebral artery"),			newStringArray("Vertebral artery")),
		new DisplayableAnatomicConcept("C1266914","110517009",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"110517009","T-11011",	"Vertebral column and cranium",	"",					null,	newStringArray("Vertebral column and cranium"),	newStringArray("Vertebral column and cranium")),
		new DisplayableAnatomicConcept("C0042993","45292006",	false/*unpaired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"45292006",	"T-81000",	"Vulva",						"VULVA",			null,	newStringArray("Vulva"),					newStringArray("Vulva")),
		new DisplayableAnatomicConcept("C0043189","53036007",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"53036007",	"T-D8040",	"Wing",							"WING",				null,	newStringArray("Wing"),						newStringArray("Wing")),
		// was C1262468
		new DisplayableAnatomicConcept("C1322271","74670003",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"74670003",	"T-15460",	"Wrist joint",					"WRIST",
			newStringArray(
				"Wrist",
				"muñeca"/*ES*/,
				"MUÒECA"/*ES ? misspelled*/,
				"pols"/*NL*/,
				"poignet"/*FR*/,
				"Handgelenk"/*DE*/,
				"καρπός"/*GR*/,
				"polso"/*IT,PT*/,
				"手首"/*JP*/,
				"손목"/*KR*/,
				"запястье руки"/*RU*/,
				"ranne"/*EE*/,
				"käe"/*EE*/
			), 
			newStringArray("Wrist joint"),
			newStringArray("Wrist joint")),
		// was C0162485 51204001 T-11167 Zygomatic arch ? CP 1258
		new DisplayableAnatomicConcept("C0043539","13881006",	true   /*paired*/,	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"13881006",	"T-11166",	"Zygoma",			"ZYGOMA",
			newStringArray("Zygoma"),
			newStringArray("Zygoma"),
			newStringArray("Zygoma")),
	};
	
	protected static DictionaryOfConcepts anatomyConcepts = new DictionaryOfConcepts(anatomicConceptEntries,badAnatomyWords,"Anatomy");

	public static DictionaryOfConcepts getAnatomyConcepts() { return anatomyConcepts; }
	
	public static DisplayableAnatomicConcept findAnatomicConcept(AttributeList list) {
		// strategy is to look in specific attributes first, then general, and look in codes before free text ...
		DisplayableConcept anatomy = null;
		{
			CodedSequenceItem anatomicRegionSequence = CodedSequenceItem.getSingleCodedSequenceItemOrNull(list,TagFromName.AnatomicRegionSequence);
			if (anatomicRegionSequence != null) {
				slf4jlogger.debug("findAnatomicConcept(): anatomicRegionSequence = {}",anatomicRegionSequence);
				anatomy = anatomyConcepts.findCodeInEntriesFirstThenTryCodeMeaningInEntriesThenTryLongestIndividualEntryContainedWithinCodeMeaning(anatomicRegionSequence);
				if (anatomy != null) slf4jlogger.debug("findAnatomicConcept(): found Anatomy in AnatomicRegionSequence = {}",anatomy.toStringBrief());
			}
		}
		if (anatomy == null) {
			String bodyPartExamined = Attribute.getSingleStringValueOrNull(list,TagFromName.BodyPartExamined);
			if (bodyPartExamined != null) {
				slf4jlogger.debug("findAnatomicConcept(): bodyPartExamined = {}",bodyPartExamined);
				anatomy = anatomyConcepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(bodyPartExamined);
				if (anatomy != null) slf4jlogger.debug("findAnatomicConcept(): found Anatomy in BodyPartExamined = {}",anatomy.toStringBrief());
			}
		}
		if (anatomy == null) {
			anatomy = findAmongstGeneralAttributes(list,anatomyConcepts,badLateralityOrViewOrAnatomyPhraseTriggers);
		}
		return (DisplayableAnatomicConcept)anatomy;
	}
	
	public static DisplayableConcept findAmongstGeneralAttributes(AttributeList list,DictionaryOfConcepts concepts,String[] badPhraseTriggers) {
		// strategy is to look in attributes of lower level entities first, and look in codes before free text ...
		DisplayableConcept found = null;
		{
			String imageComments = Attribute.getSingleStringValueOrNull(list,TagFromName.ImageComments);
			if (imageComments != null && !StringUtilities.containsRegardlessOfCase(imageComments,badPhraseTriggers)) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): imageComments = {}",imageComments);
				found = concepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(imageComments);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in ImageComments = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		if (found == null) {
			String seriesDescription = Attribute.getSingleStringValueOrNull(list,TagFromName.SeriesDescription);
			if (seriesDescription != null && !StringUtilities.containsRegardlessOfCase(seriesDescription,badPhraseTriggers)) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): seriesDescription = {}",seriesDescription);
				found = concepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(seriesDescription);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in SeriesDescription = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		if (found == null) {
			String protocolName = Attribute.getSingleStringValueOrNull(list,TagFromName.ProtocolName);
			if (protocolName != null && !StringUtilities.containsRegardlessOfCase(protocolName,badPhraseTriggers)) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): protocolName = {}",protocolName);
				found = concepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(protocolName);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in ProtocolName = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		if (found == null) {
			CodedSequenceItem performedProtocolCodeSequence = CodedSequenceItem.getSingleCodedSequenceItemOrNull(list,TagFromName.PerformedProtocolCodeSequence);
			if (performedProtocolCodeSequence != null) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): performedProtocolCodeSequence = {}",performedProtocolCodeSequence);
				found = concepts.findCodeInEntriesFirstThenTryCodeMeaningInEntriesThenTryLongestIndividualEntryContainedWithinCodeMeaning(performedProtocolCodeSequence);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in PerformedProtocolCodeSequence = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		if (found == null) {
			String performedProcedureStepDescription = Attribute.getSingleStringValueOrNull(list,TagFromName.PerformedProcedureStepDescription);
			if (performedProcedureStepDescription != null && !StringUtilities.containsRegardlessOfCase(performedProcedureStepDescription,badPhraseTriggers)) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): performedProcedureStepDescription = {}",performedProcedureStepDescription);
				found = concepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(performedProcedureStepDescription);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in PerformedProcedureStepDescription = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		if (found == null) {
			CodedSequenceItem procedureCodeSequence = CodedSequenceItem.getSingleCodedSequenceItemOrNull(list,TagFromName.ProcedureCodeSequence);
			if (procedureCodeSequence != null) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): procedureCodeSequence = {}",procedureCodeSequence);
				found = concepts.findCodeInEntriesFirstThenTryCodeMeaningInEntriesThenTryLongestIndividualEntryContainedWithinCodeMeaning(procedureCodeSequence);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in ProcedureCodeSequence = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		if (found == null) {
			String studyDescription = Attribute.getSingleStringValueOrNull(list,TagFromName.StudyDescription);
			if (studyDescription != null && !StringUtilities.containsRegardlessOfCase(studyDescription,badPhraseTriggers)) {
				slf4jlogger.debug("findAmongstGeneralAttributes(): seriesDescription = {}",studyDescription);
				found = concepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(studyDescription);
				if (found != null) slf4jlogger.debug("findAmongstGeneralAttributes(): found {} in StudyDescription = {}",concepts.getDescriptionOfConcept(),found.toStringBrief());
			}
		}
		return found;
	}
	

	/**
	 * <p>Read the DICOM input file and extract anatomical information.</p>
	 *
	 * @param	arg	array of one string, the filename to read
	 */
	public static void main(String arg[]) {
		if (arg.length == 1) {
			String inputFileName = arg[0];
			try {
				AttributeList list = new AttributeList();
				//list.read(inputFileName);
				list.read(inputFileName,null,true,true,TagFromName.PixelData);
				DisplayableAnatomicConcept anatomy = findAnatomicConcept(list);
				if (anatomy != null) {
					slf4jlogger.info(anatomy.toString());
				}
				else {
					slf4jlogger.info("########################### - ANATOMY NOT FOUND - ###########################");
				}
			} catch (Exception e) {
			slf4jlogger.error("",e);	// use SLF4J since may be invoked from script
			}
		}
	}
	
}


	
