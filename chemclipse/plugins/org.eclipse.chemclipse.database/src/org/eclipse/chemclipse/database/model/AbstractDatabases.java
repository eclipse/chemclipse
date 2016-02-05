/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.database.documents.ILibraryDescriptionDocument;
import org.eclipse.chemclipse.database.documents.ISettingsDocument;
import org.eclipse.chemclipse.database.documents.LibraryDescriptionDocument;
import org.eclipse.chemclipse.database.documents.SettingsDocument;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OStorageException;

public abstract class AbstractDatabases implements IDatabases {

	final private String identifier;

	/**
	 * Uses e.g. "org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.chromident" as an identifier.
	 * 
	 * @param identifier
	 */
	public AbstractDatabases(String identifier) {
		if(identifier != null) {
			this.identifier = identifier;
		} else {
			this.identifier = DatabasePathHelper.DEFAULT_IDENTIFIER;
		}
	}

	@Override
	public void createDatabase(String name, String description) throws NoDatabaseAvailableException {

		/*
		 * Check if the database still exists.
		 */
		if(DatabasePathHelper.isLocalDatabasePath(name) && !databaseExistsLocal(name)) {
			DatabasePathHelper pathHelper = new DatabasePathHelper(identifier);
			String trimmedName = pathHelper.trimLocalDatabasePath(name).replace(DatabasePathHelper.LOCAL_DB_PREFIX, "");
			/*
			 * Settings
			 * 0 = USE MMAP ALWAYS
			 * 1 = USE MMAP ON WRITES OR ON READ JUST WHEN THE BLOCK POOL IS FREE
			 * 2 = USE MMAP ON WRITES OR ON READ JUST WHEN THE BLOCK IS ALREADY AVAILABLE
			 * 3 = USE MMAP ONLY IF BLOCK IS ALREADY AVAILABLE
			 * 4 = NEVER USE MMAP
			 * Seems to be deprecated, added -Dfile.mmap.strategy=1, but according to the settings dump OGlobalConfiguration.dumpConfiguration(System.out);
			 * it is not there
			 */
			/*
			 * Create a new database.
			 */
			String localDatabase = pathHelper.getLocalDatabasePath(name);
			ODatabaseDocumentTx documentDatabase = new ODatabaseDocumentTx(AbstractDatabase.getOptimizedLocalDatabasePath(localDatabase));
			/*
			 * Configuring the database
			 */
			documentDatabase.create();
			/*
			 * Create a description document.
			 */
			ILibraryDescriptionDocument libraryDescription = new LibraryDescriptionDocument();
			libraryDescription.setName(trimmedName);
			libraryDescription.setDescription(description);
			libraryDescription.save();
			/*
			 * Create Settings
			 */
			ISettingsDocument settingsDocument = new SettingsDocument();
			settingsDocument.setType(identifier);
			settingsDocument.save();
			/*
			 * Close the database. A new connection will be opened in the Database instance.
			 */
			documentDatabase.close();
		}
		/*
		 * Remote database creation is by intention not implemented
		 */
	}

	@Override
	public void deleteDatabase(String name) {

		if(DatabasePathHelper.isLocalDatabasePath(name)) {
			String trimmedName = name.replace(DatabasePathHelper.LOCAL_DB_PREFIX, "");
			DatabasePathHelper pathHelper = new DatabasePathHelper(identifier);
			pathHelper.cleanDatabase(trimmedName);
		}
		/*
		 * Remote database deletion is by intention not implemented
		 */
	}

	@Override
	public List<String> getDatabaseNames(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException {

		return databaseSettings.isRemoteDatabase() ? getDatabaseNamesRemote(databaseSettings) : getDatabaseNamesLocal();
	}

	@Override
	public boolean databaseExists(IDatabaseProxy databaseProxy) throws NoDatabaseAvailableException {

		return databaseProxy.isRemoteDatabase() ? databaseExistsRemote(databaseProxy) : databaseExistsLocal(databaseProxy.getDatabaseUrl());
	}

	@Override
	public boolean databaseExistsRemote(IDatabaseProxy databaseProxy) throws NoDatabaseAvailableException {

		if(databaseProxy.isRemoteDatabase()) {
			String url = databaseProxy.getDatabaseUrl();
			List<String> databaseNames = getDatabaseNamesRemote(databaseProxy.getDatabaseSettings());
			return databaseNames.contains(url);
		}
		return false;
	}

	@Override
	public List<String> getDatabaseNamesRemote(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException {

		if(databaseSettings.isRemoteDatabase()) {
			List<String> remoteDatabaseNames = new ArrayList<String>();
			String url = databaseSettings.getUrl();
			try {
				OServerAdmin server = new OServerAdmin(url).connect(databaseSettings.getUsername(), databaseSettings.getPassword());
				Map<String, String> listDatabases = server.listDatabases();
				for(String db : listDatabases.keySet()) {
					remoteDatabaseNames.add(url + "/" + db);
				}
			} catch(OStorageException e) {
				throw new NoDatabaseAvailableException("Underlying OStorageException: " + e.getMessage());
			} catch(IOException e) {
				throw new NoDatabaseAvailableException("Underlying IOException: " + e.getMessage());
			}
			return remoteDatabaseNames;
		}
		return new ArrayList<String>();
	}

	@Override
	public Map<String, String> mapAvailableDatabases(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException {

		return databaseSettings.isRemoteDatabase() ? mapAvailableDatabasesRemote(databaseSettings) : mapAvailableDatabasesLocal();
	}

	@Override
	public String getIdentifier() {

		return identifier;
	}

	// -------------------- PRIVATE METHODS ------------------------------------------------------------
	private boolean databaseExistsLocal(String name) {

		DatabasePathHelper databasePathHelper = new DatabasePathHelper(identifier);
		if(DatabasePathHelper.isLocalDatabasePath(name)) {
			String trimmedName = databasePathHelper.trimLocalDatabasePath(name).replace(DatabasePathHelper.LOCAL_DB_PREFIX, "");
			List<String> databaseNames = getDatabaseNamesLocal();
			return databaseNames.contains(trimmedName);
		}
		return false;
	}

	private List<String> getDatabaseNamesLocal() {

		DatabasePathHelper pathHelper = new DatabasePathHelper(identifier);
		List<String> databaseNames = new ArrayList<String>();
		File storagePath = pathHelper.getStoragePath();
		for(File file : storagePath.listFiles()) {
			/*
			 * Each database is stored in a directory.
			 */
			if(file.isDirectory()) {
				databaseNames.add(file.getName());
			}
		}
		return databaseNames;
	}

	private Map<String, String> mapAvailableDatabasesLocal() {

		DatabasePathHelper pathHelper = new DatabasePathHelper(identifier);
		Map<String, String> databaseMap = new HashMap<String, String>();
		File storagePath = pathHelper.getStoragePath();
		for(File file : storagePath.listFiles()) {
			/*
			 * Each database is stored in a directory.
			 */
			if(file.isDirectory()) {
				databaseMap.put(DatabasePathHelper.LOCAL_DB_PREFIX + file.toString(), file.getName());
				// databaseUrls.add(file.toString());
			}
		}
		return databaseMap;
	}

	private Map<String, String> mapAvailableDatabasesRemote(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException {

		if(databaseSettings.isRemoteDatabase()) {
			Map<String, String> databaseMap = new HashMap<String, String>();
			String url = databaseSettings.getUrl();
			try {
				OServerAdmin server = new OServerAdmin(url).connect(databaseSettings.getUsername(), databaseSettings.getPassword());
				Map<String, String> listDatabases = server.listDatabases();
				String hostname = url.replace(DatabasePathHelper.REMOTE_DB_PREFIX, "");
				for(String db : listDatabases.keySet()) {
					databaseMap.put(url + "/" + db, hostname + "/" + db);
				}
			} catch(IOException e) {
				throw new NoDatabaseAvailableException("The database " + url + " is not available or does not exist (Underlying IOException).");
			}
			return databaseMap;
		}
		return null;
	}
}
