/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.test;

import com.pixelmed.dicom.*;
///import com.pixelmed.utils.HexDump;

import junit.framework.*;

//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;

public class TestStringAttributeValues extends TestCase {
	
	// constructor to support adding tests to suite ...
	
	public TestStringAttributeValues(String name) {
		super(name);
	}
	
	// add tests to suite manually, rather than depending on default of all test...() methods
	// in order to allow adding TestStringAttributeValues.suite() in AllTests.suite()
	// see Johannes Link. Unit Testing in Java pp36-47
	
	public static Test suite() {
		TestSuite suite = new TestSuite("TestStringAttributeValues");
		
		suite.addTest(new TestStringAttributeValues("TestUniqueIdentifierAttributeValues_Good"));
		suite.addTest(new TestStringAttributeValues("TestUniqueIdentifierAttributeValues_Bad"));

		return suite;
	}
	
	protected void setUp() {
	}
	
	protected void tearDown() {
	}
	
	public void TestUniqueIdentifierAttributeValues_Good() throws Exception {
		{
			String values [] = {
				"1.2.3.4",
				"1.2.3.4.5.6.7.8.9.0.1.2.3.4.5.6.7.8.9.0.1.2.3.4.5.6.7.8.9.0.1.64"
			};
			
			AttributeTag tag = TagFromName.SOPClassUID;
			UniqueIdentifierAttribute a = new UniqueIdentifierAttribute(tag);

			for (String value : values) {
				assertTrue("Checking individual good UID value \""+value+"\"",a.isValueWellFormed(value));
			}
			
			for (String value : values) {
				a.addValue(value);
			}

			assertTrue("Checking entire attribute of good UID values",a.isValid());
		}
	}
	
	public void TestUniqueIdentifierAttributeValues_Bad() throws Exception {
		{
			String values [] = {
				"1..2.3.4",
				"1../../2.3.4",
				"1.2.3.4.5.6.7.8.9.0.1.2.3.4.5.6.7.8.9.0.1.2.3.4.5.6.7.8.9.0.1.645"
			};
			
			AttributeTag tag = TagFromName.SOPClassUID;
			UniqueIdentifierAttribute a = new UniqueIdentifierAttribute(tag);

			for (String value : values) {
				assertFalse("Checking individual bad UID value \""+value+"\"",a.isValueWellFormed(value));
			}
			
			for (String value : values) {
				a.addValue(value);
			}

			assertFalse("Checking entire attribute of bad UID values",a.isValid());
		}
	}

}
