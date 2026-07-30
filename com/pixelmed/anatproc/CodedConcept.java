/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.anatproc;

import com.pixelmed.dicom.CodedSequenceItem;
import com.pixelmed.dicom.DicomException;

import com.pixelmed.utils.StringUtilities;

/**
 * <p>This class represents a concept that has a coded representation.</p>
 * 
 * @author	dclunie
 */
public class CodedConcept extends Concept {

	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/anatproc/CodedConcept.java,v 1.19 2026/05/19 14:38:44 dclunie Exp $";
	
	static public String[] srtLegacyCodingSchemeDesignators = { "SNM3", "99SDM" };
	
	protected String conceptIdentifier;						// the scheme-specific concept identifier, e.g., for SNOMED-CT, the SNOMED Concept Identifier
	protected String codingSchemeDesignator;				// e.g., "SCT"
	protected String legacyCodingSchemeDesignator;			// e.g.  "SRT", not "SNM3" if what is used in DICOM context groups instead of "SRT"; null if none required (i.e., same as codingSchemeDesignator)
	protected String[] otherLegacyCodingSchemeDesignators;	// e.g.  "SNM3" if what is used in DICOM context groups instead of "SRT"; null if none required (i.e., same as codingSchemeDesignator)
	protected String codingSchemeVersion;					// null if none required
	protected String codeValue;								// may be same as conceptIdentifier, e.g., for SCT
	protected String legacyCodeValue;						// e.g., for SRT rather than SCT
	protected String codeMeaning;
	
	protected String codeStringEquivalent;				// may be null
	
	protected String[] synonynms;						// may be null or empty
	
	/**
	 * <p>Create a coded concept.</p>
	 *
	 * @param	conceptUniqueIdentifier				the unique identifier of the concept, usually a UMLS CUI; required to be unique within the scope of comparisons using {@link #equals(Object) equals(Object)}
	 * @param	conceptIdentifier					the scheme-specific concept identifier, e.g., for SNOMED-CT, the SNOMED Concept Identifier
	 * @param	codingSchemeDesignator				the DICOM PS3.16 Section 8 coding scheme used as the DICOM Coding Scheme Designator, e.g., "SCT", "DCM", "LN", or a private coding scheme
	 * @param	legacyCodingSchemeDesignator		a legacy (alternative) coding scheme, e.g.  "SRT" if what is used in DICOM context groups instead of "SCT"; null if none required (i.e., treat the same as codingSchemeDesignator)
	 * @param	otherLegacyCodingSchemeDesignators	legacy (alternative) coding schemes, e.g.  "SNM3" if what is used in DICOM context groups instead of "SRT"; null if none required (i.e., treat the same as codingSchemeDesignator)
	 * @param	codingSchemeVersion					the version of the coding scheme in which this code is defined, if necessary; null if none required
	 * @param	codeValue							the code used as the DICOM Code Value (e.g., the SNOMED-CT style code)
	 * @param	legacyCodeValue						the code used as the DICOM Code Value for the legacyCodingSchemeDesignator (e.g., the SNOMED-RT style code rather than the SNOMED-CT style Concept Identifier)
	 * @param	codeMeaning							the text used as the DICOM Code Meaning
	 * @param	codeStringEquivalent				the text value used for a DICOM Code String VR equivalent attribute (e.g., for Body Part Examined instead of in Anatomic Region Sequence); may be null
	 * @param	synonynms							alternative code meanings, including abbreviations or different languages; may be null or empty
	 */
	public CodedConcept(String conceptUniqueIdentifier,String conceptIdentifier,String codingSchemeDesignator,String legacyCodingSchemeDesignator,String[] otherLegacyCodingSchemeDesignators,String codingSchemeVersion,String codeValue,String legacyCodeValue,String codeMeaning,String codeStringEquivalent,String[] synonynms) {
		super(conceptUniqueIdentifier);
		this.conceptIdentifier=conceptIdentifier;
		this.codingSchemeDesignator=codingSchemeDesignator;
		this.legacyCodingSchemeDesignator=legacyCodingSchemeDesignator;
		this.otherLegacyCodingSchemeDesignators=otherLegacyCodingSchemeDesignators;
		this.codingSchemeVersion=codingSchemeVersion;
		this.codeValue=codeValue;
		this.legacyCodeValue=legacyCodeValue;
		this.codeMeaning=codeMeaning;
		this.codeStringEquivalent=codeStringEquivalent;
		this.synonynms=synonynms;
	}
	
	protected CodedConcept() {};
	
	public String getConceptIdentifier() { return conceptIdentifier; }
	
	public String getCodingSchemeDesignator() { return codingSchemeDesignator; }
	
	public String getLegacyCodingSchemeDesignator() { return legacyCodingSchemeDesignator; }
	
	public String[] getOtherLegacyCodingSchemeDesignators() { return otherLegacyCodingSchemeDesignators; }
	
	public String getCodingSchemeVersion() { return codingSchemeVersion; }
	
	public String getCodeValue() { return codeValue; }
	
	public String getLegacyCodeValue() { return legacyCodeValue; }
	
	public String getCodeMeaning() { return codeMeaning; }
	
	public String getCodeStringEquivalent() { return codeStringEquivalent; }
	
	public String[] getSynonyms() { return synonynms; }
	
	public CodedSequenceItem getCodedSequenceItem() throws DicomException {
		CodedSequenceItem item;
		if (codingSchemeVersion != null && codingSchemeVersion.length() > 0) {
			item = new CodedSequenceItem(codeValue,codingSchemeDesignator,codingSchemeVersion,codeMeaning);
		}
		else {
			item = new CodedSequenceItem(codeValue,codingSchemeDesignator,codeMeaning);
		}
		return item;
	}
	
	public String getCodeAsString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(codeValue);
		buf.append(",");
		buf.append(codingSchemeDesignator);
		if (codingSchemeVersion != null) {
			buf.append(" [");
			buf.append(codingSchemeVersion);
			buf.append("]");
		}
		buf.append(",\"");
		buf.append(codeMeaning);
		buf.append("\")");
		return buf.toString();
	}
	
	public String getLegacyCodeAsString() {
		StringBuffer buf = new StringBuffer();
		if (legacyCodeValue != null) {
			buf.append("(");
			buf.append(legacyCodeValue);
			buf.append(",");
			buf.append(legacyCodingSchemeDesignator);
			if (otherLegacyCodingSchemeDesignators != null && otherLegacyCodingSchemeDesignators.length > 0) {
				buf.append(" {");
				String prefix = "";
				for (String s : otherLegacyCodingSchemeDesignators) {
					buf.append(prefix);
					buf.append(s);
					prefix=",";
				}
				buf.append("}");
			}
			if (codingSchemeVersion != null) {
				buf.append(" [");
				buf.append(codingSchemeVersion);
				buf.append("]");
			}
			buf.append(",\"");
			buf.append(codeMeaning);
			buf.append("\")");
		}
		return buf.toString();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(super.toString());
		buf.append("\tconcept identifier: ");
		buf.append(conceptIdentifier);
		buf.append("\n");
		buf.append("\tcode: ");
		buf.append(getCodeAsString());
		buf.append("\n");
		if (legacyCodeValue != null) {
			buf.append("\tlegacy code: ");
			buf.append(getLegacyCodeAsString());
			buf.append("\n");
		}
		buf.append("\tcodeStringEquivalent: ");
		buf.append(codeStringEquivalent);
		buf.append("\n");
		buf.append("\tsynonynms: ");
		buf.append(StringUtilities.toString(synonynms));
		buf.append("\n");
		return buf.toString();
	}
	
	public String toStringBrief() {
		StringBuffer buf = new StringBuffer();
		buf.append("CUI: ");
		buf.append(conceptUniqueIdentifier);
		buf.append(" ");
		buf.append(conceptIdentifier);
		buf.append(" ");
		buf.append(getCodeAsString());
		return buf.toString();
	}
}

