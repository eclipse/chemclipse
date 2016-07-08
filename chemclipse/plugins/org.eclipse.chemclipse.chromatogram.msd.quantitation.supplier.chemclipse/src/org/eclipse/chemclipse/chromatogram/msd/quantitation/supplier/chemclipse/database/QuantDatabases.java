/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - new features
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationTableAlreadyExistsException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.io.QuantDatabaseReader;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.io.QuantDatabaseWriter;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

public class QuantDatabases {

	public static final String IDENTIFIER_QUANTITATION = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse";
	//
	private static final Logger logger = Logger.getLogger(QuantDatabases.class);
	private static final Map<String, IQuantDatabase> databases = new HashMap<String, IQuantDatabase>();

	private QuantDatabases() {
	}

	public static void persistDatabases() {

		String storagePath = getStoragePath().getAbsolutePath();
		for(Map.Entry<String, IQuantDatabase> entry : databases.entrySet()) {
			try {
				File file = new File(storagePath + File.separator + entry.getKey());
				IQuantDatabase quantDatabase = entry.getValue();
				QuantDatabaseWriter.write(quantDatabase, file);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
	}

	public static IQuantDatabase getQuantDatabase() throws NoQuantitationTableAvailableException {

		String databaseName = PreferenceSupplier.getSelectedQuantitationTable();
		return getQuantDatabase(databaseName);
	}

	public static IQuantDatabase getQuantDatabase(String databaseName) throws NoQuantitationTableAvailableException {

		IQuantDatabase quantDatabase = databases.get(databaseName);
		if(quantDatabase == null) {
			/*
			 * Try to read the database
			 */
			try {
				File file = new File(getStoragePath() + File.separator + databaseName);
				quantDatabase = QuantDatabaseReader.read(file);
			} catch(Exception e) {
				/*
				 * New database
				 */
				quantDatabase = new QuantDatabase();
				logger.warn(e);
			}
			databases.put(databaseName, quantDatabase);
		}
		return quantDatabase;
	}

	public static List<String> getDatabaseNames() throws NoQuantitationTableAvailableException {

		List<String> databaseNames = new ArrayList<String>();
		File storageDirectory = getStoragePath();
		for(File file : storageDirectory.listFiles()) {
			if(file.isFile()) {
				databaseNames.add(file.getName());
			}
		}
		return databaseNames;
	}

	public static List<IQuantDatabaseProxy> listAvailableDatabaseProxies() throws NoQuantitationTableAvailableException {

		List<IQuantDatabaseProxy> databaseProxies = new ArrayList<IQuantDatabaseProxy>();
		List<String> databaseNames = getDatabaseNames();
		for(String databaseName : databaseNames) {
			databaseProxies.add(new QuantDatabaseProxy(databaseName));
		}
		return databaseProxies;
	}

	public static void createDatabase(String databaseName) throws QuantitationTableAlreadyExistsException {

		File storageDirectory = getStoragePath();
		File databaseNew = new File(storageDirectory.getAbsolutePath() + File.separator + databaseName);
		if(databaseNew.exists()) {
			throw new QuantitationTableAlreadyExistsException();
		} else {
			try {
				databaseNew.createNewFile();
			} catch(IOException e) {
				logger.warn(e);
			}
		}
	}

	public static boolean deleteDatabase(IQuantDatabaseProxy quantDatabaseProxy) {

		String databaseName = quantDatabaseProxy.getDatabaseName();
		String databaseUrl = quantDatabaseProxy.getDatabaseUrl();
		//
		databases.remove(databaseName);
		File database = new File(databaseUrl + File.separator + databaseUrl);
		return database.delete();
	}

	/**
	 * DO NOT CALL THIS METHOD IF YOU NOT REALLY KNOW WHAT YOU ARE DOING.<br/>
	 * Cleans all temporarily stored files in the storage directory.<br/>
	 * This method will be called on bundle start and stop.
	 */
	public static void cleanStoragePath() {

		File directory = getStoragePath();
		deleteFiles(directory);
	}

	public static File getStoragePath() {

		File file = new File(ApplicationSettings.getSettingsDirectory().getAbsolutePath() + File.separator + IDENTIFIER_QUANTITATION);
		/*
		 * Create the directory if it not exists.
		 */
		if(!file.exists()) {
			if(!file.mkdirs()) {
				logger.warn("The quantitation directory could not be created: " + file.getAbsolutePath());
			}
		}
		return file;
	}

	/**
	 * Deletes the given directory recursively.
	 * 
	 * @param directory
	 */
	private static void deleteFiles(File directory) {

		/*
		 * Delete all files in all directories.
		 */
		for(File file : directory.listFiles()) {
			if(file.isDirectory()) {
				deleteFiles(file);
			} else {
				if(!file.delete()) {
					logger.warn("The file " + file + "could not be deleted.");
				}
			}
		}
		/*
		 * Delete the directory if all files have been removed.
		 */
		if(!directory.delete()) {
			logger.warn("The directory " + directory + "could not be deleted.");
		}
	}
}
