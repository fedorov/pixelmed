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

//import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.security.cert.X509Certificate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.charset.Charset;

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
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicomweb/useragent/StudyServiceSearchTransactionUserAgent.java,v 1.12 2026/09/04 22:33:44 dclunie Exp $";

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

	// (001482)
	// same as in StudyServiceRetrieveTransactionUserAgent - should re-factor :(
	// based on "https://stackoverflow.com/questions/33084855/way-to-ignore-ssl-certificate-using-httpsurlconnection"
	private class HttpsTrustManager implements X509TrustManager {
		private final X509Certificate[] acceptedIssuers = new X509Certificate[]{};

		@Override
		public void checkClientTrusted(
				X509Certificate[] x509Certificates, String s)
				throws java.security.cert.CertificateException {
		}

		@Override
		public void checkServerTrusted(
				X509Certificate[] x509Certificates, String s)
				throws java.security.cert.CertificateException {
			slf4jlogger.trace("HttpsTrustManager.checkServerTrusted():");
		}

		public boolean isClientTrusted(X509Certificate[] chain) {
			return true;
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			slf4jlogger.trace("HttpsTrustManager.isServerTrusted():");
			return true;
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			slf4jlogger.trace("HttpsTrustManager.getAcceptedIssuers():");
			return acceptedIssuers;
		}
	}

	/**
	 * @param	endpointuri			DICOMweb URI
	 * @param	requestIdentifier	the list of matching and return keys
	 * @param	identifierHandler	the handler to use for each returned identifier
	 * @throws	IOException
	 * @throws	DicomException
	 * @throws	DicomNetworkException
	 */
	public StudyServiceSearchTransactionUserAgent(String endpointuri,AttributeList requestIdentifier,IdentifierHandler identifierHandler) throws DicomNetworkException, DicomException, IOException {
		this(endpointuri,null/*bearerToken*/,requestIdentifier,identifierHandler);
	}
	
	/**
	 * @param	endpointuri			DICOMweb URI
	 * @param	bearerToken			DICOMweb bearer token
	 * @param	requestIdentifier	the list of matching and return keys
	 * @param	identifierHandler	the handler to use for each returned identifier
	 * @throws	IOException
	 * @throws	DicomException
	 * @throws	DicomNetworkException
	 */
	public StudyServiceSearchTransactionUserAgent(String endpointuri,String bearerToken,AttributeList requestIdentifier,IdentifierHandler identifierHandler) throws DicomNetworkException, DicomException, IOException {
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
							includefieldbuf.append(t.toStringUndelimited().toUpperCase());	// (001467) some servers are case sensitive for hex data element tags
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
			// do NOT add fuzzymatching parameter yet ... only later if returns 204 and need to retry
		}
		
		boolean finishedTrying = false;
		while (!finishedTrying) {
			String queryuri = buf.toString();
			slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): queryuri=\"{}\"",queryuri);
			
			// (001482)
			// this is very bad security practice - should not be default and should only be done if need to override (control by command line option) :(
			{
				slf4jlogger.trace("StudyServiceSearchTransactionUserAgent(): may be HTTPS so overriding trust manager in case expired certificate ");
				TrustManager[] trustManagers = new TrustManager[] { new HttpsTrustManager() };
				SSLContext context = null;
				try {
					context = SSLContext.getInstance("TLS");
					context.init(null, trustManagers, new SecureRandom());
				} catch (NoSuchAlgorithmException | KeyManagementException e) {
					e.printStackTrace();
				}
				if (context != null) {
					HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
				}
			}
			
			URL url = new URL(queryuri);
			slf4jlogger.trace("StudyServiceSearchTransactionUserAgent(): opening HttpURLConnection");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			connection.setRequestMethod("GET");
			
			// (001483)
			if (bearerToken != null && bearerToken.length() > 0) {
				connection.setRequestProperty("Authorization","BEARER "+bearerToken);
			}
			
			int status = connection.getResponseCode();
			slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): status={}",status);
			
			if (status == 200) {	// (001449)
				finishedTrying = true;

				String contentType = connection.getContentType();		// (001486)
				slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): contentType={}",contentType);
				if (contentType == null || contentType.length() == 0) {
					throw new DicomException("Unexpected missing or null ContentType in search response: \""+contentType+"\"");
				}
				// e.g., Content-Type: application/dicom+json; charset=utf-8
				String contentTypeWithoutParameters = contentType.toLowerCase().replaceFirst(";.*$","").trim();
				slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): contentTypeWithoutParameters={}",contentTypeWithoutParameters);

				if (!contentTypeWithoutParameters.equals("application/dicom+json")) {		// (001486)
					throw new DicomException("Unexpected ContentType in search response (only \"application/dicom+json\" supported): \""+contentType+"\"");
				}
				
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
					JsonReader jsonReader = Json.createReader(new InputStreamReader(connection.getInputStream(),Charset.forName("UTF-8")));	// (001489)
					JsonStructure document = jsonReader.read();
					jsonReader.close();
					if (document instanceof JsonArray) {
						if (!((JsonArray)document).isEmpty()) {	// (001487)
							responseIdentifiers = new JSONRepresentationOfDicomObjectFactory().getArrayOfAttributeLists((JsonArray)document);
							if (responseIdentifiers != null) {	// (001487)
								for (AttributeList responseIdentifier : responseIdentifiers) {
									slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): responseIdentifier=\n{}",responseIdentifier);
									identifierHandler.doSomethingWithIdentifier(responseIdentifier);
								}
							}
							else {
								slf4jlogger.warn("StudyServiceSearchTransactionUserAgent(): cannot extract responseIdentifiers from JsonArray - ignoring response");
							}
						}
						else {
							slf4jlogger.info("StudyServiceSearchTransactionUserAgent(): empty response");
						}
					}
					else {
						throw new DicomException("Could not parse JSON document - expected array at top level");
					}
				}
			}
			else if (status == 204) {	// (001449) (001466)
				// 204 is a valid response - nothing there or nothing matches
				slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): 204 No Content");
				// only try with fuzzymatching parameter if fails the first time, since otherwise kills Proscia (001466)
				if (needfuzzymatchparameter) {
					slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): 204 No Content - so retry with fuzzymatching");
					buf.append("&fuzzymatching=true");	// Google DICOMweb bug that doesn't perform any wildcard matching unless fuzzymatching is specified :( (001450)
					needfuzzymatchparameter = false;	// so that it doesn't keep trying infinitely :)
				}
				else {
					slf4jlogger.debug("StudyServiceSearchTransactionUserAgent(): 204 No Content - so no identifiers - do nothing but do not fail");
					finishedTrying = true;
				}
			}
			else {
				finishedTrying = true;
				// (001449)
				throw new DicomException("Response status "+status+" other than successful 200 OK");
			}

			connection.disconnect();
		}
	}
}
