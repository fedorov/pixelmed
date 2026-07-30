/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.dicomweb.useragent;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.JSONRepresentationOfDicomObjectFactory;
import com.pixelmed.dicom.TagFromName;

import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.IdentifierHandler;

import com.pixelmed.query.QueryInformationModel;

import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonStructure;
				
/**
 * <p>.</p>
 *
 * @author	dclunie
 */
public class StudyServiceSearchTransactionUserAgent {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicomweb/useragent/StudyServiceSearchTransactionUserAgent.java,v 1.3 2026/03/08 15:20:36 dclunie Exp $";

	private static final Logger slf4jlogger = LoggerFactory.getLogger(StudyServiceSearchTransactionUserAgent.class);

	// not using QueryInformationModel.getInformationEntityForQueryLevelName() since non-static in that class
	/**
	 * @param	queryLevelName
	 */
	private static InformationEntity getInformationEntityForQueryLevelName(String queryLevelName) {
		// no PATIENT level in DICOMweb
		if 		("STUDY"   == queryLevelName)	return InformationEntity.STUDY;
		else if ("SERIES"  == queryLevelName)	return InformationEntity.SERIES;
		else if ("IMAGE"   == queryLevelName)	return InformationEntity.INSTANCE;
		else return null;
	}

	/**
	 * @param	endpointuri			DICOMweb URI
	 * @param	requestIdentifier			the list of matching and return keys
	 * @param	identifierHandler	the handler to use for each returned identifier
	 * @throws	IOException
	 * @throws	DicomException
	 * @throws	DicomNetworkException
	 */
	public StudyServiceSearchTransactionUserAgent(String endpointuri,AttributeList requestIdentifier,IdentifierHandler identifierHandler) throws DicomNetworkException, DicomException, IOException {
		// need to build path to query-level specific resource with unique keys, then convert requestIdentifier to query parameters to append to end point URI
		// see "http://dicom.nema.org/medical/dicom/current/output/chtml/part18/sect_10.6.html"
		
		HashSet<AttributeTag> used = new HashSet<AttributeTag>();
		
		// do not ever want to include these as a DICOMweb Search Transaction query parameters
		used.add(TagFromName.SpecificCharacterSet);
		used.add(TagFromName.QueryRetrieveLevel);
		
		StringBuffer buf = new StringBuffer();
		buf.append(endpointuri);
		
		InformationEntity queryLevel = getInformationEntityForQueryLevelName(Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.QueryRetrieveLevel,""));
		if (queryLevel == null) {
			throw new DicomException("Cannot determine Query Level from query request identifier to determine resource to use");
		}
		slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): queryLevel={}",queryLevel);
		if (queryLevel == InformationEntity.STUDY) {
			buf.append("/studies");
		}
		else if (queryLevel == InformationEntity.SERIES) {
			buf.append("/studies");
			{
				String studyInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.StudyInstanceUID,"");
				if (studyInstanceUID.length() == 0) {
					throw new DicomException("Missing StudyInstanceUID unique key needed for Series level query");
				}
				buf.append("/");
				buf.append(studyInstanceUID);
				used.add(TagFromName.StudyInstanceUID);
			}
			buf.append("/series");
		}
		else if (queryLevel == InformationEntity.INSTANCE) {
			buf.append("/studies");
			{
				String studyInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.StudyInstanceUID,"");
				if (studyInstanceUID.length() == 0) {
					throw new DicomException("Missing StudyInstanceUID unique key needed for Series level query");
				}
				buf.append("/");
				buf.append(studyInstanceUID);
				used.add(TagFromName.StudyInstanceUID);
			}
			buf.append("/series");
			{
				String seriesInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.SeriesInstanceUID,"");
				if (seriesInstanceUID.length() == 0) {
					throw new DicomException("Missing SeriesInstanceUID unique key needed for Instance level query");
				}
				buf.append("/");
				buf.append(seriesInstanceUID);
				used.add(TagFromName.SeriesInstanceUID);
			}
			buf.append("/instances");
		}
		
		// add all other Attributes in the Identifier as Query Parameters, if any, either with a value, or if empty as include fields
		// "https://dicom.nema.org/medical/dicom/current/output/chtml/part18/sect_8.3.4.html"
		boolean needfuzzymatchparameter = false;
		StringBuffer matchfieldbuf = new StringBuffer();
		{
			Iterator it = requestIdentifier.values().iterator();
			while (it.hasNext()) {
				Attribute a = (Attribute)it.next();
				if (a != null && a.getVL() > 0) {
					AttributeTag t = a.getTag();
					if (t != null && !used.contains(t)) {
						String v = a.getDelimitedStringValuesOrDefault("");
						if (v.length() > 0) {
							slf4jlogger.trace("StudyServiceSearchTransactionUserAgent(): adding match query parameter for {}",a);
							if (matchfieldbuf.length() > 0) {
								matchfieldbuf.append("&");
							}
							matchfieldbuf.append(t.toStringUndelimited());	// always used hex tag rather than keyword to specify data element
							matchfieldbuf.append("=");
							String percentEncodedValue = java.net.URLEncoder.encode(v,"utf-8");
							matchfieldbuf.append(percentEncodedValue);
							used.add(t);
							
							if (t == TagFromName.PatientName) {	// (001450)
								if (v.contains("*") || v.contains("?")) {
									slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): PatientName matching value contains wildcard {}",v);
									needfuzzymatchparameter = true;	// Google DICOMweb bug that doesn't perform any wildcard matching unless fuzzymatching is specified :(
								}
							}
						}
					}
				}
			}
		}
		StringBuffer includefieldbuf = new StringBuffer();
		{
			Iterator it = requestIdentifier.values().iterator();
			while (it.hasNext()) {
				Attribute a = (Attribute)it.next();
				if (a != null && a.getVL() == 0) {
					AttributeTag t = a.getTag();
					if (t != null && !used.contains(t)) {
						slf4jlogger.trace("StudyServiceSearchTransactionUserAgent(): adding includefield query parameter for {}",a);
						{
							if (includefieldbuf.length() > 0) {
								//includefieldbuf.append("&");			// separate single tag includefield
								includefieldbuf.append(",");			// single includefield with comma separated list of tags
							}
							//includefieldbuf.append("includefield=");	// separate single tag includefield
							includefieldbuf.append(t.toStringUndelimited());
							used.add(t);
						}
					}
				}
			}
		}
		if (matchfieldbuf.length() > 0 || includefieldbuf.length() > 0) {
			buf.append("?");
			buf.append(matchfieldbuf);
			if (matchfieldbuf.length() > 0 && includefieldbuf.length() > 0) {
				buf.append("&");
			}
			if (includefieldbuf.length() > 0) {
				buf.append("includefield=");							// single includefield with comma separated list of tags
				buf.append(includefieldbuf);
			}
			if (needfuzzymatchparameter) {
				buf.append("&fuzzymatching=true");	// Google DICOMweb bug that doesn't perform any wildcard matching unless fuzzymatching is specified :( (001450)
			}
		}
		
		String queryuri = buf.toString();
		slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): queryuri=\"{}\"",queryuri);
		
		URL url = new URL(queryuri);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		
		int status = connection.getResponseCode();
		slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): status={}",status);
		
		if (status == 200) {	// (001449)
			// response is an array of JSON objects, each of which is an "identifier" encoded as a JSON object containing name-value pairs
			
			AttributeList[] responseIdentifiers = null;

			//String content = null;
			//{
			//	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			//	String ln;
			//	StringBuffer contentbuf = new StringBuffer();
			//	while ((ln = in.readLine()) != null) {
			//		contentbuf.append(ln);
			//	}
			//	in.close();
			//	content = contentbuf.toString();
			//}
			//slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): content=\n{}",content);

			{
				JsonReader jsonReader = Json.createReader(connection.getInputStream());
				JsonStructure document = jsonReader.read();
				jsonReader.close();
				if (document instanceof JsonArray) {
					responseIdentifiers = new JSONRepresentationOfDicomObjectFactory().getArrayOfAttributeLists((JsonArray)document);
					for (AttributeList responseIdentifier : responseIdentifiers) {
						slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): responseIdentifier=\n{}",responseIdentifier);
						identifierHandler.doSomethingWithIdentifier(responseIdentifier);
					}
				}
				else {
					throw new DicomException("Could not parse JSON document - expected array at top level");
				}
			}
		}
		else if (status == 204) {	// (001449)
			// 204 is a valid response - nothing there or nothing matches
			slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): 204 No Content - so no identifiers - do nothing but do not fail");
		}
		else {
			// (001449)
			throw new DicomException("Response status "+status+" other than successful 200 OK");
		}

		connection.disconnect();
	}
}
