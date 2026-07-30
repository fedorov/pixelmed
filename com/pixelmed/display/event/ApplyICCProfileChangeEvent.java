/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.display.event;

import com.pixelmed.event.Event;
import com.pixelmed.event.EventContext;

/**
 * @author	dclunie
 */
public class ApplyICCProfileChangeEvent extends Event {		// (001443)

	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/display/event/ApplyICCProfileChangeEvent.java,v 1.2 2026/03/08 15:12:03 dclunie Exp $";

	private String applyICCProfile;

	/***/
	public static final String iccProfileApplied = "APPLIED";
	/***/
	public static final String iccProfileNotApplied = "NOT_APPLIED";

	/**
	 * @param	eventContext
	 * @param	applyICCProfile
	 */
	public ApplyICCProfileChangeEvent(EventContext eventContext,String applyICCProfile) {
		super(eventContext);
		this.applyICCProfile=applyICCProfile;
//System.err.println("ApplyICCProfileChangeEvent() "+applyICCProfile);
	}

	/***/
	public String getICCProfileApplied() { return applyICCProfile; }

	/***/
	public boolean isICCProfileApplied() { return applyICCProfile.equals(iccProfileApplied); }

	/***/
	public boolean isICCProfileNotApplied() { return applyICCProfile.equals(iccProfileNotApplied); }
}

