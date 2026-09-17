/* Copyright (c) 2001-2022, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.apps;

import com.pixelmed.dicom.*;

import java.io.*;

/**
 * <p>A class of static methods to read a DICOM image and extract the compressed bit stream of a selected frame.</p>
 *
 * @author	dclunie
 */
public class ExtractCompressedFrameData {

	/**
	 * <p>Read a DICOM image and extract the compressed bit stream of a selected frame.</p>
	 *
	 * @param	inputFileName	the input file name
	 * @param	outputFileName	the output file name
	 * @param	frameNumber		from zero
	 */
	public ExtractCompressedFrameData(String inputFileName,String outputFileName,int frameNumber) throws DicomException, FileNotFoundException, IOException {
		AttributeList list = new AttributeList();
		list.setDecompressPixelData(false);
		//DicomInputStream in = new DicomInputStream(new BufferedInputStream(new FileInputStream(inputFileName)));
		DicomInputStream in = new DicomInputStream(new File(inputFileName));
		list.read(in);
		in.close();
		
		byte[] bytes = null;
		Attribute a = list.get(TagFromName.PixelData);
		if (a == null) {
			throw new DicomException("Missing PixelData element");
		}
		else if (a instanceof OtherByteAttributeCompressedSeparateFramesOnDisk) {
			bytes = ((OtherByteAttributeCompressedSeparateFramesOnDisk)a).getByteValuesForSelectedFrame(frameNumber);
		}
		else if (a instanceof OtherByteAttributeMultipleCompressedFrames) {
			bytes = ((OtherByteAttributeMultipleCompressedFrames)a).getByteValuesForSelectedFrame(frameNumber);
		}
		else {
			throw new DicomException("Unsupported PixelData class "+a.getClass().getName());
		}
		
		if (bytes != null && bytes.length > 0) {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileName));
			out.write(bytes);
			out.close();
		}
		else {
			throw new DicomException("Could not extract frame bytes");
		}
	}
	
	/**
	 * <p>Read a DICOM image and extract the compressed bit stream of a selected frame.</p>
	 *
	 * @param	arg	three parameters, the inputFile, outputFile, the selected frame (numbered from  0)
	 */
	public static void main(String arg[]) {
		try {
			if (arg.length == 3) {
				new ExtractCompressedFrameData(arg[0],arg[1],Integer.parseInt(arg[2]));
			}
			else {
				System.err.println("Error: Incorrect number of arguments");
				System.err.println("Usage: ExtractCompressedFrameData inputFile outputFile frameNumber");
				System.exit(1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
