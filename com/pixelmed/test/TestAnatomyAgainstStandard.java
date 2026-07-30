/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.test;

import com.pixelmed.anatproc.*;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.TagFromName;

import junit.framework.*;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestAnatomyAgainstStandard extends TestCase {
	
	// constructor to support adding tests to suite ...
	
	public TestAnatomyAgainstStandard(String name) {
		super(name);
	}
	
	// add tests to suite manually, rather than depending on default of all test...() methods
	// in order to allow adding TestAnatomyAgainstStandard.suite() in AllTests.suite()
	// see Johannes Link. Unit Testing in Java pp36-47
	
	public static Test suite() {
		TestSuite suite = new TestSuite("TestAnatomyAgainstStandard");
		
		suite.addTest(new TestAnatomyAgainstStandard("TestAnatomyAgainstStandard_AnnexL"));
		
		return suite;
	}
	
	protected void setUp() {
	}
	
	protected void tearDown() {
	}
	
	protected String[] conflictAvoidBPE = { "LUMBAR" };		// not "IAC" - doesn't help

	private String xmlAnnexLResourceName = "/com/pixelmed/anatproc/anatomyfromannexl.xml";

	private Document readAnnexLFile() throws IOException, ParserConfigurationException, SAXException {
		InputStream i = this.getClass().getResourceAsStream(xmlAnnexLResourceName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(i);
	}
	
	public void TestAnatomyAgainstStandard_AnnexL() throws Exception {
		Document document = readAnnexLFile();
		Element root = document.getDocumentElement();
		if (root.getTagName().equals("AnnexLRows")) {
			NodeList deNodes = root.getChildNodes();
			for (int i=0; i<deNodes.getLength(); ++i) {
				Node deNode = deNodes.item(i);
				if (deNode.getNodeType() == Node.ELEMENT_NODE && ((Element)deNode).getTagName().equals("Row")) {
					String csd = "";
					String cv = "";
					String cm = "";
					String bpe = "";
					String srtcv = "";
					String fmacv = "";
					String umlscuid = "";
					{
						NamedNodeMap attributes = deNode.getAttributes();
						if (attributes != null) {
							{
								Node attribute = attributes.getNamedItem("csd");
								if (attribute != null) {
									csd = attribute.getTextContent();
								}
							}
							{
								Node attribute = attributes.getNamedItem("cv");
								if (attribute != null) {
									cv = attribute.getTextContent();
								}
							}
							{
								Node attribute = attributes.getNamedItem("cm");
								if (attribute != null) {
									cm = attribute.getTextContent();
								}
							}
							{
								Node attribute = attributes.getNamedItem("bpe");
								if (attribute != null) {
									bpe = attribute.getTextContent();
								}
							}
							{
								Node attribute = attributes.getNamedItem("srtcv");
								if (attribute != null) {
									srtcv = attribute.getTextContent();
								}
							}
							{
								Node attribute = attributes.getNamedItem("fmacv");
								if (attribute != null) {
									fmacv = attribute.getTextContent();
								}
							}
							{
								Node attribute = attributes.getNamedItem("umlscuid");
								if (attribute != null) {
									umlscuid = attribute.getTextContent();
								}
							}
						}
					}
System.err.println("csd = "+csd);
System.err.println("cv = "+cv);
System.err.println("cm = "+cm);
System.err.println("bpe = "+bpe);
System.err.println("srtcv = "+srtcv);
System.err.println("fmacv = "+fmacv);
System.err.println("umlscuid = "+umlscuid);

					if (bpe.length() > 0 && ! Arrays.asList(conflictAvoidBPE).contains(bpe)) {
System.err.println("Checking bpe = "+bpe);
						AttributeList list = new AttributeList();
						{ Attribute a = new CodeStringAttribute(TagFromName.BodyPartExamined); a.addValue(bpe); list.put(a); }
						Concept found = CTAnatomy.findAnatomicConcept(list);
						if (found != null) {
System.err.println("For bpe = "+bpe+" found in CTAnatomy ");
						}
						else {
							found = ProjectionXRayAnatomy.findAnatomicConcept(list);
							if (found != null) {
System.err.println("For bpe = "+bpe+" found in ProjectionXRayAnatomy ");
							}
						}
						
						if (found != null) {
System.err.println("For bpe = "+bpe+" found:\n"+found);
							assertEquals(csd,((CodedConcept)found).getCodingSchemeDesignator());
							assertEquals(cv,((CodedConcept)found).getCodeValue());
						}
						else {
							//fail("No concept match for BodyPartExamined "+bpe);
						}
					}
				}
			}
		}
	}
	
}


