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
 * <p>This class encapsulates information pertaining to anatomy of projection x-ray images.</p>
 * 
 * <p>Utility methods provide for the detection of anatomy from various header attributes regardless
 * of whether these are formal codes, code strings or free text comments.</p>
 * 
 * @author	dclunie
 */
public class ProjectionXRayAnatomy extends Anatomy {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/anatproc/ProjectionXRayAnatomy.java,v 1.40 2026/05/19 14:38:44 dclunie Exp $";

	private static final Logger slf4jlogger = LoggerFactory.getLogger(ProjectionXRayAnatomy.class);
	
	protected static String[] newStringArray(String... values) { return values; }		// use 1.5 varargs feature; seems like a lot of trouble to work around lack of string array curly braces outside declarations
	
	protected static String[] badLateralityWords = null;
	
	protected static DisplayableLateralityConcept[] lateralityConceptEntries = {
		new DisplayableLateralityConcept("C0205090","24028007",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"24028007",	"G-A100",	"Right",			"R",
			newStringArray(
				"RT",
				"Rechts"/*NL*/,
				"DROITE"/*FR*/
			),
			newStringArray("Right"),	newStringArray("Right")),
		new DisplayableLateralityConcept("C0205091","7771000",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"7771000",	"G-A101",	"Left",				"L",
			newStringArray(
				"LT",
				"Links"/*NL*/,
				"GAUCHE"/*FR*/
			),
			newStringArray("Right"),	newStringArray("Left")),
		new DisplayableLateralityConcept("C0238767","51440002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"51440002",	"G-A102",	"Right and left",	"B",
			newStringArray(
				"Both",
				"Bilateral"
			),
			newStringArray("Both"),	newStringArray("Both")),
		new DisplayableLateralityConcept("C0205092","66459002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"66459002",	"G-A103",	"Unilateral",		"U",
			newStringArray(
				"Unpaired"
			),
			newStringArray("Unpaired"),	newStringArray("Unpaired")),
	};
	
	protected static DictionaryOfConcepts lateralityConcepts = new DictionaryOfConcepts(lateralityConceptEntries,badLateralityWords,"Laterality");

	public static DictionaryOfConcepts getLateralityConcepts() { return lateralityConcepts; }

	protected static String[] badViewWords = null;

	protected static DisplayableViewConcept[] viewPositionConceptEntries = {
		new DisplayableViewConcept("C0442223","399033003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399033003",	"R-10202",	"frontal",					"FRONTAL"/*non-standard*/,
			newStringArray(
				"Face"/*FR*/
			),
			newStringArray("Frontal"),	newStringArray("Frontal")),
		new DisplayableViewConcept("C1302231","399160007",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399160007",	"R-10204",	"frontal oblique",			"FO"/*non-standard*/,
			null,
			newStringArray("frontal oblique"),	newStringArray("frontal oblique")),
		new DisplayableViewConcept("C0442212","399348003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399348003",	"R-10206",	"antero-posterior",			"AP",
			newStringArray(
				"AP",
				"A.P",
				"antero_posterior",
				"anteroposterior"
			),
			newStringArray("AP"),		newStringArray("AP")),
		new DisplayableViewConcept("C1302318","399312000",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399312000",	"R-10208",	"antero-posterior oblique",	"APO"/*non-standard*/,
			null,
			newStringArray("antero-posterior oblique"),	newStringArray("antero-posterior oblique")),
		new DisplayableViewConcept("C1275807","399038007",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399038007",	"R-10210",	"right posterior oblique",	"RPO"/*non-standard*/,
			null,
			newStringArray("right posterior oblique"),	newStringArray("right posterior oblique")),
		new DisplayableViewConcept("C1275802","399006002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399006002",	"R-10212",	"left posterior oblique",	"LPO"/*non-standard*/,
			null,
			newStringArray("left posterior oblique"),	newStringArray("left posterior oblique")),
		new DisplayableViewConcept("C0457409","399066004",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399066004",	"R-10214",	"postero-anterior",			"PA",
			newStringArray(
				"PA",
				"P.A",
				"postero_anterior",
				"posteroanterior"
			),
			newStringArray("PA"),		newStringArray("PA")),
		new DisplayableViewConcept("C1275812","399059000",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399059000",	"R-10216",	"postero-anterior oblique",	"PAO"/*non-standard*/,
			null,
			newStringArray("postero-anterior oblique"),	newStringArray("postero-anterior oblique")),
		new DisplayableViewConcept("C1275852","399356000",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399356000",	"R-40985",	"right anterior oblique",	"RAO"/*non-standard*/,
			null,
			newStringArray("right anterior oblique"),	newStringArray("right anterior oblique")),
		new DisplayableViewConcept("C1275823","399135007",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399135007",	"R-10220",	"left anterior oblique",	"LAO"/*non-standard*/,
			null,
			newStringArray("left anterior oblique"),	newStringArray("left anterior oblique")),
		new DisplayableViewConcept("C0205129","30730003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"30730003",	"G-A145",	"sagittal",					"SAG"/*non-standard*/,
			null,
			newStringArray("sagittal"),	newStringArray("sagittal")),
		new DisplayableViewConcept("C1302283","399260004",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399260004",	"R-10224",	"medial-lateral",			"ML"/*non-standard*/,
			null,
			newStringArray("medial-lateral"),	newStringArray("medial-lateral")),
		new DisplayableViewConcept("C0442295","260427002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"260427002",	"R-40783",	"lateral oblique",			"LO"/*non-standard*/,
			null,
			newStringArray("lateral oblique"),	newStringArray("lateral oblique")),
		new DisplayableViewConcept("C1302336","399352003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399352003",	"R-10228",	"lateral-medial",			"LM"/*non-standard*/,
			null,
			newStringArray("lateral-medial"),	newStringArray("lateral-medial")),
		new DisplayableViewConcept("C0442294","260426006",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"260426006",	"R-40782",	"medial oblique",			"MO"/*non-standard*/,
			null,
			newStringArray("medial oblique"),	newStringArray("medial oblique")),
		new DisplayableViewConcept("C0442202","399198007",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399198007",	"R-10232",	"right lateral",			"RL",
			newStringArray(
				"RL",
				"RLD",	/*decubitus ... included as a standard View Position defined term*/
				"RLat",
				"R Lat"
			),
			newStringArray("Right Lateral"),	newStringArray("Right Lateral")),
		new DisplayableViewConcept("C0442291","399236003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399236003",	"R-10234",	"right oblique",			"RLO",
			null,
			newStringArray("right oblique"),	newStringArray("right oblique")),
		new DisplayableViewConcept("C0442198","399173006",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399173006",	"R-10236",	"left lateral",				"LL",
			newStringArray(
				"LL",
				"LLD",	/*decubitus ... included as a standard View Position defined term*/
				"LLat",
				"L Lat"
			),
			newStringArray("Left Lateral"),	newStringArray("Left Lateral")),
		new DisplayableViewConcept("C0442288","399184004",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399184004",	"R-10238",	"left oblique",				"LLO",
			null,
			newStringArray("left oblique"),	newStringArray("left oblique")),
		new DisplayableViewConcept("C0442269","399061009",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399061009",	"R-10241",	"axial",					"AX"/*non-standard*/,
			null,
			newStringArray("axial"),	newStringArray("axial")),
		new DisplayableViewConcept("C0442215","399162004",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399162004",	"R-10242",	"cranio-caudal",			"CC"/*non-standard*/,
			null,
			newStringArray("cranio-caudal"),	newStringArray("cranio-caudal")),
		new DisplayableViewConcept("C1302249","399196006",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399196006",	"R-10244",	"caudo-cranial",			null,
			null,
			newStringArray("caudo-cranial"),	newStringArray("caudo-cranial")),
		new DisplayableViewConcept("C1302164","399004004",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399004004",	"R-10246",	"oblique axial",			null,
			null,
			newStringArray("oblique axial"),	newStringArray("oblique axial")),
		new DisplayableViewConcept("C1302302","399288005",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399288005",	"R-10248",	"oblique cranio-caudal",	null,
			null,
			newStringArray("oblique cranio-caudal"),	newStringArray("oblique cranio-caudal")),
		new DisplayableViewConcept("C1302262","399225005",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399225005",	"R-10250",	"oblique caudo-cranial",	null,
			null,
			newStringArray("oblique caudo-cranial"),	newStringArray("oblique caudo-cranial")),
		new DisplayableViewConcept("C1275822","399132005",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399132005",	"R-10252",	"frontal-oblique axial",	"FOA"/*non-standard*/,
			null,
			newStringArray("frontal-oblique axial"),	newStringArray("frontal-oblique axial")),
		new DisplayableViewConcept("C1275850","399325008",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399325008",	"R-10254",	"sagittal-oblique axial",	"SOA"/*non-standard*/,
			null,
			newStringArray("sagittal-oblique axial"),	newStringArray("sagittal-oblique axial")),
		new DisplayableViewConcept("C0442287","399182000",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399182000",	"R-102C1",	"oblique",					"OBL"/*non-standard*/,
			null,
			newStringArray("oblique"),	newStringArray("oblique")),
		new DisplayableViewConcept("C0442197","399067008",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399067008",	"R-102CD",	"lateral",					"LATERAL"/*non-standard*/,
			newStringArray(
				"Lat",
				"Profil"/*FR*/,
				"Sida"/*SE*/
			),
			newStringArray("Lateral"),	newStringArray("Lateral")),
		new DisplayableViewConcept("C0442227","399110001",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399110001",	"R-102C2",	"tangential",				"TAN"/*non-standard*/,
			null,
			newStringArray("tangential"),	newStringArray("tangential")),
		new DisplayableViewConcept("C0442244","399255003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399255003",	"R-10256",	"submentovertical",			"SMV"/*non-standard*/,
			null,
			newStringArray("submentovertical"),	newStringArray("submentovertical")),
		new DisplayableViewConcept("C1302340","399360002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399360002",	"R-10257",	"verticosubmental",			"VSM"/*non-standard*/,
			null,
			newStringArray("verticosubmental"),	newStringArray("verticosubmental")),
		new DisplayableViewConcept("C1302192","399071006",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399071006",	"R-102C3",	"plantodorsal",				"PD"/*non-standard*/,
			null,
			newStringArray("plantodorsal"),	newStringArray("plantodorsal")),
		new DisplayableViewConcept("C1302328","399335002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399335002",	"R-102C4",	"dorsoplantar",				"DP"/*non-standard*/,
			null,
			newStringArray("dorsoplantar"),	newStringArray("dorsoplantar")),
		new DisplayableViewConcept("C1302290","399272005",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399272005",	"R-102C5",	"parietoacanthial",			"PAC"/*non-standard*/,
			null,
			newStringArray("parietoacanthial"),	newStringArray("parietoacanthial")),
		new DisplayableViewConcept("C1302273","399242004",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399242004",	"R-102C6",	"acanthioparietal",			"ACP"/*non-standard*/,
			null,
			newStringArray("acanthioparietal"),	newStringArray("acanthioparietal")),
		new DisplayableViewConcept("C1302335","399351005",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399351005",	"R-102C7",	"orbitoparietal",			"OP"/*non-standard*/,
			null,
			newStringArray("orbitoparietal"),	newStringArray("orbitoparietal")),
		new DisplayableViewConcept("C1302320","399316002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399316002",	"R-102C8",	"parieto-orbital",			"PO"/*non-standard*/,
			null,
			newStringArray("parieto-orbital"),	newStringArray("parieto-orbital")),
		new DisplayableViewConcept("C1302201","399099002",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399099002",	"R-10230",	"latero-medial oblique",	"LMO"/*non-standard*/,
			null,
			newStringArray("latero-medial oblique"),	newStringArray("latero-medial oblique")),
		new DisplayableViewConcept("C1302345","399368009",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"399368009",	"R-10226",	"medio-lateral oblique",	"MLO"/*non-standard*/,
			null,
			newStringArray("medio-lateral oblique"),	newStringArray("medio-lateral oblique")),
		new DisplayableViewConcept("C1292533","119376003",	"SCT",	"SRT",	CodedConcept.srtLegacyCodingSchemeDesignators,	null,	"119376003",	"G-8300",	"tissue specimen",			"TISSUE"/*non-standard*/,
			newStringArray(
				"Tissue",
				"Specimen"
			),
			newStringArray("tissue specimen"),	newStringArray("tissue specimen"))
	};

	protected static DictionaryOfConcepts viewPositionConcepts = new DictionaryOfConcepts(viewPositionConceptEntries,badViewWords,"View");

	public static DisplayableViewConcept findView(AttributeList list) {
		// strategy is to look in specific attributes first, then general, and look in codes before free text ...
		DisplayableConcept view = null;
		{
			CodedSequenceItem viewCodeSequence = CodedSequenceItem.getSingleCodedSequenceItemOrNull(list,TagFromName.ViewCodeSequence);
			if (viewCodeSequence != null) {
				slf4jlogger.debug("findView(): viewCodeSequence = {}",viewCodeSequence);
				view = viewPositionConcepts.findCodeInEntriesFirstThenTryCodeMeaningInEntriesThenTryLongestIndividualEntryContainedWithinCodeMeaning(viewCodeSequence);
				if (view != null) slf4jlogger.debug("findView(): found View in ViewCodeSequence = {}",view.toStringBrief());
			}
		}
		if (view == null) {
			String viewPosition = Attribute.getSingleStringValueOrNull(list,TagFromName.ViewPosition);
			if (viewPosition != null) {
				slf4jlogger.debug("findView(): bodyPartExamined = {}",viewPosition);
				view = viewPositionConcepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(viewPosition);
				if (view != null) slf4jlogger.debug("findView(): found View in ViewPosition = {}",view.toStringBrief());
			}
		}
		//if (view == null) {
		//	String bodyPartExamined = Attribute.getSingleStringValueOrNull(list,TagFromName.BodyPartExamined);		// view should NOT be encoded in BodyPartExamined, but sometimes is :(
		//	if (bodyPartExamined != null) {
		//		slf4jlogger.debug("findAnatomicConcept(): bodyPartExamined = {}",bodyPartExamined);
		//		view = viewPositionConcepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(bodyPartExamined);
		//		if (view != null) slf4jlogger.debug("findView(): found in BodyPartExamined = {}",view.toStringBrief());
		//	}
		//}
		if (view == null) {
			view = findAmongstGeneralAttributes(list,viewPositionConcepts,badLateralityOrViewOrAnatomyPhraseTriggers);
		}
		return (DisplayableViewConcept)view;
	}

	public static DisplayableLateralityConcept findLaterality(AttributeList list,DisplayableAnatomicConcept anatomy) {
		// strategy is to look in specific attributes first, then check if unpaired anatomy prior to searching general attributes ...
		DisplayableConcept laterality = null;
		{
			String imageLaterality = Attribute.getSingleStringValueOrNull(list,TagFromName.ImageLaterality);
			if (imageLaterality != null) {
				slf4jlogger.debug("findLaterality(): imageLaterality = {}",imageLaterality);
				laterality = lateralityConcepts.findCodeStringExact(imageLaterality);
				if (laterality != null) slf4jlogger.debug("findLaterality(): found Laterality in ImageLaterality = {}",laterality.toStringBrief());
			}
		}
		if (laterality == null) {
			String vLaterality = Attribute.getSingleStringValueOrNull(list,TagFromName.Laterality);
			if (vLaterality != null) {
				slf4jlogger.debug("findLaterality(): laterality = {}",vLaterality);
				laterality = lateralityConcepts.findCodeStringExact(vLaterality);
				if (laterality != null) slf4jlogger.debug("findLaterality(): found Laterality in Laterality = {}",laterality.toStringBrief());
			}
		}
		if (laterality == null) {
			if (anatomy != null && !anatomy.isPairedStructure()) {
				laterality = lateralityConcepts.findCodeStringExact("U");
				if (laterality != null) slf4jlogger.debug("findLaterality(): anatomy is unpaired structure so use for Laterality = {}",laterality.toStringBrief());
			}
		}
		if (laterality == null) {
			String viewPosition = Attribute.getSingleStringValueOrNull(list,TagFromName.ViewPosition);
			if (viewPosition != null) {
				slf4jlogger.debug("findView(): bodyPartExamined = {}",viewPosition);
				laterality = lateralityConcepts.findInEntriesFirstThenTryLongestIndividualEntryContainedWithin(viewPosition);
				if (laterality != null) slf4jlogger.debug("findLaterality(): found Laterality in ViewPosition = {}",laterality.toStringBrief());
			}
		}
		if (laterality == null) {
			laterality = findAmongstGeneralAttributes(list,lateralityConcepts,badLateralityOrViewOrAnatomyPhraseTriggers);
		}
		return (DisplayableLateralityConcept)laterality;
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
				list.read(inputFileName);
				DisplayableAnatomicConcept anatomy = findAnatomicConcept(list);
				if (anatomy != null) {
					slf4jlogger.info(anatomy.toString());
				}
				else {
					slf4jlogger.info("########################### - ANATOMY NOT FOUND - ###########################");
				}
				DisplayableViewConcept view = findView(list);
				if (view != null) {
					slf4jlogger.info(view.toString());
				}
				else {
					slf4jlogger.info("########################### - VIEW NOT FOUND - ###########################");
				}
				DisplayableLateralityConcept laterality = findLaterality(list,anatomy);
				if (laterality != null) {
					slf4jlogger.info(laterality.toString());
				}
				else {
					slf4jlogger.info("########################### - LATERALITY NOT FOUND - ###########################");
				}
			} catch (Exception e) {
				slf4jlogger.error("",e);	// use SLF4J since may be invoked from script
			}
		}
	}
	
}


	
