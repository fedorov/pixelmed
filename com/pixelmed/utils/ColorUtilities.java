/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.utils;

/**
 * <p>Various static methods helpful for color conversions.</p>
 *
 * <p>Incorporation of linear Bradford adaption of D50/D65 for sRGB to CIELAB PCS, transform matrix precision increases to align with CSS 4 color.js, and integer return value clamping suggested by Mike Halle assisted by Claude.</p>
 *
 * @author	dclunie
 */
public class ColorUtilities {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/utils/ColorUtilities.java,v 1.18 2026/09/15 23:55:58 dclunie Exp $";

	private ColorUtilities() {}
		
	/**
	 * <p>Convert floating point CIELab values to the 16 bit fractional integer scaled representation used in ICC profiles and DICOM.</p>
	 *
	 * <p>See ICC v4.3 Tables 12 and 13, and DICOM PS 3.3 C.10.7.1.1.</p>
	 *
	 * @param	cieLab	array of length 3 containing L*,a*,b* values with L* from 0.0 to 100.0, and a* and b* from -128.0 to +127.0
	 * return			array of length 3 containing 16 bit scaled L*,a*,b* values from  0 to 65535
	 */
	public static int[] getIntegerScaledCIELabFromCIELab(float[] cieLab) {
		int[] cieLabScaled = new int[3];
		// per PS 3.3 C.10.7.1.1 ... scale same way as ICC profile encodes them

		float l = (int)Math.round (cieLab[0] * 65535 / 100);
		float a = (int)Math.round((cieLab[1] + 128) * 65535 / 255);
		float b = (int)Math.round((cieLab[2] + 128) * 65535 / 255);

		cieLabScaled[0] = l < 0 ? 0 : (l > 65535 ? 65535 : (int)l);
		cieLabScaled[1] = a < 0 ? 0 : (a > 65535 ? 65535 : (int)a);
		cieLabScaled[2] = b < 0 ? 0 : (b > 65535 ? 65535 : (int)b);
//System.err.println("CIELab ("+cieLab[0]+","+cieLab[1]+","+cieLab[2]+") -> CIELab scaled ("+cieLabScaled[0]+","+cieLabScaled[1]+","+cieLabScaled[2]+")");
		return cieLabScaled;
	}
	
	/**
	 * <p>Convert 16 bit fractional integer scaled CIELab values used in ICC profiles and DICOM to floating point.</p>
	 *
	 * <p>See ICC v4.3 Tables 12 and 13, and DICOM PS 3.3 C.10.7.1.1.</p>
	 *
	 * @param	cieLabScaled	array of length 3 containing 16 bit scaled L*,a*,b* values from  0 to 65535
	 * return					array of length 3 containing L*,a*,b* values with L* from 0.0 to 100.0, and a* and b* from -128.0 to +127.0
	 */
	public static float[] getCIELabPCSFromIntegerScaledCIELabPCS(int[] cieLabScaled) {
		float[] cieLab = new float[3];
		cieLab[0] = (float)(((double)cieLabScaled[0]) / 65535 * 100);
		cieLab[1] = (float)(((double)cieLabScaled[1]) / 65535 * 255 - 128);
		cieLab[2] = (float)(((double)cieLabScaled[2]) / 65535 * 255 - 128);
//System.err.println("CIELab scaled ("+cieLabScaled[0]+","+cieLabScaled[1]+","+cieLabScaled[2]+") -> CIELab ("+cieLab[0]+","+cieLab[1]+","+cieLab[2]+")");
		return cieLab;
	}

	/**
	 * <p>Convert D50 CIEXYZ to CIE 1976 L*, a*, b*.</p>
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Lab_color_space#CIELAB-CIEXYZ_conversions">Wikipedia CIELAB-CIEXYZ_conversions</a>
	 *
	 * @param	cieXYZ	array of length 3 containing X,Y,Z values
	 * return			array of length 3 containing L*,a*,b* values
	 */
	public static float[] getCIELabFromXYZ(float[] cieXYZ) {
		// originally per http://www.easyrgb.com/index.php?X=MATH&H=07#text7

		//double var_X = cieXYZ[0] /  96.4212;	//ref_X =  96.4212  Observer= 2°, Illuminant= D50
		//double var_Y = cieXYZ[1] / 100;		//ref_Y = 100
		//double var_Z = cieXYZ[2] /  82.5188;	//ref_Z =  82.5188

		double var_X = cieXYZ[0] /  (0.3457 / 0.3585 * 100);					//ref_X, Illuminant= D50
		double var_Y = cieXYZ[1] / 100;		    								//ref_Y
		double var_Z = cieXYZ[2] /  ((1.0 - 0.3457 - 0.3585) / 0.3585 * 100);	//ref_Z

		if ( var_X > 0.008856 ) var_X = Math.cbrt(var_X);
		else                    var_X = ( 7.787 * var_X ) + ( 16.0 / 116 );
			
		if ( var_Y > 0.008856 ) var_Y = Math.cbrt(var_Y);
		else                    var_Y = ( 7.787 * var_Y ) + ( 16.0 / 116 );
			
		if ( var_Z > 0.008856 ) var_Z = Math.cbrt(var_Z);
		else                    var_Z = ( 7.787 * var_Z ) + ( 16.0 / 116 );

		float[] cieLab = new float[3];
		cieLab[0] = (float)(( 116 * var_Y ) - 16);			// CIE-L*
		cieLab[1] = (float)(500 * ( var_X - var_Y ));		// CIE-a*
		cieLab[2] = (float)(200 * ( var_Y - var_Z ));		// CIE-b*

//System.err.println("CIEXYZ D50 ("+cieXYZ[0]+","+cieXYZ[1]+","+cieXYZ[2]+") -> CIELab D50 ("+cieLab[0]+","+cieLab[1]+","+cieLab[2]+")");
		return cieLab;
	}
	
	/**
	 * <p>Convert D50 CIE 1976 L*, a*, b* to CIEXYZ.</p>
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Lab_color_space#CIELAB-CIEXYZ_conversions">Wikipedia CIELAB-CIEXYZ_conversions</a>
	 *
	 * @param	cieLab	array of length 3 containing L*,a*,b* values
	 * return			array of length 3 containing X,Y,Z values
	 */
	public static float[] getCIEXYZFromLAB(float[] cieLab) {
		// originally per http://www.easyrgb.com/index.php?X=MATH&H=08#text8

		double var_Y = ( cieLab[0] + 16 ) / 116;
		double var_X = cieLab[1] / 500 + var_Y;
		double var_Z = var_Y - cieLab[2] / 200;
		
//System.err.println("var_Y = "+var_Y);
//System.err.println("var_X = "+var_X);
//System.err.println("var_Z = "+var_Z);

		double var_Y_pow3 = Math.pow(var_Y,3.0);
		double var_X_pow3 = Math.pow(var_X,3.0);
		double var_Z_pow3 = Math.pow(var_Z,3.0);

//System.err.println("var_Y_pow3 = "+var_Y_pow3);
//System.err.println("var_X_pow3 = "+var_X_pow3);
//System.err.println("var_Z_pow3 = "+var_Z_pow3);

		if (var_Y_pow3 > 0.008856) var_Y = var_Y_pow3;
		else                       var_Y = ( var_Y - 16d / 116 ) / 7.787;
		
		if (var_X_pow3 > 0.008856) var_X = var_X_pow3;
		else                       var_X = ( var_X - 16d / 116 ) / 7.787;
		
		if (var_Z_pow3 > 0.008856) var_Z = var_Z_pow3;
		else                       var_Z = ( var_Z - 16d / 116 ) / 7.787;

//System.err.println("var_Y = "+var_Y);
//System.err.println("var_X = "+var_X);
//System.err.println("var_Z = "+var_Z);

		float[] cieXYZ = new float[3];
		//cieXYZ[0] = (float)( 96.4212 * var_X);   //ref_X =  96.4212     Observer= 2°, Illuminant= D50
		//cieXYZ[1] = (float)(100      * var_Y);   //ref_Y = 100
		//cieXYZ[2] = (float)( 82.5188 * var_Z);   //ref_Z =  82.5188

		cieXYZ[0] = (float)( 0.3457 / 0.3585 * 100	 * var_X);   				//ref_X, Illuminant= D50
		cieXYZ[1] = (float)(100      * var_Y);   								//ref_Y
		cieXYZ[2] = (float)( (1.0 - 0.3457 - 0.3585) / 0.3585 * 100 * var_Z);	//ref_Z

//System.err.println("CIELab D50 ("+cieLab[0]+","+cieLab[1]+","+cieLab[2]+") -> CIEXYZ D50 ("+cieXYZ[0]+","+cieXYZ[1]+","+cieXYZ[2]+")");
		return cieXYZ;
	}
		
	/**
	 * <p>Convert RGB values in sRGB to CIEXYZ in ICC PCS.</p>
	 *
	 * <p>SRGB Observer = 2°, Illuminant = D65, XYZ PCS Illuminant = D50.</p>
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/SRGB#The_reverse_transformation">Wikipedia SRGB Reverse Transformation</a>
	 *
	 * @param	rgb		array of length 3 containing R,G,B values each from 0 to 255
	 * return			array of length 3 containing X,Y,Z values
	 */
	public static float[] getCIEXYZPCSFromSRGB(float[] rgb) {
		// originally per http://www.easyrgb.com/index.php?X=MATH&H=02#text2
		
		double var_R = ((double)rgb[0])/255.0;
		double var_G = ((double)rgb[1])/255.0;
		double var_B = ((double)rgb[2])/255.0;
		
		// see also https://entropymine.com/imageworsener/srgbformula/

		if ( var_R > 0.04045 ) var_R = Math.pow((var_R+0.055)/1.055,2.4);
		else                   var_R = var_R / 12.92;
			
		if ( var_G > 0.04045 ) var_G = Math.pow((var_G+0.055)/1.055,2.4);
		else                   var_G = var_G / 12.92;
			
		if ( var_B > 0.04045 ) var_B = Math.pow((var_B+0.055)/1.055,2.4);
		else                   var_B = var_B / 12.92;

		var_R = var_R * 100;
		var_G = var_G * 100;
		var_B = var_B * 100;

		// SRGB Observer = 2°, Illuminant = D65, XYZ Illuminant = D65

		// https://stackoverflow.com/questions/66360637/which-matrix-is-correct-to-map-xyz-to-linear-rgb-for-srgb
		// https://github.com/color-js/color.js/issues/596
		
		//double X = (double)(var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805);
		//double Y = (double)(var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722);
		//double Z = (double)(var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505);

		//double X = (double)(var_R * 0.4124564 + var_G * 0.3575761 + var_B * 0.1804375);
		//double Y = (double)(var_R * 0.2126729 + var_G * 0.7151522 + var_B * 0.0721750);
		//double Z = (double)(var_R * 0.0193339 + var_G * 0.1191920 + var_B * 0.9503041);

		// https://github.com/color-js/color.js/blob/main/src/spaces/srgb-linear.js
		
		//double X = (double)(var_R * 0.41239079926595934 + var_G * 0.357584339383878   + var_B * 0.1804807884018343 );
		//double Y = (double)(var_R * 0.21263900587151027 + var_G * 0.715168678767756   + var_B * 0.07219231536073371);
		//double Z = (double)(var_R * 0.01933081871559182 + var_G * 0.11919477979462598 + var_B * 0.9505321522496607 );

		// https://terathon.com/blog/rgb-xyz-matrix.html
		// https://www.w3.org/TR/css-color-4/#color-conversion-code
		
		double X = (double)(var_R * 506752.0 / 1228815 + var_G *  87881.0 / 245763 + var_B * 12673.0 /   70218);
		double Y = (double)(var_R *  87098.0 /  409605 + var_G * 175762.0 / 245763 + var_B * 12673.0 /  175545);
		double Z = (double)(var_R *   7918.0 /  409605 + var_G *  87881.0 / 737289 + var_B * 1001167.0 / 1053270);

		float[] cieXYZ = new float[3];

		// Bruce Lindbloom's linear Bradford CIE XYZ D65 to D50 for PCS
		// http://www.brucelindbloom.com/index.html?ChromAdapt.html

		//cieXYZ[0] = (float)(X *  1.0478112 + Y * 0.0228866 + Z * -0.0501270);
		//cieXYZ[1] = (float)(X *  0.0295424 + Y * 0.9904844 + Z * -0.0170491);
		//cieXYZ[2] = (float)(X * -0.0092345 + Y * 0.0150436 + Z *  0.7521316);
		
		// https://github.com/color-js/color.js/blob/main/src/adapt.js
		// https://github.com/w3c/csswg-drafts/issues/9607
		// https://www.w3.org/TR/css-color-4/#color-conversion-code
		
		cieXYZ[0] = (float)(X *  1.0479297925449969   + Y * 0.022946870601609652 + Z * -0.05019226628920524);
		cieXYZ[1] = (float)(X *  0.02962780877005599  + Y * 0.9904344267538799   + Z * -0.017073799063418826);
		cieXYZ[2] = (float)(X * -0.009243040646204504 + Y * 0.015055191490298152 + Z *  0.7518742814281371);

//System.err.println("RGB ("+rgb[0]+","+rgb[1]+","+rgb[2]+") -> CIEXYZ D65 ("+X+","+Y+","+Z+") -> CIEXYZ D50 ("+cieXYZ[0]+","+cieXYZ[1]+","+cieXYZ[2]+")");
		return cieXYZ;
	}

	/**
	 * <p>Convert RGB values in sRGB to CIEXYZ in ICC PCS.</p>
	 *
	 * <p>SRGB Observer = 2°, Illuminant = D65, XYZ PCS Illuminant = D50.</p>
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/SRGB#The_reverse_transformation">Wikipedia SRGB Reverse Transformation</a>
	 *
	 * @param	rgb		array of length 3 containing R,G,B values each from 0 to 255
	 * return			array of length 3 containing X,Y,Z values
	 */
	public static float[] getCIEXYZPCSFromSRGB(int[] rgb) {
		float[] frgb = new float[3];
		frgb[0]=(float)rgb[0];
		frgb[1]=(float)rgb[1];
		frgb[2]=(float)rgb[2];
		return getCIEXYZPCSFromSRGB(frgb);
	}
	
	/**
	 * <p>Convert CIEXYZ in ICC PCS to RGB values in sRGB.</p>
	 *
	 * <p>SRGB Observer = 2°, Illuminant = D65, XYZ PCS Illuminant = D50.</p>
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/SRGB#The_forward_transformation_.28CIE_xyY_or_CIE_XYZ_to_sRGB.29">Wikipedia SRGB Forward Transformation</a>
	 *
	 * @param	cieXYZ	array of length 3 containing X,Y,Z values
	 * return			array of length 3 containing R,G,B values each from 0 to 255
	 */
	public static int[] getSRGBFromCIEXYZPCS(float[] cieXYZ) {
		// originally per http://www.easyrgb.com/index.php?X=MATH&H=01#text1

		double d50_X = cieXYZ[0] / 100;
		double d50_Y = cieXYZ[1] / 100;
		double d50_Z = cieXYZ[2] / 100;

		// Bruce Lindbloom's linear Bradford CIE XYZ D50 to D65 for PCS
		// http://www.brucelindbloom.com/index.html?ChromAdapt.html

		//double var_X = d50_X *  0.9555766 + d50_Y * -0.0230393 + d50_Z * 0.0631636;
		//double var_Y = d50_X * -0.0282895 + d50_Y *  1.0099416 + d50_Z * 0.0210077;
		//double var_Z = d50_X *  0.0122982 + d50_Y * -0.0204830 + d50_Z * 1.3299098;
		
		// https://github.com/color-js/color.js/blob/main/src/adapt.js
		// https://github.com/w3c/csswg-drafts/issues/9607

		double var_X = d50_X *  0.955473421488075    + d50_Y * -0.02309845494876471  + d50_Z * 0.06325924320057072;
		double var_Y = d50_X * -0.0283697093338637   + d50_Y *  1.0099953980813041   + d50_Z * 0.021041441191917323;
		double var_Z = d50_X *  0.012314014864481998 + d50_Y * -0.020507649298898964 + d50_Z * 1.330365926242124;

		// https://stackoverflow.com/questions/66360637/which-matrix-is-correct-to-map-xyz-to-linear-rgb-for-srgb
		// https://github.com/color-js/color.js/issues/596

		//double var_R = var_X *  3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
		//double var_G = var_X * -0.9689 + var_Y *  1.8758 + var_Z *  0.0415;
		//double var_B = var_X *  0.0557 + var_Y * -0.2040 + var_Z *  1.0570;

		//double var_R = var_X *  3.2404542 + var_Y * -1.5371385 + var_Z * -0.4985314;
		//double var_G = var_X * -0.9692660 + var_Y *  1.8760108 + var_Z *  0.0415560;
		//double var_B = var_X *  0.0556434 + var_Y * -0.2040259 + var_Z *  1.0572252;

		// https://github.com/color-js/color.js/blob/main/src/spaces/srgb-linear.js
		
		//double var_R = var_X *  3.2409699419045226  + var_Y * -1.537383177570094   + var_Z * -0.4986107602930034;
		//double var_G = var_X * -0.9692436362808796  + var_Y *  1.8759675015077202  + var_Z *  0.04155505740717559;
		//double var_B = var_X *  0.05563007969699366 + var_Y * -0.20397695888897652 + var_Z *  1.0569715142428786;

		// https://terathon.com/blog/rgb-xyz-matrix.html
		// https://www.w3.org/TR/css-color-4/#color-conversion-code
		
		double var_R = var_X *   12831.0 /   3959 + var_Y *     -329.0 /    214 + var_Z *  -1974.0 /   3959;
		double var_G = var_X * -851781.0 / 878810 + var_Y *  1648619.0 / 878810 + var_Z *  36519.0 / 878810;
		double var_B = var_X *     705.0 /  12673 + var_Y *    -2585.0 /  12673 + var_Z *    705.0 /    667;
		
		// see also https://entropymine.com/imageworsener/srgbformula/

		if ( var_R > 0.0031308 ) var_R = 1.055 * Math.pow(var_R,1/2.4) - 0.055;
		else                     var_R = 12.92 * var_R;
		
		if ( var_G > 0.0031308 ) var_G = 1.055 * Math.pow(var_G,1/2.4) - 0.055;
		else                     var_G = 12.92 * var_G;
		
		if ( var_B > 0.0031308 ) var_B = 1.055 * Math.pow(var_B,1/2.4) - 0.055;
		else                     var_B = 12.92 * var_B;

		double r = (int)Math.round(var_R * 255);
		double g = (int)Math.round(var_G * 255);
		double b = (int)Math.round(var_B * 255);

		int[] rgb = new int[3];
		rgb[0] = r < 0 ? 0 : (r > 255 ? 255 : (int)r);
		rgb[1] = g < 0 ? 0 : (g > 255 ? 255 : (int)g);
		rgb[2] = b < 0 ? 0 : (b > 255 ? 255 : (int)b);
//System.err.println("CIEXYZ D50 ("+cieXYZ[0]+","+cieXYZ[1]+","+cieXYZ[2]+") -> CIEXYZ D65 ("+(var_X*100)+","+(var_Y*100)+","+(var_Z*100)+") -> RGB ("+rgb[0]+","+rgb[1]+","+rgb[2]+")");
		return rgb;
	}

	/**
	 * <p>Convert CIEXYZ in ICC PCS to RGB values in sRGB.</p>
	 *
	 * <p>SRGB Observer = 2°, Illuminant = D65, XYZ PCS Illuminant = D50.</p>
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/SRGB#The_forward_transformation_.28CIE_xyY_or_CIE_XYZ_to_sRGB.29">Wikipedia SRGB Forward Transformation</a>
	 *
	 * @param	cieXYZ	array of length 3 containing X,Y,Z values
	 * return			array of length 3 containing R,G,B values each from 0 to 255
	 */
	public static int[] getSRGBFromCIEXYZPCS(int[] cieXYZ) {
		float[] fcieXYZ = new float[3];
		fcieXYZ[0]=(float)cieXYZ[0];
		fcieXYZ[1]=(float)cieXYZ[1];
		fcieXYZ[2]=(float)cieXYZ[2];
		return getSRGBFromCIEXYZPCS(fcieXYZ);
	}

	/**
	 * <p>Convert RGB values in sRGB to CIELab in ICC PCS.</p>
	 *
	 * @param	rgb		array of length 3 containing R,G,B values each from 0 to 255
	 * return			array of length 3 containing L*,a*,b* values
	 */
	public static float[] getCIELabPCSFromSRGB(int[] rgb) {
		return getCIELabFromXYZ(getCIEXYZPCSFromSRGB(rgb));
	}
		
	/**
	 * <p>Convert RGB values in sRGB to CIELab in ICC PCS.</p>
	 *
	 * @param	rgb		array of length 3 containing R,G,B values each from 0 to 255
	 * return			array of length 3 containing L*,a*,b* values
	 */
	public static float[] getCIELabPCSFromSRGB(float[] rgb) {
		return getCIELabFromXYZ(getCIEXYZPCSFromSRGB(rgb));
	}

	/**
	 * <p>Convert RGB values in sRGB to 16 bit fractional integer scaled CIELab in ICC PCS.</p>
	 *
	 * @param	rgb		array of length 3 containing R,G,B values each from 0 to 255
	 * return			array of length 3 containing L*,a*,b* values each from 0 to 65535
	 */
	public static int[] getIntegerScaledCIELabPCSFromSRGB(int[] rgb) {
		return getIntegerScaledCIELabFromCIELab(getCIELabPCSFromSRGB(rgb));
	}

	/**
	 * <p>Convert RGB values in sRGB to 16 bit fractional integer scaled CIELab in ICC PCS.</p>
	 *
	 * @param	rgb		array of length 3 containing R,G,B values each from 0 to 255
	 * return			array of length 3 containing L*,a*,b* values each from 0 to 65535
	 */
	public static int[] getIntegerScaledCIELabPCSFromSRGB(float[] rgb) {
		return getIntegerScaledCIELabFromCIELab(getCIELabPCSFromSRGB(rgb));
	}
	
	/**
	 * <p>Convert CIELab in ICC PCS to RGB values in sRGB.</p>
	 *
	 * @param	cieLab	array of length 3 containing L*,a*,b* values
	 * return			array of length 3 containing R,G,B values each from 0 to 255
	 */
	public static int[] getSRGBFromCIELabPCS(float[] cieLab) {
		return getSRGBFromCIEXYZPCS(getCIEXYZFromLAB(cieLab));
	}

	/**
	 * <p>Convert 16 bit fractional integer scaled CIELab in ICC PCS to RGB values in sRGB.</p>
	 *
	 * @param	cieLabScaled	array of length 3 containing L*,a*,b* values each from 0 to 65535
	 * return					array of length 3 containing R,G,B values each from 0 to 255
	 */
	public static int[] getSRGBFromIntegerScaledCIELabPCS(int[] cieLabScaled) {
		return getSRGBFromCIELabPCS(getCIELabPCSFromIntegerScaledCIELabPCS(cieLabScaled));
	}
	
	/**
	 * <p>Convert color values</p>
	 *
	 * @param	arg	sRGB8toCIELab16 or CIELab16tosRGB8 (case insensitive) and three color values (each decimal or 0xhex)
	 */
	public static void main(String arg[]) {
		boolean bad = true;
		try {
			if (arg.length == 4) {
				int[] input = null;
				float[] finput = null;
				if (arg[1].startsWith("0x") || arg[2].startsWith("0x") || arg[3].startsWith("0x")
				 || (!arg[1].contains(".") && !arg[2].contains(".") && !arg[3].contains("."))
				 ) {
					input = new int[3];
					input[0] = arg[1].startsWith("0x") ? Integer.parseInt(arg[1].replaceFirst("0x",""),16) :  Integer.parseInt(arg[1]);
					input[1] = arg[2].startsWith("0x") ? Integer.parseInt(arg[2].replaceFirst("0x",""),16) :  Integer.parseInt(arg[2]);
					input[2] = arg[3].startsWith("0x") ? Integer.parseInt(arg[3].replaceFirst("0x",""),16) :  Integer.parseInt(arg[3]);
				}
				else {
					finput = new float[3];
					finput[0] = Float.parseFloat(arg[1]);
					finput[1] = Float.parseFloat(arg[2]);
					finput[2] = Float.parseFloat(arg[3]);
				}
				
				int[] output = null;
				float[] foutput = null;
				
				String inputType = null;
				String outputType = null;
				
				if (arg[0].toLowerCase(java.util.Locale.US).equals("sRGB8toCIELab16".toLowerCase(java.util.Locale.US))) {
					if (input != null) {
						output = getIntegerScaledCIELabPCSFromSRGB(input);
					}
					else if (finput != null) {
						output = getIntegerScaledCIELabPCSFromSRGB(finput);
					}
					inputType = "sRGB8";
					outputType = "CIELab16";
					bad = false;
				}
				else if (arg[0].toLowerCase(java.util.Locale.US).equals("sRGB8toCIEXYZ".toLowerCase(java.util.Locale.US))) {
					if (input != null) {
						foutput = getCIEXYZPCSFromSRGB(input);
					}
					else if (finput != null) {
						foutput = getCIEXYZPCSFromSRGB(finput);
					}
					inputType = "sRGB8";
					outputType = "CIEXYZ";
					bad = false;
				}
				else if (arg[0].toLowerCase(java.util.Locale.US).equals("CIEXYZtosRGB8".toLowerCase(java.util.Locale.US))) {
					if (input != null) {
						output = getSRGBFromCIEXYZPCS(input);
					}
					else if (finput != null) {
						output = getSRGBFromCIEXYZPCS(finput);
					}
					inputType = "CIEXYZ";
					outputType = "sRGB8";
					bad = false;
				}
				else if (arg[0].toLowerCase(java.util.Locale.US).equals("CIELab16tosRGB8".toLowerCase(java.util.Locale.US))) {
					if (input != null) {
						output = getSRGBFromIntegerScaledCIELabPCS(input);
					}
					// not float input
					inputType = "CIELab16";
					outputType = "sRGB8";
					bad = false;
				}
				else {
					System.err.println("Unrecognized conversion type "+arg[0]);
				}
				if (output != null || foutput != null) {
					if (input != null) {
						System.err.println(inputType+": "+input[0]+" "+input[1]+" "+input[2]+" (dec) (0x"+Integer.toHexString(input[0])+" 0x"+Integer.toHexString(input[1])+" 0x"+Integer.toHexString(input[2])+")");
					}
					if (finput != null) {
						System.err.println(inputType+": "+finput[0]+" "+finput[1]+" "+finput[2]+" (dec)");
					}
					if (output != null) {
						System.err.println(outputType+": "+output[0]+" "+output[1]+" "+output[2]+" (dec) (0x"+Integer.toHexString(output[0])+" 0x"+Integer.toHexString(output[1])+" 0x"+Integer.toHexString(output[2])+")");
					}
					if (foutput != null) {
						System.err.println(outputType+": "+foutput[0]+" "+foutput[1]+" "+foutput[2]+" (dec)");
					}
				}
			}
			else {
				System.err.println("Error: incorrect number of arguments");
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);	// no need to use SLF4J since command line utility/test
		}
		if (bad) {
			System.err.println("Usage: ColorUtilities sRGB8toCIELab16|sRGB8toCIEXYZ|CIEXYZtosRGB8|CIELab16tosRGB8 R|L G|a B|b");
		}
	}
}


