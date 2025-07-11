/* Copyright (c) 2001-2025, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameFilterByCaseInsensitiveSuffix implements FilenameFilter {
	
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/utils/FilenameFilterByCaseInsensitiveSuffix.java,v 1.12 2025/01/29 10:58:09 dclunie Exp $";

	private String suffix;
	
	public FilenameFilterByCaseInsensitiveSuffix(String suffix) {
		this.suffix = suffix.toUpperCase();
	}
	
	public boolean accept(File dir,String name) {
		return name != null && name.toUpperCase().trim().endsWith(suffix);
	}
}
	
