/* Copyright (c) 2001-2012, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.database;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;

import java.io.File;

import java.lang.reflect.Constructor;

import java.util.List;
import java.util.Map;

/**
 * <p>This class provides methods for removing entries from a database, all its children and any associated
 * files that were copied into the database (rather than referenced).</p>
 *
 * @author	dclunie
 */
public class DeleteFromDatabase {

	/***/
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/database/DeleteFromDatabase.java,v 1.2 2012/09/07 16:25:32 dclunie Exp $";
	
	public static void deleteRecordChildrenAndFilesByUniqueKey(DatabaseInformationModel d,String ieName,String keyValue) throws DicomException {
		InformationEntity ie = InformationEntity.fromString(ieName);	// already handles upper or lower case
		deleteRecordChildrenAndFilesByUniqueKey(d,ie,keyValue);
	}

	/**
	 * <p>Remove the database entry, all its children and any copied files.</p>
	 *
	 * @param	d
	 * @param	ie
	 * @param	keyValue					for the PATIENT level, the unique key is the PatientID, otherwise it is the InstanceUID of the entity
	 * @exception	DicomException
	 */
	public static void deleteRecordChildrenAndFilesByUniqueKey(DatabaseInformationModel d,InformationEntity ie,String keyValue) throws DicomException {
		if (ie != null) {
			if (d != null) {
			
				// really should consider adding PATIENT to DatabaseInformationModel.findAllAttributeValuesForAllRecordsForThisInformationEntityWithSpecifiedUID()
				// instead of having it return null for patient level since PatientID is not a true "UID", and that would better generalize the model
				// rather than hardwiting it here (and/or rename the method to ...WithSpecifiedUniqueKey() or similar) :(
				String keyColumnName = ie.equals(InformationEntity.PATIENT) ? "PATIENTID" : d.getUIDColumnNameForInformationEntity(ie);
				String localPrimaryKeyColumnName = d.getLocalPrimaryKeyColumnName(ie);
								
//System.err.println("Query database for "+ie+" "+keyColumnName+" "+keyValue);
				List<Map<String,String>> results = d.findAllAttributeValuesForAllRecordsForThisInformationEntityWithSpecifiedKeyValue(ie,keyColumnName,keyValue);
//System.err.println("Results = "+results);
				if (results != null && results.size() > 0) {
					for (Map<String,String> result : results) {
						String localPrimaryKeyValue = result.get(localPrimaryKeyColumnName);
System.err.println("Deleting "+ie+" "+localPrimaryKeyValue+" "+keyValue);
						deleteRecordChildrenAndFilesByLocalPrimaryKey(d,ie,localPrimaryKeyValue);
						
					}
				}
				// else do nothing and success already false
			}
			else {
				throw new DicomException("No database");
			}
		}
		else {
			throw new DicomException("Unrecognized Information Entity");
		}
	}
	

	/**
	 * <p>Remove the database entry, all its children and any copied files.</p>
	 *
	 * @param	d
	 * @param	ie
	 * @param	localPrimaryKeyValue
	 * @exception	DicomException			if the databaseInformationModel or ie are invalid
	 */
	public static void deleteRecordChildrenAndFilesByLocalPrimaryKey(DatabaseInformationModel d,InformationEntity ie,String localPrimaryKeyValue) throws DicomException {
		if (ie != null) {
			if (d != null) {
				if (localPrimaryKeyValue != null && localPrimaryKeyValue.length() > 0) {
					if (ie.equals(InformationEntity.INSTANCE)) {
						// delete any referenced files
						Map<String,String> result = d.findAllAttributeValuesForSelectedRecord(ie,localPrimaryKeyValue);
						if (result != null) {
							String fileName = result.get(d.getLocalFileNameColumnName(ie));
							String fileReferenceType = result.get(d.getLocalFileReferenceTypeColumnName(ie));
							if (fileReferenceType != null && fileReferenceType.equals(DatabaseInformationModel.FILE_COPIED)) {
								try {
System.err.println("Deleting file "+fileName);
									if (!new File(fileName).delete()) {
										System.err.println("Failed to delete local copy of file "+fileName);
									}
								}
								catch (Exception e) {
									e.printStackTrace(System.err);
									System.err.println("Failed to delete local copy of file "+fileName);
								}
							}
						}
					}
				
					// delete any children first
					InformationEntity childIE = d.getChildTypeForParent(ie,true/*concatenation*/);
					if (childIE != null) {
						String childLocalPrimaryKeyColumnName = d.getLocalPrimaryKeyColumnName(childIE);
						List<Map<String,String>> results = d.findAllAttributeValuesForAllRecordsForThisInformationEntityWithSpecifiedParent(childIE,localPrimaryKeyValue);
						if (results == null || results.size() == 0) {
							// could be because model supports concatenations but this series has no concatentations, so check for instance children of series ...
System.err.println("No result for "+childIE+", so checking without concatenations");
							childIE = d.getChildTypeForParent(ie,false/*concatenation*/);
							childLocalPrimaryKeyColumnName = d.getLocalPrimaryKeyColumnName(childIE);
							results = d.findAllAttributeValuesForAllRecordsForThisInformationEntityWithSpecifiedParent(childIE,localPrimaryKeyValue);
						}
						if (results != null && results.size() > 0) {
							for (Map<String,String> result : results) {
								String childLocalPrimaryKeyValue = result.get(childLocalPrimaryKeyColumnName);
								deleteRecordChildrenAndFilesByLocalPrimaryKey(d,childIE,childLocalPrimaryKeyValue);
							}
						}
					}

					// now delete ourselves ...
System.err.println("Deleting "+ie+" "+localPrimaryKeyValue);
					d.deleteRecord(ie,localPrimaryKeyValue);
				}
				else {
					throw new DicomException("Missing local primary key");
				}
			}
			else {
				throw new DicomException("No database");
			}
		}
		else {
			throw new DicomException("Unrecognized Information Entity");
		}
	}
	
	/**
	 * <p>Remove the database entry, all its children and any copied files.</p>
	 *
	 * <p>For the PATIENT level, the unique key is the PatientID, otherwise it is the InstanceUID of the entity.</p>
	 *
	 * @param	arg	four arguments, the class name of the model, the (full) path of the database file prefix, the level of the entity to remove and the unique key of the entity
	 */
	public static void main(String arg[]) {
		if (arg.length == 4) {
			String databaseModelClassName = arg[0];
			String databaseFileName = arg[1];
		
			if (databaseModelClassName.indexOf('.') == -1) {					// not already fully qualified
				databaseModelClassName="com.pixelmed.database."+databaseModelClassName;
			}
//System.err.println("Class name = "+databaseModelClassName);

			try {
				DatabaseInformationModel databaseInformationModel = new com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel(databaseFileName);
				//DatabaseInformationModel databaseInformationModel = null;
				//Class classToUse = Thread.currentThread().getContextClassLoader().loadClass(databaseModelClassName);
				//Class[] parameterTypes = { databaseFileName.getClass() };
				//Constructor constructorToUse = classToUse.getConstructor(parameterTypes);
				//Object[] args = { databaseFileName };
				//databaseInformationModel = (DatabaseInformationModel)(constructorToUse.newInstance(args));

				if (databaseInformationModel != null) {
					//{
					//	List everything = databaseInformationModel.findAllAttributeValuesForAllRecordsForThisInformationEntity(InformationEntity.PATIENT);
					//	System.err.println("everything.size() = "+everything.size());
					//}
					deleteRecordChildrenAndFilesByUniqueKey(databaseInformationModel,arg[2],arg[3]);
					databaseInformationModel.close();	// this is really important ... will not persist everything unless we do this
				}
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
				System.exit(0);
			}
			
		}
		else {
			System.err.println("Usage: java com.pixelmed.database.DeleteFromDatabase databaseModelClassName databaseFilePathPrefix databaseFileName path(s)");
		}
	}
}

