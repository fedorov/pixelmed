/* Copyright (c) 2001-2026, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.dicomweb.useragent;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.dicom.TagFromName;

import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.ReceivedObjectHandler;

import com.pixelmed.query.QueryInformationModel;

import com.pixelmed.utils.CopyStream;
import com.pixelmed.utils.FileUtilities;

import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;

import java.util.Enumeration;

/**
 * <p>.</p>
 *
 * @author	dclunie
 */
public class StudyServiceRetrieveTransactionUserAgent {
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicomweb/useragent/StudyServiceRetrieveTransactionUserAgent.java,v 1.3 2026/03/08 15:20:36 dclunie Exp $";

	private static final Logger slf4jlogger = LoggerFactory.getLogger(StudyServiceRetrieveTransactionUserAgent.class);

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
	
	private void processSingleDicomFile(File temporaryReceivedFile,File savedImagesFolder,StoredFilePathStrategy storedFilePathStrategy,ReceivedObjectHandler receivedObjectHandler,IdentifierHandler identifierHandler) throws DicomException, DicomNetworkException, IOException {
		slf4jlogger.debug("processSingleDicomFile(): temporaryReceivedFile={}",temporaryReceivedFile);
		
		AttributeList list = new AttributeList();
		list.readOnlyMetaInformationHeader(temporaryReceivedFile);
		
		String mediaStorageSOPInstanceUID = Attribute.getSingleStringValueOrEmptyString(list,TagFromName.MediaStorageSOPInstanceUID);
		if (mediaStorageSOPInstanceUID.length() == 0) {
			throw new DicomException("Missing Media Storage SOP Instance UID in file");
		}
		
		File receivedFile=storedFilePathStrategy.makeReliableStoredFilePathWithFoldersCreated(savedImagesFolder,mediaStorageSOPInstanceUID);
		if (receivedFile.exists()) {
			slf4jlogger.trace("processSingleDicomFile(): Deleting pre-existing file for same SOPInstanceUID");
			receivedFile.delete();		// prior to rename of temporary file, in case might cause renameTo() fail
		}
		if (!temporaryReceivedFile.renameTo(receivedFile)) {
			slf4jlogger.trace("processSingleDicomFile(): Could not move temporary file into place ... copying instead");
			CopyStream.copy(temporaryReceivedFile,receivedFile);
			temporaryReceivedFile.delete();
		}
		if (slf4jlogger.isTraceEnabled()) slf4jlogger.trace("processSingleDicomFile(): temporaryReceivedFile exists (should be false) = {}",temporaryReceivedFile.exists());
		if (slf4jlogger.isTraceEnabled()) slf4jlogger.trace("processSingleDicomFile(): receivedFile exists (should be true) = {}",receivedFile.exists());
		if (!receivedFile.exists()) {
			throw new DicomException("Failed to move or copy received file into place");
		}
		slf4jlogger.trace("CGetResponseOrCStoreRequestHandler.sendPDataIndication(): Notify receivedObjectHandler");
		if (receivedFile != null && receivedFile.exists() && receivedObjectHandler != null) {
			String receivedFileName=receivedFile.getPath();
			// Modified from StorageSOPClassSCP.receiveAndProcessOneRequestMessage() ...
			if (receivedFileName != null) {
				String ts = Attribute.getSingleStringValueOrEmptyString(list,TagFromName.TransferSyntaxUID);
				String calledAE = "NONE";	// not relevant since using DICOMweb not DIMSE
				receivedObjectHandler.sendReceivedObjectIndication(receivedFileName,ts,calledAE);
			}
		}
		
		{
			// we should probably simulate C-GET response identifier here by creating an identifier from the AttributeList of the file
			//try {
			//	if (identifierHandler != null) {
			//		identifierHandler.doSomethingWithIdentifier(list);
			//	}
			//}
			//catch (DicomException e) {
			//	// do not stop ... other identifiers may be OK
			//	slf4jlogger.error("Ignoring exception",e);
			//}
		}
	}

	/**
	 * @param	endpointuri					DICOMweb URI
	 * @param	requestIdentifier			the list of unique keys
	 * @param	identifierHandler			the handler to use for each returned identifier
	 * @param	savedImagesFolder			the folder in which to store received data sets (may be null, to ignore received data for testing)
	 * @param	storedFilePathStrategy		the strategy to use for naming received files and folders
	 * @param	receivedObjectHandler		the handler to call after each data set has been received and stored
	 * @param	acceptheadervalue			the value of the Accept: header to send (null if don't send)
	 * @throws	IOException
	 * @throws	DicomException
	 * @throws	DicomNetworkException
	 */
	public StudyServiceRetrieveTransactionUserAgent(String endpointuri,AttributeList requestIdentifier,IdentifierHandler identifierHandler,
			File savedImagesFolder,StoredFilePathStrategy storedFilePathStrategy,ReceivedObjectHandler receivedObjectHandler,
			String acceptheadervalue
		) throws DicomNetworkException, DicomException, IOException {
		// need to build path to query-level specific resource with unique keys, then append to end point URI
		// see "http://dicom.nema.org/medical/dicom/current/output/chtml/part18/sect_10.4.html"
		
		StringBuffer buf = new StringBuffer();
		buf.append(endpointuri);
		
		InformationEntity queryLevel = getInformationEntityForQueryLevelName(Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.QueryRetrieveLevel,""));
		if (queryLevel == null) {
			throw new DicomException("Cannot determine Query Level from query request identifier to determine resource to use");
		}
		slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): queryLevel={}",queryLevel);
		if (queryLevel == InformationEntity.STUDY) {
			buf.append("/studies");
			{
				String studyInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.StudyInstanceUID,"");
				if (studyInstanceUID.length() == 0) {
					throw new DicomException("Missing StudyInstanceUID unique key needed for Series level retrieve");
				}
				buf.append("/");
				buf.append(studyInstanceUID);
			}
		}
		else if (queryLevel == InformationEntity.SERIES) {
			buf.append("/studies");
			{
				String studyInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.StudyInstanceUID,"");
				if (studyInstanceUID.length() == 0) {
					throw new DicomException("Missing StudyInstanceUID unique key needed for Series level retrieve");
				}
				buf.append("/");
				buf.append(studyInstanceUID);
			}
			buf.append("/series");
			{
				String seriesInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.SeriesInstanceUID,"");
				if (seriesInstanceUID.length() == 0) {
					throw new DicomException("Missing SeriesInstanceUID unique key needed for Instance level retrieve");
				}
				buf.append("/");
				buf.append(seriesInstanceUID);
			}
		}
		else if (queryLevel == InformationEntity.INSTANCE) {
			buf.append("/studies");
			{
				String studyInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.StudyInstanceUID,"");
				if (studyInstanceUID.length() == 0) {
					throw new DicomException("Missing StudyInstanceUID unique key needed for Series level retrieve");
				}
				buf.append("/");
				buf.append(studyInstanceUID);
			}
			buf.append("/series");
			{
				String seriesInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.SeriesInstanceUID,"");
				if (seriesInstanceUID.length() == 0) {
					throw new DicomException("Missing SeriesInstanceUID unique key needed for Instance level retrieve");
				}
				buf.append("/");
				buf.append(seriesInstanceUID);
			}
			buf.append("/instances");
			{
				String sopInstanceUID = Attribute.getSingleStringValueOrDefault(requestIdentifier,TagFromName.SOPInstanceUID,"");
				if (sopInstanceUID.length() == 0) {
					throw new DicomException("Missing SOPInstanceUID unique key needed for Instance level retrieve");
				}
				buf.append("/");
				buf.append(sopInstanceUID);
			}
		}
				
		String retrieveuri = buf.toString();
		slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): retrieveuri=\"{}\"",retrieveuri);
		
		URL url = new URL(retrieveuri);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		
		if (acceptheadervalue != null) {
			slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): setting Accept header to {}",acceptheadervalue);
			connection.setRequestProperty("Accept",acceptheadervalue);
		}
		
		int status = connection.getResponseCode();
		slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): status={}",status);
		
		if (status == HttpURLConnection.HTTP_OK) {
			String contentType = connection.getContentType();
			slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): contentType={}",contentType);
			String contentTypeWithoutParameters = contentType.toLowerCase().replaceFirst(";.*$","").trim();
			slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): contentTypeWithoutParameters={}",contentTypeWithoutParameters);

            //int contentLength = connection.getContentLength();
			//slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): contentLength={}",contentLength);
            
            File temporaryReceivedFile=new File(savedImagesFolder,FileUtilities.makeTemporaryFileName());
			slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): writing http response to File = {}",temporaryReceivedFile);
            {
            	FileOutputStream out = new FileOutputStream(temporaryReceivedFile);
				InputStream in = connection.getInputStream();
            	CopyStream.copy(in,out);
            }
            
            if (contentTypeWithoutParameters.equals("multipart/related")) {
            	// https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
            	// Tried using javax.mail.internet.MimeMultipart, but seems to laod too much into memory and fails for large files so do it by hand ...
				slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): multipart/related response");
				{
					InputStream in = new BufferedInputStream(new FileInputStream(temporaryReceivedFile));
					
					// rather than extract and use boundary specified in contentType header, consume first instance of boundary and record it ...
					
					byte[] boundary = new byte[80];		// max boundary length is 70 bytes (not including the two hyphens that precede it and the CR LFR that follow it) per RFC 2046
					
					int c = in.read();
					if (c != '-') throw new DicomException("Expected boundary start hyphen character, got '"+(char)c+"'");
					c = in.read();
					if (c != '-') throw new DicomException("Expected boundary start hyphen character, got '"+(char)c+"'");
					int bl = 0;
					while (true) {
						c = in.read();
						if (c == -1) throw new DicomException("Premature EOF while reading boundary");
						if (c == 0x0d) break;	// CR - boundary is terminated by CR LF
						boundary[bl++] = (byte)c;
						if (bl >= boundary.length) throw new DicomException("Boundary exceeds maximum length of "+boundary.length);
					}
					c = in.read();
					if (c == -1) throw new DicomException("Premature EOF while reading boundary");
					if (c != 0x0a) throw new DicomException("Expected boundary LF termination character after CR, got '"+(char)c+"'");	// LF - boundary is terminated by CR LF

					slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): have boundary = {}",new String(boundary,0,bl,StandardCharsets.US_ASCII));

					boolean doneWithAllParts = false;
					while (!doneWithAllParts) {
						slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): doing next part - process headers (if any)");
						String bpContentType= "";
						byte[] headerbuffer = new byte[16384];	// there is actually no limit but this is a common one in web server implementations
						boolean doneWithHeaders = false;
						int n = 0;
						while (!doneWithHeaders) {
							c = in.read();
							if (c == 0x0d) {
								c = in.read();
								if (c == 0x0a) {
									if (n == 0) {
										slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): have now consumed the empty line with CR LF after boundary before actual content");
										doneWithHeaders = true;
									}
									else {
										// have reached end of header line and consumed trailing CR LF
										String header = new String(headerbuffer,0,n,StandardCharsets.US_ASCII);		// US_ASCII per "https://www.rfc-editor.org/rfc/rfc7230#section-3.2.4"
										slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): have header = {}",header);
										if (header.toLowerCase().startsWith("content-type:")) {						// field-name is case insensitive per "https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2"
											bpContentType = header.substring("content-type:".length()).trim();		// remove optional white space (OWS) after field-name
											slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): have bpContentType = {}",bpContentType);
										}
										// else ignore other headers
										n = 0;	// prepare for start of next header or terminating empty line
									}
								}
								else {
									throw new DicomException("Expected header line LF termination character after CR, got '"+(char)c+"'");
								}
							}
							else {
								headerbuffer[n++] = (byte)c;
								slf4jlogger.trace("StudyServiceRetrieveTransactionUserAgent(): have header character = {}",(char)c);
								if (n >= headerbuffer.length) throw new DicomException("Header line exceeds maximum length of "+headerbuffer.length);
							}
						}
						
						// now positioned at start of actual data - keep reading until we encounter boundary, looking for pair of hyphens as initial signal it may be present
						{
							slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): using body part ContentType = {}",bpContentType);
							if (bpContentType != null && bpContentType.length() > 0) {
								// e.g. 'contentType = application/dicom; transfer-syntax=1.2.840.10008.1.2.1'
								String bpContentTypeWithoutParameters = bpContentType.toLowerCase().replaceFirst(";.*$","").trim();
								slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): bpContentTypeWithoutParameters = {}",bpContentTypeWithoutParameters);
								if (bpContentTypeWithoutParameters.equals("application/dicom")) {
									File partFile=new File(savedImagesFolder,FileUtilities.makeTemporaryFileName());
									slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): writing part to File = {}",partFile);
									OutputStream out = new BufferedOutputStream(new FileOutputStream(partFile));
									boolean doneWithContent = false;
									while (!doneWithContent) {	// keep processing until CR LF --boundary
										c = in.read();
										if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
										if (c == 0x0d) {
											c = in.read();
											if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
											if (c == 0x0a) {
												c = in.read();
												if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
												if (c == '-') {
													c = in.read();
													if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
													if (c == '-') {
														slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): cound be start of boundary after CR LF --");
														// could be start of boundary
														int i = 0;
														while (true) {
															c = in.read();
															if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
															if (c != boundary[i]) {
																slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): not the boundary after all despite CR LF --");
																// not the boundary after all
																out.write(0x0d);	// read but not written yet
																out.write(0x0a);	// read but not written yet
																out.write('-');		// read but not written yet
																out.write('-');		// read but not written yet
																for (int j=0; j<i; ++j) out.write(boundary[j]);
																out.write(c);
																break;
															}
															if (++i >= bl) {
																out.flush();		// so that we have something to look at when debugging if exception thrown
																// have boundary - consume trailing CR LF to position for next part (if any), or -- (without trailing CR LF) if final part
																c = in.read();
																if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
																if (c == '-') {
																	c = in.read();
																	if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
																	slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): have -- after boundary so last part");
																	doneWithContent = true;
																	doneWithAllParts=true;
																	break;
																}
																if (c == 0x0d) {
																	c = in.read();
																	if (c == -1) throw new DicomException("Premature EOF while searching for boundary");
																	if (c == 0x0a) {
																		slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): have consumed CR LF after boundary so positioned for headers of next part");
																	}
																	else {
																		throw new DicomException("Expected boundary LF termination character after CR, got '"+(char)c+"'");
																	}
																}
																else {
																	throw new DicomException("Expected boundary CR termination character, got '"+(char)c+"'");
																}
																
																doneWithContent = true;
																break;
															}
														}
													}
													else {
														slf4jlogger.trace("StudyServiceRetrieveTransactionUserAgent(): not the boundary lead in after all despite CR LF -");
														out.write(0x0d);	// read but not written yet
														out.write(0x0a);	// read but not written yet
														out.write('-');		// read but not written yet
														out.write(c);
													}
												}
												else {
													slf4jlogger.trace("StudyServiceRetrieveTransactionUserAgent(): not the boundary lead in after all despite CR LF");
													out.write(0x0d);	// read but not written yet
													out.write(0x0a);	// read but not written yet
													out.write(c);
												}
											}
											else {
												slf4jlogger.trace("StudyServiceRetrieveTransactionUserAgent(): not the boundary lead in after all despite CR");
												out.write(0x0d);	// read but not written yet
												out.write(c);
											}
										}
										else {
											out.write(c);
										}
									}
										
									out.flush();
									out.close();

									processSingleDicomFile(partFile,savedImagesFolder,storedFilePathStrategy,receivedObjectHandler,identifierHandler);
								}
							}
							else {
								slf4jlogger.warn("StudyServiceRetrieveTransactionUserAgent(): ignoring body part without ContentType");
							}
						}
					}
				}
				temporaryReceivedFile.delete();
			}
			else if (contentTypeWithoutParameters.equals("application/dicom")) {
				slf4jlogger.debug("StudyServiceRetrieveTransactionUserAgent(): single part application/dicom response");
				processSingleDicomFile(temporaryReceivedFile,savedImagesFolder,storedFilePathStrategy,receivedObjectHandler,identifierHandler);
			}
			else {
				throw new DicomException("Unexpected ContentType in retrieve response: \""+contentType+"\"");
			}
            
            // by this point temporaryReceivedFile will have been moved or deleted
		}

		connection.disconnect();
	}


	/**
	 * @param	endpointuri					DICOMweb URI
	 * @param	requestIdentifier			the list of unique keys
	 * @param	identifierHandler			the handler to use for each returned identifier
	 * @param	savedImagesFolder			the folder in which to store received data sets (may be null, to ignore received data for testing)
	 * @param	storedFilePathStrategy		the strategy to use for naming received files and folders
	 * @param	receivedObjectHandler		the handler to call after each data set has been received and stored
	 * @throws	IOException
	 * @throws	DicomException
	 * @throws	DicomNetworkException
	 */
	public StudyServiceRetrieveTransactionUserAgent(String endpointuri,AttributeList requestIdentifier,IdentifierHandler identifierHandler,
			File savedImagesFolder,StoredFilePathStrategy storedFilePathStrategy,ReceivedObjectHandler receivedObjectHandler
		) throws DicomNetworkException, DicomException, IOException {
		this(endpointuri,requestIdentifier,identifierHandler,
			savedImagesFolder,storedFilePathStrategy,receivedObjectHandler,
			null/*acceptheadervalue*/);
	}
}
