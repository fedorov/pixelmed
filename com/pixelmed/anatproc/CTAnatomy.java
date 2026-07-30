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
public class CTAnatomy extends Anatomy {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/anatproc/CTAnatomy.java,v 1.30 2026/05/19 14:38:44 dclunie Exp $";

	private static final Logger slf4jlogger = LoggerFactory.getLogger(CTAnatomy.class);
			
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


	
