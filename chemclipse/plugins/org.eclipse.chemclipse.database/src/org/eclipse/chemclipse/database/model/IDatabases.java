/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;

public interface IDatabases {

	/**
	 * The local or remote database will be returned which has been selected
	 * by the settings.
	 * 
	 * @return {@link IDatabase}
	 * @throws NoDatabaseAvailableException
	 * @throws IOException
	 */
	IDatabase getDatabase() throws NoDatabaseAvailableException;

	/**
	 * Creates a new database identified by the name.
	 * 
	 * @param name
	 */
	void createDatabase(String name, String description) throws NoDatabaseAvailableException;

	/**
	 * Deletes the database identified by the given name.
	 * 
	 * @param name
	 */
	void deleteDatabase(String name);

	/**
	 * Returns the proxy list of available local databases.
	 * 
	 * @return List<? extends IDatabaseProxy>
	 * @throws NoDatabaseAvailableException
	 */
	List<? extends IDatabaseProxy> listAvailableDatabaseProxies() throws NoDatabaseAvailableException;

	/**
	 * Returns a list of existing remote database names.
	 * 
	 * @return List<String>
	 * @throws NoDatabaseAvailableException
	 */
	List<String> getDatabaseNamesRemote(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException;

	/**
	 * Returns whether a database identified by the given name exists or not.
	 * 
	 * @param name
	 * @return boolean
	 * @throws NoDatabaseAvailableException
	 */
	boolean databaseExists(IDatabaseProxy databaseProxy) throws NoDatabaseAvailableException;

	IDatabase getDatabase(IDatabaseProxy chromIdentDatabaseProxy) throws NoDatabaseAvailableException;

	Map<String, String> mapAvailableDatabases(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException;

	boolean databaseExistsRemote(IDatabaseProxy databaseProxy) throws NoDatabaseAvailableException;

	List<String> getDatabaseNames(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException;

	List<String> getDatabaseNames() throws NoDatabaseAvailableException;

	List<? extends IDatabaseProxy> listAvailableDatabaseProxies(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException;

	String getIdentifier();

	void setDatabaseInPreferenceSupplier(IDatabaseProxy databaseProxy) throws NoDatabaseAvailableException;
}
