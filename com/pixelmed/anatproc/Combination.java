/* Copyright (c) 2001-2025, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.anatproc;

import com.pixelmed.dicom.AttributeList;

/**
 * @author	dclunie
 */
class Combination {
	
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/anatproc/Combination.java,v 1.13 2025/01/29 10:58:05 dclunie Exp $";

	Concept parent;
	Concept[] children;
		
	Combination(Concept parent,Concept[] children) {
		this.parent = parent;
		this.children = children;
	}
	
	int size() {
		return children == null ? 0 : children.length;
	}
	
	boolean contains(Concept candidate) {
		boolean match = false;
		for (Concept child : children) {
			if (child.equals(candidate)) {
				match = true;
				break;
			}
		}
		return match;
	}
	
	boolean isSelf(Concept candidate) {
		return parent.equals(candidate);
	}
	
	boolean containsOrIsSelf(Concept candidate) {
		return isSelf(candidate) || contains(candidate);
	}
}
