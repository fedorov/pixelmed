/* Copyright (c) 2001-2025, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.dicom;

import java.io.*;
import java.util.*;

/**
 * <p>A class to encapsulate the information related to Coding Scheme Identification encoded in composite instances within CodingSchemeIdentificationSequence.</p>
 *
 * <p>Includes a "dictionary" of various commonly used coding schemes and mappings between their CodingSchemeDesignators and OIDs, as well
 * as convenience methods to add and extract what coding schemes are used within an instance.</p>
 *
 * @author	dclunie
 */
public class CodingSchemeIdentification {

	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicom/CodingSchemeIdentification.java,v 1.19 2025/01/29 10:58:06 dclunie Exp $";

	protected List<CodingSchemeIdentificationItem> listOfItems;

	public static final String REGISTRY_HL7 = "HL7";
	
	public static final CodingSchemeIdentificationItem ACR = new CodingSchemeIdentificationItem("ACR",REGISTRY_HL7,"2.16.840.1.113883.6.76","ACR Index for Radiological Diagnosis");
	public static final CodingSchemeIdentificationItem ASTM_SIG = new CodingSchemeIdentificationItem("ASTM-sigpurpose",null/*registry*/,"1.2.840.10065.1.12","ASTM E 2084 Signature Purpose codes");
	public static final CodingSchemeIdentificationItem C4 = new CodingSchemeIdentificationItem("C4",REGISTRY_HL7,"2.16.840.1.113883.6.12","CPT-4");
	public static final CodingSchemeIdentificationItem C5 = new CodingSchemeIdentificationItem("C5",REGISTRY_HL7,"2.16.840.1.113883.6.82","CPT-5");
	public static final CodingSchemeIdentificationItem caDSR = new CodingSchemeIdentificationItem("caDSR",null/*registry*/,"2.16.840.1.113883.3.26.2","Cancer Data Standard Repository");
	public static final CodingSchemeIdentificationItem CD2 = new CodingSchemeIdentificationItem("CD2",REGISTRY_HL7,"2.16.840.1.113883.6.13","American Dental Association Current Dental Terminology 2");
	public static final CodingSchemeIdentificationItem CTV3 = new CodingSchemeIdentificationItem("CTV3",REGISTRY_HL7,"2.16.840.1.113883.6.6","Clinical Terms Version 3");
	public static final CodingSchemeIdentificationItem DC = new CodingSchemeIdentificationItem("DC",null/*registry*/,"1.2.840.10008.2.16.10","Dublin Core");
	public static final CodingSchemeIdentificationItem DCM = new CodingSchemeIdentificationItem("DCM",REGISTRY_HL7,"1.2.840.10008.2.16.4","DICOM Controlled Terminology");
	public static final CodingSchemeIdentificationItem DCMUID = new CodingSchemeIdentificationItem("DCMUID",null/*registry*/,"1.2.840.10008.2.6.1","DICOM UID Registry");
	public static final CodingSchemeIdentificationItem FMA = new CodingSchemeIdentificationItem("FMA",REGISTRY_HL7,"2.16.840.1.113883.6.119","FMA");
	public static final CodingSchemeIdentificationItem HPC = new CodingSchemeIdentificationItem("HPC",REGISTRY_HL7,"2.16.840.1.113883.6.14","Healthcare Financing Administration (HCFA) Common Procedure CodingSystem (HCPCS)");
	public static final CodingSchemeIdentificationItem I10 = new CodingSchemeIdentificationItem("I10",REGISTRY_HL7,"2.16.840.1.113883.6.3","ICD10");
	public static final CodingSchemeIdentificationItem I10C = new CodingSchemeIdentificationItem("I10C",REGISTRY_HL7,"2.16.840.1.113883.6.90","ICD-10-CM");
	public static final CodingSchemeIdentificationItem I10P = new CodingSchemeIdentificationItem("I10P",REGISTRY_HL7,"2.16.840.1.113883.6.4","ICD-10-PCS");
	public static final CodingSchemeIdentificationItem I11 = new CodingSchemeIdentificationItem("I11",null/*registry*/,"1.2.840.10008.2.16.16","ICD11");	// HL7 OID reg shows 2.16.840.1.113883.6.347 - not what is in DICOM PS3.16 Section 8 table :(
	public static final CodingSchemeIdentificationItem I9 = new CodingSchemeIdentificationItem("I9",REGISTRY_HL7,"2.16.840.1.113883.6.42","ICD9");
	public static final CodingSchemeIdentificationItem I9C = new CodingSchemeIdentificationItem("I9C",REGISTRY_HL7,"2.16.840.1.113883.6.2","ICD9-CM");
	public static final CodingSchemeIdentificationItem IBSI = new CodingSchemeIdentificationItem("IBSI",null/*registry*/,"1.2.840.10008.2.16.13","Image Biomarker Standardisation Initiative");
	public static final CodingSchemeIdentificationItem ICDO3 = new CodingSchemeIdentificationItem("ICDO3",REGISTRY_HL7,"2.16.840.1.113883.6.43.1","ICD-O-3");
	public static final CodingSchemeIdentificationItem ISO3166_1 = new CodingSchemeIdentificationItem("ISO3166_1",REGISTRY_HL7,"2.16.1","ISO 2 letter country codes");
	public static final CodingSchemeIdentificationItem ISO639_1 = new CodingSchemeIdentificationItem("ISO639_1",REGISTRY_HL7,"2.16.840.1.113883.6.99","ISO 3 letter language codes");
	public static final CodingSchemeIdentificationItem ISO639_2 = new CodingSchemeIdentificationItem("ISO639_2",REGISTRY_HL7,"2.16.840.1.113883.6.100","ISO 3 letter language codes");
	public static final CodingSchemeIdentificationItem ITIS_TSN = new CodingSchemeIdentificationItem("ITIS_TSN",null/*registry*/,"1.2.840.10008.2.16.7","ITIS TSN");
	public static final CodingSchemeIdentificationItem LN = new CodingSchemeIdentificationItem("LN",REGISTRY_HL7,"2.16.840.1.113883.6.1","LOINC");
	public static final CodingSchemeIdentificationItem MA = new CodingSchemeIdentificationItem("MA",null/*registry*/,"1.2.840.10008.2.16.5","Adult Mouse Anatomy Ontology");
	public static final CodingSchemeIdentificationItem MAYOASRG = new CodingSchemeIdentificationItem("MAYOASRG",null/*registry*/,"1.2.840.10008.2.16.12","Mayo Clinic Non-radiological Images Specific Body Structure Anatomical Surface Region Guide");
	public static final CodingSchemeIdentificationItem MDC = new CodingSchemeIdentificationItem("MDC",REGISTRY_HL7,"2.16.840.1.113883.6.24","ISO/IEEE 11073 Medical Device Nomenclature");
	public static final CodingSchemeIdentificationItem MGI = new CodingSchemeIdentificationItem("MGI",null/*registry*/,"1.2.840.10008.2.16.8","MGI");
	public static final CodingSchemeIdentificationItem MSH = new CodingSchemeIdentificationItem("MSH",REGISTRY_HL7,"2.16.840.1.113883.6.177","MeSH");
	public static final CodingSchemeIdentificationItem NBD = new CodingSchemeIdentificationItem("NBD",null/*registry*/,"2.16.840.1.113883.15.2","NASPE/BPEG Defibrillator Code");
	public static final CodingSchemeIdentificationItem NBG = new CodingSchemeIdentificationItem("NBG",null/*registry*/,"2.16.840.1.113883.15.3","NASPE/BPEG Generic Pacemaker Code");
	public static final CodingSchemeIdentificationItem NCIt = new CodingSchemeIdentificationItem("NCIt",REGISTRY_HL7,"2.16.840.1.113883.3.26.1.1","NCI Thesaurus");
	public static final CodingSchemeIdentificationItem NDC = new CodingSchemeIdentificationItem("NDC",REGISTRY_HL7,"2.16.840.1.113883.6.69","National Drug Code Directory");
	public static final CodingSchemeIdentificationItem NEU = new CodingSchemeIdentificationItem("NEU",REGISTRY_HL7,"2.16.840.1.113883.6.210","NeuroNames");
	public static final CodingSchemeIdentificationItem NICIP = new CodingSchemeIdentificationItem("NICIP",null/*registry*/,"2.16.840.1.113883.2.1.3.2.4.21","NICIP");
	public static final CodingSchemeIdentificationItem NYUMCCG = new CodingSchemeIdentificationItem("NYUMCCG",null/*registry*/,"1.2.840.10008.2.16.11","New York University Melanoma Clinical Cooperative Group");
	public static final CodingSchemeIdentificationItem PATHLEX = new CodingSchemeIdentificationItem("PATHLEX",null/*registry*/,"1.3.6.1.4.1.19376.1.8.2.1","PathLex");
	public static final CodingSchemeIdentificationItem POS = new CodingSchemeIdentificationItem("POS",REGISTRY_HL7,"2.16.840.1.113883.6.50","HCFA Place of Service (POS) Codes for Professional Claims");
	public static final CodingSchemeIdentificationItem PUBCHEM_CID = new CodingSchemeIdentificationItem("PUBCHEM_CID",null/*registry*/,"1.2.840.10008.2.16.9","PubChem");
	public static final CodingSchemeIdentificationItem RADELEMENT = new CodingSchemeIdentificationItem("RADELEMENT",null/*registry*/,"1.2.840.10008.2.16.15","RadElement");
	public static final CodingSchemeIdentificationItem RADLEX = new CodingSchemeIdentificationItem("RADLEX",REGISTRY_HL7,"2.16.840.1.113883.6.256","RadLex");
	public static final CodingSchemeIdentificationItem RFC3066 = new CodingSchemeIdentificationItem("RFC3066",REGISTRY_HL7,"2.16.840.1.113883.6.121","RFC 3066");
	public static final CodingSchemeIdentificationItem RFC5646 = new CodingSchemeIdentificationItem("RFC5646",REGISTRY_HL7,"2.16.840.1.113883.6.316","RFC 5646");
	public static final CodingSchemeIdentificationItem RO = new CodingSchemeIdentificationItem("RO",null/*registry*/,"1.2.840.10008.2.16.14","Radiomics Ontology");
	public static final CodingSchemeIdentificationItem RXNORM = new CodingSchemeIdentificationItem("RXNORM",REGISTRY_HL7,"2.16.840.1.113883.6.88","RXNORM");
	public static final CodingSchemeIdentificationItem SCT = new CodingSchemeIdentificationItem("SCT",REGISTRY_HL7,"2.16.840.1.113883.6.96","SNOMED CT using SNOMED-CT style values");
	public static final CodingSchemeIdentificationItem SDM = new CodingSchemeIdentificationItem("99SDM",REGISTRY_HL7,"2.16.840.1.113883.6.53","SDM");
	public static final CodingSchemeIdentificationItem SNM3 = new CodingSchemeIdentificationItem("SNM3",REGISTRY_HL7,"2.16.840.1.113883.6.51","SNOMED V3");
	public static final CodingSchemeIdentificationItem SRT = new CodingSchemeIdentificationItem("SRT",REGISTRY_HL7,"2.16.840.1.113883.6.96","SNOMED CT using SNOMED-RT style values");
	public static final CodingSchemeIdentificationItem UBERON = new CodingSchemeIdentificationItem("UBERON",null/*registry*/,"1.2.840.10008.2.16.6","UBERON");
	public static final CodingSchemeIdentificationItem UCUM = new CodingSchemeIdentificationItem("UCUM",REGISTRY_HL7,"2.16.840.1.113883.6.8","Unified Code for Units of Measure");
	public static final CodingSchemeIdentificationItem UMLS = new CodingSchemeIdentificationItem("UMLS",REGISTRY_HL7,"2.16.840.1.113883.6.86","UMLS codes as CUIs making up the values in a coding system");
	public static final CodingSchemeIdentificationItem UPC = new CodingSchemeIdentificationItem("UPC",REGISTRY_HL7,"2.16.840.1.113883.6.55","Universal Product Code - Universal Code Council");
	public static final CodingSchemeIdentificationItem Private_99_OFFIS_DCMTK = new CodingSchemeIdentificationItem("99_OFFIS_DCMTK",null/*registry*/,"1.2.276.0.7230010.3.0.0.1","OFFIS DCMTK");
	public static final CodingSchemeIdentificationItem Private_99PMP = new CodingSchemeIdentificationItem("99PMP",null/*registry*/,VersionAndConstants.codingSchemeUIDFor99PMP,"PixelMed Publishing");
	public static final CodingSchemeIdentificationItem Private_99IPCMR = new CodingSchemeIdentificationItem("99IPCMR",null/*registry*/,VersionAndConstants.codingSchemeUIDFor99IPCMR,"Imaging Procedure Code Mapping Resource");
	public static final CodingSchemeIdentificationItem Private_99QIICR = new CodingSchemeIdentificationItem("99QIICR",null/*registry*/,"1.3.6.1.4.1.43046.3.0.0","NCI QIICR Project 3DSlicer BWH");

	protected static CodingSchemeIdentificationItem[] knownCodingSchemes = {
		ACR,
		ASTM_SIG,
		C4,
		C5,
		caDSR,
		CD2,
		CTV3,
		DC,
		DCM,
		DCMUID,
		FMA,
		HPC,
		I10,
		I10C,
		I10P,
		I11,
		I9,
		I9C,
		IBSI,
		ICDO3,
		ISO3166_1,
		ISO639_1,
		ISO639_2,
		ITIS_TSN,
		LN,
		MA,
		MAYOASRG,
		MDC,
		MGI,
		MSH,
		NBD,
		NBG,
		NCIt,
		NDC,
		NEU,
		NICIP,
		NYUMCCG,
		PATHLEX,
		POS,
		PUBCHEM_CID,
		RADELEMENT,
		RADLEX,
		RFC3066,
		RFC5646,
		RO,
		RXNORM,
		SCT,
		SDM,
		SNM3,
		SRT,
		UBERON,
		UCUM,
		UMLS,
		UPC,
		Private_99_OFFIS_DCMTK,
		Private_99PMP,
		Private_99IPCMR,
		Private_99QIICR
	};
	
	protected static CodingSchemeIdentificationItem lookupByCodingSchemeDesignator(String csd) {
		CodingSchemeIdentificationItem found = null;
		if (csd != null && csd.length() > 0) {
			for (CodingSchemeIdentificationItem item: knownCodingSchemes) {
				if (item.getCodingSchemeDesignator().equals(csd)) {
					found = item;
					break;
				}
			}
		}
		return found;
	}

	/**
	 * <p>Construct a CodingSchemeIdentification instance from the CodingSchemeIdentificationSequence in the supplied list.</p>
	 *
	 * @param	list	the list in which to look for the CodingSchemeIdentificationSequence attribute
	 */
	public CodingSchemeIdentification(AttributeList list) {
		if (list != null) {
			Attribute a = list.get(TagFromName.CodingSchemeIdentificationSequence);
			if (a != null && a instanceof SequenceAttribute) {
				SequenceAttribute csis = (SequenceAttribute)a;
				int n = csis.getNumberOfItems();
				if (n > 0) {
					listOfItems = new ArrayList<CodingSchemeIdentificationItem>();
					for (int i=0; i<n; ++i) {
						SequenceItem si = csis.getItem(i);
						CodingSchemeIdentificationItem item = new CodingSchemeIdentificationItem(si);
						listOfItems.add(item);
					}
				}
			}
		}
	}
	
	/**
	 * <p>Construct a CodingSchemeIdentification from a list of CodingSchemeIdentificationItems.</p>
	 *
	 * @param	listOfItems		may be null if none (yet)
	 */
	public CodingSchemeIdentification(List<CodingSchemeIdentificationItem> listOfItems) {
		this.listOfItems = listOfItems;
	}
	
	/**
	 * <p>Get as a CodingSchemeIdentificationSequence attribute.</p>
	 *
	 * @return		a SequenceAttribute with one item per coding scheme, or null if no coding schemes
	 */
	public SequenceAttribute getAsSequenceAttribute() {
		SequenceAttribute a = null;
		if (listOfItems != null) {
			a = new SequenceAttribute(TagFromName.CodingSchemeIdentificationSequence);
			for (CodingSchemeIdentificationItem item: listOfItems) {
				SequenceItem si = item.getAsSequenceItem();
//System.err.println("CodingSchemeIdentificationItem.getAsSequenceAttribute(): adding "+si);
				a.addItem(si);
			}
		}
		return a;
	}
	
	/**
	 * <p>Get details of a particular coding scheme by looking up by CodingSchemeDesignator value.</p>
	 *
	 * @param	codingSchemeDesignator	coding scheme designator
	 * @return		a CodingSchemeIdentificationItem, or null if not found
	 */
	public CodingSchemeIdentificationItem getByCodingSchemeDesignator(String codingSchemeDesignator) {
		CodingSchemeIdentificationItem found = null;
		if (listOfItems != null && codingSchemeDesignator != null && codingSchemeDesignator.length() > 0) {
			for (CodingSchemeIdentificationItem item: listOfItems) {
//System.err.println("CodingSchemeIdentificationItem.getByCodingSchemeDesignator(): checking "+item);
				if (item.getCodingSchemeDesignator().equals(codingSchemeDesignator)) {
//System.err.println("CodingSchemeIdentificationItem.getByCodingSchemeDesignator(): found "+item);
					found = item;
				}
			}
		}
		return found;
	}
	
	protected static void recursivelyCollectCodingSchemeDesignators(AttributeList list,Set<String> schemesFound) {
		Iterator i = list.values().iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (o instanceof SequenceAttribute) {
				SequenceAttribute a = (SequenceAttribute)o;
				Iterator items = a.iterator();
				if (items != null) {
					while (items.hasNext()) {
						SequenceItem item = (SequenceItem)(items.next());
						if (item != null) {
							AttributeList itemAttributeList = item.getAttributeList();
							if (itemAttributeList != null) {
								recursivelyCollectCodingSchemeDesignators(itemAttributeList,schemesFound);
							}
						}
					}
				}
			}
			else {
				Attribute a = (Attribute)o;
				AttributeTag tag = a.getTag();
				if (tag.equals(TagFromName.CodingSchemeDesignator)) {
					String csd = a.getSingleStringValueOrEmptyString();
					if (csd.length() > 0) {
//System.err.println("CodingSchemeIdentificationItem.recursivelyCollectCodingSchemeDesignators(): added "+csd);
						schemesFound.add(csd);
					}
				}
			}
		}
	}

	/**
	 * <p>Build a new CodingSchemeIdentification instance by examining all uses of CodedSequenceItems within the supplied list.</p>
	 *
	 * @param	list	the list in which to look for the CodedSequenceItem attribute
	 * @return			a new CodingSchemeIdentification, or null if none found
	 */
	public static CodingSchemeIdentification getCodingSchemesFromExistingAttributeList(AttributeList list) {
//System.err.print("CodingSchemeIdentificationItem.getCodingSchemesFromExistingAttributeList(): list ="+list);
		Set<String> schemesFound = new TreeSet<String>();
		recursivelyCollectCodingSchemeDesignators(list,schemesFound);
		
		List<CodingSchemeIdentificationItem> listOfItems = null;
		for (String csd: schemesFound) {
//System.err.println("CodingSchemeIdentificationItem.getCodingSchemesFromExistingAttributeList(): found "+csd);
			CodingSchemeIdentificationItem item = lookupByCodingSchemeDesignator(csd);
			if (item != null) {
				if (listOfItems == null) {
					listOfItems = new ArrayList<CodingSchemeIdentificationItem>();
				}
				listOfItems.add(item);
			}
		}
		
		return new CodingSchemeIdentification(listOfItems);
	}


	/**
	 * <p>Replace any existing CodingSchemeIdentificationSequence in the list with information gathered by examining all uses of CodedSequenceItems within the list.</p>
	 *
	 * @param	list	the list in which to look for the CodedSequenceItem attribute
	 */
	public static void replaceCodingSchemeIdentificationSequenceWithCodingSchemesUsedInAttributeList(AttributeList list) {
		CodingSchemeIdentification csi = CodingSchemeIdentification.getCodingSchemesFromExistingAttributeList(list);
		SequenceAttribute a = csi.getAsSequenceAttribute();
		if (a != null) {
			list.put(a);
		}
	}


}

