/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.apps;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.TagFromName;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>A class of static methods to read a DICOM color image and extract the ICC Profile as a raw binary file.</p>
 *
 * @author	dclunie
 */
public class ExtractICCProfile {

	/**
	 * <p>Read a DICOM color image and extract the ICC Profile as a raw binary file.</p>
	 *
	 * @param	inputFileName	the input file name
	 * @param	outputFileName	the output file name
	 */
	public ExtractICCProfile(String inputFileName,String outputFileName) throws DicomException, FileNotFoundException, IOException {
		AttributeList list = new AttributeList();
		DicomInputStream in = new DicomInputStream(new BufferedInputStream(new FileInputStream(inputFileName)));
		list.read(in,TagFromName.PixelData/*stopAtTag*/);
		in.close();
		
		Attribute aICCProfile = null;
		Attribute aOpticalPathSequence = list.get(TagFromName.OpticalPathSequence);
		if (aOpticalPathSequence == null) {
System.err.println("No OpticalPathSequence so looking for ICCProfile in top level data set");
			aICCProfile = list.get(TagFromName.ICCProfile);
		}
		else {
			// assume single item and/or multiple items all with same ICC Profile
System.err.println("OpticalPathSequence so looking for ICCProfile in first item");
			aICCProfile = SequenceAttribute.getNamedAttributeFromWithinSequenceWithSingleItem((SequenceAttribute)aOpticalPathSequence,TagFromName.ICCProfile);
		}
		if (aICCProfile != null) {
			byte[] iccProfile = aICCProfile.getByteValues();
			FileOutputStream out = new FileOutputStream(outputFileName);
			out.write(iccProfile);
			out.close();
		}
		else {
			throw new DicomException("No ICC Profile Attribute found");
		}
	}
	
	/**
	 * <p>Read a DICOM color image and extract the ICC Profile as a raw binary file.</p>
	 *
	 * @param	arg	two parameters, the inputFile, outputFile
	 */
	public static void main(String arg[]) {
		try {
			if (arg.length == 2) {
				new ExtractICCProfile(arg[0],arg[1]);
			}
			else {
				System.err.println("Error: Incorrect number of arguments");
				System.err.println("Usage: ExtractICCProfile inputFile outputFile");
				System.exit(1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
