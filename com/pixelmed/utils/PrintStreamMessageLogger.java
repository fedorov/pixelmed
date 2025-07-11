/* Copyright (c) 2001-2025, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.utils;

import java.io.PrintStream;

/**
 * <p>A class to write log and status messages to a <code>PrintStream</code> such as <code>System.err</code>.</p>
 *
 * @author      dclunie
 */
public class PrintStreamMessageLogger implements MessageLogger {
	
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/utils/PrintStreamMessageLogger.java,v 1.12 2025/01/29 10:58:09 dclunie Exp $";

	protected PrintStream printStream;

	public PrintStreamMessageLogger(PrintStream printStream) {
		this.printStream = printStream;
	}
	
	public void send(String message) {
		printStream.print(message);
	}
		
	public void sendLn(String message) {
		printStream.println(message);
	}
}

